package com.dekarrin.script;

/**
 * Represents a problem with the format of a command argument
 * or flag in a command stream.
 */
public class ArgumentFormatException extends CommandFormatException {
	
	private static final long serialVersionUID = 5929328110108967180L;

	/**
	 * The name of the argument that is in error.
	 */
	private String argumentName;
	
	/**
	 * The bad value of the argument.
	 */
	private String argumentValue;
	
	/**
	 * Creates a new ArgumentFormatException with a specified
	 * message.
	 * 
	 * @param arg
	 * The name of the argument that is formatted incorrectly.
	 * 
	 * @param value
	 * The incorrect value of the argument.
	 */
	public ArgumentFormatException(String arg, String value) {
		super(arg+" is set to '"+value+"'");
		argumentName = arg;
		argumentValue = value;
	}
	
	/**
	 * Creates a new ArgumentFormatException with a specified
	 * message.
	 * 
	 * @param arg
	 * The name of the argument that is formatted incorrectly.
	 * 
	 * @param value
	 * The incorrect value of the argument.
	 * 
	 * @param message
	 * A message to append to the default message.
	 */
	public ArgumentFormatException(String arg, String value, String message) {
		super(arg+" is set to '"+value+"'"+message);
		argumentName = arg;
		argumentValue = value;
	}
	
	/**
	 * Gets the name of the argument that is incorrectly
	 * formatted.
	 * 
	 * @return
	 * The argument.
	 */
	public String getArg() {
		return argumentName;
	}
	
	/**
	 * Gets the value of the argument that caused the error.
	 * 
	 * @return
	 * The value.
	 */
	public String getArgValue() {
		return argumentValue;
	}
}
