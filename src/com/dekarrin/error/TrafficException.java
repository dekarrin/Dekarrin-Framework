package com.dekarrin.error;

/**
 * Exception for a bad network traffic event.
 */
public class TrafficException extends Exception {
	
	private static final long serialVersionUID = 570067777164059206L;

	/**
	 * Creates a new TrafficException with a specified message.
	 *
	 * @param message
	 * The message to assign to this TrafficException.
	 */
	public TrafficException(String message) {
		super(message);
	}
}