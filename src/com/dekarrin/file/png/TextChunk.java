package com.dekarrin.file.png;

/**
 * Chunk that represents text data.
 */
abstract class TextChunk extends Chunk {
	
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
	 */
	public TextChunk(int type, byte[] data) {
		super(type, data);
	}
	
	/**
	 * Creates a new TextChunk from only a type name.
	 *
	 * @param type
	 * The type name.
	 */
	public TextChunk(int type) {
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