/**
 * Chunk for image trailer.
 */
public class TrailerChunk extends CriticalChunk {
	
	/**
	 * Creates a new TrailerChunk.
	 *
	 * @param data
	 * The data in the chunk.
	 *
	 * @param crc
	 * The CRC for this chunk.
	 */
	public TrailerChunk(byte[] data, int crc) {
		super(new byte[]{73, 69, 78, 68}, data, crc);// IEND
	}
}