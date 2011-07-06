package com.dekarrin.file.png;

import java.util.Vector;
import java.awt.Point;
import java.util.Calendar;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import com.dekarrin.graphics.*;
import com.dekarrin.zip.*;

/**
 * Represents a PNG file. This class is attempting to be compliant
 * with version 1.2 of the PNG standard; it's not there yet.
 */
public class PortableNetworkGraphic {
	
	/**
	 * Unit specifier for unknown.
	 */
	public static final int UNKNOWN_UNIT = 0;
	
	/**
	 * Unit specifier for the meter.
	 */
	public static final int METER_UNIT = 1;
	
	/**
	 * The background mode for indexed colors.
	 */
	public static final int INDEXED_COLOR_MODE = 0;
	
	/**
	 * The background mode for grayscale.
	 */
	public static final int GRAYSCALE_MODE = 1;
	
	/**
	 * The background mode for truecolor.
	 */
	public static final int TRUECOLOR_MODE = 2;
	
	/**
	 * The rendering intent of perceptual.
	 */
	public static final int RENDERING_INTENT_PERCEPTUAL = 0;
	
	/**
	 * The rendering intent of relative colorimetric.
	 */
	public static final int RENDERING_INTENT_RELATIVE_COLORIMETRIC = 1;
	
	/**
	 * The rendering intent of saturation.
	 */
	public static final int RENDERING_INTENT_SATURATION = 2;
	
	/**
	 * The rendering intent of absolute colorimetric.
	 */
	public static final int RENDERING_INTENT_ABSOLUTE_COLORIMETRIC = 3;
	 
	/**
	 * The compression method value for deflate/inflate.
	 */
	public static final int COMPRESSION_METHOD_ZLIB = 0;
	
	/**
	 * Filter method for adaptive filtering with five basic filter
	 * types.
	 */
	public static final int FILTER_METHOD_ADAPTIVE = 0;
	
	/**
	 * Interlace method for no interlacing.
	 */
	public static final int INTERLACE_METHOD_NONE = 0;
	
	/**
	 * Interlace method for Adam7 interlacing.
	 */
	public static final int INTERLACE_METHOD_ADAM7 = 1;
	
	/**
	 * Color type for no colors.
	 */
	public static final int COLOR_TYPE_GRAYSCALE = 0;
	
	/**
	 * Color type for color used.
	 */
	public static final int COLOR_TYPE_COLOR = 2;
	
	/**
	 * Color type for using both color and a palette.
	 */
	public static final int COLOR_TYPE_COLOR_PALETTE = 3;
	
	/**
	 * Color type for using only an alpha channel.
	 */
	public static final int COLOR_TYPE_GRAYSCALE_ALPHA = 4;
	
	/**
	 * Color type for using both an alpha channel and color.
	 */
	public static final int COLOR_TYPE_COLOR_ALPHA = 6;
	
	/**
	 * The bit depth of this image.
	 */
	private int bitDepth;
	
	/**
	 * The height of this image.
	 */
	private int height;
	
	/**
	 * The width of this image.
	 */
	private int width;
	
	/**
	 * The color type of this image.
	 */
	private int colorMode;
	
	/**
	 * Whether or not this png uses interlacing.
	 */
	private boolean adam7Interlacing;
	
	/**
	 * The color palette, if there is one.
	 */
	private Color[] colorPalette;
	
	/**
	 * The alpha palette.
	 */
	private int[] alphaPalette;
	
	/**
	 * The palette frequencies.
	 */
	private int[] frequencies;
	
	/**
	 * The complete palette of this image. This includes
	 * the histogram, if it exists.
	 */
	private Palette palette;
	
	/**
	 * The alpha color.
	 */
	private Color alphaColor;
	
	/**
	 * The image gamma.
	 */
	private int gamma;
	
	/**
	 * The color space for this Image;
	 */
	private Chromaticity chromaticity;
	
	/**
	 * The rendering intent of this image.
	 */
	private int renderingIntent;
	
	/**
	 * The color profile for this png.
	 */
	private IccProfile profile;
	
	/**
	 * The textual data from this png.
	 */
	private HashMap<String,String> textData;
	
	/**
	 * The background color to show the image against.
	 */
	private Color backgroundColor;
	
