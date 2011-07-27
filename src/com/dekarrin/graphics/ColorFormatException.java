package com.dekarrin.graphics;

public class ColorFormatException extends NumberFormatException {
	
	private static final long serialVersionUID = -5739773093310247748L;

	/**
	 * Creates a new ColorFormatException with a message.
	 * 
	 * @param message
	 * The message to give for a call to getMessage().
	 */
	public ColorFormatException(String message) {
		super(message);
	}
}
