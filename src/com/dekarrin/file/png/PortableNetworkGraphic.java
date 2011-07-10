package com.dekarrin.file.png;

import java.awt.Point;
import java.nio.ByteBuffer;
import java.util.*;
import com.dekarrin.graphics.*;
import com.dekarrin.zip.*;
import com.dekarrin.util.*;
import com.dekarrin.io.*;
import com.dekarrin.error;

/**
 * Represents a PNG file. This class is attempting to be compliant
 * with version 1.2 of the PNG standard; it's not there yet.
 */
public class PortableNetworkGraphic {
	
	/**
	 * Parses a PNG file into a PNG object.
	 */
	private class PngReader {
		
		/**
		 * The mode used for verification of the png file.
		 */
		private static final int VERIFICATION_MODE = 0;
		
		/**
		 * The mode used for reading chunk lengths.
		 */
		private static final int LENGTH_READING_MODE = 1;
		
		/**
		 * The mode used for reading chunk types.
		 */
		private static final int TYPE_READING_MODE = 2;
		
		/**
		 * The mode used for reading chunk data.
		 */
		private static final int DATA_READING_MODE = 3;
		
		/**
		 * The mode used for reading chunk CRCs.
		 */
		private static final int CRC_READING_MODE = 4;
		
		/**
		 * The mode of this Parser.
		 */
		private int parseMode;
		
		/**
		 * The array of values in the header of a valid png file.
		 */
		private static final IntHolder MAGIC_NUMBER = new IntHolder(new int[]{137, 80, 78, 71, 13, 10, 26, 10});
			
		/**
		 * The array of length bytes in the chunk currently being
		 * read.
		 */
		private ByteHolder lengthBytes;
		
		/**
		 * The array of type bytes in the chunk currently being
		 * read.
		 */
		private ByteHolder typeBytes;
		
		/**
		 * The array of crc bytes in the chunk currently being
		 * processed.
		 */
		private ByteHolder crcBytes;
		
		/**
		 * The actual data from the chunk.
		 */
		private ByteHolder chunkData;
		
		/**
		 * The length of the data field of the chunk currently being
		 * processed.
		 */
		private int dataLength;
		
		/**
		 * The calculated CRC of the chunk.
		 */
		private int cyclicRedundancyCheck;
		
		/**
		 * The png file being parsed.
		 */
		private File pngFile;
		
		/**
		 * The stream of the png file.
		 */
		private FileInputStream pngStream;
		
		/**
		 * The chunks that have been read.
		 */
		private ArrayList<Chunk> completedChunks = new ArrayList<Chunk>(50);
		
		/**
		 * Creates a new PngReader and parses a PNG file.
		 *
		 * @param filename
		 * The location of the PNG.
		 */
		public PngReader(String filename) throws FileNotFoundException {
			pngFile = new File(filename);
			createStream();
			resetDataFields();
		}
		
		/**
		 * Parses the png file into a PNG object.
		 *
		 * @return
		 * The png object.
		 */
		public Chunk[] read() throws InvalidFileFormatException, StreamFailureException {
			parseChunksFromFile();
			return completedChunks.toArray();
		}
		
