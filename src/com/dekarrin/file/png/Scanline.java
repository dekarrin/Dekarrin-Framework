package com.dekarrin.file.png;

/**
 * A scanline from the png.
 */
class Scanline {

	/**
	 * The filtering method for none.
	 */
	public static final int NO_FILTER = 0;
	
	/**
	 * The filtering method for a sub() filter.
	 */
	public static final int SUB_FILTER = 1;
	
	/**
	 * The filtering method for an up() filter.
	 */
	public static final int UP_FILTER = 2;
	
	/**
	 * The filtering method for an average() filter.
	 */
	public static final int AVERAGE_FILTER = 3;
	
	/**
	 * The filtering method for a paeth() filter.
	 */
	public static final int PAETH_FILTER = 4;
	
	/**
	 * The pixel class.
	 */
	private class Pixel {
	
		private int[] samples;
		
		public Pixel(byte[] data, int sampleCount, int bitDepth) {
			ByteParser p = new ByteParser(data);
			int sampleWidth = bitDepth / 8;
			samples = new int[sampleCount];
			int i = 0;
			while(p.remaining() > 0) {
				samples[i++] = p.parseByte(sampleWidth);
			}
		}
		
		public int[] getSamples() {
			return samples;
		}
	}

	/**
	 * The pixels that make up this scanline.
	 */
	private Pixel[] pixels;
	
	/**
	 * The filtering method used for this Scanline.
	 */
	private int filteringMethod;
	
	/**
	 * The bit depth of the containing image.
	 */
	private static int bitDepth;
	
	/**
	 * The number of samples in every pixel of data.
	 */
	private static int samples;
	
	/**
	 * The total length of a scanline in bytes.
	 */
	private static int width;
	
	/**
	 * The last set of scanline data that was parsed.
	 */
	private static byte[] lastData;
	
	/**
	 * Creates a new scanline from an unfiltered
	 * byte stream.
	 *
	 * @param byteStream
	 * The stream to create the scanline from.
	 *
	 * @param bitDepth
	 * The bit depth of the image this Scanline is a part of.
	 *
	 * @param sampleCount
	 * The number of samples per pixel of image data.
	 *
	 * @return
	 * The new Scanline.
	 */
	public static Scanline getInstanceFromFiltered(byte[] byteStream, int bitDepth, int sampleCount) {
		Scanline.width = byteStream.length;
		Scanline.bitDepth = bitDepth;
		Scanline.samples = sampleCount;
		byte[] dataBytes = new byte[width-1];
		ByteParser p = new ByteParser(byteStream);
		int filteringMethod = p.parseInt(1);
		switch(filteringMethod) {
			case NO_FILTER:
				dataBytes = p.parseFinalBytes();
				break;
				
			case SUB_FILTER:
				dataBytes = Scanline.subDefilter(p.parseFinalBytes());
				break;
				
			case UP_FILTER:
				dataBytes = Scanline.upDefilter(p.parseFinalBytes(), lastData);
				break;
				
			case AVERAGE_FILTER:
				dataBytes = Scanline.averageDefilter(p.parseFinalBytes(), lastData);
				break;
				
			case PAETH_FILTER:
				dataBytes = Scanline.paethDefilter(p.parseFinalBytes(), lastData);
				break;
		}
		Pixel[] pixels = new Pixel[getImageWidth()];
		p = new ByteParser(dataBytes);
		byte[] pixelData = new byte[getPixelWidth()];
		for(int i = 0; i < width; i++) {
			pixelData = p.parseBytes(getPixelWidth());
			pixels[i] = new Pixel(pixelData, Scanline.samples, Scanline.bitDepth);
		}
		lastData = dataBytes;
		return new Scanline(pixels, filteringMethod);
	}
	
	/**
	 * Defilters the data according to the Sub filtering
	 * algorithm.
	 *
	 * @param sub
	 * The data to be defiltered.
	 *
	 * @return
	 * The defiltered data.
	 */
	private static byte[] subDefilter(byte[] sub) {
		byte[] raw = new byte[sub.length];
		int bpp = getPixelWidth();
		for(int i = 0; i < bpp && i < sub.length; i++) {
			raw[i] = sub[i];
		}
		for(int i = bpp; i < sub.length; i++) {
			raw[i] = sub[i] + raw[i-bpp];
		}
		return raw;
	}
	
	/**
	 * Defilters the data according to the Up filtering
	 * algorithm.
	 *
	 * @param up
	 * The data to be defiltered.
	 *
	 * @param prior
	 * The set of the last data. This must be the unfiltered
	 * version. This can be null, to indicate that this is the
	 * first scanline.
	 *
	 * @return
	 * The defiltered data.
	 */
	private static byte[] upDefilter(byte[] up, byte[] prior) {
		byte[] raw = new byte[up.length];
		if(prior == null) {
			prior = new byte[up.length];
			Arrays.fill(prior, 0);
		}
		for(int i = 0; i < up.length; i++) {
			raw[i] = up[i] + prior[i];
		}
		return raw;
	}
	
