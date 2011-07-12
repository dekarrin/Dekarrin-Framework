package com.dekarrin.file.png;

/**
 * Chunk that represents text data.
 */
public abstract class TextChunk extends AncillaryChunk {
	
	/**
	 * The keyword of this text data.
	 */
	protected String keyword;
	
	/**
	 * The text in this chunk.
	 */
	protected String text;
	
	/**
	 * Creates a new TextChunk.
	 *
	 * @param type
	 * The type of this chunk.
	 *
	 * @param data
	 * The data of this chunk.
	 *
	 * @param crc
	 * The CRC for the chunk.
	 *
	 * @throws InvalidChunkException
	 * If the cyclic reduncdancy check read from the chunk
	 * does not match the one calculated on the type and data.
	 */
	public TextChunk(byte[] type, byte[] data, long crc) throws InvalidChunkException {
		super(type, data, crc);
	}
	
	/**
	 * Creates a new TextChunk from only a type name.
	 *
	 * @param type
	 * The type name.
	 */
	public TextChunk(byte[] type) {
		super(type);
	}
	
	/**
	 * Retrieves the keyword from this chunk.
	 *
	 * @return
	 * The keyword.
	 */
	protected String getKeyword() {
		return keyword;
	}
	
	/**
	 * Retrieves the actual text data from this chunk.
	 *
	 * @return
	 * The text data.
	 */
	public abstract String getText();
}