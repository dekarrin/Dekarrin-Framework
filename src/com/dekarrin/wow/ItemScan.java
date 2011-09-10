package com.dekarrin.wow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dekarrin.db.TableData;
import com.dekarrin.error.TrafficException;
import com.dekarrin.program.ConsoleProgram;
import com.dekarrin.program.FatalErrorException;
import com.dekarrin.util.GrowableIntHolder;

/**
 * Gets data on items from the WOW database.
 */
public class ItemScan extends ConsoleProgram {
	
	/**
	 * The number of items to get information on every execution.
	 */
	private int itemsPerExec;
	
	private static final String ITEM_API = "/api/wow/item/";
	
	public static void main(String[] args) {
		new ItemScan(args);
	}
	
	private ApiCore core;
	
	public ItemScan(String[] args) {
		super(args);
		try {
			core = new ApiCore();
			core.db.use("itemdata");
			loadSettings();
			if(hasNullItems()) {
				downloadItemData();
			}
		} catch(SQLException e) {
			giveFatalError(e.getMessage());
		}
	}
	
	/**
	 * Loads the application settings from disk.
	 * 
	 * @throws SQLException
	 */
	private void loadSettings() throws SQLException {
		itemsPerExec = Integer.parseInt(core.getSetting("items_per_exec"));
	}
	
	/**
	 * Checks if there are any items that have not yet been looked up.
	 * 
	 * @return
	 * Whether there exist items that have no information attached locally.
	 * 
	 * @throws SQLException 
	 */
	private boolean hasNullItems() throws SQLException {
		core.db.selectItem("id", "items", "`name` IS NULL");
		return !(core.db.getResult().isEmpty());
	}
	
	/**
	 * Gets data on unknown items.
	 * 
	 * @throws SQLException
	 */
	private void downloadItemData() throws SQLException {
		int i = 0;
		while(hasNullItems() && i < itemsPerExec) {
			processNextItem();
			i++;
		}
	}
	
	/**
	 * Downloads data on an item and stores it in the database.
	 */
	private void processNextItem() {
		int itemId = getNextId();
		String response = getItemData(itemId);
		JSONObject itemJson = new JSONObject(response);
		ItemData data = parseItemResponse(itemJson);
		saveToDatabase(data);
	}
	
	/**
	 * Gets the id of the next item to get info on.
	 * 
	 * @return
	 * The id of the next item entry whose fields are null.
	 */
	private int getNextId() throws SQLException {
		String item = core.db.selectItem("id", "items", "`name`=NULL AND `description`=NULL AND `item_class`=NULL");
		int id = Integer.parseInt(item);
		return id;
	}
	
	/**
	 * Gets the next item's information from the WOW community
	 * API.
	 * 
	 * @param id
	 * The id of the item to get info on.
	 * 
	 * @return
	 * The response from the API.
	 */
	private String getItemData(int id) throws TrafficException {
		String itemData = null;
		try {
			itemData = core.getRequest(ITEM_API+id, true);
		} catch(FatalErrorException e) {
			giveFatalError(e.getMessage());
		}
		return itemData;
	}
	
