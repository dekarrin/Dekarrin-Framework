package com.dekarrin.file.png;

import java.awt.Point;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.io.*;
import com.dekarrin.graphics.*;
import com.dekarrin.zip.*;
import com.dekarrin.util.*;
import com.dekarrin.io.*;
import com.dekarrin.error.*;

/**
 * Represents a PNG file. This class is attempting to be compliant
 * with version 1.2 of the PNG standard; it's not there yet.
 */
public class PortableNetworkGraphic implements ChunkListener {
	
	/**
	 * Parses a steam into a PortableNetworkGraphic object.
	 */
	private class PngReader {
		
		/**
		 * The actual data from the chunk.
		 */
		private ByteHolder chunkData;
		
		/**
		 * The mode used for reading chunk CRCs.
		 */
		private final int CRC_READING_MODE = 4;
		
		/**
		 * The array of crc bytes in the chunk currently being
		 * processed.
		 */
		private ByteHolder crcBytes;
		
		/**
		 * The calculated CRC of the chunk.
		 */
		private long cyclicRedundancyCheck;
		
		/**
		 * The mode used for reading chunk data.
		 */
		private final int DATA_READING_MODE = 3;
		
		/**
		 * The length of the data field of the chunk currently being
		 * processed.
		 */
		private int dataLength;
		
		/**
		 * The mode used for reading chunk lengths.
		 */
		private final int LENGTH_READING_MODE = 1;
			
		/**
		 * The array of length bytes in the chunk currently being
		 * read.
		 */
		private ByteHolder lengthBytes;
		
		/**
		 * The objects listening for read chunks.
		 */
		private ArrayList<ChunkListener> listeners = new ArrayList<ChunkListener>();
		
		/**
		 * The array of values in the header of a valid png file.
		 */
		private final IntHolder MAGIC_NUMBER = new IntHolder(PortableNetworkGraphic.MAGIC_NUMBER);
		
		/**
		 * The mode of this Parser.
		 */
		private int parseMode;
		
		/**
		 * Whether or not the stream is currently being parsed.
		 */
		private boolean parsing;
		
		/**
		 * The stream of the PNG file.
		 */
		private InputStream pngStream;
		
		/**
		 * The mode used for reading chunk types.
		 */
		private final int TYPE_READING_MODE = 2;
		
		/**
		 * The array of type bytes in the chunk currently being
		 * read.
		 */
		private ByteHolder typeBytes;
		
		/**
		 * The mode used for verification of the png file.
		 */
		private final int VERIFICATION_MODE = 0;
		
		/**
		 * Creates a new stream reader to read PNG data from.
		 * 
		 * @param stream
		 * The InputStream to read the PNG data from.
		 */
		public PngReader(InputStream stream) {
			pngStream = stream;
			resetDataFields();
		}
		
		/**
		 * Sets a listener for this PngReader's completion method.
		 * 
		 * @param listener
		 * The listener to register.
		 */
		public void addChunkListener(ChunkListener listener) {
			listeners.add(listener);
		}
		