		/**
		 * Parses a chunk into its correct type based on its type.
		 */
		private Chunk createChunk() {
			String type = new String(typeBytes.toArray(), 0, 4); 
			byte[] data = chunkData.toArray();
			long crc = cyclicRedundancyCheck;
			
			Chunk newChunk;
			if(type.equals("IHDR")) {
				newChunk = new HeaderChunk(data, crc);
			} else if(type.equals("PLTE")) {
				newChunk = new PaletteChunk(data, crc);
			} else if(type.equals("IDAT")) {
				newChunk = new ImageDataChunk(data, crc);
			} else if(type.equals("IEND")) {
				newChunk = new TrailerChunk(data, crc);
			} else if(type.equals("tRNS")) {
				newChunk = new TransparencyChunk(data, crc);
			} else if(type.equals("gAMA")) {
				newChunk = new GammaChunk(data, crc);
			} else if(type.equals("cHRM")) {
				newChunk = new ChromaticitiesChunk(data, crc);
			} else if(type.equals("sRGB")) {
				newChunk = new StandardRgbColorSpaceChunk(data, crc);
			} else if(type.equals("iCCP")) {
				newChunk = new EmbeddedColorProfileChunk(data, crc);
			} else if(type.equals("tEXt")) {
				newChunk = new TextDataChunk(data, crc);
			} else if(type.equals("zTXt")) {
				newChunk = new CompressedTextDataChunk(data, crc);
			} else if(type.equals("iTXt")) {
				newChunk = new InternationalTextDataChunk(data, crc);
			} else if(type.equals("bKGD")) {
				newChunk = new BackgroundColorChunk(data, crc);
			} else if(type.equals("pHYs")) {
				newChunk = new PhysicalPixelDimensionsChunk(data, crc);
			} else if(type.equals("sBIT")) {
				newChunk = new SignificantBitsChunk(data, crc);
			} else if(type.equals("sPLT")) {
				newChunk = new SuggestedPaletteChunk(data, crc);
			} else if(type.equals("hIST")) {
				newChunk = new PaletteHistogramChunk(data, crc);
			} else if(type.equals("tIME")) {
				newChunk = new LastModificationTimeChunk(data, crc);
			} else {
				newChunk = new Chunk(type, data, crc);
			}
			resetDataFields();
			return newChunk;
		}
		
		/**
		 * Gets the data from the png file and stores it as generic
		 * chunks in the completedChunks ArrayList.
		 */
		private void parseChunksFromFile() throws StreamFailureException, InvalidFileFormatException {
			boolean parsing = true;
			int nextByteAsInt;
			parseMode = VERIFICATION_MODE;
			while((nextByteAsInt = getNextByte()) != -1) {
				parseByte(nextByteAsInt);
			}
			try {
				pngStream.close();
			} catch(IOException e) {
				throw new StreamFailureException(e.getMessage());
			}
		}
		
		/**
		 * Parses a single integer into a byte and then parses the
		 * data itself.
		 *
		 * @param readByte
		 * The byte to parse.
		 *
		 * @throws InvalidFileFormatException
		 * When the header does not validate.
		 */
		private void parseByte(int readByte) throws InvalidFileFormatException {
			switch(parseMode) {
				case VERIFICATION_MODE:
					verifyByte(readByte);
					break;
				
				case LENGTH_READING_MODE:
					addByteToHolder(readByte, lengthBytes);
					break;
					
				case TYPE_READING_MODE:
					addByteToHolder(readByte, typeBytes);
					break;
					
				case DATA_READING_MODE:
					addByteToHolder(readByte, chunkData);
					break;
				
				case CRC_READING_MODE:
					addByteToHolder(readByte, crcBytes);
					break;
					
				default:
					throw new IllegalModeException("Invalid mode");
			}
			
			checkParseModeCompletion();
		}
		
		/**
		 * Checks the parse mode, and changes it as appropriate. If
		 * it needs to be changed, the appropriate actions are taken
		 * for changing the state of the parser to the new mode.
		 */
		private void checkParseModeCompletion() {
			switch(parseMode) {
				case VERIFICATION_MODE:
					if(isComplete(MAGIC_NUMBER)) {
						parseMode = LENGTH_READING_MODE;
					}
					break;
					
				case LENGTH_READING_MODE:
					if(isComplete(lengthBytes)) {
						interpretLengthBytes();
						setChunkDataSize();
						parseMode = TYPE_READING_MODE;
					}
					break;
					
				case TYPE_READING_MODE:
					if(isComplete(typeBytes)) {
						if(dataLength > 0) {
							parseMode = DATA_READING_MODE;
						} else {
							parseMode = CRC_READING_MODE;	
						}
					}
					break;
					
				case DATA_READING_MODE:
					if(isComplete(chunkData)) {
						parseMode = CRC_READING_MODE;
					}
					break;
					
				case CRC_READING_MODE:
					if(isComplete(crcBytes)) {
						interpretCrcBytes();
						Chunk chunk = createChunk();
						completedChunks.add(chunk);
						parseMode = LENGTH_READING_MODE;
					}
					break;
			}
		}
		
