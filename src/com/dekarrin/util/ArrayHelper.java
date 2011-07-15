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
}