package com.dekarrin.script;

/**
 * Indicates that a command is improperly formated.
 */
public class CommandFormatException extends CommandException {
	
	private static final long serialVersionUID = -3267921567494699523L;
	
	/**
	 * Creates a new CommandFormatException with the specified
	 * message appended to the default message.
	 * 
	 * @param message
	 * The message to create the CommandFormatException with.
	 */
	public CommandFormatException(String message) {
		super("invalid format - "+message);
	}
}
