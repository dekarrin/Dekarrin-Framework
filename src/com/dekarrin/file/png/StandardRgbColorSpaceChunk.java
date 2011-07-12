package com.dekarrin.file.png;

/**
 * Chunk data for a standard RGB color space.
 */
public class StandardRgbColorSpaceChunk extends AncillaryChunk {
	
	/**
	 * The type of this chunk.
	 */
	public static final byte[] TYPE_CODE = {115, 82, 71, 66}; // sRGB

	/**
	 * The rendering intent of this png.
	 */
	private int renderingIntent;
	
	/**
	 * Creates a new StandardRgbColorSpaceChunk. The supplied data is parsed.
	 *
	 * @param data
	 * The raw data in this chunk.
	 *
	 * @param crc
	 * The CRC for this chunk.
	 */
	public StandardRgbColorSpaceChunk(byte[] data, long crc) {
		super(TYPE_CODE, data, crc);
		parseData();
	}
	
	/**
	 * Creates a new StandardRgbColorSpaceChunk from existing data.
	 *
	 * @param renderingIntent
	 * What the rendering intent is to be.
	 */
	public StandardRgbColorSpaceChunk(int renderingIntent) {
		super(TYPE_CODE);
		setProperties(renderingIntent);
		setChunkData(createDataBytes());
	}
	
	/**
	 * Gets the rendering intent.
	 *
	 * @return
	 * The rendering intent.
	 */
	public int getRenderingIntent() {
		return renderingIntent;
	}
	
	/**
	 * Parses the chunk data.
	 */
	private void parseData() {
		int intent = parser.parseInt(1);
		setProperties(intent);
	}
	
	/**
	 * Creates a databyte array from the internal properties.
	 *
	 * @return
	 * The data array.
	 */
	private byte[] createDataBytes() {
		ByteComposer composer = new ByteComposer(1);
		composer.composeInt(renderingIntent, 1);
		return composer.toArray();
	}
	
	/**
	 * Sets the properties of this chunk from an external
	 * source of data.
	 *
	 * @param renderingIntent
	 * The rendering intent of this chunk.
	 */
	private void setProperties(int renderingIntent) {
		this.renderingIntent = renderingIntent;
	}
}