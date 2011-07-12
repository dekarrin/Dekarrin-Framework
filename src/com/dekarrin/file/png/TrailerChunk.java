package com.dekarrin.file.png;

/**
 * Chunk for image trailer.
 */
public class TrailerChunk extends CriticalChunk {
	
	/**
	 * The type code for this chunk.
	 */
	public static final byte[] TYPE_CODE = {73, 69, 78, 68}; // IEND
	
	/**
	 * Creates a new TrailerChunk.
	 *
	 * @param data
	 * The data in the chunk.
	 *
	 * @param crc
	 * The CRC for this chunk.
	 */
	public TrailerChunk(byte[] data, long crc) {
		super(TYPE_CODE, data, crc);
	}
	
	/**
	 * Creates a new, blank TrailerChunk.
	 */
	public TrailerChunk() {
		super(TYPE_CODE);
		setChunkData(new byte[0]);
	}
}