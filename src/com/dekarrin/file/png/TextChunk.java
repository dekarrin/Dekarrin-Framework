/**
 * Chunk that represents text data.
 */
public abstract class TextChunk extends AncillaryChunk {
	
	/**
	 * The keyword of this text data.
	 */
	protected String keyword;
	
	/**
	 * The text in this chunk.
	 */
	protected String text;
	
	/**
	 * Creates a new TextChunk.
	 *
	 * @param type
	 * The type of this chunk.
	 *
	 * @param data
	 * The data of this chunk.
	 *
	 * @param crc
	 * The CRC for the chunk.
	 */
	public TextChunk(byte[] type, byte[] data, int crc) {
		super(type, data, crc);
	}
	
	/**
	 * Retrieves the keyword from this chunk.
	 *
	 * @returns
	 * The keyword.
	 */
	protected String getKeyword() {
		return keyword;
	}
	
	/**
	 * Retrieves the actual text data from this chunk.
	 *
	 * @returns
	 * The text data.
	 */
	public abstract String getText();
}