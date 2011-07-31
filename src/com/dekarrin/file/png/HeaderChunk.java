package com.dekarrin.file.png;

import com.dekarrin.util.ByteComposer;

/**
 * The header chunk from a PNG file.
 */
class HeaderChunk extends Chunk {
	
	/**
	 * The width of the image.
	 */
	private int width;
	
	/**
	 * The height of the image.
	 */
	private int height;
	
	/**
	 * The sample depth of the image.
	 */
	private int sampleDepth;
	
	/**
	 * Interpretation of image data.
	 *
	 * 0 - Each pixel is a grayscale sample
	 * 2 - Each pixel is an RGB triple
	 * 3 - Each pixel is a palette index
	 * 4 - Each pixel is a grayscale sample followed by alpha
	 * 6 - Each pixel is an RGB triple followed by alpha
	 */
	private ColorMode mode;
	
	/**
	 * Compression method. Currently, only deflate/inflate is
	 * defined. If this is not 0, it should result in an error.
	 */
	private CompressionEngine compressionEngine;
	
	/**
	 * Filter method. Only 0 is defined, and an error should
	 * result from any other value.
	 */
	private FilterMethod filterMethod;
	
	/**
	 * Interlace method. Typically either 0 (none) or 1
	 * (Adam7).
	 */
	private InterlaceMethod interlaceMethod;
	
	/**
	 * Creates a new HeaderChunk. The supplied data is parsed.
	 *
	 * @param data
	 * The raw data in this chunk.
	 */
	public HeaderChunk(byte[] data) {
		super(Chunk.IHDR, data);
		parseData();
	}
	
	/**
	 * Creates a new HeaderChunk and generates the internal
	 * data from the parameters.
	 *
	 * @param width
	 * The width of this PNG.
	 *
	 * @param height
	 * The height of this PNG.
	 *
	 * @param depth
	 * The sample depth of this PNG.
	 *
	 * @param colorType
	 * The color type of this PNG.
	 *
	 * @param compressionMethod
	 * The compression method to use for this png's data.
	 *
	 * @param filterMethod
	 * The image data filtering method to use for this PNG.
	 *
	 * @param interlaceMethod
	 * The interlace method to use for this PNG.
	 */
	public HeaderChunk(int width, int height, int depth, ColorMode mode, CompressionEngine cm, FilterMethod fm, InterlaceMethod im) {
		super(Chunk.IHDR);
		setProperties(width, height, depth, mode, cm, fm, im);
		setChunkData(createDataBytes());
	}
	
	/**
	 * Gets the width of this PNG.
	 *
	 * @return
	 * The width.
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Gets the height of this PNG.
	 *
	 * @return
	 * The height.
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Gets the sample depth of this PNG.
	 *
	 * @return
	 * The sample depth.
	 */
	public int getSampleDepth() {
		return sampleDepth;
	}
	
	/**
	 * Gets the color type of this PNG.
	 *
	 * @return
	 * The color type.
	 */
	public ColorMode getColorMode() {
		return mode;
	}
	
	/**
	 * Gets the compression method of this PNG.
	 *
	 * @return
	 * The compression method.
	 */
	public CompressionEngine getCompressionEngine() {
		return compressionEngine;
	}
	
	/**
	 * Gets the filter method of this PNG.
	 *
	 * @return
	 * The filter method.
	 */
	public FilterMethod getFilterMethod() {
		return filterMethod;
	}
	
	/**
	 * Gets the interlace method of this PNG.
	 *
	 * @return
	 * The interlace method.
	 */
	public InterlaceMethod getInterlaceMethod() {
		return interlaceMethod;
	}
	
	/**
	 * Parses chunk data for header information.
	 */
	private void parseData() {
		int w							= parser.parseInt();
		int h							= parser.parseInt();
		int depth						= parser.parseInt(1);
		ColorMode mode					= ColorMode.fromData(parser.parseInt(1));
		CompressionEngine compression	= CompressionEngine.fromData(parser.parseInt(1));
		FilterMethod filter				= FilterMethod.fromData(parser.parseInt(1));
		InterlaceMethod interlace		= InterlaceMethod.fromData(parser.parseInt(1));
		setProperties(w, h, depth, mode, compression, filter, interlace);
	}
	
	/**
	 * Sets the internal properties for this chunk.
	 *
	 * @param width
	 * The width of this PNG.
	 *
	 * @param height
	 * The height of this PNG.
	 *
	 * @param depth
	 * The sample depth of this PNG.
	 *
	 * @param colorType
	 * The color type of this PNG.
	 *
	 * @param compressionMethod
	 * The compression method to use for this png's data.
	 *
	 * @param filterMethod
	 * The image data filtering method to use for this PNG.
	 *
	 * @param interlaceMethod
	 * The interlace method to use for this PNG.
	 */
	private void setProperties(int width, int height, int depth, ColorMode mode, CompressionEngine cm, FilterMethod fm, InterlaceMethod im) {
		this.width = width;
		this.height = height;
		this.sampleDepth = depth;
		this.mode = mode;
		this.compressionEngine = cm;
		this.filterMethod = fm;
		this.interlaceMethod = im;
	}
	
	/**
	 * Creates the data byte array for this chunk.
	 *
	 * @return
	 * The data byte array.
	 */
	private byte[] createDataBytes() {
		ByteComposer composer = new ByteComposer(13);
		composer.composeInt(width);
		composer.composeInt(height);
		composer.composeInt(sampleDepth, 1);
		composer.composeInt(mode.dataValue(), 1);
		composer.composeInt(compressionEngine.dataValue(), 1);
		composer.composeInt(filterMethod.dataValue(), 1);
		composer.composeInt(interlaceMethod.dataValue(), 1);
		return composer.toArray();
	}
	
}