		/**
		 * Parses the png file into a PNG object.
		 *
		 * @return
		 * The png object.
		 */
		public void read() throws InvalidFormatException, StreamFailureException, UnknownChunkException {
			parseChunksFromStream();
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
		 * Passes a background color chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announceBackgroundColorChunk(BackgroundColorChunk chunk) {
				for(ChunkListener listener: listeners) {
				listener.backgroundColorChunkProcessed(chunk);
			}
		}
		
		/**
		 * Passes a chromaticities chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announceChromaticitiesChunk(ChromaticitiesChunk chunk) {
			for(ChunkListener listener: listeners) {
				listener.chromaticitiesChunkProcessed(chunk);
			}
		}
		
		/**
		 * Passes a compressed text data chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announceCompressedTextDataChunk(CompressedTextDataChunk chunk) {
			for(ChunkListener listener: listeners) {
				listener.compressedTextDataChunkProcessed(chunk);
			}
		}
		
		/**
		 * Passes a embedded color profile chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announceEmbeddedColorProfileChunk(EmbeddedColorProfileChunk chunk) {
			for(ChunkListener listener: listeners) {
				listener.embeddedColorProfileChunkProcessed(chunk);
			}
		}
		
		/**
		 * Passes a gamma chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announceGammaChunk(GammaChunk chunk) {
			for(ChunkListener listener: listeners) {
				listener.gammaChunkProcessed(chunk);
			}
		}
		
		/**
		 * Passes a header chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announceHeaderChunk(HeaderChunk chunk) {
			for(ChunkListener listener: listeners) {
				listener.headerChunkProcessed(chunk);
			}
		}
		
		/**
		 * Passes an image data chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announceImageDataChunk(ImageDataChunk chunk) {
			for(ChunkListener listener: listeners) {
				listener.imageDataChunkProcessed(chunk);
			}
		}
		
		/**
		 * Passes an international text data chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announceInternationalTextDataChunk(InternationalTextDataChunk chunk) {
			for(ChunkListener listener: listeners) {
				listener.internationalTextDataChunkProcessed(chunk);
			}
		}
		
		/**
		 * Passes a modification time chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announceModificationTimeChunk(ModificationTimeChunk chunk) {
			for(ChunkListener listener: listeners) {
				listener.modificationTimeChunkProcessed(chunk);
			}
		}
		
		/**
		 * Passes a palette chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announcePaletteChunk(PaletteChunk chunk) {
			for(ChunkListener listener: listeners) {
				listener.paletteChunkProcessed(chunk);
			}
		}
		
		/**
		 * Passes a palette histogram chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announcePaletteHistogramChunk(PaletteHistogramChunk chunk) {
			for(ChunkListener listener: listeners) {
				listener.paletteHistogramChunkProcessed(chunk);
			}
		}
		
		/**
		 * Passes a physical pixel dimensions chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announcePhysicalPixelDimensionsChunk(PhysicalPixelDimensionsChunk chunk) {
			for(ChunkListener listener: listeners) {
				listener.physicalPixelDimensionsChunkProcessed(chunk);
			}
		}
		
		/**
		 * Passes a significant bits chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announceSignificantBitsChunk(SignificantBitsChunk chunk) {
			for(ChunkListener listener: listeners) {
				listener.significantBitsChunkProcessed(chunk);
			}
		}
		
		/**
		 * Passes a standard RGB color space chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announceStandardRgbColorSpaceChunk(StandardRgbColorSpaceChunk chunk) {
			for(ChunkListener listener: listeners) {
				listener.standardRgbColorSpaceChunkProcessed(chunk);
			}
		}
		
		/**
		 * Passes a suggested palette chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announceSuggestedPaletteChunk(SuggestedPaletteChunk chunk) {
			for(ChunkListener listener: listeners) {
				listener.suggestedPaletteChunkProcessed(chunk);
			}
		}
		
		/**
		 * Passes a text data chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announceTextDataChunk(TextDataChunk chunk) {
			for(ChunkListener listener: listeners) {
				listener.textDataChunkProcessed(chunk);
			}
		}
		
		/**
		 * Passes a trailer chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announceTrailerChunk(TrailerChunk chunk) {
			for(ChunkListener listener: listeners) {
				listener.trailerChunkProcessed(chunk);
			}
		}
		
		/**
		 * Passes a transparency chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announceTransparencyChunk(TransparencyChunk chunk) {
			for(ChunkListener listener: listeners) {
				listener.transparencyChunkProcessed(chunk);
			}
		}
		
		/**
		 * Passes an unknown chunk to the registered listeners.
		 * 
		 * @param chunk
		 * The chunk to pass.
		 */
		private void announceUnknownChunk(Chunk chunk) throws UnknownChunkException {
			for(ChunkListener listener: listeners) {
				listener.unknownChunkProcessed(chunk);
			}
		}
		
		/**
		 * Checks the parse mode, and changes it as appropriate. If
		 * it needs to be changed, the appropriate actions are taken
		 * for changing the state of the parser to the new mode.
		 *
		 * @throws InvalidFormatException
		 * If a chunk that is being read is invalid.
		 */
		private void checkParseModeCompletion() throws InvalidFormatException, UnknownChunkException {
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
						try {
							createChunk();
						} catch(InvalidChunkException e) {
							throw new InvalidFormatException("Error reading: "+e.getMessage(), "png");
						}
						parseMode = LENGTH_READING_MODE;
					}
					break;
			}
		}
		