		/**
		 * Checks a header byte for validity. After each check, the
		 * verification pointer is incremented.
		 *
		 * @param headerByte
		 * The byte from the header.
		 *
		 * @throws InvalidFileFormatException
		 * If a byte is not valid.
		 */
		private void verifyByte(int headerByte) throws InvalidFileFormatException {
			if(headerByte != MAGIC_NUMBER.next()) {
				throw new InvalidFileFormatException("Bad header file!", "png");
			}
		}
			
		/**
		 * Adds a byte to a tempory holding array. The given int is
		 * converted into a byte and then put into the specified
		 * array.
		 *
		 * @param inputByte
		 * The byte to add.
		 *
		 * @param holder
		 * The array to put the byte in.
		 */
		private void addByteToHolder(int inputByte, ByteHolder holder) {
			byte actualByte = (byte)inputByte;
			holder.add(actualByte);
		}
		
		/**
		 * Checks if a holder position pointer has reached its capacity.
		 *
		 * @param holder
		 * The holder to check.
		 *
		 * @return
		 * True if the position has come to the end; false otherwise.
		 */
		private boolean isComplete(PrimitiveHolder holder) {
			boolean complete = false;
			if(holder.isAtEnd()) {
				complete = true;
			}
			return complete;
		}
		
		/**
		 * Interprets the bytes in the lengthBytes array into a single
		 * int.
		 */
		private void interpretLengthBytes() {
			dataLength = getIntFromBytes(lengthBytes.toArray());
		}
		
		/**
		 * Interprets the bytes in the crcBytes array into a single
		 * int.
		 */
		private void interpretCrcBytes() {
			cyclicRedundancyCheck = getIntFromBytes(crcBytes.toArray());
		}
		
		/**
		 * Sets the size of the chunkData array to the read length.
		 */
		private void setChunkDataSize() {
			chunkData = new ByteHolder(dataLength);
		}
		
		/**
		 * Initializes fields used for storing chunk data.
		 */
		private void resetDataFields() {
			dataLength				= 0;
			cyclicRedundancyCheck	= 0;
			lengthBytes				= new ByteHolder(4);
			typeBytes				= new ByteHolder(4);
			crcBytes				= new ByteHolder(4);
			chunkData				= null;
			MAGIC_NUMBER.reset();
		}
		
		/**
		 * Converts a byte array into an int.
		 *
		 * @param subject
		 * The bytes to convert.
		 *
		 * @return
		 * The int, made up of the first four bytes in the array.
		 */
		private int getIntFromBytes(byte[] subject) {
			ByteBuffer bb = ByteBuffer.wrap(subject);
			int converted = bb.getInt();
			return converted;
		}
		
		/**
		 * Gets the next byte from the png stream.
		 *
		 * @return
		 * The read byte.
		 */
		private int getNextByte() throws StreamFailureException {
			int nextByte = -1;
			try {
				nextByte = pngStream.read();
			} catch(IOException e) {
				throw new StreamFailureException(e.getMessage());
			}
			return nextByte;
		}
		
		/**
		 * Creates the stream for the png file.
		 */
		private void createStream() throws FileNotFoundException {
			pngStream = new FileInputStream(pngFile);
		}
	}
	
	/**
	 * Writes Png Chunk arrays to disk.
	 */
	private class PngWriter {
		
		/**
		 * The magic number for a png file.
		 */
		private static final byte[] MAGIC_NUMBER = new byte[] {(byte)137, 80, 78, 71, 13, 10, 26, 10};
		
		/**
		 * The stream of the png file.
		 */
		private FileOutputStream pngStream;
		
		/**
		 * The file on disk.
		 */
		private File pngFile;
		
		/**
		 * Creates a new PngWriter.
		 *
		 * @param filename
		 * The location of the PNG.
		 */
		public PngWriter(String filename) throws FileNotFoundException {
			pngFile = new File(filename);
			createStream();
		}
		
		/**
		 * Writes chunks to disk.
		 *
		 * @param chunks
		 * The chunks to write.
		 */
		public void write(Chunk[] chunks) throws StreamFailureException {
			writeMagicNumber();
			for(Chunk c: chunks) {
				writeChunk(c);
			}
			try {
				pngStream.flush();
				pngStream.close();
			} catch(IOException e) {
				throw new StreamFailureException(e.getMessage());
			}
		}
		
