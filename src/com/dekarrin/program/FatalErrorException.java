package com.dekarrin.program;

/**
 * A general error that cannot be handled; the only thing that should be
 * done about it is to show it to the user.
 */
public class FatalErrorException extends Exception {
	
	private static final long serialVersionUID = 1359606802959044953L;

	/**
	 * Creates a new FatalErrorException with a specified message.
	 *
	 * @param message
	 * The message to assign to this FatalErrorException.
	 */
	public FatalErrorException(String message) {
		super(message);
	}
}