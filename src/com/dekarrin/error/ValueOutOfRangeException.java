package com.dekarrin.error;

/**
 * Exception indicating that the passed-in value exceeds the range.
 */
public class ValueOutOfRangeException extends RuntimeException {
	
	/**
	 * The message of this ValueOutOfRangeException, returned by
	 * getMessage().
	 */
	private String message;
	
	/**
	 * Creates a new ValueOutOfRangeException with a specified message.
	 *
	 * @param message
	 * The message to assign to this ValueOutOfRangeException.
	 */
	public ValueOutOfRangeException(String message) {
		this.message = message;
	}
	
	/**
	 * Shows the message for this ValueOutOfRangeException.
	 *
	 * @return
	 * The message.
	 */
	public String getMessage() {
		return message;
	}
}