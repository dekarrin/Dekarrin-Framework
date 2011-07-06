package com.dekarrin.file.png;

import com.dekarrin.graphics.Color;

/**
 * Chunk containing background color data. This will either be an
 * index referencing a palette color, or it will be a color. This
 * must be checked by any item getting the value of the background
 * color.
 */
public class BackgroundColorChunk extends AncillaryChunk {
	
	/**
	 * The mode of this background chunk. This is dependent on
	 * the color type of the png, but the amount of data in this
	 * chunk varies with each type, so it can use this
	 * information to process itself.
	 */
	private int colorMode;
	
	/**
	 * The palette index of the background color. This may or may
	 * not be used.
	 */
	private int paletteIndex;
	
	/**
	 * The color value of the background.
	 */
	private Color color;
	
	/**
	 * Creates a new BackgroundColorChunk.
	 *
	 * @param data
	 * The chunk data.
	 *
	 * @param crc
	 * The chunk crc.
	 */
	public BackgroundColorChunk(byte[] data, long crc) {
		super(new byte[]{98, 75, 71, 68}, data, crc); // bKGD
		parseData();
	}
	
	/**
	 * Gets the color mode.
	 *
	 * @return
	 * The color mode.
	 */
	public int getColorMode() {
		return colorMode;
	}
	
	/**
	 * Gets the palette reference.
	 *
	 * @param index
	 * The index of the color in the palette.
	 */
	public int getPaletteIndex() {
		return paletteIndex;
	}
	
	/**
	 * Gets the color.
	 *
	 * @return
	 * The color.
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Parses chunk data. The data that is actually parsed will be
	 * different depending on how much data is in this chunk.
	 */
	private void parseData() {
		setColorMode();
		parseBackgroundProperty();
	}
	
	/**
	 * Sets the color mode. This is determined by the amount
	 * of data in this chunk.
	 */
	private void setColorMode() {
		if(getLength() == 1) {
			colorMode = INDEXED_COLOR_MODE;
		} else if(getLength() == 2) {
			colorMode = GRAYSCALE_MODE;
		} else if(getLength() == 6) {
			colorMode = TRUECOLOR_MODE;
		}
	}
	
	/**
	 * Parses the correct background property for the current mode.
	 * Only one of the two background properties is needed.
	 */
	private void parseBackgroundProperty() {
		int r,g,b;
		switch(colorMode) {
			case INDEXED_COLOR_MODE:
				paletteIndex = parser.parseByte();
				break;
				
			case GRAYSCALE_MODE:
				g = parser.parseInt(2);
				color = new GrayColor();
				color.setValue(g);
				break;
				
			case TRUECOLOR_MODE:
				r = parser.parseInt(2);
				g = parser.parseInt(2);
				b = parser.parseInt(2);
				color = new Color();
				color.setSamples(r, g, b);
				break;
		}
	}
}