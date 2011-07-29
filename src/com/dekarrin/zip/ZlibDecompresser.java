package com.dekarrin.zip;

import java.util.zip.*;
import java.io.UnsupportedEncodingException;

/**
 * Decompresses binary data using ZLIB.
 */
public class ZlibDecompresser implements Decompresser {
	
	/**
	 * The data being operated on. This is zip compressed.
	 */
	private byte[] compressedData;
	
	/**
	 * The data output. This is uncompressed.
	 */
	private byte[] decompressedData;
	
	/**
	 * The size of the output buffer. This is how many bytes
	 * are attempted to be decompressed each time.
	 */
	private int outputBufferSize = 50;
	
	/**
	 * Creates a new ZlibDecompresser for the specified data.
	 *
	 * @param data
	 * The binary data to decompress.
	 */
	public ZlibDecompresser(byte[] data) {
		compressedData = data;
	}
	
	/**
	 * Creates a new ZlibDecompresser for the specified String.
	 *
	 * @param data
	 * The String to decompress.
	 */
	public ZlibDecompresser(String data) {
		compressedData = data.getBytes();
	}
	
	/**
	 * Creates a new ZlibDecompresser for the specified String.
	 *
	 * @param data
	 * The String to decompress.
	 *
	 * @param charset
	 * The character set to use for the string. If this is invalid,
	 * the string will be decoded using the default character set.
	 */
	public ZlibDecompresser(String data, String charset) {
		try {
			compressedData = data.getBytes(charset);
		} catch(UnsupportedEncodingException e) {
			compressedData = data.getBytes();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public byte[] decompress() {
		if(!alreadyDecompressed()) {
			decompressInputData();
		}
		return decompressedData;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String decompressString() {
		decompress();
		String output = new String(decompressedData);
		return output;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String decompressString(String encoding) {
		decompress();
		String output;
		try {
			output = new String(decompressedData, encoding);
		} catch(UnsupportedEncodingException e) {
			output = new String(decompressedData);
		}
		return output;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setBufferSize(int newSize) {
		outputBufferSize = newSize;
	}
	
	/**
	 * Decompresses the stored data.
	 */
	private void decompressInputData() {
		Inflater decompresser = new Inflater();
		decompresser.setInput(compressedData);
		byte[] outputBuffer = new byte[outputBufferSize];
		int actualLength = 0;
		while(!decompresser.finished()) {
			try {
				actualLength = decompresser.inflate(outputBuffer);
			} catch(DataFormatException e) {
				System.err.println("Bad DEFLATE format!");
				System.exit(1);
			}
			addToOutput(outputBuffer, actualLength);
		}
		decompresser.end();
	}
	
	/**
	 * Adds the result of an inflation to the output bytes.
	 *
	 * @param result
	 * The result of an inflation operation.
	 *
	 * @param length
	 * The length to which the result array should be processed.
	 */
	private void addToOutput(byte[] result, int length) {
		int oldDataLength = (decompressedData != null) ? decompressedData.length : 0;
		allocateDecompressedData(length);
		for(int i = 0; i < length; i++) {
			decompressedData[oldDataLength + i] = result[i];
		}
	}
	
	/**
	 * Allocates the decompressedData with more memory. If it hasn't
	 * been initialized yet, that is done as well.
	 *
	 * @param amount
	 * The extra amount to allocate to decompressedData. It's new size
	 * will be its old size + amount.
	 */
	private void allocateDecompressedData(int amount) {
		if(decompressedData == null) {
			decompressedData = new byte[0];
		}
		byte[] holder = new byte[decompressedData.length + amount];
		for(int i = 0; i < decompressedData.length; i++) {
			holder[i] = decompressedData[i];
		}
		decompressedData = holder;
	}
	
	/**
	 * Checks if the data has already been decompressed.
	 *
	 * @return
	 * True if it has been decompressed; false otherwise.
	 */
	private boolean alreadyDecompressed() {
		return (decompressedData != null);
	}
}