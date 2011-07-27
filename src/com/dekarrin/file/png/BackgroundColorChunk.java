package com.dekarrin.file.png;

import com.dekarrin.file.png.PortableNetworkGraphic.ColorMode;
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
	 * The mode of this background chunk. This is dependent on
	 * the color type of the png, but the amount of data in this
	 * chunk varies with each type, so it can use this
	 * information to process itself.
	 */
	private ColorMode mode;
	
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
	 */
	public BackgroundColorChunk(byte[] data) {
		super(Chunk.bKGD, data);
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
	public BackgroundColorChunk(Color color, ColorMode colorMode) {
		super(Chunk.bKGD);
		this.mode = colorMode;
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
		super(Chunk.bKGD);
		this.mode = ColorMode.INDEXED;
		setProperties(index);
		setChunkData(createDataBytes());
	}
	
	/**
	 * Gets the color mode.
	 *
	 * @return
	 * The color mode.
	 */
	public ColorMode getColorMode() {
		return mode;
	}
	
	/**
	 * Gets the palette reference.
	 *
	 * @return
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
			mode = ColorMode.INDEXED;
		} else if(getLength() == 2) {
			mode = ColorMode.GRAYSCALE;
		} else if(getLength() == 6) {
			mode = ColorMode.TRUECOLOR;
		}
	}
	
	/**
	 * Parses the correct background property for the current mode.
	 * Only one of the two background properties is needed.
	 */
	private void parseBackgroundProperty() {
		int r,g,b;
		switch(mode) {
			case INDEXED:
				setProperties(parser.parseInt(1));
				break;
				
			case GRAYSCALE:
			case GRAYSCALE_ALPHA:
				g = parser.parseInt(2);
				GrayColor c = new GrayColor();
				c.setValue(g);
				setProperties(c);
				break;
				
			case TRUECOLOR:
			case TRUECOLOR_ALPHA:
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
		switch(mode) {
			case INDEXED:
				composer = new ByteComposer(1);
				composer.composeInt(paletteIndex, 1);
				break;
				
			case GRAYSCALE:
			case GRAYSCALE_ALPHA:
				composer = new ByteComposer(2);
				composer.composeInt(color.getRed(), 2);
				break;
				
			case TRUECOLOR:
			case TRUECOLOR_ALPHA:
				composer = new ByteComposer(6);
				composer.composeInt(color.getRed(), 2);
				composer.composeInt(color.getGreen(), 2);
				composer.composeInt(color.getBlue(), 2);
				break;
		}
		return composer.toArray();
	}
}