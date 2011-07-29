package com.dekarrin.file.png;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * A length unit for measuring pixels.
 */
public enum Unit {
	
	/**
	 * Unknown unit.
	 */
	UNKNOWN(0),
	
	/**
	 * The standard metric meter.
	 */
	METER(1);
	
	/**
	 * Maps data values to Units.
	 */
	private static final Map<Integer,Unit> dataTable;
	
	static {
		dataTable = new HashMap<Integer,Unit>();
		for(Unit cm: EnumSet.allOf(Unit.class)) {
			dataTable.put(cm.dataValue(), cm);
		}
	}
	
	/**
	 * The value that is written to disk for this Unit.
	 */
	private int dataValue;
	
	/**
	 * Creates a new Unit.
	 * 
	 * @param value
	 * The value of this Unit as it is written to disk.
	 */
	private Unit(int value) {
		dataValue = value;
	}
	
	/**
	 * Gets a Unit from a data value read from disk.
	 * 
	 * @param dataValue
	 * The data value of the desired Unit.
	 * 
	 * @return
	 * The Unit that has the given data value.
	 */
	public static Unit fromData(int dataValue) {
		return dataTable.get(dataValue);
	}
	
	/**
	 * Gets the value of the unit to write to a PNG file.
	 * 
	 * @return
	 * What value to write for this Unit.
	 */
	public int dataValue() {
		return dataValue;
	}
	
}