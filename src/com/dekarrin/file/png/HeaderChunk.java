package com.dekarrin.file.png;

import com.dekarrin.util.ByteComposer;

/**
 * The header chunk from a png file.
 */
public class HeaderChunk extends CriticalChunk {
	
	/**
	 * The width of the image.
	 */
	private int width;
	
	/**
	 * The height of the image.
	 */
	private int height;
	
	/**
	 * The bit depth of the image.
	 */
	private int bitDepth;
	
	/**
	 * Interpretation of image data.
	 *
	 * 0 - Each pixel is a grayscale sample
	 * 2 - Each pixel is an RGB triple
	 * 3 - Each pixel is a palette index
	 * 4 - Each pixel is a grayscale sample followed by alpha
	 * 6 - Each pixel is an RGB triple followed by alpha
	 */
	private int colorType;
	
	/**
	 * Compression method. Currently, only deflate/inflate is
	 * defined. If this is not 0, it should result in an error.
	 */
	private int compressionMethod;
	
	/**
	 * Filter method. Only 0 is defined, and an error should
	 * result from any other value.
	 */
	private int filterMethod;
	
	/**
	 * Interlace method. Typically either 0 (none) or 1
	 * (Adam7).
	 */
	private int interlaceMethod;
	
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
	 * The width of this png.
	 *
	 * @param height
	 * The height of this png.
	 *
	 * @param bitDepth
	 * The bit depth of this png.
	 *
	 * @param colorType
	 * The color type of this png.
	 *
	 * @param compressionMethod
	 * The compression method to use for this png's data.
	 *
	 * @param filterMethod
	 * The image data filtering method to use for this png.
	 *
	 * @param interlaceMethod
	 * The interlace method to use for this png.
	 */
	public HeaderChunk(int width, int height, int bitDepth, int colorType, int compressionMethod, int filterMethod, int interlaceMethod) {
		super(Chunk.IHDR);
		setProperties(width, height, bitDepth, colorType, compressionMethod, filterMethod, interlaceMethod);
		setChunkData(createDataBytes());
	}
	
	/**
	 * Gets the width of this png.
	 *
	 * @return
	 * The width.
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Gets the height of this png.
	 *
	 * @return
	 * The height.
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Gets the bit depth of this png.
	 *
	 * @return
	 * The bit depth.
	 */
	public int getBitDepth() {
		return bitDepth;
	}
	
	/**
	 * Gets the color type of this png.
	 *
	 * @return
	 * The color type.
	 */
	public int getColorType() {
		return colorType;
	}
	
	/**
	 * Gets the compression method of this png.
	 *
	 * @return
	 * The compression method.
	 */
	public int getCompressionMethod() {
		return compressionMethod;
	}
	
	/**
	 * Gets the filter method of this png.
	 *
	 * @return
	 * The filter method.
	 */
	public int getFilterMethod() {
		return filterMethod;
	}
	
	/**
	 * Gets the interlace method of this png.
	 *
	 * @return
	 * The interlace method.
	 */
	public int getInterlaceMethod() {
		return interlaceMethod;
	}
	
	/**
	 * Parses chunk data for header information.
	 */
	private void parseData() {
		int w			= parser.parseInt();
		int h			= parser.parseInt();
		int depth		= parser.parseInt(1);
		int mode		= parser.parseInt(1);
		int compression	= parser.parseInt(1);
		int filter		= parser.parseInt(1);
		int interlace	= parser.parseInt(1);
		setProperties(w, h, depth, mode, compression, filter, interlace);
	}
	
	/**
	 * Sets the internal properties for this chunk.
	 *
	 * @param width
	 * The width of this png.
	 *
	 * @param height
	 * The height of this png.
	 *
	 * @param bitDepth
	 * The bit depth of this png.
	 *
	 * @param colorType
	 * The color type of this png.
	 *
	 * @param compressionMethod
	 * The compression method to use for this png's data.
	 *
	 * @param filterMethod
	 * The image data filtering method to use for this png.
	 *
	 * @param interlaceMethod
	 * The interlace method to use for this png.
	 */
	private void setProperties(int width, int height, int bitDepth, int colorType, int compressionMethod, int filterMethod, int interlaceMethod) {
		this.width = width;
		this.height = height;
		this.bitDepth = bitDepth;
		this.colorType = colorType;
		this.compressionMethod = compressionMethod;
		this.filterMethod = filterMethod;
		this.interlaceMethod = interlaceMethod;
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
		composer.composeInt(bitDepth, 1);
		composer.composeInt(colorType, 1);
		composer.composeInt(compressionMethod, 1);
		composer.composeInt(filterMethod, 1);
		composer.composeInt(interlaceMethod, 1);
		return composer.toArray();
	}
	
}