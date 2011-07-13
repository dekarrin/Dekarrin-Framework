package com.dekarrin.io;

/**
 * Exception indicating that an error occured with the stream
 */
public class StreamFailureException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3733188104606442560L;
	/**
	 * The message of this StreamFailureException, returned by
	 * getMessage().
	 */
	private String message;
	
	/**
	 * Creates a new StreamFailureException with a specified message.
	 *
	 * @param message
	 * The message to assign to this StreamFailureException
	 */
	public StreamFailureException(String message) {
		this.message = message;
	}
	
	/**
	 * Shows the message for this StreamFailureException.
	 */
	public String getMessage() {
		return message;
	}
}