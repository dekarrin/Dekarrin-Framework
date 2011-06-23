package com.dekarrin.util;

/**
 * Provides methods for String-related tasks.
 */
public class StringHelper {

	/**
	 * Concatenates an Array of Strings into a single String.
	 *
	 * @param data
	 * The String array to convert from.
	 */
	public static String arrayToString(String[] data) {
		String finalString = null;
		
		for(int i = 0; i < data.length; i++) {
			finalString = (finalString != null) ? finalString + data[i] : data[i];
		}
		
		return finalString;
	}
	
	/**
	 * Pads a String with a character to the right.
	 *
	 * @param subject
	 * The String to pad.
	 *
	 * @param padChar
	 * The character that the String will be padded with.
	 *
	 * @param count
	 * The number of characters that this String should be padded to.
	 */
	public static String padRight(String subject, char padChar, int count) {
		String padded = subject;
		
		while(padded.length() < count) {
			padded = padded + Character.toString(padChar);
		}
		
		return padded;
	}
	
	/**
	 * Pads a String with a character to the left.
	 *
	 * @param subject
	 * The String to pad.
	 *
	 * @param padChar
	 * The character that the String will be padded with.
	 *
	 * @param count
	 * The number of characters that this String should be padded to.
	 */
	public static String padLeft(String subject, char padChar, int count) {
		String padded = subject;
		
		while(padded.length() < count) {
			padded = Character.toString(padChar) + padded;
		}
		
		return padded;
	}
	
	/**
	 * Capitalizes all first letters of words in a string.
	 *
	 * @param subject
	 * The String to be capitalized.
	 *
	 * @returns
	 * The String after it has been capitalized.
	 */
	public static String capitalize(String subject) {
		String[] words = subject.split(" ");
		String finished = "";
		
		for(String w: words) {
			finished += w.substring(0, 1).toUpperCase() + w.substring(1, w.length()).toLowerCase();
			finished += " ";
		}
		finished = finished.substring(0, finished.length() - 1);
		
		return finished;
	}
}
