package com.dekarrin.file.png;

import com.dekarrin.zip.ZlibDecompresser;

/**
 * Chunk that holds international characters.
 */
public class InternationalTextDataChunk extends TextChunk {

	/**
	 * The type code for this chunk.
	 */
	public static final byte[] TYPE_CODE = {105, 84, 88, 116};

	/**
	 * Whether the text data contained is compressed.
	 */
	private boolean compressed;
	
	/**
	 * Which compression method is used.
	 */
	private int compressionMethod;
	
	/**
	 * The compressed text.
	 */
	private String compressedText;
	
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
		super(TYPE_CODE, data, crc);
		parseData();
	}
	
	/**
	 * Creates a new InternationalTextDataChunk from external
	 * data.
	 *
	 * @param keyword
	 * The keyword in the native platform langauge.
	 *
	 * @param translatedKeyword
	 * The keyword translated into this chunk's language.
	 *
	 * @param contents
	 * The text data contents of this chunk.
	 *
	 * @param language
	 * The RFC-1766 language tag for this text data.
	 *
	 * @param compressionMethod
	 * The compression method to use for this chunk's text
	 * field.
	 */
	public InternationalTextDataChunk(String keyword, String translatedKeyword, String contents, String language, int compressionMethod) {
		super(TYPE_CODE, generateData(keyword, translatedKeyword, contents, language, compressionMethod));
	}
	
	/**
	 * Checks whether this chunk is compressed.
	 *
	 * @return
	 * Whether it is compressed.
	 */
	public boolean isCompressed() {
		return compressed;
	}
	
	/**
	 * Gets the compression method.
	 *
	 * @return
	 * The compression method.
	 */
	public int getCompressionMethod() {
		return compressionMethod;
	}
	
	/**
	 * Gets the text of this chunk.
	 */
	public String getText() {
		if(text == null) {
			decompressText();
		}
		return text;
	}
	
	/**
	 * Gets the language tag.
	 *
	 * @return
	 * The language tag.
	 */
	public String getLanguageTag() {
		return languageTag;
	}
	
	/**
	 * Gets the translated keyword.
	 *
	 * @return
	 * The keyword.
	 */
	public String getTranslatedKeyword() {
		return translatedKeyword;
	}
	
	/**
	 * Parses chunk data into usable data.
	 */
	private void parseData() {
		String keyword			= parser.parseString();
		boolean compressed		= parser.parseBoolean();
		int method				= parser.parseInt(1);
		String lang				= parser.parseString();
		String transKeyword		= parser.parseString();
		String readText			= parser.parseFinalString();
		if(compressed) {
			setProperties(keyword, transKeyword, null, readText, lang, method, compressed);
		} else {
			setProperties(keyword, transKeyword, readText, null, lang, method, compressed);
		}
	}
	
	/**
	 * Decomporesses the text to its actual contents.
	 */
	private void decompressText() {
		ZlibDecompresser zd = new ZlibDecompresser(compressedText);
		text = zd.decompressString("UTF-8");
	}
	
	/**
	 * Compresses the actual text to its compact form.
	 */
	private void compressText() {
		ZlibCompresser zc = new ZlibCompresser(text);
		compressedText = zc.compressString("UTF-8");
	}
	
	/**
	 * Generates the internal byte data array and
	 * sets the internal properties.
	 *
	 * @param keyword
	 * The keyword in the native platform langauge.
	 *
	 * @param translatedKeyword
	 * The keyword translated into this chunk's language.
	 *
	 * @param contents
	 * The text data contents of this chunk.
	 *
	 * @param language
	 * The RFC-1766 language tag for this text data.
	 *
	 * @param compressionMethod
	 * The compression method to use for this chunk's text
	 * field.
	 *
	 * @return
	 * The data byte array.
	 */
	private byte[] generateData(String keyword, String translatedKeyword, String contents, String language, int compressionMethod) {
		boolean compressed = (contents > UNCOMPRESSED_TEXT_LIMIT) ? true : false;
		setProperties(keyword, translatedKeyword, contents, null, language, compressionMethod, compressed);
		byte[] data = createDataBytes();
		return data;
	}
	
	/**
	 * Sets the internal properties of this chunk.
	 *
	 * @param keyword
	 * The keyword in the native platform langauge.
	 *
	 * @param translatedKeyword
	 * The keyword translated into this chunk's language.
	 *
	 * @param contents
	 * The text data contents of this chunk. This cannot be
	 * null at the same time as compressedContents.
	 *
	 * @param compressedContents
	 * The compressed text data contents of this chunk.
	 * This cannot be null at the same time as contents.
	 *
	 * @param language
	 * The RFC-1766 language tag for this text data.
	 *
	 * @param compressionMethod
	 * The compression method to use for this chunk's text
	 * field.
	 *
	 * @param compressed
	 * Whether or not this chunk data is to be compressed.
	 */
	private void setProperties(String keyword, String translatedKeyword, String contents, String compressedContents, String language, int compressionMethod, boolean compressed) {
		this.keyword = keyword;
		this.translatedKeyword = translatedKeyword;
		this.compressionMethod = compressionMethod;
		this.compressed = compressed;
		this.languageTag = language;
		if(contents != null) {
			text = contents;
		}
		if(compressedContents 1= null) {
			compressedText = compressedContents;
		}
		if(contents == null) {
			decompressText();
		}
		if(compressedContents == null) {
			compressText();
		}
	}
	
	/**
	 * Creates the data byte array for this chunk.
	 *
	 * @return
	 * The data byte array.
	 */
	private byte[] createDataBytes() {
		int dataLength = keyword.length() + languageTag.length() + translatedKeyword.length() + text.length() + 5;
		ByteComposer composer = new ByteComposer(dataLength);
		composer.composeString(keyword, true);
		composer.composeBoolean(compressed);
		composer.composeInt(compressionMethod, 1);
		composer.composeString(languageTag, true);
		composer.composeString(translatedKeyword, true);
		if(compressed) {
			composer.composeString(compressedText, false);
		} else {
			composer.composeString(text, false);
		}
		return composer.toArray();
	}
}