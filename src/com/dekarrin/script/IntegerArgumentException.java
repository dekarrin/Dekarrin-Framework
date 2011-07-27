package com.dekarrin.script;

/**
 * Thrown when an argument is not properly formated as an
 * integer.
 */
public class IntegerArgumentException extends ArgumentFormatException {

	private static final long serialVersionUID = 6100553136041458383L;

	/**
	 * Creates a new IntegerArgumentException.
	 * 
	 * @param arg
	 * The argument that caused the error.
	 * 
	 * @param value
	 * The value of the argument that caused the error.
	 */
	public IntegerArgumentException(String arg, String value) {
		super(arg, value, "; it must be an integer");
	}
}
