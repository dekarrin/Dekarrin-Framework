package com.dekarrin.file.png;

/**
 * Chunk with uncompressed text content.
 */
public class TextDataChunk extends TextChunk {
	
	/**
	 * Creates a new TextDataChunk.
	 *
	 * @param data
	 * The chunk data.
	 *
	 * @param crc
	 * The cyclic redundancy check.
	 */
	public TextDataChunk(byte[] data, int crc) {
		super(new byte[]{116, 69, 88, 116}, data, crc); // tEXt
		parseData();
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
	
}