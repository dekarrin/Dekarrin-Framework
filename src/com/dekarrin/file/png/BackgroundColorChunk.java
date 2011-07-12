package com.dekarrin.file.png;

import com.dekarrin.graphics.Color;
import com.dekarrin.graphics.GrayColor;
import com.dekarrin.util.ByteComposer;

/**
 * Chunk containing background color data. This will either be an
 * index referencing a palette color, or it will be a color. This
 * must be checked by any item getting the value of the background
 * color.
 */
public class BackgroundColorChunk extends AncillaryChunk {
	
	/**
	 * The chunk type.
	 */
	public static final byte[] TYPE_CODE = {98, 75, 71, 68}; // bKGD
	
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
	 *
	 * @throws InvalidChunkException
	 * If the cyclic reduncdancy check read from the chunk
	 * does not match the one calculated on the type and data.
	 */
	public BackgroundColorChunk(byte[] data, long crc) throws InvalidChunkException {
		super(TYPE_CODE, data, crc);
		parseData();
	}
	
	/**
	 * Creates a new BackgroundColorChunk from existing
	 * data.
	 *
	 * @param color
	 * The background color.
	 *
	 * @param colorMode
	 * THe color mode to process the color as.
	 */
	public BackgroundColorChunk(Color color, int colorMode) {
		super(TYPE_CODE);
		this.colorMode = colorMode;
		setProperties(color);
		setChunkData(createDataBytes());
	}
	
	/**
	 * Creates a new BackgroundColorChunk from existing
	 * data.
	 *
	 * @param index
	 * The index of the the palette color that is to act
	 * as the background color.
	 */
	public BackgroundColorChunk(int index) {
		super(TYPE_CODE);
		this.colorMode = INDEXED_COLOR_MODE;
		setProperties(index);
		setChunkData(createDataBytes());
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
				setProperties(parser.parseInt(1));
				break;
				
			case GRAYSCALE_MODE:
				g = parser.parseInt(2);
				GrayColor c = new GrayColor();
				c.setValue(g);
				setProperties(c);
				break;
				
			case TRUECOLOR_MODE:
				r = parser.parseInt(2);
				g = parser.parseInt(2);
				b = parser.parseInt(2);
				Color color = new Color();
				color.setSamples(r, g, b);
				setProperties(color);
				break;
		}
	}
	
	/**
	 * Sets the internal properties for this background color.
	 *
	 * @param color
	 * The color to set the background to.
	 */
	private void setProperties(Color color) {
		this.color = color;
	}
	
	/**
	 * Sets the internal properties for this background color.
	 *
	 * @param index
	 * The index of the background color.
	 */
	private void setProperties(int index) {
		paletteIndex = index;
	}
	
	/**
	 * Creats the data byte array from the internal properties.
	 *
	 * @return
	 * The data bytes.
	 */
	private byte[] createDataBytes() {
		ByteComposer composer = null;
		switch(colorMode) {
			case INDEXED_COLOR_MODE:
				composer = new ByteComposer(1);
				composer.composeInt(paletteIndex, 1);
				break;
				
			case GRAYSCALE_MODE:
				composer = new ByteComposer(2);
				composer.composeInt(color.getRed(), 2);
				break;
				
			case TRUECOLOR_MODE:
				composer = new ByteComposer(6);
				composer.composeInt(color.getRed(), 2);
				composer.composeInt(color.getGreen(), 2);
				composer.composeInt(color.getBlue(), 2);
				break;
		}
		return composer.toArray();
	}
}