package com.dekarrin.file.png;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * The model used for each pixel's representation.
 */
public enum ColorMode {
	
	/**
	 * Each pixel is a single grayscale value.
	 */
	GRAYSCALE(0, 1),
	
	/**
	 * Each pixel is an RGB triple.
	 */
	TRUECOLOR(2, 3),
	
	/**
	 * Each pixel is an index of the color palette.
	 */
	INDEXED(3, 1),
	
	/**
	 * Each pixel is a single grayscale value and an alpha value.
	 */
	GRAYSCALE_ALPHA(4, 2),
	
	/**
	 * Each pixel is an RGB triple and an alpha value.
	 */
	TRUECOLOR_ALPHA(6, 4);
	
	/**
	 * Maps data values to ColorModes.
	 */
	private static final Map<Integer,ColorMode> dataTable;
	
	static {
		dataTable = new HashMap<Integer,ColorMode>();
		for(ColorMode cm: EnumSet.allOf(ColorMode.class)) {
			dataTable.put(cm.dataValue(), cm);
		}
	}
	
	/**
	 * The value that is written to disk for this ColorMode.
	 */
	private int dataValue;
	
	/**
	 * The number of samples in each pixel of this ColorMode.
	 */
	private int samplesPerPixel;
	
	/**
	 * Creates a new ColorType.
	 * 
	 * @param value
	 * The value of this ColorType as it is written to disk.
	 * 
	 * @param samples
	 * The number of samples every single pixel in an image
	 * with this color mode contains.
	 */
	private ColorMode(int value, int samples) {
		dataValue = value;
		samplesPerPixel = samples;
	}
	
	/**
	 * Gets a ColorMode from a data value read from disk.
	 * 
	 * @param dataValue
	 * The data value of the desired ColorMode.
	 * 
	 * @return
	 * The ColorMode that has the given data value.
	 */
	public static ColorMode fromData(int dataValue) {
		return dataTable.get(dataValue);
	}
	
	/**
	 * Gets the value of the color mode to write to a PNG
	 * file.
	 * 
	 * @return
	 * What value to write for this ColorMode.
	 */
	public int dataValue() {
		return dataValue;
	}
	
	/**
	 * Gets the number of samples in each pixel of this ColorMode.
	 * 
	 * @return
	 * The number of samples in each pixel.
	 */
	public int samples() {
		return samplesPerPixel;
	}
}