package com.dekarrin.file.png;

import java.io.UnsupportedEncodingException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.dekarrin.io.InvalidFormatException;
import com.dekarrin.zip.*;

/**
 * Methods to use for data reduction and expansion.
 */
public enum CompressionEngine implements Compresser, Decompresser {
	
	/**
	 * Uses the DEFLATE/INFLATE compression algorithm.
	 */
	ZLIB(0) {
		protected Compresser getCompresser() {
			return new ZlibCompresser(contents);
		}
		protected Decompresser getDecompresser() {
			return new ZlibDecompresser(contents);
		}
	};
	
	/**
	 * Maps data values to CompressionEngines.
	 */
	private static final Map<Integer,CompressionEngine> dataTable;
	
	static {
		dataTable = new HashMap<Integer,CompressionEngine>();
		for(CompressionEngine cm: EnumSet.allOf(CompressionEngine.class)) {
			dataTable.put(cm.dataValue(), cm);
		}
	}
	
	/**
	 * The size of the output buffer. This will not affect
	 * the size of the value returned, but will change the memory
	 * requirement for the operation. A larger buffer means less
	 * time wasted on the creation of a new buffer every time the
	 * current one is filled, but also uses more memory.
	 */
	private int outputBufferSize = 1024;
	
	/**
	 * The data to be compressed or decompressed.
	 */
	protected byte[] contents = null;
	
	/**
	 * The value that is written to disk for this CompressionEngine.
	 */
	private int dataValue;
	
	/**
	 * The object used for decompression.
	 */
	private Decompresser decompresser = null;
	
	/**
	 * The object used for compression.
	 */
	private Compresser compresser = null;
	
	/**
	 * Creates a new CompressionEngine.
	 * 
	 * @param value
	 * The value of this CompressionEngine as it is written to disk.
	 */
	private CompressionEngine(int value) {
		dataValue = value;
	}
	
	/**
	 * Gets a CompressionEngine from a data value read from disk.
	 * 
	 * @param dataValue
	 * The data value of the desired CompressionEngine.
	 * 
	 * @return
	 * The CompressionEngine that has the given data value.
	 */
	public static CompressionEngine fromData(int dataValue) {
		return dataTable.get(dataValue);
	}
	
	/**
	 * Gets the value of the compression method to write to a PNG
	 * file.
	 * 
	 * @return
	 * What value to write for this CompressionEngine.
	 */
	public int dataValue() {
		return dataValue;
	}
	
	/**
	 * Sets the contents of the data buffer.
	 * 
	 * @param contents
	 * The new contents of the buffer.
	 */
	public void setContents(byte[] contents) {
		this.contents = contents;
		selectComDec();
	}
	
	/**
	 * Sets the contents of the data buffer to a String using the
	 * default system encoding.
	 * 
	 * @param contents
	 * The String to set the contents to.
	 */
	public void setContents(String contents) {
		this.contents = contents.getBytes();
		selectComDec();
	}
	
	/**
	 * Sets the contents of the data buffer to a String.
	 * 
	 * @param contents
	 * The String to set the contents to.
	 * 
	 * @param encoding
	 * The character set to use for the string. If this is invalid,
	 * the string will be decoded using the default character set.
	 */
	public void setContents(String contents, String encoding) {
		try {
			this.contents = contents.getBytes(encoding);
		} catch(UnsupportedEncodingException e) {
			this.contents = contents.getBytes();
		}
		selectComDec();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public byte[] decompress() {
		checkState();
		decompresser.setBufferSize(outputBufferSize);
		return decompresser.decompress();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String decompressString() {
		checkState();
		decompresser.setBufferSize(outputBufferSize);
		return decompresser.decompressString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String decompressString(String encoding) {
		checkState();
		decompresser.setBufferSize(outputBufferSize);
		return decompresser.decompressString(encoding);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public byte[] compress() {
		checkState();
		compresser.setBufferSize(outputBufferSize);
		return compresser.compress();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String compressString() {
		checkState();
		compresser.setBufferSize(outputBufferSize);
		return compresser.compressString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public String compressString(String encoding) {
		checkState();
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
	 * Selects both a Compresser and a Decompresser for the current
	 * contents with the current compression method.
	 */
	private void selectComDec() {
		compresser = getCompresser();
		decompresser = getDecompresser();
	}
	
	/**
	 * Gets the appropriate Compresser for the compression method
	 * that is currently being used.
	 * 
	 * @param contents
	 * The contents to set the new Compresser to.
	 * 
	 * @return
	 * The Compresser for the current compression method.
	 */
	protected abstract Compresser getCompresser();
	
	/**
	 * Gets the appropriate Decompresser for the compression method
	 * that is currently being used.
	 * 
	 * @param contents
	 * The contents to set the new Decompresser to.
	 * 
	 * @return
	 * The Decompresser for the current compression method.
	 * 
	 * @throws InvalidFormatException
	 * If the current compression method cannot be handled.
	 */
	protected abstract Decompresser getDecompresser();
	
	/**
	 * Checks that this CompressionEngine has had its contents set.
	 */
	private void checkState() {
		if(contents == null) {
			throw new IllegalStateException("contents null");
		}
	}
}