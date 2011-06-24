/**
 * Chunk data for a standard RGB color space.
 */
public class StandardRgbColorSpaceChunk extends AncillaryChunk {

	/**
	 * The rendering intent of this png.
	 */
	private int renderingIntent;
	
	/**
	 * Creates a new StandardRgbColorSpaceChunk. The supplied data is parsed.
	 *
	 * @param data
	 * The raw data in this chunk.
	 *
	 * @param crc
	 * The CRC for this chunk.
	 */
	public StandardRgbColorSpaceChunk(byte[] data, int crc) {
		super(new byte[]{115, 82, 71, 66}, data, crc); // sRGB
		parseData();
	}
	
	/**
	 * Gets the rendering intent.
	 *
	 * @returns
	 * The rendering intent.
	 */
	public int getRenderingIntent() {
		return renderingIntent;
	}
	
	/**
	 * Parses the chunk data.
	 */
	private void parseData() {
		renderingIntent = parser.parseInt(1);
	}
}