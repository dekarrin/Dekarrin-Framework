package com.dekarrin.file.png;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.dekarrin.error.ValueOutOfRangeException;
import com.dekarrin.graphics.Chromaticity;
import com.dekarrin.graphics.Color;
import com.dekarrin.graphics.ColorProfile;
import com.dekarrin.graphics.GrayColor;
import com.dekarrin.graphics.Image;
import com.dekarrin.graphics.Palette;
import com.dekarrin.graphics.Resolution;
import com.dekarrin.io.InvalidFormatException;
import com.dekarrin.io.StreamFailureException;
import com.dekarrin.util.ArrayHelper;
import com.dekarrin.util.ByteHolder;

/**
 * Represents a PNG file. This class is attempting to be compliant
 * with version 1.2 of the PNG standard; it's not there yet.
 */
public class PortableNetworkGraphic {
	
	/**
	 * The number of bytes in each image data chunk.
	 */
	public static final int IDAT_BUFFER_LENGTH = 50;
	
	/**
	 * The amount of data that can be written uncompressed in
	 * bytes. This is used when compression depends on data
	 * size.
	 */
	public static final int UNCOMPRESSED_DATA_LIMIT = 1024;
	
	/**
	 * The background color to show the image against.
	 */
	private Color backgroundColor;
	
	/**
	 * A reference to a palette color for use as the
	 * background.
	 */
	private int backgroundColorIndex;
	
	/**
	 * The sample depth of this image.
	 */
	private int sampleDepth;
	
	/**
	 * The color space for this Image;
	 */
	private Chromaticity chromaticity;
	
	/**
	 * The color mode of this image.
	 */
	private ColorMode mode;
	
	/**
	 * The compression method used in this PNG's image data.
	 */
	private CompressionEngine compressionEngine;
	
	/**
	 * Whether or not critical Chunks have been modified.
	 */
	private boolean criticallyModified = false;
	
	/**
	 * The image data chunks. These are preserved in case unknown unsafe
	 * to copy chunks exist in the PNG.
	 */
	private Vector<ImageDataChunk> dataChunks = new Vector<ImageDataChunk>();
	
	/**
	 * The filter method used in this PNG's image data.
	 */
	private FilterMethod filterMethod;
	
	/**
	 * The image gamma.
	 */
	private int gamma;
	
	/**
	 * Whether the image gamma is set.
	 */
	private boolean gammaSet = false;
	
	/**
	 * The height of this image.
	 */
	private int height;
	
	/**
	 * The image contained by this PNG.
	 */
	private Image image;
	/**
	 * What method of interlacing this PNG uses.
	 */
	private InterlaceMethod interlaceMethod;
	
	/**
	 * The time that this was last modified.
	 */
	private Date lastModified;
	
	/**
	 * The complete palette of this image. This includes
	 * the histogram, if it exists.
	 */
	private Palette palette;
	
	/**
	 * The palette alpha entries. Only set if there is a
	 * palette.
	 */
	private int[] paletteAlphas;
	
	/**
	 * The palette color entries. Only set if there is a
	 * palette.
	 */
	private Color[] paletteColors;
	
	/**
	 * The palette frequency entries. Only set if there is
	 * a palette.
	 */
	private int[] paletteFrequencies;
	
	/**
	 * The color profile for this PNG.
	 */
	private ColorProfile profile;
	
	/**
	 * The suggested reduced palette for this image if the actual
	 * palette cannot be completely rendered.
	 */
	private Palette reducedPalette;
	
	/**
	 * The rendering intent of this image.
	 */
	private RenderingIntent renderingIntent;
	
	/**
	 * Whether the rendering intent is set.
	 */
	private boolean renderingIntentSet = false;
	
	/**
	 * The resolution of this image.
	 */
	private Resolution resolution;
	
	/**
	 * The number of significant bits in the original alpha
	 * channel.
	 */
	private int significantAlphaBits;
	
	/**
	 * Whether the significant bits were set.
	 */
	private boolean significantBitsSet = false;
	
	/**
	 * The number of significant bits in the original image.
	 */
	private Color significantColorBits;
	
	/**
	 * The textual data from this PNG.
	 */
	private HashMap<String,ArrayList<String>> textData;
	
	/**
	 * The color to be used as fully transparent.
	 */
	private Color transparentColor;
	
	/**
	 * The pointer to the unknown chunks container.
	 */
	private Vector<Chunk> unknownChunks;
	
	/**
	 * Any chunks read before PLTE that are not understood.
	 */
	private Vector<Chunk> unknownColorSpaceChunks = new Vector<Chunk>();

	/**
	 * Any chunks read after IDAT that are not understood.
	 */
	private Vector<Chunk> unknownPostDataChunks = new Vector<Chunk>();
	
	/**
	 * Any chunks read after PLTE but before IDAT that are not
	 * understood.
	 */
	private Vector<Chunk> unknownPreDataChunks = new Vector<Chunk>();
	
	/**
	 * The width of this image.
	 */
	private int width;
	
	/**
	 * Creates a new PNG file from an image.
	 *
	 * @param image
	 * The Image to create the file from.
	 *
	 * @param colorMode
	 * Which color mode should be used for saving the image.
	 */
	public PortableNetworkGraphic(Image image, ColorMode colorMode) {
		this.image = image;
		width = image.width;
		height = image.height;
		sampleDepth = image.sampleDepth;
		mode = colorMode;
		compressionEngine = CompressionEngine.ZLIB;
		filterMethod = FilterMethod.ADAPTIVE;
		interlaceMethod = InterlaceMethod.NONE;
		switch(colorMode) {
			case GRAYSCALE:
				if(sampleDepth != 1 && sampleDepth != 2 && sampleDepth != 4 && sampleDepth != 8 && sampleDepth != 16) {
					throw new ValueOutOfRangeException("Cannot set bit depth to "+sampleDepth+" in grayscale mode.");
				}
				break;
			
			case INDEXED:
				if(sampleDepth != 8 && sampleDepth != 4 && sampleDepth != 2 && sampleDepth != 1) {
					throw new ValueOutOfRangeException("Cannot set bit depth to "+sampleDepth+" in indexed mode.");
				}
				break;
				
			case GRAYSCALE_ALPHA:
			case TRUECOLOR:
			case TRUECOLOR_ALPHA:
				if(sampleDepth != 8 && sampleDepth != 16) {
					throw new ValueOutOfRangeException("Cannot set bit depth to "+sampleDepth+" in current color mode.");
				}
				break;
		}
	}
	
	/**
	 * Creates a new PNG from a file.
	 *
	 * @param file
	 * The file to load the PNG data from.
	 *
	 * @throws FileNotFoundException
	 * If the specified file does not exist.
	 *
	 * @throws InvalidFormatException
	 * If the file is corrupt or is not in the correct
	 * format for a PNG.
	 *
	 * @throws StreamFailureException
	 * If the file stream fails for some other reason.
	 */
	public PortableNetworkGraphic(String file) throws FileNotFoundException, InvalidFormatException, StreamFailureException {
		loadPngFile(file);
	}
	
	/**
	 * Adds comment text data to this PNG. This is inserted with
	 * the generic tag 'Comment'.
	 *
	 * @param message
	 * The text data to add.
	 */
	public void addText(String message) {
		addText("Comment", message);
	}
	
