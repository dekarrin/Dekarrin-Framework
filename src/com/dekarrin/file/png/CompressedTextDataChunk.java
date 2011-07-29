package com.dekarrin.file.png;

import com.dekarrin.io.InvalidFormatException;
import com.dekarrin.util.ByteComposer;

/**
 * Chunk holding compressed text data.
 */
class CompressedTextDataChunk extends TextChunk {
	
	/**
	 * The raw, compressed text.
	 */
	private String compressedText;
	
	/**
	 * The method used for compression.
	 */
	private CompressionMethod compressionMethod;
	
	/**
	 * Creates a new CompressedTextDataChunk. Does not decompress
	 * automatically; a call to getText() will, though.
	 *
	 * @param data
	 * The chunk data.
	 * 
	 * @throws InvalidFormatException
	 * If an invalid compression method is specified.
	 */
	public CompressedTextDataChunk(byte[] data) throws InvalidFormatException {
		super(Chunk.zTXt, data);
		parseData();
	}
	
	/**
	 * Creates a new CompressedTextDataChunk from external data.
	 *
	 * @param keyword
	 * The keyword to store this chunk under.
	 *
	 * @param contents
	 * The actual text contents of this chunk.
	 *
	 * @param cm
	 * The method to use for compression/decompression of the text
	 * data.
	 * 
	 * @throws InvalidFormatException
	 * If an invalid compression method is specified.
	 */
	public CompressedTextDataChunk(String keyword, String contents, CompressionMethod cm) throws InvalidFormatException {
		super(Chunk.zTXt);
		setProperties(keyword, contents, null, cm);
		setChunkData(createDataBytes());
	}
	
	/**
	 * Gets the raw text from this chunk.
	 *
	 * @return
	 * The compressed text.
	 */
	public String getCompressedText() {
		return compressedText;
	}
	
	/**
	 * Gets the compression method.
	 *
	 * @return
	 * The compression method.
	 */
	public CompressionMethod getCompressionMethod() {
		return compressionMethod;
	}
	
	/**
	 * Gets the uncompressed text. If the text hasn't been decompressed,
	 * then the compressed text is inflated and stored for faster retrieval.
	 * If it has been decompressed, the previously stored uncompressed text
	 * is returned instead of decompressing again.
	 *
	 * @return
	 * The uncompressed text.
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * Parses data into meaningful contents.
	 * 
	 * @throws InvalidFormatException
	 * If an invalid compression method is specified.
	 */
	private void parseData() throws InvalidFormatException  {
		String keyword = parser.parseString();
		CompressionMethod cm = CompressionMethod.fromData(parser.parseInt(1));
		String compressed = parser.parseRemainingString();
		setProperties(keyword, null, compressed, cm);
	}
	
	/**
	 * Decompresses the text read from chunk data.
	 * 
	 * @throws InvalidFormatException
	 * If an invalid compression method is specified.
	 */
	private void decompressText() throws InvalidFormatException {
		PngCompressionEngine eng = new PngCompressionEngine(compressedText, compressionMethod);
		text = eng.decompressString();
	}
	
	/**
	 * Compresses the text to chunk data.
	 * 
	 * @throws InvalidFormatException
	 * If an invalid compression method is specified.
	 */
	private void compressText() throws InvalidFormatException {
		PngCompressionEngine eng = new PngCompressionEngine(text, compressionMethod);
		compressedText = eng.compressString();
	}
	
	/**
	 * Sets the internal properties of this chunk.
	 *
	 * @param keyword
	 * The keyword to store this chunk under.
	 *
	 * @param contents
	 * The actual text contents of this chunk. This cannot be
	 * null at the same time as compressedContents.
	 *
	 * @param compressedContents
	 * The actual text contents of this chunk, compressed. This
	 * cannot be null at the same time as contents.
	 *
	 * @param cm
	 * The method to use for compression/decompression of the text
	 * data.
	 * 
	 * @throws InvalidFormatException
	 * If an invalid compression method is specified.
	 */
	private void setProperties(String keyword, String contents, String compressedContents, CompressionMethod cm) throws InvalidFormatException {
		this.keyword = keyword;
		this.compressionMethod = cm;
		if(contents != null) {
			text = contents;
		}
		if(compressedContents != null) {
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
	 * Creates the data bytes array for this chunk.
	 *
	 * @return
	 * The data byte array.
	 */
	private byte[] createDataBytes() {
		int dataLength = keyword.length() + compressedText.length() + 2;
		ByteComposer composer = new ByteComposer(dataLength);
		composer.composeString(keyword, true);
		composer.composeInt(compressionMethod.dataValue(), 1);
		composer.composeString(compressedText, false);
		return composer.toArray();
	}
}