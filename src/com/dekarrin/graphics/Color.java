package com.dekarrin.graphics;

import com.dekarrin.error.ValueOutOfRangeException;

/**
 * Represents a color at a specific bit depth.
 */
public class Color {
	
	/**
	 * The bit depth of each sample.
	 */
	private int bitDepth;
	
	/**
	 * The red sample for this color.
	 */
	private int red;
	
	/**
	 * The green sample for this color.
	 */
	private int green;
	
	/**
	 * The blue sample for this color.
	 */
	private int blue;
	
	/**
	 * The alpha sample for this color.
	 */
	private int alpha;
	
	/**
	 * Creates a new Color at the specified bit depth. The
	 * new Color will have the default value of 0 in every
	 * sample, but the alpha will be at the maximum.
	 *
	 * @param bitDepth
	 * The bit depth to set the color to.
	 */
	public Color(int bitDepth) {
		this.bitDepth = bitDepth;
		setSamples(0, 0, 0, maximumValue());
	}
	
	/**
	 * Creates a new Color at the default bit depth of 8.
	 */
	public Color() {
		this(8);
	}
	
	/**
	 * Sets all the color samples of this Color in one method.
	 *
	 * @param red
	 * The value of the red sample.
	 *
	 * @param green
	 * The value of the green sample.
	 *
	 * @param blue
	 * The value of the blue sample.
	 */
	public void setSamples(int red, int green, int blue) {
		setRed(red);
		setGreen(green);
		setBlue(blue);
	}
	
	/**
	 * Sets all the samples of this Color in one method.
	 *
	 * @param red
	 * The value of the red sample.
	 *
	 * @param green
	 * The value of the green sample.
	 *
	 * @param blue
	 * The value of the blue sample.
	 *
	 * @param alpha
	 * The value of the alpha sample.
	 */
	public void setSamples(int red, int green, int blue, int alpha) {
		setRed(red);
		setGreen(green);
		setBlue(blue);
		setAlpha(alpha);
	}
	
	/**
	 * Gets the bit depth that this Color is set to.
	 *
	 * @return
	 * The bit depth.
	 */
	public int bitDepth() {
		return bitDepth;
	}
	
	/**
	 * Changes the bit depth of this color. The sample values
	 * are changed into whatever value at the new bit depth is
	 * closest to the sample value at the old bit depth.
	 *
	 * @param bitDepth
	 * The bit depth to change this Color to.
	 */
	public void changeBitDepth(int bitDepth) {
		convertSampleBitDepths(bitDepth);
		this.bitDepth = bitDepth;
	}
	
	/**
	 * Gets the maximum possible value for any sample in this
	 * color. This is calculated from the bit depth.
	 *
	 * @return
	 * The maximum possible value.
	 */
	public int maximumValue() {
		return maximumValue(bitDepth);
	}
	
	/**
	 * Gets the red value of this color.
	 *
	 * @return
	 * The red value.
	 */
	public int getRed() {
		return red;
	}
	
	/**
	 * Gets the green value of this Color.
	 *
	 * @return
	 * The green value.
	 */
	public int getGreen() {
		return green;
	}
	
	/**
	 * Gets the blue value of this Color.
	 *
	 * @return
	 * The blue value.
	 */
	public int getBlue() {
		return blue;
	}
	
	/**
	 * Gets the alpha value of this Color.
	 *
	 * @return
	 * The alpha value.
	 */
	public int getAlpha() {
		return alpha;
	}
	
	/**
	 * Sets the red value for this Color.
	 *
	 * @param value
	 * The new value to set the red to. This must be within
	 * the range of this Color. The range is [0, 2^bitDepth-1].
	 *
	 * @throws ValueOutOfRangeException
	 * If the value is not within this Color's range.
	 */
	public void setRed(int value) throws ValueOutOfRangeException {
		if(value < 0 || value > maximumValue()) {
			String unformatted = "Attempted to set red value to %d; allowed range is 0-%d";
			throw new ValueOutOfRangeException(String.format(unformatted, value, maximumValue()));
		}
		red = value;
	}
	
