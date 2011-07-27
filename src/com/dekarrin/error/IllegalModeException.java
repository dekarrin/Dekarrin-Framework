package com.dekarrin.error;

/**
 * Exception indicating that the mode being used is invalid.
 */
public class IllegalModeException extends RuntimeException {

	private static final long serialVersionUID = 1363967626569560295L;

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