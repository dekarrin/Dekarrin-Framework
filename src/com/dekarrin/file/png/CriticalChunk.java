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
	 */
	public CriticalChunk(byte[] type, byte[] data, long crc) {
		super(type, data, crc);
	}
}