package com.dekarrin.graphics;

/**
 * Performs operations on an Image.
 */
public abstract class Tool {
	
	/**
	 * The current position of this Pencil.
	 */
	protected Point position = new Point(0, 0);
	
	/**
	 * The Image to perform the manipulations on.
	 */
	protected Image image;
	
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
		x = position.get(Point.X) + deltaX;
		y = position.get(Point.Y) + deltaY;
		Point nextPosition = new Point(x, y);
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