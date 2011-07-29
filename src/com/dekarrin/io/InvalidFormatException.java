package com.dekarrin.io;

/**
 * Exception indicating that the file being read is corrupt,
 * or that it is the wrong format.
 */
public class InvalidFormatException extends Exception {
	
	private static final long serialVersionUID = -5479990326795564244L;
	
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
	public InvalidFormatException(String message, String fileType) {
		super(message);
		this.fileType = fileType.toLowerCase();
	}
	
	/**
	 * Shows the message for this InvalidFileFormatException. The message
	 * contains information on both the file type and error.
	 *
	 * @return
	 * The message.
	 */
	public String getMessage() {
		String errorMessage = String.format("Bad '.%s' file! %s", fileType, super.getMessage());
		return errorMessage;
	}
}