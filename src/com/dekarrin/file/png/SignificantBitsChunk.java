package com.dekarrin.file.png;

import java.awt.Color;

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
	 *
	 * @param crc
	 * The chunk CRC.
	 */
	public SignificantBitsChunk(byte[] data, int crc) {
		super(new byte[]{115, 66, 73, 84}, data, crc); // sBIT
		parseData();
	}
	
	/**
	 * Gets the significant color bits.
	 *
	 * @returns
	 * The significant bits in the colors.
	 */
	public Color getColorBits() {
		return significantColorBits;
	}
	
	/**
	 * Gets the significant alpha bits.
	 *
	 * @returns
	 * The significant bits in the alpha.
	 */
	public byte getAlphaBits() {
		return significantAlphaBits;
	}
	
	/**
	 * Parses chunk data into something usable.
	 */
	private void parseData() {
		switch(getLength()) {
			case 1: // grayscale
				int g = parser.parseInt(1);
				significantColorBits = new Color(g, g, g);
				break;
				
			case 2: // grayscale + alpha
				int g = parser.parseInt(1);
				significantColorBits = new Color(g, g, g);
				significantAlphaBits = parser.parseInt(1);
				break;
				
			case 3: // color
				int r = parser.parseInt(1);
				int g = parser.parseInt(1);
				int b = parser.parseInt(1);
				significantColorBits = new Color(r, g, b);
				break;
				
			case 4: // color + alpha
				int r = parser.parseInt(1);
				int g = parser.parseInt(1);
				int b = parser.parseInt(1);
				significantColorBits = new Color(r, g, b);
				significantAlphaBits = parser.parseInt(1);
				break;
		}
	}
}