package com.dekarrin.error;

/**
 * Exception indicating that the mode being used is invalid.
 */
public class IllegalModeException extends RuntimeException {
	
	/**
	 * The message of this IllegalModeException, returned by
	 * getMessage().
	 */
	private String message;
	
	/**
	 * Creates a new IllegalModeException with a specified message.
	 *
	 * @param message
	 * The message to assign to this IllegalModeException.
	 */
	public IllegalModeException(String message) {
		this.message = message;
	}
	
	/**
	 * Shows the message for this IllegalModeException.
	 *
	 * @returns
	 * The message.
	 */
	public String getMessage() {
		return message;
	}
}