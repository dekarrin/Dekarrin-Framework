package com.dekarrin.io;

/**
 * Exception indicating that the file being read is corrupt,
 * or that it is the wrong format.
 */
public class InvalidFileFormatException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5479990326795564244L;

	/**
	 * The message of this InvalidFileFormatException, returned by
	 * getMessage().
	 */
	private String message;
	
	/**
	 * The requested file type. This may not be the actual
	 * type of the file, but it is the one that the program
	 * expected.
	 */
	private String fileType;
	
	/**
	 * Creates a new InvalidFileFormatException with a specified message.
	 *
	 * @param message
	 * The message to assign to this InvalidFileFormatException. This
	 * will be returned when getMessage() is called on this object.
	 *
	 * @param fileType
	 * The type of file that caused this exception to be thrown.
	 */
	public InvalidFileFormatException(String message, String fileType) {
		this.message = message;
		this.fileType = fileType;
	}
	
	/**
	 * Shows the message for this InvalidFileFormatException. The message
	 * contains information on both the file type and error.
	 *
	 * @return
	 * The message.
	 */
	public String getMessage() {
		String errorMessage = String.format("Bad %s file! %s", fileType, message);
		return errorMessage;
	}
}