		/**
		 * Writes the magic number for a Png file to the stream.
		 */
		private void writeMagicNumber() throws StreamFailureException {
			try {
				pngStream.write(MAGIC_NUMBER);
			} catch(IOException e) {
				throw new StreamFailureException(e.getMessage());
			}
		}
		
		/**
		 * Writes a chunk to the stream.
		 *
		 * @param chunk
		 * The chunk to write.
		 */
		private void writeChunk(Chunk c) throws StreamFailureException {
			byte[] outgoingBytes = c.getBytes();
			try {
				pngStream.write(outgoingBytes);
			} catch(IOException e) {
				throw new StreamFailureException(e.getMessage());
			}
		}
		
		/**
		 * Creates the stream for the png file.
		 */
		private void createStream() throws FileNotFoundException {
			pngStream = new FileOutputStream(pngFile);
		}
	}
	
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
	private HashMap<String,ArrayList<String>> textData;
	
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
	 * Any chunks read before PLTE that are not understood.
	 */
	private Vector<Chunk> unknownColorSpaceChunks = new Vector<Chunk>();
	
	/**
	 * Any chunks read after PLTE but before IDAT that are not
	 * understood.
	 */
	private Vector<Chunk> unknownPreDataChunks = new Vector<Chunk>();
	
	/**
	 * Any chunks read after IDAT that are not understood.
	 */
	private Vector<Chunk> unknownPostDataChunks = new Vector<Chunk>();
	
	/**
	 * The image data chunks. These are preserved in case unknown unsafe
	 * to copy chunks exist in the png.
	 */
	private ImageDataChunk[] dataChunks;
	
	/**
	 * Whether or not critical Chunks have been modified.
	 */
	private boolean modifiedCritical = false;
	
	/**
	 * The image contained by this Png.
	 */
	private Image image;

	/**
	 * Creates a new PNG from a series of chunks.
	 *
	 * @param file
	 * The file to load the png data from.
	 */
	public PortableNetworkGraphic(String file) {
		loadPngFile(file);
	}
	
	/**
	 * Creates a new png file from an image.
	 *
	 * @param image
	 * The Image to create the file from.
	 *
	 * @param colorType
	 * Which color mode should be used for saving the image.
	 */
	public PortableNetworkGraphic(Image image, int colorType) {
		this.image = image;
		width = image.width;
		height = image.height;
		bitDepth = image.bitDepth;
		colorMode = colorType;
		compressionMethod = COMPRESSION_METHOD_ZLIB;
		filterMethod = FILTER_METHOD_ADAPTIVE;
		interlaceMethod = INTERLACE_METHOD_NONE;
	}
	
	/**
	 * Saves this Png to disk. Its data is converted into Chunks,
	 * compressable chunks are compressed, and the image data is
	 * filtered. The resulting Chunk array is then written to a
	 * file.
	 *
	 * @param location
	 * Where to save the Png file.
	 */
	public void save(String location) {
		writePngFile(location);
	}
	
	/**
	 * Loads this Png from disk. The disk data is read, the image
	 * data is defiltered, compressable chunks are decompressed,
	 * and the chunks are converted into object data.
	 *
	 * @param location
	 * Where to load the Png file from.
	 */
	public void load(String location) {
		loadPngFile(location);
	}
	
	/**
	 * Gets the height of the image.
	 *
	 * @return
	 * The height.
	 */
	public int getHeight() {
		return height;
	}
	
	/**
	 * Gets the width of the image.
	 *
	 * @return
	 * The width.
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Gets the bit depth of the image.
	 *
	 * @return
	 * The bit depth.
	 */
	public int getBitDepth() {
		return bitDepth;
	}
	
	/**
	 * Gets the color mode of this png.
	 *
	 * @return
	 * The color mode.
	 */
	public int getColorMode() {
		return colorMode;
	}
	
	/**
	 * Checks which compression method is being used by the
	 * Png.
	 *
	 * @return
	 * The compression method.
	 */
	public int getCompressionMethod() {
		return compressionMethod;
	}
	
	/**
	 * Gets the filtering method used in this png.
	 *
	 * @param filtering method.
	 */
	public int getFilteringMethod() {
		return filteringMethod;
	}
	
	/**
	 * Checks whether the image is interlaced.
	 *
	 * @return
	 * Whether it is.
	 */
	public boolean isInterlaced() {
		return adam7Interlacing;
	}
	
