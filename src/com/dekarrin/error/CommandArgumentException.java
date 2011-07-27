package com.dekarrin.error;

public class CommandArgumentException extends Exception {
	
	private static final long serialVersionUID = 9087509873464380983L;

	/**
	 * Creates a new CommandArgumentException.
	 * 
	 * @param message
	 * The message.
	 */
	public CommandArgumentException(String message) {
		super(message);
	}
}
