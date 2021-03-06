package com.dekarrin.zip;

/**
 * Methods for decompressing binary data.
 */
public interface Decompresser {
	
	/**
	 * Decompresses the data. If the data has already been decompressed,
	 * the stored output is returned and the decompression is skipped.
	 *
	 * @return
	 * The decompressed data.
	 */
	public byte[] decompress();
	
	/**
	 * Decompresses the data into a String. The string uses the default
	 * encoding for the system.
	 *
	 * @return
	 * The decompressed String.
	 */
	public String decompressString();
	
	/**
	 * Decompresses the data into a String using a specified encoding.
	 *
	 * @param encoding
	 * The encoding to use. If this is invalid, the String will be
	 * encoded using the default character set.
	 *
	 * @return
	 * The decompressed String.
	 */
	public String decompressString(String encoding);
	
	/**
	 * Changes the size of the output buffer. This will not affect
	 * the size of the value returned, but will change the memory
	 * requirement for the operation. A larger buffer means less
	 * time wasted on the creation of a new buffer every time the
	 * current one is filled, but also uses more memory. The
	 * default size for the buffer is 50.
	 * 
	 * @param newSize
	 * The size in bytes to set the output buffer to.
	 */
	public void setBufferSize(int newSize);
}
