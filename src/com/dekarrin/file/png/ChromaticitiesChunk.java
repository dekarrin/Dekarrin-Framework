package com.dekarrin.file.png;

import java.awt.Point;

/**
 * Png chunk for referencing chromaticities.
 */
public class ChromaticitiesChunk extends AncillaryChunk {
	
	/**
	 * The type of this chunk.
	 */
	public static final byte[] TYPE_CODE = {99, 72, 82, 77}; // cHRM

	/**
	 * White point chromaticity.
	 */
	private Point whitePoint;
	
	/**
	 * Red chromaticity.
	 */
	private Point red;
	
	/**
	 * Green chromaticity.
	 */
	private Point green;
	
	/**
	 * Blue chromaticity.
	 */
	private Point blue;
	
	/**
	 * Creates a new ChromaticitiesChunk.
	 *
	 * @param data
	 * The chunk data.
	 *
	 * @param crc
	 * The chunk CRC.
	 */
	public ChromaticitiesChunk(byte[] data, long crc) {
		super(TYPE_CODE, data, crc);
		parseData();
	}
	
	/**
	 * Creates a new ChromaticitiesChunk that generates its
	 * own internal data array.
	 *
	 * @param whitePoint
	 * The white point.
	 *
	 * @param red
	 * The red point.
	 *
	 * @param green
	 * The green point.
	 *
	 * @param blue
	 * The blue point.
	 */
	public ChromaticitiesChunk(Point whitePoint, Point red, Point green, Point blue) {
		super(TYPE_CODE, generateData(whitePoint, red, green, blue));
	}
	
	/**
	 * Gets the white point chromaticity.
	 *
	 * @return
	 * The white point chromaticity.
	 */
	public Point getWhitePoint() {
		return whitePoint;
	}
	
	/**
	 * Gets the red chromaticity.
	 *
	 * @return
	 * The red chromaticity.
	 */
	public Point getRed() {
		return red;
	}
	
	/**
	 * Gets the green chromaticity.
	 *
	 * @return
	 * The green chromaticity.
	 */
	public Point getGreen() {
		return green;
	}
	
	/**
	 * Gets the blue chromaticity.
	 *
	 * @return
	 * The blue chromaticity.
	 */
	public Point getBlue() {
		return blue;
	}
	
	/**
	 * Parses the chunk data into meaningful chromaticities.
	 */
	private void parseData() {
		int wX	= parser.parseInt();
		int wY	= parser.parseInt();
		int rX	= parser.parseInt();
		int rY	= parser.parseInt();
		int bX	= parser.parseInt();
		int bY	= parser.parseInt();
		int gX	= parser.parseInt();
		int gY	= parser.parseInt();
		
		setProperties(new Point(wX, wY), new Point(rX, rY), new Point(gX, gY), new Point(bX, bY));
	}
	
	/**
	 * Creates the databytes from the input data.
	 *
	 * @param whitePoint
	 * The white point.
	 *
	 * @param red
	 * The red point.
	 *
	 * @param green
	 * The green point.
	 *
	 * @param blue
	 * The blue point.
	 *
	 * @return
	 * The data field.
	 */
	private byte[] generateData(Point whitePoint, Point red, Point green, Point blue) {
		setProperties(whitePoint, red, green, blue);
		data = createDataBytes();
		return data;
	}
	
	/**
	 * Sets the internal properties of this chunk.
	 *
	 * @param whitePoint
	 * The white point.
	 *
	 * @param red
	 * The red point.
	 *
	 * @param green
	 * The green point.
	 *
	 * @param blue
	 * The blue point.
	 */
	private void setProperties(Point whitePoint, Point red, Point green, Point blue) {
		this.whitePoint = whitePoint;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	/**
	 * Writes the data bytes for this chunk.
	 *
	 * @return
	 * The byte array from the internal data.
	 */
	private byte[] createDataBytes() {
		ByteComposer bytes = new ByteComposer(32);
		bytes.composeInt(whitePoint.x);
		bytes.composeInt(whitePoint.y);
		bytes.composeInt(red.x);
		bytes.composeInt(red.y);
		bytes.composeInt(green.x);
		bytes.composeInt(green.y);
		bytes.composeInt(blue.x);
		bytes.composeInt(blue.y);
		return bytes.toArray();
	}
}