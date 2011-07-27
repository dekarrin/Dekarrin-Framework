package com.dekarrin.script;

/**
 * Thrown when an argument is not properly formatted as
 * a real number.
 */
public class RealArgumentException extends ArgumentFormatException {
	
	private static final long serialVersionUID = -5700774411476285272L;

	/**
	 * Creates a new RealArgumentException.
	 * 
	 * @param arg
	 * The argument that caused the error.
	 * 
	 * @param value
	 * The value of the argument that caused the error.
	 */
	public RealArgumentException(String arg, String value) {
		super(arg, value, "; it must be an real number");
	}
}
