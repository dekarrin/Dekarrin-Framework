/**
 * Represents a gamma chunk.
 */
class GammaChunk extends Chunk implements AncillaryChunk {

	/**
	 * The gamma stored in this chunk.
	 */
	private int gamma;
	
	/**
	 * Creates a new GammaChunk.
	 *
	 * @param data
	 * The chunk data.
	 *
	 * @param crc
	 * The chunk crc.
	 */
	public GammaChunk(byte[] data, int crc) {
		super(new byte[]{103, 65, 77, 65}, data, crc) // gAMA
		parseData();
	}
	
	/**
	 * Parses the chunk data into the content.
	 */
	private void parseData() {
		gamma = parseInt();
	}
	
}