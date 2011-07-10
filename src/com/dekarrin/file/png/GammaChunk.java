package com.dekarrin.file.png;

/**
 * Represents a gamma chunk.
 */
public class GammaChunk extends AncillaryChunk {
	
	/**
	 * The type of this chunks.
	 */
	public static final byte[] TYPE_CODE = {103, 65, 77, 65}; // gAMA

	/**
	 * The gamma stored in this chunk.
	 */
	private int gamma;
	
	/**
	 * Creates a new GammaChunk.
	 *
	 * @param data
	 * The chunk data.
	 *
	 * @param crc
	 * The chunk crc.
	 */
	public GammaChunk(byte[] data, long crc) {
		super(TYPE_CODE, data, crc);
		parseData();
	}
	
	/**
	 * Creates a new GammaChunk from a value.
	 *
	 * @param gamma
	 * The gamma value.
	 */
	public GammaChunk(int gamma) {
		super(TYPE_CODE, generateData(gamma))
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
	 * Creates the data field for this chunk.
	 *
	 * @param gamma
	 * The gamma value for this chunk.
	 *
	 * @return
	 * The data field.
	 */
	private byte[] generateData(int gamma) {
		setProperties()
		data = createDataBytes();
		return data;
	}
	
	/**
	 * Creates the data field for this chunk from the internal
	 * properties.
	 */
	private void createDataBytes() {
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