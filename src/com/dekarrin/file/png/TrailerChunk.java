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
	 *
	 * @throws InvalidChunkException
	 * If the cyclic reduncdancy check read from the chunk
	 * does not match the one calculated on the type and data.
	 */
	public TrailerChunk(byte[] data, long crc) throws InvalidChunkException {
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