package com.dekarrin.file.png;

/**
 * The header chunk from a png file.
 */
public class HeaderChunk extends Chunk implements CriticalChunk {
	
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
	private byte bitDepth;
	
	/**
	 * Interpretation of image data.
	 *
	 * 0 - Each pixel is a grayscale sample
	 * 2 - Each pixel is an RGB triple
	 * 3 - Each pixel is a palette index
	 * 4 - Each pixel is a grayscale sample followed by alpha
	 * 6 - Each pixel is an RGB triple followed by alpha
	 */
	private byte colorType;
	
	/**
	 * Compression method. Currently, only deflate/inflate is
	 * defined. If this is not 0, it should result in an error.
	 */
	private byte compressionMethod;
	
	/**
	 * Filter method. Only 0 is defined, and an error should
	 * result from any other value.
	 */
	private byte filterMethod;
	
	/**
	 * Interlace method. Typically either 0 (none) or 1
	 * (Adam7).
	 */
	private byte interlaceMethod;
	
	/**
	 * Creates a new HeaderChunk. The supplied data is parsed.
	 *
	 * @param data
	 * The raw data in this chunk.
	 *
	 * @param crc
	 * The CRC for this chunk.
	 */
	public HeaderChunk(byte[] data, int crc) {
		super(new byte[]{49, 48, 44, 52}, data, crc); // IHED
		parseData();
	}
	
	/**
	 * Gets the width of this png.
	 *
	 * @returns
	 * The width.
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Gets the height of this png.
	 *
	 * @returns
	 * The height.
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Gets the bit depth of this png.
	 *
	 * @returns
	 * The bit depth.
	 */
	public byte getBitDepth() {
		return bitDepth;
	}
	
	/**
	 * Gets the color type of this png.
	 *
	 * @returns
	 * The color type.
	 */
	public byte getColorType() {
		return colorType;
	}
	
	/**
	 * Gets the compression method of this png.
	 *
	 * @returns
	 * The compression method.
	 */
	public byte getCompressionMethod() {
		return compressionMethod;
	}
	
	/**
	 * Gets the filter method of this png.
	 *
	 * @returns
	 * The filter method.
	 */
	public byte getFilterMethod() {
		return filterMethod;
	}
	
	/**
	 * Gets the interlace method of this png.
	 *
	 * @returns
	 * The interlace method.
	 */
	public byte getInterlaceMethod() {
		return interlaceMethod;
	}
	
	/**
	 * Parses chunk data for header information.
	 */
	private void parseData() {
		width				= parseInt();
		height				= parseInt();
		bitDepth			= parseByte();
		colorType			= parseByte();
		compressionMethod	= parseByte();
		filterMethod		= parseByte();
		interlaceMethod		= parseByte();
	}
	
}