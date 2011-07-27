package com.dekarrin.math;

/**
 * The slope between two points.
 */
public class Slope {
	
	/**
	 * The rise between the two points.
	 */
	public double rise;
	
	/**
	 * The run between the two points.
	 */
	public double run;

	/**
	 * Creates a new Slope for two points.
	 * 
	 * @param p1
	 * The first point.
	 * 
	 * @param p2
	 * The second point.
	 */
	public Slope(Point p1, Point p2) {
		rise = p2.get(Point.Y) - p1.get(Point.Y);
		run = p2.get(Point.X) - p1.get(Point.Y); 
	}
	
	/**
	 * Gets the slope as a single number.
	 * 
	 * @return
	 * The slope.
	 */
	public double slope() {
		return rise / run;
	}
}
