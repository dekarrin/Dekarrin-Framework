package com.dekarrin.graphics;

import java.awt.Point;

/**
 * Specifies a Chromaticity.
 */
public class Chromaticity {

	/**
	 * The red point.
	 */
	private Point red;
	
	/**
	 * The green point.
	 */
	private Point green;
	
	/**
	 * The blue point.
	 */
	private Point blue;
	
	/**
	 * The white point.
	 */
	private Point whitePoint;
	
	/**
	 * Creates a new Chromaticity.
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
	 * @param whitePoint
	 * The white point.
	 */
	public Chromaticity(Point red, Point green, Point blue, Point whitePoint) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.whitePoint = whitePoint;
	}
	
	/**
	 * Gets the red x-value.
	 *
	 * @return
	 * The red x.
	 */
	public int getRedX() {
		return red.x;
	}
	
	/**
	 * Gets the red y-value.
	 *
	 * @return
	 * The red y.
	 */
	public int getRedY() {
		return red.y;
	}
	
	/**
	 * Gets the green x-value.
	 *
	 * @return
	 * The green x.
	 */
	public int getGreenX() {
		return green.x;
	}
	
	/**
	 * Gets the green y-value.
	 *
	 * @return
	 * The green y.
	 */
	public int getGreenY() {
		return green.y;
	}
	
	/**
	 * Gets the blue x-value.
	 *
	 * @return
	 * The blue x.
	 */
	public int getBlueX() {
		return blue.x;
	}
	
	/**
	 * Gets the blue y-value.
	 *
	 * @return
	 * The blue y.
	 */
	public int getBlueY() {
		return blue.y;
	}
	
	/**
	 * Gets the white point x-value.
	 *
	 * @return
	 * The white point x.
	 */
	public int getWhitePointX() {
		return whitePoint.x;
	}
	
	/**
	 * Gets the white point y-value.
	 *
	 * @return
	 * The white point y.
	 */
	public int getWhitePointY() {
		return whitePoint.y;
	}
}