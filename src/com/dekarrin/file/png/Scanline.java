package com.dekarrin.file.png;

import com.dekarrin.io.InvalidFormatException;
import com.dekarrin.util.ByteComposer;
import com.dekarrin.util.ByteHolder;
import com.dekarrin.util.ByteParser;
import java.util.Arrays;

/**
 * A scanline from the PNG.
 */
class Scanline {
	
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
	 * The depth of each sample.
	 */
	private int sampleDepth;
	
	/**
	 * The bytes from this scanline, filtered.
	 */
	private byte[] filteredData;
	
	/**
	 * The filter method to use.
	 */
	private FilterMethod filterMethod;
	
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
	 * @param sampleDepth
	 * The bit depth of each sample.
	 *
	 * @param data
	 * The data to start this Scanline with. This is not
	 * necessarily guaranteed to have one byte per sample;
	 * some bit depths have multiple samples packed into
	 * one byte.
	 * 
	 * @param fm
	 * The method to use when filtering.
	 * 
	 * @throws InvalidFormatException
	 * If the filter method is invalid.
	 */
	public Scanline(int samples, int sampleDepth, byte[] data, FilterMethod fm) throws InvalidFormatException {
		this.samplesPerPixel = samples;
		this.sampleDepth = sampleDepth;
		this.filteredData = data;
		this.filterMethod = fm;
		defilterData();
	}
	
	/**
	 * Creates a new empty Scanline.
	 *
	 * @param samples
	 * The number of samples in each pixel.
	 *
	 * @param sampleDepth
	 * The bit depth of each sample.
	 *
	 * @param width
	 * The width in pixels of this scanline.
	 * 
	 * @param fm
	 * The method to use when filtering.
	 */
	public Scanline(int samples, int sampleDepth, int width, FilterMethod fm) {
		this.samplesPerPixel = samples;
		this.sampleDepth = sampleDepth;
		this.filterMethod = fm;
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
	 * 
	 * @throws InvalidFormatException
	 * If the filter method is invalid.
	 */
	public byte[] getFiltered() throws InvalidFormatException {
		if(filteredData == null) {
			filterData();
		}
		return filteredData;
	}
	
	/**
	 * Gets the filtered bytes from this Scanline using a
	 * specified algorithm.
	 * 
	 * @param filterType
	 * The filter type to use.
	 * 
	 * @return
	 * The filtered bytes.
	 * 
	 * @throws InvalidFormatException
	 * If the filter method is invalid.
	 */
	public byte[] getFiltered(FilterType filterType) throws InvalidFormatException {
		if(filteredData == null) {
			filterData(filterType);
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
	 * will consider the last row to be non-existent.
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
	 * 
	 * @throws InvalidFormatException
	 * If the filter method is invalid.
	 */
	private void defilterData() throws InvalidFormatException {
		byte[] unfiltered = unfilter(filteredData);
		int[] samples = unpack(unfiltered);
		buildSamples(samples);
	}
	
	/**
	 * Filters the data from the samples array and stores it in the
	 * filteredData property.
	 * 
	 * @throws InvalidFormatException
	 * If the filter method is invalid.
	 */
	private void filterData() throws InvalidFormatException {
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
	 * The filtering type to use.
	 * 
	 * @throws InvalidFormatException
	 * If the filter method is invalid.
	 */
	private void filterData(FilterType filterType) throws InvalidFormatException {
		int[] samples = extractSamples();
		byte[] unfiltered = pack(samples);
		filteredData = filter(unfiltered, filterType);
	}
	
	/**
	 * Filters data bytes.
	 * 
	 * @param unfiltered
	 * The data to filter.
	 * 
	 * @param filterType
	 * The filter type to use.
	 * 
	 * @return
	 * The filtered data.
	 * 
	 * @throws InvalidFormatException
	 * If the filter method is invalid.
	 */
	private byte[] filter(byte[] unfiltered, FilterType filterType) throws InvalidFormatException {
		ByteHolder filtered = new ByteHolder(unfiltered.length + 1);
		switch(filterMethod) {
			case ADAPTIVE:
				filtered.add((byte)filterType.dataValue());
				filtered.add(filterType.filter(unfiltered, lastData, getBpp()));
				break;
				
			default:
				throw new InvalidFormatException("Bad filter method!", "png");
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
	 * 
	 * @throws InvalidFormatException
	 * If the filter method is wrong.
	 */
	private byte[] filter(byte[] unfiltered) throws InvalidFormatException {
		return filter(unfiltered, FilterType.choose(unfiltered, lastData, getBpp()));
	}
	
	/**
	 * Unfilters data bytes.
	 *
	 * @param filtered
	 * The data to unfilter.
	 *
	 * @return
	 * The unfiltered data.
	 * 
	 * @throws InvalidFormatException
	 * If the filter method is invalid.
	 */
	private byte[] unfilter(byte[] filtered) throws InvalidFormatException {
		byte[] unfiltered = null;
		FilterType filterType = FilterType.fromData(filtered[0]);
		filtered = Arrays.copyOfRange(filtered, 1, filtered.length);
		switch(filterMethod) {
			case ADAPTIVE:
				unfiltered = filterType.filter(filtered, lastData, getBpp());
				break;
				
			default:
				throw new InvalidFormatException("Bad filter method!", "png");
		}
		lastData = unfiltered;
		return unfiltered;
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
		if(sampleDepth <= 8) {
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
		if(sampleDepth <= 8) {
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
		int samplesPerByte = 8 / sampleDepth;
		int[] samples = new int[unfilteredData.length * samplesPerByte];
		int bitMask = (int)Math.pow(2, sampleDepth)-1;
		int sampleOffset = 0;
		for(int i = 0; i < unfilteredData.length; i++) {
			int nextByte = ((int)unfilteredData[i] & 0xff);
			for(int j = 8-sampleDepth; j >= 0; j -= sampleDepth) {
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
		int samplesPerByte = 8 / sampleDepth;
		byte[] unfiltered = new byte[samples.length / samplesPerByte];
		int nextByte;
		int unfilteredOffset = 0;
		int samplesOffset = 0;
		while(samplesOffset < samples.length) {
			nextByte = 0;
			for(int i = 8-sampleDepth; i >= 0; i -= sampleDepth) {
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
		int sampleWidth = sampleDepth / 8;
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
		int sampleWidth = sampleDepth / 8;
		ByteComposer unfiltered = new ByteComposer(samples.length * sampleWidth);
		for(int s: samples) {
			unfiltered.composeInt(s, sampleWidth);
		}
		return unfiltered.toArray();
	}
	
	/**
	 * Gets the bytes per pixel for this Scanline. This always
	 * rounds up to at least 1.
	 *
	 * @return
	 * The bytes per pixel.
	 */
	private int getBpp() {
		int bytesPerSample = (sampleDepth / 8);
		if(bytesPerSample < 1) {
			bytesPerSample = 1;
		}
		int bpp = (samplesPerPixel * bytesPerSample);
		return bpp;
	}
	
}
