package com.dekarrin.wow;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dekarrin.program.ConsoleProgram;
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
		item.disenchantingSkillRank	= json.getInt("disenchantingSkillRank");
		item.description		=	json.getString("description");
		item.name				=	json.getString("name");
		item.icon				=	json.getString("icon");
		item.stackable			=	json.getInt("stackable");
		item.itemBind			=	json.getInt("itemBind");
		item.itemSpells			=	extractItemSpells(json);
		item.bonusStats			=	extractBonusStats(json);
		item.buyPrice			=	json.getInt("buyPrice");
		item.allowableClasses	=	extractAllowableClasses(json);
		item.itemClass			=	json.getInt("itemClass");
		item.itemSubClass		=	json.getInt("itemSubClass");
		item.containerSlots		=	json.getInt("containerSlots");
		item.inventoryType		=	json.getInt("inventoryType");
		item.equippable			=	json.getBoolean("equippable");
		item.itemLevel			=	json.getInt("itemLevel");
		item.maxCount			=	json.getInt("maxCount");
		item.maxDurability		=	json.getInt("maxDurability");
		item.minFactionId		=	json.getInt("minFactionId");
		item.minReputation		=	json.getInt("minReputation");
		item.quality			=	json.getInt("quality");
		item.sellPrice			=	json.getInt("sellPrice");
		item.requiredSkill		=	json.getInt("requiredSkill");
		item.requiredLevel		=	json.getInt("requiredLevel");
		item.requiredSkillRank	=	json.getInt("requiredSkillRank");
		item.itemSource			=	extractItemSource(json);
		item.baseArmor			=	json.getInt("baseArmor");
		item.hasSockets			=	json.getBoolean("hasSockets");
		item.isAuctionable		=	json.getBoolean("isAuctionable");
		item.weaponInfo			=	extractWeaponInfo(json);
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
	private int extractMinDamage(JSONObject json) throws JSONException {
		JSONObject weaponInfo = json.optJSONObject("weaponInfo");
		WeaponInfo wi = null;
		if(weaponInfo != null) {
			JSONArray damage = weaponInfo.getJSONArray("damage");
			JSONObject d = damage.getJSONObject(0);
			
		}
		return wi;
	}
}
