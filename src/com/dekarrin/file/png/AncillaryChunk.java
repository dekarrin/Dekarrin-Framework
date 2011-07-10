package com.dekarrin.file.png;

/**
 * Represents an ancillary png chunk. This class serves no
 * purpose other than classification.
 */
public abstract class AncillaryChunk extends Chunk {
	
	/**
	 * Constructor for an AncillaryChunk. Passes
	 * parameters up to superconstructor.
	 *
	 * @param type
	 * The type bytes of the chunk.
	 *
	 * @param data
	 * The data bytes of the chunk.
	 *
	 * @param crc
	 * The cyclic redundancy check read from the chunk.
	 */
	public AncillaryChunk(byte[] type, byte[] data, long crc) {
		super(type, data, crc);
	}
	
	/**
	 * Creates a new AncillaryChunk from only data and a type name.
	 *
	 * @param type
	 * The type name.
	 *
	 * @param data
	 * The chunk data.
	 */
	public AncillaryChunk(byte[] type, byte[] data) {
		super(type, data);
	}
}