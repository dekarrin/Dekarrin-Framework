package com.dekarrin.wow;

import java.util.ArrayList;

/**
 * Holds information on an item.
 */
public class ItemData {
	
	/**
	 * The unique id number of the item.
	 */
	public int id = 0;

	/**
	 * The skill level in disenchanting required to disenchant this
	 * item.
	 */
	public int disenchantingSkillRank = 0;
	
	/**
	 * A short description of the item.
	 */
	public String description = null;
	
	/**
	 * The name of the item.
	 */
	public String name = null;
	
	/**
	 * The icon to use for this item.
	 */
	public String icon = null;
	
	/**
	 * The quantity that this item stacks to.
	 */
	public int stackable = 0;
	
	/**
	 * Whether this item binds. If so, what type of binding.
	 */
	public int itemBind = 0;
	
	/**
	 * A list of additional stats.
	 */
	public StatData[] bonusStats = null;
	
	/**
	 * A list of spell IDs.
	 */
	public int[] itemSpells = null;
	
	public int[] allowableClasses = null;
	public int buyPrice = 0;
	public int itemClass = 0;
	public int itemSubClass = 0;
	public int containerSlots = 0;
	public WeaponInfo weaponInfo = null;
	public int inventoryType = 0;
	public boolean equippable = false;
	public int itemLevel = 0;
	public int maxCount = 0;
	public int maxDurability = 0;
	public int minFactionId = 0;
	public int minReputation = 0;
	public int quality = 0;
	public int sellPrice = 0;
	public int requiredLevel = 0;
	public int requiredSkill = 0;
	public int requiredSkillRank = 0;
	public int itemSource = 0;
	public int baseArmor = 0;
	public boolean hasSockets = false;
	public boolean isAuctionable = false;
	public ItemData() {super();}
}