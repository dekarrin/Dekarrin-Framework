package com.dekarrin.graphics;

import com.dekarrin.error.ValueOutOfRangeException;
import com.dekarrin.util.HelperString;

/**
 * Represents a color at a specific bit depth.
 */
public class Color {
	
	/**
	 * Holds the HSB/HSV values for this color. Once they
	 * have been calculated once, there's no reason to do
	 * so again unless they have been changed.
	 */
	private /* struct */ class HsbColorValues {
		public double hue = 0.0;
		public double saturation = 0.0;
		public double brightness = 0.0;
		public boolean hueIsSet = false;
		public boolean saturationIsSet = false;
		public boolean brightnessIsSet = false;
		public void setHue(double hue) {
			this.hue = hue;
			hueIsSet = true;
		}
		public void setSaturation(double saturation) {
			this.saturation = saturation;
			saturationIsSet = true;
		}
		public void setBrightness(double brightness) {
			this.brightness = brightness;
			brightnessIsSet = true;
		}
	}
	
	/**
	 * The values of this Color in the HSB/HSV color space.
	 */
	private HsbColorValues asHsb = new HsbColorValues(); 
	
	/**
	 * The bit depth of each sample.
	 */
	private int sampleDepth;
	
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
	 * The predefined Color for red. This Color has a sample
	 * depth of 8; it will need to be converted for use with
	 * systems that have different sample depths.
	 */
	public static final Color RED = new Color(8, 255, 0, 0);
	
	/**
	 * The predefined Color for orange. This Color has a sample
	 * depth of 8; it will need to be converted for use with
	 * systems that have different sample depths.
	 */
	public static final Color ORANGE = new Color(8, 255, 128, 0);
	
	/**
	 * The predefined Color for yellow. This Color has a sample
	 * depth of 8; it will need to be converted for use with
	 * systems that have different sample depths.
	 */
	public static final Color YELLOW = new Color(8, 255, 255, 0);
	
	/**
	 * The prefefined Color for green. This Color has a sample
	 * depth of 8; it will need to be converted for use with
	 * systems that have different sample depths.
	 */
	public static final Color GREEN = new Color(8, 0, 255, 0);
	
	/**
	 * The predefined Color for blue. This Color has a sample
	 * depth of 8; it will need to be converted for use with
	 * systems that have different sample depths.
	 */
	public static final Color BLUE = new Color(8, 0, 0, 255);
	
	/**
	 * The predefined Color for violet. This Color has a sample
	 * depth of 8; it will need to be converted for use with
	 * systems that have different sample depths.
	 */
	public static final Color PURPLE = new Color(8, 128, 0, 128);
	
	/**
	 * The predefined Color for brown. This Color has a sample
	 * depth of 8; it will need to be converted for use with
	 * systems that have different sample depths.
	 */
	public static final Color BROWN = new Color(8, 150, 75, 0);
	
	/**
	 * The predefined Color for black. This Color has a sample
	 * depth of 8; it will need to be converted for use with
	 * systems that have different sample depths.
	 */
	public static final Color BLACK = new Color(8, 0, 0, 0);
	
	/**
	 * The predefined Color for gray. This Color has a sample
	 * depth of 8; it will need to be converted for use with
	 * systems that have different sample depths.
	 */
	public static final Color GRAY = new Color(8, 128, 128, 128);
	
	/**
	 * The predefined Color for white. This Color has a sample
	 * depth of 8; it will need to be converted for use with
	 * systems that have different sample depths.
	 */
	public static final Color WHITE = new Color(8, 255, 255, 255);
	
	/**
	 * Gets color values as an HTML-notation hex string.
	 * 
	 * @param r
	 * The red value.
	 * 
	 * @param g
	 * The green value.
	 * 
	 * @param b
	 * The blue value.
	 * 
	 * @param a
	 * The alpha value.
	 * 
	 * @param depth
	 * The depth in bits of each sample.
	 * 
	 * @return
	 * The hex color string that represents the given values.
	 */
	public static String hexValue(int r, int g, int b, int a, int depth) {
		String red = valueToHex(r, depth);
		String green = valueToHex(g, depth);
		String blue = valueToHex(b, depth);
		String alpha = valueToHex(a, depth);
		String hex = String.format("#%s%s%s%s", red, green, blue, alpha);
		return hex;
	}
	
