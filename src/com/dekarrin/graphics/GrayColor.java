package com.dekarrin.graphics;

import com.dekarrin.error.ValueOutOfRangeException;

/**
 * Represents a grayscale color at a specific bit depth. This is accomplished
 * by overriding parent methods so that every getter and setter accesses the
 * gray value instead of the colors.
 */
public class GrayColor extends Color {
	
	/**
	 * Creates a new GrayColor from a Color. The given Color's
	 * red value is used to calculate this GrayColor's grayscale
	 * value. The given Color's alpha value is taken as well.
	 * 
	 * @param color
	 * The color to use as the basis for this GrayColor.
	 */
	public GrayColor(Color color) {
		super(color.bitDepth());
		setValue(color.getRed());
		setAlpha(color.getAlpha());
	}
	
	/**
	 * Creates a new GrayColor at the specified bit depth. The
	 * new GrayColor will have the default value of 0 in every
	 * sample, but the alpha will be at the maximum.
	 *
	 * @param bitDepth
	 * The bit depth to set the color to.
	 */
	public GrayColor(int bitDepth) {
		super(bitDepth);
	}
	
	/**
	 * Creates a new GrayColor at the default bit depth of 8.
	 */
	public GrayColor() {
		this(8);
	}
	
	/**
	 * Sets the gray value for this GrayColor.
	 *
	 * @param value
	 * The new value to set the value to. This must be within
	 * the range of this GrayColor. The range is [0, 2^bitDepth-1].
	 *
	 * @throws ValueOutOfRangeException
	 * If the value is not within this GrayColor's range.
	 */
	public void setValue(int value) throws ValueOutOfRangeException {
		super.setRed(value);
		super.setGreen(value);
		super.setBlue(value);
	}
	
	/**
	 * Sets the gray value for this GrayColor.
	 *
	 * @param value
	 * The new value to set the value to. This must be within
	 * the range of this GrayColor. The range is [0, 2^bitDepth-1].
	 *
	 * @throws ValueOutOfRangeException
	 * If the value is not within this GrayColor's range.
	 */
	@Override
	public void setRed(int value) throws ValueOutOfRangeException {
		setValue(value);
	}
	
	/**
	 * Sets the gray value for this GrayColor.
	 *
	 * @param value
	 * The new value to set the value to. This must be within
	 * the range of this GrayColor. The range is [0, 2^bitDepth-1].
	 *
	 * @throws ValueOutOfRangeException
	 * If the value is not within this GrayColor's range.
	 */
	@Override
	public void setGreen(int value) throws ValueOutOfRangeException {
		setValue(value);
	}
	
	/**
	 * Sets the gray value for this GrayColor.
	 *
	 * @param value
	 * The new value to set the value to. This must be within
	 * the range of this GrayColor. The range is [0, 2^bitDepth-1].
	 *
	 * @throws ValueOutOfRangeException
	 * If the value is not within this GrayColor's range.
	 */
	@Override
	public void setBlue(int value) throws ValueOutOfRangeException {
		setValue(value);
	}
	
	/**
	 * Gets the gray value for this GrayColor.
	 *
	 * @return
	 * The gray value.
	 */
	public int getValue() {
		return getRed();
	}
}