	/**
	 * Adds text data to this PNG.
	 *
	 * @param keyword
	 * The keyword of the text data.
	 *
	 * @param message
	 * The text data to add.
	 */
	public void addText(String keyword, String message) {
		if(textData == null) {
			textData = new HashMap<String,ArrayList<String>>(1);
		}
		keyword = makeKeywordValid(keyword);
		ArrayList<String> list;
		if(textData.containsKey(keyword)) {
			list = textData.get(keyword);
		} else {
			list = new ArrayList<String>(1);
			textData.put(keyword, list);
		}
		list.add(message);
	}
	
	/**
	 * Copies the image data of this PNG to disk. Its image data is
	 * copied from the original source data unmodified.
	 * 
	 * @param location
	 * Where to save the PNG file.
	 *
	 * @throws StreamFailureException
	 * If the file stream failed.
	 */
	public void copy(String location) throws StreamFailureException, InvalidFormatException {
		writePngFile(location, true);
	}
	
	/**
	 * Gets the aspect ratio of the pixels. This is always expressed
	 * as the ratio of the width to the height. To set the aspect
	 * ratio, mearly change the resolution with setPixelDimensions().
	 *
	 * @return
	 * The ratio of the pixel width to the pixel height.
	 */
	public double getAspectRatio() {
		double ratio = (double)resolution.x / (double)resolution.y;
		return ratio;
	}
	
	/**
	 * Gets the background color to show this image against.
	 *
	 * @return
	 * What the background color should be.
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	
	/**
	 * Gets the bit depth of the image.
	 *
	 * @return
	 * The bit depth.
	 */
	public int getBitDepth() {
		return image.sampleDepth;
	}
	
	/**
	 * Gets the chromaticity for this PNG image.
	 *
	 * @return
	 * The chromaticity used.
	 */
	public Chromaticity getChromaticity() {
		return chromaticity;
	}
	
	/**
	 * Gets the color mode of this PNG.
	 *
	 * @return
	 * The color mode.
	 */
	public ColorMode getColorMode() {
		return mode;
	}
	
	/**
	 * Checks which compression method is being used by the
	 * PNG.
	 *
	 * @return
	 * The compression method.
	 */
	public CompressionEngine getCompressionEngine() {
		return compressionEngine;
	}
	
	/**
	 * Gets the filtering method used in this PNG.
	 *
	 * @return
	 * The filtering method.
	 */
	public FilterMethod getFilterMethod() {
		return filterMethod;
	}

	/**
	 * Gets the gamma of this PNG image.
	 *
	 * @return
	 * The gamma.
	 */
	public int getGamma() {
		return gamma;
	}
	
	/**
	 * Gets the height of the image.
	 *
	 * @return
	 * The height.
	 */
	public int getHeight() {
		return image.height;
	}
	
	/**
	 * Gets the image data from this PNG. This modifies the PNG so
	 * that unsafe-to-copy unknown chunks cannot be copied, because
	 * if the image is accessed, it can be changed.
	 *
	 * @return
	 * The image data.
	 */
	public Image getImage() {
		criticallyModify();
		return image;
	}
	
	/**
	 * Gets all keywords in this PNG that have text
	 * data associated with them.
	 *
	 * @return
	 * The used keywords.
	 */
	public String[] getKeywords() {
		return textData.keySet().toArray(new String[0]);
	}
	
	/**
	 * Gets the last modification time of this PNG.
	 *
	 * @return
	 * The last modification time.
	 */
	public Date getModificationDate() {
		return lastModified;
	}
	
	/**
	 * Gets the palette for this image.
	 *
	 * @return
	 * The palette used for the image.
	 */
	public Palette getPalette() {
		return palette;
	}
	
	/**
	 * Gets the International Color Consortium Color profile
	 * for this PNG.
	 *
	 * @return
	 * The profile.
	 */
	public ColorProfile getProfile() {
		return profile;
	}
	
	/**
	 * Gets the suggested reduced color palette for use on devices
	 * that cannot support the full range of colors in the full palette.
	 *
	 * @return
	 * The suggested palette.
	 */
	public Palette getReducedPalette() {
		return reducedPalette;
	}
	
	/**
	 * Gets the rendering intent for this PNG image.
	 *
	 * @return
	 * The rendering intent.
	 */
	public RenderingIntent getRenderingIntent() {
		return renderingIntent;
	}
	
	/**
	 * Gets the resolution of this image as a number of pixels
	 * per inch.
	 *
	 * @return
	 * The resolution.
	 */
	public int getResolution() {
		int res = resolution.x * resolution.y;
		return res;
	}
	
	/**
	 * Gets the number of significant bits used for the alpha channel
	 * from the original image data.
	 *
	 * @return
	 * The number of significant bits in the alpha channel.
	 */
	public int getSignificantAlphaBits() {
		return significantColorBits.getAlpha();
	}
	
	/**
	 * Gets the number of significant bits used for the blue channel
	 * from the original image data.
	 *
	 * @return
	 * The number of significant bits in the blue channel.
	 */
	public int getSignificantBlueBits() {
		return significantColorBits.getBlue();
	}
	
	/**
	 * Gets the number of significant bits used for the grayscale
	 * values from the original image data.
	 *
	 * @return
	 * The number of significant bits in the grayscale values.
	 */
	public int getSignificantGrayscaleBits() {
		return significantColorBits.getRed();
	}
	
	/**
	 * Gets the number of significant bits used for the green channel
	 * from the original image data.
	 *
	 * @return
	 * The number of significant bits in the green channel.
	 */
	public int getSignificantGreenBits() {
		return significantColorBits.getGreen();
	}
	
	/**
	 * Gets the number of significant bits used for the red channel
	 * from the original image data.
	 *
	 * @return
	 * The number of significant bits in the red channel.
	 */
	public int getSignificantRedBits() {
		return significantColorBits.getRed();
	}
	
	/**
	 * Gets text data from this PNG.
	 *
	 * @param keyword
	 * The keyword to get the data for.
	 * 
	 * @return
	 * All text data associated with the specified
	 * keyword.
	 */
	public String[] getText(String keyword) {
		ArrayList<String> list = textData.get(keyword);
		String[] textDataList = list.toArray(new String[0]);
		return textDataList;
	}
	
	/**
	 * Gets the color to use as the transparent.
	 *
	 * @return
	 * The color value used as the transparency.
	 */
	public Color getTransparentColor() {
		return transparentColor;
	}
	
	/**
	 * Gets all the chunks that this PNG was unable to parse.
	 * This is useful for classes that might extend
	 * PortableNetworkGraphic.
	 *
	 * @return
	 * The unrecognized chunks.
	 */
	public Chunk[] getUnknownChunks() {
		Chunk[] unknown1 = unknownColorSpaceChunks.toArray(new Chunk[0]);
		Chunk[] unknown2 = unknownPreDataChunks.toArray(new Chunk[0]);
		Chunk[] unknown3 = unknownPostDataChunks.toArray(new Chunk[0]);
		Chunk[] combined = new Chunk[unknown1.length + unknown2.length + unknown3.length];
		int k = 0;
		for(int i = 0; i < unknown1.length; i++) {
			combined[k++] = unknown1[i];
		}
		for(int i = 0; i < unknown2.length; i++) {
			combined[k++] = unknown2[i];
		}
		for(int i = 0; i < unknown3.length; i++) {
			combined[k++] = unknown3[i];
		}
		return combined;
	}
	
	/**
	 * Gets the width of the image.
	 *
	 * @return
	 * The width.
	 */
	public int getWidth() {
		return image.width;
	}
	