	/**
	 * Sets the green value for this Color.
	 *
	 * @param value
	 * The new value to set the green to. This must be within
	 * the range of this Color. The range is [0, 2^bitDepth-1].
	 *
	 * @throws ValueOutOfRangeException
	 * If the value is not within this Color's range.
	 */
	public void setGreen(int value) throws ValueOutOfRangeException {
		if(value < 0 || value > maximumValue()) {
			String unformatted = "Attempted to set green value to %d; allowed range is 0-%d";
			throw new ValueOutOfRangeException(String.format(unformatted, value, maximumValue()));
		}
		green = value;
	}
	
	/**
	 * Sets the blue value for this Color.
	 *
	 * @param value
	 * The new value to set the blue to. This must be within
	 * the range of this Color. The range is [0, 2^bitDepth-1].
	 *
	 * @throws ValueOutOfRangeException
	 * If the value is not within this Color's range.
	 */
	public void setBlue(int value) throws ValueOutOfRangeException {
		if(value < 0 || value > maximumValue()) {
			String unformatted = "Attempted to set blue value to %d; allowed range is 0-%d";
			throw new ValueOutOfRangeException(String.format(unformatted, value, maximumValue()));
		}
		blue = value;
	}
	
	/**
	 * Sets the alpha value for this Color.
	 *
	 * @param value
	 * The new value to set the alpha to. This must be within
	 * the range of this Color. The range is [0, 2^bitDepth-1].
	 *
	 * @throws ValueOutOfRangeException
	 * If the value is not within this Color's range.
	 */
	public void setAlpha(int value) throws ValueOutOfRangeException {
		if(value < 0 || value > maximumValue()) {
			String unformatted = "Attempted to set alpha value to %d; allowed range is 0-%d";
			throw new ValueOutOfRangeException(String.format(unformatted, value, maximumValue()));
		}
		alpha = value;
	}
	
	/**
	 * Changes the sample values into their equivelents at the new
	 * bit depth.
	 *
	 * @param newBitDepth
	 * The bit depth that the samples are to be converted to.
	 */
	private void convertSampleBitDepths(int newBitDepth) {
		double rFactor, gFactor, bFactor, aFactor;
		rFactor = getDepthFactor(red, bitDepth);
		gFactor = getDepthFactor(green, bitDepth);
		bFactor = getDepthFactor(blue, bitDepth);
		aFactor = getDepthFactor(alpha, bitDepth);
		red = convertSample(rFactor, newBitDepth);
		green = convertSample(gFactor, newBitDepth);
		blue = convertSample(bFactor, newBitDepth);
		alpha = convertSample(aFactor, newBitDepth);
	}
	
	/**
	 * Finds the bit depth factor for a sample at a bit depth.
	 *
	 * @param sample
	 * The sample to get the factor of.
	 *
	 * @param bitDepth
	 * The bit depth to get the factor at.
	 *
	 * @return
	 * The sample factor at the specified bit depth.
	 */
	private double getDepthFactor(int sample, int bitDepth) {
		double factor = (double)sample / (double)maximumValue(bitDepth);
		return factor;
	}
	
	/**
	 * Finds the maximum value at a specific bit depth.
	 *
	 * @param bitDepth
	 * The bit depth to get the maximum value for.
	 *
	 * @return
	 * The maximum value of a sample at the specified bit depth.
	 */
	private int maximumValue(int bitDepth) {
		int maxValue = (int)Math.pow(2, bitDepth) - 1;
		return maxValue;
	}
	
	/**
	 * Converts a sample factor to its new value for a bit depth.
	 *
	 * @param factor
	 * The intensity factor of the sample. This ranges from 0.0-1.0
	 *
	 * @param bitDepth
	 * The bit depth to convert the sample to.
	 *
	 * @return
	 * The calculated value of the sample.
	 */
	private int convertSample(double factor, int bitDepth) {
		int sampleValue = (int)(factor * maximumValue(bitDepth));
		return sampleValue;
	}
}