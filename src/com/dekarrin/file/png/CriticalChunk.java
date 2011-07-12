package com.dekarrin.file.png;

/**
 * Represents a critical png chunk. This class serves no purpose
 * other than classification.
 */
public abstract class CriticalChunk extends Chunk {
	
	/**
	 * Constructor for a CriticalChunk. Passes
	 * parameters up to superconstructor.
	 *
	 * @param type
	 * The type bytes of the chunk.
	 *
	 * @param data
	 * The data bytes of the chunk.
	 *
	 * @param crc
	 * The cyclic redundancy check read from the chunk.
	 *
	 * @throws InvalidChunkException
	 * If the cyclic reduncdancy check read from the chunk
	 * does not match the one calculated on the type and data.
	 */
	public CriticalChunk(byte[] type, byte[] data, long crc) throws InvalidChunkException {
		super(type, data, crc);
	}
	
	/**
	 * Creates a new Chunk from only a type name.
	 *
	 * @param type
	 * The type name.
	 */
	public CriticalChunk(byte[] type) {
		super(type);
	}
}