package com.dekarrin.wow;

public enum Faction {
	ALLIANCE(1),
	HORDE(2),
	NEUTRAL(4),
	ALLIANCE_HORDE(3),
	HORDE_NEUTRAL(6),
	ALLIANCE_NEUTRAL(5),
	ALL(7);
	
	private int bitValue;
	
	private Faction(int mask) {
		this.bitValue = mask;
	}
	
	public int getMask() {
		return bitValue;
	}
}
