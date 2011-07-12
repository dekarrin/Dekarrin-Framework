package com.dekarrin.file.png;

import com.dekarrin.error.MessageException;

/**
 * Represents an unknown chunk. This is typically thrown if an
 * operation that requires a known chunk encounters a chunk
 * whose type is unrecognized.
 */
public class UnknownChunkException extends MessageException {
	
	/**
	 * The type of the chunk that was encountered.
	 */
	private String type;
	
	/**
	 * Creates a new UnknownChunkException with the specified
	 * message.
	 *
	 * @param message
	 * The message held by this UnknownChunkException.
	 *
	 * @param type
	 * The type of the chunk encountered.
	 */
	public UnknownChunkException(String message, String type) {
		super(message);
		this.type = type;
	}
	
	/**
	 * Gets the type of the chunk that was encountered.
	 *
	 * @return
	 * The chunk type.
	 */
	public String getType() {
		return type;
	}
}