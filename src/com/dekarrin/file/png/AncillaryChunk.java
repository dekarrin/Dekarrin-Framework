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
	 */
	public AncillaryChunk(int type, byte[] data) {
		super(type, data);
	}
	
	/**
	 * Creates a new AncillaryChunk from only a type name.
	 *
	 * @param type
	 * The type name.
	 */
	public AncillaryChunk(int type) {
		super(type);
	}
}