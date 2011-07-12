package com.dekarrin.file.png;

import com.dekarrin.graphics.Color;

/**
 * Holds data on a reduced suggested palette.
 */
public class SuggestedPaletteChunk extends AncillaryChunk {
	
	/**
	 * The type code for this chunk.
	 */
	public static final byte[] TYPE_CODE = {115, 80, 76, 84}; // sPLT
	
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
		super(TYPE_CODE, data, crc);
		parseData();
	}
	
	/**
	 * Creates a new SuggestedPaletteChunk from existing data.
	 *
	 * @param name
	 * The name of the palette.
	 *
	 * @param bitDepth
	 * The sample depth of the palette.
	 *
	 * @param redSamples
	 * The red color samples from the palette.
	 *
	 * @param greenSamples
	 * The green color samples from the palette.
	 *
	 * @param blueSamples
	 * The blue color samples from the palette.
	 *
	 * @param alphaSamples
	 * The alpha color samples from the palette.
	 * 
	 * @param frequencies
	 * The frequencies of each palette entry.
	 */
	public SuggestedPaletteChunk(String name, int bitDepth, int[] redSamples, int[] greenSamples, int[] blueSamples, int[] alphaSamples, int[] frequencies) {
		super(TYPE_CODE);
		setProperties(name, bitDepth, redSamples, greenSamples, blueSamples, alphaSamples, frequencies);
		setChunkData(createDataBytes());
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
		String name = parser.parseString();
		int depth = parser.parseInt(1);
		int sampleWidth = sampleDepth / 8;
		int paletteCount = parser.remaining() / (sampleWidth * 4 + 2);
		int[] freq = new int[paletteCount];
		int[] red = new int[paletteCount];
		int[] green = new int[paletteCount];
		int[] blue = new int[paletteCount];
		int[] alpha = new int[paletteCount];
		for(int i = 0; i < paletteCount; i++) {
			red[i]		=	parser.parseInt(sampleWidth);
			green[i]	=	parser.parseInt(sampleWidth);
			blue[i]		=	parser.parseInt(sampleWidth);
			alpha[i]	=	parser.parseInt(sampleWidth);
			freq[i]		=	parser.parseInt(2);
		}
		setProperties(name, depth, red, green, blue, alpha, freq);
	}
	
	/**
	 * Sets the internal properties.
	 *
	 * @param name
	 * The name of the palette.
	 *
	 * @param bitDepth
	 * The sample depth of the palette.
	 *
	 * @param redSamples
	 * The red color samples from the palette.
	 *
	 * @param greenSamples
	 * The green color samples from the palette.
	 *
	 * @param blueSamples
	 * The blue color samples from the palette.
	 *
	 * @param alphaSamples
	 * The alpha color samples from the palette.
	 * 
	 * @param frequencies
	 * The frequencies of each palette entry.
	 */
	private void setProperties(String name, int bitDepth, int[] redSamples, int[] greenSamples, int[] blueSamples, int[] alphaSamples, int[] frequencies) {
		paletteName = name;
		sampleDepth = bitDepth;
		this.frequencies = frequencies;
		paletteEntries = new Color[redSamples.length];
		for(int i = 0; i < redSamples.length; i++) {
			paletteEntries[i] = new Color();
			paletteEntries[i].setSamples(redSamples[i], greenSamples[i], blueSamples[i], alphaSamples[i]);
		}
	}
	
	/**
	 * Creates the data byte array.
	 *
	 * @return
	 * The data byte array.
	 */
	private byte[] createDataBytes() {
		int sampleWidth = sampleDepth / 8;
		int dataLength = paletteName.length() + 2 + ((sampleWidth * 4 + 2) * paletteEntries.length);
		ByteComposer composer = new ByteComposer(dataLength);
		composer.composeString(paletteName, true);
		composer.composeInt(sampleDepth, 1);
		for(int i = 0; i < paletteEntries.length; i++) {
			composer.composeInt(paletteEntries[i].getRed(), sampleWidth);
			composer.composeInt(paletteEntries[i].getGreen(), sampleWidth);
			composer.composeInt(paletteEntries[i].getBlue(), sampleWidth);
			composer.composeInt(paletteEntries[i].getAlpha(), sampleWidth);
			composer.composeInt(frequencies[i], 2);
		}
		return composer.toArray();
	}
}