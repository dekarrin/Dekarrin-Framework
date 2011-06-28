package com.dekarrin.file.png;

/**
 * Holds information on the frequency of each palette color.
 */
public class PaletteHistogramChunk extends AncillaryChunk {
	
	/**
	 * The frequencies of each palette item.
	 */
	private int[] frequencies;
	
	/**
	 * Creates a new PaletteHistogramChunk.
	 *
	 * @param data
	 * The chunk data.
	 *
	 * @param crc
	 * The chunk CRC.
	 */
	public PaletteHistogramChunk(byte[] data, long crc) {
		super(new byte[]{104, 73, 83, 84}, data, crc); // hIST
		parseData();
	}
	
	/**
	 * Gets all frequencies.
	 *
	 * @returns
	 * The frequencies.
	 */
	public int[] getFrequencies() {
		return frequencies;
	}
	
	/**
	 * Gets a single frequency.
	 *
	 * @param index
	 * The index of the frequency to get.
	 *
	 * @returns
	 * The frequency at the specified index.
	 */
	public int getFrequency(int index) {
		return frequencies[index];
	}
	
	/**
	 * Parses chunk data.
	 */
	private void parseData() {
		frequencies = parser.parseFinalInts(2);
	}
}