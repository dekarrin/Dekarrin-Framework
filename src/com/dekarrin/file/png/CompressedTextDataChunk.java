package com.dekarrin.file.png;

import com.dekarrin.zip.*;
import com.dekarrin.util.ByteComposer;

/**
 * Chunk holding compressed text data.
 */
public class CompressedTextDataChunk extends TextChunk {
	
	/**
	 * The raw, compressed text.
	 */
	private String compressedText;
	
	/**
	 * The method used for compression.
	 */
	private int compressionMethod;
	
	/**
	 * Creates a new CompressedTextDataChunk. Does not decompress
	 * automatically; a call to getText() will, though.
	 *
	 * @param data
	 * The chunk data.
	 */
	public CompressedTextDataChunk(byte[] data) {
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
	 * @param compressionMethod
	 * The method to use for compression/decompression of the text
	 * data.
	 */
	public CompressedTextDataChunk(String keyword, String contents, int compressionMethod) {
		super(Chunk.zTXt);
		setProperties(keyword, contents, null, compressionMethod);
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
	public int getCompressionMethod() {
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
		if(text == null) {
			decompressText();
		}
		return text;
	}
	
	/**
	 * Parses data into meaningful contents.
	 */
	private void parseData() {
		String keyword = parser.parseString();
		int compressionMethod = parser.parseInt(1);
		String compressed = parser.parseRemainingString();
		setProperties(keyword, null, compressed, compressionMethod);
	}
	
	/**
	 * Decompresses the text read from chunk data.
	 */
	private void decompressText() {
		ZlibDecompresser zd = new ZlibDecompresser(compressedText);
		text = zd.decompressString();
	}
	
	/**
	 * Compresses the text to chunk data.
	 */
	private void compressText() {
		ZlibCompresser zc = new ZlibCompresser(text);
		compressedText = zc.compressString();
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
	 * @param compressionMethod
	 * The method to use for compression/decompression of the text
	 * data.
	 */
	private void setProperties(String keyword, String contents, String compressedContents, int compressionMethod) {
		this.keyword = keyword;
		this.compressionMethod = compressionMethod;
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
		composer.composeInt(compressionMethod, 1);
		composer.composeString(compressedText, false);
		return composer.toArray();
	}
}