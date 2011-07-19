package com.dekarrin.file.png;

import com.dekarrin.graphics.Color;
import com.dekarrin.graphics.GrayColor;
import com.dekarrin.util.ByteComposer;

/**
 * Holds information on original significant bits.
 */
public class SignificantBitsChunk extends AncillaryChunk {
	
	/**
	 * Significant bits in the original colors.
	 */
	private Color significantColorBits;
	
	/**
	 * Significant bits in the original alpha channel.
	 */
	private int significantAlphaBits;
	
	/**
	 * Creates a new SignificantBitsChunk.
	 *
	 * @param data
	 * The chunk data.
	 */
	public SignificantBitsChunk(byte[] data) {
		super(Chunk.sBIT, data);
		parseData();
	}
	
	/**
	 * Creates a new SignificantBitsChunk from existing data.
	 * 
	 * @param redBits
	 * The number of significant bits in the red channel.
	 *
	 * @param greenBits
	 * The number of significant bits in the green channel.
	 *
	 * @param blueBits
	 * The number of significant bits in the blue channel.
	 *
	 * @param alphaBits
	 * The number of significant bits in the alpha channel.
	 */
	public SignificantBitsChunk(int redBits, int greenBits, int blueBits, int alphaBits) {
		super(Chunk.sBIT);
		setProperties(redBits, greenBits, blueBits, alphaBits);
		setChunkData(createColorDataBytes());
	}
	
	/**
	 * Creates a new SignificantBitsChunk from existing data.
	 * 
	 * @param redBits
	 * The number of significant bits in the red channel.
	 *
	 * @param greenBits
	 * The number of significant bits in the green channel.
	 *
	 * @param blueBits
	 * The number of significant bits in the blue channel.
	 */
	public SignificantBitsChunk(int redBits, int greenBits, int blueBits) {
		super(Chunk.sBIT);
		setProperties(redBits, greenBits, blueBits, 0);
		setChunkData(createColorDataBytes());
	}
	
	/**
	 * Creates a new SignificantBitsChunk from existing data.
	 * 
	 * @param grayBits
	 * The number of significant bits in the grayscale data.
	 * 
	 * @param alphaBits
	 * The number of significant bits in the alpha channel.
	 */
	public SignificantBitsChunk(int grayBits, int alphaBits) {
		super(Chunk.sBIT);
		setProperties(grayBits, grayBits, grayBits, alphaBits);
		setChunkData(createGrayscaleDataBytes());
	}
	
	/**
	 * Creates a new SignificantBitsChunk from existing data.
	 * 
	 * @param grayBits
	 * The number of significant bits in the grayscale data.
	 */
	public SignificantBitsChunk(int grayBits) {
		super(Chunk.sBIT);
		setProperties(grayBits, grayBits, grayBits, 0);
		setChunkData(createGrayscaleDataBytes());
	}
	
	/**
	 * Gets the significant color bits.
	 *
	 * @return
	 * The significant bits in the colors.
	 */
	public Color getColorBits() {
		return significantColorBits;
	}
	
	/**
	 * Gets the significant alpha bits.
	 *
	 * @return
	 * The significant bits in the alpha.
	 */
	public int getAlphaBits() {
		return significantAlphaBits;
	}
	
	/**
	 * Parses chunk data into something usable.
	 */
	private void parseData() {
		int r,g,b,a;
		switch(getLength()) {
			// lengths are used here, so the cases cannot correspond to to the color
			// type constants.
			case 1: // grayscale
				g = parser.parseInt(1);
				setProperties(g, g, g, 0);
				break;
				
			case 2: // grayscale + alpha
				g = parser.parseInt(1);
				a = parser.parseInt(1);
				setProperties(g, g, g, a);
				break;
				
			case 3: // color
				r = parser.parseInt(1);
				g = parser.parseInt(1);
				b = parser.parseInt(1);
				setProperties(r, g, b, 0);
				break;
				
			case 4: // color + alpha
				r = parser.parseInt(1);
				g = parser.parseInt(1);
				b = parser.parseInt(1);
				a = parser.parseInt(1);
				setProperties(r, g, b, a);
				break;
		}
	}
	
	/**
	 * Sets the properties of significant bits. In order to
	 * set grayscale significant bits, just set the RGB
	 * significant bit parameters to the same values.
	 *
	 * @param redSigBits
	 * The number of significant bits in the red channel.
	 * 
	 * @param greenSigBits
	 * The number of significant bits in the green channel.
	 *
	 * @param blueSigBits
	 * The number of significant bits in the blue channel.
	 *
	 * @param alphaSigBits
	 * The number of significant bits in the alpha channel.
	 */
	private void setProperties(int redSigBits, int greenSigBits, int blueSigBits, int alphaSigBits) {
		significantColorBits = new Color();
		significantColorBits.setSamples(redSigBits, greenSigBits, blueSigBits);
		significantAlphaBits = alphaSigBits;
	}
	
	/**
	 * Creates the data bytes for grayscale-mode significant bits.
	 *
	 * @return
	 * The data bytes.
	 */
	private byte[] createGrayscaleDataBytes() {
		int dataLength = (significantAlphaBits == 0) ? 2 : 1; // there will never be a channel with 0 sig bits;
		ByteComposer composer = new ByteComposer(dataLength);
		composer.composeInt(((GrayColor)significantColorBits).getValue(), 1);
		if(dataLength > 1) {
			composer.composeInt(significantAlphaBits, 1);
		}
		return composer.toArray();
	}
	
	/**
	 * Creates the data bytes for color-mode significant bits.
	 *
	 * @return
	 * The data bytes.
	 */
	private byte[] createColorDataBytes() {
		int dataLength = (significantAlphaBits == 0) ? 4 : 3; // there will never be a channel with 0 sig bits;
		ByteComposer composer = new ByteComposer(dataLength);
		composer.composeInt(significantColorBits.getRed(), 1);
		composer.composeInt(significantColorBits.getGreen(), 1);
		composer.composeInt(significantColorBits.getBlue(), 1);
		if(dataLength > 3) {
			composer.composeInt(significantAlphaBits, 1);
		}
		return composer.toArray();
	}
}