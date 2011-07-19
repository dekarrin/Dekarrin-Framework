package com.dekarrin.file.png;

import com.dekarrin.util.ByteComposer;

/**
 * Represents a gamma chunk.
 */
public class GammaChunk extends AncillaryChunk {

	/**
	 * The gamma stored in this chunk.
	 */
	private int gamma;
	
	/**
	 * Creates a new GammaChunk.
	 *
	 * @param data
	 * The chunk data.
	 */
	public GammaChunk(byte[] data) {
		super(Chunk.gAMA, data);
		parseData();
	}
	
	/**
	 * Creates a new GammaChunk from a value.
	 *
	 * @param gamma
	 * The gamma value.
	 */
	public GammaChunk(int gamma) {
		super(Chunk.gAMA);
		setProperties(gamma);
		setChunkData(createDataBytes());
	}
	
	/**
	 * Gets the gamma of this chunk.
	 *
	 * @return
	 * The gamma.
	 */
	public int getGamma() {
		return gamma;
	}
	
	/**
	 * Creates the internal data byte array for this chunk.
	 *
	 * @return
	 * The data byte array.
	 */
	private byte[] createDataBytes() {
		ByteComposer bytes = new ByteComposer(4);
		bytes.composeInt(gamma);
		return bytes.toArray();
	}
	
	/**
	 * Sets the internal properties of this chunk.
	 *
	 * @param gamma
	 * The gamma of this chunk.
	 */
	private void setProperties(int gamma) {
		this.gamma = gamma;
	}
	
	/**
	 * Parses the chunk data into the content.
	 */
	private void parseData() {
		setProperties(parser.parseInt());
	}
	
}