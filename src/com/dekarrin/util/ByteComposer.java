package com.dekarrin.util;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * a wrapper class for ByteBuffer that makes converting
 * different data types into series of bytes easier.
 * This class is the counterpart to ByteParser. 
 */
public class ByteComposer {

	/**
	 * The ByteBuffer reponsible for composing the data
	 * into a byte array.
	 */
	private ByteBuffer dataBuffer;
	
	/**
	 * Creates a new ByteComposer with an array of a
	 * specified size.
	 *
	 * @param size
	 * The size of the array to create.
	 */
	public ByteComposer(int size) {
		dataBuffer = ByteBuffer.wrap(new byte[size]);
	}
	
	/**
	 * Gets the contents of this ByteComposer as a byte
	 * array.
	 *
	 * @return
	 * The contents.
	 */
	public byte[] toArray() {
		return dataBuffer.array();
	}
	
	/**
	 * Resizes the storage array of this ByteComposer. Any bytes
	 * that extend beyond the new size are discarded; all other
	 * bytes are preserved. If the new size is greater than the
	 * current write position of the buffer, the write position is
	 * preserved as well; otherwise, it is reset to 0.
	 *
	 * @param size
	 * The new size to set this ByteComposer's internal storage
	 * array to.
	 */
	public void resize(int size) {
		int oldSizePosition = dataBuffer.position();
		byte[] currentArray = toArray();
		byte[] setValues = Arrays.copyOfRange(currentArray, 0, oldSizePosition);
		byte[] resizedArray = Arrays.copyOfRange(setValues, 0, size);
		dataBuffer = ByteBuffer.wrap(resizedArray);
		if(oldSizePosition < size) {
			dataBuffer.position(oldSizePosition);
		}
	}
	
	/**
	 * Adds a byte to the data.
	 *
	 * @param value
	 * The byte to add.
	 */
	public void composeByte(byte value) {
		dataBuffer.put(value);
	}
	
	/**
	 * Adds multiple bytes to the data.
	 *
	 * @param values
	 * The bytes to add.
	 */
	public void composeBytes(byte[] values) {
		dataBuffer.put(values);
	}
	
	/**
	 * Adds a short to the data.
	 *
	 * @param value
	 * The short to add.
	 */
	public void composeShort(short value) {
		dataBuffer.putShort(value);
	}
	
	/**
	 * Adds multiple shorts to the data.
	 *
	 * @param values
	 * The shorts to add.
	 */
	public void composeShorts(short[] values) {
		for(int i = 0; i < values.length; i++) {
			composeShort(values[i]);
		}
	}
	
	/**
	 * Adds an int to the data.
	 *
	 * @param value
	 * The int to add.
	 */
	public void composeInt(int value) {
		dataBuffer.putInt(value);
	}
	
	/**
	 * Adds an int to the data with a specified
	 * width.
	 *
	 * @param value
	 * The int to add.
	 *
	 * @param width
	 * The width of the int.
	 */
	public void composeInt(int value, int width) {
		composeBytes(intToBytes(value, width));
	}
	
	/**
	 * Adds multiple ints to the data.
	 *
	 * @param values
	 * The ints to add.
	 */
	public void composeInts(int[] values) {
		for(int i = 0; i < values.length; i++) {
			composeInt(values[i]);
		}
	}
	
	/**
	 * Adds multiple ints to the data with specified
	 * widths.
	 *
	 * @param values
	 * The ints to add.
	 *
	 * @param width
	 * The width of each int.
	 */
	public void composeInts(int[] values, int width) {
		for(int i = 0; i < values.length; i++) {
			composeInt(values[i], width);
		}
	}
	
	/**
	 * Adds a long to the data.
	 *
	 * @param value
	 * The long to add.
	 */
	public void composeLong(long value) {
		dataBuffer.putLong(value);
	}
	
	/**
	 * Adds multiple longs to the data.
	 *
	 * @param values
	 * The longs to add.
	 */
	public void composeLongs(long[] values) {
		for(int i = 0; i < values.length; i++) {
			composeLong(values[i]);
		}
	}
	
	/**
	 * Adds a float to the data.
	 *
	 * @param value
	 * The float to add.
	 */
	public void composeFloat(float value) {
		dataBuffer.putFloat(value);
	}
	
	/**
	 * Adds multiple floats to the data.
	 *
	 * @param values
	 * The floats to add.
	 */
	public void composeFloats(float[] values) {
		for(int i = 0; i < values.length; i++) {
			composeFloat(values[i]);
		}
	}
	
