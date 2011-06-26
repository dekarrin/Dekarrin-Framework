package com.dekarrin.file.png;

/**
 * Represents a gamma chunk.
 */
class GammaChunk extends AncillaryChunk {

	/**
	 * The gamma stored in this chunk.
	 */
	private int gamma;
	
	/**
	 * Creates a new GammaChunk.
	 *
	 * @param data
	 * The chunk data.
	 *
	 * @param crc
	 * The chunk crc.
	 */
	public GammaChunk(byte[] data, int crc) {
		super(new byte[]{103, 65, 77, 65}, data, crc); // gAMA
		parseData();
	}
	
	/**
	 * Gets the gamma of this chunk.
	 *
	 * @returns
	 * The gamma.
	 */
	public int getGamma() {
		return gamma;
	}
	
	/**
	 * Parses the chunk data into the content.
	 */
	private void parseData() {
		gamma = parser.parseInt();
	}
	
}