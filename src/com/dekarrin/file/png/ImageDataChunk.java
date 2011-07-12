package com.dekarrin.file.png;

/**
 * Chunk for image data. Though it is identified as a different
 * chunk from the standard 'Chunk', the fact that all IDAT
 * chunks must be interpreted together means that no ImageDataChunk
 * may parse its own contents.
 */
public class ImageDataChunk extends CriticalChunk {
	
	/**
	 * The type code for this chunk.
	 */
	public static final byte[] TYPE_CODE = {73, 68, 65, 84}; // IDAT
	
	/**
	 * Creates a new ImageDataChunk.
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
	public ImageDataChunk(byte[] data, long crc) throws InvalidChunkException {
		super(TYPE_CODE, data, crc);
	}
	
	/**
	 * Creates a new ImageDataChunk from existing
	 * data.
	 *
	 * @param imageData
	 * The image data to use in this chunk.
	 */
	public ImageDataChunk(byte[] imageData) {
		super(TYPE_CODE);
		setChunkData(imageData);
	}
}