package com.dekarrin.graphics;

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
	 * @param Image
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
		Color monoColor = new Color(image.bitDepth);
		monoColor.setSamples(112, 66, 20);
		monochrome(monoColor);
	}
}
