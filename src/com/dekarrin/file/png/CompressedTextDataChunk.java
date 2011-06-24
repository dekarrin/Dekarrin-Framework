package com.dekarrin.file.png;

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
	 *
	 * @param crc
	 * The cyclic redundancy code for the chunk.
	 */
	public CompressedTextDataChunk(byte[] data, int crc) {
		super(new byte[]{122, 84, 88, 116}, data, crc); // zTXt
		parseData();
	}
	
	/**
	 * Gets the raw text from this chunk.
	 *
	 * @returns
	 * The compressed text.
	 */
	public String getCompressedText() {
		return compressedText;
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
	 * Gets the uncompressed text. If the text hasn't been decompressed,
	 * then the compressed text is inflated and stored for faster retrieval.
	 * If it has been decompressed, the previously stored uncompressed text
	 * is returned instead of decompressing again.
	 *
	 * @returns
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
		keyword				= parser.parseString();
		compressionMethod	= parser.parseInt(1);
		compressedText		= parser.parseFinalString();
	}
	
	/**
	 * Decompresses the text read from chunk data.
	 */
	private void decompressText() {
		ZlibDecompresser zd = new ZlibDecompresser(compressedText);
		text = zd.decompressString();
	}
}