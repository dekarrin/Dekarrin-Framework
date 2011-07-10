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
		int[][] subarrays = new int[occurance+1][];
		int[] sub;
		int start;
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
		byte[][] subarrays = new byte[occurance+1][];
		int start;
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
}