	/**
	 * A reference to a palette color for use as the
	 * background.
	 */
	private int paletteBackground;
	
	/**
	 * The resolution of this image.
	 */
	private Resolution resolution;
	
	/**
	 * The number of significant bits in the original image.
	 */
	private Color significantColorBits;
	
	/**
	 * The number of significant bits in the original alpha
	 * channel.
	 */
	private int significantAlphaBits;
	
	/**
	 * The suggested reduced palette for this image if the actual
	 * palette cannot be completely rendered.
	 */
	private Palette reducedPalette;
	
	/**
	 * The time that this was last modified.
	 */
	private Date lastModified;
	
	/**
	 * The image contained by this Png.
	 */
	private Image image;

	/**
	 * Creates a new PNG from a series of chunks.
	 *
	 * @param chunks
	 * The chunks to generate the png from.
	 */
	public PortableNetworkGraphic(Chunk[] chunks) {
		Vector<ImageDataChunk> idatChunks = new Vector<ImageDataChunk>();
		for(Chunk c: chunks) {
			String type = c.getTypeName();
			
			if(type.equals("IHDR")) {
				readHeaderChunk((HeaderChunk)c);
			} else if(type.equals("PLTE")) {
				readPaletteChunk((PaletteChunk)c);
			} else if(type.equals("IDAT")) {
				idatChunks.add((ImageDataChunk)c);
			} else if(type.equals("IEND")) {
				break;//do not read past an iend, even if there is more data.
			} else if(type.equals("tRNS")) {
				readTransparencyChunk((TransparencyChunk)c);
			} else if(type.equals("gAMA")) {
				readGammaChunk((GammaChunk)c);
			} else if(type.equals("cHRM")) {
				readChromaticitiesChunk((ChromaticitiesChunk)c);
			} else if(type.equals("sRGB")) {
				readStandardRgbColorSpaceChunk((StandardRgbColorSpaceChunk)c);
			} else if(type.equals("iCCP")) {
				readEmbeddedColorProfileChunk((EmbeddedColorProfileChunk)c);
			} else if(type.equals("tEXt") || type.equals("zTXt") || type.equals("iTXt")) {
				readTextChunk((TextChunk)c);
			} else if(type.equals("bKGD")) {
				readBackgroundColorChunk((BackgroundColorChunk)c);
			} else if(type.equals("pHYs")) {
				readPhysicalPixelDimensionsChunk((PhysicalPixelDimensionsChunk)c);
			} else if(type.equals("sBIT")) {
				readSignificantBitsChunk((SignificantBitsChunk)c);
			} else if(type.equals("sPLT")) {
				readSuggestedPaletteChunk((SuggestedPaletteChunk)c);
			} else if(type.equals("hIST")) {
				readPaletteHistogramChunk((PaletteHistogramChunk)c);
			} else if(type.equals("tIME")) {
				readLastModificationTimeChunk((LastModificationTimeChunk)c);
			}
		}
		finishChunkDecoding();
		decodeImageData(idatChunks.toArray(new ImageDataChunk[0]));
	}
	
	/**
	 * Gets the image data from this png.
	 *
	 * @return
	 * The image data.
	 */
	public Image getImage() {
		return image;
	}
	
	/**
	 * Builds an image from image data chunks.
	 *
	 * @param chunks
	 * The image chunks that make up the image data.
	 */
	private void decodeImageData(ImageDataChunk[] chunks) {
		byte[] compressedData = extractData(chunks);
		byte[] decompressedData = decompressData(compressedData);
		byte[][] lines = extractScanlines(decompressedData);
		Scanline[] rawData = new Scanline[compressedData.length];
		int samples = samplesPerPixel();
		for(int i = 0; i < lines.length; i++) {
			rawData[i] = Scanline.getInstanceFromFiltered(lines[i], bitDepth, samples);
		}
		constructImage(rawData);
	}
	
