package com.dekarrin.file.png;

import java.awt.Color;

/**
 * A palette chunk from a png file.
 */
public class PaletteChunk extends CriticalChunk {
	
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
	 */
	public PaletteChunk(byte[] data, int crc) {
		super(new byte[]{80, 76, 84, 69}, data, crc); // PLTE
		parseData();
	}
	
	/**
	 * Gets a palette entry.
	 *
	 * @parma index
	 * The index of the color.
	 *
	 * @returns
	 * The color.
	 */
	public Color getColorSwatch(int index) {
		return paletteEntries[index];
	}
	
	/**
	 * Gets all palette entries.
	 *
	 * @returns
	 * The palette entries.
	 */
	public Color[] getPalette() {
		return paletteEntries;
	}
	
	/**
	 * Parses palette information from chunk data.
	 */
	private void parseData() {
		paletteEntries = new Color[chunkData.length / 3];
		int r, g, b;
		for(int i = 0; i < paletteEntries.length; i++) {
			r = parser.parseInt(1);
			g = parser.parseInt(1);
			b = parser.parseInt(1);
			paletteEntries[i] = new Color(r, g, b);
		}
	}
}