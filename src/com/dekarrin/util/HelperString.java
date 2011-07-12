package com.dekarrin.util;

import java.util.*;

/**
 * Provides methods for String-related tasks.
 */
public class HelperString {
	
	/**
	 * The internal String that operations are to be performed on.
	 */
	private String internalString;
	
	/**
	 * The default pad character.
	 */
	public static final char DEFAULT_PADDING_CHARACTER = ' ';
	
	/**
	 * Creates a new HelperString from a String.
	 *
	 * @param internal
	 * A String to use as the basis for this HelperString.
	 */
	public HelperString(String internal) {
		internalString = internal;
	}
	
	/**
	 * Creates a new HelperString from an array of Strings.
	 *
	 * @param internals
	 * The array of strings to use as the basis for this
	 * HelperString. These strings are concatenated into a
	 * single string.
	 */
	public HelperString(String[] internals) {
		internalString = arrayToString(internals);
	}
	
	/**
	 * Creates a new HelperString from an array of String byte
	 * data.
	 *
	 * @param byteData
	 * The String data to parse.
	 */
	public HelperString(byte[] byteData) {
		internalString = new String(byteData);
	}
	
	/**
	 * Creates several new HelperStrings from an array of String
	 * byte data. The Strings are assumed to be divided by null
	 * separators.
	 *
	 * @param byteData
	 * The byte data to parse.
	 *
	 * @return
	 * An array of HelperStrings created by extracting String data
	 * from the given byte array.
	 */
	public static HelperString[] createFromBytes(byte[] byteData) {
		Vector<HelperString> hsList = new Vector<HelperString>(3);
		int nullPosition;
		byte[] stringBytes;
		int endIndex = -1;
		while(endIndex != byteData.length) {
			byteData = Arrays.copyOfRange(byteData, endIndex+1, byteData.length);
			nullPosition = Arrays.asList(byteData).indexOf(0);
			endIndex = (nullPosition != -1) ? nullPosition : byteData.length;
			stringBytes = Arrays.copyOfRange(byteData, 0, endIndex);
			hsList.add(new HelperString(stringBytes));
		}
		HelperString[] outputList = hsList.toArray(new HelperString[0]);
		return outputList;
	}
	
	/**
	 * Pads this HelperString with a character to the right.
	 *
	 * @param count
	 * The number of characters that this HelperString should be
	 * padded to.
	 *
	 * @param padChar
	 * The character that this HelperString will be padded with.
	 *
	 * @return
	 * This HelperString.
	 */
	public HelperString padRight(int count, char padChar) {
		String padded = internalString;
		while(padded.length() < count) {
			padded = padded + Character.toString(padChar);
		}
		internalString = padded;
		return this;
	}
	
	/**
	 * Pads this HelperString to the right. This HelperString is
	 * padded on the right with the default character, the space
	 * character.
	 *
	 * @param count
	 * The number of characters that this HelperString should be
	 * padded to.
	 *
	 * @return
	 * This HelperString.
	 */
	public HelperString padRight(int count) {
		return padRight(count, DEFAULT_PADDING_CHARACTER);
	}
	
	/**
	 * Pads this HelperString with a character to the left.
	 *
	 * @param count
	 * The number of characters that this HelperString should be
	 * padded to.
	 *
	 * @param padChar
	 * The character that this HelperString will be padded with.
	 *
	 * @return
	 * This HelperString.
	 */
	public HelperString padLeft(int count, char padChar) {
		String padded = internalString;
		while(padded.length() < count) {
			padded = Character.toString(padChar) + padded;
		}
		internalString = padded;
		return this;
	}
	
	/**
	 * Pads this HelperString to the left. This HelperString is
	 * padded on the left with the default character, the space
	 * character.
	 *
	 * @param count
	 * The number of characters that this HelperString should be
	 * padded to.
	 *
	 * @return
	 * This HelperString.
	 */
	public HelperString padLeft(int count) {
		return padLeft(count, DEFAULT_PADDING_CHARACTER);
	}
	
	/**
	 * Capitalizes all first letters of words in this HelperString.
	 *
	 * @return
	 * This HelperString.
	 */
	public HelperString capitalize(String subject) {
		String[] words = internalString.split(" ");
		String finished = "";
		for(String w: words) {
			finished += w.substring(0, 1).toUpperCase() + w.substring(1, w.length()).toLowerCase();
			finished += " ";
		}
		finished = finished.substring(0, finished.length() - 1);
		internalString = finished;
		return this;
	}
	
	/**
	 * Gets this HelperString's String representation.
	 *
	 * @return
	 * This HelperString as a String.
	 */
	public String toString() {
		return internalString;
	}
	
	/**
	 * Concatenates an array of Strings into a single String.
	 *
	 * @param data
	 * The String array to convert from.
	 *
	 * @return
	 * All of the given Strings concatenated into a single String.
	 */
	private String arrayToString(String[] data) {
		String finalString = null;
		for(int i = 0; i < data.length; i++) {
			finalString = (finalString != null) ? finalString + data[i] : data[i];
		}
		return finalString;
	}
}
