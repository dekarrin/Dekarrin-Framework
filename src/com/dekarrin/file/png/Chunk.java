package com.dekarrin.file.png;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

/**
 * Represents a chunk from a png file.
 */
public class Chunk {
	
	/**
	 * The rendering intent of perceptual.
	 */
	public static final byte RENDERING_INTENT_PERCEPTUAL = 0;
	
	/**
	 * The rendering intent of relative colorimetric.
	 */
	public static final byte RENDERING_INTENT_RELATIVE_COLORIMETRIC = 1;
	
	/**
	 * The rendering intent of saturation.
	 */
	public static final byte RENDERING_INTENT_SATURATION = 2;
	
	/**
	 * The rendering intent of absolute colorimetric.
	 */
	public static final byte RENDERING_INTENT_ABSOLUTE_COLORIMETRIC = 3;
	 
	/**
	 * The compression method value for deflate/inflate.
	 */
	public static final byte COMPRESSION_METHOD_ZLIB = 0;
	
	/**
	 * Filter method for adaptive filtering with five basic filter
	 * types.
	 */
	public static final byte FILTER_METHOD_ADAPTIVE = 0;
	
	/**
	 * Interlace method for no interlacing.
	 */
	public static final byte INTERLACE_METHOD_NONE = 0;
	
	/**
	 * Interlace method for Adam7 interlacing.
	 */
	public static final byte INTERLACE_METHOD_ADAM7 = 1;
	
	/**
	 * Color type for no colors.
	 */
	public static final byte COLOR_TYPE_GRAYSCALE = 0;
	
	/**
	 * Color type for color used.
	 */
	public static final byte COLOR_TYPE_COLOR = 2;
	
	/**
	 * Color type for using both color and a palette.
	 */
	public static final byte COLOR_TYPE_COLOR_PALETTE = 3;
	
	/**
	 * Color type for using only an alpha channel.
	 */
	public static final byte COLOR_TYPE_GRAYSCALE_ALPHA = 4;
	
	/**
	 * Color type for using both an alpha channel and color.
	 */
	public static final byte COLOR_TYPE_COLOR_ALPHA = 6;
	
	/**
	 * Used for parsing primitives from chunkData.
	 */
	private ByteBuffer dataBuffer;
	
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
		dataBuffer = ByteBuffer.wrap(chunkData);
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
	public Chunk(byte[] type, byte[] data, int crc) {
		this.crc = (long)crc;
		chunkData = data;
		chunkType = type;
		//TODO: check validity of chunk here with generateCrc().
		dataBuffer = ByteBuffer.wrap(chunkData);
	}
	
	/**
	 * Gets the type of this chunk as a four-character String.
	 *
	 * @returns
	 * The name.
	 */
	public String getTypeName() {
		return new String(chunkType, 0, 4);
	}
	
	/**
	 * Gets the type of this chunk.
	 *
	 * @returns
	 * The type.
	 */
	public byte[] getType() {
		return chunkType;
	}
	
	/**
	 * Gets the length of this chunk.
	 *
	 * @returns
	 * The length.
	 */
	public int getLength() {
		return chunkData.length;
	}
	
	/**
	 * Gets the data of this chunk.
	 *
	 * @returns
	 * The data.
	 */
	public byte[] getData() {
		return chunkData;
	}
	
	/**
	 * Gets the crc of this chunk.
	 *
	 * @returns
	 * The CRC.
	 */
	public long getCrc() {
		return crc;
	}
	
	/**
	 * Checks if this chunk is ancillary.
	 *
	 * @returns
	 * Whether it is.
	 */
	public boolean isAncillary() {
		return (chunkType[0] & 32 == 32);
	}
	
	/**
	 * Checks if this chunk is private.
	 *
	 * @returns
	 * Whether it is.
	 */
	public boolean isPrivate() {
		return (chunkType[1] & 32 == 32);
	}
	
	/**
	 * Checks if this chunk is safe to copy.
	 *
	 * @returns
	 * Whether it is.
	 */
	public boolean isSafeToCopy() {
		return (chunkType[3] & 32 == 32);
	}
	
	/**
	 * Gets the next byte from the chunk data.
	 *
	 * @returns
	 * The next byte.
	 */
	protected byte parseByte() {
		return dataBuffer.get();
	}
	
	/**
	 * Gets multiple next bytes from the chunk data. After the
	 * supplied array is filled, the position is remembered. This
	 * is equivilent to calling parseByte() multiple times.
	 *
	 * @param byteArray
	 * The array to fill with values.
	 */
	protected void parseBytes(byte[] byteArray) {
		dataBuffer.get(byteArray);
	}
	
	/**
	 * Gets the rest of the chunkData.
	 *
	 * @returns
	 * The rest of the chunkData.
	 */
	protected byte[] parseFinalBytes() {
		int remainingLength = dataBuffer.capacity() - dataBuffer.position();
		byte[] remainingBytes = new byte[remainingLength];
		parseBytes(remainingBytes);
		return remainingBytes;
	}
	
	/**
	 * Gets the next int from the chunk data. Reads 4 bytes and
	 * converts them into an int.
	 *
	 * @returns
	 * The next int.
	 */
	protected int parseInt() {
		return dataBuffer.getInt();
	}
	
	/**
	 * Gets multiple next ints from the chunk data. After the
	 * supplied array is filled, the position is remembered. This
	 * is equivilent to calling parseInt() multiple times.
	 *
	 * @param intArray
	 * The array to fill with values.
	 */
	protected void parseInts(int[] intArray) {
		for(int i = 0; i < intArray.length; i++) {
			intArray[i] = parseInt();
		}
	}
	
	/**
	 * Parses the chunk data until a null seperater is encountered.
	 *
	 * @param
	 * A string from the chunkData.
	 */
	protected String parseString() {
		StringBuffer word = new StringBuffer();
		byte charByte;
		while((charByte = parseByte()) != 0) {
			word.append(byteToString(charByte));
		}
		String parsed = word.toString();
		return parsed;
	}
	
	/**
	 * Gets the rest of the chunkData as a string.
	 *
	 * @returns
	 * The rest of the chunkData.
	 */
	protected String parseFinalString() {
		byte[] remainingBytes = parseFinalBytes();
		String parsed = byteToString(remainingBytes);
		return parsed;
	}
	
	/**
	 * Converts a single byte into a string.
	 *
	 * @param subject
	 * The byte to convert.
	 *
	 * @returns
	 * The string.
	 */
	protected String byteToString(byte subject) {
		return byteToString(new byte[]{subject});
	}
	
	/**
	 * Converts an entire array into a string.
	 *
	 * @param subject
	 * An array of bytes to convert.
	 *
	 * @returns
	 * The resulting string.
	 */
	protected String byteToString(byte[] subject) {
		return new String(subject);
	}
	
	/**
	 * Generates CRC code for this chunk.
	 *
	 * @returns
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