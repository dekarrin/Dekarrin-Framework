package com.dekarrin.graphics;

import com.dekarrin.math.Point;

/**
 * Performs operations on an Image.
 */
public abstract class Tool {
	
	/**
	 * The current position of this Pencil.
	 */
	protected Point position = new Point(2, 0, 0);
	
	/**
	 * The Image to perform the manipulations on.
	 */
	protected Image image;
	
	/**
	 * Creates a new Tool for an Image.
	 * 
	 * @param image
	 * The image to operate on.
	 */
	public Tool(Image image) {
		this.image = image;
	}
	
	/**
	 * Moves this Tool by a specified amount.
	 *
	 * @param deltaX
	 * The amount to move along the x-axis.
	 *
	 * @param deltaY
	 * The amount to move along the y-axis.
	 */
	public void move(int deltaX, int deltaY) {
		double x = position.get(Point.X) + deltaX;
		double y = position.get(Point.Y) + deltaY;
		Point nextPosition = new Point(2, x, y);
		moveTo(nextPosition);
	}
	
	/**
	 * Moves this Tool to a new position.
	 *
	 * @param newPosition
	 * The Point to move this Tool to.
	 */
	public void moveTo(Point newPosition) {
		position = newPosition;
	}
}