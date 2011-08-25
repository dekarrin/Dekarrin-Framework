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
import com.dekarrin.program.ConsoleProgram;

/**
 * Grabs data from the WOW auction house.
 * @author Michael Nelson
 *
 */
public class AuctionScan extends ConsoleProgram {
	
	private class AuctionData {
		public long auc = 0L;
		public int item = 0;
		public String owner = null;
		public int quantity = 0;
		public AuctionTime timeLeft = null;
		public int bid = 0;
		public int buyout = 0;
		public String faction = null;
		public AuctionData() {super();}
		public AuctionData(HashMap<String,String> dataSource) {
			auc = Long.parseLong(dataSource.get("auc"));
			item = Integer.parseInt(dataSource.get("item"));
			owner = dataSource.get("owner");
			quantity = Integer.parseInt(dataSource.get("quantity"));
			timeLeft = AuctionTime.valueOf(dataSource.get("timeLeft"));
			bid = Integer.parseInt(dataSource.get("bid"));
			buyout = Integer.parseInt(dataSource.get("buyout"));
			faction = dataSource.get("faction");
		}
		public boolean equals(AuctionData ad) {
			if(
			 ad.auc == auc && ad.item == item &&
			 ad.owner.equals(owner) && ad.quantity == quantity &&
			 ad.timeLeft == timeLeft && ad.bid == bid &&
			 ad.buyout == buyout && ad.faction == faction
			) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	private HttpClient client = new DefaultHttpClient();
	
	/**
	 * The time between snapshots, in milliseconds.
	 */
	private static final long SNAPSHOT_INTERVAL = 180000000L;
 	private static final String BLIZZARD_HOST = "us.battle.net";
	private static final String AUCTION_API = "/api/wow/auction/data/";
	private static final int MAXIMUM_REQUESTS = 2500;
	private static final int FLAG_ALLIANCE = 1;
	private static final int FLAG_HORDE = 2;
	private static final int FLAG_NEUTRAL = 4;
	
	private JSONObject remoteDump;
	
	MySqlEngine db = new MySqlEngine();
	
	/**
	 * The realm to scan. Multiple realms are unacceptable, due to the
	 * massive amount of memory required to hold data on even one.
	 */
	private String realm = "bronzebeard";
	
	/**
	 * The faction(s) that are to be scanned and kept track of.
	 */
	private Faction faction;
	
	/**
	 * The number of requests made to the WOW server today.
	 */
	private int requestCount;
	
	/**
	 * The time that a dump was last retrieved from the WOW server.
	 */
	private long lastDumpTime = 0;
	
	/**
	 * The database id of the snapshot being commited.
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
			db.connect("localhost", 3306, "wow", "greeneconomyapple");
			db.use("auctionscan");
			loadSettings();
			checkSnapshot();
			if(dumpOutOfDate() || onFullSnapshot) {
				downloadAuctionData();
				setupDeletedAuctions();
				addNewAndChangedAuctions();
				if(!onFullSnapshot) {
					removeOldAuctions();
				}
			}
			saveState();
		} catch(SQLException e) {
			giveFatalError("SQL Error: "+e.getMessage()+"\nQuery: "+db.getLastQuery());
		} catch(JSONException e) {
			giveFatalError("BAD JSON!");
		}
	}
	
	/**
	 * Checks if the dump is out of date by looking up the timestamp
	 * of the last dump and comparing it to the current snapshot.
	 * 
	 * @return
	 * True if the dump is out of date, false otherwise.
	 */
	private boolean dumpOutOfDate() throws SQLException {
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
	 */
	private void checkSnapshot() throws SQLException {
		if(lastSnapshot == 0) {
			setupFullSnapshot();
		} else {
			String time = db.selectItem("time", "snapshots", "`id`='"+lastSnapshot+"'");
			long snapshotTime = Long.parseLong(time);
			if(snapshotTime + SNAPSHOT_INTERVAL < System.currentTimeMillis()) {
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
	 */
	private void downloadRemoteDumpData() throws JSONException, MalformedURLException {
		String response = getRequest(new URL("http://" + BLIZZARD_HOST + AUCTION_API + realm));
		JSONObject json = new JSONObject(response);
		JSONObject data = json.getJSONArray("files").getJSONObject(0);
		remoteDumpLocation = new URL(data.getString("url"));
		remoteDumpTime = data.getLong("lastModified");
	}
	
	/**
	 * Loads the application settings from the database.
	 */
	private void loadSettings() throws SQLException {
		realm = getSetting("realm");
		lastFullSnapshot = Long.parseLong(getSetting("last_full_snapshot"));
		lastDumpTime = Long.parseLong(getSetting("last_dump_time"));
		lastSnapshot = Long.parseLong(getSetting("last_snapshot"));
		requestCount = Integer.parseInt(getSetting("requests"));
		faction = Faction.valueOf(getSetting("faction"));
	}
	
	/**
	 * Saves the current timestamp to disk.
	 */
	private void saveState() throws SQLException {
		setSetting("last_dump_time", Long.toString(lastDumpTime));
		setSetting("last_full_snapshot", Long.toString(lastFullSnapshot));
		setSetting("last_snapshot", Long.toString(lastSnapshot));
		setSetting("requests", Integer.toString(requestCount));
	}
	
	/**
	 * Gets the auction house data.
	 */
	private void downloadAuctionData() throws JSONException {
		String response = getRequest(remoteDumpLocation);
		remoteDump = new JSONObject(response);
	}
	
	/**
	 * Generates an HTTP GET request.
	 */
	private String getRequest(URL location) {
		HttpGet request = new HttpGet(location.toString());
		HttpResponse response = null;
		try {
			response = client.execute(request);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			giveFatalError("Bad request");
		} catch (IOException e) {
			e.printStackTrace();
			giveFatalError(e.getMessage());
		}
		byte[] responseContent = null;
		HttpEntity ent = response.getEntity();
		if(ent != null) {
			InputStream is = null;
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			try {
				is = ent.getContent();
				int next;
				while((next = is.read()) != -1) {
					buffer.write(next);
				}
				buffer.flush();
			} catch(IOException e) {
				giveFatalError("Bad IO!");
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					giveFatalError("Can't close response!");
				}
			}
			responseContent = buffer.toByteArray();
		}
		return new String(responseContent);
	}
	
	/**
	 * Sets a setting in the table.
	 */
	private void setSetting(String key, String value) throws SQLException {
		db.updateItem("settings", "value", value, "`key`='"+key+"'");
	}
	
	/**
	 * Gets a setting from the db.
	 */
	private String getSetting(String key) throws SQLException {
		return db.selectItem("value", "settings", "`key`='"+key+"'");
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
		TableData data = new TableData("settings");
		data.addColumn("type");
		data.addRow("full");
		db.insert(new TableData("settings").addColumn("type").addRow("full"));
		lastSnapshot = db.getInsertId();
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
		db.insert(new TableData("snapshots").addColumn("type").addRow("incremental"));
		lastSnapshot = db.getInsertId();
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
		if(auctionExists(auction) && !onFullSnapshot) {
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
		String item = db.selectItem("id", "auctions",
				"`auc`='"+auction.auc+"' AND `snapshot`='"+lastSnapshot+"'");
		return (item != null);
	}
	
	/**
	 * Checks for auctions that no longer exist and adds a removed
	 * event for them.
	 */
	private void removeOldAuctions() {
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
		db.selectRows("auctions", "`auc`='"+auction.auc+"' AND `snapshot`='"+lastSnapshot+"'");
		HashMap<String,String> newData;
		newData = db.getResult().getMap();
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
	private void addNewAuction(AuctionData auction) {
		addAuction(auction);
		addEvent("add", auction);
	}
	
	/**
	 * Gets the auc's of all the auctions in the current snapshot
	 * @throws SQLException 
	 */
	private long[] getCurrentIds() throws SQLException {
		db.call("get_current_ids");
		TableData td = db.getResult();
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
	private void addEvent(String eventType, AuctionData auction) {
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
		db.insert(td);
	}
	
	/**
	 * Adds an auction to the database.
	 * 
	 * @param auction
	 * The data on the auction.
	 */
	private void addAuction(AuctionData auction) {
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
	}
}