package com.dekarrin.graphics;

import com.dekarrin.math.Point;
import com.dekarrin.math.PointTransformer;

/**
 * Performs resize operations on an Image.
 */
public class ImageResizer {

	/**
	 * The image that the operations are to be conducted
	 * on.
	 */
	private Image image;
	
	/**
	 * The interpolation mode for nearest neighbor scaling.
	 */
	public static final int INTERPOLATION_NEAREST_NEIGHBOR = 0;
	
	/**
	 * Creates a new ImageResizer containing the
	 * specified image.
	 * 
	 * @param image
	 * The image to resize.
	 */
	public ImageResizer(Image image) {
		this.image = image;
	}
	
	/**
	 * Gets the contained image from this ImageResizer.
	 * 
	 * @return
	 * The image.
	 */
	public Image getImage() {
		return image;
	}
	
	/**
	 * Scales the image up by a specific factor. The new
	 * size of the image will be the old size times the
	 * factor.
	 * 
	 * @param factor
	 * The factor to multiply the current size by.
	 */
	public void scale(double factor) {
		PointTransformer pt = new PointTransformer(2);
		Image scaled = new Image((int)(image.width*factor), (int)(image.height*factor), image.sampleDepth);
		for(int y = 0; y < image.height; y++) {
			for(int x = 0; x < image.width; x++) {
				Point pixel = new Point(2, x, y);
				pixel = pt.scale(pixel, factor);
				float pX = (float)pixel.get(Point.X);
				float pY = (float)pixel.get(Point.Y);
				scaled.setColorAt(Math.round(pX), Math.round(pY), image.getColorAt(x, y));
			}
		}
		interpolate(scaled, INTERPOLATION_NEAREST_NEIGHBOR);
		image = scaled;
	}
	
	/**
	 * Interpolates the image data to fill null
	 * color values.
	 * 
	 * @param image
	 * The image to interpolate.
	 * 
	 * @param mode
	 * The interpolation mode to use.
	 */
	private void interpolate(Image image, int mode) {
		switch(mode) {
			case INTERPOLATION_NEAREST_NEIGHBOR:
				nearestNeighborInterpolate(image);
				break;
		}
	}
	
	/**
	 * Interpolates the image data according to nearest
	 * neighbor interpolation.
	 * 
	 * @param image
	 * The image to interpolate.
	 */
	private void nearestNeighborInterpolate(Image image) {
		
	}
}