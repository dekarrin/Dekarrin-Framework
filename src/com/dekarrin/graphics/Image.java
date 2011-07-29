
package com.dekarrin.graphics;

/**
 * Holds an image as an array of samples.
 */
public class Image {
	
	/**
	 * The Channels containing the Samples themselves.
	 */
	private Channel[] channels;
	
	/**
	 * The number of bits used for each sample.
	 */
	public int sampleDepth;
	
	/**
	 * The height of this image.
	 */
	public int height;
	
	/**
	 * The width of this image.
	 */
	public int width;
	
	/**
	 * Refers to the red channel, if it exists.
	 */
	public static final int RED = 0;
	
	/**
	 * Refers to the green channel, if it exists.
	 */
	public static final int GREEN = 1;
	
	/**
	 * Refers to the blue channel, if it exists.
	 */
	public static final int BLUE = 2;
	
	/**
	 * Refers to the alpha channel, if it exists.
	 */
	public static final int ALPHA = 3;
	
	/**
	 * Refers to the gray channel, if it exists.
	 */
	public static final int GRAY = 0;
	
	/**
	 * Refers to the gray alpha channel, if it exists.
	 */
	public static final int GRAY_ALPHA = 1;
	
	/**
	 * Creates a new RGB image.
	 * The image will have 3 channels, one for each of red,
	 * green, and blue.
	 *
	 * @param width
	 * The width of the image in pixels.
	 *
	 * @param height
	 * The height of the image in pixels.
	 *
	 * @param sampleDepth
	 * How many bits are used for each sample.
	 */
	public Image(int width, int height, int sampleDepth) {
		this.sampleDepth = sampleDepth;
		this.width = width;
		this.height = height;
		createChannels(width, height, 3);
	}
	
	/**
	 * Creates a new RGB image with an alpha channel.
	 * The image will have 3 or 4 channels depending on
	 * whether or not alpha is used.
	 *
	 * @param width
	 * The width of the image in pixels.
	 *
	 * @param height
	 * The height of the image in pixels.
	 *
	 * @param sampleDepth
	 * How many bits are used for each sample.
	 *
	 * @param withAlpha
	 * Whether to give the image an alpha channel.
	 */
	public Image(int width, int height, int sampleDepth, boolean withAlpha) {
		this.sampleDepth = sampleDepth;
		this.width = width;
		this.height = height;
		int channels = withAlpha ? 4 : 3;
		createChannels(width, height, channels);
	}
	
	/**
	 * Creates a new RGB image with an alpha channel.
	 * The image will have 3 or 4 channels depending on
	 * whether or not alpha is used.
	 *
	 * @param width
	 * The width of the image in pixels.
	 *
	 * @param height
	 * The height of the image in pixels.
	 *
	 * @param sampleDepth
	 * How many bits are used for each sample.
	 *
	 * @param withAlpha
	 * Whether to give the image an alpha channel.
	 * 
	 * @param withColor
	 * Whether or not to use color. If this is false, the
	 * image will be grayscale.
	 */
	public Image(int width, int height, int sampleDepth, boolean withAlpha, boolean withColor) {
		this.sampleDepth = sampleDepth;
		this.width = width;
		this.height = height;
		int channels = withAlpha ? withColor ? 4 : 2 : withColor ? 3 : 1;
		createChannels(width, height, channels);
	}
	
	/**
	 * Inserts an empty row in this image at the specified point.
	 *
	 * @param index
	 * The index the row is to be placed at. The top of the
	 * Image corresponds to 0, and each row below that is one
	 * more higher than the one above it. If this is greater
	 * than the image height, other rows will be inserted to come
	 * up to the specified index.
	 */
	public void insertRow(int index) {
		for(Channel c: channels) {
			c.insertRow(index);
			height = c.height;
		}
	}
	
	/**
	 * Inserts an empty row at the bottom of this image.
	 */
	public void insertRow() {
		for(Channel c: channels) {
			c.insertRow();
			height = c.height;
		}
	}
	
	/**
	 * Inserts an empty column at the specified point.
	 *
	 * @param index
	 * The index the column is to be placed at. The left side of
	 * the Image corresponds to 0, and each row to the right of
	 * that is one more higher than the one to the left of it. If
	 * this is greater than the image width, other rows will be
	 * inserted to come up to the specified index.
	 */
	public void insertColumn(int index) {
		for(Channel c: channels) {
			c.insertColumn(index);
			width = c.width;
		}
	}
	
	/**
	 * Inserts an empty row at the right of this image.
	 */
	public void insertColumn() {
		for(Channel c: channels) {
			c.insertColumn();
			width = c.width;
		}
	}
	
	/**
	 * Adds a channel to this image. If there is already a channel
	 * at the specified index, this method has no effect.
	 *
	 * @param index
	 * The index of the channel to add.
	 */
	public void addChannel(int index) {
		if(!hasChannel(index)) {
			if(channels.length < index+1) {
				Channel[] newChannels = java.util.Arrays.copyOfRange(channels, 0, index+1);
				channels = newChannels;
			}
			channels[index] = new Channel(width, height);
		}
	}
	
	/**
	 * Gets the value at the coordinates of a point in a channel.
	 *
	 * @param channel
	 * The channel to get the value from.
	 *
	 * @param x
	 * The x value of the pixel. The origin is the left side and
	 * x counts up as on a normal Cartesian graph.
	 *
	 * @param y
	 * The y value of the pixel. The origin is at the top and counts
	 * up as it moves down.
	 *
	 * @return
	 * The value.
	 */
	public int valueAt(int channel, int x, int y) {
		return channels[channel].valueAt(x, y);
	}
	