	/**
	 * Checks to see if this PNG has a background color.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasBackgroundColor() {
		return (backgroundColor != null);
	}
	
	/**
	 * Checks to see if this PNG has chromaticity data.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasChromaticity() {
		return (chromaticity != null);
	}
	
	/**
	 * Checks to see if this PNG has gamma set.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasGamma() {
		return gammaSet;
	}
	
	/**
	 * Checks to see if this PNG has a histogram.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasHistogram() {
		boolean has = false;
		if(hasPalette()) {
			if(palette.hasFrequencies()) {
				has = true;
			}
		}
		return has;
	}
	
	/**
	 * Checks to see if this PNG has a palette.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasPalette() {
		return (mode == ColorMode.INDEXED);
	}
	
	/**
	 * Checks to see if this PNG has a color profile.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasProfile() {
		return (profile != null);
	}
	
	/**
	 * Checks to see if this PNG has a rendering intent.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasRenderingIntent() {
		return renderingIntentSet;
	}
	
	/**
	 * Checks to see if this PNG has resolution data.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasResolution() {
		return (resolution != null);
	}
	
	/**
	 * Checks to see if this PNG has significat bits
	 * data.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasSignificantBits() {
		return significantBitsSet;
	}
	
	/**
	 * Checks to see if this PNG has a suggested palette.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasSuggestedPalette() {
		return (reducedPalette != null);
	}
	
	/**
	 * Checks to see if this PNG has text data.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasTextData() {
		return (textData != null);
	}
	
	/**
	 * Checks to see if this PNG should output
	 * a tRNS chunk.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasTransparencyData() {
		boolean has;
		switch(mode) {
			case TRUECOLOR:
			case GRAYSCALE:
				has = (transparentColor != null);
				break;
				
			case INDEXED:
				has = (paletteAlphas != null);
				break;
			
			default:
				has = false;
		}
		return has;
	}
	
	/**
	 * Checks whether the image is interlaced.
	 *
	 * @return
	 * Whether it is.
	 */
	public boolean isInterlaced() {
		return (interlaceMethod != InterlaceMethod.NONE);
	}
	
	/**
	 * Loads this PNG from disk. The disk data is read, the image
	 * data is defiltered, compressable chunks are decompressed,
	 * and the chunks are converted into object data.
	 *
	 * @param location
	 * Where to load the PNG file from.
	 *
	 * @throws FileNotFoundException
	 * If the specified file does not exist.
	 *
	 * @throws InvalidFormatException
	 * If the file is corrupt or is not in the correct
	 * format for a PNG.
	 *
	 * @throws StreamFailureException
	 * If the file stream fails for some other reason.
	 */
	public void load(String location) throws FileNotFoundException, InvalidFormatException, StreamFailureException {
		loadPngFile(location);
	}
	
	/**
	 * Saves this PNG to disk. Its data is converted into Chunks,
	 * compressible chunks are compressed, and the image data is
	 * filtered. The resulting Chunk array is then written to a
	 * file.
	 *
	 * @param location
	 * Where to save the PNG file.
	 *
	 * @throws StreamFailureException
	 * If the file stream failed.
	 * 
	 * @throws InvalidFormatException
	 * If an invalid value is written to the PNG.
	 */
	public void save(String location) throws StreamFailureException, InvalidFormatException {
		writePngFile(location, false);
	}
	
	/**
	 * Sets the background color to show this image against.
	 *
	 * @param backgroundColor
	 * What the background color should be.
	 */
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	/**
	 * Sets the chromaticity for this PNG image.
	 *
	 * @param chromaticity
	 * The chromaticity to use.
	 */
	public void setChromaticity(Chromaticity chromaticity) {
		this.chromaticity = chromaticity;
	}
	
	/**
	 * Sets the gamma of this PNG image.
	 *
	 * @param gamma
	 * The gamma.
	 */
	public void setGamma(int gamma) {
		this.gamma = gamma;
		gammaSet = true;
	}
	
	/**
	 * Sets the last modified time of this PNG to the current time.
	 */
	public void setModified() {
		lastModified = new Date();
	}
	
	/**
	 * Sets the palette for this PNG.
	 *
	 * @param palette
	 * The palette to be used for the image.
	 */
	public void setPalette(Palette palette) {
		this.palette = palette;
	}
	
	/**
	 * Changes the resolution of the image. The given values will
	 * be converted from dpi to meters before being stored. If
	 * the given values are not the same, this also changes the
	 * pixel aspect ratio.
	 *
	 * @param x
	 * The x dimension of pixels per inch.
	 *
	 * @param y
	 * The y dimension of pixels per inch.
	 */
	public void setPixelDimensions(int x, int y) {
		int width = (int)(x / 0.0254); // default and only unit is the meter; 1in = 0.0254m.
		int height = (int)(y / 0.0254);
		resolution = new Resolution(width, height);
	}
	
	/**
	 * Sets the International Color Consortium Color profile
	 * for this PNG.
	 *
	 * @param profile
	 * The profile.
	 */
	public void setProfile(ColorProfile profile) {
		this.profile = profile;
	}
	
	/**
	 * Sets a suggested reduced color palette for use on devices
	 * that cannot support the full range of colors.
	 *
	 * @param reducedPalette
	 * The suggested palette.
	 */
	public void setReducedPalette(Palette reducedPalette) {
		this.reducedPalette = reducedPalette;
	}
	
	/**
	 * Sets the rendering intent for this PNG image.
	 *
	 * @param renderingIntent
	 * The rendering intent.
	 */
	public void setRenderingIntent(RenderingIntent renderingIntent) {
		this.renderingIntent = renderingIntent;
		renderingIntentSet = true;
	}
	
	/**
	 * Sets a single color to use as transparent. This is only
	 * effectve if the color mode does not already have an
	 * alpha channel.
	 *
	 * @param color
	 * The color value to use as the transparency.
	 */
	public void setTransparentColor(Color color) {
		transparentColor = color;
	}
	
	/**
	 * Loads an unknown chunk.
	 * 
	 * @param chunk
	 * The unknown chunk.
	 */
	public void unknownChunkProcessed(Chunk chunk) throws UnknownChunkException {
		if(chunk.isAncillary()) {
			unknownChunks.add(chunk);
		} else {
			throw new UnknownChunkException("Unknown chunk type '"+chunk.getTypeName()+"'", chunk.getTypeName());
		}
	}
	
	/**
	 * Sets this PNG to not copy unsafe to copy chunks when
	 * it saves.
	 */
	protected void criticallyModify() {
		criticallyModified = true;
	}
	
	/**
	 * Converts the background color into a chunk.
	 *
	 * @return
	 * The resulting BackgroundColorChunk.
	 */
	private BackgroundColorChunk backgroundColorToChunk() {
		BackgroundColorChunk bcc;
		if(mode == ColorMode.INDEXED) {
			bcc = new BackgroundColorChunk(palette.indexOf(backgroundColor));
		} else {
			bcc = new BackgroundColorChunk(backgroundColor, mode);
		}
		return bcc;
	}
	
	/**
	 * Converts the Chromaticity to a chunk.
	 *
	 * @return
	 * The chunk containing the chromaticity.
	 */
	private ChromaticitiesChunk chromaticityToChunk() {
		int wx = chromaticity.getWhitePointX();
		int wy = chromaticity.getWhitePointY();
		int rx = chromaticity.getRedX();
		int ry = chromaticity.getRedY();
		int gx = chromaticity.getGreenX();
		int gy = chromaticity.getGreenY();
		int bx = chromaticity.getBlueX();
		int by = chromaticity.getBlueY();
		ChromaticitiesChunk cc = new ChromaticitiesChunk(new Point(wx, wy), new Point(rx, ry), new Point(gx, gy), new Point(bx, by));
		return cc;
	}
	
