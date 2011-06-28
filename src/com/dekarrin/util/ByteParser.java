package com.dekarrin.util;

import java.nio.ByteBuffer;

/**
 * A wrapper for the ByteBuffer class that serves as a more
 * convenient way of easily parsing bytes. This class wraps
 * around an array of bytes and adds the capabilities of
 * reading the rest of the buffer.
 */
public class ByteParser {
	
	/**
	 * The ByteBuffer responsible for parsing the data.
	 */
	private ByteBuffer dataBuffer;
	
	/**
	 * Creates a new ByteParser for an array of bytes.
	 *
	 * @param dataArray
	 * The array to wrap around.
	 */
	public ByteParser(byte[] dataArray) {
		dataBuffer = ByteBuffer.wrap(dataArray);
	}
	
	/**
	 * Gets the next byte from the data.
	 *
	 * @returns
	 * The next byte.
	 */
	public byte parseByte() {
		return dataBuffer.get();
	}
	
	/**
	 * Gets multiple next bytes from the data. After the
	 * supplied array is filled, the position is remembered. This
	 * is equivilent to calling parseByte() multiple times.
	 *
	 * @param byteArray
	 * The array to fill with values.
	 */
	public void parseBytes(byte[] byteArray) {
		dataBuffer.get(byteArray);
	}
	
	/**
	 * Gets the rest of the data.
	 *
	 * @returns
	 * The rest of the data.
	 */
	public byte[] parseFinalBytes() {
		byte[] remainingBytes = new byte[remainingLength()];
		parseBytes(remainingBytes);
		return remainingBytes;
	}
	
	/**
	 * Gets the next short from the data.
	 *
	 * @returns
	 * The next short.
	 */
	public short parseShort() {
		return dataBuffer.getShort();
	}
	
	/**
	 * Gets multiple next shorts from the data. After the
	 * supplied array is filled, the position is remembered. This
	 * is equivilent to calling parseShort() multiple times.
	 *
	 * @param shortArray
	 * The array to fill with values.
	 */
	public void parseShorts(short[] shortArray) {
		for(int i = 0; i < shortArray.length; i++) {
			shortArray[i] = parseShort();
		}
	}
	
	/**
	 * Gets the rest of the data as shorts.
	 *
	 * @returns
	 * The rest of the data.
	 */
	public short[] parseFinalShorts() {
		short[] remainingShorts = new short[remainingLength()];
		parseShorts(remainingShorts);
		return remainingShorts;
	}
	
	/**
	 * Gets the next int from the data. Reads 4 bytes and
	 * converts them into an int.
	 *
	 * @returns
	 * The next int.
	 */
	public int parseInt() {
		return dataBuffer.getInt();
	}
	
	/**
	 * Gets the next int of a specific size from the data.
	 *
	 * @param width
	 * The width in bytes of the int to be read. This must be
	 * one of the supported widths: 1, 2, or 4.
	 *
	 * @returns
	 * The next int.
	 */
	public int parseInt(int width) {
		int parsedInt = 0;
		switch(width) {
			case 1:
				parsedInt = (int)((int)dataBuffer.get() & 0xff);
				break;
				
			case 2:
				parsedInt = (int)((int)dataBuffer.getShort() & 0xffff);
				break;
				
			case 4:
				parsedInt = dataBuffer.getInt();
				break;
				
			default:
				throw new IllegalArgumentException("invalid integer width");
		}
		return parsedInt;
	}
	
	/**
	 * Gets multiple next ints from the data. After the
	 * supplied array is filled, the position is remembered. This
	 * is equivilent to calling parseInt() multiple times.
	 *
	 * @param intArray
	 * The array to fill with values.
	 */
	public void parseInts(int[] intArray) {
		for(int i = 0; i < intArray.length; i++) {
			intArray[i] = parseInt();
		}
	}
	
