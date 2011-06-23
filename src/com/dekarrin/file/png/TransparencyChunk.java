package com.dekarrin.file.png;

/**
 * Chunk for transparency data. The format of data varies depending on
 * other chunks, so it cannot parse itself.
 */
public class TansparencyChunk extends Chunk implements AncillaryChunk {
	
	/**
	 * Creates a new TransparencyChunk.
	 *
	 * @param data
	 * The data in the chunk.
	 *
	 * @param crc
	 * The CRC for this chunk.
	 */
	public TransparencyChunk(byte[] data, int crc) {
		super(new byte[]{116, 82, 78, 83}, data, crc);// tRNS
	}
}