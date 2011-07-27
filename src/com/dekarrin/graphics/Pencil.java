package com.dekarrin.graphics;

import com.dekarrin.math.*;

/**
 * Provides methods for drawing straight lines on an Image.
 */
public class Pencil extends Tool {
	
	/**
	 * The color of lines drawn by this Pencil.
	 */
	private Color lineColor = Color.BLACK;
	
	/**
	 * Whether the edges of a line is smoothed.
	 */
	private boolean lineAntiAlias = true;

	/**
	 * Whether or not this Pencil is currently drawing.
	 */
	private boolean down = false;
	
	/**
	 * Creates a new Pencil for an Image. Tbe new Pencil will not be
	 * down; any calls to {@link #move(int, int)} or
	 * {@link #moveTo(Point)} will not draw lines. Once
	 * {@link #down()} is called, then lines will be drawn.
	 *
	 * @param image
	 * The image to paint on.
	 */
	public Pencil(Image image) {
		super(image);
	}
	
	/**
	 * Starts drawing with this Pencil. After {@code down()} is
	 * called, subsequent calls to {@link #move(int, int) move()}
	 * will draw a line from the current position to that
	 * position.
	 */
	public void down() {
		down = true;
	}
	
	/**
	 * Stops drawing with this Pencil. After {@code up()} is
	 * called, subsequent calls to {@link #move(int, int) move()}
	 * will not draw lines until {@link #down()} is called.
	 */
	public void up() {
		down = false;
	}
	
	/**
	 * Moves this Pencil by a specified amount. Whether or not this
	 * draws a line depends on whether this Pencil is
	 * {@link #down() down()} or {@link #up()}.
	 *
	 * @param deltaX
	 * {@inheritDoc}
	 *
	 * @param deltaY
	 * {@inheritDoc}
	 */
	@Override
	public void move(int deltaX, int deltaY) {
		double x = position.get(Point.X) + deltaX;
		double y = position.get(Point.Y) + deltaY;
		Point nextPosition = new Point(2, x, y);
		moveTo(nextPosition);
	}
	
	/**
	 * Moves this Pencil to a new position. Whether or not this
	 * draws a line depends on whether this Pencil is
	 * {@link #down() down()} or {@link #up()}.
	 *
	 * @param newPosition
	 * {@inheritDoc}
	 */
	@Override
	public void moveTo(Point newPosition) {
		super.moveTo(newPosition);
		if(down) {
			drawLine(position, newPosition);
		}
	}
	
	/**
	 * Sets the Pencil color.
	 *
	 * @param c
	 * The new color of the Pencil.
	 */
	public void setColor(Color c) {
		lineColor = c;
	}
	
	/**
	 * Sets whether or not anti-aliasing should be used for
	 * the line.
	 *
	 * @param antiAlias
	 * Whether or not anti-aliasing should be used.
	 */
	public void setAntiAlias(boolean antiAlias) {
		lineAntiAlias = antiAlias;
	}
	
	/**
	 * Draws a line between two points.
	 *
	 * @param p1
	 * The first end point of the line.
	 *
	 * @param p2
	 * The second end point of the line.
	 */
	private void drawLine(Point p1, Point p2) {
		if(lineAntiAlias) {
			xiaolinLine(p1, p2);
		} else {
			bresenhamLine(p1, p2);
		}
	}
	
