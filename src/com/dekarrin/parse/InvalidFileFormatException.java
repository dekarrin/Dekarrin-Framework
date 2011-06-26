package com.dekarrin.parse;

/**
 * Represents an incorrect file format.
 */
public class InvalidFileFormatException extends Exception {
	
	/**
	 * The message of this InvalidFileFormatException, returned by
	 * getMessage().
	 */
	private String message;
	
	/**
	 * Creates a new InvalidFileFormatException with a specified message.
	 *
	 * @param message
	 * The message to assign to this InvalidFileFormatException
	 */
	public InvalidFileFormatException(String message) {
		this.message = message;
	}
	
	/**
	 * Shows the message for this InvalidFileFormatException.
	 */
	public String getMessage() {
		return message;
	}
}