	/**
	 * Gets multiple next ints from the data. After the
	 * supplied array is filled, the position is remembered. This
	 * is equivilent to calling parseInt() multiple times.
	 *
	 * @param width
	 * The width in bytes of the int to be read. This must be
	 * one of the supported widths: 1, 2, or 4.
	 *
	 * @param intArray
	 * The array to fill with values.
	 */
	public void parseInts(int[] intArray, int width) {
		for(int i = 0; i < intArray.length; i++) {
			intArray[i] = parseInt(width);
		}
	}
	
	/**
	 * Gets the rest of the data as ints.
	 *
	 * @returns
	 * The rest of the data.
	 */
	public int[] parseFinalInts() {
		int[] remainingInts = new int[remainingLength()];
		parseInts(remainingInts);
		return remainingInts;
	}
	
	/**
	 * Gets the rest of the data as ints.
	 *
	 * @param width
	 * The width in bytes of the int to be read. This must be
	 * one of the supported widths: 1, 2, or 4.
	 *
	 * @returns
	 * The rest of the data.
	 */
	public int[] parseFinalInts(int width) {
		int[] remainingInts = new int[remainingLength()];
		parseInts(remainingInts, width);
		return remainingInts;
	}
	
	/**
	 * Gets the next long from the data.
	 *
	 * @returns
	 * The next long.
	 */
	public long parseLong() {
		return dataBuffer.getLong();
	}
	
	/**
	 * Gets multiple next longs from the data. After the
	 * supplied array is filled, the position is remembered. This
	 * is equivilent to calling parseLong() multiple times.
	 *
	 * @param longArray
	 * The array to fill with values.
	 */
	public void parseLongs(long[] longArray) {
		for(int i = 0; i < longArray.length; i++) {
			longArray[i] = parseLong();
		}
	}
	
	/**
	 * Gets the rest of the data as longs.
	 *
	 * @returns
	 * The rest of the data.
	 */
	public long[] parseFinalLongs() {
		long[] remainingLongs = new long[remainingLength()];
		parseLongs(remainingLongs);
		return remainingLongs;
	}
	
	/**
	 * Gets the next float from the data.
	 *
	 * @returns
	 * The next float.
	 */
	public float parseFloat() {
		return dataBuffer.getFloat();
	}
	
	/**
	 * Gets multiple next floats from the data. After the
	 * supplied array is filled, the position is remembered. This
	 * is equivilent to calling parseFloat() multiple times.
	 *
	 * @param floatArray
	 * The array to fill with values.
	 */
	public void parseFloats(float[] floatArray) {
		for(int i = 0; i < floatArray.length; i++) {
			floatArray[i] = parseFloat();
		}
	}
	
	/**
	 * Gets the rest of the data as floats.
	 *
	 * @returns
	 * The rest of the data.
	 */
	public float[] parseFinalFloats() {
		float[] remainingFloats = new float[remainingLength()];
		parseFloats(remainingFloats);
		return remainingFloats;
	}
	
	/**
	 * Gets the next double from the data.
	 *
	 * @returns
	 * The next double.
	 */
	public double parseDouble() {
		return dataBuffer.getDouble();
	}
	
	/**
	 * Gets multiple next doubles from the data. After the
	 * supplied array is filled, the position is remembered. This
	 * is equivilent to calling parseDouble() multiple times.
	 *
	 * @param doubleArray
	 * The array to fill with values.
	 */
	public void parseDoubles(double[] doubleArray) {
		for(int i = 0; i < doubleArray.length; i++) {
			doubleArray[i] = parseDouble();
		}
	}
	
	/**
	 * Gets the rest of the data as doubles.
	 *
	 * @returns
	 * The rest of the data.
	 */
	public double[] parseFinalDoubles() {
		double[] remainingDoubles = new double[remainingLength()];
		parseDoubles(remainingDoubles);
		return remainingDoubles;
	}
	
	/**
	 * Gets the next boolean from the data.
	 *
	 * @returns
	 * The next boolean.
	 */
	public boolean parseBoolean() {
		byte next = dataBuffer.get();
		boolean boolValue = !(next == 0);
		return boolValue;
	}
	