	/**
	 * Assembles partial components of read objects into their
	 * completed versions.
	 */
	private void cleanUpDataStructures() {
		combinePaletteComponents();
		combineSignificantBits();
		setIndexedBackgroundColor();
		Scanline.resetLines();
	}
	
	/**
	 * Combines the components of the palette into a completed palette.
	 */
	private void combinePaletteComponents() {
		if(paletteColors != null) {
			Color[] colorList = new Color[paletteColors.length];
			Arrays.fill(colorList, new Color(sampleDepth));
			if(paletteAlphas != null) {
				int i = 0;
				for(; i < paletteAlphas.length; i++) {
					colorList[i].setRed(paletteColors[i].getRed());
					colorList[i].setGreen(paletteColors[i].getGreen());
					colorList[i].setBlue(paletteColors[i].getBlue());
					colorList[i].setAlpha(paletteAlphas[i]);
				}
				for(; i < paletteColors.length; i++) {
					colorList[i].setRed(paletteColors[i].getRed());
					colorList[i].setGreen(paletteColors[i].getGreen());
					colorList[i].setBlue(paletteColors[i].getBlue());
				}
			} else {
				colorList = paletteColors;
			}
			palette = new Palette("untitled", sampleDepth, colorList, paletteFrequencies);
		}
	}
	
	/**
	 * Combines the alpha significant bits with the main color.
	 */
	private void combineSignificantBits() {
		if(significantColorBits != null) {
			if(significantAlphaBits != 0) {
				significantColorBits.setAlpha(significantAlphaBits);
			}
		}
	}
	
	/**
	 * Compresses a byte array.
	 *
	 * @param data
	 * The uncompressed data.
	 *
	 * @return
	 * The compressed data.
	 * 
	 * @throws InvalidFormatException
	 * If the compression method specified by this PNG
	 * is invalid.
	 */
	private byte[] compressData(byte[] data) throws InvalidFormatException {
		compressionEngine.setContents(data);
		return compressionEngine.compress();
	}
	
	/**
	 * Concatenates all the image data into one array.
	 * 
	 * @return
	 * The data contents of the image data.
	 */
	private byte[] concatenateImageData() {
		byte[] iData = new byte[0];
		byte[] nextData;
		for(ImageDataChunk c: dataChunks) {
			nextData = c.getData();
			iData = ArrayHelper.append(iData, nextData);
		}
		return iData;
	}
	
	/**
	 * Creates an image from a series of scanlines using truecolor
	 * mode.
	 *
	 * @param scanlines
	 * The scanlines that make up the image.
	 *
	 * @return
	 * The completed image.
	 */
	private void constructColorImage(Scanline[] scanlines) {
		boolean hasAlpha = (mode == ColorMode.TRUECOLOR_ALPHA);
		image = new Image(width, height, sampleDepth, hasAlpha);
		Color color = new Color(sampleDepth);
		for(int y = 0; y < scanlines.length; y++) {
			for(int x = 0; x < scanlines[y].pixels(); x++) {
				color.setRed(scanlines[y].getSample(x, Scanline.RED_SAMPLE));
				color.setGreen(scanlines[y].getSample(x, Scanline.GREEN_SAMPLE));
				color.setBlue(scanlines[y].getSample(x, Scanline.BLUE_SAMPLE));
				if(hasAlpha) {
					color.setAlpha(scanlines[y].getSample(x, Scanline.ALPHA_SAMPLE));
				} else if(transparentColor != null && transparentColor.equals(color)) {
					color.setAlpha(0);
				}
				image.setColorAt(x, y, color);
			}
		}
	}
	
	/**
	 * Creates an image from a series of scanlines using grayscale
	 * mode.
	 *
	 * @param scanlines
	 * The scanlines that make up the image.
	 *
	 * @return
	 * The completed image.
	 */
	private void constructGrayscaleImage(Scanline[] scanlines) {
		boolean hasAlpha = (mode == ColorMode.GRAYSCALE_ALPHA);
		image = new Image(width, height, sampleDepth, hasAlpha);
		GrayColor color = new GrayColor(sampleDepth);
		for(int y = 0; y < scanlines.length; y++) {
			for(int x = 0; x < scanlines[y].pixels(); x++) {
				color.setValue(scanlines[y].getSample(x, Scanline.GRAYSCALE_VALUE_SAMPLE));
				if(hasAlpha) {
					color.setAlpha(scanlines[y].getSample(x, Scanline.GRAYSCALE_ALPHA_SAMPLE));
				} else if(transparentColor != null && transparentColor.equals(color)) {
					color.setAlpha(0);
				}
				image.setColorAt(x, y, color);
			}
		}
	}
	 
	/**
	 * Builds the image from a series of scanlines.
	 *
	 * @param scanlines
	 * The scanlines that make up the image data.
	 */
	private void constructImage(Scanline[] scanlines) {
		switch(mode) {
			case GRAYSCALE:
			case GRAYSCALE_ALPHA:
				constructGrayscaleImage(scanlines);
				break;
				
			case TRUECOLOR:
			case TRUECOLOR_ALPHA:
				constructColorImage(scanlines);
				break;
				
			case INDEXED:
				constructIndexedImage(scanlines);
				break;
		}
	}
	
	/**
	 * Creates an image from a series of scanlines using indexed
	 * mode.
	 *
	 * @param scanlines
	 * The scanlines that make up the image.
	 *
	 * @return
	 * The completed image.
	 */
	private void constructIndexedImage(Scanline[] scanlines) {
		image = new Image(width, height, sampleDepth);
		Color color;
		for(int y = 0; y < scanlines.length; y++) {
			for(int x = 0; x < scanlines[y].pixels(); x++) {
				int paletteIndex = scanlines[y].getSample(x, Scanline.PALETTE_INDEX_SAMPLE);
				color = palette.getColor(paletteIndex);
				image.setColorAt(x, y, color);
			}
		}
	}
	
	/**
	 * Converts the data in this PNG into chunks to be written
	 * to disk.
	 * 
	 * @param forcePreservation
	 * Whether or not the image data is preserved from the source.
	 * 
	 * @throws InvalidFormatException
	 * If one of the chunks specifies an incorrect value for the
	 * PNG standard.
	 */
	private Chunk[] convertToChunks(boolean forcePreservation) throws InvalidFormatException {
		Vector<Chunk> chunks = new Vector<Chunk>();
		chunks.add(headerToChunk());
		if(unknownColorSpaceChunks.size() > 0) {
			for(Chunk c: unknownColorSpaceChunks) {
				if(c.isSafeToCopy() || !criticallyModified) {
					chunks.add(c);
				}
			}
		}
		if(hasChromaticity()) {
			chunks.add(chromaticityToChunk());
		}
		if(hasGamma()) {
			chunks.add(gammaToChunk());
		}
		if(hasProfile()) {
			chunks.add(profileToChunk());
		}
		if(hasSignificantBits()) {
			chunks.add(significantBitsToChunk());
		}
		if(hasRenderingIntent()) {
			chunks.add(renderingIntentToChunk());
		}
		if(hasPalette()) {
			chunks.add(paletteToChunk());
		}
		if(unknownPreDataChunks.size() > 0) {
			for(Chunk c: unknownPreDataChunks) {
				if(c.isSafeToCopy() || !criticallyModified) {
					chunks.add(c);
				}
			}
		}
		if(hasBackgroundColor()) {
			chunks.add(backgroundColorToChunk());
		}
		if(hasHistogram()) {
			chunks.add(histogramToChunk());
		}
		if(hasTransparencyData()) {
			chunks.add(transparencyDataToChunk());
		}
		if(hasResolution()) {
			chunks.add(resolutionToChunk());
		}
		if(hasSuggestedPalette()) {
			chunks.add(suggestedPaletteToChunk());
		}
		if(forcePreservation || preserveImageData()) {
			for(ImageDataChunk idc: dataChunks) {
				chunks.add(idc);
			}
		} else {
			ImageDataChunk[] idatChunks = encodeImageData();
			for(ImageDataChunk idc: idatChunks) {
				chunks.add(idc);
			}
		}
		if(unknownPostDataChunks.size() > 0) {
			for(Chunk c: unknownPostDataChunks) {
				if(c.isSafeToCopy() || !criticallyModified) {
					chunks.add(c);
				}
			}
		}
		if(hasTextData()) {
			TextChunk[] textDataChunks = textDataToChunks();
			for(TextChunk tdc: textDataChunks) {
				chunks.add(tdc);
			}
		}
		setModified();
		chunks.add(modificationTimeToChunk());
		chunks.add(new TrailerChunk());
		return chunks.toArray(new Chunk[0]);
	}
	
