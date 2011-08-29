package com.dekarrin.wow;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dekarrin.db.MySqlEngine;
import com.dekarrin.db.TableData;
import com.dekarrin.error.TrafficException;
import com.dekarrin.program.ConsoleProgram;
import com.dekarrin.program.FatalErrorException;

/**
 * Grabs data from the WOW auction house.
 * @author Michael Nelson
 *
 */
public class AuctionScan extends ConsoleProgram {
	
	private static final String AUCTION_API = "/api/wow/auction/data/";
	private static final int FLAG_ALLIANCE = 1;
	private static final int FLAG_HORDE = 2;
	private static final int FLAG_NEUTRAL = 4;
	
	private JSONObject remoteDump;
	
	private ApiCore core;
	
	/**
	 * The realm to scan. Multiple realms are unacceptable, due to the
	 * massive amount of memory required to hold data on even one.
	 */
	private String realm;
	
	/**
	 * The time between snapshots, in milliseconds.
	 */
	private long snapshotInterval;
	
	/**
	 * The faction(s) that are to be scanned and kept track of.
	 */
	private Faction faction;
	
	/**
	 * The time that a dump was last retrieved from the WOW server.
	 */
	private long lastDumpTime = 0;
	
	/**
	 * The database id of the snapshot being comitted.
	 */
	private long lastSnapshot;
	
	/**
	 * The database id of the last full (non-incremental) snapshot
	 * of the WOW auction houses.
	 */
	private long lastFullSnapshot;
	
	/**
	 * The last time that the WOW server created a dump of the auction
	 * data.
	 */
	private long remoteDumpTime;
	
	/**
	 * The location of the most current auction house dump on the WOW
	 * server.
	 */
	private URL remoteDumpLocation;
	
	/**
	 * Whether or not a full snapshot is currently being created.
	 */
	private boolean onFullSnapshot = false;
	
	/**
	 * A list of current auction ids.
	 */
	private HashMap<Long,Long> deletedAuctions;
	
	public static void main(String[] args) {
		new AuctionScan(args);
	}
	
	public AuctionScan(String[] args) {
		super(args);
		try {
			System.out.println("Opening database host connection...");
			core = new ApiCore();
			System.out.println("Loading settings...");
			loadSettings();
			System.out.println("Checking snapshot...");
			checkSnapshot();
			if(dumpOutOfDate() || onFullSnapshot) {
				System.out.println("New dump is required.");
				System.out.println("Downloading auction data...");
				downloadAuctionData();
				if(!onFullSnapshot) {
					System.out.println("Setting up used auction ID's...");
					setupDeletedAuctions();
				}
				System.out.println("Adding new and changed auctions...");
				addNewAndChangedAuctions();
				System.out.println("Checking for auction deletion...");
				if(!onFullSnapshot) {
					System.out.println("Auction deletion required.");
					System.out.println("Adding deletion events...");
					removeOldAuctions();
				} else {
					System.out.println("Auction deletion not required.");
				}
			} else {
				System.out.println("Snapshot is up to date.");
			}
			System.out.println("Saving settings...");
			saveSettings();
			System.out.println("Closing database host connection...");
			core.db.close();
		} catch(SQLException e) {
			e.printStackTrace();
			giveFatalError("Last Query: "+core.db.getLastQuery());
		} catch(JSONException e) {
			giveFatalError("BAD JSON!");
		} catch(TrafficException e) {
			ui.println("Request limit exceeded");
			ui.println("Canont access WOW API until limit has reset.");
		}
		System.out.println();
		System.out.println("done");
	}
	
