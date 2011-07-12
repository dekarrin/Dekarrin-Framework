package com.dekarrin.file.png;

import java.awt.Dimension;

/**
 * Stores the intended pixel size or aspect ratio. Holds pixels
 * per unit measurements.
 */
public class PhysicalPixelDimensionsChunk extends AncillaryChunk {
	
	/**
	 * The type code of this chunk.
	 */
	public static final byte[] TYPE_CODE = {112, 72, 89, 115}; // pHYs
	
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
		super(TYPE_CODE, data, crc);
		parseData();
	}
	
	/**
	 * Creates a new PhysicalPixelDimensionsChunk using existing
	 * data.
	 *
	 * @param x
	 * The width of a pixel.
	 *
	 * @param y
	 * The height of a pixel.
	 *
	 * @param unitSpecifier
	 * The unit that the pixel dimensions are in.
	 */
	public PhysicalPixelDimensionsChunk(int x, int y, int unitSpecifier) {
		super(TYPE_CODE);
		setProperties(x, y, unitSpecifier);
		setChunkData(createDataBytes());
	}
	
	/**
	 * Gets the dimensions.
	 *
	 * @return
	 * The dimensions.
	 */
	public Dimension getDimensions() {
		return dimensions;
	}
	
	/**
	 * Gets the x-axis dimension.
	 *
	 * @return
	 * The width.
	 */
	public int getWidth() {
		return (int)dimensions.getWidth();
	}
	
	/**
	 * Gets the y-axis dimension.
	 *
	 * @return
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
	
	/**
	 * Creates the internal properties from external data.
	 *
	 * @param x
	 * The width of a pixel.
	 *
	 * @param y
	 * The height of a pixel.
	 *
	 * @param unitSpecifier
	 * The unit that the pixel dimensions are in.
	 */
	private void setProperties(int x, int y, int unitSpecifier) {
		dimensions = new Dimension(x, y);
		this.unitSpecifier = unitSpecifier;
	}
	
	/**
	 * Creates the data byte array.
	 *
	 * @return
	 * The data byte array.
	 */
	private byte[] createDataBytes() {
		ByteComposer composer = new ByteComposer(9);
		composer.composeInt(getWidth());
		composer.composeInt(getHeight());
		composer.composeInt(unitSpecifier, 1);
		return composer.toArray();
	}
}