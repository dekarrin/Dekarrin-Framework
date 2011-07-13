package com.dekarrin.cli;

/**
 * Represents an exception where a command that a CLI program
 * is unable to process is attempted to be processed.
 */
public class UnrecognizedCommandException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7205495414317692195L;

	/**
	 * The command that caused the error.
	 */
	private String command;
	
	/**
	 * The message of this UnrecognizedCommandException.
	 */
	private String message;
	
	/**
	 * Creates a new UnrecognizedCommandException.
	 *
	 * @param command
	 * The command that caused the error.
	 *
	 * @param message
	 * The message to give to anyone querying this
	 * UnrecognizedCommandException.
	 */
	public UnrecognizedCommandException(String command, String message) {
		this.command = command;
		this.message = message;
	}
	
	/**
	 * Gets the command that caused the problem.
	 *
	 * @return
	 * The command.
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * Gets the message for this UnrecognizedCommandException.
	 *
	 * @return
	 * The message.
	 */
	public String getMessage() {
		return message;
	}
}