	/**
	 * Creates the image from a series of scanlines.
	 *
	 * @param scanlines
	 * The scanlines that make up the image data.
	 */
	private void constructImage(Scanline[] scanlines) {
		switch(colorMode) {
			case COLOR_TYPE_GRAYSCALE:
			case COLOR_TYPE_GRAYSCALE_ALPHA:
				image = constructGrayscaleImage(scanlines);
				break;
				
			case COLOR_TYPE_COLOR:
			case COLOR_TYPE_COLOR_ALPHA:
				image = constructColorImage(scanlines);
				break;
				
			case COLOR_TYPE_COLOR_PALETTE:
				image = constructImageFromPalette(scanlines);
				break;
		}
	}
	
	/**
	 * Creates an image from a series of scanlines using grayscale
	 * mode.
	 *
	 * @param scanline
	 * The scanlines that make up the image.
	 *
	 * @return
	 * The completed image.
	 */
	private Image constructGrayscaleImage(Scanline[] scanlines) {
		boolean hasAlpha = (colorMode == COLOR_TYPE_GRAYSCALE_ALPHA);
		Image img = new Image(width, height, bitDepth, hasAlpha);
		GrayColor color;
		for(int y = 0; y < scanlines.length; y++) {
			int[][] samples = scanlines[y].getSamples();
			for(int x = 0; x < samples.length; x++) {
				color = new GrayColor(bitDepth);
				color.setValue(samples[x][0]);
				if(hasAlpha) {
					color.setAlpha(samples[x][1]);
				} else if(alphaColor.equals(color)) {
					color.setAlpha(0);
				}
				img.setColorAt(x, y, color);
			}
		}
		return img;
	}
	
	/**
	 * Creates an image from a series of scanlines using truecolor
	 * mode.
	 *
	 * @param scanline
	 * The scanlines that make up the image.
	 *
	 * @return
	 * The completed image.
	 */
	private Image constructColorImage(Scanline[] scanlines) {
		boolean hasAlpha = (colorMode == COLOR_TYPE_COLOR_ALPHA);
		Image img = new Image(width, height, bitDepth, hasAlpha);
		Color color;
		for(int y = 0; y < scanlines.length; y++) {
			int[][] samples = scanlines[y].getSamples();
			for(int x = 0; x < samples.length; x++) {
				color = new Color(bitDepth);
				color.setRed(samples[x][0]);
				color.setGreen(samples[x][1]);
				color.setBlue(samples[x][2]);
				if(hasAlpha) {
					color.setAlpha(samples[x][3]);
				} else if(alphaColor.equals(color)) {
					color.setAlpha(0);
				}
				img.setColorAt(x, y, color);
			}
		}
		return img;
	}
	
	/**
	 * Creates an image from a series of scanlines using indexed
	 * mode.
	 *
	 * @param scanline
	 * The scanlines that make up the image.
	 *
	 * @return
	 * The completed image.
	 */
	private Image constructImageFromPalette(Scanline[] scanlines) {
		Image img = new Image(width, height, bitDepth);
		Color color;
		for(int y = 0; y < scanlines.length; y++) {
			int[][] samples = scanlines[y].getSamples();
			for(int x = 0; x < samples.length; x++) {
				color = palette.getColor(samples[x][0]);
				img.setColorAt(x, y, color);
			}
		}
		return img;
	}
	
	/**
	 * Seperates the raw image data into scanline series. Each of
	 * the scanline series will be the filtered byte stream of a
	 * scanline.
	 *
	 * @param data
	 * The raw uncompressed image data.
	 * 
	 * @return
	 * The raw data seperated into scanline series.
	 */
	private byte[][] extractScanlines(byte[] data) {
		int scanlineWidth = getScanlineWidth();
		byte[][] lines = new byte[height][scanlineWidth];
		byte[] buffer = new byte[scanlineWidth];
		int currentByte = 0;
		int currentLine = 0;
		for(int i = 0; i < data.length; i++) {
			lines[currentLine][currentByte++] = data[i];
			if(currentByte == scanlineWidth) {
				currentLine++;
				currentByte = 0;
			}
		}
		return lines;
	}
	
	/**
	 * Gets the length of a scanline in bytes.
	 *
	 * @return
	 * The length of a scanline.
	 */
	private int getScanlineWidth() {
		int sw = (getPixelWidth() * width) + 1;
		return sw;
	}
	
	/**
	 * Gets the length of a pixel in bytes.
	 *
	 * @return
	 * The length of a pixel.
	 */
	private int getPixelWidth() {
		int pw = samplesPerPixel() * (bitDepth / 8);
		return pw;
	}
	