	/**
	 * Parses the JSON response into an ItemData object.
	 * 
	 * @param json
	 * The JSON formatted item data.
	 * 
	 * @return
	 * The parsed ItemData object.
	 */
	private ItemData parseItemResponse(JSONObject json) throws JSONException {
		ItemData item = new ItemData();
		item.id					=	json.getInt("id");
		item.disenchantingSkillRank	= getJsonInt("disenchantingSkillRank", -1, json);
		item.description		=	getJsonString("description", json);
		item.name				=	getJsonString("name", json);
		item.icon				=	getJsonString("icon", json);
		item.stackable			=	getJsonInt("stackable", -1, json);
		item.itemBind			=	getJsonInt("itemBind", -1, json);
		item.itemSpells			=	extractItemSpells(json);
		item.bonusStats			=	extractBonusStats(json);
		item.buyPrice			=	getJsonInt("buyPrice", -1, json);
		item.allowableClasses	=	extractAllowableClasses(json);
		item.itemClass			=	getJsonInt("itemClass", -1, json);
		item.itemSubClass		=	getJsonInt("itemSubClass", -1, json);
		item.containerSlots		=	getJsonInt("containerSlots", -1, json);
		item.inventoryType		=	getJsonInt("inventoryType", -1, json);
		item.equippable			=	getJsonBoolean("equippable", false, json);
		item.itemLevel			=	getJsonInt("itemLevel", -1, json);
		item.maxCount			=	getJsonInt("maxCount", -1, json);
		item.maxDurability		=	getJsonInt("maxDurability", -1, json);
		item.minFactionId		=	getJsonInt("minFactionId", -1, json);
		item.minReputation		=	getJsonInt("minReputation", -1, json);
		item.quality			=	getJsonInt("quality", -1, json);
		item.sellPrice			=	getJsonInt("sellPrice", -1, json);
		item.requiredSkill		=	getJsonInt("requiredSkill", -1, json);
		item.requiredLevel		=	getJsonInt("requiredLevel", -1, json);
		item.requiredSkillRank	=	getJsonInt("requiredSkillRank", -1, json);
		item.itemSource			=	extractItemSource(json);
		item.baseArmor			=	getJsonInt("baseArmor", -1, json);
		item.hasSockets			=	getJsonBoolean("hasSockets", false, json);
		item.isAuctionable		=	getJsonBoolean("isAuctionable", false, json);
		item.weaponInfo			=	extractWeaponInfo(json);
		return item;
	}
	
	/**
	 * Extracts the item spells if they exist. They are then inserted
	 * into the database and the spell id is retained.
	 * 
	 * @param json
	 * The source JSONObject for this item.
	 * 
	 * @return
	 * A list of spell IDs.
	 * 
	 * @throws JSONException
	 */
	private int[] extractItemSpells(JSONObject json) throws JSONException {
		GrowableIntHolder ids = new GrowableIntHolder(0);
		JSONArray spells = json.getJSONArray("itemSpells");
		for(int i = 0; i < spells.length(); i++) {
			JSONObject s = spells.getJSONObject(i);
			ids.add(s.getInt("spellId"));
			SpellData spell = parseSpell(s);
			addSpell(spell);
		}
		if(ids.size() > 0) {
			return ids.toArray();
		} else {
			return null;
		}
	}
	
	/**
	 * Extracts the bonus stats if they exist.
	 * 
	 * @param json
	 * The source JSONObject for this item.
	 * 
	 * @return
	 * The list of StatData objects.
	 * 
	 * @throws JSONException
	 */
	private StatData[] extractBonusStats(JSONObject json) throws JSONException {
		Vector<StatData> bonusStats = new Vector<StatData>();
		JSONArray stats = json.getJSONArray("bonusStats");
		for(int i = 0; i < stats.length(); i++) {
			JSONObject s = stats.getJSONObject(i);
			bonusStats.add(parseBonusStat(s));
		}
		return bonusStats.toArray(new StatData[0]);
	}
	
	/**
	 * Gets the classes that are allowed to use this item.
	 * 
	 * @param json
	 * The source JSONObject for this item.
	 * 
	 * @return
	 * The list of classes that can use it.
	 */
	private int[] extractAllowableClasses(JSONObject json) throws JSONException {
		GrowableIntHolder ids = new GrowableIntHolder(0);
		JSONArray classes = json.optJSONArray("allowableClasses");
		if(classes != null) {
			for(int i = 0; i < classes.length(); i++) {
				ids.add(classes.getInt(i));
			}
		}
		if(ids.size() > 0) {
			return ids.toArray();
		} else {
			return null;
		}
	}
	
	/**
	 * Gets the item source from the data, if it exists.
	 * 
	 * @param json
	 * The source JSONObject for this item.
	 * 
	 * @return
	 * The id of the source.
	 */
	private int extractItemSource(JSONObject json) throws JSONException {
		JSONObject source = json.optJSONObject("itemSource");
		int id = -1;
		if(source != null) {
			id = source.getInt("sourceId");
			String sourceType = source.getString("sourceType");
			addItemSource(id, sourceType);
		}
		return id;
	}
	