	/**
	 * Creates as many IDAT chunks as necessary from a series
	 * of bytes.
	 *
	 * @param data
	 * The data that makes up the image, pre-compressed and
	 * pre-filtered.
	 *
	 * @return
	 * The IDAT chunks generated from the data.
	 */
	private ImageDataChunk[] createIdatChunks(byte[] data) {
		int dataLength = data.length / IDAT_BUFFER_LENGTH;
		if(data.length % IDAT_BUFFER_LENGTH != 0) {
			dataLength++;
		}
		ImageDataChunk[] chunks = new ImageDataChunk[dataLength];
		int chunksPointer = 0;
		ByteHolder idatBuffer;
		for(int i = 0; i < data.length; i += IDAT_BUFFER_LENGTH) {
			if(i + IDAT_BUFFER_LENGTH < data.length) {
				idatBuffer = new ByteHolder(IDAT_BUFFER_LENGTH);
			} else {
				idatBuffer = new ByteHolder(data.length - i);
			}
			for(int j = 0; j < IDAT_BUFFER_LENGTH && i+j < data.length; j++) {
				idatBuffer.add(data[i+j]);
			}
			chunks[chunksPointer++] = new ImageDataChunk(idatBuffer.toArray());
		}
		return chunks;
	}
	
	/**
	 * Decompresses a byte array.
	 *
	 * @param data
	 * The compressed data.
	 *
	 * @return
	 * The decompressed data.
	 * 
	 * @throws InvalidFormatException
	 * If the compression method specified by this PNG
	 * is invalid.
	 */
	private byte[] decompressData(byte[] data) throws InvalidFormatException {
		compressionEngine.setContents(data);
		return compressionEngine.decompress();
	}
	
	/**
	 * Creates a series of scanlines from an image using truecolor
	 * mode.
	 *
	 * @param img
	 * The completed image.
	 *
	 * @return
	 * The scanlines that make up the image.
	 */
	private Scanline[] deconstructColorImage(Image img) {
		boolean hasAlpha = (mode == ColorMode.TRUECOLOR_ALPHA);
		Scanline[] lines = new Scanline[height];
		Color color;
		for(int y = 0; y < height; y++) {
			lines[y] = new Scanline(mode.samples(), sampleDepth, width, filterMethod);
			for(int x = 0; x < width; x++) {
				color = img.getColorAt(x, y);
				lines[y].setSample(x, Scanline.RED_SAMPLE, color.getRed());
				lines[y].setSample(x, Scanline.GREEN_SAMPLE, color.getGreen());
				lines[y].setSample(x, Scanline.BLUE_SAMPLE, color.getBlue());
				if(hasAlpha) {
					lines[y].setSample(x, Scanline.ALPHA_SAMPLE, color.getAlpha());
				}
			}
		}
		return lines;
	}
	
	/**
	 * Creates a series of scanlines from an image using grayscale
	 * mode.
	 *
	 * @param img
	 * The completed image.
	 *
	 * @return
	 * The scanlines that make up the image.
	 */
	private Scanline[] deconstructGrayscaleImage(Image img) {
		boolean hasAlpha = (mode == ColorMode.GRAYSCALE_ALPHA);
		Scanline[] lines = new Scanline[height];
		GrayColor color;
		for(int y = 0; y < height; y++) {
			lines[y] = new Scanline(mode.samples(), sampleDepth, width, filterMethod);
			for(int x = 0; x < width; x++) {
				color = new GrayColor(sampleDepth);
				color.setValue(image.valueAt(Image.GRAY, x, y));
				color.setAlpha(image.valueAt(Image.GRAY_ALPHA, x, y));
				lines[y].setSample(x, Scanline.GRAYSCALE_VALUE_SAMPLE, color.getValue());
				if(hasAlpha) {
					lines[y].setSample(x, Scanline.GRAYSCALE_ALPHA_SAMPLE, color.getAlpha());
				}
			}
		}
		return lines;
	}
	
	
	/**
	 * Creates a series of scanlines from the image.
	 *
	 * @return
	 * The scanlines that make up the image data.
	 */
	private Scanline[] deconstructImage() {
		Scanline[] scanlines = null;
		switch(mode) {
			case GRAYSCALE:
			case GRAYSCALE_ALPHA:
				scanlines = deconstructGrayscaleImage(image);
				break;
				
			case TRUECOLOR:
			case TRUECOLOR_ALPHA:
				scanlines = deconstructColorImage(image);
				break;
				
			case INDEXED:
				scanlines = deconstructImageFromPalette(image);
				break;
		}
		return scanlines;
	}
	
	/**
	 * Creates a series of scanlines from an image using indexed
	 * mode.
	 *
	 * @param img
	 * The completed image.
	 *
	 * @return
	 * The scanlines that make up the image.
	 */
	private Scanline[] deconstructImageFromPalette(Image img) {
		Scanline[] lines = new Scanline[height];
		Color color;
		for(int y = 0; y < height; y++) {
			lines[y] = new Scanline(mode.samples(), sampleDepth, width, filterMethod);
			for(int x = 0; x < width; x++) {
				color = img.getColorAt(x, y);
				int paletteIndex = palette.indexOf(color);
				lines[y].setSample(x, Scanline.PALETTE_INDEX_SAMPLE, paletteIndex);
			}
		}
		return lines;
	}
	
	/**
	 * Turns scanlines into a single array of bytes.
	 *
	 * @param scanlines
	 * The scanlines to deconstruct.
	 *
	 * @return
	 * The data of the scanlines.
	 * 
	 * @throws InvalidFormatException
	 * If the filter method is invalid.
	 */
	private byte[] deconstructScanlines(Scanline[] lines) throws InvalidFormatException {
		ByteHolder holder = new ByteHolder(lines.length * getScanlineWidth());
		byte[] buffer;
		for(Scanline sl: lines) {
			buffer = sl.getFiltered();
			holder.add(buffer);
		}
		return holder.toArray();
	}
	
	/**
	 * Builds image chunks from the image data.
	 *
	 * @return
	 * The ImageDataChunks that decode to the image data.
	 * 
	 * @throws InvalidFormatException
	 * If a format error occurs with one of the header values.
	 */
	private ImageDataChunk[] encodeImageData() throws InvalidFormatException {
		Scanline[] rawData = deconstructImage();
		byte[] uncompressedData = deconstructScanlines(rawData);
		byte[] compressedData = compressData(uncompressedData);
		ImageDataChunk[] chunks = createIdatChunks(compressedData);
		return chunks;
	}
	
