package com.dekarrin.zip;

import java.util.zip.*;
import java.io.UnsupportedEncodingException;

/**
 * Compresses binary data using ZLIB.
 */
public class ZlibCompresser {
	
	/**
	 * The data being operated on. This is uncompressed.
	 */
	private byte[] uncompressedData;
	
	/**
	 * The data output. This is zip compressed.
	 */
	private byte[] compressedData;
	
	/**
	 * The size of the output buffer. This is how many bytes
	 * are attempted to be compressed each time.
	 */
	private static final int OUTPUT_BUFFER_SIZE = 50;
	
	/**
	 * Creates a new ZlibCompresser for the specified data.
	 *
	 * @param data
	 * The binary data to compress.
	 */
	public ZlibCompresser(byte[] data) {
		uncompressedData = data;
	}
	
	/**
	 * Creates a new ZlibCompresser for the specified String.
	 *
	 * @param data
	 * The String to compress.
	 */
	public ZlibCompresser(String data) {
		uncompressedData = data.getBytes();
	}
	
	/**
	 * Creates a new ZlibCompresser for the specified String.
	 *
	 * @param data
	 * The String to compress.
	 *
	 * @param charset
	 * The character set to use for the string. If this is invalid,
	 * the string will be decoded using the default character set.
	 */
	public ZlibCompresser(String data, String charset) {
		try {
			uncompressedData = data.getBytes(charset);
		} catch(UnsupportedEncodingException e) {
			uncompressedData = data.getBytes();
		}
	}
	
	/**
	 * Compresses the data. If the data has already been compressed,
	 * the stored output is returned and the compression is skipped.
	 *
	 * @return
	 * The compressed data.
	 */
	public byte[] compress() {
		if(!alreadyCompressed()) {
			compressInputData();
		}
		return compressedData;
	}
	
	/**
	 * Compresses the data into a String. The string uses the default
	 * encoding for the system.
	 *
	 * @return
	 * The compressed String.
	 */
	public String compressString() {
		compress();
		String output = new String(compressedData);
		return output;
	}
	
	/**
	 * Compresses the data into a String using a specified encoding.
	 *
	 * @param encoding
	 * The encoding to use. If this is invalid, the String will be
	 * encoded using the default character set.
	 *
	 * @return
	 * The compressed String.
	 */
	public String compressString(String encoding) {
		compress();
		String output;
		try {
			output = new String(compressedData, encoding);
		} catch(UnsupportedEncodingException e) {
			output = new String(compressedData);
		}
		return output;
	}
	
	/**
	 * Compresses the stored data.
	 */
	private void compressInputData() {
		Deflater compresser = new Deflater();
		compresser.setInput(uncompressedData);
		compresser.finish();
		byte[] outputBuffer = new byte[OUTPUT_BUFFER_SIZE];
		int actualLength = 0;
		while(!compresser.finished()) {
			actualLength = compresser.deflate(outputBuffer);
			addToOutput(outputBuffer, actualLength);
		}
		compresser.end();
	}
	
	/**
	 * Adds the result of a deflation to the output bytes.
	 *
	 * @param result
	 * The result of a deflation operation.
	 *
	 * @param length
	 * The length to which the result array should be processed.
	 */
	private void addToOutput(byte[] result, int length) {
		int oldDataLength = (compressedData != null) ? compressedData.length : 0;
		allocateCompressedData(length);
		for(int i = 0; i < length; i++) {
			compressedData[oldDataLength + i] = result[i];
		}
	}
	
	/**
	 * Allocates the compressed data with more memory. If it hasn't
	 * been initialized yet, that is done as well.
	 *
	 * @param amount
	 * The extra amount to allocate to the compressed data. It's new size
	 * will be its old size + amount.
	 */
	private void allocateCompressedData(int amount) {
		if(compressedData == null) {
			compressedData = new byte[0];
		}
		byte[] holder = new byte[compressedData.length + amount];
		for(int i = 0; i < compressedData.length; i++) {
			holder[i] = compressedData[i];
		}
		compressedData = holder;
	}
	
	/**
	 * Checks if the data has already been compressed.
	 *
	 * @return
	 * True if it has been compressed; false otherwise.
	 */
	private boolean alreadyCompressed() {
		return (compressedData != null);
	}
}