package com.dekarrin.file.png;

import java.io.UnsupportedEncodingException;

import com.dekarrin.io.InvalidFormatException;
import com.dekarrin.zip.*;

/**
 * Handles the compression and decompression of PNG
 * data. The actual compression/decompression algorithm
 * used depends on what compression method has been
 * selected for use.
 */
public class PngCompressionEngine implements Compresser, Decompresser {
	
	/**
	 * The size of the output buffer. This will not affect
	 * the size of the value returned, but will change the memory
	 * requirement for the operation. A larger buffer means less
	 * time wasted on the creation of a new buffer every time the
	 * current one is filled, but also uses more memory.
	 */
	private int outputBufferSize = 1024;
	
	/**
	 * The compression method currently being used.
	 */
	private CompressionMethod compressionMethod;
	
	/**
	 * The data to be compressed or decompressed.
	 */
	private byte[] contents;
	
	/**
	 * The object used for decompression.
	 */
	private Decompresser decompresser = null;
	
	/**
	 * The object used for compression.
	 */
	private Compresser compresser = null;
	
	/**
	 * Creates a new PngCompressionEngine.
	 * 
	 * @param data
	 * The bytes to compress/decompress.
	 * 
	 * @param cm
	 * The compression method to use.
	 * 
	 * @throws InvalidFormatException
	 * If the given compression method is invalid.
	 */
	public PngCompressionEngine(byte[] data, CompressionMethod cm) throws InvalidFormatException {
		contents = data;
		compressionMethod = cm;
		selectComDec();
	}
	
	/**
	 * Creates a new PngCompressionEngine from a String using
	 * the default system encoding.
	 * 
	 * @param data
	 * The String to compress/decompress.
	 * 
	 * @param cm
	 * The compression method to use.
	 * 
	 * @throws InvalidFormatException
	 * If the given compression method is invalid.
	 */
	public PngCompressionEngine(String data, CompressionMethod cm) throws InvalidFormatException {
		contents = data.getBytes();
		compressionMethod = cm;
		selectComDec();
	}
	
	/**
	 * Creates a new PngCompressionEngine from a String.
	 * 
	 * @param data
	 * The String to compress/decompress.
	 * 
	 * @param encoding
	 * The encoding to interpret the String as. If this is invalid,
	 * it will revert to the default system encoding.
	 * 
	 * @param cm
	 * The compression method to use.
	 * 
	 * @throws InvalidFormatException
	 * If the given compression method is invalid.
	 */
	public PngCompressionEngine(String data, String encoding, CompressionMethod cm) throws InvalidFormatException {
		try {
			contents = data.getBytes(encoding);
		} catch(UnsupportedEncodingException e) {
			contents = data.getBytes();
		}
		compressionMethod = cm;
		selectComDec();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public byte[] decompress() {
		decompresser.setBufferSize(outputBufferSize);
		return decompresser.decompress();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String decompressString() {
		decompresser.setBufferSize(outputBufferSize);
		return decompresser.decompressString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String decompressString(String encoding) {
		decompresser.setBufferSize(outputBufferSize);
		return decompresser.decompressString(encoding);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public byte[] compress() {
		compresser.setBufferSize(outputBufferSize);
		return compresser.compress();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String compressString() {
		compresser.setBufferSize(outputBufferSize);
		return compresser.compressString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String compressString(String encoding) {
		compresser.setBufferSize(outputBufferSize);
		return compresser.compressString(encoding);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setBufferSize(int newSize) {
		outputBufferSize = newSize;
	}
	
	/**
	 * Gets the appropriate Decompresser for the compression method
	 * that is currently being used.
	 * 
	 * @return
	 * The Decompresser for the current compression method.
	 * 
	 * @throws InvalidFormatException
	 * If the current compression method cannot be handled.
	 */
	private Decompresser getDecompresser() throws InvalidFormatException {
		Decompresser decom = null;
		switch(compressionMethod) {
			case ZLIB:
				decom = new ZlibDecompresser(contents);
				break;
				
			default:
				throw new InvalidFormatException("Bad compression method!", "png");
		}
		return decom;
	}
	
	/**
	 * Gets the appropriate Compresser for the compression method
	 * that is currently being used.
	 * 
	 * @return
	 * The Compresser for the current compression method.
	 * 
	 * @throws InvalidFormatException
	 * If the current compression method cannot be handled.
	 */
	private Compresser getCompresser() throws InvalidFormatException {
		Compresser com = null;
		switch(compressionMethod) {
			case ZLIB:
				com = new ZlibCompresser(contents);
				break;
				
			default:
				throw new InvalidFormatException("Bad compression method!", "png");
		}
		return com;
	}
	
	/**
	 * Selects both a Compresser and a Decompresser for the current
	 * contents with the current compression method.
	 * 
	 * @throws InvalidFormatException
	 * If the current compression method cannot be handled.
	 */
	private void selectComDec() throws InvalidFormatException {
		compresser = getCompresser();
		decompresser = getDecompresser();
	}
}