	/**
	 * Sets the palette for this Png.
	 *
	 * @param palette
	 * The palette to be used for the image.
	 */
	public void setPalette(Palette palette) {
		this.palette = palette;
	}
	
	/**
	 * Gets the palette for this image.
	 *
	 * @return
	 * The palette used for the image.
	 */
	public void getPalette() {
		return palette;
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
		alphaColor = color;
	}
	
	/**
	 * Gets the color to use as the transparent.
	 *
	 * @return
	 * The color value used as the transparency.
	 */
	public Color getTransparentColor() {
		return alphaColor;
	}
	
	/**
	 * Sets the gamma of this png image.
	 *
	 * @param gamma
	 * The gamma.
	 */
	public void setGamma(int gamma) {
		this.gamma = gamma;
	}
	
	/**
	 * Gets the gamma of this png image.
	 *
	 * @return
	 * The gamma.
	 */
	public int getGamma() {
		return gamma;
	}
	
	/**
	 * Sets the chromaticity for this png image.
	 *
	 * @param chromaticity
	 * The chromaticity to use.
	 */
	public void setChromaticity(Chromaticity chromaticity) {
		this.chromaticity = chromaticity;
	}
	
	/**
	 * Gets the chromaticity for this png image.
	 *
	 * @return
	 * The chromaticity used.
	 */
	public Chromaticity getChromaticity() {
		return chromaticity;
	}
	
	/**
	 * Sets the rendering intent for this png image.
	 *
	 * @param renderingIntent
	 * The rendering intent.
	 */
	public void setRenderingIntent(int renderingIntent) {
		this.renderingIntent = renderingIntent;
	}
	
	/**
	 * Gets the rendering intent for this png image.
	 *
	 * @return
	 * The rendering intent.
	 */
	public int getRenderingIntent() {
		return renderingIntent;
	}
	
	/**
	 * Sets the International Color Consortium Color profile
	 * for this png.
	 *
	 * @param profile
	 * The profile.
	 */
	public void setProfile(IccProfile profile) {
		this.profile = profile;
	}
	
	/**
	 * Gets the International Color Consortium Color profile
	 * for this png.
	 *
	 * @return
	 * The profile.
	 */
	public IccProfile GetProfile() {
		return profile;
	}
	
	/**
	 * Adds text data to this Png.
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
		checkKeyword(keyword);
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
	 * Adds comment text data to this Png. This is inserted with
	 * the generic tag 'Comment'.
	 *
	 * @param message
	 * The text data to add.
	 */
	public void addText(String message) {
		addText("Comment", message);
	}
	
