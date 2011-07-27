package com.dekarrin.script;

/**
 * Indicates that there was a problem with a command.
 */
public class CommandException extends Exception {
	
	private static final long serialVersionUID = 8544974456390939684L;
	
	/**
	 * Creates a new CommandException with a specified message.
	 *
	 * @param message
	 * The message to assign to this CommandException.
	 */
	public CommandException(String message) {
		super("command error - "+message);
	}
}