	/**
	 * Gets the number of samples in each pixel.
	 *
	 * @return
	 * The number of samples.
	 */
	private int samplesPerPixel() {
		int samples = 0;
		switch(colorMode) {
			case COLOR_TYPE_GRAYSCALE:
				samples = 1;
				break;
				
			case COLOR_TYPE_COLOR:
				samples = 3;
				break;
				
			case COLOR_TYPE_COLOR_PALETTE:
				samples = 1;
				break;
				
			case COLOR_TYPE_GRAYSCALE_ALPHA:
				samples = 2;
				break;
				
			case COLOR_TYPE_COLOR_ALPHA:
				samples = 4;
				break;
		}
		return samples;
	}
	
	/**
	 * Decompresses a byte array.
	 *
	 * @param data
	 * The compressed data.
	 *
	 * @return
	 * The decompressed data.
	 */
	private byte[] decompressData(byte[] data) {
		ZlibDecompresser decompresser = new ZlibDecompresser(data);
		return decompresser.decompress();
	}
	
	/**
	 * Gets only the raw data from a series of chunks. The
	 * data is then concatenated.
	 *
	 * @param chunks
	 * The chunks whose data is to be concatenated.
	 *
	 * @return
	 * The data as a byte array.
	 */
	private byte[] extractData(Chunk[] chunks) {
		int dataLength = 0;
		for(Chunk c: chunks) {
			dataLength += c.getLength();
		}
		byte[] data = new byte[dataLength];
		int dataPointer = 0;
		byte[] chunkData;
		for(Chunk c: chunks) {
			chunkData = c.getData();
			for(byte b: chunkData) {
				data[dataPointer++] = b;
			}
		}
		return data;
	}
	
	/**
	 * Gets the last modified information from that chunk.
	 *
	 * @param chunk
	 * The last modification time chunk.
	 */
	private void readLastModificationTimeChunk(LastModificationTimeChunk chunk) {
		Calendar c = Calendar.getInstance();
		c.set(chunk.getYear(), chunk.getMonth(), chunk.getDay(), chunk.getHour(), chunk.getMinute(), chunk.getSecond());
		lastModified = c.getTime();
	}
	
	/**
	 * Gets the histogram information from the chunk data.
	 *
	 * @param chunk
	 * The palette histogram chunk.
	 */
	private void readPaletteHistogramChunk(PaletteHistogramChunk chunk) {
		frequencies = chunk.getFrequencies();
	}
	
	/**
	 * Reads the suggested palette from the chunk data.
	 *
	 * @param chunk
	 * The suggested palette chunk.
	 */
	private void readSuggestedPaletteChunk(SuggestedPaletteChunk chunk) {
		Color[] c = chunk.getPaletteEntries();
		int[] f = chunk.getFrequencies();
		reducedPalette = new Palette(chunk.getPaletteName(), chunk.getSampleDepth(), c, f);
	}
	
	/**
	 * Reads the number of significant bits present in the original
	 * file.
	 *
	 * @param chunk
	 * The SignificantBitsChunk.
	 */
	private void readSignificantBitsChunk(SignificantBitsChunk chunk) {
		significantColorBits = chunk.getColorBits();
		if(colorMode == COLOR_TYPE_GRAYSCALE_ALPHA || colorMode == COLOR_TYPE_COLOR_ALPHA) {
			significantAlphaBits = chunk.getAlphaBits();
		}
	}
	
	/**
	 * Reads a header chunk and assigns properties based on it.
	 *
	 * @param header
	 * The header chunk.
	 */
	private void readHeaderChunk(HeaderChunk header) {
		bitDepth = header.getBitDepth();
		height = header.getHeight();
		width = header.getWidth();
		colorMode = header.getColorType();
	}
	
	/**
	 * Reads dimensions.
	 *
	 * @param chunk
	 * The dimension chunk.
	 */
	private void readPhysicalPixelDimensionsChunk(PhysicalPixelDimensionsChunk chunk) {
		resolution = new Resolution(chunk.getWidth(), chunk.getHeight());
	}
	
