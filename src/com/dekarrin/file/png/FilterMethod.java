package com.dekarrin.file.png;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Methods to use for optimizing image data for compression.
 */
public enum FilterMethod {
	
	/**
	 * Uses a different filter algorithm depending on which one
	 * is the most efficient.
	 */
	ADAPTIVE(0);
	
	/**
	 * Maps data values to FilterMethods.
	 */
	private static final Map<Integer,FilterMethod> dataTable;
	
	static {
		dataTable = new HashMap<Integer,FilterMethod>();
		for(FilterMethod cm: EnumSet.allOf(FilterMethod.class)) {
			dataTable.put(cm.dataValue(), cm);
		}
	}
	
	/**
	 * The value that is written to disk for this FilterMethod.
	 */
	private int dataValue;
	
	/**
	 * Creates a new FilterMethod.
	 * 
	 * @param value
	 * The value of this FilterMethod as it is written to disk.
	 */
	private FilterMethod(int value) {
		dataValue = value;
	}
	
	/**
	 * Gets a FilterMethod from a data value read from disk.
	 * 
	 * @param dataValue
	 * The data value of the desired FilterMethod.
	 * 
	 * @return
	 * The FilterMethod that has the given data value.
	 */
	public static FilterMethod fromData(int dataValue) {
		return dataTable.get(dataValue);
	}
	
	/**
	 * Gets the value of the filter method to write to a PNG
	 * file.
	 * 
	 * @return
	 * What value to write for this FilterMethod.
	 */
	public int dataValue() {
		return dataValue;
	}
}