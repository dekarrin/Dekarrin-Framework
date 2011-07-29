package com.dekarrin.file.png;

/**
 * Chunk for image trailer.
 */
public class TrailerChunk extends Chunk {
	
	/**
	 * Creates a new TrailerChunk.
	 *
	 * @param data
	 * The data in the chunk.
	 */
	public TrailerChunk(byte[] data) {
		super(Chunk.IEND, data);
	}
	
	/**
	 * Creates a new, blank TrailerChunk.
	 */
	public TrailerChunk() {
		super(Chunk.IEND);
		setChunkData(new byte[0]);
	}
}