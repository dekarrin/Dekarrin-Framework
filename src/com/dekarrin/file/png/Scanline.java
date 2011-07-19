package com.dekarrin.file.png;

import com.dekarrin.util.ArrayHelper;
import com.dekarrin.util.ByteComposer;
import com.dekarrin.util.ByteHolder;
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
	 * The sample type for a palette index.
	 */
	public static final int PALETTE_INDEX_SAMPLE = 0;
	
	/**
	 * The sample type for a grayscale value.
	 */
	public static final int GRAYSCALE_VALUE_SAMPLE = 0;
	
	/**
	 * The sample type for a grayscale alpha.
	 */
	public static final int GRAYSCALE_ALPHA_SAMPLE = 1;
	
	/**
	 * The sample type for a truecolor red value.
	 */
	public static final int RED_SAMPLE = 0;
	
	/**
	 * The sample type for a truecolor green value.
	 */
	public static final int GREEN_SAMPLE = 1;
	
	/**
	 * The sample type for a truecolor blue value.
	 */
	public static final int BLUE_SAMPLE = 2;
	
	/**
	 * The sample type for a truecolor alpha value.
	 */
	public static final int ALPHA_SAMPLE = 3;
	
	/**
	 * The last decoded data, used in filtering algorithms.
	 */
	private static byte[] lastData;
	
	/**
	 * The number of samples that each pixel contains
	 */
	private int samplesPerPixel;
	
	/**
	 * The bit depth of each sample.
	 */
	private int bitDepth;
	
	/**
	 * The bytes from this scanline, filtered.
	 */
	private byte[] filteredData;
	
	/**
	 * The actual samples of this Scanline. Dimension 1 is
	 * the pixel, and dimension 2 holds the actual samples.
	 */
	private int[][] samples;
	
	/**
	 * Creates a new Scanline from filtered bytes.
	 *
	 * @param samples
	 * The number of samples in each pixel.
	 *
	 * @param bitDepth
	 * The bit depth of each sample.
	 *
	 * @param data
	 * The data to start this Scanline with. This is not
	 * necessarily gaurenteed to have one byte per sample;
	 * some bit depths have multiple samples packed into
	 * one byte.
	 */
	public Scanline(int samples, int bitDepth, byte[] data) {
		this.samplesPerPixel = samples;
		this.bitDepth = bitDepth;
		this.filteredData = data;
		defilterData();
	}
	
	/**
	 * Creates a new empty Scanline.
	 *
	 * @param samples
	 * The number of samples in each pixel.
	 *
	 * @param bitDepth
	 * The bit depth of each sample.
	 *
	 * @param width
	 * The width in pixels of this scanline.
	 */
	public Scanline(int samples, int bitDepth, int width) {
		this.samplesPerPixel = samples;
		this.bitDepth = bitDepth;
		createSamples(width);
	}
	
	/**
	 * Gets the value of a sample.
	 *
	 * @param pixel
	 * The pixel to get the sample from.
	 *
	 * @param sampleType
	 * The sample type. This can be one of several values; see
	 * the class constants for more information.
	 *
	 * @return
	 * The sample value.
	 */
	public int getSample(int pixel, int sampleType) {
		return samples[pixel][sampleType];
	}
	
	/**
	 * Sets the value of a sample.
	 *
	 * @param pixel
	 * The pixel to set the sample at.
	 *
	 * @param sampleType
	 * The sample type. This can be one of several values; see
	 * the class constants for more information.
	 *
	 * @param value
	 * What to set the sample to.
	 */
	public void setSample(int pixel, int sampleType, int value) {
		filteredData = null;
		samples[pixel][sampleType] = value;
	}
	
	/**
	 * Gets the filtered bytes from this Scanline.
	 *
	 * @return
	 * The filtered bytes.
	 */
	public byte[] getFiltered() {
		if(filteredData == null) {
			filterData();
		}
		return filteredData;
	}
	
	/**
	 * Gets the filtered bytes from this Scanline using a
	 * specified algorithm.
	 * 
	 * @param filterMethod
	 * The filter method to use.
	 * 
	 * @return
	 * The filtered bytes.
	 */
	public byte[] getFiltered(int filterMethod) {
		if(filteredData == null) {
			filterData(filterMethod);
		}
		return filteredData;
	}
	
	/**
	 * Gets the number of pixels in this Scanline.
	 * 
	 * @return
	 * The number of pixels.
	 */
	public int pixels() {
		return samples.length;
	}
	
	/**
	 * Gets the number of samples in each pixel.
	 * 
	 * @return
	 * The number of samples.
	 */
	public int samples() {
		return samplesPerPixel;
	}
	
	/**
	 * Informs the Scanline class that the next instance will be
	 * a top row. This resets the internal array used for storing
	 * the last decoded data. When this is called, the next
	 * Scanline instance will assume that it is the top row, and
	 * any filtering that requires the use of the last decoded row
	 * will consider the last row to be non-existant.
	 */
	public static void resetLines() {
		lastData = null;
	}
	
	/**
	 * Creates the samples array for a certain amount of
	 * pixels.
	 *
	 * @param pixels
	 * The number of pixels that sample array should hold.
	 */
	private void createSamples(int pixels) {
		samples = new int[pixels][samplesPerPixel];
	}
		
	/**
	 * Defilters the data into its samples and stores them in the
	 * samples property.
	 */
	private void defilterData() {
		byte[] unfiltered = unfilter(filteredData);
		int[] samples = unpack(unfiltered);
		buildSamples(samples);
	}
	
	/**
	 * Filters the data from the samples array and stores it in the
	 * filteredData property.
	 */
	private void filterData() {
		int[] samples = extractSamples();
		byte[] unfiltered = pack(samples);
		filteredData = filter(unfiltered);
	}
	
	/**
	 * Filters the data from the samples array and stores it in the
	 * filteredData property. The data is filtered according to the
	 * specified algorithm.
	 * 
	 * @param filterMethod
	 * The filtering algorithm to use.
	 */
	private void filterData(int filterMethod) {
		int[] samples = extractSamples();
		byte[] unfiltered = pack(samples);
		filteredData = filter(unfiltered, filterMethod);
	}
	
	/**
	 * Filters data bytes.
	 * 
	 * @param unfiltered
	 * The data to filter.
	 * 
	 * @param filterMethod
	 * The filter algorithm to use.
	 * 
	 * @return
	 * The filtered data.
	 */
	private byte[] filter(byte[] unfiltered, int filterMethod) {
		ByteHolder filtered = new ByteHolder(unfiltered.length + 1);
		filtered.add((byte)filterMethod);
		switch(filterMethod) {
			case NO_FILTER:
				filtered.add(unfiltered);
				break;
				
			case SUB_FILTER:
				filtered.add(subFilter(unfiltered));
				break;
				
			case UP_FILTER:
				filtered.add(upFilter(unfiltered));
				break;
				
			case AVERAGE_FILTER:
				filtered.add(averageFilter(unfiltered));
				break;
				
			case PAETH_FILTER:
				filtered.add(paethFilter(unfiltered));
				break;
		}
		lastData = unfiltered;
		return filtered.toArray();
	}
	
	/**
	 * Filters data bytes.
	 * 
	 * @param unfiltered
	 * The data to filter.
	 *
	 * @return
	 * The filtered data.
	 */
	private byte[] filter(byte[] unfiltered) {
		return filter(unfiltered, chooseFilterMethod(unfiltered));
	}
	
	/**
	 * Unfilters data bytes.
	 *
	 * @param filtered
	 * The data to unfilter.
	 *
	 * @return
	 * The unfiltered data.
	 */
	private byte[] unfilter(byte[] filtered) {
		byte[] unfiltered = null;
		int filterMethod = filtered[0];
		filtered = Arrays.copyOfRange(filtered, 1, filtered.length);
		switch(filterMethod) {
			case NO_FILTER:
				unfiltered = filtered;
				break;
				
			case SUB_FILTER:
				unfiltered = subUnfilter(filtered);
				break;
				
			case UP_FILTER:
				unfiltered = upUnfilter(filtered);
				break;
				
			case AVERAGE_FILTER:
				unfiltered = averageUnfilter(filtered);
				break;
				
			case PAETH_FILTER:
				unfiltered = paethUnfilter(filtered);
				break;
		}
		lastData = unfiltered;
		return unfiltered;
	}
	
	/**
	 * Chooses a filter method based on which one produces the smallest
	 * running total. This heuristic is taken from the PNG specification.
	 *
	 * @param unfiltered
	 * The data that is to be filtered.
	 *
	 * @return
	 * The number representing the filter algorithm to use.
	 */
	private int chooseFilterMethod(byte[] unfiltered) {
		int noTotal = (int)ArrayHelper.sum(unfiltered);
		int subTotal = (int)ArrayHelper.sum(subFilter(unfiltered));
		int upTotal = (int)ArrayHelper.sum(upFilter(unfiltered));
		int averageTotal = (int)ArrayHelper.sum(averageFilter(unfiltered));
		int paethTotal = (int)ArrayHelper.sum(paethFilter(unfiltered));
		int method = NO_FILTER;
		if(subTotal < noTotal) {
			method = SUB_FILTER;
			if(upTotal < subTotal) {
				method = UP_FILTER;
				if(averageTotal < upTotal) {
					method = AVERAGE_FILTER;
					if(paethTotal < averageTotal) {
						method = PAETH_FILTER;
					}
				}
			}
		}
		return method;
	}
	
	/**
	 * Creates the samples from a single array of sample values.
	 *
	 * @param allSamples
	 * The array of samples to build the internal samples array
	 * from.
	 */
	private void buildSamples(int[] allSamples) {
		createSamples(allSamples.length / samplesPerPixel);
		int currentPixel = 0;
		for(int i = 0; i < allSamples.length; i += samplesPerPixel) {
			for(int j = 0; j < samplesPerPixel; j++) {
				samples[currentPixel][j] = allSamples[i+j];
			}
			currentPixel++;
		}
	}
	
	/**
	 * Extracts all of the sample data as a single int array.
	 *
	 * @return
	 * The samples as one array.
	 */
	private int[] extractSamples() {
		int[] extracted = new int[samplesPerPixel * samples.length];
		int extractedPointer = 0;
		for(int i = 0; i < samples.length; i++) {
			for(int j = 0; j < samplesPerPixel; j++) {
				extracted[extractedPointer++] = samples[i][j];
			}
		}
		return extracted;
	}
	
	/**
	 * Packs the samples into unfiltered data.
	 *
	 * @param samples
	 * The sample values to pack.
	 *
	 * @return unfiltered
	 * The data, ready to be filtered.
	 */
	private byte[] pack(int[] samples) {
		byte[] unfiltered = null;
		if(bitDepth <= 8) {
			unfiltered = packSingleByteSamples(samples);
		} else {
			unfiltered = packMultiByteSamples(samples);
		}
		return unfiltered;
	}
	
	/**
	 * Unpacks the samples from the unfiltered data.
	 *
	 * @param unfilteredData
	 * The unfiltered data.
	 *
	 * @return
	 * The samples.
	 */
	private int[] unpack(byte[] unfilteredData) {
		int[] samples = null;
		if(bitDepth <= 8) {
			samples = unpackSingleByteSamples(unfilteredData);
		} else {
			samples = unpackMultiByteSamples(unfilteredData);
		}
		return samples;
	}
	
	/**
	 * Unpacks samples that are 8 bits or shorter.
	 *
	 * @param unfilteredData
	 * The unfiltered data.
	 *
	 * @return
	 * The samples.
	 */
	private int[] unpackSingleByteSamples(byte[] unfilteredData) {
		int samplesPerByte = 8 / bitDepth;
		int[] samples = new int[unfilteredData.length * samplesPerByte];
		int bitMask = (int)Math.pow(2, bitDepth)-1;
		int sampleOffset = 0;
		for(int i = 0; i < unfilteredData.length; i++) {
			int nextByte = ((int)unfilteredData[i] & 0xff);
			for(int j = 8-bitDepth; j >= 0; j -= bitDepth) {
				int shiftedSample = nextByte & (bitMask << j);
				samples[sampleOffset++] = shiftedSample >>> j;
			}
		}
		return samples;
	}
	
	/**
	 * Packs samples that are 8 bits or shorter.
	 *
	 * @param samples
	 * The samples to pack.
	 *
	 * @return
	 * The samples packed into unfiltered data bytes.
	 */
	private byte[] packSingleByteSamples(int[] samples) {
		int samplesPerByte = 8 / bitDepth;
		byte[] unfiltered = new byte[samples.length / samplesPerByte];
		int nextByte;
		int unfilteredOffset = 0;
		int samplesOffset = 0;
		while(samplesOffset < samples.length) {
			nextByte = 0;
			for(int i = 8-bitDepth; i >= 0; i -= bitDepth) {
				nextByte |= (samples[samplesOffset++] << i);
			}
			unfiltered[unfilteredOffset++] = (byte)nextByte;
		}
		return unfiltered;
	}
	
	/**
	 * Unpacks samples that span multiple bytes.
	 *
	 * @param unfilteredData
	 * The unfiltered data.
	 *
	 * @return
	 * The samples.
	 */
	private int[] unpackMultiByteSamples(byte[] unfilteredData) {
		int sampleWidth = bitDepth / 8;
		int[] samples = new int[unfilteredData.length / sampleWidth];
		ByteParser parser = new ByteParser(unfilteredData);
		int i = 0;
		while(parser.remainingBlocks(sampleWidth) > 0) {
			samples[i++] = parser.parseInt(sampleWidth);
		}
		return samples;
	}
	
	/**
	 * Packs samples that span multiple bytes.
	 *
	 * @param samples
	 * The samples to pack.
	 *
	 * @return
	 * The samples packed into unfiltered data bytes.
	 */
	private byte[] packMultiByteSamples(int[] samples) {
		int sampleWidth = bitDepth / 8;
		ByteComposer unfiltered = new ByteComposer(samples.length * sampleWidth);
		for(int s: samples) {
			unfiltered.composeInt(s, sampleWidth);
		}
		return unfiltered.toArray();
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
	private byte[] subFilter(byte[] raw) {
		byte[] sub = new byte[raw.length];
		int bpp = getBpp();
		for(int i = 0; i < bpp && i < raw.length; i++) {
			sub[i] = raw[i];
		}
		for(int i = bpp; i < sub.length; i++) {
			sub[i] = (byte)((raw[i] - raw[i-bpp]) % 256);
		}
		return sub;
	}
	
	/**
	 * Filters the data according to the Up filtering
	 * algorithm.
	 *
	 * @param raw
	 * The data to be filtered.
	 *
	 * @return
	 * The filtered data.
	 */
	private byte[] upFilter(byte[] raw) {
		byte[] prior = (lastData != null) ? lastData : new byte[raw.length];
		byte[] up = new byte[raw.length];
		for(int i = 0; i < raw.length; i++) {
			up[i] = (byte)((raw[i] - prior[i]) % 256);
		}
		return up;
	}
	
	/**
	 * Filters the data according to the Average filtering
	 * algorithm.
	 *
	 * @param raw
	 * The data to be filtered.
	 *
	 * @return
	 * The filtered data.
	 */
	private byte[] averageFilter(byte[] raw) {
		byte[] prior = (lastData != null) ? lastData : new byte[raw.length];
		byte[] average = new byte[raw.length];
		int bpp = getBpp();
		for(int i = 0; i < bpp && i < raw.length; i++) {
			average[i] = (byte)((raw[i] - calculateAverage((byte)0, prior[i])) % 256);
		}
		for(int i = bpp; i < raw.length; i++) {
			average[i] = (byte)((raw[i] - calculateAverage(raw[i-bpp], prior[i])) % 256);
		}
		return average;
	}
	
	/**
	 * Filters the data according to the Paeth filtering
	 * algorithm.
	 *
	 * @param raw
	 * The data to be defiltered.
	 *
	 * @return
	 * The filtered data.
	 */
	private byte[] paethFilter(byte[] raw) {
		byte[] prior = (lastData != null) ? lastData : new byte[raw.length];
		byte[] paeth = new byte[raw.length];
		int bpp = getBpp();
		for(int i = 0; i < bpp && i < raw.length; i++) {
			paeth[i] = (byte)((raw[i] - paethPredictor((byte)0, prior[i], (byte)0)) % 256);
		}
		for(int i = bpp; i < raw.length; i++) {
			paeth[i] = (byte)((raw[i] - paethPredictor(raw[i-bpp], prior[i], prior[i-bpp])) % 256);
		}
		return paeth;
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
	private byte[] subUnfilter(byte[] sub) {
		byte[] raw = new byte[sub.length];
		int bpp = getBpp();
		for(int i = 0; i < bpp && i < sub.length; i++) {
			raw[i] = sub[i];
		}
		for(int i = bpp; i < sub.length; i++) {
			raw[i] = (byte)((sub[i] + raw[i-bpp]) % 256);
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
	 * @return
	 * The defiltered data.
	 */
	private byte[] upUnfilter(byte[] up) {
		byte[] prior = (lastData != null) ? lastData : new byte[up.length];
		byte[] raw = new byte[up.length];
		for(int i = 0; i < up.length; i++) {
			raw[i] = (byte)((up[i] + prior[i]) % 256);
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
	 * @return
	 * The defiltered data.
	 */
	private byte[] averageUnfilter(byte[] average) {
		byte[] prior = (lastData != null) ? lastData : new byte[average.length];
		byte[] raw = new byte[average.length];
		int bpp = getBpp();
		for(int i = 0; i < bpp && i < average.length; i++) {
			raw[i] = (byte)((average[i] + calculateAverage((byte)0, prior[i])) % 256);
		}
		for(int i = bpp; i < average.length; i++) {
			raw[i] = (byte)((average[i] + calculateAverage(raw[i-bpp], prior[i])) % 256);
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
	 * @return
	 * The defiltered data.
	 */
	private byte[] paethUnfilter(byte[] paeth) {
		byte[] prior = (lastData != null) ? lastData : new byte[paeth.length];
		byte[] raw = new byte[paeth.length];
		int bpp = getBpp();
		for(int i = 0; i < bpp && i < paeth.length; i++) {
			raw[i] = (byte)((paeth[i] + paethPredictor((byte)0, prior[i], (byte)0)) % 256);
		}
		for(int i = bpp; i < paeth.length; i++) {
			raw[i] = (byte)((paeth[i] + paethPredictor(raw[i-bpp], prior[i], prior[i-bpp])) % 256);
		}
		return raw;
	}
	
	/**
	 * Gets the bytes per pixel for this Scanline. This always
	 * rounds up to at least 1.
	 *
	 * @return
	 * The bytes per pixel.
	 */
	private int getBpp() {
		int bytesPerSample = (bitDepth / 8);
		if(bytesPerSample < 1) {
			bytesPerSample = 1;
		}
		int bpp = (samplesPerPixel * bytesPerSample);
		return bpp;
	}
	
	/**
	 * Calculates the average of two byte values. Uses unsigned
	 * math by explicit integer casting.
	 * 
	 * @param left
	 * The value of the pixel to the left of the one being
	 * calculated.
	 * 
	 * @param above
	 * The value of the pixel above the one being calculated.
	 * 
	 * @return
	 * The average of the two given values.
	 */
	private int calculateAverage(byte left, byte above) {
		int l = (int)left & 0xff;
		int a = (int)above & 0xff;
		int avg = (int)Math.floor((l+a)/2);
		return avg;
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
	private int paethPredictor(byte left, byte above, byte upperLeft) {
		int predictor;
		int a,b,c; // results are off if not properly cast to ints
		a = (int)left & 0xff;
		b = (int)above & 0xff;
		c = (int)upperLeft & 0xff;
		int paeth = a + b - c;
		int pa = Math.abs(paeth - a);
		int pb = Math.abs(paeth - b);
		int pc = Math.abs(paeth - c);
		if(pa <= pb && pa <= pc) {
			predictor = a;
		} else if(pb <= pc) {
			predictor = b;
		} else {
			predictor = c;
		}
		return predictor;
	}
}