	/**
	 * Gets text data from this Png.
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
	 * Gets all keywords in this Png that have text
	 * data associated with them.
	 *
	 * @return
	 * The used keywords.
	 */
	public String[] getKeywords() {
		return textData.keySet().toArray();
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
	 * Gets the background color to show this image against.
	 *
	 * @return
	 * What the background color should be.
	 */
	public Color getBackgroundColor() {
		return backgroundColor;
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
		int width = (x / 0.0254); // default and only unit is the meter; 1in = 0.0254m.
		int height = (y / 0.0254);
		resolution = new Resolution(width, height);
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
	 * Gets the image data from this png. This modifies the Png so
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
	 * Sets the last modified time of this Png to the current time.
	 */
	public void setModified() {
		lastModified = new Date();
	}
	
	/**
	 * Gets the last modification time of this Png.
	 *
	 * @return
	 * The last modification time.
	 */
	public Date getModificationDate() {
		lastModified = new Date();
	}
	
	/**
	 * Gets all the chunks that this Png was unable to parse.
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
	 * Sets this png to not copy unsafe to copy chunks when
	 * it saves.
	 */
	protected criticallyModify() {
		modifiedCritical = true;
	}
	
	/**
	 * Reads the png data from a file.
	 *
	 * @param file
	 * The name of the file load the data from.
	 */
	private void loadPngFile(String file) {
		PngReader reader = new PngReader(file);
		Chunk[] chunks = reader.read();
		parseChunks(chunks);
	}
	
	/**
	 * Writes the png data to a file.
	 *
	 * @param file
	 * The name of the file to write the data to.
	 */
	private void writePngFile(String file) {
		Chunk[] chunks = convertToChunks();
		PngWriter writer = new PngWriter(file);
		writer.write(chunks);
	}
	
	/**
	 * Converts the data in this Png into chunks to be written
	 * to disk.
	 */
	private Chunk[] convertToChunks() {
		Vector<Chunk> chunks = new Vector<Chunk>();
		chunks.add(headerToChunk());
		if(unknownColorSpaceChunks.size() > 0) {
			for(Chunk c: unknownColorSpaceChunks) {
				if(c.isSafeToCopy() || !modifiedCritical) {
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
				if(c.isSafeToCopy() || !modifiedCritical) {
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
		if(preserveImageData()) {
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
				if(c.isSafeToCopy() || !modifiedCritical) {
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
	 * Converts the suggested palette data into a chunk.
	 *
	 * @return
	 * The resulting SuggestedPaletteChunk.
	 */
	private SuggestedPaletteChunk suggestedPaletteToChunk() {
		int[] red = new int[suggestedPalette.size()];
		int[] green = new int[suggestedPalette.size()];
		int[] blue = new int[suggestedPalette.size()];
		int[] alpha = new int[suggestedPalette.size()];
		int[] frequencies = new int[suggestedPalette.size()];
		Color color;
		for(int i = 0; i < suggestedPalette.size(); i++) {
			color = suggestedPalette.getColor(i);
			red[i] = color.getRed();
			green[i] = color.getGreen();
			blue[i] = color.getBlue();
			alpha[i] = color.getAlpha();
		}
		SuggestedPaletteChunk spc = new SuggestedPaletteChunk(suggestedPalette.getName(), suggestedPalette.getSampleDepth(), red, green, blue, alpha, suggestedPalette.getAllFrequencies());
		return spc;
	}
	
	/**
	 * Converts the resolution data into a chunk.
	 *
	 * @return
	 * The resulting PhysicalPixelDimensionsChunk.
	 */
	private PhysicalPixelDimensionsChunk resolutionToChunk() {
		PhysicalPixelDimensionChunk ppdc = new PhysicalPixelDimensionChunk(resolution.x, resolution.y, METER_UNIT);
		return ppdc;
	}
	
	/**
	 * Converts the transparency data into a chunk.
	 *
	 * @return
	 * The resulting TransparencyChunk.
	 */
	private TransparencyChunk transparencyDataToChunk() {
		TransparencyChunk tc = null;
		if(colorMode == COLOR_TYPE_COLOR_INDEXED) {
			int[] transparencies = extractTransparencies();
			tc = new TransparencyChunk(transparencies);
		} else if(colorMode == COLOR_TYPE_COLOR) {
			tc = new TransparencyChunk(alphaColor.getRed());
		} else {
			tc = new TransparencyChunk(alphaColor.getRed(), alphaColor.getGreen(), alphaColor.getBlue());
		}
		return tc;
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
	 * Converts the background color into a chunk.
	 *
	 * @return
	 * The resulting BackgroundColorChunk.
	 */
	private BackgroundColorChunk backgroundColorToChunk() {
		BackgroundColorChunk bcc
		if(colorMode == COLOR_TYPE_COLOR_INDEXED) {
			bcc = new BackgroundColorChunk(getColorIndex(backgroundColor));
		} else {
			if(colorMode == COLOR_TYPE_COLOR && colorMode == COLOR_TYPE_COLOR_ALPHA) {
				bcc = new BackgroundColorChunk(backgroundColor, TRUECOLOR_MODE);
			} else {
				bcc = new BackgroundColorChunk(backgroundColor, GRAYSCALE_MODE);
			}
		}
		return bcc;
	}
	
	/**
	 * Gets the appropriate data from this Png's palette and converts
	 * it into a chunk. This will only convert the palette color value;
	 * Png PLTE chunks do not hold transparency information.
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
	 * Converts the significant bits to a new SignificantBitsChunk.
	 *
	 * @return
	 * The resulting SignificantBitsChunk.
	 */
	private SignificantBitsChunk significantBitsToChunk() {
		SignificantBitsChunk sbc = null;
		switch(colorType) {
			case COLOR_TYPE_GRAYSCALE:
				sbc = new SignificantBitsChunk(getSignificantGrayscaleBits());
				break;
				
			case COLOR_TYPE_COLOR:
			case COLOR_TYPE_COLOR_PALETTE:
				sbc = new SignificantBitsChunk(getSignificantRedBits(), getSignificantGreenBits(), getSignificantBlueBits());
				break;
				
			case COLOR_TYPE_GRAYSCALE_ALPHA:
				sbc = new SignificantBitsChunk(getSignificantGrayscaleBits(), getSignificantAlphaBits());
				break;
				
			case COLOR_TYPE_COLOR_ALPHA:
				sbc = new SignificantBitsChunk(getSignificantRedBits(), getSignificantGreenBits(), getSignificantBlueBits(), getSignificantAlphaBits());
				break;
		}
		return sbc;
	}
	
	/**
	 * Converts the profile data to a new EmbeddedColorProfileChunk.
	 *
	 * @return
	 * The resulting EmbeddedColorProfileChunk.
	 */
	private EmbeddedColorProfileChunk profileToChunk() {
		EmbeddedColorProfileChunk ecpc = new EmbeddedColorProfileChunk(profile.name, profile.data);
		return ecpc;
	}
	
	/**
	 * Converts the header data into a new HeaderChunk.
	 *
	 * @return
	 * The resulting HeaderChunk.
	 */
	private HeaderChunk headerToChunk() {
		HeaderChunk hc = new HeaderChunk(width, height, bitDepth, colorType, compressionMethod, filterMethod, interlaceMethod);
		return hc;
	}
	
	/**
	 * Converts the Chromaticity to a chunk.
	 *
	 * @return
	 * The chunk containing the chromaticity.
	 */
	private ChromaticitiesChunk chromaticityToChunk() {
		int wx = chromaticity.getWhiteX();
		int wy = chromaticity.getWhiteY();
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
		if(unsafeChunks && !modifiedCritical) {
			preserve = true;
		}
		return preserve;
	}
	
	/**
	 * Parses chunks into data for this Png.
	 *
	 * @param chunks
	 * The chunks to parse.
	 */
	private void parseChunks(Chunk[] chunks) {
		Vector<Chunk> unknown = unknownColorSpaceChunks;
		Vector<ImageDataChunk> idatChunks = new Vector<ImageDataChunk>();
		for(Chunk c: chunks) {
			String type = c.getTypeName();
			if(type.equals("IHDR")) {
				readHeaderChunk((HeaderChunk)c);
				unknown = unknownColorSpaceChunks;
			} else if(type.equals("PLTE")) {
				readPaletteChunk((PaletteChunk)c);
				unknown = unknownPreDataChunks;
			} else if(type.equals("IDAT")) {
				idatChunks.add((ImageDataChunk)c);
				unknown = unknownPostDataChunks;
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
			} else {
				if(c.isAncillary()) {
					unknown.add(c);
				} else {
					throw new UnknownChunkException("Unknown chunk type '"+c.getTypeName()+"'", c.getTypeName());
				}
			}
		}
		finishChunkDecoding();
		decodeImageData(idatChunks.toArray(new ImageDataChunk[0]));
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
		Scanline[] rawData = new Scanline[lines.length];
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
				case COLOR_TYPE_GRAYSCALE_ALPHA:
				case COLOR_TYPE_COLOR:
				case COLOR_TYPE_COLOR_ALPHA:
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
		if(colorPalette != null) {
			Color[] colorList = new Color[colorPalette.length];
			Arrays.fill(colorList, new Color(bitDepth));
			if(alphaPalette != null) {
				int i = 0;
				for(; i < alphaPalette.length; i++) {
					colorList[i].setRed(colorPalette[i].getRed());
					colorList[i].setGreen(colorPalette[i].getGreen());
					colorList[i].setBlue(colorPalette[i].getBlue());
					colorList[i].setAlpha(alphaPalette[i]);
				}
				for(; i < colorPalette.length; i++) {
					colorList[i].setRed(colorPalette[i].getRed());
					colorList[i].setGreen(colorPalette[i].getGreen());
					colorList[i].setBlue(colorPalette[i].getBlue());
				}
			} else {
				colorList = colorPalette;
			}
			palette = new Palette("untitled", bitDepth, colorList, frequencies);
		}
	}
}