package com.dekarrin.file.png;

/**
 * Chunk that holds international characters.
 */
public class InternationalTextDataChunk extends TextChunk implements AncillaryChunk {

	/**
	 * Whether the text data contained is compressed.
	 */
	private boolean isCompressed;
	
	/**
	 * Which compression method is used.
	 */
	private byte compressionMethod;
	
	
}