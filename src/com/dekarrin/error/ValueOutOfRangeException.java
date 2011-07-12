package com.dekarrin.error;

/**
 * Exception indicating that the passed-in value exceeds the range.
 */
public class ValueOutOfRangeException extends RuntimeMessageException {
	
	/**
	 * Creates a new ValueOutOfRangeException with a specified message.
	 *
	 * @param message
	 * The message to assign to this ValueOutOfRangeException.
	 */
	public ValueOutOfRangeException(String message) {
		super(message);
	}
}