	/**
	 * Gets color values as an HTML-notation hex string.
	 * 
	 * @param r
	 * The red value.
	 * 
	 * @param g
	 * The green value.
	 * 
	 * @param b
	 * The blue value.
	 * 
	 * @param depth
	 * The depth in bits of each sample.
	 * 
	 * @return
	 * The hex color string that represents the given values.
	 */
	public static String hexValue(int r, int g, int b, int depth) {
		String red = valueToHex(r, depth);
		String green = valueToHex(g, depth);
		String blue = valueToHex(b, depth);
		String hex = String.format("#%s%s%s", red, green, blue);
		return hex;
	}
	

	/**
	 * Gets a color from a String containing some sort of color
	 * representation. Currently only implemented for # hex value
	 * strings.
	 * 
	 * @param colorString
	 * The representation of this Color.
	 * 
	 * @param depth
	 * The sample depth of the Color to create.
	 * 
	 * @return
	 * The Color created from the String.
	 */
	public static Color parseColor(String colorString, int depth) {
		if(!colorString.matches("#[0-9a-fA-F]+")) {
			throw new ColorFormatException("Color must be a hex code preceeded by '#'");
		}
		Color parsedColor = new Color(depth);
		String colorCode = colorString.substring(1);
		if(colorCode.length() % 3 == 0) {
			int interval = colorCode.length() / 3;
			String rString = colorCode.substring(0, interval);
			String gString = colorCode.substring(interval, interval*2);
			String bString = colorCode.substring(interval*2, interval*3);
			int r = hexToValue(rString);
			int g = hexToValue(gString);
			int b = hexToValue(bString);
			try {
				parsedColor.setSamples(r, g, b);
			} catch(ValueOutOfRangeException e) {
				throw new ColorFormatException(e.getMessage());
			}
		} else {
			int value = hexToValue(colorCode);
			try {
				parsedColor.setSamples(value, value, value);
			} catch(ValueOutOfRangeException e) {
				throw new ColorFormatException(e.getMessage());
			}
		}
		return parsedColor;
	}
	
	/**
	 * Creates a new Color at the specified bit depth. The
	 * new Color will have the default value of 0 in every
	 * sample, but the alpha will be at the maximum.
	 *
	 * @param sampleDepth
	 * The bit depth to set the color to.
	 */
	public Color(int sampleDepth) {
		this(sampleDepth, 0, 0, 0);
	}
	
	/**
	 * Creates a new Color with a maximum alpha.
	 *
	 * @param sampleDepth
	 * The bit depth to set the color to.
	 *
	 * @param r
	 * The red value of the new Color.
	 *
	 * @param g
	 * The green value of the new Color.
	 *
	 * @param b
	 * The blue value of the new Color.
	 */
	public Color(int sampleDepth, int r, int g, int b) {
		this.sampleDepth = sampleDepth;
		setSamples(r, g, b, maximumValue());
	}
	
	/**
	 * Creates a new Color.
	 *
	 * @param sampleDepth
	 * The bit depth to set the color to.
	 *
	 * @param r
	 * The red value of the new Color.
	 *
	 * @param g
	 * The green value of the new Color.
	 *
	 * @param b
	 * The blue value of the new Color.
	 *
	 * @param a
	 * The value of the alpha sample.
	 */
	public Color(int sampleDepth, int r, int g, int b, int a) {
		this.sampleDepth = sampleDepth;
		setSamples(r, g, b, a);
	}
	
	/**
	 * Creates a new Color at the default bit depth of 8.
	 */
	public Color() {
		this(8);
	}
	
	/**
	 * Checks if a color has samples that are
	 * equivalent to this Color's values.
	 *
	 * @param color
	 * The Color to compare this one with.
	 *
	 * @return
	 * Whether the given color has the same samples
	 * as this one.
	 */
	public boolean equals(Color color) {
		return equals(color, 0);
	}
	
