package com.dekarrin.graphics;

import com.dekarrin.util.ArrayHelper;

/**
 * Performs manipulations on an image.
 */
public class ImageManipulator {

	/**
	 * The image to perform the operations on.
	 */
	private Image image;
	
	/**
	 * Creates a new ImageManipulator for an Image.
	 * 
	 * @param image
	 * The image to perform the operations on.
	 */
	public ImageManipulator(Image image) {
		this.image = image;
	}
	
	/**
	 * Gets the image from this ImageManipulator.
	 * 
	 * @return
	 * The image being operated on.
	 */
	public Image getImage() {
		return image;
	}
	
	/**
	 * Replaces a color in the image.
	 * 
	 * @param replacedColor
	 * The color to replace.
	 * 
	 * @param newColor
	 * The color to swap out with the old one.
	 * 
	 * @param tolerance
	 * How much the colors to be replaced can
	 * vary from replacedColor. This ranges between 0 and
	 * 1, with 0 specifying no variance at all and 1
	 * specifying all variances.
	 */
	public void replaceColor(Color replacedColor, Color newColor, double tolerance) {
		Color c;
		for(int x = 0; x < image.width; x++) {
			for(int y = 0; y < image.height; y++) {
				c = image.getColorAt(x, y);
				if(c.equals(replacedColor, tolerance)) {
					image.setColorAt(x, y, newColor);
				}
			}
		}
	}
	
	/**
	 * Makes the image monochrome.
	 * 
	 * @param monoColor
	 * The monochrome color that all hues should be set to.
	 */
	public void monochrome(Color monoColor) {
		double hue = monoColor.hue();
		Color c;
		for(int x = 0; x < image.width; x++) {
			for(int y = 0; y < image.height; y++) {
				c = image.getColorAt(x, y);
				c.setHue(hue);
				image.setColorAt(x, y, c);
			}
		}
	}
	
	/**
	 * Makes the image black and white.
	 */
	public void desaturate() {
		Color c;
		for(int x = 0; x < image.width; x++) {
			for(int y = 0; y < image.height; y++) {
				c = image.getColorAt(x, y);
				c.setSaturation(0.0);
				image.setColorAt(x, y, c);
			}
		}
	}
	
	/**
	 * Makes the image sepia-toned.
	 */
	public void sepia() {
		Color sepia = new Color(image.bitDepth);
		sepia.setSamples(112, 66, 20);
		monochrome(sepia);
	}
	
	/**
	 * Pixelates the image.
	 * 
	 * @param scale
	 * The scale factor to pixelate the image by.
	 */
	public void pixelate(int scale) {
		expandImageToDivisibility(scale);
		Color[] pixelColors = new Color[scale * scale];
		double[] pixelWeights = new double[scale * scale];
		Color average;
		for(int y = 0; y < image.height; y += scale) {
			for(int x = 0; x < image.width; x += scale) {
				// get all the colors in the group; assign a weight
				for(int i = 0; i < scale && y+i < image.height; i++) {
					for(int j = 0; j < scale && x+j < image.width; j++) {
						pixelColors[i*scale+j] = image.getColorAt(x+j, y+i);
						pixelWeights[i*scale+j] = getPixelWeight(j, i, scale, scale);
					}
				}
				average = average(pixelColors, pixelWeights);
				// sampled all of them, now go back through and set the pixels.
				for(int i = 0; i < scale && y+i < image.height; i++) {
					for(int j = 0; j < scale && x+j < image.width; j++) {
						image.setColorAt(x+j, y+i, average);
					}
				}
			}
		}
	}
	
	/**
	 * Gets the average color between several others.
	 * 
	 * @param colors
	 * The colors to average together.
	 * 
	 * @param weights
	 * How much weight each color has.
	 */
	private Color average(Color[] colors, double[] weights) {
		double weightSum = ArrayHelper.sum(weights);
		double r=0,g=0,b=0,a=0;
		for(int i = 0; i < colors.length; i++) {
			r += ((colors[i].getRed()*weights[i]) / weightSum);
			g += ((colors[i].getGreen()*weights[i]) / weightSum);
			b += ((colors[i].getBlue()*weights[i]) / weightSum);
			a += ((colors[i].getAlpha()*weights[i]) / weightSum);
		}
		Color avgColor = new Color(image.bitDepth);
		avgColor.setSamples((int)r, (int)g, (int)b, (int)a);
		return avgColor;
	}
	
	/**
	 * Gets the weight of a pixel based on its x, y position.
	 * The weight is calculated using a double inverse
	 * exponential function, whose maximum value is 1 and
	 * whose minimum value is 1/4.
	 * 
	 * @param x
	 * The pixel's x position.
	 * 
	 * @param y
	 * The pixel's y position.
	 * 
	 * @param maxX
	 * The maximum possible x position.
	 * 
	 * @param maxY
	 * The maximum possible y position.
	 * 
	 * @return
	 * The weight for the given position.
	 */
	private double getPixelWeight(int x, int y, int maxX, int maxY) {
		double xPos = (double)x / maxX;
		double yPos = (double)y / maxY;
		double xWeight = weighMiddleDistributed(xPos);
		double yWeight = weighMiddleDistributed(yPos);
		double finalWeight = (xWeight + yWeight) / 2.0;
		return finalWeight;
	}
	
	/**
	 * Gets the weight of a value with the focus in the middle.
	 * The maximum weight is given for 0.5, and the minimum
	 * weight is given for 0 and 1. Intermediate values are
	 * calculated by using the negative exponential function
	 * -3(x-0.5)^2+1.
	 * 
	 * @param value
	 * The value to get the weight of. This must be within
	 * the range [0, 1].
	 * 
	 * @return
	 * The weight of the given value.
	 */
	private double weighMiddleDistributed(double value) {
		double offset = Math.pow(value - 0.5, 2);
		double weight = -3 * offset + 1;
		return weight;
	}
	
	/**
	 * Adds rows and columns to the image until both of its
	 * dimensions are divisible by the given factor.
	 * 
	 * @param factor
	 * The factor that the image must be divisible by.
	 */
	private void expandImageToDivisibility(int factor) {
		while(image.width % factor != 0) {
			image.insertColumn();
		}
		while(image.height % factor != 0) {
			image.insertRow();
		}
	}
}
