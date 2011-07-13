package com.dekarrin.error;

/**
 * Runtime exception with a message.
 */
public class RuntimeMessageException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1082386472498279970L;
	/**
	 * The message of this RuntimeMessageException, returned by
	 * getMessage().
	 */
	private String message;
	
	/**
	 * Creates a new RuntimeMessageException with a specified message.
	 *
	 * @param message
	 * The message to assign to this RuntimeMessageException.
	 */
	public RuntimeMessageException(String message) {
		this.message = message;
	}
	
	/**
	 * Shows the message for this RuntimeMessageException.
	 *
	 * @return
	 * The message.
	 */
	public String getMessage() {
		return message;
	}
}