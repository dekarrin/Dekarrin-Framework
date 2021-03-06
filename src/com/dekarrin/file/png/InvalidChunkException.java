package com.dekarrin.file.png;

/**
 * Represents an invalid chunk. This is typically thrown if a PNG
 * Chunk generates a CRC that does not match the CRC read from disk.
 */
public class InvalidChunkException extends Exception {
	
	private static final long serialVersionUID = 3392588602015984994L;

	/**
	 * Creates a new InvalidChunkException with the specified
	 * message.
	 *
	 * @param message
	 * The message held by this InvalidChunkException.
	 */
	public InvalidChunkException(String message) {
		super(message);
	}
}