package com.dekarrin.file.png;

import com.dekarrin.graphics.Color;
import com.dekarrin.graphics.GrayColor;

/**
 * Chunk for transparency data. The format of data varies, so the current
 * color type must be passed in at construction for it to parse.
 */
public class TransparencyChunk extends AncillaryChunk {
	
	/**
	 * The type code of this chunk.
	 */
	public static final byte[] TYPE_CODE = {116, 82, 78, 83};
	
	/**
	 * The mode of this background chunk. This is dependent on
	 * the color type of the png, but the amount of data in this
	 * chunk varies with each type, so it can use this
	 * information to process itself.
	 */
	private int colorMode;
	
	/**
	 * The alpha values for the palettes. This may or may
	 * not be used.
	 */
	private int[] paletteAlphas;
	
	/**
	 * The color value of the transparency. This may or may
	 * not be used.
	 */
	private Color color;
	
	/**
	 * Creates a new TransparencyChunk that automatically parses
	 * itself.
	 *
	 * @param data
	 * The data in the chunk.
	 *
	 * @param crc
	 * The CRC for this chunk.
	 *
	 * @param colorMode
	 * The mode of color in the png this chunk is a part of.
	 */
	public TransparencyChunk(byte[] data, long crc, int colorMode) {
		super(TYPE_CODE, data, crc);// tRNS
		this.colorMode = colorMode;
		parseData();
	}
	
	/**
	 * Creates a new TransparencyChunk. A TransparencyChunk created using
	 * this constructor will not parse itself until parseForColorMode()
	 * is called.
	 *
	 * @param data
	 * The data in the chunk.
	 *
	 * @param crc
	 * The CRC for this chunk.
	 */
	public TransparencyChunk(byte[] data, long crc) {
		super(TYPE_CODE, data, crc);// tRNS
	}
	
	/**
	 * Creates a new TransparencyChunk using existing data.
	 *
	 * @param transparencies
	 * The tranparency value for each palette color.
	 */
	public TransparencyChunk(int[] transparencies) {
		super(TYPE_CODE, generateData(transparencies));
	}
	
	/**
	 * Creates a new TransparencyChunk using existing data in
	 * grayscale mode.
	 *
	 * @param grayValue
	 * The value of the gray color used as the transparent color.
	 */
	public TransparencyChunk(int grayValue) {
		super(TYPE_CODE, generateData(grayValue));
	}
	
	/**
	 * Creates a new TransparencyChunk using existing data in
	 * color mode.
	 *
	 * @param redValue
	 * The value of the red color used as the transparent color.
	 *
	 * @param greenValue
	 * The value of the green color used as the transparent color.
	 *
	 * @param blueValue
	 * The value of the blue color used as the transparent color.
	 */
	public TransparencyChunk(int redValue, int greenValue, int blueValue) {
		super(TYPE_CODE, generateData(redValue, greenValue, blueValue));
	}
	
	/**
	 * Gets the color mode.
	 *
	 * @return
	 * The color mode, or null if it is not set.
	 */
	public int getColorMode() {
		return colorMode;
	} 
	
	/**
	 * Gets the entire palette alpha list.
	 *
	 * @return
	 * The palette alphas, or null if they are not set.
	 */
	public int[] getPaletteAlphas() {
		return paletteAlphas;
	}
	
	/**
	 * Gets a single alpha from the palette alphas.
	 * Behavior is undefined if the palette alphas are not yet
	 * set.
	 *
	 * @param index
	 * The index of the palette alpha.
	 *
	 * @return
	 * The alpha value.
	 */
	public int getPaletteAlpha(int index) {
		return paletteAlphas[index];
	}
	
	/**
	 * Gets the color.
	 *
	 * @return
	 * The color.
	 */
	public Color getAlpha() {
		return color;
	}
	