	/**
	 * Checks if a Color represents the same color as this Color.
	 * 
	 * @param color
	 * The Color to compare this one with.
	 * 
	 * @param tolerance
	 * An index that specifies how much variation is allowed. This
	 * ranges from 0 to 1. 0 specifies no variation, while 1 specifies
	 * all variation; i.e, a tolerance of 1 specifies an allowed
	 * variation so great such that every Color will be equal to this
	 * one, while 0 specifies an allowed variation such that only
	 * Colors that have the exact same values as this one will be
	 * equal to this one.
	 * 
	 * @return
	 * Whether the given Color has the same values as this one within
	 * the specified amount of tolerance.
	 */
	public boolean equals(Color color, double tolerance) {
		double maximumDifference = 4.0; // the greatest variance, black opaque to white transparent.
		double r1,g1,b1,a1,r2,g2,b2,a2;
		r1 = this.sampleAsFactor(red);
		g1 = this.sampleAsFactor(green);
		b1 = this.sampleAsFactor(blue);
		a1 = this.sampleAsFactor(alpha);
		r2 = getDepthFactor(color.getRed(), color.sampleDepth());
		g2 = getDepthFactor(color.getGreen(), color.sampleDepth());
		b2 = getDepthFactor(color.getBlue(), color.sampleDepth());
		a2 = getDepthFactor(color.getAlpha(), color.sampleDepth());
		double rDiff = Math.abs(r1 - r2);
		double gDiff = Math.abs(g1 - g2);
		double bDiff = Math.abs(b1 - b2);
		double aDiff = Math.abs(a1 - a2);
		double totalDiff = rDiff + gDiff + bDiff + aDiff;
		return (totalDiff / maximumDifference <= tolerance);
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
	 * Resets the alpha sample to maximum.
	 */
	public void resetAlpha() {
		setAlpha(maximumValue());
	}
	
	/**
	 * Gets the bit depth that this Color is set to.
	 *
	 * @return
	 * The bit depth.
	 */
	public int sampleDepth() {
		return sampleDepth;
	}
	
	/**
	 * Changes the bit depth of this color. The sample values
	 * are changed into whatever value at the new bit depth is
	 * closest to the sample value at the old bit depth.
	 *
	 * @param sampleDepth
	 * The bit depth to change this Color to.
	 */
	public void changeBitDepth(int sampleDepth) {
		convertSampleBitDepths(sampleDepth);
		this.sampleDepth = sampleDepth;
	}
	
	/**
	 * Gets the maximum possible value for any sample in this
	 * color. This is calculated from the bit depth.
	 *
	 * @return
	 * The maximum possible value.
	 */
	public int maximumValue() {
		return maximumValue(sampleDepth);
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
	 * the range of this Color. The range is [0, 2^sampleDepth-1].
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
	 * the range of this Color. The range is [0, 2^sampleDepth-1].
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
	 * the range of this Color. The range is [0, 2^sampleDepth-1].
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
	 * the range of this Color. The range is [0, 2^sampleDepth-1].
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
	 * Gets the hue of this Color.
	 * 
	 * @return
	 * The hue.
	 */
	public double hue() {
		if(this.asHsb.hueIsSet) {
			return this.asHsb.hue;
		}
		double r = sampleAsFactor(red);
		double g = sampleAsFactor(green);
		double b = sampleAsFactor(blue);
		double minRgb = Math.min(r, Math.min(g, b));
		double maxRgb = Math.max(r, Math.max(g, b));
		double deltaRgb = maxRgb - minRgb;
		double hue = 0;
		if(maxRgb != 0) {
			double deltaR = 60*(maxRgb - r)/deltaRgb + 180;
			double deltaG = 60*(maxRgb - g)/deltaRgb + 180;
			double deltaB = 60*(maxRgb - b)/deltaRgb + 180;
			
			if(r == maxRgb) {
				hue = deltaB - deltaG;
			} else if(g == maxRgb) {
				hue = 120 + deltaR - deltaB;
			} else {
				hue = 240 + deltaG - deltaR;
			}
		}
		if(hue < 0) {
			hue += 360;
		}
		if(hue >= 360) {
			hue -= 360;
		}
		this.asHsb.setHue(hue); 
		return hue;
	}
	
	/**
	 * Gets the saturation of this color.
	 * 
	 * @return
	 * The saturation.
	 */
	public double saturation() {
		if(this.asHsb.saturationIsSet) {
			return this.asHsb.saturation;
		}
		double r = sampleAsFactor(red);
		double g = sampleAsFactor(green);
		double b = sampleAsFactor(blue);
		double minRgb = Math.min(r, Math.min(g, b));
		double maxRgb = Math.max(r, Math.max(g, b));
		double deltaRgb = maxRgb - minRgb;
		double saturation = 0;
		if(maxRgb != 0) {
			saturation = deltaRgb / maxRgb;
		}
		this.asHsb.setSaturation(saturation);
		return saturation;
	}
	
	/**
	 * Gets the brightness / value of this color.
	 * 
	 * @return
	 * The brightness.
	 */
	public double brightness() {
		if(this.asHsb.brightnessIsSet) {
			return this.asHsb.brightness;
		}
		double r = sampleAsFactor(red);
		double g = sampleAsFactor(green);
		double b = sampleAsFactor(blue);
		double brightness = Math.max(r, Math.max(g, b));
		this.asHsb.setBrightness(brightness);
		return brightness;
	}
	
	/**
	 * Sets the hue of this Color.
	 * 
	 * @param hue
	 * The hue in degrees.
	 */
	public void setHue(double hue) {
		setSamplesFromHsb(hue, saturation(), brightness());
		this.asHsb.setHue(hue);
	}
	
	/**
	 * Sets the saturation of this Color.
	 * 
	 * @param saturation
	 * The saturation as a factor in the range 0..1.
	 */
	public void setSaturation(double saturation) {
		setSamplesFromHsb(hue(), saturation, brightness());
		this.asHsb.setSaturation(saturation);
	}
	
	/**
	 * Sets the brightness/value of this Color.
	 * 
	 * @param brightness
	 * The brightness as a factor in the range 0..1.
	 */
	public void setBrightness(double brightness) {
		setSamplesFromHsb(hue(), saturation(), brightness);
		this.asHsb.setBrightness(brightness);
	}
	
	/**
	 * Sets the RGB samples to values by converting HSB values.
	 * 
	 * @param hue
	 * The color to change it to. This must be specified in degrees.
	 * Values that fall outside of the range 0..360 will be
	 * converted using mod 360.
	 * 
	 * @param saturation
	 * How much color there is. This ranges from 0..1.
	 * 
	 * @param brightness
	 * The intensity of the color. Also known as 'value'. This
	 * ranges from 0..1.
	 */
	public void setSamplesFromHsb(double hue, double saturation, double brightness) {
		hue = hue % 360;
		if(saturation < 0.0 || saturation > 1.0) {
			String unformatted = "Attempted to set saturation to %d; allowed range is 0-1";
			throw new ValueOutOfRangeException(String.format(unformatted, saturation));
		}
		if(brightness < 0.0 || brightness > 1.0) {
			String unformatted = "Attempted to set brightness to %d; allowed range is 0-1";
			throw new ValueOutOfRangeException(String.format(unformatted, brightness));
		}
		double chroma = brightness * saturation;
		double huePrime = hue / 60;
		double secondComponent = chroma * (1.0 - Math.abs((huePrime % 2.0) - 1.0));
		int hueSectionIndex = (int)Math.floor(huePrime);
		double rInitial, gInitial, bInitial;
		rInitial = gInitial = bInitial = 0;
		switch(hueSectionIndex) {
			case 0:
				rInitial = chroma;
				gInitial = secondComponent;
				break;
				
			case 1:
				rInitial = secondComponent;
				gInitial = chroma;
				break;
				
			case 2:
				gInitial = chroma;
				bInitial = secondComponent;
				break;
				
			case 3:
				gInitial = secondComponent;
				bInitial = chroma;
				break;
				
			case 4:
				rInitial = secondComponent;
				bInitial = chroma;
				break;
				
			case 5:
				rInitial = chroma;
				bInitial = secondComponent;
				break;
		}
		double brightnessDelta = brightness - chroma;
		int rFinal,gFinal,bFinal;
		rFinal = convertSample(rInitial + brightnessDelta, sampleDepth);
		gFinal = convertSample(gInitial + brightnessDelta, sampleDepth);
		bFinal = convertSample(bInitial + brightnessDelta, sampleDepth);
		setSamples(rFinal, gFinal, bFinal);
	}
	
	/**
	 * Gets the color values as an HTML-notation hex
	 * string.
	 * 
	 * @param withAlpha
	 * Whether or not to include the alpha as a fourth
	 * value.
	 * 
	 * @return
	 * The hex color string that represents this Color.
	 */
	public String hexValue(boolean withAlpha) {
		String r = valueToHex(red, sampleDepth);
		String g = valueToHex(green, sampleDepth);
		String b = valueToHex(blue, sampleDepth);
		String a = "";
		if(withAlpha) {
			a = valueToHex(alpha, sampleDepth);
		}
		String hex = String.format("#%s%s%s%s", r, g, b, a);
		return hex;
	}
	
	/**
	 * Converts a sample to a double value, where 1 is the maximum
	 * value for this Color's bit depth, and 0 is equivalent to 0.
	 * 
	 * @param sample
	 * The sample to convert
	 * 
	 * @return
	 * The sample as a factor from 0..1.
	 */
	private double sampleAsFactor(int sample) {
		return getDepthFactor(sample, sampleDepth);
	}
	
	/**
	 * Changes the sample values into their equivalents at the new
	 * bit depth.
	 *
	 * @param newBitDepth
	 * The bit depth that the samples are to be converted to.
	 */
	private void convertSampleBitDepths(int newBitDepth) {
		double rFactor, gFactor, bFactor, aFactor;
		rFactor = getDepthFactor(red, sampleDepth);
		gFactor = getDepthFactor(green, sampleDepth);
		bFactor = getDepthFactor(blue, sampleDepth);
		aFactor = getDepthFactor(alpha, sampleDepth);
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
	 * @param sampleDepth
	 * The bit depth to get the factor at.
	 *
	 * @return
	 * The sample factor at the specified bit depth.
	 */
	private double getDepthFactor(int sample, int sampleDepth) {
		double factor = (double)sample / (double)maximumValue(sampleDepth);
		return factor;
	}
	
	/**
	 * Finds the maximum value at a specific bit depth.
	 *
	 * @param sampleDepth
	 * The bit depth to get the maximum value for.
	 *
	 * @return
	 * The maximum value of a sample at the specified bit depth.
	 */
	private int maximumValue(int sampleDepth) {
		int maxValue = (int)Math.pow(2, sampleDepth) - 1;
		return maxValue;
	}
	
	/**
	 * Converts a sample factor to its new value for a bit depth.
	 *
	 * @param factor
	 * The intensity factor of the sample. This ranges from 0.0-1.0
	 *
	 * @param sampleDepth
	 * The bit depth to convert the sample to.
	 *
	 * @return
	 * The calculated value of the sample.
	 */
	private int convertSample(double factor, int sampleDepth) {
		int sampleValue = (int)(factor * maximumValue(sampleDepth));
		return sampleValue;
	}
	
	/**
	 * Converts a single color value into its hex equivalent.
	 * 
	 * @param value
	 * The color to get the hex value for.
	 * 
	 * @param sampleDepth
	 * The sample depth of the color, specified in bits.
	 * 
	 * @return
	 * The hex String for the given color.
	 */
	private static String valueToHex(int value, int sampleDepth) {
		int paddingLevel = (int)Math.ceil((double)sampleDepth / 4);
		String unpadded = Integer.toString(value, 16);
		HelperString padded = new HelperString(unpadded);
		padded.padLeft(paddingLevel, '0');
		return padded.toString();
	}
	
	/**
	 * Converts a hex value into the color value it represents.
	 * 
	 * @param hexValue
	 * The hex value to get the color value for.
	 * 
	 * @return
	 * The color value for the given hex string.
	 */
	private static int hexToValue(String hex) {
		int value = Integer.parseInt(hex, 16);
		return value;
	}
}