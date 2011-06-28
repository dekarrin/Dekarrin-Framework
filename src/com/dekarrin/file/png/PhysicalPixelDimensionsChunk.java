package com.dekarrin.file.png;

import java.awt.Dimension;

/**
 * Stores the intended pixel size or aspect ratio. Holds pixels
 * per unit measurements.
 */
public class PhysicalPixelDimensionsChunk extends AncillaryChunk {
	
	/**
	 * The dimensions of the pixels.
	 */
	private Dimension dimensions;
	
	/**
	 * The unit specifier.
	 */
	private int unitSpecifier;
	
	/**
	 * Creates a new PhyscialPixelDimensionsChunk.
	 *
	 * @param data
	 * The chunk data.
	 *
	 * @param crc
	 * The chunk CRC.
	 */
	public PhysicalPixelDimensionsChunk(byte[] data, long crc) {
		super(new byte[]{112, 72, 89, 115}, data, crc); // pHYs
		parseData();
	}
	
	/**
	 * Gets the dimensions.
	 *
	 * @returns
	 * The dimensions.
	 */
	public Dimension getDimensions() {
		return dimensions;
	}
	
	/**
	 * Gets the x-axis dimension.
	 *
	 * @returns
	 * The width.
	 */
	public int getWidth() {
		return (int)dimensions.getWidth();
	}
	
	/**
	 * Gets the y-axis dimension.
	 *
	 * @returns
	 * The height.
	 */
	public int getHeight() {
		return (int)dimensions.getHeight();
	}
	
	/**
	 * Gets the unit specifier.
	 */
	public int getUnitSpecifier() {
		return unitSpecifier;
	}
	
	/**
	 * Parses chunk data for useful information.
	 */
	private void parseData() {
		int x = parser.parseInt();
		int y = parser.parseInt();
		dimensions = new Dimension(x, y);
		unitSpecifier = parser.parseByte();
	}
}