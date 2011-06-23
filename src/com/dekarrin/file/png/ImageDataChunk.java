package com.dekarrin.file.png;

/**
 * Chunk for image data. Though it is identified as a different
 * chunk from the standard 'Chunk', the fact that all IDAT
 * chunks must be interpreted together means that no ImageDataChunk
 * may parse its own contents.
 */
public class ImageDataChunk extends Chunk implements CriticalChunk {
	
	/**
	 * Creates a new ImageDataChunk.
	 *
	 * @param data
	 * The data in the chunk.
	 *
	 * @param crc
	 * The CRC for this chunk.
	 */
	public ImageDataChunk(byte[] data, int crc) {
		super(new byte[]{73, 68, 65, 84}, data, crc);// IDAT
	}
}