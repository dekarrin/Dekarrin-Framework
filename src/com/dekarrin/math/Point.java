package com.dekarrin.math;

/**
 * Represents a coordinate in Euclidean space.
 */
public class Point implements Cloneable {
	
	/**
	 * The position index for the x coordinate.
	 */
	public static final int X = 0;
	
	/**
	 * The position index for the y coordinate.
	 */
	public static final int Y = 1;
	
	/**
	 * The position index for the z coordinate.
	 */
	public static final int Z = 2;
	
	/**
	 * The default dimensions for a new Point. This
	 * is used when the dimension is unspecified in the
	 * constructor. By default, it is set to 2.
	 */
	public static int defaultDimensions = 2;
	
	/**
	 * Changes the default number of dimensions for new
	 * Points.
	 * 
	 * @param d
	 * The new number of default dimensions.
	 */
	public static void setDefaultDimensions(int d) {
		defaultDimensions = d;
	}
	
	/**
	 * The coordinates of this Point.
	 */
	private double[] coords;

	/**
	 * Creates a new Point with {@code d} dimensions.
	 * 
	 * @param d
	 * The number of dimensions for the Point to have.
	 */
	public Point(int d) {
		coords = new double[d];
	}
	
	/**
	 * Creates a new Point with {@code d} dimensions
	 * and coordinates.
	 * 
	 * @param d
	 * The number of dimensions.
	 * 
	 * @param coords
	 * The coordinates of the Point. There must be the
	 * same number of coordinates as there are dimensions.
	 */
	public Point(int d, double... coords) {
		coords = new double[d];
		for(int i = 0; i < coords.length; i++) {
			this.coords[i] = coords[i];
		}
	}
	
	/**
	 * Creates a new Point with the default number of
	 * dimensions.
	 */
	public Point() {
		coords = new double[Point.defaultDimensions];
	}
	
	/**
	 * Gets a specific coordinate by its index.
	 * 
	 * @param index
	 * The index of the coordinate. The first
	 * coordinate in an ordered tuple is always
	 * 0.
	 * 
	 * @return
	 * The coordinate.
	 */
	public double get(int index) {
		return coords[index];
	}
	
	/**
	 * Sets a specific coordinate by its index.
	 * 
	 * @param index
	 * The index of the coordinate. The first
	 * coordinate in an ordered tuple is always
	 * 0.
	 * 
	 * @param coord
	 * The value to set the coordinate to.
	 */
	public void set(int index, double coord) {
		coords[index] = coord;
	}
	
	/**
	 * Gets the dimension of this Point.
	 */
	public int dimension() {
		return coords.length;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Point clone() {
		Point p = new Point(dimension());
		for(int i = 0; i < dimension(); i++) {
			p.set(i, get(i));
		}
		return p;
	}
}
