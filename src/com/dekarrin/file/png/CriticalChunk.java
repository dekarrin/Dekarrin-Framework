package com.dekarrin.file.png;

/**
 * Represents a critical png chunk. This class serves no purpose
 * other than classification.
 */
public abstract class CriticalChunk extends Chunk {
	
	/**
	 * Constructor for a CriticalChunk. Passes
	 * parameters up to super constructor.
	 *
	 * @param type
	 * The type of the chunk.
	 *
	 * @param data
	 * The data bytes of the chunk.
	 */
	public CriticalChunk(int type, byte[] data) {
		super(type, data);
	}
	
	/**
	 * Creates a new Chunk from only a type name.
	 *
	 * @param type
	 * The type name.
	 */
	public CriticalChunk(int type) {
		super(type);
	}
}