	/**
	 * Reads some text data into this png.
	 *
	 * @param chunk
	 * The chunk containing text data.
	 */
	private void readTextChunk(TextChunk chunk) {
		if(textData == null) {
			textData = new HashMap<String,String>();
		}
		textData.put(chunk.getKeyword(), chunk.getText());
	}
	
	/**
	 * Reads an embedded color profile.
	 *
	 * @param chunk
	 * The ICCP chunk.
	 */
	private void readEmbeddedColorProfileChunk(EmbeddedColorProfileChunk chunk) {
		profile = new IccProfile(chunk.getProfileName(), chunk.getProfile());
	}
	
	/**
	 * Reads a palette chunk and assigns properties based on it.
	 *
	 * @param chunk
	 * The palette chunk.
	 */
	private void readPaletteChunk(PaletteChunk chunk) {
		colorPalette = chunk.getPalette();
	}
	
	/**
	 * Reads a chromaticites chunk and assigns properties based
	 * on it.
	 *
	 * @param chunk
	 * The chromaticites chunk.
	 */
	private void readChromaticitiesChunk(ChromaticitiesChunk chunk) {
		Point r = chunk.getRed();
		Point g = chunk.getGreen();
		Point b = chunk.getBlue();
		Point w = chunk.getWhitePoint();
		chromaticity = new Chromaticity(r, g, b, w);
	}
	
	/**
	 * Reads a transparency chunk and assigns properties based
	 * on it.
	 *
	 * @param chunk
	 * The transparency chunk.
	 */
	private void readTransparencyChunk(TransparencyChunk chunk) {
		chunk.parseWithColorMode(colorMode);
		if(chunk.getColorMode() == colorMode) {
			switch(chunk.getColorMode()) {
				case COLOR_TYPE_GRAYSCALE:
				case COLOR_TYPE_COLOR:
					alphaColor = chunk.getAlpha();
					break;
					
				case COLOR_TYPE_COLOR_PALETTE:
					alphaPalette = chunk.getPaletteAlphas();
					break;
			}
		} else {
			throw new RuntimeException("Color mode mismatch!");
		}
	}
	
	/**
	 * Reads a gamma chunk and assigns properties based on it.
	 *
	 * @param chunk
	 * The gamma chunk.
	 */
	private void readGammaChunk(GammaChunk chunk) {
		gamma = chunk.getGamma();
	}
	
	/**
	 * Reads a standard RGB color space chunk and assigns
	 * properties based on it.
	 *
	 * @param chunk
	 * The standard RGB color space chunk.
	 */
	private void readStandardRgbColorSpaceChunk(StandardRgbColorSpaceChunk chunk) {
		renderingIntent = chunk.getRenderingIntent();
	}
	
	/**
	 * Reads the background color from a chunk.
	 *
	 * @param chunk.
	 * The background color chunk.
	 */
	private void readBackgroundColorChunk(BackgroundColorChunk chunk) {
		if(chunk.getColorMode() == colorMode) {
			switch(chunk.getColorMode()) {
				case COLOR_TYPE_GRAYSCALE:
				case COLOR_TYPE_COLOR:
					backgroundColor = chunk.getColor();
					break;
					
				case COLOR_TYPE_COLOR_PALETTE:
					paletteBackground = chunk.getPaletteIndex();
					break;
			}
		} else {
			throw new RuntimeException("Color mode mismatch!");
		}
	}
	
	/**
	 * Assembles partial components of read objects into their
	 * completed versions.
	 */
	private void finishChunkDecoding() {
		combinePaletteComponents();
	}
	
	/**
	 * Combines the components of the palette into a completed palette.
	 */
	private void combinePaletteComponents() {
		Color[] colorList = new Color[colorPalette.length];
		Arrays.fill(colorList, new Color(bitDepth));
		if(alphaPalette != null) {
			for(int i = 0; i < colorPalette.length; i++) {
				colorList[i].setRed(colorPalette[i].getRed());
				colorList[i].setGreen(colorPalette[i].getGreen());
				colorList[i].setBlue(colorPalette[i].getBlue());
				colorList[i].setAlpha(alphaPalette[i]);
			}
		} else {
			colorList = colorPalette;
		}
		palette = new Palette("untitled", bitDepth, colorList, frequencies);
	}
}