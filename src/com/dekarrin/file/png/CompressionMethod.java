package com.dekarrin.file.png;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Methods to use for data reduction and expansion.
 */
public enum CompressionMethod {
	
	/**
	 * Uses the DEFLATE/INFLATE compression algorithm.
	 */
	ZLIB(0);
	
	/**
	 * Maps data values to CompressionMethods.
	 */
	private static final Map<Integer,CompressionMethod> dataTable;
	
	static {
		dataTable = new HashMap<Integer,CompressionMethod>();
		for(CompressionMethod cm: EnumSet.allOf(CompressionMethod.class)) {
			dataTable.put(cm.dataValue(), cm);
		}
	}
	
	/**
	 * The value that is written to disk for this CompressionMethod.
	 */
	private int dataValue;
	
	/**
	 * Creates a new CompressionMethod.
	 * 
	 * @param value
	 * The value of this CompressionMethod as it is written to disk.
	 */
	private CompressionMethod(int value) {
		dataValue = value;
	}
	
	/**
	 * Gets a CompressionMethod from a data value read from disk.
	 * 
	 * @param dataValue
	 * The data value of the desired CompressionMethod.
	 * 
	 * @return
	 * The CompressionMethod that has the given data value.
	 */
	public static CompressionMethod fromData(int dataValue) {
		return dataTable.get(dataValue);
	}
	
	/**
	 * Gets the value of the compression method to write to a PNG
	 * file.
	 * 
	 * @return
	 * What value to write for this CompressionMethod.
	 */
	public int dataValue() {
		return dataValue;
	}
	
}