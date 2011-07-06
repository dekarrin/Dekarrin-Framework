package com.dekarrin.file.png;

import com.dekarrin.graphics.Color;
import com.dekarrin.graphics.GrayColor;

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
		super(new byte[]{116, 82, 78, 83}, data, crc);// tRNS
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
		super(new byte[]{116, 82, 78, 83}, data, crc);// tRNS
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
				paletteAlphas = parser.parseFinalInts(1);
				break;
				
			case GRAYSCALE_MODE:
				g = parser.parseInt(2);
				GrayColor c = new GrayColor();
				c.setValue(g);
				color = c;
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