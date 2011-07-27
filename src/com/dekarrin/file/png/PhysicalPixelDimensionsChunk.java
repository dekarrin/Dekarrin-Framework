package com.dekarrin.file.png;

import java.awt.Dimension;

import com.dekarrin.file.png.PortableNetworkGraphic.Unit;
import com.dekarrin.util.ByteComposer;

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
	private Unit unitSpecifier;
	
	/**
	 * Creates a new PhyscialPixelDimensionsChunk.
	 *
	 * @param data
	 * The chunk data.
	 */
	public PhysicalPixelDimensionsChunk(byte[] data) {
		super(Chunk.pHYs, data);
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
	public PhysicalPixelDimensionsChunk(int x, int y, Unit unitSpecifier) {
		super(Chunk.pHYs);
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
	 * 
	 * @return
	 * The unit.
	 */
	public Unit getUnitSpecifier() {
		return unitSpecifier;
	}
	
	/**
	 * Parses chunk data for useful information.
	 */
	private void parseData() {
		int x = parser.parseInt();
		int y = parser.parseInt();
		Unit unit = Unit.values()[parser.parseInt(1)];
		setProperties(x, y, unit);
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
	private void setProperties(int x, int y, Unit unit) {
		dimensions = new Dimension(x, y);
		this.unitSpecifier = unit;
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
		composer.composeInt(unitSpecifier.ordinal(), 1);
		return composer.toArray();
	}
}