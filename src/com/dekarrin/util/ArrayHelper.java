package com.dekarrin.util;

import java.util.Arrays;

/**
 * Provides several useful methods for dealing with
 * arrays.
 */
public class ArrayHelper {
	
	/**
	 * Splits an array at a value. This is similar to calling
	 * split() on a String; the given array is split into
	 * smaller arrays at any index that the value occurs at.
	 *
	 * @param subject
	 * The subject to split.
	 *
	 * @param value
	 * The value to split at.
	 *
	 * @return
	 * The given array split into smaller arrays.
	 */
	public static int[][] split(int[] subject, int value) {
		int occurances = count(subject, value);
		int[][] subarrays = new int[occurances+1][];
		int start = -1;
		for(int i = 0; i < subject.length; i++) {
			if(i != value) {
				start = i;
				break;
			}
		}
		int end;
		int j = 0;
		for(int i = 0; i < subject.length; i++) {
			if(subject[i] == value) {
				end = i;
				subarrays[j++] = Arrays.copyOfRange(subject, start, end);
				start = i + 1;
			}
		}
		if(start != subject.length) {
			subarrays[j] = Arrays.copyOfRange(subject, start, subject.length);
		} else {
			subarrays = Arrays.copyOfRange(subarrays, 0, subarrays.length-1);
		}
		return subarrays;
	}
	
	/**
	 * Splits an array at a value. This is similar to calling
	 * split() on a String; the given array is split into
	 * smaller arrays at any index that the value occurs at.
	 *
	 * @param subject
	 * The subject to split.
	 *
	 * @param value
	 * The value to split at.
	 *
	 * @return
	 * The given array split into smaller arrays.
	 */
	public static byte[][] split(byte[] subject, byte value) {
		int occurances = count(subject, value);
		byte[][] subarrays = new byte[occurances+1][];
		int start = -1;
		for(int i = 0; i < subject.length; i++) {
			if(i != value) {
				start = i;
				break;
			}
		}
		int end;
		int j = 0;
		for(int i = 0; i < subject.length; i++) {
			if(subject[i] == value) {
				end = i;
				subarrays[j++] = Arrays.copyOfRange(subject, start, end);
				start = i + 1;
			}
		}
		if(start != subject.length) {
			subarrays[j] = Arrays.copyOfRange(subject, start, subject.length);
		} else {
			subarrays = Arrays.copyOfRange(subarrays, 0, subarrays.length-1);
		}
		return subarrays;
	}
	
	/**
	 * Counts the number of occurances of a value.
	 *
	 * @param subject
	 * The subject to search in.
	 *
	 * @param value
	 * The value to get the number of occurances of.
	 *
	 * @return
	 * The number of occurances of the specified value.
	 */
	public static int count(boolean[] subject, boolean value) {
		int c = 0;
		for(boolean i: subject) {
			if(i == value) {
				c++;
			}
		}
		return c;
	}
	
	/**
	 * Counts the number of occurrences of a value.
	 *
	 * @param subject
	 * The subject to search in.
	 *
	 * @param value
	 * The value to get the number of occurances of.
	 *
	 * @return
	 * The number of occurances of the specified value.
	 */
	public static int count(byte[] subject, byte value) {
		int c = 0;
		for(byte i: subject) {
			if(i == value) {
				c++;
			}
		}
		return c;
	}
	
	/**
	 * Counts the number of occurances of a value.
	 *
	 * @param subject
	 * The subject to search in.
	 *
	 * @param value
	 * The value to get the number of occurances of.
	 *
	 * @return
	 * The number of occurances of the specified value.
	 */
	public static int count(char[] subject, char value) {
		int c = 0;
		for(char i: subject) {
			if(i == value) {
				c++;
			}
		}
		return c;
	}
	
	/**
	 * Counts the number of occurances of a value.
	 *
	 * @param subject
	 * The subject to search in.
	 *
	 * @param value
	 * The value to get the number of occurances of.
	 *
	 * @return
	 * The number of occurances of the specified value.
	 */
	public static int count(double[] subject, double value) {
		int c = 0;
		for(double i: subject) {
			if(i == value) {
				c++;
			}
		}
		return c;
	}
	
	/**
	 * Counts the number of occurances of a value.
	 *
	 * @param subject
	 * The subject to search in.
	 *
	 * @param value
	 * The value to get the number of occurances of.
	 *
	 * @return
	 * The number of occurances of the specified value.
	 */
	public static int count(float[] subject, float value) {
		int c = 0;
		for(float i: subject) {
			if(i == value) {
				c++;
			}
		}
		return c;
	}
	
