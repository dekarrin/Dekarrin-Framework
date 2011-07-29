package com.dekarrin.file.png;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Goals for color remapping in the standard RGB color space.
 */
public enum RenderingIntent {
	
	/**
	 * Preserves the color gamut as much as possible.
	 */
	PERCEPTUAL(0),
	
	/**
	 * Preserves color appearance with respect to each color.
	 */
	RELATIVE_COLORIMETRIC(1),
	
	/**
	 * Preserves saturation at the expense of hue and lightness.
	 */
	SATURATION(2),
	
	/**
	 * Preserves colorimetry at the expense of appearance.
	 */
	ABSOLUTE_COLORIMETRIC(3);
	
	/**
	 * Maps data values to RenderingIntents.
	 */
	private static final Map<Integer,RenderingIntent> dataTable;
	
	static {
		dataTable = new HashMap<Integer,RenderingIntent>();
		for(RenderingIntent cm: EnumSet.allOf(RenderingIntent.class)) {
			dataTable.put(cm.dataValue(), cm);
		}
	}
	
	/**
	 * The value that is written to disk for this RenderingIntent.
	 */
	private int dataValue;
	
	/**
	 * Creates a new RenderingIntent.
	 * 
	 * @param value
	 * The value of this RenderingIntent as it is written to disk.
	 */
	private RenderingIntent(int value) {
		dataValue = value;
	}
	
	/**
	 * Gets a RenderingIntent from a data value read from disk.
	 * 
	 * @param dataValue
	 * The data value of the desired RenderingIntent.
	 * 
	 * @return
	 * The RenderingIntent that has the given data value.
	 */
	public static RenderingIntent fromData(int dataValue) {
		return dataTable.get(dataValue);
	}
	
	/**
	 * Gets the value of the rendering intent to write to a PNG
	 * file.
	 * 
	 * @return
	 * What value to write for this RenderingIntent.
	 */
	public int dataValue() {
		return dataValue;
	}
	
}