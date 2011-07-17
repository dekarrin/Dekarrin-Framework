package com.dekarrin.zip;

/**
 * Provides methods for progressively reading compressed
 * data.
 */
public class PersistantZlibDecompresser {
	
	/**
	 * DEFLATE algorithm.
	 */
	public static int COMPRESSION_METHOD_DEFLATE = 8;

	/**
	 * The compression method used by the data.
	 */
	private int compressionMethod;
	
	/**
	 * Information on the compressed data.
	 */
	private int compressionInfo;
	
	/**
	 * Initial bytes fed to compresser.
	 */
	private int presetDictionary;
	
	
}