	/**
	 * Defilters the data according to the Average filtering
	 * algorithm.
	 *
	 * @param average
	 * The data to be defiltered.
	 *
	 * @param prior
	 * The set of the last data. This must be the unfiltered
	 * version. This can be null to indicate that this is
	 * the first scanline.
	 *
	 * @return
	 * The defiltered data.
	 */
	private static byte[] averageDefilter(byte[] average, byte[] prior) {
		byte[] raw = new byte[average.length];
		int bpp = getPixelWidth();
		if(prior == null) {
			prior = new byte[average.length];
			Arrays.fill(prior, 0);
		}
		for(int i = 0; i < bpp && i < average.length; i++) {
			raw[i] = average[i] + (byte)Math.floor( (double)prior[i] / 2 );
		}
		for(int i = bpp; i < average.length; i++) {
			raw[i] = average[i] + (byte)Math.floor( (double)(raw[i-bpp]+prior[i]) / 2 );
		}
		return raw;
	}
	
	/**
	 * Defilters the data according to the Paeth filtering
	 * algorithm.
	 *
	 * @param paeth
	 * The data to be defiltered.
	 *
	 * @param prior
	 * The data from the previous scanline, already decoded.
	 * This can be null to indicate that paeth is the first
	 * scanline.
	 *
	 * @return
	 * The defiltered data.
	 */
	private static byte[] paethDefilter(byte[] paeth, byte[] prior) {
		byte[] raw = new byte[paeth.length];
		int bpp = getPixelWidth();
		if(prior == null) {
			prior = new byte[paeth.length];
			Arrays.fill(prior, 0);
		}
		for(int i = 0; i < bpp && i < paeth.length; i++) {
			raw[i] = paeth[i] + paethPredictor(0, prior[i], 0);
		}
		for(int i = bpp; i < paeth.length; i++) {
			raw[i] = paeth[i] + paethPredictor(raw[i-bpp], prior[i], prior[i-bpp]);
		}
		return raw;
	}
	
	/**
	 * Calculates the predictor from whichever neighboring pixel
	 * is closest to the paeth function of the pixels.
	 *
	 * @param left
	 * The corresponding byte to the left of the pixel whose
	 * predictor is being calculated.
	 *
	 * @param above
	 * The corresponding byte above the pixel whose predictor is
	 * being calculated.
	 *
	 * @param upperLeft
	 * The corresponding byte from the pixel above and to the
	 * left of the current pixel.
	 *
	 * @return
	 * The paeth predictor byte.
	 */
	private static byte paethPredictor(byte left, byte above, byte upperLeft) {
		byte predictor;
		int paeth = left + above - upperLeft;
		int leftPaeth = Math.abs(paeth - left);
		int abovePaeth = Math.abs(paeth - above);
		int upperLeftPaeth = Math.abs(paeth - upperLeft);
		if(leftPaeth <= abovePaeth && leftPaeth <= upperLeftPaeth) {
			predictor = left;
		} else if(abovePaeth <= upperLeftPaeth) {
			predictor = above;
		} else {
			predictor = upperLeft;
		}
		return predictor;
	}
	
	/**
	 * Filters the data according to the Sub filtering
	 * algorithm.
	 *
	 * @param unfilteredData
	 * The data to be filtered.
	 *
	 * @return
	 * The filtered data.
	 */
	private static byte[] subFilter(byte[] unfilteredData) {
		byte[] filteredData = new byte[unfilteredData.length];
		for(int i = 0; i < getPixelWidth(); i++) {
			filteredData[i] = unfilteredData[i];
		}
		for(int i = getPixelWidth(); i < unfilteredData.length; i++) {
			filteredData[i] = unfilteredData[i] + filteredData[i-getPixelWidth()];
		}
		return filteredData;
	}
	
	/**
	 * Gets the width of a single pixel.
	 *
	 * @return
	 * The width of a pixel in bytes.
	 */
	private static int getPixelWidth() {
		int width = Scanline.samples * (Scanline.bitDepth / 8);
		return width;
	}
	
	/**
	 * Gets the width in pixels of the entire image.
	 *
	 * @return
	 * The width of the image.
	 */
	private static int getImageWidth() {
		int width = Scanline.width / getPixelWidth();
		return width;
	}
	
	/**
	 * Creates a new scanline from a series of pixels.
	 *
	 * @param pixels
	 * The pixels that make up the new Scanline.
	 */
	public Scanline(Pixel[] pixels, int filteringMethod) {
		this.pixels = pixels;
		this.filteringMethod = filteringMethod;
	}
	
	/**
	 * Gets the exact bytes of this scanline.
	 *
	 * @return
	 * The bytes.
	 */
	public byte[] getBytes() {
		byte[] data = new byte[width];
		data[0] = (byte)filteringMethod;
		int p = 1;
		for(int i = 0; i < pixels.length; i++) {
			int[] samples = pixels[i].getSamples();
			for(int j: samples) {
				data[p++] = (byte)j;
			}
		}
		return data;
	}
	
	/**
	 * Gets all of the sample data.
	 *
	 * @return
	 * The samples.
	 */
	public int[][] getSamples() {
		int[][] sampleData = new int[pixels.length][samples];
		int[] samples;
		for(int i = 0; i < pixels.length; i++) {
			samples = pixels[i].getSamples();
			for(int j = 0; j < samples.length; j++) {
				sampleData[i][j] = samples[j];
			}
		}
		return sampleData;
	}
}