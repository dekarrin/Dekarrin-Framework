package com.dekarrin.file.png;

import com.dekarrin.graphics.Color;

/**
 * Holds data on a reduced suggested palette.
 */
public class SuggestedPaletteChunk extends AncillaryChunk {
	
	/**
	 * The name of the palette.
	 */
	private String paletteName;
	
	/**
	 * The sample depth of the palette.
	 */
	private int sampleDepth;
	
	/**
	 * The palette entries.
	 */
	private Color[] paletteEntries;
	
	/**
	 * The frequencies of each of the palette entries.
	 */
	private int[] frequencies;
	
	/**
	 * Creates a new SuggestedPaletteChunk.
	 *
	 * @param data
	 * The chunk data.
	 *
	 * @param crc
	 * The chunk CRC.
	 */
	public SuggestedPaletteChunk(byte[] data, long crc) {
		super(new byte[]{115, 80, 76, 84}, data, crc); // sPLT
		parseData();
	}
	
	/**
	 * Gets the name of this palette.
	 *
	 * @return
	 * The name.
	 */
	public String getPaletteName() {
		return paletteName;
	}
	
	/**
	 * Gets the sample depth.
	 *
	 * @return
	 * The sample depth.
	 */
	public int getSampleDepth() {
		return sampleDepth;
	}
	
	/**
	 * Gets all palette entries.
	 *
	 * @return
	 * The entries.
	 */
	public Color[] getPaletteEntries() {
		return paletteEntries;
	}
	
	/**
	 * Gets a palette entry.
	 *
	 * @param index
	 * The index of the entry.
	 *
	 * @return
	 * The entry at the specified index.
	 */
	public Color getPaletteEntry(int index) {
		return paletteEntries[index];
	}
	
	/**
	 * Gets all frequencies.
	 *
	 * @return
	 * The frequencies.
	 */
	public int[] getFrequencies() {
		return frequencies;
	}
	
	/**
	 * Gets a frequency.
	 *
	 * @param index
	 * The index of the frequency.
	 *
	 * @return
	 * The frequency at the specified index.
	 */
	public int getFrequency(int index) {
		return frequencies[index];
	}
	
	/**
	 * Parses the chunk data.
	 */
	private void parseData() {
		paletteName = parser.parseString();
		sampleDepth = parser.parseInt(1);
		int sampleWidth = sampleDepth / 8;
		int paletteCount = parser.remaining() / (sampleWidth * 4 + 2);
		paletteEntries = new Color[paletteCount];
		frequencies = new int[paletteCount];
		int r, g, b, a, freq;
		for(int i = 0; i < paletteCount; i++) {
			r = parser.parseInt(sampleWidth);
			g = parser.parseInt(sampleWidth);
			b = parser.parseInt(sampleWidth);
			a = parser.parseInt(sampleWidth);
			paletteEntries[i]	= new Color();
			paletteEntries[i].setSamples(r, g, b, a);
			frequencies[i]		= parser.parseInt(2);
		}
	}
}