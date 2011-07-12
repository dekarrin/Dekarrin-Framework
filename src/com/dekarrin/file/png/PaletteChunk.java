package com.dekarrin.file.png;

import com.dekarrin.graphics.Color;
import com.dekarrin.util.ByteComposer;

/**
 * A palette chunk from a png file.
 */
public class PaletteChunk extends CriticalChunk {
	
	/**
	 * The type for this chunk.
	 */
	public static final byte[] TYPE_CODE = {80, 76, 84, 69}; // PLTE
	
	/**
	 * The palette entries.
	 */
	private Color[] paletteEntries;
	
	/**
	 * Creates a new PaletteChunk and parses the supplied data.
	 *
	 * @param data
	 * The raw data in this chunk.
	 *
	 * @param crc
	 * The CRC for this chunk.
	 *
	 * @throws InvalidChunkException
	 * If the cyclic reduncdancy check read from the chunk
	 * does not match the one calculated on the type and data.
	 */
	public PaletteChunk(byte[] data, long crc) throws InvalidChunkException {
		super(TYPE_CODE, data, crc);
		parseData();
	}
	
	/**
	 * Creates a new PaletteChunk from existing data.
	 *
	 * @param colors
	 * The colors to be in this PaletteChunk.
	 */
	public PaletteChunk(Color[] colors) {
		super(TYPE_CODE);
		setProperties(colors);
		setChunkData(createDataBytes());
	}
	
	/**
	 * Gets a palette entry.
	 *
	 * @param index
	 * The index of the color.
	 *
	 * @return
	 * The color.
	 */
	public Color getColorSwatch(int index) {
		return paletteEntries[index];
	}
	
	/**
	 * Gets all palette entries.
	 *
	 * @return
	 * The palette entries.
	 */
	public Color[] getPalette() {
		return paletteEntries;
	}
	
	/**
	 * Gets the size of the palette entry list.
	 *
	 * @return
	 * The size.
	 */
	public int size() {
		return paletteEntries.length;
	}
	
	/**
	 * Parses palette information from chunk data.
	 */
	private void parseData() {
		Color[] colors = new Color[getLength() / 3];
		for(int i = 0; i < paletteEntries.length; i++) {
			colors[i] = new Color();
			colors[i].setSamples(parser.parseInt(1), parser.parseInt(1), parser.parseInt(1));
		}
		setProperties(colors);
	}
	
	/**
	 * Sets the internal properties from an external source
	 * of data.
	 *
	 * @param colors
	 * The colors to be in the palette.
	 */
	private void setProperties(Color[] colors) {
		paletteEntries = new Color[colors.length];
		for(int i = 0; i < colors.length; i++) {
			colors[i].resetAlpha();
			paletteEntries[i] = colors[i];
		}
	}
	
	/**
	 * Creates the data bytes from the internal properties.
	 *
	 * @return
	 * The data bytes.
	 */
	private byte[] createDataBytes() {
		ByteComposer data = new ByteComposer(paletteEntries.length * 3);
		for(Color c: paletteEntries) {
			data.composeInt(c.getRed(), 1);
			data.composeInt(c.getGreen(), 1);
			data.composeInt(c.getBlue(), 1);
		}
		return data.toArray();
	}
}