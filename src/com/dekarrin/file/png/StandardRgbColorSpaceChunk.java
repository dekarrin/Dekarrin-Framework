package com.dekarrin.file.png;

import com.dekarrin.file.png.PortableNetworkGraphic.RenderingIntent;
import com.dekarrin.util.ByteComposer;

/**
 * Chunk data for a standard RGB color space.
 */
public class StandardRgbColorSpaceChunk extends AncillaryChunk {

	/**
	 * The rendering intent of this png.
	 */
	private RenderingIntent renderingIntent;
	
	/**
	 * Creates a new StandardRgbColorSpaceChunk. The supplied data is parsed.
	 *
	 * @param data
	 * The raw data in this chunk.
	 */
	public StandardRgbColorSpaceChunk(byte[] data) {
		super(Chunk.sRGB, data);
		parseData();
	}
	
	/**
	 * Creates a new StandardRgbColorSpaceChunk from existing data.
	 *
	 * @param renderingIntent
	 * What the rendering intent is to be.
	 */
	public StandardRgbColorSpaceChunk(RenderingIntent renderingIntent) {
		super(Chunk.sRGB);
		setProperties(renderingIntent);
		setChunkData(createDataBytes());
	}
	
	/**
	 * Gets the rendering intent.
	 *
	 * @return
	 * The rendering intent.
	 */
	public RenderingIntent getRenderingIntent() {
		return renderingIntent;
	}
	
	/**
	 * Parses the chunk data.
	 */
	private void parseData() {
		RenderingIntent intent = RenderingIntent.values()[parser.parseInt(1)];
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
		composer.composeInt(renderingIntent.ordinal(), 1);
		return composer.toArray();
	}
	
	/**
	 * Sets the properties of this chunk from an external
	 * source of data.
	 *
	 * @param renderingIntent
	 * The rendering intent of this chunk.
	 */
	private void setProperties(RenderingIntent renderingIntent) {
		this.renderingIntent = renderingIntent;
	}
}