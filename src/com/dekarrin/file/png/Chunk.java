package com.dekarrin.file.png;

import com.dekarrin.util.ByteParser;
import java.util.zip.CRC32;
import java.util.HashMap;

/**
 * Represents a chunk from a png file.
 */
public class Chunk {
	
	/**
	 * Unit specifier for unknown.
	 */
	public static final int UNKNOWN_UNIT = 0;
	
	/**
	 * Unit specifier for the meter.
	 */
	public static final int METER_UNIT = 1;
	
	/**
	 * The background mode for indexed colors.
	 */
	public static final int INDEXED_COLOR_MODE = 0;
	
	/**
	 * The background mode for grayscale.
	 */
	public static final int GRAYSCALE_MODE = 1;
	
	/**
	 * The background mode for truecolor.
	 */
	public static final int TRUECOLOR_MODE = 2;
	
	/**
	 * The rendering intent of perceptual.
	 */
	public static final int RENDERING_INTENT_PERCEPTUAL = 0;
	
	/**
	 * The rendering intent of relative colorimetric.
	 */
	public static final int RENDERING_INTENT_RELATIVE_COLORIMETRIC = 1;
	
	/**
	 * The rendering intent of saturation.
	 */
	public static final int RENDERING_INTENT_SATURATION = 2;
	
	/**
	 * The rendering intent of absolute colorimetric.
	 */
	public static final int RENDERING_INTENT_ABSOLUTE_COLORIMETRIC = 3;
	 
	/**
	 * The compression method value for deflate/inflate.
	 */
	public static final int COMPRESSION_METHOD_ZLIB = 0;
	
	/**
	 * Filter method for adaptive filtering with five basic filter
	 * types.
	 */
	public static final int FILTER_METHOD_ADAPTIVE = 0;
	
	/**
	 * Interlace method for no interlacing.
	 */
	public static final int INTERLACE_METHOD_NONE = 0;
	
	/**
	 * Interlace method for Adam7 interlacing.
	 */
	public static final int INTERLACE_METHOD_ADAM7 = 1;
	
	/**
	 * Color type for no colors.
	 */
	public static final int COLOR_TYPE_GRAYSCALE = 0;
	
	/**
	 * Color type for color used.
	 */
	public static final int COLOR_TYPE_COLOR = 2;
	
	/**
	 * Color type for using both color and a palette.
	 */
	public static final int COLOR_TYPE_COLOR_PALETTE = 3;
	
	/**
	 * Color type for using only an alpha channel.
	 */
	public static final int COLOR_TYPE_GRAYSCALE_ALPHA = 4;
	
	/**
	 * Color type for using both an alpha channel and color.
	 */
	public static final int COLOR_TYPE_COLOR_ALPHA = 6;
	
	/**
	 * Used for parsing primitives from chunkData.
	 */
	protected ByteParser parser;
	
	/**
	 * The type of this chunk.
	 */
	private byte[] chunkType;
	
	/**
	 * The data bytes appropriate to the chunk type. This may be null.
	 */
	protected byte[] chunkData;
	
	/**
	 * Cyclic redundancy check. This does not include the the length
	 * field.
	 */
	protected long crc;
	
	/**
	 * Creates a new empty Chunk.
	 */
	public Chunk() {
		super();
		parser = new ByteParser(chunkData);
	}
	
	/**
	 * Creates a new Chunk.
	 *
	 * @param type
	 * The type of the chunk. This is made of four letters.
	 *
	 * @param data
	 * The data in the chunk.
	 *
	 * @param crc
	 * The read CRC for this chunk. This may not be the actual
	 * CRC; if it isn't, this chunk is considered corrupted.
	 */
	public Chunk(byte[] type, byte[] data, long crc) {
		this.crc = crc;
		chunkData = data;
		chunkType = type;
		//TODO: check validity of chunk here with generateCrc().
		parser = new ByteParser(chunkData);
	}
	
	/**
	 * Gets the type of this chunk as a four-character String.
	 *
	 * @return
	 * The name.
	 */
	public String getTypeName() {
		return new String(chunkType, 0, 4);
	}
	
	/**
	 * Gets the type of this chunk.
	 *
	 * @return
	 * The type.
	 */
	public byte[] getType() {
		return chunkType;
	}
	
	/**
	 * Gets the length of this chunk.
	 *
	 * @return
	 * The length.
	 */
	public int getLength() {
		return chunkData.length;
	}
	
	/**
	 * Gets the data of this chunk.
	 *
	 * @return
	 * The data.
	 */
	public byte[] getData() {
		return chunkData;
	}
	
	/**
	 * Gets the crc of this chunk.
	 *
	 * @return
	 * The CRC.
	 */
	public long getCrc() {
		return crc;
	}
	
	/**
	 * Checks if this chunk is ancillary.
	 *
	 * @return
	 * Whether it is.
	 */
	public boolean isAncillary() {
		return ((chunkType[0] & 32) == 32);
	}
	
	/**
	 * Checks if this chunk is private.
	 *
	 * @return
	 * Whether it is.
	 */
	public boolean isPrivate() {
		return ((chunkType[1] & 32) == 32);
	}
	
	/**
	 * Checks if this chunk is safe to copy.
	 *
	 * @return
	 * Whether it is.
	 */
	public boolean isSafeToCopy() {
		return ((chunkType[3] & 32) == 32);
	}
		
	/**
	 * Generates CRC code for this chunk.
	 *
	 * @return
	 * The generated CRC.
	 */
	private long generateCrc() {
		CRC32 checker = new CRC32();
		checker.update(chunkType);
		checker.update(chunkData);
		long crc = checker.getValue();
		return crc;
	}
}