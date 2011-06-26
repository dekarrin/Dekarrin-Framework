package com.dekarrin.cli;

/**
 * Represents an exception where a command that a CLI program
 * is unable to process is attempted to be processed.
 */
public class UnrecognizedCommandException extends RuntimeException {
	
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
	 * @returns
	 * The command.
	 */
	public String getCommand() {
		return command;
	}
	
	/**
	 * Gets the message for this UnrecognizedCommandException.
	 *
	 * @returns
	 * The message.
	 */
	public String getMessage() {
		return message;
	}
}