package com.dekarrin.error;

/**
 * Exception with a message.
 */
public class MessageException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1095430943887736092L;
	/**
	 * The message of this MessageException, returned by
	 * getMessage().
	 */
	private String message;
	
	/**
	 * Creates a new MessageException with a specified message.
	 *
	 * @param message
	 * The message to assign to this MessageException.
	 */
	public MessageException(String message) {
		this.message = message;
	}
	
	/**
	 * Shows the message for this MessageException.
	 *
	 * @return
	 * The message.
	 */
	public String getMessage() {
		return message;
	}
}