	/**
	 * Checks if the dump is out of date by looking up the timestamp
	 * of the last dump and comparing it to the current snapshot.
	 * 
	 * @return
	 * True if the dump is out of date, false otherwise.
	 * @throws TrafficException 
	 */
	private boolean dumpOutOfDate() throws SQLException, TrafficException {
		try {
			downloadRemoteDumpData();
		} catch(MalformedURLException e) {
			giveFatalError("Bad URL!");
		} catch (JSONException e) {
			giveFatalError("Bad JSON!");
		}
		if(lastDumpTime < remoteDumpTime) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Checks if the current snapshot is out of date. This is different
	 * from the remote dump; the snapshot is a complete index of every
	 * single auction taken after some period that is at least 48
	 * hours long.
	 * 
	 * @throws SQLException 
	 * @throws TrafficException 
	 */
	private void checkSnapshot() throws SQLException, TrafficException {
		if(lastSnapshot == 0) {
			setupFullSnapshot();
		} else {
			String time = core.db.selectItem("time", "snapshots", "`id`='"+lastSnapshot+"'");
			long snapshotTime = Long.parseLong(time);
			if(snapshotTime + snapshotInterval < System.currentTimeMillis()) {
				setupFullSnapshot();
			} else if(dumpOutOfDate()) {
				setupIncrementalSnapshot();
			}
		}
	}
	
	/**
	 * Downloads the data from the remote source.
	 * @throws JSONException 
	 * @throws MalformedURLException 
	 * @throws TrafficException
	 */
	private void downloadRemoteDumpData() throws JSONException, MalformedURLException, TrafficException {
		String response = null;
		try {
			response = core.getRequest(AUCTION_API + realm, true);
		} catch(FatalErrorException e) {
			giveFatalError(e.getMessage());
		}
		JSONObject json = new JSONObject(response);
		JSONObject data = json.getJSONArray("files").getJSONObject(0);
		remoteDumpLocation = new URL(data.getString("url"));
		remoteDumpTime = data.getLong("lastModified");
	}
	
	/**
	 * Loads the application settings from the database.
	 */
	private void loadSettings() throws SQLException {
		realm = core.getSetting("realm");
		lastFullSnapshot = Long.parseLong(core.getSetting("last_full_snapshot"));
		lastDumpTime = Long.parseLong(core.getSetting("last_dump_time"));
		lastSnapshot = Long.parseLong(core.getSetting("last_snapshot"));
		faction = Faction.valueOf(core.getSetting("faction"));
		snapshotInterval = Long.parseLong(core.getSetting("snapshot_interval"));
	}
	
	/**
	 * Saves the current settings to the database.
	 */
	private void saveSettings() throws SQLException {
		core.setSetting("last_dump_time", Long.toString(lastDumpTime));
		core.setSetting("last_full_snapshot", Long.toString(lastFullSnapshot));
		core.setSetting("last_snapshot", Long.toString(lastSnapshot));
	}
	
	/**
	 * Gets the auction house data.
	 */
	private void downloadAuctionData() throws JSONException, TrafficException {
		String response = null;
		try {
			response = core.getRequest(remoteDumpLocation.toString(), false);
		} catch(FatalErrorException e) {
			giveFatalError(e.getMessage());
		}
		remoteDump = new JSONObject(response);
	}
	
	/**
	 * Scans the remote auction dump for new and modified auctions.
	 * @throws JSONException 
	 * @throws SQLException 
	 */
	private void addNewAndChangedAuctions() throws JSONException, SQLException {
		if(includeAlliance()) {
			parseAuctionData(remoteDump.getJSONObject("alliance"), "alliance");
		}
		if(includeHorde()) {
			parseAuctionData(remoteDump.getJSONObject("horde"), "horde");
		}
		if(includeNeutral()) {
			parseAuctionData(remoteDump.getJSONObject("neutral"), "neutral");
		}
	}
	
	/**
	 * Checks whether Alliance auctions are to be included.
	 * 
	 * @return
	 * Whether they should be.
	 */
	private boolean includeAlliance() {
		return ((faction.getMask() & FLAG_ALLIANCE) == FLAG_ALLIANCE);
	}
	
	/**
	 * Checks whether Horde auctions are to be included.
	 * 
	 * @return
	 * Whether they should be.
	 */
	private boolean includeHorde() {
		return ((faction.getMask() & FLAG_HORDE) == FLAG_HORDE);
	}
	
	/**
	 * Checks whether Neutral auctions are to be included.
	 * 
	 * @return
	 * Whether they should be.
	 */
	private boolean includeNeutral() {
		return ((faction.getMask() & FLAG_NEUTRAL) == FLAG_NEUTRAL);
	}
	
	/**
	 * Parses auction data from an auction house.
	 * 
	 * @param data
	 * The raw JSON data.
	 * 
	 * @param faction
	 * The faction that the data is for.
	 * 
	 * @throws JSONException 
	 * @throws SQLException 
	 */
	private void parseAuctionData(JSONObject data, String faction) throws JSONException, SQLException {
		JSONArray auctions = data.getJSONArray("auctions");
		for(int i = 0; i < auctions.length(); i++) {
			JSONObject jsonAuction = auctions.getJSONObject(i);
			AuctionData auction = new AuctionData();
			String time = jsonAuction.getString("timeLeft");
			auction.auc			= jsonAuction.getLong("auc");
			auction.item		= jsonAuction.getInt("item");
			auction.owner		= jsonAuction.getString("owner");
			auction.bid			= jsonAuction.getInt("bid");
			auction.buyout		= jsonAuction.getInt("buyout");
			auction.quantity	= jsonAuction.getInt("quantity");
			auction.timeLeft	= AuctionTime.valueOf(time);
			auction.faction		= faction;
			processAuction(auction);
		}
	}
	
	/**
	 * Gets a new full snapshot ready.
	 * 
	 * @throws SQLException
	 */
	private void setupFullSnapshot() throws SQLException {
		core.db.insert(new TableData("snapshots").addColumn("type").addRow("full"));
		lastSnapshot = core.db.getInsertId();
		lastFullSnapshot = lastSnapshot;
		onFullSnapshot = true;
		lastDumpTime = System.currentTimeMillis() / 1000L;
	}
	
	/**
	 * Gets an incremental snapshot ready.
	 * 
	 * @throws SQLException
	 */
	private void setupIncrementalSnapshot() throws SQLException {
		core.db.insert(new TableData("snapshots").addColumn("type").addRow("incremental"));
		lastSnapshot = core.db.getInsertId();
		onFullSnapshot = false;
		lastDumpTime = System.currentTimeMillis() / 1000L;
	}
	
	/**
	 * Processes an auction for storage in the current snapshot.
	 * 
	 * @param auction
	 * The auction data.
	 * @throws SQLException 
	 */
	private void processAuction(AuctionData auction) throws SQLException {
		if(!onFullSnapshot && auctionExists(auction)) {
			setNotDeleted(auction);
			changeExistingAuction(auction);
		} else {
			addNewAuction(auction);
		}
	}
	
	/**
	 * Checks if an auction already exists in the current snapshot.
	 * @throws SQLException 
	 */
	private boolean auctionExists(AuctionData auction) throws SQLException {
		return deletedAuctions.containsKey(auction.auc);
	}
	
	/**
	 * Checks for auctions that no longer exist and adds a removed
	 * event for them.
	 */
	private void removeOldAuctions() throws SQLException {
		for(Long auc: deletedAuctions.values()) {
			AuctionData ad = new AuctionData();
			ad.auc = auc;
			addEvent("remove", ad);
		}
	}
	
	/**
	 * Does nothing to the current auction; only an event is added,
	 * if anything changed.
	 * @throws SQLException 
	 */
	private void changeExistingAuction(AuctionData auction) throws SQLException {
		core.db.selectRows("auctions", "`auc`='"+auction.auc+"' AND `snapshot`='"+lastSnapshot+"'");
		HashMap<String,String> newData;
		newData = core.db.getResult().getMap();
		AuctionData oldAuction = new AuctionData(newData);
		if(!oldAuction.equals(auction)) {
			if(auction.bid != oldAuction.bid) {
				addEvent("bid", auction);
			}
			if(auction.timeLeft != oldAuction.timeLeft) {
				addEvent("time", auction);
			}
		}
	}
	
	/**
	 * Adds a new auction to the database.
	 * 
	 * @param auction
	 * The auction to add.
	 */
	private void addNewAuction(AuctionData auction) throws SQLException {
		addAuction(auction);
		addEvent("add", auction);
	}
	
	/**
	 * Gets the auc's of all the auctions in the current snapshot
	 * @throws SQLException 
	 */
	private long[] getCurrentIds() throws SQLException {
		core.db.call("get_current_ids");
		TableData td = core.db.getResult();
		return td.getLongArray("auc");
	}
	
	private void setupDeletedAuctions() throws SQLException {
		deletedAuctions = new HashMap<Long,Long>();
		long[] aucs = getCurrentIds();
		for(long auc: aucs) {
			deletedAuctions.put(auc, auc);
		}
	}
	
	/**
	 * Makes it so an auction is tagged as not deleted.
	 */
	private void setNotDeleted(AuctionData auction) {
		deletedAuctions.remove(auction.auc);
	}
	
	/**
	 * Adds an event to the database.
	 * 
	 * @param eventType
	 * The event that is being added. This must be one of
	 * the enumerated values on the DB ("add","remove","time",
	 * or "bid").
	 * 
	 * @param auction
	 * Information on the auction being added.
	 */
	private void addEvent(String eventType, AuctionData auction) throws SQLException {
		TableData td = new TableData("events");
		td.addColumn("auction", "event", "snapshot");
		td.addRow(Long.toString(auction.auc), eventType, Long.toString(lastSnapshot));
		eventType.toLowerCase();
		if(eventType.equals("time")) {
			td.addColumn("argument");
			td.jumpColumn("argument").set(auction.timeLeft.toString());
		} else if(eventType.equals("bid")) {
			td.addColumn("argument");
			td.jumpColumn("argument").set(Integer.toString(auction.bid));
		}
		core.db.insert(td);
	}
	
	/**
	 * Adds an auction to the database.
	 * 
	 * @param auction
	 * The data on the auction.
	 */
	private void addAuction(AuctionData auction) throws SQLException {
		checkItem(auction.item);
		TableData td = new TableData("auctions");
		td.addColumn("auc", "item", "owner", "bid", "buyout", "quantity", "time_left", "faction");
		td.addRow();
		td.setLong(auction.auc);
		td.setLong(auction.item);
		td.set(auction.owner);
		td.setInt(auction.bid);
		td.setInt(auction.buyout);
		td.setInt(auction.quantity);
		td.set(auction.timeLeft.toString());
		td.set(auction.faction.toString());
		core.db.insert(td);
	}
	
	/**
	 * Checks whether the item for an auction exists. If it does not exist,
	 * then it is created. This does not fill in any values for the item;
	 * it only adds the item to the database so that the ItemScan program
	 * knows what to update when it runs.
	 * 
	 * @param itemId
	 * The id of the item to check.
	 */
	private void checkItem(int itemId) throws SQLException {
		if(!itemExists(itemId)) {
			addBlankItem(itemId);
		}
	}
	
	/**
	 * Checks whether an entry for an item exists in the local database.
	 * 
	 * @param itemId
	 * The ID of the item to check for.
	 * 
	 * @return
	 * Whether an entry for the item exists.
	 */
	private boolean itemExists(int itemId) throws SQLException {
		core.db.selectRows("items", "`id` = "+itemId);
		return (!core.db.getResult().isEmpty());
	}
	
	/**
	 * Adds a blank entry to the local item database.
	 * 
	 * @param itemId
	 * The ID of the item to add.
	 */
	private void addBlankItem(int itemId) throws SQLException {
		TableData td = new TableData("items");
		td.addColumn("id").setInt(itemId);
		core.db.insert(td);
	}
}