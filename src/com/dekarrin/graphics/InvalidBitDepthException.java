package com.dekarrin.graphics;

/**
 * Exception indicating that the given bit depth is invalid.
 */
public class InvalidBitDepthException extends RuntimeException {
	
	/**
	 * The message of this InvalidBitDepthException, returned by
	 * getMessage().
	 */
	private String message;
	
	/**
	 * Creates a new InvalidBitDepthException with a specified message.
	 *
	 * @param message
	 * The message to assign to this InvalidBitDepthException.
	 */
	public InvalidBitDepthException(String message) {
		this.message = message;
	}
	
	/**
	 * Shows the message for this InvalidBitDepthException.
	 *
	 * @return
	 * The message.
	 */
	public String getMessage() {
		return message;
	}
}