	/**
	 * Parses this TransparencyChunk for a certain color mode.
	 *
	 * @param colorMode
	 * The color mode to parse with respect to.
	 */
	public void parseWithColorMode(int colorMode) {
		this.colorMode = colorMode;
		if(color == null && paletteAlphas == null) {
			parseData();
		}
	}
	
	/**
	 * Parses the data.
	 */
	private void parseData() {
		parseTransparencyProperty();
	}
	
	/**
	 * Parses the correct transparency property for the current mode.
	 * Only one of the two properties is needed.
	 */
	private void parseTransparencyProperty() {
		int r,g,b;
		switch(colorMode) {
			case INDEXED_COLOR_MODE:
				setProperties(parser.parseRemainingInts(1));
				break;
				
			case GRAYSCALE_MODE:
				setProperties(parser.parseInt(2));
				break;
				
			case TRUECOLOR_MODE:
				setProperties(parser.parseInt(2), parser.parseInt(2), parser.parseInt(2));
				break;
		}
	}
	
	/**
	 * Sets the internal properties and creates a data byte array
	 * in indexed color mode.
	 *
	 * @param transparencies
	 * The transparency of each of the palette entries.
	 *
	 * @return
	 * The data byte array.
	 */
	private byte[] generateData(int[] transparencies) {
		colorMode = INDEXED_COLOR_MODE;
		setProperties(transparencies);
		byte[] data = createDataBytes();
		return data;
	}
	
	/**
	 * Sets the internal properties and creates a data byte array
	 * in grayscale mode.
	 *
	 * @param grayValue
	 * The value of the color acting as the transparent color.
	 *
	 * @return
	 * The data byte array.
	 */
	private byte[] generateData(int grayValue) {
		colorMode = GRAYSCALE_MODE;
		setProperties(grayValue);
		byte[] data = createDataBytes();
		return data;
	}
	
	/**
	 * Sets the internal properties and creates a data byte array
	 * in truecolor mode.
	 *
	 * @param redValue
	 * The red value of the color acting as the transparent color.
	 *
	 * @param greenValue
	 * The green value of the color acting as the transparent color.
	 *
	 * @param blueValue
	 * The blue value of the color acting as the transparent color.
	 *
	 * @return
	 * The data byte array.
	 */
	private byte[] generateData(int redValue, int greenValue, int blueValue) {
		colorMode = TRUECOLOR_MODE;
		setProperties(redValue, greenValue, blueValue);
		byte[] data = createDataBytes();
		return data;
	}
	
	/**
	 * Sets the internal properties for this chunk in indexed color
	 * mode.
	 *
	 * @param transparencies
	 * The transparency values for each palette entry.
	 */
	private void setProperties(int[] transparencies) {
		paletteAlphas = transparencies;
	}
	
	/**
	 * Sets the internal properties for this chunk in grayscale
	 * mode.
	 *
	 * @param grayValue
	 * The value of the color acting as the transparent color.
	 */
	private void setProperties(int grayValue) {
		GrayColor g = new GrayColor();
		g.setValue(grayValue);
		color = g;
	}
	
	/**
	 * Sets the internal properties for this chunk in color mode.
	 *
	 * @param redValue
	 * The red value of the color acting as the transparent color.
	 *
	 * @param greenValue
	 * The green value of the color acting as the transparent color.
	 *
	 * @param blueValue
	 * The blue value of the color acting as the transparent color.
	 */
	private void setProperties(int redValue, int greenValue, int blueValue) {
		color = new Color();
		color.setSamples(redValue, greenValue, blueValue);
	}

	/**
	 * Creates the data byte array.
	 *
	 * @return
	 * The data byte array.
	 */
	private byte[] createDataBytes() {
		ByteComposer composer;
		switch(colorMode) {
			case INDEXED_COLOR_MODE:
				composer = new ByteComposer(paletteAlphas.length);
				for(int i = 0; i < paletteAlphas.length; i++) {
					composer.composeInt(paletteAlphas[i], 1);
				}
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