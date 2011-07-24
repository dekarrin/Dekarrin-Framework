package com.dekarrin.graphics;

import com.dekarrin.error.RuntimeMessageException;

public class ColorFormatException extends RuntimeMessageException {
	
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
