package com.dekarrin.file.png;

import com.dekarrin.file.png.PortableNetworkGraphic.ColorMode;
import com.dekarrin.graphics.Color;
import com.dekarrin.graphics.GrayColor;
import com.dekarrin.util.ByteComposer;

/**
 * Chunk for transparency data. The format of data varies, so the current
 * color type must be passed in at construction for it to parse.
 */
public class TransparencyChunk extends AncillaryChunk {
	
	/**
	 * The mode of this background chunk. This is dependent on
	 * the color type of the png, but the amount of data in this
	 * chunk varies with each type, so it can use this
	 * information to process itself.
	 */
	private ColorMode mode;
	
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
	 * @param colorMode
	 * The color mode of the PNG file that this chunk is a part
	 * of.
	 */
	public TransparencyChunk(byte[] data, ColorMode colorMode) {
		super(Chunk.tRNS, data);
		this.mode = colorMode;
		parseData();
	}
	
	/**
	 * Creates a new TransparencyChunk. A TransparencyChunk created using
	 * this constructor will not parse itself until parseForColorMode()
	 * is called.
	 *
	 * @param data
	 * The data in the chunk.
	 */
	public TransparencyChunk(byte[] data) {
		super(Chunk.tRNS, data);
	}
	
	/**
	 * Creates a new TransparencyChunk using existing data in
	 * indexed color mode.
	 *
	 * @param transparencies
	 * The tranparency value for each palette color.
	 */
	public TransparencyChunk(int[] transparencies) {
		super(Chunk.tRNS);
		mode = ColorMode.INDEXED;
		setProperties(transparencies);
		setChunkData(createDataBytes());
	}
	
	/**
	 * Creates a new TransparencyChunk using existing data in
	 * grayscale mode.
	 *
	 * @param grayValue
	 * The value of the gray color used as the transparent color.
	 */
	public TransparencyChunk(int grayValue) {
		super(Chunk.tRNS);
		mode = ColorMode.GRAYSCALE;
		setProperties(grayValue);
		setChunkData(createDataBytes());
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
		super(Chunk.tRNS);
		mode = ColorMode.TRUECOLOR;
		setProperties(redValue, greenValue, blueValue);
		setChunkData(createDataBytes());
	}
	
	/**
	 * Gets the color mode.
	 *
	 * @return
	 * The color mode, or null if it is not set.
	 */
	public ColorMode getColorMode() {
		return mode;
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
	public Color getTransparentColor() {
		return color;
	}
	
	/**
	 * Parses this TransparencyChunk for a certain color mode.
	 *
	 * @param colorMode
	 * The color mode to parse with respect to.
	 */
	public void parseWithColorMode(ColorMode colorMode) {
		this.mode = colorMode;
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
		switch(mode) {
			case INDEXED:
				setProperties(parser.parseRemainingInts(1));
				break;
				
			case GRAYSCALE:
			case GRAYSCALE_ALPHA:
				setProperties(parser.parseInt(2));
				break;
				
			case TRUECOLOR:
			case TRUECOLOR_ALPHA:
				setProperties(parser.parseInt(2), parser.parseInt(2), parser.parseInt(2));
				break;
		}
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
		ByteComposer composer = null;
		switch(mode) {
			case INDEXED:
				composer = new ByteComposer(paletteAlphas.length);
				for(int i = 0; i < paletteAlphas.length; i++) {
					composer.composeInt(paletteAlphas[i], 1);
				}
				break;
				
			case GRAYSCALE:
			case GRAYSCALE_ALPHA:
				composer = new ByteComposer(2);
				composer.composeInt(((GrayColor)color).getValue(), 2);
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