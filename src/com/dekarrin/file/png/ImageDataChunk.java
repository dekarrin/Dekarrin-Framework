package com.dekarrin.file.png;

/**
 * Chunk for image data. Though it is identified as a different
 * chunk from the standard 'Chunk', the fact that all IDAT
 * chunks must be interpreted together means that no ImageDataChunk
 * may parse its own contents.
 */
class ImageDataChunk extends Chunk {
	
	/**
	 * Creates a new ImageDataChunk.
	 *
	 * @param data
	 * The data in the chunk.
	 */
	public ImageDataChunk(byte[] data) {
		super(Chunk.IDAT, data);
	}
}