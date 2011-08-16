package com.dekarrin.file.png;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.dekarrin.io.InvalidFormatException;

/**
 * Methods to use for optimizing image data for compression.
 */
public enum InterlaceMethod {
	
	/**
	 * Uses no interlacing. The image data is transmitted
	 * line-by-line from top to bottom.
	 */
	NONE(0, 1) {
		protected Scanline[] pass() throws InvalidFormatException {
			int width = getScanlineWidth();
			int numberOfScanlines = rawData.length / width;
			if(numberOfScanlines != imageHeight) {
				throw new InvalidFormatException("Number of scanlines does not equal image height", "png");
			}
			Scanline[] lines = new Scanline[imageHeight];
			byte[] scanlineData;
			for(int i=0,start=0,end=0; i < imageHeight; i++) {
				start = i*width;
				end = (i+1)*width;
				scanlineData = Arrays.copyOfRange(rawData, start, end);
				lines[i] = new Scanline(colorMode.samples(), sampleDepth, scanlineData, filter);
			}
			return lines;
		}
	};/*,
	
	/**
	 * Uses the Adam7 interlacing algorithm. The image data is
	 * transmitted over a series of seven passes, with each pass
	 * transmitting more data than the last. The image is
	 * converted to a series of the following 8x8 pixel grid, with
	 * each number representing the pass in which that pixel is
	 * transmitted:
	 * 
	 * 1 6 4 6 2 6 4 6
	 * 7 7 7 7 7 7 7 7
	 * 5 6 5 6 5 6 5 6
	 * 7 7 7 7 7 7 7 7
	 * 3 6 4 6 3 6 4 6
	 * 7 7 7 7 7 7 7 7
	 * 5 6 5 6 5 6 5 6
	 * 7 7 7 7 7 7 7 7
	 *
	ADAM7(1, 7) {
		protected Scanline[] pass() throws InvalidFormatException {
			int[] vertOff = {0, 0, 4, 0, 2, 0, 1};
			int[] horzOff = {0, 4, 0, 2, 0, 1, 0};
			int[] vertInc = {8, 8, 8, 4, 4, 2, 2};
			int[] horzInc = {8, 8, 4, 4, 2, 2, 1};
			Scanline[] lines = scanlines;
			for(int i = vertOff[currentPass]; i < scanlines.length; i += vertInc[currentPass]) {
				for(int j = horzOff[currentPass]; j < lines[i].)
			}
			return null;
		}
	};*/
	
	/**
	 * Maps data values to InterlaceMethods.
	 */
	private static final Map<Integer,InterlaceMethod> dataTable;
	
	static {
		dataTable = new HashMap<Integer,InterlaceMethod>();
		for(InterlaceMethod cm: EnumSet.allOf(InterlaceMethod.class)) {
			dataTable.put(cm.dataValue(), cm);
		}
	}
	
	/**
	 * The value that is written to disk for this InterlaceMethod.
	 */
	private int dataValue;
	
	/**
	 * Creates a new InterlaceMethod.
	 * 
	 * @param value
	 * The value of this InterlaceMethod as it is written to disk.
	 * 
	 * @param passes
	 * The number of passes in this InterlaceMethod.
	 */
	private InterlaceMethod(int value, int passes) {
		dataValue = value;
		numberOfPasses = passes;
	}
	
	/**
	 * The raw data buffer. Depending on whether the InterlaceEngine
	 * is interlacing or deinterlacing, this could be either the input
	 * buffer or the output buffer.
	 */
	protected byte[] rawData;
	
	/**
	 * The Scanlines from the data. Depending on whether the
	 * InterlaceEngine is interlacing or deinterlacing, this could be
	 * either the input buffer or the output buffer.
	 */
	protected Scanline[] scanlines;
	
	/**
	 * Gets a InterlaceMethod from a data value read from disk.
	 * 
	 * @param dataValue
	 * The data value of the desired InterlaceMethod.
	 * 
	 * @return
	 * The InterlaceMethod that has the given data value.
	 */
	public static InterlaceMethod fromData(int dataValue) {
		return dataTable.get(dataValue);
	}
	
	/**
	 * Gets the value of the interlace method to write to a PNG
	 * file.
	 * 
	 * @return
	 * What value to write for this InterlaceMethod.
	 */
	public int dataValue() {
		return dataValue;
	}
	
	/**
	 * The height of the image that the scanlines are being
	 * created for.
	 */
	protected int imageHeight;
	
	/**
	 * The width of each scanline.
	 */
	protected int imageWidth;
	
	/**
	 * The sample depth of the scanlines.
	 */
	protected int sampleDepth;
	
	/**
	 * The color mode to use for the scanlines.
	 */
	protected ColorMode colorMode;
	
	/**
	 * The filter method for the scanlines.
	 */
	protected FilterMethod filter;
	
	/**
	 * Deinterlaces raw data into Scanlines.
	 * 
	 * @param data
	 * What to set it to.
	 * 
	 * @return
	 * The resulting scanlines.
	 */
	public Scanline[] deinterlace(byte[] data, int height, int width, int depth, ColorMode mode, FilterMethod fm) throws InvalidFormatException {
		rawData = data;
		imageHeight = height;
		scanlines = new Scanline[imageHeight];
		imageWidth = width;
		sampleDepth = depth;
		colorMode = mode;
		filter = fm;
		deinterlaceData();
		return scanlines;
	}
	
	/**
	 * Interlaces Scanlines into raw data.
	 * 
	 * @param lines
	 * The scanlines.
	 * 
	 * @return
	 * The resulting data.
	 */
	public byte[] interlace(Scanline[] lines) {
		scanlines = lines;
		interlaceData();
		return rawData;
	}
	
	/**
	 * The pass that the engine is currently on.
	 */
	protected int currentPass = 0;
	
	/**
	 * The number of passes that there are in this
	 * interlace method.
	 */
	private int numberOfPasses = 0;
	
	/**
	 * Interlaces the data.
	 */
	private void interlaceData() {
		
	}
	
	/**
	 * Deinterlaces the data.
	 */
	private void deinterlaceData() throws InvalidFormatException {
		for(currentPass = 0; currentPass < numberOfPasses; currentPass++) {
			scanlines = pass();
		}
	}
	
	/**
	 * Gets the length of a scanline in bytes.
	 *
	 * @return
	 * The length of a scanline.
	 */
	protected int getScanlineWidth() {
		int sw = (int)Math.ceil(getPixelWidth() * imageWidth) + 1;
		return sw;
	}
	
	/**
	 * Gets the length of a pixel in bytes.
	 *
	 * @return
	 * The length of a pixel.
	 */
	private double getPixelWidth() {
		double pw;
		pw = colorMode.samples() * ((double)sampleDepth / 8);
		return pw;
	}
	
	/**
	 * One pass of processing interlaced data. This will replace
	 * whatever scanlines currently is, so it should use the data in it.
	 */
	protected abstract Scanline[] pass() throws InvalidFormatException;
	
}