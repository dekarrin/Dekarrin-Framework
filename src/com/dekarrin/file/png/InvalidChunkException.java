package com.dekarrin.file.png;

import com.dekarrin.error.MessageException;

/**
 * Represents an invalid chunk. This is typically thrown if a Png
 * Chunk generates a crc that does not match the CRC read from disk.
 */
public class InvalidChunkException extends MessageException {
	
	/**
	 * Creates a new InvalidChunkException with the specified
	 * message.
	 *
	 * @param message.
	 * The message held by this InvalidChunkException.
	 */
	public InvalidChunkException(String message) {
		super(message);
	}
}