	/**
	 * Gets multiple next booleans from the data. After the
	 * supplied array is filled, the position is remembered. This
	 * is equivilent to calling parseBoolean() multiple times.
	 *
	 * @param booleanArray
	 * The array to fill with values.
	 */
	public void parseBooleans(boolean[] booleanArray) {
		for(int i = 0; i < booleanArray.length; i++) {
			booleanArray[i] = parseBoolean();
		}
	}
	
	/**
	 * Gets the rest of the data as booleans.
	 *
	 * @returns
	 * The rest of the data.
	 */
	public boolean[] parseFinalBooleans() {
		boolean[] remainingBooleans = new boolean[remainingLength()];
		parseBooleans(remainingBooleans);
		return remainingBooleans;
	}
	
	/**
	 * Gets the next char from the data.
	 *
	 * @returns
	 * The next char.
	 */
	public char parseChar() {
		return dataBuffer.getChar();
	}
	
	/**
	 * Gets multiple next chars from the data. After the
	 * supplied array is filled, the position is remembered. This
	 * is equivilent to calling parseChar() multiple times.
	 *
	 * @param charArray
	 * The array to fill with values.
	 */
	public void parseChars(char[] charArray) {
		for(int i = 0; i < charArray.length; i++) {
			charArray[i] = parseChar();
		}
	}
	
	/**
	 * Gets the rest of the data as chars.
	 *
	 * @returns
	 * The rest of the data.
	 */
	public char[] parseFinalChars() {
		char[] remainingChars = new char[remainingLength()];
		parseChars(remainingChars);
		return remainingChars;
	}
	
	
	
	/**
	 * Parses the data into a String. The data is parsed until a
	 * null seperater is encountered.
	 *
	 * @param
	 * A String from the data.
	 */
	public String parseString() {
		StringBuffer word = new StringBuffer();
		byte charByte;
		while((charByte = parseByte()) != 0) {
			word.append(byteToString(charByte));
		}
		String parsed = word.toString();
		return parsed;
	}
	
	/**
	 * Gets multiple next Strings from the data. After the
	 * supplied array is filled, the position is remembered. This
	 * is equivilent to calling parseString() multiple times.
	 *
	 * @param stringArray
	 * The array to fill with values.
	 */
	public void parseStrings(String[] stringArray) {
		for(int i = 0; i < stringArray.length; i++) {
			stringArray[i] = parseString();
		}
	}
	
	/**
	 * Gets the rest of the data as one String.
	 *
	 * @returns
	 * The rest of the data.
	 */
	public String parseFinalString() {
		byte[] remainingBytes = parseFinalBytes();
		String parsed = byteToString(remainingBytes);
		return parsed;
	}
	
	/**
	 * Gets the rest of the data as several Strings. This method
	 * is similar to parseFinalString(), but it takes into
	 * consideration the fact that there may be more than one
	 * String at the end divided by null separators.
	 *
	 * @returns
	 * The rest of the data.
	 */
	public String[] parseFinalStrings() {
		byte[] remainingBytes = parseFinalBytes();
		HelperString[] finalStrings = HelperString.createFromBytes(remainingBytes);
		String[] outputStrings = new String[finalStrings.length];
		for(int i = 0; i < finalStrings.length; i++) {
			outputStrings[i] = finalStrings[i].toString();
		}
		return outputStrings;
	}
	
	/**
	 * Gets the number of bytes remaining in the buffer.
	 *
	 * @returns
	 * The number of bytes left.
	 */
	public int remaining() {
		return remainingLength();
	}
	
	/**
	 * Gets the length in bytes of the data not yet parsed.
	 *
	 * @returns
	 * The length.
	 */
	private int remainingLength() {
		int length = dataBuffer.capacity() - dataBuffer.position();
		return length;
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
	private String byteToString(byte subject) {
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
	private String byteToString(byte[] subject) {
		return new String(subject);
	}
}