package com.dekarrin.util;

import java.nio.ByteBuffer;

/**
 * A wrapper for the ByteBuffer class that serves as a more
 * convenient way of easily parsing bytes. This class wraps
 * around an array of bytes and adds the capabilities of
 * reading the rest of the buffer. All values are read as
 * big-endian values from the wrapped array.
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
	 * @return
	 * The next byte.
	 */
	public byte parseByte() {
		return dataBuffer.get();
	}
	
	/**
	 * Gets multiple next bytes from the data. After the
	 * bytes are obtained, the position is remembered. This
	 * is equivilent to calling parseByte() multiple times.
	 *
	 * @param count
	 * The number of bytes to get.
	 *
	 * @return
	 * An array of the specified number of bytes.
	 */
	public byte[] parseBytes(int count) {
		byte[] byteArray = new byte[count];
		dataBuffer.get(byteArray);
		return byteArray;
	}
	
	/**
	 * Gets the rest of the data.
	 *
	 * @return
	 * The rest of the data.
	 */
	public byte[] parseRemainingBytes() {
		byte[] remainingBytes = parseBytes(remaining());
		return remainingBytes;
	}
	
	/**
	 * Gets the next short from the data.
	 *
	 * @return
	 * The next short.
	 */
	public short parseShort() {
		return dataBuffer.getShort();
	}
	
	/**
	 * Gets multiple next shorts from the data. After the
	 * shorts are obtained, the position is remembered. This
	 * is equivilent to calling parseShort() multiple times.
	 *
	 * @param count
	 * The number of shorts to get.
	 *
	 * @return
	 * An array of the specified number of shorts.
	 */
	public short[] parseShorts(int count) {
		short shortArray = new short[count];
		for(int i = 0; i < count; i++) {
			shortArray[i] = parseShort();
		}
		return shortArray;
	}
	
	/**
	 * Gets the rest of the data as shorts.
	 *
	 * @return
	 * The rest of the data.
	 */
	public short[] parseRemainingShorts() {
		short[] remainingShorts = parseShorts(remainingShorts());
		return remainingShorts;
	}
	
	/**
	 * Gets the next int from the data. Reads 4 bytes and
	 * converts them into an int.
	 *
	 * @return
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
	 * no bigger than the width of a native integer, 4.
	 *
	 * @return
	 * The next int.
	 */
	public int parseInt(int width) {
		byte[] holder = parseBytes(width);
		parsedInt = arrayToInt(holder);
		return parsedInt;
	}
	
	/**
	 * Gets multiple next ints from the data. After the
	 * ints are obtained, the position is remembered. This
	 * is equivilent to calling parseInt() multiple times.
	 *
	 * @param count
	 * The number of ints to get.
	 *
	 * @return
	 * An array of the specifed number of ints.
	 */
	public int[] parseInts(int count) {
		int[] intArray = new int[count];
		for(int i = 0; i < count; i++) {
			intArray[i] = parseInt();
		}
		return intArray;
	}
	
	/**
	 * Gets multiple next ints from the data. After the
	 * ints are obtained, the position is remembered. This
	 * is equivilent to calling parseInt(int) multiple times.
	 *
	 * @param count
	 * The number of ints to get.
	 *
	 * @param width
	 * The width in bytes of the int to be read. This must be
	 * no bigger than the width of a native integer, 4.
	 *
	 * @return
	 * An array of the specified number of ints, read at the
	 * specified width.
	 */
	public int[] parseInts(int count, int width) {
		int[] intArray = new int[count];
		for(int i = 0; i < count; i++) {
			intArray[i] = parseInt(width);
		}
		return intArray;
	}
	
	/**
	 * Gets the rest of the data as ints.
	 *
	 * @return
	 * The rest of the data.
	 */
	public int[] parseRemainingInts() {
		int[] remainingInts = parseInts(remainingInts());
		return remainingInts;
	}
	
	/**
	 * Gets the rest of the data as ints.
	 *
	 * @param width
	 * The width in bytes of the int to be read. This must be
	 * no bigger than the width of a native integer, 4.
	 *
	 * @return
	 * The rest of the data.
	 */
	public int[] parseRemainingInts(int width) {
		int[] remainingInts = parseInts(remainingBlocks(width), width);
		return remainingInts;
	}
	
	/**
	 * Gets the next long from the data.
	 *
	 * @return
	 * The next long.
	 */
	public long parseLong() {
		return dataBuffer.getLong();
	}
	
	/**
	 * Gets multiple next longs from the data. After the
	 * longs are obtained, the position is remembered. This
	 * is equivilent to calling parseLong() multiple times.
	 *
	 * @param count
	 * How many longs to get.
	 *
	 * @return
	 * An array of the specified number of longs.
	 */
	public long[] parseLongs(int count) {
		long[] longArray = new long[count];
		for(int i = 0; i < count; i++) {
			longArray[i] = parseLong();
		}
		return longArray;
	}
	
	/**
	 * Gets the rest of the data as longs.
	 *
	 * @return
	 * The rest of the data.
	 */
	public long[] parseRemainingLongs() {
		long[] remainingLongs = parseLongs(remainingLongs());
		return remainingLongs;
	}
	
	/**
	 * Gets the next float from the data.
	 *
	 * @return
	 * The next float.
	 */
	public float parseFloat() {
		return dataBuffer.getFloat();
	}
	
	/**
	 * Gets multiple next floats from the data. After the
	 * floats are obtained, the position is remembered. This
	 * is equivilent to calling parseFloat() multiple times.
	 *
	 * @param count
	 * The number of floats to get.
	 *
	 * @return
	 * An array containing the specified number of floats.
	 */
	public float[] parseFloats(int count) {
		float[] floatArray = new float[count];
		for(int i = 0; i < count; i++) {
			floatArray[i] = parseFloat();
		}
		return floatArray;
	}
	
	/**
	 * Gets the rest of the data as floats.
	 *
	 * @return
	 * The rest of the data.
	 */
	public float[] parseRemainingFloats() {
		float[] remainingFloats = parseFloats(remainingFloats());
		return remainingFloats;
	}
	
	/**
	 * Gets the next double from the data.
	 *
	 * @return
	 * The next double.
	 */
	public double parseDouble() {
		return dataBuffer.getDouble();
	}
	
	/**
	 * Gets multiple next doubles from the data. After the
	 * doubles are obtained, the position is remembered. This
	 * is equivilent to calling parseDouble() multiple times.
	 *
	 * @param count
	 * The number of doubles to get.
	 *
	 * @return
	 * An array containing the specified number of doubles.
	 */
	public double[] parseDoubles(int count) {
		double[] doubleArray = new double[count];
		for(int i = 0; i < count; i++) {
			doubleArray[i] = parseDouble();
		}
		return doubleArray;
	}
	
	/**
	 * Gets the rest of the data as doubles.
	 *
	 * @return
	 * The rest of the data.
	 */
	public double[] parseRemainingDoubles() {
		double[] remainingDoubles = parseDoubles(remainingDoubles());
		return remainingDoubles;
	}
	
	/**
	 * Gets the next boolean from the data.
	 *
	 * @return
	 * The next boolean.
	 */
	public boolean parseBoolean() {
		byte next = dataBuffer.get();
		boolean boolValue = !(next == 0);
		return boolValue;
	}
	
	/**
	 * Gets multiple next booleans from the data. After the
	 * booleans are obtained, the position is remembered. This
	 * is equivilent to calling parseBoolean() multiple times.
	 *
	 * @param count
	 * The number of booleans to get.
	 *
	 * @return
	 * An array containing the specified number of booleans.
	 */
	public boolean[] parseBooleans(int count) {
		boolean[] booleanArray = new boolean[count];
		for(int i = 0; i < count; i++) {
			booleanArray[i] = parseBoolean();
		}
		return booleanArray;
	}
	
	/**
	 * Gets the rest of the data as booleans.
	 *
	 * @return
	 * The rest of the data.
	 */
	public boolean[] parseRemainingBooleans() {
		boolean[] remainingBooleans = parseBooleans(remaining());
		return remainingBooleans;
	}
	
	/**
	 * Gets the next char from the data.
	 *
	 * @return
	 * The next char.
	 */
	public char parseChar() {
		return dataBuffer.getChar();
	}
	
	/**
	 * Gets multiple next chars from the data. After the
	 * chars are obtained, the position is remembered. This
	 * is equivilent to calling parseChar() multiple times.
	 *
	 * @param count
	 * The number of chars to get.
	 *
	 * @return
	 * An array containing the specified number of chars.
	 */
	public char[] parseChars(int count) {
		char[] charArray = new char[count];
		for(int i = 0; i < count; i++) {
			charArray[i] = parseChar();
		}
		return charArray;
	}
	
	/**
	 * Gets the rest of the data as chars.
	 *
	 * @return
	 * The rest of the data.
	 */
	public char[] parseRemainingChars() {
		char[] remainingChars = parseChars(remainingChars());
		return remainingChars;
	}
	
	/**
	 * Parses the data into a String using a character width of
	 * 1 byte. The data is parsed until a null separator is
	 * encountered.
	 *
	 * @return
	 * A String from the data.
	 */
	public String parseString() {
		String parsedString = parseString(1);
		return parsedString;
	}
	
	/**
	 * Parses the data into a String. The data is parsed until
	 * a null separator is encountered.
	 *
	 * @param characterWidth
	 * The width of a character in bytes.
	 *
	 * @return
	 * A String from the data.
	 */
	public String parseString(int characterWidth) {
		StringBuffer buffer = new StringBuffer();
		byte[] characterBytes = new byte[characterWidth];
		String nextCharacter;
		while(!isNullSeparator(characterBytes = parseBytes(characterWidth))) {
			nextCharacter = bytesToString(characterBytes, characterWidth);
			buffer.append(nextCharacter);
		}
		String parsedString = buffer.toString();
		return parsedString;
	}
	
	/**
	 * Gets multiple next Strings from the data using a character
	 * width of 1 byte. After the Strings are obtained, the
	 * position is remembered. This is equivilent to calling
	 * parseString() multiple times.
	 *
	 * @param count
	 * The number of Strings to get.
	 *
	 * @return
	 * An array containing the specified number of Strings.
	 */
	public String[] parseStrings(int count) {
		return parseStrings(count, 1);
	}
	
	/**
	 * Gets multiple next Strings from the data. After the
	 * Strings are obtained, the position is remembered. This
	 * is equivilent to calling parseString() multiple times.
	 *
	 * @param count
	 * The number of Strings to get.
	 *
	 * @param characterWidth
	 * The width of each character in bytes.
	 *
	 * @return
	 * An array containing the specified number of Strings.
	 */
	public String[] parseStrings(int count, int characterWidth) {
		String[] stringArray = new String[count];
		for(int i = 0; i < count; i++) {
			stringArray[i] = parseString(characterWidth);
		}
		return stringArray;
	}
	
	/**
	 * Gets the rest of the data as one String using a character
	 * width of 1 byte.
	 *
	 * @param characterWidth
	 * The width of each character in bytes.
	 *
	 * @return
	 * The rest of the data.
	 */
	public String parseRemainingString() {
		return parseRemainingString(1);
	}
	
	/**
	 * Gets the rest of the data as one String.
	 *
	 * @param characterWidth
	 * The width of each character in bytes.
	 *
	 * @return
	 * The rest of the data.
	 */
	public String parseRemainingString(int characterWidth) {
		byte[] remainingBytes = parseRemainingBytes();
		String parsed = bytesToString(remainingBytes, characterWidth);
		return parsed;
	}
	
	/**
	 * Gets the rest of the data as several Strings using a
	 * character width of 1 byte. This method is similar to
	 * parseRemainingString(), but it takes into consideration the
	 * fact that there may be more than one String at the end
	 * divided by null separators.
	 *
	 * @return
	 * The rest of the data.
	 */
	public String[] parseRemainingStrings() {
		return parseRemainingStrings(1);
	}
	
	/**
	 * Gets the rest of the data as several Strings. This method
	 * is similar to parseRemainingString(), but it takes into
	 * consideration the fact that there may be more than one
	 * String at the end divided by null separators.
	 *
	 * @param characterWidth
	 * The width of each character in bytes.
	 *
	 * @return
	 * The rest of the data.
	 */
	public String[] parseRemainingStrings(int characterWidth) {
		byte[] characterBytes = new byte[characterWidth];
		Vector<String> results = new Vector<String>();
		StringBuffer buffer = new StringBuffer();
		String nextCharacter;
		while(remaining() > characterWidth) {
			characterBytes = parseBytes(characterWidth);
			if(isNullSeparator(nextCharacter) {
				results.add(buffer.toString());
				buffer = new StringBuffer();
			} else {
				nextCharacter = bytesToString(characterBytes, characterWidth);
				buffer.append(nextCharacter);
			}
		}
		String[] remainingStrings = results.toArray(new String[0]);	
		return remainingStrings;
	}
	
	/**
	 * Gets the number of bytes remaining in the buffer.
	 *
	 * @return
	 * The number of bytes left.
	 */
	public int remaining() {
		return remainingBlocks(1);
	}
	
	/**
	 * Gets the number of shorts remaining in the buffer.
	 *
	 * @return
	 * The number of shorts left.
	 */
	public int remainingShorts() {
		return remainingBlocks(2);
	}
	
	/**
	 * Gets the number of ints remaining in the buffer.
	 *
	 * @return
	 * The number of ints left.
	 */
	public int remainingInts() {
		return remainingBlocks(4);
	}
	
	/**
	 * Gets the number of longs remaining in the buffer.
	 *
	 * @return
	 * The number of longs left.
	 */
	public int remainingLongs() {
		return remainingBlocks(8);
	}
	
	/**
	 * Gets the number of floats remaining in the buffer.
	 *
	 * @return
	 * The number of floats left.
	 */
	public int remainingFloats() {
		return remainingBlocks(4);
	}
	
	/**
	 * Gets the number of doubles remaining in the buffer.
	 *
	 * @return
	 * The number of doubles left.
	 */
	public int remainingDoubles() {
		return remainingBlocks(8);
	}
	
	/**
	 * Gets the number of chars remaining in the buffer.
	 *
	 * @return
	 * The number of chars left.
	 */
	public int remainingChars() {
		return remainingBlocks(2);
	}
	
	/**
	 * Gets the number of blocks of bytes remaining in the buffer.
	 *
	 * @param size
	 * The width of each block.
	 *
	 * @return
	 * The number of blocks of bytes of the specified width that
	 * are left in the buffer.
	 */
	private int remainingBlocks(int size) {
		int bytes = dataBuffer.capacity() - dataBuffer.position();
		int blocks = bytes / size;
		return blocks;
	}
	
	/**
	 * Converts a single byte into a string.
	 *
	 * @param subject
	 * The byte to convert.
	 *
	 * @return
	 * The string.
	 */
	private String byteToString(byte subject) {
		return bytesToString(new byte[]{subject}, 1);
	}
	
	/**
	 * Converts an entire array into a string. The chars are
	 * decoded using unicode with the specified character width.
	 *
	 * @param subject
	 * An array of bytes to convert.
	 *
	 * @param characterWidth
	 * The width in bytes of a character.
	 *
	 * @return
	 * The resulting string.
	 */
	private String bytesToString(byte[] subject, int characterWidth) {
		IntHolder codePoints = new IntHolder(subject.length / characterWidth);
		byte[] characterBuffer = new byte[characterWidth];
		for(int i = 0; i < subject.length; i += characterWidth) {
			for(int j = 0; j < characterWidth; j++) {
				characterBuffer[j] = subject[i+j];
			}
			codePoints.add(arrayToInteger(characterBuffer));
		}
		String result = new String(codePoints.toArray(), 0, codePoints.size());
		return result;
	}
	
	/**
	 * Converts an array of bytes into an int.
	 *
	 * @param intBytes
	 * The bytes that make up the integer.
	 *
	 * @return
	 * The resulting integer.
	 */
	private int arrayToInteger(byte[] intBytes) {
		int result = 0;
		for(int i = 0; i < intBytes.length; i++) {
			result = (result << 8) + (intBytes[i] & 0xff);
		}
		return result;
	}
	
	/**
	 * Checks if a byte array contains a null separator.
	 *
	 * @param characterBuffer
	 * The byte array for the character to be checked.
	 *
	 * @return
	 * True if the array contains all 0's; false otherwise.
	 */
	private boolean isNullSeparator(byte[] characterBuffer) {
		boolean isNull = true;
		for(int i = 0; i < characterBuffer.length; i++) {
			if(characterBuffer[i] != 0) {
				isNull = false;
				break;
			}
		}
		return isNull;
	}
}