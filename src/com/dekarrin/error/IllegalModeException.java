package com.dekarrin.error;

/**
 * Exception indicating that the mode being used is invalid.
 */
public class IllegalModeException extends RuntimeMessageException {
	
	/**
	 * Creates a new IllegalModeException with a specified message.
	 *
	 * @param message
	 * The message to assign to this IllegalModeException.
	 */
	public IllegalModeException(String message) {
		super(message);
	}
}