	/**
	 * Draws a line using Bresenham's line algorithm.
	 *
	 * @param p1
	 * The first end point of the line.
	 *
	 * @param p2
	 * The second end point of the line.
	 */
	private void bresenhamLine(Point p1, Point p2) {
		com.dekarrin.math.Slope slope = new com.dekarrin.math.Slope(p1, p2);
		boolean steep = Math.abs(slope.slope()) > 1;
		int x1,x2,y1,y2;
		x1 = (!steep) ? (int)p1.get(Point.X) : (int)p1.get(Point.Y);
		y1 = (!steep) ? (int)p1.get(Point.Y) : (int)p1.get(Point.X);
		x2 = (!steep) ? (int)p2.get(Point.X) : (int)p2.get(Point.Y);
		y2 = (!steep) ? (int)p2.get(Point.Y) : (int)p2.get(Point.X);
		int dx = (int)Math.abs(x2-x1);
		int dy = (int)Math.abs(y2-y1);
		int error = dx / 2;
		int xStep = (x1 < x2) ? 1 : -1; // for point-by-point control
		int yStep = (y1 < y2) ? 1 : -1;
		for(int y=y1,x=x1; x < x2; x += xStep) {
			if(!steep) {
				plot(x, y, 1);
			} else {
				plot(y, x, 1);
			}
			error -= dy;
			if(error < 0) {
				y += yStep;
				error += dx;
			}
		}
	}
	
	/**
	 * Draws a line using Xiaolin Wu's line algorithm.
	 *
	 * @param p1
	 * The first end point of the line.
	 *
	 * @param p2
	 * The second end point of the line.
	 */
	private void xiaolinLine(Point p1, Point p2) {
		com.dekarrin.math.Slope slope = new com.dekarrin.math.Slope(p1, p2);
		boolean steep = Math.abs(slope.slope()) > 1;
		int x1,x2,y1,y2;
		x1 = (!steep) ? (int)p1.get(Point.X) : (int)p1.get(Point.Y);
		y1 = (!steep) ? (int)p1.get(Point.Y) : (int)p1.get(Point.X);
		x2 = (!steep) ? (int)p2.get(Point.X) : (int)p2.get(Point.Y);
		y2 = (!steep) ? (int)p2.get(Point.Y) : (int)p2.get(Point.X);
		int dx = x2 - x1;
		int dy = y2 - y1;
		if(x2 < x1) {
			int t = x1;
			x1 = x2;
			x2 = t;
			t = y1;
			y1 = y2;
			y2 = t;
		}
		double gradient = (double) dy / dx;
		// handle first endpoint
		int xend = (int)Math.round(x1);
		double yend = y1 + gradient * (xend - x1);
		double xgap = rfPart(x1 + 0.5);
		int xpxl1 = xend; // this will be used in the main loop
		int ypxl1 = (int)Math.floor(yend);
		plot(xpxl1, ypxl1, rfPart(yend) * xgap);
		plot(xpxl1, ypxl1 + 1, fPart(yend) * xgap);
		double intery = yend + gradient; // first y-intersection for the main loop
		// handle second endpoint
		xend = (int)Math.round(x2);
		yend = y2 + gradient * (xend - x2);
		xgap = fPart(x2 + 0.5);
		int xpxl2 = xend; // this will be used in the main loop
		int ypxl2 = (int)Math.floor(yend);
		plot(xpxl2, ypxl2, rfPart(yend) * xgap);
		plot(xpxl2, ypxl2 + 1, fPart(yend) * xgap);
		// main loop
		for(int x = xpxl1 + 1; x < xpxl2; x++) {
			plot(x, (int)Math.floor(intery), rfPart(intery));
			plot(x, (int)Math.floor(intery) + 1, fPart(intery));
			intery += gradient;
		}
	}
	
	/**
	 * Gets the fpart's complement.
	 *
	 * @param x
	 * The number to get the rfpart of.
	 */
	private double rfPart(double x) {
		return 1 - fPart(x);
	}
	
	/**
	 * Gets the fractional part of a number.
	 *
	 * @param x
	 * The number to get the fPart of.
	 */
	private double fPart(double x) {
		return x - Math.floor(x);
	}
	
	/**
	 * Plots a pixel.
	 *
	 * @param x
	 * The x-coordinate of the pixel.
	 *
	 * @param y
	 * The y-coordinate of the pixel.
	 *
	 * @param intensity
	 * The amount that the color overpowers
	 * whatever else is there.
	 */
	private void plot(int x, int y, double intensity) {
		int alpha = (int)(intensity*lineColor.maximumValue());
		lineColor.setAlpha(alpha);
		image.setColorAt(x, y, lineColor);
	}
}