	/**
	 * Counts the number of occurances of a value.
	 *
	 * @param subject
	 * The subject to search in.
	 *
	 * @param value
	 * The value to get the number of occurances of.
	 *
	 * @return
	 * The number of occurances of the specified value.
	 */
	public static int count(int[] subject, int value) {
		int c = 0;
		for(int i: subject) {
			if(i == value) {
				c++;
			}
		}
		return c;
	}
	
	/**
	 * Counts the number of occurances of a value.
	 *
	 * @param subject
	 * The subject to search in.
	 *
	 * @param value
	 * The value to get the number of occurances of.
	 *
	 * @return
	 * The number of occurances of the specified value.
	 */
	public static int count(long[] subject, long value) {
		int c = 0;
		for(long i: subject) {
			if(i == value) {
				c++;
			}
		}
		return c;
	}
	
	/**
	 * Counts the number of occurances of a value.
	 *
	 * @param subject
	 * The subject to search in.
	 *
	 * @param value
	 * The value to get the number of occurances of.
	 *
	 * @return
	 * The number of occurances of the specified value.
	 */
	public static int count(short[] subject, short value) {
		int c = 0;
		for(short i: subject) {
			if(i == value) {
				c++;
			}
		}
		return c;
	}
	
	/**
	 * Counts the number of occurances of a value. The value
	 * is compared by using the equals() method.
	 *
	 * @param subject
	 * The subject to search in.
	 *
	 * @param value
	 * The value to get the number of occurances of.
	 *
	 * @return
	 * The number of occurances of the specified value.
	 */
	public static <T> int count(T[] subject, T value) {
		int c = 0;
		for(T i: subject) {
			if(i.equals(value)) {
				c++;
			}
		}
		return c;
	}
	
	/**
	 * Gets the total of all values in an array.
	 * 
	 * @param subject
	 * The array to total the elements in.
	 * 
	 * @return
	 * The result of adding all the elements of the subject
	 * together.
	 */
	public static long sum(byte[] subject) {
		long sum = 0;
		for(byte b: subject) {
			sum += b;
		}
		return sum;
	}
	
	/**
	 * Gets the total of all values in an array.
	 * 
	 * @param subject
	 * The array to total the elements in.
	 * 
	 * @return
	 * The result of adding all the elements of the subject
	 * together.
	 */
	public static long sum(short[] subject) {
		long sum = 0;
		for(short s: subject) {
			sum += s;
		}
		return sum;
	}
	
	/**
	 * Gets the total of all values in an array.
	 * 
	 * @param subject
	 * The array to total the elements in.
	 * 
	 * @return
	 * The result of adding all the elements of the subject
	 * together.
	 */
	public static long sum(char[] subject) {
		long sum = 0;
		for(char c: subject) {
			sum += c;
		}
		return sum;
	}
	
	/**
	 * Gets the total of all values in an array. For the
	 * purposes of adding booleans together, true is 1 and
	 * false is 0.
	 * 
	 * @param subject
	 * The array to total the elements in.
	 * 
	 * @return
	 * The result of adding all the elements of the subject
	 * together.
	 */
	public static long sum(boolean[] subject) {
		long sum = 0;
		for(boolean b: subject) {
			if(b) {
				sum++;
			}
		}
		return sum;
	}
	
	/**
	 * Gets the total of all values in an array.
	 * 
	 * @param subject
	 * The array to total the elements in.
	 * 
	 * @return
	 * The result of adding all the elements of the subject
	 * together.
	 */
	public static long sum(int[] subject) {
		long sum = 0;
		for(int i: subject) {
			sum += i;
		}
		return sum;
	}
	
	/**
	 * Gets the total of all values in an array.
	 * 
	 * @param subject
	 * The array to total the elements in.
	 * 
	 * @return
	 * The result of adding all the elements of the subject
	 * together.
	 */
	public static long sum(long[] subject) {
		long sum = 0;
		for(long l: subject) {
			sum += l;
		}
		return sum;
	}
	
	/**
	 * Gets the total of all values in an array.
	 * 
	 * @param subject
	 * The array to total the elements in.
	 * 
	 * @return
	 * The result of adding all the elements of the subject
	 * together.
	 */
	public static double sum(float[] subject) {
		double sum = 0.0;
		for(float f: subject) {
			sum += f;
		}
		return sum;
	}
	
