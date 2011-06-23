package com.dekarrin.file.png;

import java.awt.Color;

/**
 * A palette chunk from a png file.
 */
public class PaletteChunk extends Chunk implements CriticalChunk {
	
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
		int red;
		int green;
		int blue;
		int k = 0;
		for(int i = 0; i < chunkData.length; i += 3) {
			red		= (int)((int)chunkData[i] & 0xff);
			green	= (int)((int)chunkData[i+1] & 0xff);
			blue	= (int)((int)chunkData[i+2] & 0xff);
			paletteEntries[k++] = new Color(red, green, blue);
		}
	}
}