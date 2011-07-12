package com.dekarrin.file.png;

/**
 * Holds information on the frequency of each palette color.
 */
public class PaletteHistogramChunk extends AncillaryChunk {
	
	/**
	 * The type of this chunk.
	 */
	public static final byte[] TYPE_CODE = {104, 73, 83, 84}; // hIST
	
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
		super(TYPE_CODE, data, crc);
		parseData();
	}
	
	/**
	 * Creates a new PaletteHistogramChunk from existing
	 * data.
	 *
	 * @param frequencies
	 * The frequencies of each color.
	 */
	public PaletteHistogramChunk(int[] frequencies) {
		super(TYPE_CODE);
		setProperties(frequencies);
		setChunkData(createDataBytes());
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
	 * Gets a single frequency.
	 *
	 * @param index
	 * The index of the frequency to get.
	 *
	 * @return
	 * The frequencies of each color.
	 */
	public int getFrequency(int index) {
		return frequencies[index];
	}
	
	/**
	 * Parses chunk data.
	 */
	private void parseData() {
		setProperties(parser.parseFinalInts(2));
	}
	
	/**
	 * Sets the internal properties of this chunk.
	 *
	 * @param frequencies
	 * The frequencies of each color.
	 */
	private void setProperties(int[] frequencies) {
		this.frequencies = frequencies;
	}
	
	/**
	 * Creates the internal data byte array.
	 *
	 * @return
	 * The data byte array.
	 */
	private byte[] createDataBytes() {
		ByteComposer composer = new ByteComposer(frequencies.length * 2);
		for(int f: frequecies) {
			composer.composeInt(f, 2);
		}
		return composer.toArray();
	}
}