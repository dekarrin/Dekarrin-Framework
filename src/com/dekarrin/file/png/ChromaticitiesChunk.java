package com.dekarrin.file.png;

import java.awt.Point;
import com.dekarrin.util.ByteComposer;

/**
 * PNG chunk for referencing chromaticities.
 */
class ChromaticitiesChunk extends Chunk {

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
	 */
	public ChromaticitiesChunk(byte[] data) {
		super(Chunk.cHRM, data);
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
		super(Chunk.cHRM);
		setProperties(whitePoint, red, green, blue);
		setChunkData(createDataBytes());
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