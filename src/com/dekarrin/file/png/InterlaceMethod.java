package com.dekarrin.file.png;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Methods to use for optimizing image data for compression.
 */
public enum InterlaceMethod {
	
	/**
	 * Uses no interlacing. The image data is transmitted
	 * line-by-line from top to bottom.
	 */
	NONE(0),
	
	/**
	 * Uses the Adam7 interlacing algorithm. The image data is
	 * transmitted over a series of seven passes, with each pass
	 * transmitting more data than the last.
	 */
	ADAM7(1);
	
	/**
	 * Maps data values to InterlaceMethods.
	 */
	private static final Map<Integer,InterlaceMethod> dataTable;
	
	static {
		dataTable = new HashMap<Integer,InterlaceMethod>();
		for(InterlaceMethod cm: EnumSet.allOf(InterlaceMethod.class)) {
			dataTable.put(cm.dataValue(), cm);
		}
	}
	
	/**
	 * The value that is written to disk for this InterlaceMethod.
	 */
	private int dataValue;
	
	/**
	 * Creates a new InterlaceMethod.
	 * 
	 * @param value
	 * The value of this InterlaceMethod as it is written to disk.
	 */
	private InterlaceMethod(int value) {
		dataValue = value;
	}
	
	/**
	 * Gets a InterlaceMethod from a data value read from disk.
	 * 
	 * @param dataValue
	 * The data value of the desired InterlaceMethod.
	 * 
	 * @return
	 * The InterlaceMethod that has the given data value.
	 */
	public static InterlaceMethod fromData(int dataValue) {
		return dataTable.get(dataValue);
	}
	
	/**
	 * Gets the value of the interlace method to write to a PNG
	 * file.
	 * 
	 * @return
	 * What value to write for this InterlaceMethod.
	 */
	public int dataValue() {
		return dataValue;
	}
	
}