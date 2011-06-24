package com.dekarrin.file.png;

import java.awt.Point;

/**
 * Png chunk for referencing chromaticities.
 */
public class ChromaticitiesChunk extends AncillaryChunk {

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
	public ChromaticitiesChunk(byte[] data, int crc) {
		super(new byte[]{99, 72, 82, 77}, data, crc); // cHRM
		parseData();
	}
	
	/**
	 * Gets the white point chromaticity.
	 *
	 * @returns
	 * The white point chromaticity.
	 */
	public Point getWhitePoint() {
		return whitePoint;
	}
	
	/**
	 * Gets the red chromaticity.
	 *
	 * @returns
	 * The red chromaticity.
	 */
	public Point getRed() {
		return red;
	}
	
	/**
	 * Gets the green chromaticity.
	 *
	 * @returns
	 * The green chromaticity.
	 */
	public Point getGreen() {
		return green;
	}
	
	/**
	 * Gets the blue chromaticity.
	 *
	 * @returns
	 * The blue chromaticity.
	 */
	public Point getblue() {
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
		
		whitePoint	= new Point(wX, wY);
		red			= new Point(rX, rY);
		green		= new Point(gX, gY);
		blue		= new Point(bX, bY);
	}
}