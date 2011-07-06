package com.dekarrin.graphics;

/**
 * Represents a channel from an image. Holds a rectangular
 * array of pixels.
 */
public class Channel {
	
	/**
	 * The pixel array for this Channel.
	 */
	private int[][] pixels;
	
	/**
	 * Keeps track of the height of this Channel.
	 */
	public int height;
	
	/**
	 * Keeps track of the width of this Channel.
	 */
	public int width;
	
	/**
	 * Creates a new channel of a specified size.
	 *
	 * @param width
	 * The width of the channel in pixels.
	 *
	 * @param height
	 * The height of the channel in pixels.
	 */
	public Channel(int width, int height) {
		this.width = width;
		this.height = height;
		pixels = new int[height][width];
		initializePixels();
	}
	
	/**
	 * Adds a row to this channel.
	 *
	 * @param index
	 * Where to insert the row. If this is less than the
	 * maximum, all the other rows are moved down from that
	 * position. If this is greater than the height, than
	 * rows are added until the height is at the index.
	 */
	public void insertRow(int index) {
		if(index >= height) {
			while(height < index + 1) {
				insertRow();
			}
		} else {
			addRow();
			for(int i = height; i > index; i--) {
				pixels[i] = pixels[i-1];
			}
			initializeRow(index);
		}
	}
	
	/**
	 * Adds a row to this Channel at the bottom.
	 */
	public void insertRow() {
		addRow();
		initializeRow(pixels.length-1);
	}
	
	/**
	 * Adds a column to this channel.
	 *
	 * @param index
	 * Where to insert the column. If this is less than the
	 * maximum, all the other rows are moved right from that
	 * position. If this is greater than the width, than
	 * rows are added until the width is at the index.
	 */
	public void insertColumn(int index) {
		if(index >= width) {
			while(width < index + 1) {
				insertColumn();
			}
		} else {
			addColumn();
			for(int i = 0; i < height; i++) {
				for(int j = width; j > index; j--) {
					pixels[i][j] = pixels[i][j-1];
				}
			}
			initializeColumn(index);
		}
	}
	
	/**
	 * Adds a column to this Channel at the right.
	 */
	public void insertColumn() {
		addColumn();
		initializeColumn(width-1);
	}
	
	/**
	 * Gets the value of this Channel at a specific x-y location.
	 *
	 * @param x
	 * The x-coordinate of the value to get.
	 *
	 * @param y
	 * The y-coordinate of the value to get.
	 *
	 * @return
	 * The value at the specified point.
	 */
	public int valueAt(int x, int y) {
		return pixels[y][x];
	}
	
	/**
	 * Sets the value of this Channel at a specific point.
	 *
	 * @param x
	 * The x-coordinate of the value to set.
	 *
	 * @param y
	 * The y-coordinate of the value to set.
	 *
	 * @param value
	 * The value to set the point to.
	 */
	public void setValueAt(int x, int y, int value) {
		pixels[y][x] = value;
	}
	
	/**
	 * Makes the pixel array one row larger.
	 */
	private void addRow() {
		height++;
		int[][] newPixels = new int[height][width];
		for(int i = 0; i < height-1; i++) {
			newPixels[i] = pixels[i];
		}
		pixels = newPixels;
	}
	
	/**
	 * Makes the pixel array one column larger.
	 */
	private void addColumn() {
		width++;
		for(int i = 0; i < height; i++) {
			int[] newPixels = new int[width];
			for(int j = 0; j < width-1; j++) {
				newPixels[j] = pixels[i][j];
			}
			pixels[i] = newPixels;
		}
	}
	
	/**
	 * Sets all the values of this Channel to 0.
	 */
	private void initializePixels() {
		for(int row = 0; row < pixels.length; row++) {
			initializeRow(row);
		}
	}
	
	/**
	 * Sets all the values of a row to 0.
	 *
	 * @param row
	 * The row to initialize.
	 */
	private void initializeRow(int row) {
		pixels[row] = new int[width];
		for(int col = 0; col < width; col++) {
			pixels[row][col] = 0;
		}
	}
	
	/**
	 * Sets all the values of a column to 0.
	 *
	 * @param col
	 * The column to initialize.
	 */
	private void initializeColumn(int col) {
		for(int i = 0; i < height; i++) {
			pixels[i][col] = 0;
		}
	}
}