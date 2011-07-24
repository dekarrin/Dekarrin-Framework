package com.dekarrin.error;

public class UnknownCommandException extends MessageException {
	
	private static final long serialVersionUID = 2344198275789992192L;

	/**
	 * Creates a new UnknownCommandException.
	 * 
	 * @param command
	 * The name of the command.
	 */
	public UnknownCommandException(String command) {
		super("Unknown command '"+command+"'");
	}
}