	/**
	 * Extracts the weapon info from the data.
	 * 
	 * @param json
	 * The source JSONObject for this item.
	 * 
	 * @return
	 * The weapon info.
	 */
	private WeaponInfo extractWeaponInfo(JSONObject json) throws JSONException {
		WeaponInfo weaponInfo = null;
		JSONObject info = json.optJSONObject("weaponInfo");
		if(info != null) {
			weaponInfo = new WeaponInfo();
			JSONArray damage = info.getJSONArray("damage");
			JSONObject d = damage.getJSONObject(0);
			weaponInfo.minDamage = d.getInt("minDamage");
			weaponInfo.maxDamage = d.getInt("maxDamage");
			weaponInfo.weaponSpeed = info.getDouble("weaponSpeed");
			weaponInfo.dps = info.getDouble("dps");
		}
		return weaponInfo;
	}
	
	/**
	 * Gets an int from a JSONObject if it exists; otherwise returns the
	 * given default value.
	 * 
	 * @param key
	 * The key of the int to get.
	 * 
	 * @param defaultValue
	 * The value to return if the given JSONObject does not contain the key.
	 * 
	 * @param json
	 * The source JSONObject for this item.
	 * 
	 * @return
	 * The value of the given key if it exists; otherwise the given default
	 * value.
	 */
	private int getJsonInt(String key, int defaultValue, JSONObject json) throws JSONException {
		int value = defaultValue;
		if(json.has(key)) {
			value = json.getInt(key);
		}
		return value;
	}
	
	/**
	 * Gets a boolean from a JSONObject if it exists.
	 * 
	 * @param key
	 * The key of the boolean to get.
	 * 
	 * @param defaultValue
	 * The value to return if the key does not exist.
	 * 
	 * @param json
	 * The source JSONObject for this item.
	 * 
	 * @return
	 * The value of the given key if it exists; otherwise the given default
	 * value.
	 */
	private boolean getJsonBoolean(String key, boolean defaultValue, JSONObject json) throws JSONException {
		boolean value = defaultValue;
		if(json.has(key)) {
			value = json.getBoolean(key);
		}
		return value;
	}
	
	/**
	 * Gets a String from a JSONObject if it exists.
	 * 
	 * @param key
	 * The key of the String to get.
	 * 
	 * @param json
	 * The source JSONObject for this item.
	 * 
	 * @return
	 * The value of the given key if it exists; otherwise null.
	 */
	private String getJsonString(String key, JSONObject json) throws JSONException {
		String value = null;
		if(json.has(key)) {
			value = json.getString(key);
		}
		return value;
	}
	
	/**
	 * Deconstructs an ItemData struct and saves it into the database.
	 * 
	 * @param data
	 * The ItemData object to save.
	 */
	private void saveToDatabase(ItemData data) throws SQLException {
		TableData td = new TableData("items");
		addIntData(data.id, td);
		addIntData(data.disenchantingSkillRank, td);
		addStringData(data.description, td);
		addStringData(data.name, td);
		addStringData(data.icon, td);
		addIntData(data.stackable, td);
		addIntData(data.itemBind, td);
		addStatData(data.bonusStats, td);
		addIntArrData(data.itemSpells, td);
		addIntArrData(data.allowableClasses, td);
		addIntData(data.buyPrice, td);
		addIntData(data.itemClass, td);
		addIntData(data.itemSubClass, td);
		addIntData(data.containerSlots, td);
		addWeaponData(data.weaponInfo, td);
		addIntData(data.inventoryType, td);
		addBooleanData(data.equippable, td);
		addIntData(data.itemLevel, td);
		addIntData(data.maxCount, td);
		addIntData(data.maxDurability, td);
		addIntData(data.minFactionId, td);
		addIntData(data.minReputation, td);
		addIntData(data.quality, td);
		addIntData(data.sellPrice, td);
		addIntData(data.requiredSkill, td);
		addIntData(data.requiredLevel, td);
		addIntData(data.requiredSkillRank, td);
		addIntData(data.itemSource, td);
		addIntData(data.baseArmor, td);
		addBooleanData(data.hasSockets, td);
		addBooleanData(data.isAuctionable, td);
	}
}