	/**
	 * Adds a double to the data.
	 *
	 * @param value
	 * The double to add.
	 */
	public void composeDouble(double value) {
		dataBuffer.putDouble(value);
	}
	
	/**
	 * Adds multiple doubles to the data.
	 *
	 * @param values
	 * The doubles to add.
	 */
	public void composeDoubles(double[] values) {
		for(int i = 0; i < values.length; i++) {
			composeDouble(values[i]);
		}
	}
	
	/**
	 * Adds a boolean to the data.
	 *
	 * @param value
	 * The boolen to add.
	 */
	public void composeBoolean(boolean value) {
		byte byteValue = (byte)((value) ? 1 : 0);
		dataBuffer.put(byteValue);
	}
	
	/**
	 * Adds multiple booleans to the data.
	 *
	 * @param values
	 * The booleans to add.
	 */
	public void composeBooleans(boolean[] values) {
		for(int i = 0; i < values.length; i++) {
			composeBoolean(values[i]);
		}
	}
	
	/**
	 * Adds a char to the data.
	 *
	 * @param value
	 * The char to add.
	 */
	public void composeChar(char value) {
		dataBuffer.putChar(value);
	}
	
	/**
	 * Adds multiple chars to the data.
	 *
	 * @param values
	 * The chars to add.
	 */
	public void composeChars(char[] values) {
		for(int i = 0; i < values.length; i++) {
			composeChar(values[i]);
		}
	}
	
	/**
	 * Adds a String to the data. The String is broken up
	 * into its component chars and each is written to the
	 * data.
	 *
	 * @param value
	 * The String to write.
	 *
	 * @param characterWidth
	 * The width in bytes of each character.
	 *
	 * @param withSeparator
	 * If this is true, a null separator is added to the end
	 * of the String in the data.
	 */
	public void composeString(String value, int characterWidth, boolean withSeparator) {
		char[] stringChars = value.toCharArray();
		byte[] characterBytes = new byte[characterWidth];
		ByteHolder stringBytes = new ByteHolder(stringChars.length * characterWidth);
		for(char c: stringChars) {
			characterBytes = charToBytes(c, characterWidth);
			stringBytes.add(characterBytes);
		}
		composeBytes(stringBytes.toArray());
		if(withSeparator) {
			composeBytes(makeNullSeparator(characterWidth));	
		}
	}
	
	/**
	 * Adds a String to the data with the default character
	 * width of 1 byte. The String is broken up into its
	 * component chars and each is written to the data array.
	 *
	 * @param value
	 * The String to write.
	 *
	 * @param withSeparator
	 * If this is true, a null separator is added to the end
	 * of the String in the data.
	 */
	public void composeString(String value, boolean withSeparator) {
		composeString(value, 1, withSeparator);
	}
	
	/**
	 * Converts an int into bytes.
	 *
	 * @param value
	 * The int to convert.
	 *
	 * @param width
	 * The number of bytes to convert to. Depending on
	 * how big the int is, this may alter the value.
	 * 
	 * @return
	 * The bytes that make up the int.
	 */
	private byte[] intToBytes(int value, int width) {
		byte[] bytes = new byte[width];
		for(int i = 0; i < width; i++) {
			bytes[i] = (byte)(value >>> 8*(width-1-i));
		}
		return bytes;
	}
	
	/**
	 * Converts a char into bytes.
	 *
	 * @param value
	 * The char to convert.
	 *
	 * @param width
	 * The number of bytes to convert to. Depending on
	 * what value the char is, this may alter the value.
	 * 
	 * @return
	 * The bytes that make up the char.
	 */
	private byte[] charToBytes(char value, int width) {
		short shortValue = (short)value;
		byte[] bytes = new byte[width];
		for(int i = 0; i < width; i++) {
			bytes[i] = (byte)(shortValue >>> 8*(width-1-i));
		}
		return bytes;
	}
	
	/**
	 * Creates a null separator byte array.
	 *
	 * @param width
	 * How many bytes wide the separator should be.
	 *
	 * @return
	 * The null separator bytes.
	 */
	private byte[] makeNullSeparator(int width) {
		byte[] nullBytes = new byte[width];
		for(int i = 0; i < width; i++) {
			nullBytes[i] = 0;
		}
		return nullBytes;
	}
}