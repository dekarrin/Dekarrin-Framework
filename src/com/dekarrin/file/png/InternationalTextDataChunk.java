package com.dekarrin.file.png;

import com.dekarrin.zip.ZlibDecompresser;

/**
 * Chunk that holds international characters.
 */
public class InternationalTextDataChunk extends TextChunk {

	/**
	 * Whether the text data contained is compressed.
	 */
	private boolean compressed;
	
	/**
	 * Which compression method is used.
	 */
	private int compressionMethod;
	
	/**
	 * The language of this string.
	 */
	private String languageTag;
	
	/**
	 * The keyword translated.
	 */
	private String translatedKeyword;
	
	/**
	 * Creates a new InternationalTextDataChunk.
	 *
	 * @param data
	 * The chunk data.
	 *
	 * @param crc
	 * The chunk CRC.
	 */
	public InternationalTextDataChunk(byte[] data, long crc) {
		super(new byte[]{105, 84, 88, 116}, data, crc);
		parseData();
	}
	
	/**
	 * Checks whether this chunk is compressed.
	 *
	 * @returns
	 * Whether it is compressed.
	 */
	public boolean isCompressed() {
		return compressed;
	}
	
	/**
	 * Gets the compression method.
	 *
	 * @returns
	 * The compression method.
	 */
	public int getCompressionMethod() {
		return compressionMethod;
	}
	
	/**
	 * Gets the text of this chunk.
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Gets the language tag.
	 *
	 * @returns
	 * The language tag.
	 */
	public String getLanguageTag() {
		return languageTag;
	}
	
	/**
	 * Gets the translated keyword.
	 *
	 * @returns
	 * The keyword.
	 */
	public String getTranslatedKeyword() {
		return translatedKeyword;
	}
	
	/**
	 * Parses chunk data into usable data.
	 */
	private void parseData() {
		keyword				= parser.parseString();
		compressed			= parser.parseBoolean();
		compressionMethod	= parser.parseInt(1);
		languageTag			= parser.parseString();
		translatedKeyword	= parser.parseString();
		text				= parser.parseFinalString();
		if(compressed) {
			ZlibDecompresser zd = new ZlibDecompresser(text);
			text = zd.decompressString("UTF-8");
		}
	}
}