	/**
	 * Sets the value at the coordinates of a point in a channel. If
	 * the specified channel does not exist, this method has no
	 * effect.
	 *
	 * @param channel
	 * The channel to that the value is to be set in.
	 *
	 * @param x
	 * The x value of the pixel. The origin is the left side and
	 * x counts up as on a normal Cartesian graph.
	 *
	 * @param y
	 * The y value of the pixel. The origin is at the top and counts
	 * up as it moves down.
	 *
	 * @param value
	 * The value that it is to be set to.
	 */
	public void setValueAt(int channel, int x, int y, int value) {
		if(hasChannel(channel)) {
			channels[channel].setValueAt(x, y, value);
		}
	}
	
	/**
	 * Gets the number of channels in this image.
	 *
	 * @return
	 * The number of channels.
	 */
	public int channelCount() {
		int chans = 0;
		for(int i = 0; i < channels.length; i++) {
			if(hasChannel(i)) {
				chans++;
			}
		}
		return chans;
	}
	
	/**
	 * Checks if there is a specific channel associated with this image.
	 *
	 * @param index
	 * The index of the channel to check for.
	 */
	public boolean hasChannel(int index) {
		if(channels.length > index && channels[index] != null) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the color at the specified pixel. Note that this does
	 * not return a reference to actual color objects; Image does
	 * not store colors as Color objects, and the Color object
	 * returned by this method is only used as a convenience class
	 * for grouping the sample values together. To set the values
	 * from a Color object, see setColorAt().
	 *
	 * @param x
	 * The x value of the pixel. The origin is the left side and
	 * x counts up as on a normal Cartesian graph.
	 *
	 * @param y
	 * The y value of the pixel. The origin is at the top and counts
	 * up as it moves down.
	 *
	 * @return
	 * A color representing the values of the specified pixel.
	 */
	public Color getColorAt(int x, int y) {
		Color c = new Color(sampleDepth);
		int r = hasChannel(RED) ? valueAt(RED, x, y) : c.maximumValue();
		int g = hasChannel(GREEN) ? valueAt(GREEN, x, y) : c.maximumValue();
		int b = hasChannel(BLUE) ? valueAt(BLUE, x, y) : c.maximumValue();
		int a = hasChannel(ALPHA) ? valueAt(ALPHA, x, y) : c.maximumValue();
		c.setSamples(r, g, b, a);
		return c;
	}
	
	/**
	 * Sets the color at the specified pixel.
	 *
	 * @param x
	 * The x value of the pixel. The origin is the left side and
	 * x counts up as on a normal Cartesian graph.
	 *
	 * @param y
	 * The y value of the pixel. The origin is at the top and counts
	 * up as it moves down.
	 *
	 * @param color
	 * The new color to set that point to.
	 *
	 * @throws InvalidBitDepthException
	 * If the Color has a different bit depth than this Image.
	 */
	public void setColorAt(int x, int y, Color color) throws InvalidBitDepthException {
		if(color.sampleDepth() != sampleDepth) {
			String message = String.format("Color bit depth is %s; should be %s!", color.sampleDepth(), sampleDepth);
			throw new InvalidBitDepthException(message);
		}
		setValueAt(RED, x, y, color.getRed());
		setValueAt(GREEN, x, y, color.getGreen());
		setValueAt(BLUE, x, y, color.getBlue());
		setValueAt(ALPHA, x, y, color.getAlpha());
	}
	
	/**
	 * Sets the color at the specified pixel. If there is not already an
	 * alpha channel, this method can be set to add one when the color
	 * requires it.
	 *
	 * @param x
	 * The x value of the pixel. The origin is the left side and
	 * x counts up as on a normal Cartesian graph.
	 *
	 * @param y
	 * The y value of the pixel. The origin is at the top and counts
	 * up as it moves down.
	 *
	 * @param color
	 * The new color to set that point to.
	 *
	 * @param addAlpha
	 * Whether an alpha should be added if the color has an alpha.
	 *
	 * @throws InvalidBitDepthException
	 * If the Color has a different bit depth than this Image.
	 */
	public void setColorAt(int x, int y, Color color, boolean addAlpha) throws InvalidBitDepthException {
		if(color.sampleDepth() != sampleDepth) {
			String message = String.format("Color bit depth is %s; should be %s!", color.sampleDepth(), sampleDepth);
			throw new InvalidBitDepthException(message);
		}
		if(addAlpha && !hasChannel(ALPHA) && color.getAlpha() != color.maximumValue()) {
			addChannel(ALPHA);
		}
		setValueAt(RED, x, y, color.getRed());
		setValueAt(GREEN, x, y, color.getGreen());
		setValueAt(BLUE, x, y, color.getBlue());
		setValueAt(ALPHA, x, y, color.getAlpha());
	}
	
	/**
	 * Creates the underlying Channels that make up this image.
	 *
	 * @param width
	 * How wide the channels should be.
	 *
	 * @param height
	 * How tall the channels should be.
	 *
	 * @param count
	 * How many channels should be created.
	 */
	private void createChannels(int width, int height, int count) {
		channels = new Channel[count];
		for(int i = 0; i < count; i++) {
			channels[i] = new Channel(width, height);
		}
	}
	
	/**
	 * Changes the bit depth of the samples.
	 * 
	 * @param sampleDepth
	 * The bit depth to change it to.
	 */
	public void changeBitDepth(int sampleDepth) {
		Color c;
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				c = getColorAt(x, y);
				c.changeBitDepth(sampleDepth);
				setColorAt(x, y, c);
			}
		}
		this.sampleDepth = sampleDepth;
	}
}