	/**
	 * Gets the total of all values in an array.
	 * 
	 * @param subject
	 * The array to total the elements in.
	 * 
	 * @return
	 * The result of adding all the elements of the subject
	 * together.
	 */
	public static double sum(double[] subject) {
		double sum = 0.0;
		for(double d: subject) {
			sum += d;
		}
		return sum;
	}
	
	/**
	 * Appends two arrays together.
	 * 
	 * @param beginArray
	 * The array to come first.
	 * 
	 * @param endArray
	 * The array to come last.
	 * 
	 * @return
	 * The two arrays appended together.
	 */
	public static byte[] append(byte[] beginArray, byte[] endArray) {
		byte[] appended = new byte[beginArray.length + endArray.length];
		for(int i = 0; i < beginArray.length; i++) {
			appended[i] = beginArray[i];
		}
		for(int i = 0; i < endArray.length; i++) {
			appended[beginArray.length + i] = endArray[i];
		}
		return appended;
	}
	
	/**
	 * Appends two arrays together.
	 * 
	 * @param beginArray
	 * The array to come first.
	 * 
	 * @param endArray
	 * The array to come last.
	 * 
	 * @return
	 * The two arrays appended together.
	 */
	public static short[] append(short[] beginArray, short[] endArray) {
		short[] appended = new short[beginArray.length + endArray.length];
		for(int i = 0; i < beginArray.length; i++) {
			appended[i] = beginArray[i];
		}
		for(int i = 0; i < endArray.length; i++) {
			appended[beginArray.length + i] = endArray[i];
		}
		return appended;
	}
	
	/**
	 * Appends two arrays together.
	 * 
	 * @param beginArray
	 * The array to come first.
	 * 
	 * @param endArray
	 * The array to come last.
	 * 
	 * @return
	 * The two arrays appended together.
	 */
	public static int[] append(int[] beginArray, int[] endArray) {
		int[] appended = new int[beginArray.length + endArray.length];
		for(int i = 0; i < beginArray.length; i++) {
			appended[i] = beginArray[i];
		}
		for(int i = 0; i < endArray.length; i++) {
			appended[beginArray.length + i] = endArray[i];
		}
		return appended;
	}
	
	/**
	 * Appends two arrays together.
	 * 
	 * @param beginArray
	 * The array to come first.
	 * 
	 * @param endArray
	 * The array to come last.
	 * 
	 * @return
	 * The two arrays appended together.
	 */
	public static long[] append(long[] beginArray, long[] endArray) {
		long[] appended = new long[beginArray.length + endArray.length];
		for(int i = 0; i < beginArray.length; i++) {
			appended[i] = beginArray[i];
		}
		for(int i = 0; i < endArray.length; i++) {
			appended[beginArray.length + i] = endArray[i];
		}
		return appended;
	}
	
	/**
	 * Appends two arrays together.
	 * 
	 * @param beginArray
	 * The array to come first.
	 * 
	 * @param endArray
	 * The array to come last.
	 * 
	 * @return
	 * The two arrays appended together.
	 */
	public static char[] append(char[] beginArray, char[] endArray) {
		char[] appended = new char[beginArray.length + endArray.length];
		for(int i = 0; i < beginArray.length; i++) {
			appended[i] = beginArray[i];
		}
		for(int i = 0; i < endArray.length; i++) {
			appended[beginArray.length + i] = endArray[i];
		}
		return appended;
	}
	
	/**
	 * Appends two arrays together.
	 * 
	 * @param beginArray
	 * The array to come first.
	 * 
	 * @param endArray
	 * The array to come last.
	 * 
	 * @return
	 * The two arrays appended together.
	 */
	public static float[] append(float[] beginArray, float[] endArray) {
		float[] appended = new float[beginArray.length + endArray.length];
		for(int i = 0; i < beginArray.length; i++) {
			appended[i] = beginArray[i];
		}
		for(int i = 0; i < endArray.length; i++) {
			appended[beginArray.length + i] = endArray[i];
		}
		return appended;
	}
	
	/**
	 * Appends two arrays together.
	 * 
	 * @param beginArray
	 * The array to come first.
	 * 
	 * @param endArray
	 * The array to come last.
	 * 
	 * @return
	 * The two arrays appended together.
	 */
	public static double[] append(double[] beginArray, double[] endArray) {
		double[] appended = new double[beginArray.length + endArray.length];
		for(int i = 0; i < beginArray.length; i++) {
			appended[i] = beginArray[i];
		}
		for(int i = 0; i < endArray.length; i++) {
			appended[beginArray.length + i] = endArray[i];
		}
		return appended;
	}
	
