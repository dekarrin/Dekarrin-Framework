package com.dekarrin.file.png;

import com.dekarrin.util.ByteComposer;

/**
 * Chunk with uncompressed text content.
 */
class TextDataChunk extends TextChunk {
	
	/**
	 * Creates a new TextDataChunk.
	 *
	 * @param data
	 * The chunk data.
	 */
	public TextDataChunk(byte[] data) {
		super(Chunk.tEXt, data);
		parseData();
	}
	
	/**
	 * Creates a new TextDataChunk from external data.
	 *
	 * @param keyword
	 * The keyword that this TextDataChunk's contents are
	 * to be associated with.
	 *
	 * @param contents
	 * The actual text data making up this chunk.
	 */
	public TextDataChunk(String keyword, String contents) {
		super(Chunk.tEXt);
		setProperties(keyword, contents);
		setChunkData(createDataBytes());
	}
	
	/**
	 * Gets the text of this chunk.
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Turns chunk data into usable data.
	 */
	private void parseData() {
		keyword	= parser.parseString();
		text	= parser.parseRemainingString();
	}
	
	/**
	 * Sets the internal properties of this chunk.
	 *
	 * @param keyword
	 * The keyword that this TextDataChunk's contents are
	 * to be associated with.
	 *
	 * @param contents
	 * The actual text data making up this chunk.
	 */
	private void setProperties(String keyword, String contents) {
		this.keyword = keyword;
		text = contents;
	}
	
	/**
	 * Creates the data byte array for this chunk.
	 *
	 * @return
	 * The data byte array.
	 */
	private byte[] createDataBytes() {
		int dataLength = keyword.length() + text.length() + 1;
		ByteComposer composer = new ByteComposer(dataLength);
		composer.composeString(keyword, true);
		composer.composeString(text, false);
		return composer.toArray();
	}
}