package com.dekarrin.file.png;

import com.dekarrin.util.ByteParser;
import java.util.Arrays;

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
	private static class Pixel {
	
		private int[] samples;
		
		public Pixel(byte[] data, int sampleCount, int bitDepth) {
			ByteParser p = new ByteParser(data);
			int sampleWidth = bitDepth / 8;
			samples = new int[sampleCount];
			int i = 0;
			while(p.remaining() > 0) {
				samples[i++] = p.parseInt(sampleWidth);
			}
		}
		
		public Pixel(int sampleCount) {
			samples = new int[sampleCount];
		}
		
		public void setSample(int sample, int value) {
			if(samples == null) {
				samples = new int[sample];
			} else {
				growArray(sample + 1);
			}
			samples[sample] = value;
		}
		
		public int[] getSamples() {
			return samples;
		}
		
		private void growArray(int size) {
			if(samples.length < size) {
				samples = Arrays.copyOfRange(samples, 0, size);
			}
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
	 * Resets the lastData entered into a Scanline.
	 */
	public static void reset() {
		lastData = null;
	}
	
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
		for(int i = 0; i < getImageWidth(); i++) {
			p.parseBytes(pixelData);
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
			raw[i] = addBytes(sub[i], raw[i-bpp]);
		}
		return raw;
	}
	
	/**
	 * Filters the data according to the Sub filtering
	 * algorithm.
	 *
	 * @param raw
	 * The data to be filtered.
	 *
	 * @return
	 * The filtered data.
	 */
	private static byte[] subFilter(byte[] raw) {
		byte[] sub = new byte[raw.length];
		int bpp = getPixelWidth();
		for(int i = 0; i < bpp && i < sub.length; i++) {
			sub[i] = raw[i];
		}
		for(int i = bpp; i < sub.length; i++) {
			sub[i] = subtractBytes(raw[i], raw[i-bpp]);
		}
		return sub;
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
			Arrays.fill(prior, (byte)0);
		}
		for(int i = 0; i < up.length; i++) {
			raw[i] = addBytes(up[i], prior[i]);
		}
		return raw;
	}
	
	/**
	 * Filters the data according to the Up filtering
	 * algorithm.
	 *
	 * @param raw
	 * The data to be filtered.
	 *
	 * @param prior
	 * The set of the last data. This must be the unfiltered
	 * version. This can be null, to indicate that this is the
	 * first scanline.
	 *
	 * @return
	 * The filtered data.
	 */
	private static byte[] upFilter(byte[] raw, byte[] prior) {
		byte[] up = new byte[raw.length];
		if(prior == null) {
			prior = new byte[raw.length];
			Arrays.fill(prior, (byte)0);
		}
		for(int i = 0; i < up.length; i++) {
			up[i] = subtractBytes(raw[i], prior[i]);
		}
		return up;
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
			Arrays.fill(prior, (byte)0);
		}
		for(int i = 0; i < bpp && i < average.length; i++) {
			raw[i] = addBytes(average[i], (byte)Math.floor( (double)prior[i] / 2 ));
		}
		for(int i = bpp; i < average.length; i++) {
			raw[i] = addBytes(average[i], (byte)Math.floor( (double)(raw[i-bpp]+prior[i]) / 2 ));
		}
		return raw;
	}
	
	/**
	 * Filters the data according to the Average filtering
	 * algorithm.
	 *
	 * @param raw
	 * The data to be filtered.
	 *
	 * @param prior
	 * The set of the last data. This must be the unfiltered
	 * version. This can be null to indicate that this is
	 * the first scanline.
	 *
	 * @return
	 * The filtered data.
	 */
	private static byte[] averageFilter(byte[] raw, byte[] prior) {
		byte[] average = new byte[raw.length];
		int bpp = getPixelWidth();
		if(prior == null) {
			prior = new byte[raw.length];
			Arrays.fill(prior, (byte)0);
		}
		for(int i = 0; i < bpp && i < raw.length; i++) {
			average[i] = subtractBytes(raw[i], (byte)Math.floor( (double)prior[i] / 2 ));
		}
		for(int i = bpp; i < raw.length; i++) {
			average[i] = subtractBytes(raw[i], (byte)Math.floor( (double)(raw[i-bpp]+prior[i]) / 2 ));
		}
		return average;
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
			Arrays.fill(prior, (byte)0);
		}
		for(int i = 0; i < bpp && i < paeth.length; i++) {
			raw[i] = addBytes(paeth[i], paethPredictor((byte)0, prior[i], (byte)0));
		}
		for(int i = bpp; i < paeth.length; i++) {
			raw[i] = addBytes(paeth[i], paethPredictor(raw[i-bpp], prior[i], prior[i-bpp]));
		}
		return raw;
	}
	
	/**
	 * Filters the data according to the Paeth filtering
	 * algorithm.
	 *
	 * @param raw
	 * The data to be defiltered.
	 *
	 * @param prior
	 * The data from the previous scanline, already decoded.
	 * This can be null to indicate that paeth is the first
	 * scanline.
	 *
	 * @return
	 * The filtered data.
	 */
	private static byte[] paethFilter(byte[] raw, byte[] prior) {
		byte[] paeth = new byte[raw.length];
		int bpp = getPixelWidth();
		if(prior == null) {
			prior = new byte[raw.length];
			Arrays.fill(prior, (byte)0);
		}
		for(int i = 0; i < bpp && i < raw.length; i++) {
			paeth[i] = subtractBytes(raw[i], paethPredictor((byte)0, prior[i], (byte)0));
		}
		for(int i = bpp; i < raw.length; i++) {
			paeth[i] = subtractBytes(raw[i], paethPredictor(raw[i-bpp], prior[i], prior[i-bpp]));
		}
		return paeth;
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
	 * Adds two bytes together.
	 *
	 * @param byte1
	 * One of the bytes to be added together.
	 *
	 * @param byte2
	 * One of the bytes to be added together.
	 *
	 * @return
	 * The two bytes added together.
	 */
	private static byte addBytes(byte byte1, byte byte2) {
		int a = byte1;
		int b = byte2;
		int c = (a + b) % 256;
		byte result = (byte)((byte)c & 0xff);
		return result;
	}
	
	/**
	 * Subtracts two bytes.
	 *
	 * @param byte1
	 * The byte to perform the subtraction on.
	 *
	 * @param byte2
	 * The byte to subtract from the other.
	 *
	 * @return
	 * The result of subtracting byte2 from byte1.
	 */
	private static byte subtractBytes(byte byte1, byte byte2) {
		int a = byte1;
		int b = byte2;
		int c = (a - b) % 256;
		byte result = (byte)((byte)c & 0xff);
		return result;
	}
	
	/**
	 * Gets the sum of a byte array.
	 *
	 * @param bytes
	 * The bytes to add together.
	 *
	 * @return
	 * All bytes added together.
	 */
	private static int byteSum(byte[] bytes) {
		int sum = 0;
		for(byte i: bytes) {
			sum += i;
		}
		return sum;
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
	 * Creates a new, empty Scanline.
	 */
	public Scanline(int samplesPerPixel, int bitDepth) {
		Scanline.bitDepth = bitDepth;
		Scanline.samples = samplesPerPixel;
	}
	
	/**
	 * Adds a sample to this scanline at a specified pixel.
	 *
	 * @param pixelIndex
	 * The pixel to add the sample at.
	 *
	 * @param sampleIndex
	 * The index of the sample to add.
	 *
	 * @param value
	 * The value to set the sample to.
	 */
	public void setSample(int pixelIndex, int sampleIndex, int value) {
		if(pixels == null) {
			pixels = new Pixel[pixelIndex];
		} else {
			growPixelArray(pixelIndex + 1);
		}
		if(pixels[pixelsIndex] == null) {
			pixels[pixelIndex] = new Pixel(pixelsIndex);
		}
		pixels[pixelIndex].setSample(sampleIndex, value);
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
	
	/**
	 * Filters this scanline's bytes.
	 *
	 * @return
	 * The filtered bytes.
	 */
	public byte[] getFilteredBytes() {
		byte[] bytes = getBytes();
		ByteHolder filteredBytes = ByteHolder(bytes.length + 1);
		int filterAlgorithm = selectFilterAlgorithm();
		filteredBytes.add(filterAlgorithm);
		switch(filterAlgorithm) {
			case NO_FILTER:
				filteredBytes.add(bytes);
				break;
				
			case SUB_FILTER:
				filteredBytes.add(subFilter(bytes));
				break;
				
			case UP_FILTER:
				filteredBytes.add(upFilter(bytes, lastData));
				break;
				
			case AVERAGE_FILTER:
				filteredBytes.add(averageFilter(bytes, lastData));
				break;
				
			case PAETH_FILTER:
				filteredBytes.add(paethFilter(bytes, lastData));
				break;
		}
		lastData = bytes;
		return filteredBytes.toArray();
	}
	
	/**
	 * Selects a filtering algorithm for this row based on the
	 * sum of absolute values.
	 */
	private int selectFilterAlgorithm() {
		int method = NO_FILTER;
		int sum = byteSum(getBytes());
		int sum2;
		if((sum2 = byteSum(subFilter(getBytes()))) < sum) {
			sum = sum2;
			method = SUB_FILTER;
		}
		if((sum2 = byteSum(upFilter(getBytes()))) < sum) {
			sum = sum2;
			method = UP_FILTER;
		}
		if((sum2 = byteSum(averageFilter(getBytes()))) < sum) {
			sum = sum2;
			method = AVERAGE_FILTER;
		}
		if((sum2 = byteSum(paethFilter(getBytes()))) < sum) {
			sum = sum2;
			method = PAETH_FILTER;
		}
		return method;
	}
	
	/**
	 * Ensures that the pixel array is the correct length,
	 * and makes it longer if it isn't.
	 *
	 * @param size
	 * The desired length.
	 */
	private void growPixelArray(int size) {
		if(pixels.length < size) {
			pixels = Arrays.copyOfRange(pixels, 0, size);
		}
	}
}