package com.dekarrin.file.png;

/**
 * Chunk with uncompressed text content.
 */
public class TextDataChunk extends TextChunk {

	/**
	 * The type code for this chunk.
	 */
	public static final byte[] TYPE_CODE = {116, 69, 88, 116}; // tEXt
	
	/**
	 * Creates a new TextDataChunk.
	 *
	 * @param data
	 * The chunk data.
	 *
	 * @param crc
	 * The cyclic redundancy check.
	 */
	public TextDataChunk(byte[] data, long crc) {
		super(TYPE_CODE, data, crc);
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
		super(TYPE_CODE, generateData(keyword, contents));
	}
	
	/**
	 * Gets the text of this chunk.
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Turns chunk data into useable data.
	 */
	private void parseData() {
		keyword	= parser.parseString();
		text	= parser.parseFinalString();
	}
	
	/**
	 * Creates the data byte array and sets the internal
	 * properties.
	 *
	 * @param keyword
	 * The keyword that this TextDataChunk's contents are
	 * to be associated with.
	 *
	 * @param contents
	 * The actual text data making up this chunk.
	 *
	 * @return
	 * The data byte array.
	 */
	private byte[] generateData(String keyword, String contents) {
		setProperties(keyword, contents);
		byte[] data = createDataBytes();
		return data;
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