	/**
	 * Appends two arrays together.
	 * 
	 * @param beginArray
	 * The array to come first.
	 * 
	 * @param endArray
	 * The array to come last.
	 * 
	 * @return
	 * The two arrays appended together.
	 */
	public static boolean[] append(boolean[] beginArray, boolean[] endArray) {
		boolean[] appended = new boolean[beginArray.length + endArray.length];
		for(int i = 0; i < beginArray.length; i++) {
			appended[i] = beginArray[i];
		}
		for(int i = 0; i < endArray.length; i++) {
			appended[beginArray.length + i] = endArray[i];
		}
		return appended;
	}
	
	/**
	 * Appends two arrays together.
	 * 
	 * @param beginArray
	 * The array to come first.
	 * 
	 * @param endArray
	 * The array to come last.
	 * 
	 * @return
	 * The two arrays appended together.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] append(T[] beginArray, T[] endArray) {
		Object[] appended = new Object[beginArray.length + endArray.length];
		for(int i = 0; i < beginArray.length; i++) {
			appended[i] = beginArray[i];
		}
		for(int i = 0; i < endArray.length; i++) {
			appended[beginArray.length + i] = endArray[i];
		}
		return (T[])appended;
	}
	
	/**
	 * Converts an array of bytes into a short. The bytes
	 * are read in big-endian order, with the first index
	 * representing the first used byte. If the length of the
	 * array of bytes is not equal to the number of bytes in
	 * a short, it is either padded or truncated to make
	 * the result fit within a short.
	 * 
	 * For in-depth examples of the mechanics of this and
	 * other to-primitive methods, see {@link
	 * ArrayHelper#toInt(byte[]) toInt()}.
	 *
	 * @param primitives
	 * The bytes that make up the short. The length of this
	 * array need not be the same size as a short. If it
	 * is more than two bytes, only the last two bytes will
	 * be used. If it is less than two bytes, the short is
	 * padded to the left with zero-filled bytes to make it
	 * two bytes long.
	 *
	 * @return
	 * The short created by concatenating the given byte
	 * primitives.
	 */
	public static short toShort(byte[] primitives) {
		short result = 0;
		for(int i = 0; i < primitives.length; i++) {
			result = (short)((result << 8) | (primitives[i] & 0xff));
		}
		return result;
	}
	
	/**
	 * Converts an array of bytes into an integer. The bytes
	 * are read in big-endian order, with the first index
	 * representing the first used byte. If the length of the
	 * array of bytes is not equal to the number of bytes in
	 * an integer, it is either padded or truncated to make
	 * the result fit within an integer.
	 * 
	 * Examples:
	 * toInt(new byte[]{36, 123, -28, 8}) takes the bitfields
	 * for 36, 123, -28, and 8. These are 00100100, 01111011,
	 * 11100100, and 00001000, respectively. The bitfields are
	 * concatenated together get a new 32-bit bit field,
	 * 00100100 01111011 11100100 00001000. This bitfield
	 * represents the integer value 612099080, which is
	 * returned.
	 * 
	 * toInt(new byte[]{36, 123}) takes the bitfields for 36
	 * and 123. These are 00100100 and 01111011, respectively.
	 * The bitfields are concatenated, but because there are
	 * fewer than four bytes, the resulting bitfield is padded
	 * to the left with zero-filled bytes in order to result
	 * in a 32-bit field, 00000000 00000000 00100100 01111011.
	 * This bitfield represents the integer value 9339, which
	 * is returned.
	 * 
	 * toInt(new byte[]{36, 123, -28, 8, 17, -1}) takes the
	 * bitfields for 36, 123, -28, 8, 17, and -1. These are
	 * 00100100, 01111011, 11100100, 00001000, 00010001, and
	 * 11111111, respectively. Because there are more than
	 * four bytes, only the last four bytes from the array
	 * are concatenated into a new 32-bit field,
	 * 11100100 00001000 00010001 11111111. This bitfield
	 * represents the integer value 3825734143, which is
	 * returned.
	 *
	 * @param primitives
	 * The bytes that make up the integer. The length of this
	 * array need not be the same size as an integer. If it
	 * is more than four bytes, only the last four bytes will
	 * be used. If it is less than four bytes, the integer is
	 * padded to the left with zero-filled bytes to make it
	 * four bytes long.
	 *
	 * @return
	 * The integer created by concatenating the given byte
	 * primitives.
	 */
	public static int toInt(byte[] primitives) {
		int result = 0;
		for(int i = 0; i < primitives.length; i++) {
			result = (result << 8) | (primitives[i] & 0xff);
		}
		return result;
	}
	