	/**
	 * Gets all transparency values from each of the palette
	 * colors.
	 *
	 * @return
	 * The transparency values.
	 */
	private int[] extractTransparencies() {
		int[] trans = new int[palette.size()];
		for(int i = 0; i < palette.size(); i++) {
			trans[i] = palette.getColor(i).getAlpha();
		}
		return trans;
	}
	
	/**
	 * Converts the gamma to a chunk.
	 *
	 * @return
	 * The chunk containing the gamma.
	 */
	private GammaChunk gammaToChunk() {
		GammaChunk gc = new GammaChunk(gamma);
		return gc;
	}
	
	/**
	 * Gets the length of a pixel in bytes.
	 *
	 * @return
	 * The length of a pixel.
	 */
	private double getPixelWidth() {
		double pw;
		pw = mode.samples() * ((double)sampleDepth / 8);
		return pw;
	}
	
	/**
	 * Gets any scan lines that may exist from the data.
	 * 
	 * @param data
	 * Extra data to be added to the previous data in order to
	 * create scan lines.
	 */
	private Scanline[] getScanlinesFromData(byte[] data) throws InvalidFormatException {
		int numberOfScanlines = data.length / getScanlineWidth();
		if(numberOfScanlines != height) {
			throw new InvalidFormatException("Number of scanlines does not equal image height", "png");
		}
		Scanline[] lines = new Scanline[height];
		byte[] scanlineData;
		for(int i=0,start=0,end=0; i < height; i++) {
			start = i*getScanlineWidth();
			end = (i+1)*getScanlineWidth();
			scanlineData = Arrays.copyOfRange(data, start, end);
			lines[i] = new Scanline(mode.samples(), sampleDepth, scanlineData, filterMethod);
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
		int sw = (int)Math.ceil(getPixelWidth() * width) + 1;
		return sw;
	}
	
	/**
	 * Converts the header data into a new HeaderChunk.
	 *
	 * @return
	 * The resulting HeaderChunk.
	 */
	private HeaderChunk headerToChunk() {
		HeaderChunk hc = new HeaderChunk(image.width, image.height, image.sampleDepth, mode, compressionEngine, filterMethod, interlaceMethod);
		return hc;
	}
	
	/**
	 * Converts the histogram into a chunk.
	 *
	 * @return
	 * The resulting PaletteHistogramChunk.
	 */
	private PaletteHistogramChunk histogramToChunk() {
		int[] frequencies = palette.getAllFrequencies();
		PaletteHistogramChunk phc = new PaletteHistogramChunk(frequencies);
		return phc;
	}
	
	/**
	 * Reads the PNG data from a file.
	 *
	 * @param file
	 * The name of the file load the data from.
	 *
	 * @throws FileNotFoundException
	 * If the specified file does not exist.
	 *
	 * @throws InvalidFormatException
	 * If the file is corrupt or is not in the correct
	 * format for a PNG. This is also thrown if an
	 * unknown critical chunk is encountered.
	 *
	 * @throws StreamFailureException
	 * If the file stream fails for some other reason.
	 */
	private void loadPngFile(String file) throws FileNotFoundException, InvalidFormatException, StreamFailureException {
		Chunk nextChunk = null;
		FileInputStream f = new FileInputStream(file);
		ChunkInputStream reader = new ChunkInputStream(f);
		reader.verifyPng();
		boolean reading = true;
		while(reading) {
			nextChunk = reader.readChunk();
			if(nextChunk.getType() == Chunk.IEND) {
				reading = false;
			}
			processChunk(nextChunk);
		}
		processImageData();
		cleanUpDataStructures();
	}
	
	/**
	 * Makes a keyword's charset valid. The validity of a keyword
	 * is determined by the PNG specification at
	 * {@url http://www.libpng.org/pub/png/spec/1.2/PNG-Chunks.html#C.Anc-text}.
	 * All characters outside of the Latin-1 character set are
	 * removed, and any non-printable characters are removed as well.
	 * Non-breaking spaces are removed also, due to the fact that they
	 * are indistinguishable from spaces.
	 *
	 * @param keyword
	 * The keyword to make valid.
	 *
	 * @return
	 * The keyword with its character set corrected.
	 */
	private String makeKeywordCharsetValid(String keyword) {
		StringBuffer k = new StringBuffer();
		char c;
		for(int i = 0; i < keyword.length(); i++) {
			c = keyword.charAt(i);
			if(c < 256 && !Character.isISOControl(c) && c != 160/*<-- non-breaking space disallowed*/) {
				k.append(c);
			}
			// completely disallow any non-permitted characters.
		}
		return k.toString();
	}
	
	/**
	 * Makes the length of a keyword valid. The validity of a keyword
	 * is determined by the PNG specification at
	 * {@url http://www.libpng.org/pub/png/spec/1.2/PNG-Chunks.html#C.Anc-text}.
	 * If the keyword is longer than 79 characters, it is truncated. If
	 * the keyword is empty, it is set to the default keyword value of
	 * "Comment".
	 *
	 * @param keyword
	 * The keyword to make valid.
	 *
	 * @return
	 * The keyword with its length corrected.
	 */
	private String makeKeywordLengthValid(String keyword) {
		if(keyword.length() > 0) {
			if(keyword.length() > 79) {
				keyword = keyword.substring(0, 78);
			}
		} else {
			keyword = "Comment";
		}
		return keyword;
	}
	
	/**
	 * Makes a keyword's spacing valid. The validity of a keyword
	 * is determined by the PNG specification at
	 * {@url http://www.libpng.org/pub/png/spec/1.2/PNG-Chunks.html#C.Anc-text}.
	 * The keyword is stripped of trailing and leading spaces, and
	 * consecutive spaces are collapsed into one.
	 *
	 * @param keyword
	 * The keyword to make valid.
	 *
	 * The keyword with its spacing corrected.
	 */
	private String makeKeywordSpacingValid(String keyword) {
		keyword = keyword.trim();
		keyword = keyword.replaceAll(" +", " ");
		return keyword;
	}
	
	/**
	 * Ensures that a keyword is valid. If the specified
	 * keyword is not valid, it is reformatted so as to be
	 * valid.
	 *
	 * @param keyword
	 * The keyword to check for validity.
	 *
	 * @return
	 * The keyword reformatted to be valid. If it is already
	 * valid, the keyword returned as is.
	 */
	private String makeKeywordValid(String keyword) {
		keyword = makeKeywordLengthValid(keyword);
		keyword = makeKeywordCharsetValid(keyword);
		keyword = makeKeywordSpacingValid(keyword);
		return keyword;
	}
	
	/**
	 * Converts the last modification time to a chunk.
	 *
	 * @return
	 * The resulting ModificationTimeChunk.
	 */
	private ModificationTimeChunk modificationTimeToChunk() {
		ModificationTimeChunk mtc = new ModificationTimeChunk(lastModified);
		return mtc;
	}
	
	/**
	 * Gets the appropriate data from this PNG's palette and converts
	 * it into a chunk. This will only convert the palette color value;
	 * PNG PLTE chunks do not hold transparency information.
	 *
	 * @return
	 * The resulting PaletteChunk.
	 */
	private PaletteChunk paletteToChunk() {
		Color[] colorList = palette.getAllColors();
		PaletteChunk pc = new PaletteChunk(colorList);
		return pc;
	}
	
	/**
	 * Checks if image data needs to be preserved.
	 *
	 * @return
	 * Whether the image data must be preserved.
	 */
	private boolean preserveImageData() {
		boolean preserve = false;
		boolean unsafeChunks = false;
		for(Chunk c: unknownColorSpaceChunks) {
			if(!c.isSafeToCopy()) {
				unsafeChunks = true;
			}
		}
		if(!unsafeChunks) {
			for(Chunk c: unknownPreDataChunks) {
				if(!c.isSafeToCopy()) {
					unsafeChunks = true;
				}
			}
		}
		if(!unsafeChunks) {
			for(Chunk c: unknownPostDataChunks) {
				if(!c.isSafeToCopy()) {
					unsafeChunks = true;
				}
			}
		}
		if(unsafeChunks && !criticallyModified) {
			preserve = true;
		}
		return preserve;
	}
	
	/**
	 * Parses a chunk for data and adds it to the data of this
	 * PNG.
	 * 
	 * @param chunk
	 * The chunk to process.
	 */
	private void processChunk(Chunk chunk) {
		int type = chunk.getType();
		switch(type) {
			case Chunk.IHDR:
				readHeaderChunk((HeaderChunk) chunk);
				break;

			case Chunk.PLTE:
				readPaletteChunk((PaletteChunk) chunk);
				break;
				
			case Chunk.IDAT:
				unknownChunks = unknownPostDataChunks;
				dataChunks.add((ImageDataChunk) chunk);
				break;

			case Chunk.IEND:
				break;

			case Chunk.tRNS:
				readTransparencyChunk((TransparencyChunk) chunk);
				break;

			case Chunk.gAMA:
				readGammaChunk((GammaChunk) chunk);
				break;

			case Chunk.cHRM:
				readChromaticitiesChunk((ChromaticitiesChunk) chunk);
				break;

			case Chunk.sRGB:
				readStandardRgbColorSpaceChunk((StandardRgbColorSpaceChunk) chunk);
				break;

			case Chunk.iCCP:
				readEmbeddedColorProfileChunk((EmbeddedColorProfileChunk) chunk);
				break;

			case Chunk.iTXt:
				readInternationalTextDataChunk((InternationalTextDataChunk) chunk);
				break;

			case Chunk.tEXt:
				readTextDataChunk((TextDataChunk) chunk);
				break;

			case Chunk.zTXt:
				readCompressedTextDataChunk((CompressedTextDataChunk) chunk);
				break;

			case Chunk.bKGD:
				readBackgroundColorChunk((BackgroundColorChunk) chunk);
				break;

			case Chunk.pHYs:
				readPhysicalPixelDimensionsChunk((PhysicalPixelDimensionsChunk) chunk);
				break;

			case Chunk.sBIT:
				readSignificantBitsChunk((SignificantBitsChunk) chunk);
				break;

			case Chunk.sPLT:
				readSuggestedPaletteChunk((SuggestedPaletteChunk) chunk);
				break;

			case Chunk.hIST:
				readPaletteHistogramChunk((PaletteHistogramChunk) chunk);
				break;

			case Chunk.tIME:
				readModificationTimeChunk((ModificationTimeChunk) chunk);
				break;
		}
	}
	
	/**
	 * Builds an image from image data chunks.
	 *
	 * @param chunks
	 * The image chunk to process make up the image data.
	 */
	private void processImageData() throws InvalidFormatException {
		byte[] compressedData = concatenateImageData();
		byte[] decompressedData = decompressData(compressedData);
		//InterlaceEngine iEngine = interlaceMethod.getInterlaceEngine();
		//iEngine.setRawData(decompressedData);
		Scanline[] lines = getScanlinesFromData(decompressedData);
		//Scanline[] lines = iEngine.getScanlines();
		constructImage(lines);
	}
	
	/**
	 * Converts the profile data to a new EmbeddedColorProfileChunk.
	 *
	 * @return
	 * The resulting EmbeddedColorProfileChunk.
	 * 
	 * @throws InvalidFormatException
	 * If an invalid compression method is specified.
	 */
	private EmbeddedColorProfileChunk profileToChunk() throws InvalidFormatException {
		EmbeddedColorProfileChunk ecpc = new EmbeddedColorProfileChunk(profile.name, profile.data, compressionEngine);
		return ecpc;
	}
	
	/**
	 * Reads the background color from a chunk.
	 *
	 * @param chunk.
	 * The background color chunk.
	 */
	private void readBackgroundColorChunk(BackgroundColorChunk chunk) {
		if(mode == ColorMode.INDEXED) {
			backgroundColorIndex = chunk.getPaletteIndex();
		} else {
			backgroundColor = chunk.getColor();
		}
	}
	
	/**
	 * Reads a chromaticities chunk and assigns properties based
	 * on it.
	 *
	 * @param chunk
	 * The chromaticities chunk.
	 */
	private void readChromaticitiesChunk(ChromaticitiesChunk chunk) {
		Point r = chunk.getRed();
		Point g = chunk.getGreen();
		Point b = chunk.getBlue();
		Point w = chunk.getWhitePoint();
		chromaticity = new Chromaticity(r, g, b, w);
	}
	
	/**
	 * Reads compressed text data into this PNG.
	 * 
	 * @param chunk
	 * The compressed text data chunk.
	 */
	private void readCompressedTextDataChunk(CompressedTextDataChunk chunk) {
		addText(chunk.getKeyword(), chunk.getText());
	}
	
	/**
	 * Reads an embedded color profile.
	 *
	 * @param chunk
	 * The ICCP chunk.
	 */
	private void readEmbeddedColorProfileChunk(EmbeddedColorProfileChunk chunk) {
		profile = new ColorProfile(chunk.getProfileName(), chunk.getProfile());
	}
	
	/**
	 * Reads a gamma chunk and assigns properties based on it.
	 *
	 * @param chunk
	 * The gamma chunk.
	 */
	private void readGammaChunk(GammaChunk chunk) {
		gamma = chunk.getGamma();
		gammaSet = true;
	}
	
	/**
	 * Reads a header chunk and assigns properties based on it.
	 *
	 * @param header
	 * The header chunk.
	 */
	private void readHeaderChunk(HeaderChunk header) {
		unknownChunks = unknownColorSpaceChunks;
		sampleDepth = header.getSampleDepth();
		height = header.getHeight();
		width = header.getWidth();
		mode = header.getColorMode();
	}
	
	/**
	 * Reads international text data into this PNG.
	 * 
	 * @param chunk
	 * The international text data chunk.
	 */
	private void readInternationalTextDataChunk(InternationalTextDataChunk chunk) {
		addText(chunk.getKeyword(), chunk.getText());
	}
	
	/**
	 * Gets the last modified information from that chunk.
	 *
	 * @param chunk
	 * The last modification time chunk.
	 */
	private void readModificationTimeChunk(ModificationTimeChunk chunk) {
		Calendar c = Calendar.getInstance();
		c.set(chunk.getYear(), chunk.getMonth(), chunk.getDay(), chunk.getHour(), chunk.getMinute(), chunk.getSecond());
		lastModified = c.getTime();
	}
	/**
	 * Reads a palette chunk and assigns properties based on it.
	 *
	 * @param chunk
	 * The palette chunk.
	 */
	private void readPaletteChunk(PaletteChunk chunk) {
		unknownChunks = unknownPreDataChunks;
		paletteColors = chunk.getPalette();
	}
	
	/**
	 * Gets the histogram information from the chunk data.
	 *
	 * @param chunk
	 * The palette histogram chunk.
	 */
	private void readPaletteHistogramChunk(PaletteHistogramChunk chunk) {
		paletteFrequencies = chunk.getFrequencies();
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
	 * Reads the number of significant bits present in the original
	 * file.
	 *
	 * @param chunk
	 * The SignificantBitsChunk.
	 */
	private void readSignificantBitsChunk(SignificantBitsChunk chunk) {
		significantColorBits = chunk.getColorBits();
		if(mode == ColorMode.GRAYSCALE_ALPHA || mode == ColorMode.TRUECOLOR_ALPHA) {
			significantAlphaBits = chunk.getAlphaBits();
		}
		significantBitsSet = true;
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
		renderingIntentSet = true;
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
	 * Reads text data into this PNG.
	 * 
	 * @param chunk
	 * The text data chunk.
	 */
	private void readTextDataChunk(TextDataChunk chunk) {
		addText(chunk.getKeyword(), chunk.getText());
	}
	
	/**
	 * Reads a transparency chunk and assigns properties based
	 * on it.
	 *
	 * @param chunk
	 * The transparency chunk.
	 */
	private void readTransparencyChunk(TransparencyChunk chunk) {
		chunk.parseWithColorMode(mode);
		switch(chunk.getColorMode()) {
			case GRAYSCALE:
			case GRAYSCALE_ALPHA:
				transparentColor = new GrayColor(chunk.getTransparentColor());
				break;
			
			case TRUECOLOR:
			case TRUECOLOR_ALPHA:
				transparentColor = chunk.getTransparentColor();
				break;
				
			case INDEXED:
				paletteAlphas = chunk.getPaletteAlphas();
				break;
		}
	}
	
	/**
	 * Converts the rendering intent into a chunk.
	 *
	 * @return
	 * The resulting RenderingIntentChunk.
	 */
	private StandardRgbColorSpaceChunk renderingIntentToChunk() {
		StandardRgbColorSpaceChunk srcspc = new StandardRgbColorSpaceChunk(renderingIntent);
		return srcspc;
	}
	
	/**
	 * Converts the resolution data into a chunk.
	 *
	 * @return
	 * The resulting PhysicalPixelDimensionsChunk.
	 */
	private PhysicalPixelDimensionsChunk resolutionToChunk() {
		PhysicalPixelDimensionsChunk ppdc = new PhysicalPixelDimensionsChunk(resolution.x, resolution.y, Unit.METER);
		return ppdc;
	}
	
	/**
	 * Sets the background color if only an index to a palette color
	 * was provided.
	 */
	private void setIndexedBackgroundColor() {
		if(mode == ColorMode.INDEXED) {
			backgroundColor = palette.getColor(backgroundColorIndex);
		}
	}
	
	/**
	 * Converts the significant bits to a new SignificantBitsChunk.
	 *
	 * @return
	 * The resulting SignificantBitsChunk.
	 */
	private SignificantBitsChunk significantBitsToChunk() {
		SignificantBitsChunk sbc = null;
		switch(mode) {
			case GRAYSCALE:
				sbc = new SignificantBitsChunk(getSignificantGrayscaleBits());
				break;
				
			case TRUECOLOR:
			case INDEXED:
				sbc = new SignificantBitsChunk(getSignificantRedBits(), getSignificantGreenBits(), getSignificantBlueBits());
				break;
				
			case GRAYSCALE_ALPHA:
				sbc = new SignificantBitsChunk(getSignificantGrayscaleBits(), getSignificantAlphaBits());
				break;
				
			case TRUECOLOR_ALPHA:
				sbc = new SignificantBitsChunk(getSignificantRedBits(), getSignificantGreenBits(), getSignificantBlueBits(), getSignificantAlphaBits());
				break;
		}
		return sbc;
	}
	
	/**
	 * Converts the suggested palette data into a chunk.
	 *
	 * @return
	 * The resulting SuggestedPaletteChunk.
	 */
	private SuggestedPaletteChunk suggestedPaletteToChunk() {
		int[] red = new int[reducedPalette.size()];
		int[] green = new int[reducedPalette.size()];
		int[] blue = new int[reducedPalette.size()];
		int[] alpha = new int[reducedPalette.size()];
		Color color;
		for(int i = 0; i < reducedPalette.size(); i++) {
			color = reducedPalette.getColor(i);
			red[i] = color.getRed();
			green[i] = color.getGreen();
			blue[i] = color.getBlue();
			alpha[i] = color.getAlpha();
		}
		SuggestedPaletteChunk spc = new SuggestedPaletteChunk(reducedPalette.getName(), reducedPalette.getSampleDepth(), red, green, blue, alpha, reducedPalette.getAllFrequencies());
		return spc;
	}
	
	/**
	 * Converts all text data and tags into text chunks.
	 *
	 * @return
	 * The resulting TextChunk array.
	 * 
	 * @throws InvalidFormatException
	 * If an invalid compression method is specified.
	 */
	private TextChunk[] textDataToChunks() throws InvalidFormatException {
		Vector<TextChunk> chunks = new Vector<TextChunk>();
		for(Map.Entry<String,ArrayList<String>> keywordSet: textData.entrySet()) {
			String keyword = keywordSet.getKey();
			ArrayList<String> texts = keywordSet.getValue();
			for(String contents: texts) {
				TextChunk tc = null;
				if(contents.length() > UNCOMPRESSED_DATA_LIMIT) {
					tc = new CompressedTextDataChunk(keyword, contents, compressionEngine);
				} else {
					tc = new TextDataChunk(keyword, contents);
				}
				chunks.add(tc);
			}
		}
		return chunks.toArray(new TextChunk[0]);
	}
	
	/**
	 * Converts the transparency data into a chunk.
	 *
	 * @return
	 * The resulting TransparencyChunk.
	 */
	private TransparencyChunk transparencyDataToChunk() {
		TransparencyChunk tc = null;
		switch(mode) {
			case INDEXED:
				int[] transparencies = extractTransparencies();
				tc = new TransparencyChunk(transparencies);
				break;
				
			case GRAYSCALE:
				tc = new TransparencyChunk(((GrayColor)transparentColor).getValue());
				break;
				
			case TRUECOLOR:
				tc = new TransparencyChunk(transparentColor.getRed(), transparentColor.getGreen(), transparentColor.getBlue());
				break;
		}
		return tc;
	}
	
	/**
	 * Writes the PNG data to a file.
	 *
	 * @param location
	 * The name of the file to write the data to.
	 * 
	 * @param forcePreservation
	 * Whether or not image data should be forced to
	 * be preserved from the original chunks.
	 *
	 * @throws StreamFailureException
	 * If the file stream failed.
	 * 
	 * @throws InvalidFormatException
	 * If a chunk specifies an invalid value for a PNG file.
	 */
	private void writePngFile(String location, boolean forcePreservation) throws StreamFailureException, InvalidFormatException {
		Chunk[] chunks = convertToChunks(forcePreservation);
		FileOutputStream file = null;
		while(file == null) {
			try {
				file = new FileOutputStream(location);
			} catch(FileNotFoundException e) {
				File f = new File(location);
				try {
					f.createNewFile();
				} catch(IOException ioe) {
					throw new StreamFailureException(ioe.getMessage());
				}
			}
		}
		ChunkOutputStream writer = new ChunkOutputStream(file);
		writer.writeMagicNumber();
		for(Chunk c: chunks) {
			writer.writeChunk(c);
		}
	}
}