		/**
		 * Creates a chunk based on the contained data and announces
		 * it to its listeners.
		 *
		 * @throws InvalidChunkException
		 * If the cyclic redundancy check read from the chunk
		 * does not match the one calculated on the type and data.
		 */
		private void createChunk() throws InvalidChunkException, UnknownChunkException {
			byte[] typeByteArray = this.typeBytes.toArray();
			String type = new String(typeByteArray, 0, 4); 
			byte[] data = chunkData.toArray();
			long crc = cyclicRedundancyCheck;
			
			if(type.equals("IHDR")) {
				announceHeaderChunk(new HeaderChunk(data, crc));
			} else if(type.equals("PLTE")) {
				announcePaletteChunk(new PaletteChunk(data, crc));
			} else if(type.equals("IDAT")) {
				announceImageDataChunk(new ImageDataChunk(data, crc));
			} else if(type.equals("IEND")) {
				parsing = false;
				announceTrailerChunk(new TrailerChunk(data, crc));
			} else if(type.equals("tRNS")) {
				announceTransparencyChunk(new TransparencyChunk(data, crc));
			} else if(type.equals("gAMA")) {
				announceGammaChunk(new GammaChunk(data, crc));
			} else if(type.equals("cHRM")) {
				announceChromaticitiesChunk(new ChromaticitiesChunk(data, crc));
			} else if(type.equals("sRGB")) {
				announceStandardRgbColorSpaceChunk(new StandardRgbColorSpaceChunk(data, crc));
			} else if(type.equals("iCCP")) {
				announceEmbeddedColorProfileChunk(new EmbeddedColorProfileChunk(data, crc));
			} else if(type.equals("tEXt")) {
				announceTextDataChunk(new TextDataChunk(data, crc));
			} else if(type.equals("zTXt")) {
				announceCompressedTextDataChunk(new CompressedTextDataChunk(data, crc));
			} else if(type.equals("iTXt")) {
				announceInternationalTextDataChunk(new InternationalTextDataChunk(data, crc));
			} else if(type.equals("bKGD")) {
				announceBackgroundColorChunk(new BackgroundColorChunk(data, crc));
			} else if(type.equals("pHYs")) {
				announcePhysicalPixelDimensionsChunk(new PhysicalPixelDimensionsChunk(data, crc));
			} else if(type.equals("sBIT")) {
				announceSignificantBitsChunk(new SignificantBitsChunk(data, crc));
			} else if(type.equals("sPLT")) {
				announceSuggestedPaletteChunk(new SuggestedPaletteChunk(data, crc));
			} else if(type.equals("hIST")) {
				announcePaletteHistogramChunk(new PaletteHistogramChunk(data, crc));
			} else if(type.equals("tIME")) {
				announceModificationTimeChunk(new ModificationTimeChunk(data, crc));
			} else {
				announceUnknownChunk(new Chunk(typeByteArray, data, crc));
			}
			resetDataFields();
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
		 * Converts a byte array into a long.
		 *
		 * @param subject
		 * The bytes to convert. If this is not at leas eight bytes
		 * long than it is padded according to big endian order
		 * (bytes are added in front of the given bytes).
		 *
		 * @return
		 * The long, made up of the first eight bytes in the array.
		 */
		private long getLongFromBytes(byte[] subject) {
			if(subject.length < 8) {
				ByteHolder holder = new ByteHolder(8);
				for(int i = 0; i < 8 - subject.length; i++) {
					holder.add((byte)0);
				}
				holder.add(subject);
				subject = holder.toArray();
			}
			ByteBuffer bb = ByteBuffer.wrap(subject);
			long converted = bb.getLong();
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
		 * Interprets the bytes in the crcBytes array into a single
		 * int.
		 */
		private void interpretCrcBytes() {
			cyclicRedundancyCheck = getLongFromBytes(crcBytes.toArray());
		}
		
		/**
		 * Interprets the bytes in the lengthBytes array into a single
		 * int.
		 */
		private void interpretLengthBytes() {
			dataLength = getIntFromBytes(lengthBytes.toArray());
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
		 * Parses a single integer into a byte and then parses the
		 * data itself.
		 *
		 * @param readByte
		 * The byte to parse.
		 *
		 * @throws InvalidFormatException
		 * When the header does not validate, or if a chunk crc does
		 * not match.
		 */
		private void parseByte(int readByte) throws InvalidFormatException, UnknownChunkException {
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
		 * Gets the data from the PNG file and stores it as generic
		 * chunks in the completedChunks ArrayList.
		 */
		private void parseChunksFromStream() throws StreamFailureException, InvalidFormatException, UnknownChunkException {
			int nextByteAsInt;
			parsing = true;
			parseMode = VERIFICATION_MODE;
			while(parsing && (nextByteAsInt = getNextByte()) != -1) {
				parseByte(nextByteAsInt);
			}
			try {
				pngStream.close();
			} catch(IOException e) {
				throw new StreamFailureException(e.getMessage());
			}
		}
		
		/**
		 * Initializes fields used for storing chunk data.
		 */
		private void resetDataFields() {
			dataLength				= 0;
			cyclicRedundancyCheck	= 0L;
			lengthBytes				= new ByteHolder(4);
			typeBytes				= new ByteHolder(4);
			crcBytes				= new ByteHolder(4);
			chunkData				= null;
			MAGIC_NUMBER.reset();
		}
		
		/**
		 * Sets the size of the chunkData array to the read length.
		 */
		private void setChunkDataSize() {
			chunkData = new ByteHolder(dataLength);
		}
		
		/**
		 * Checks a header byte for validity. After each check, the
		 * verification pointer is incremented.
		 *
		 * @param headerByte
		 * The byte from the header.
		 *
		 * @throws InvalidFormatException
		 * If a byte is not valid.
		 */
		private void verifyByte(int headerByte) throws InvalidFormatException {
			if(headerByte != MAGIC_NUMBER.next()) {
				throw new InvalidFormatException("Bad header file!", "png");
			}
		}
	}
	
	/**
	 * Writes Png Chunk arrays to disk.
	 */
	private class PngWriter {
		
		/**
		 * The file on disk.
		 */
		private File pngFile;
		
		/**
		 * The stream of the png file.
		 */
		private FileOutputStream pngStream;
		
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
		 * Creates the stream for the png file.
		 */
		private void createStream() throws FileNotFoundException {
			pngStream = new FileOutputStream(pngFile);
		}
		
		/**
		 * Gets the magic number constant from PortableNetworkGraphic
		 * and converts it into the proper form for writing.
		 *
		 * @return
		 * The magic number as an array of bytes.
		 */
		private byte[] getMagicNumber() {
			byte[] magic = new byte[PortableNetworkGraphic.MAGIC_NUMBER.length];
			for(int i = 0; i < PortableNetworkGraphic.MAGIC_NUMBER.length; i++) {
				magic[i] = (byte)PortableNetworkGraphic.MAGIC_NUMBER[i];
			}
			return magic;
		}
		
		/**
		 * Writes a chunk to the stream.
		 *
		 * @param chunk
		 * The chunk to write.
		 */
		private void writeChunk(Chunk c) throws StreamFailureException {
			try {
				pngStream.write(c.toBytes());
			} catch(IOException e) {
				throw new StreamFailureException(e.getMessage());
			}
		}
		
		/**
		 * Writes the magic number for a Png file to the stream.
		 */
		private void writeMagicNumber() throws StreamFailureException {
			byte[] magicNumber = getMagicNumber();
			try {
				pngStream.write(magicNumber);
			} catch(IOException e) {
				throw new StreamFailureException(e.getMessage());
			}
		}
	}
	
	/**
	 * Color type for color used.
	 */
	public static final int COLOR_TYPE_COLOR = 2;
	
	/**
	 * Color type for using both an alpha channel and color.
	 */
	public static final int COLOR_TYPE_COLOR_ALPHA = 6;
	
	/**
	 * Color type for using both color and a palette.
	 */
	public static final int COLOR_TYPE_COLOR_PALETTE = 3;
	
	/**
	 * Color type for no colors.
	 */
	public static final int COLOR_TYPE_GRAYSCALE = 0;
	
	/**
	 * Color type for using only an alpha channel.
	 */
	public static final int COLOR_TYPE_GRAYSCALE_ALPHA = 4;
	
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
	 * The background mode for grayscale.
	 */
	public static final int GRAYSCALE_MODE = 1;
	
	/**
	 * The number of bytes in each image data chunk.
	 */
	public static final int IDAT_BUFFER_LENGTH = 50;
	
	/**
	 * The background mode for indexed colors.
	 */
	public static final int INDEXED_COLOR_MODE = 0;
	
	/**
	 * Interlace method for Adam7 interlacing.
	 */
	public static final int INTERLACE_METHOD_ADAM7 = 1;
	
	/**
	 * Interlace method for no interlacing.
	 */
	public static final int INTERLACE_METHOD_NONE = 0;
	 
	/**
	 * The values in the header of a valid png file.
	 */
	public static final int[] MAGIC_NUMBER = {137, 80, 78, 71, 13, 10, 26, 10};
	
	/**
	 * Unit specifier for the meter.
	 */
	public static final int METER_UNIT = 1;
	
	/**
	 * The rendering intent of absolute colorimetric.
	 */
	public static final int RENDERING_INTENT_ABSOLUTE_COLORIMETRIC = 3;
	
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
	 * The background mode for truecolor.
	 */
	public static final int TRUECOLOR_MODE = 2;
	
	/**
	 * The amount of data that can be written uncompressed in
	 * bytes. This is used when compression depends on data
	 * size.
	 */
	public static final int UNCOMPRESSED_DATA_LIMIT = 1024;
	
	/**
	 * Unit specifier for unknown.
	 */
	public static final int UNKNOWN_UNIT = 0;
	
	/**
	 * The background color to show the image against.
	 */
	private Color backgroundColor;
	
	/**
	 * The image deflater.
	 */
	//private Deflater imageCompresser;
	
	/**
	 * A reference to a palette color for use as the
	 * background.
	 */
	private int backgroundColorIndex;
	
	/**
	 * The bit depth of this image.
	 */
	private int bitDepth;
	
	/**
	 * The color space for this Image;
	 */
	private Chromaticity chromaticity;
	
	/**
	 * The color type of this image.
	 */
	private int colorMode;
	
	/**
	 * The compression method used in this Png's image data.
	 */
	private int compressionMethod;
	
	/**
	 * Whether or not critical Chunks have been modified.
	 */
	private boolean criticallyModified = false;
	
	/**
	 * The number of the Scanline that is currently being added
	 * to the image.
	 */
	private int currentScanline = 0;
	
	/**
	 * The image data chunks. These are preserved in case unknown unsafe
	 * to copy chunks exist in the png.
	 */
	private Vector<ImageDataChunk> dataChunks = new Vector<ImageDataChunk>();
	
	/**
	 * The filter method used in this Png's image data.
	 */
	private int filterMethod;
	
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
	 * The image contained by this Png.
	 */
	private Image image;
	
	/**
	 * The image inflater.
	 */
	private Inflater imageDecompresser;
	
	/**
	 * What method of interlacing this Png uses.
	 */
	private int interlaceMethod;
	
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
	 * The excess data yielded from a Scanline extraction
	 * operation.
	 */
	private byte[] previousImageData;
	
	/**
	 * The color profile for this png.
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
	private int renderingIntent;
	
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
	 * The textual data from this png.
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
		switch(colorType) {
			case COLOR_TYPE_GRAYSCALE:
				if(bitDepth != 1 && bitDepth != 2 && bitDepth != 4 && bitDepth != 8 && bitDepth != 16) {
					throw new ValueOutOfRangeException("Cannot set bit depth to "+bitDepth+" in grayscale mode.");
				}
				break;
			
			case COLOR_TYPE_COLOR_PALETTE:
				if(bitDepth != 8 && bitDepth != 4 && bitDepth != 2 && bitDepth != 1) {
					throw new ValueOutOfRangeException("Cannot set bit depth to "+bitDepth+" in indexed mode.");
				}
				break;
				
			case COLOR_TYPE_GRAYSCALE_ALPHA:
			case COLOR_TYPE_COLOR:
			case COLOR_TYPE_COLOR_ALPHA:
				if(bitDepth != 8 && bitDepth != 16) {
					throw new ValueOutOfRangeException("Cannot set bit depth to "+bitDepth+" in current color mode.");
				}
		}
	}
	
	/**
	 * Creates a new PNG from a file.
	 *
	 * @param file
	 * The file to load the png data from.
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
	 * Reads the background color from a chunk.
	 *
	 * @param chunk.
	 * The background color chunk.
	 */
	public void backgroundColorChunkProcessed(BackgroundColorChunk chunk) {
		if(chunk.getColorMode() == colorMode) {
			switch(chunk.getColorMode()) {
				case COLOR_TYPE_GRAYSCALE:
				case COLOR_TYPE_GRAYSCALE_ALPHA:
				case COLOR_TYPE_COLOR:
				case COLOR_TYPE_COLOR_ALPHA:
					backgroundColor = chunk.getColor();
					break;
					
				case COLOR_TYPE_COLOR_PALETTE:
					backgroundColorIndex = chunk.getPaletteIndex();
					break;
			}
		} else {
			throw new RuntimeException("Color mode mismatch!");
		}
	}
	
	/**
	 * Reads a chromaticites chunk and assigns properties based
	 * on it.
	 *
	 * @param chunk
	 * The chromaticites chunk.
	 */
	public void chromaticitiesChunkProcessed(ChromaticitiesChunk chunk) {
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
	public void compressedTextDataChunkProcessed(CompressedTextDataChunk chunk) {
		addText(chunk.getKeyword(), chunk.getText());
	}
	
	/**
	 * Reads an embedded color profile.
	 *
	 * @param chunk
	 * The ICCP chunk.
	 */
	public void embeddedColorProfileChunkProcessed(EmbeddedColorProfileChunk chunk) {
		profile = new ColorProfile(chunk.getProfileName(), chunk.getProfile());
	}
	
	/**
	 * Reads a gamma chunk and assigns properties based on it.
	 *
	 * @param chunk
	 * The gamma chunk.
	 */
	public void gammaChunkProcessed(GammaChunk chunk) {
		gamma = chunk.getGamma();
		gammaSet = true;
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
		return bitDepth;
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
	public int getFilterMethod() {
		return filterMethod;
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
	 * Gets the height of the image.
	 *
	 * @return
	 * The height.
	 */
	public int getHeight() {
		return height;
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
	 * Gets all keywords in this Png that have text
	 * data associated with them.
	 *
	 * @return
	 * The used keywords.
	 */
	public String[] getKeywords() {
		return textData.keySet().toArray(new String[0]);
	}
	
	/**
	 * Gets the last modification time of this Png.
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
	 * for this png.
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
	 * Gets the rendering intent for this png image.
	 *
	 * @return
	 * The rendering intent.
	 */
	public int getRenderingIntent() {
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
	 * Gets the color to use as the transparent.
	 *
	 * @return
	 * The color value used as the transparency.
	 */
	public Color getTransparentColor() {
		return transparentColor;
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
	 * Gets the width of the image.
	 *
	 * @return
	 * The width.
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Checks to see if this png has a background color.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasBackgroundColor() {
		return (backgroundColor != null);
	}
	
	/**
	 * Checks to see if this png has chromaticity data.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasChromaticity() {
		return (chromaticity != null);
	}
	
	/**
	 * Checks to see if this png has gamma set.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasGamma() {
		return gammaSet;
	}
	
	/**
	 * Checks to see if this png has a histogram.
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
	 * Checks to see if this png has a palette.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasPalette() {
		return (colorMode == COLOR_TYPE_COLOR_PALETTE);
	}
	
	/**
	 * Checks to see if this png has a color profile.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasProfile() {
		return (profile != null);
	}
	
	/**
	 * Checks to see if this png has a rendering intent.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasRenderingIntent() {
		return renderingIntentSet;
	}
	
	/**
	 * Checks to see if this png has resolution data.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasResolution() {
		return (resolution != null);
	}
	
	/**
	 * Checks to see if this png has significat bits
	 * data.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasSignificantBits() {
		return significantBitsSet;
	}
	
	/**
	 * Checks to see if this png has a suggested palette.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasSuggestedPalette() {
		return (reducedPalette != null);
	}
	
	/**
	 * Checks to see if this png has text data.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasTextData() {
		return (textData != null);
	}
	
	/**
	 * Checks to see if this png should output
	 * a tRNS chunk.
	 *
	 * @return
	 * Whether it does.
	 */
	public boolean hasTransparencyData() {
		boolean has;
		switch(colorMode) {
			case COLOR_TYPE_COLOR:
			case COLOR_TYPE_GRAYSCALE:
				has = (transparentColor != null);
				break;
				
			case COLOR_TYPE_COLOR_PALETTE:
				has = (paletteAlphas != null);
				break;
			
			default:
				has = false;
		}
		return has;
	}
	
	/**
	 * Reads a header chunk and assigns properties based on it.
	 *
	 * @param header
	 * The header chunk.
	 */
	public void headerChunkProcessed(HeaderChunk header) {
		unknownChunks = unknownColorSpaceChunks;
		imageDecompresser = new Inflater();
		bitDepth = header.getBitDepth();
		height = header.getHeight();
		width = header.getWidth();
		colorMode = header.getColorType();
	}
	
	/**
	 * Builds an image from image data chunks.
	 *
	 * @param chunks
	 * The image chunk to process make up the image data.
	 */
	public void imageDataChunkProcessed(ImageDataChunk chunk) {
		unknownChunks = unknownPostDataChunks;
		dataChunks.add(chunk);
		byte[] compressedData = chunk.getData();
		byte[] decompressedData = decompressData(compressedData);
		Scanline[] lines = getScanlinesFromData(decompressedData);
		if(lines != null) {
			constructImage(lines);
		}
	}
	
	/**
	 * Reads international text data into this PNG.
	 * 
	 * @param chunk
	 * The international text data chunk.
	 */
	public void internationalTextDataChunkProcessed(InternationalTextDataChunk chunk) {
		addText(chunk.getKeyword(), chunk.getText());
	}
	
	/**
	 * Checks whether the image is interlaced.
	 *
	 * @return
	 * Whether it is.
	 */
	public boolean isInterlaced() {
		return (interlaceMethod != INTERLACE_METHOD_NONE);
	}
	
	/**
	 * Loads this Png from disk. The disk data is read, the image
	 * data is defiltered, compressable chunks are decompressed,
	 * and the chunks are converted into object data.
	 *
	 * @param location
	 * Where to load the Png file from.
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
	 * Gets the last modified information from that chunk.
	 *
	 * @param chunk
	 * The last modification time chunk.
	 */
	public void modificationTimeChunkProcessed(ModificationTimeChunk chunk) {
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
	public void paletteChunkProcessed(PaletteChunk chunk) {
		unknownChunks = unknownPreDataChunks;
		paletteColors = chunk.getPalette();
	}
	
	/**
	 * Gets the histogram information from the chunk data.
	 *
	 * @param chunk
	 * The palette histogram chunk.
	 */
	public void paletteHistogramChunkProcessed(PaletteHistogramChunk chunk) {
		paletteFrequencies = chunk.getFrequencies();
	}
	
	/**
	 * Reads dimensions.
	 *
	 * @param chunk
	 * The dimension chunk.
	 */
	public void physicalPixelDimensionsChunkProcessed(PhysicalPixelDimensionsChunk chunk) {
		resolution = new Resolution(chunk.getWidth(), chunk.getHeight());
	}
	
	/**
	 * Saves this Png to disk. Its data is converted into Chunks,
	 * compressable chunks are compressed, and the image data is
	 * filtered. The resulting Chunk array is then written to a
	 * file.
	 *
	 * @param location
	 * Where to save the Png file.
	 *
	 * @throws StreamFailureException
	 * If the file stream failed.
	 */
	public void save(String location) throws StreamFailureException {
		writePngFile(location, false);
	}
	
	/**
	 * Copies the image data of this Png to disk. Its image data is
	 * copied from the original source data unmodified.
	 * 
	 * @param location
	 * Where to save the Png file.
	 *
	 * @throws StreamFailureException
	 * If the file stream failed.
	 */
	public void copy(String location) throws StreamFailureException {
		writePngFile(location, true);
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
	 * Sets the chromaticity for this png image.
	 *
	 * @param chromaticity
	 * The chromaticity to use.
	 */
	public void setChromaticity(Chromaticity chromaticity) {
		this.chromaticity = chromaticity;
	}
	
	/**
	 * Sets the gamma of this png image.
	 *
	 * @param gamma
	 * The gamma.
	 */
	public void setGamma(int gamma) {
		this.gamma = gamma;
		gammaSet = true;
	}
	
	/**
	 * Sets the last modified time of this Png to the current time.
	 */
	public void setModified() {
		lastModified = new Date();
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
	 * for this png.
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
	 * Sets the rendering intent for this png image.
	 *
	 * @param renderingIntent
	 * The rendering intent.
	 */
	public void setRenderingIntent(int renderingIntent) {
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
	 * Reads the number of significant bits present in the original
	 * file.
	 *
	 * @param chunk
	 * The SignificantBitsChunk.
	 */
	public void significantBitsChunkProcessed(SignificantBitsChunk chunk) {
		significantColorBits = chunk.getColorBits();
		if(colorMode == COLOR_TYPE_GRAYSCALE_ALPHA || colorMode == COLOR_TYPE_COLOR_ALPHA) {
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
	public void standardRgbColorSpaceChunkProcessed(StandardRgbColorSpaceChunk chunk) {
		renderingIntent = chunk.getRenderingIntent();
		renderingIntentSet = true;
	}
	
	/**
	 * Reads the suggested palette from the chunk data.
	 *
	 * @param chunk
	 * The suggested palette chunk.
	 */
	public void suggestedPaletteChunkProcessed(SuggestedPaletteChunk chunk) {
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
	public void textDataChunkProcessed(TextDataChunk chunk) {
		addText(chunk.getKeyword(), chunk.getText());
	}
	
	/**
	 * Signals that the PNG data stream is at its end.
	 * 
	 * @param chunk
	 * The trailer chunk that signaled the end.
	 */
	public void trailerChunkProcessed(TrailerChunk chunk) {
		cleanUpDataStructures();
	}
	
	/**
	 * Reads a transparency chunk and assigns properties based
	 * on it.
	 *
	 * @param chunk
	 * The transparency chunk.
	 */
	public void transparencyChunkProcessed(TransparencyChunk chunk) {
		chunk.parseWithColorMode(colorMode);
		if(chunk.getColorMode() == colorMode) {
			switch(chunk.getColorMode()) {
				case COLOR_TYPE_GRAYSCALE:
					transparentColor = new GrayColor(chunk.getTransparentColor());
					
				case COLOR_TYPE_COLOR:
					transparentColor = chunk.getTransparentColor();
					break;
					
				case COLOR_TYPE_COLOR_PALETTE:
					paletteAlphas = chunk.getPaletteAlphas();
					break;
			}
		} else {
			throw new RuntimeException("Color mode mismatch!");
		}
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
	 * Sets this png to not copy unsafe to copy chunks when
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
		if(colorMode == COLOR_TYPE_COLOR_PALETTE) {
			bcc = new BackgroundColorChunk(palette.indexOf(backgroundColor));
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
			Arrays.fill(colorList, new Color(bitDepth));
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
			palette = new Palette("untitled", bitDepth, colorList, paletteFrequencies);
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
	 */
	private byte[] compressData(byte[] data) {
		ZlibCompresser compresser = new ZlibCompresser(data);
		return compresser.compress();
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
		boolean hasAlpha = (colorMode == COLOR_TYPE_COLOR_ALPHA);
		if(image == null) {
			image = new Image(width, height, bitDepth, hasAlpha);
		}
		Color color = new Color(bitDepth);
		for(int i = 0; i < scanlines.length; i++) {
			for(int x = 0; x < scanlines[i].pixels(); x++) {
				color.setRed(scanlines[i].getSample(x, Scanline.RED_SAMPLE));
				color.setGreen(scanlines[i].getSample(x, Scanline.GREEN_SAMPLE));
				color.setBlue(scanlines[i].getSample(x, Scanline.BLUE_SAMPLE));
				if(hasAlpha) {
					color.setAlpha(scanlines[i].getSample(x, Scanline.ALPHA_SAMPLE));
				} else if(transparentColor != null && transparentColor.equals(color)) {
					color.setAlpha(0);
				}
				image.setColorAt(x, currentScanline, color);
			}
			currentScanline++;
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
		boolean hasAlpha = (colorMode == COLOR_TYPE_GRAYSCALE_ALPHA);
		if(image == null) {
			image = new Image(width, height, bitDepth, hasAlpha);
		}
		GrayColor color = new GrayColor(bitDepth);
		for(int i = 0; i < scanlines.length; i++) {
			for(int x = 0; x < scanlines[i].pixels(); x++) {
				color.setValue(scanlines[i].getSample(x, Scanline.GRAYSCALE_VALUE_SAMPLE));
				if(hasAlpha) {
					color.setAlpha(scanlines[i].getSample(x, Scanline.GRAYSCALE_ALPHA_SAMPLE));
				} else if(transparentColor != null && transparentColor.equals(color)) {
					color.setAlpha(0);
				}
				image.setColorAt(x, currentScanline, color);
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
		switch(colorMode) {
			case COLOR_TYPE_GRAYSCALE:
			case COLOR_TYPE_GRAYSCALE_ALPHA:
				constructGrayscaleImage(scanlines);
				break;
				
			case COLOR_TYPE_COLOR:
			case COLOR_TYPE_COLOR_ALPHA:
				constructColorImage(scanlines);
				break;
				
			case COLOR_TYPE_COLOR_PALETTE:
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
		if(image == null) {
			image = new Image(width, height, bitDepth);
		}
		Color color;
		for(int i = 0; i < scanlines.length; i++) {
			for(int x = 0; x < scanlines[i].pixels(); x++) {
				int paletteIndex = scanlines[i].getSample(x, Scanline.PALETTE_INDEX_SAMPLE);
				color = palette.getColor(paletteIndex);
				image.setColorAt(x, currentScanline, color);
			}
			currentScanline++;
		}
	}
	
	/**
	 * Converts the data in this Png into chunks to be written
	 * to disk.
	 * 
	 * @param forcePreservation
	 * Whether or not the image data is preserved from the source.
	 */
	private Chunk[] convertToChunks(boolean forcePreservation) {
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
	 */
	private byte[] decompressData(byte[] data) {
		imageDecompresser.setInput(data);
		byte[] outputBuffer = new byte[50];
		int actualLength = 0;
		byte[] decompressedData = null;
		while(!imageDecompresser.needsInput() && !imageDecompresser.finished()) {
			try {
				actualLength = imageDecompresser.inflate(outputBuffer);
			} catch(DataFormatException e) {
				System.err.println("Bad DEFLATE format!");
			}
			int oldDataLength = (decompressedData != null) ? decompressedData.length : 0;
			if(decompressedData == null) {
				decompressedData = new byte[0];
			}
			byte[] holder = new byte[decompressedData.length + actualLength];
			for(int i = 0; i < decompressedData.length; i++) {
				holder[i] = decompressedData[i];
			}
			decompressedData = holder;
			for(int i = 0; i < actualLength; i++) {
				decompressedData[oldDataLength + i] = outputBuffer[i];
			}
		}
		return decompressedData;
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
		boolean hasAlpha = (colorMode == COLOR_TYPE_COLOR_ALPHA);
		Scanline[] lines = new Scanline[height];
		Color color;
		for(int y = 0; y < height; y++) {
			lines[y] = new Scanline(samplesPerPixel(), bitDepth, width);
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
		boolean hasAlpha = (colorMode == COLOR_TYPE_GRAYSCALE_ALPHA);
		Scanline[] lines = new Scanline[height];
		GrayColor color;
		for(int y = 0; y < height; y++) {
			lines[y] = new Scanline(samplesPerPixel(), bitDepth, width);
			for(int x = 0; x < width; x++) {
				color = new GrayColor(bitDepth);
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
		switch(colorMode) {
			case COLOR_TYPE_GRAYSCALE:
			case COLOR_TYPE_GRAYSCALE_ALPHA:
				scanlines = deconstructGrayscaleImage(image);
				break;
				
			case COLOR_TYPE_COLOR:
			case COLOR_TYPE_COLOR_ALPHA:
				scanlines = deconstructColorImage(image);
				break;
				
			case COLOR_TYPE_COLOR_PALETTE:
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
			lines[y] = new Scanline(samplesPerPixel(), bitDepth, width);
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
	 */
	private byte[] deconstructScanlines(Scanline[] lines) {
		ByteHolder holder = new ByteHolder(lines.length * getScanlineWidth());
		byte[] buffer;
		for(Scanline sl: lines) {
			buffer = sl.getFiltered(Scanline.NO_FILTER);
			System.out.print("[OUT]: ");
			for(byte b: buffer) {
				System.out.print(b+":");
			}
			System.out.println();
			holder.add(buffer);
		}
		return holder.toArray();
	}
	
	/**
	 * Builds image chunks from the image data.
	 *
	 * @return
	 * The ImageDataChunks that decode to the image data.
	 */
	private ImageDataChunk[] encodeImageData() {
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
		pw = samplesPerPixel() * ((double)bitDepth / 8);
		return pw;
	}
	
	/**
	 * Gets any scan lines that may exist from the data.
	 * 
	 * @param data
	 * Extra data to be added to the previous data in order to
	 * create scan lines.
	 */
	private Scanline[] getScanlinesFromData(byte[] data) {
		byte[] composedData;
		if(previousImageData != null) {
			composedData = ArrayHelper.append(previousImageData, data);
		} else {
			composedData = data;
		}
		int numberOfScanlines = composedData.length / getScanlineWidth();
		Scanline[] lines = null;
		if(numberOfScanlines > 0) {
			lines = new Scanline[numberOfScanlines];
			int start;
			int end;
			byte[] scanlineData;
			for(int i = 0; i < numberOfScanlines; i++) {
				start = i*getScanlineWidth();
				end = (i+1)*getScanlineWidth();
				scanlineData = Arrays.copyOfRange(composedData, start, end);
				System.out.print("[IN]: ");
				for(byte b: scanlineData) {
					System.out.print(b+":");
				}
				System.out.println();
				lines[i] = new Scanline(samplesPerPixel(), bitDepth, scanlineData);
			}
			start = numberOfScanlines * getScanlineWidth();
			end = composedData.length;
			previousImageData = Arrays.copyOfRange(composedData, start, end);
		} else {
			previousImageData = composedData;
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
		HeaderChunk hc = new HeaderChunk(width, height, bitDepth, colorMode, compressionMethod, filterMethod, interlaceMethod);
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
	 * Reads the png data from a file.
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
		FileInputStream f = new FileInputStream(file);
		PngReader reader = new PngReader(f);
		reader.addChunkListener(this);
		try {
			reader.read();
		} catch(UnknownChunkException e) {
			throw new InvalidFormatException("Unknown critical chunk '"+e.getType()+"' encountered.", "png");
		}
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
		PhysicalPixelDimensionsChunk ppdc = new PhysicalPixelDimensionsChunk(resolution.x, resolution.y, METER_UNIT);
		return ppdc;
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
	 * Sets the background color if only an index to a palette color
	 * was provided.
	 */
	private void setIndexedBackgroundColor() {
		if(colorMode == COLOR_TYPE_COLOR_PALETTE) {
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
		switch(colorMode) {
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
	 */
	private TextChunk[] textDataToChunks() {
		Vector<TextChunk> chunks = new Vector<TextChunk>();
		for(Map.Entry<String,ArrayList<String>> keywordSet: textData.entrySet()) {
			String keyword = keywordSet.getKey();
			ArrayList<String> texts = keywordSet.getValue();
			for(String contents: texts) {
				TextChunk tc = null;
				if(contents.length() > UNCOMPRESSED_DATA_LIMIT) {
					tc = new CompressedTextDataChunk(keyword, contents, COMPRESSION_METHOD_ZLIB);
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
		if(colorMode == COLOR_TYPE_COLOR_PALETTE) {
			int[] transparencies = extractTransparencies();
			tc = new TransparencyChunk(transparencies);
		} else if(colorMode == COLOR_TYPE_GRAYSCALE) {
			tc = new TransparencyChunk(((GrayColor)transparentColor).getValue());
		} else {
			tc = new TransparencyChunk(transparentColor.getRed(), transparentColor.getGreen(), transparentColor.getBlue());
		}
		return tc;
	}
	
	/**
	 * Writes the png data to a file.
	 *
	 * @param file
	 * The name of the file to write the data to.
	 * 
	 * @param forcePreservation
	 * Whether or not image data should be forced to
	 * be preserved from the original chunks.
	 *
	 * @throws StreamFailureException
	 * If the file stream failed.
	 */
	private void writePngFile(String file, boolean forcePreservation) throws StreamFailureException {
		Chunk[] chunks = convertToChunks(forcePreservation);
		PngWriter writer = null;
		while(writer == null) {
			try {
				writer = new PngWriter(file);
			} catch(FileNotFoundException e) {
				File f = new File(file);
				try {
					f.createNewFile();
				} catch(IOException ioe) {
					throw new StreamFailureException(ioe.getMessage());
				}
			}
		}
		writer.write(chunks);
	}
}