	/**
	 * Converts an array of bytes into a long. The bytes
	 * are read in big-endian order, with the first index
	 * representing the first used byte. If the length of the
	 * array of bytes is not equal to the number of bytes in
	 * a long, it is either padded or truncated to make
	 * the result fit within a long.
	 * 
	 * For in-depth examples of the mechanics of this and
	 * other to-primitive methods, see {@link
	 * ArrayHelper#toInt(byte[]) toInt()}.
	 *
	 * @param primitives
	 * The bytes that make up the long. The length of this
	 * array need not be the same size as a long. If it
	 * is more than eight bytes, only the last eight bytes will
	 * be used. If it is less than eight bytes, the long is
	 * padded to the left with zero-filled bytes to make it
	 * eight bytes long.
	 *
	 * @return
	 * The long created by concatenating the given byte
	 * primitives.
	 */
	public static long toLong(byte[] primitives) {
		long result = 0;
		for(int i = 0; i < primitives.length; i++) {
			result = (long)((result << 8) | (primitives[i] & 0xff));
		}
		return result;
	}
	
	/**
	 * Converts an array of bytes into a char. The bytes
	 * are read in big-endian order, with the first index
	 * representing the first used byte. If the length of the
	 * array of bytes is not equal to the number of bytes in
	 * a char, it is either padded or truncated to make
	 * the result fit within a char.
	 * 
	 * For in-depth examples of the mechanics of this and
	 * other to-primitive methods, see {@link
	 * ArrayHelper#toInt(byte[]) toInt()}.
	 *
	 * @param primitives
	 * The bytes that make up the char. The length of this
	 * array need not be the same size as a char. If it
	 * is more than two bytes, only the last two bytes will
	 * be used. If it is less than two bytes, the char is
	 * padded to the left with zero-filled bytes to make it
	 * two bytes long.
	 *
	 * @return
	 * The char created by concatenating the given byte
	 * primitives.
	 */
	public static char toChar(byte[] primitives) {
		char result = 0;
		for(int i = 0; i < primitives.length; i++) {
			result = (char)((result << 8) | (primitives[i] & 0xff));
		}
		return result;
	}
	
	/**
	 * Gets the bytes that make up a short.
	 * 
	 * @param primitive
	 * The primitive value to convert to bytes.
	 * 
	 * @return
	 * An array of bytes that make up the given primitive.
	 */
	public static byte[] toArray(short primitive) {
		int width = 2;
		byte[] bytes = new byte[width];
		for(int i = 0; i < width; i++) {
			bytes[i] = (byte)(primitive >>> 8*(width-1-i));
		}
		return bytes;
	}
	
	/**
	 * Gets the bytes that make up an integer.
	 * 
	 * @param primitive
	 * The primitive value to convert to bytes.
	 * 
	 * @return
	 * An array of bytes that make up the given primitive.
	 */
	public static byte[] toArray(int primitive) {
		int width = 4;
		byte[] bytes = new byte[width];
		for(int i = 0; i < width; i++) {
			bytes[i] = (byte)(primitive >>> 8*(width-1-i));
		}
		return bytes;
	}
	
	/**
	 * Gets the bytes that make up a long.
	 * 
	 * @param primitive
	 * The primitive value to convert to bytes.
	 * 
	 * @return
	 * An array of bytes that make up the given primitive.
	 */
	public static byte[] toArray(long primitive) {
		int width = 8;
		byte[] bytes = new byte[width];
		for(int i = 0; i < width; i++) {
			bytes[i] = (byte)(primitive >>> 8*(width-1-i));
		}
		return bytes;
	}
	
	/**
	 * Gets the bytes that make up a char.
	 * 
	 * @param primitive
	 * The primitive value to convert to bytes.
	 * 
	 * @return
	 * An array of bytes that make up the given primitive.
	 */
	public static byte[] toArray(char primitive) {
		int width = 2;
		byte[] bytes = new byte[width];
		for(int i = 0; i < width; i++) {
			bytes[i] = (byte)(primitive >>> 8*(width-1-i));
		}
		return bytes;
	}
}