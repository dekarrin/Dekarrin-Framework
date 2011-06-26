package com.dekarrin.parse;

import com.dekarrin.util.ByteHolder;
import com.dekarrin.util.IntHolder;
import com.dekarrin.file.png.*;
import com.dekarrin.error.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Parses a PNG file into a PNG object.
 */
public class PngParser {
	
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
	 * Creates a new PngParser and parses a PNG file.
	 *
	 * @param filename
	 * The location of the PNG.
	 */
	public PngParser(String filename) throws FileNotFoundException {
		pngFile = new File(filename);
		createStream();
		resetDataFields();
	}
	
	/**
	 * Parses the png file into a PNG object.
	 *
	 * @returns
	 * The png object.
	 */
	public void parse() throws InvalidFileFormatException, StreamFailureException {
		parseChunksFromFile();
		parseIndividualChunks();
	}
	
	/**
	 * Gets the parsed chunks as an array.
	 *
	 * @returns
	 * The chunks.
	 */
	public Chunk[] getChunks() {
		Chunk[] chunks = completedChunks.toArray(new Chunk[0]);
		return chunks;
	}
	
	/**
	 * Parses each chunk into its proper type.
	 */
	private void parseIndividualChunks() {
		for(int i = 0; i < completedChunks.size(); i++) {
			Chunk converted = parseChunk(completedChunks.get(i));
			completedChunks.put(i, converted);
		}
	}
	
	/**
	 * Parses a chunk into its correct type based on its type.
	 */
	private Chunk parseChunk(Chunk pc) {
		String type = pc.getTypeName();
		byte[] data = pc.getData();
		int crc = pc.getCrc();
		
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
			newChunk = new SignficantBitsChunk(data, crc);
		} else if(type.equals("sPLT")) {
			newChunk = new SuggestesPaletteChunk(data, crc);
		} else if(type.equals("hIST")) {
			newChunk = new PaletteHistogramChunk(data, crc);
		} else if(type.equals("tIME")) {
			newChunk = new LastModificationTimeChunk(data, crc);
		} else {
			newChunk = pc;
		}
		
		return newChunk;
	}
	
	/**
	 * Gets the data from the png file and stores it as generic
	 * chunks in the completedChunks ArrayList.
	 */
	private void parseChunksFromFile() {
		boolean parsing = true;
		int nextByteAsInt;
		parseMode = VERIFICATION_MODE;
		while((nextByteAsInt = getNextByte()) != -1) {
			parseByte(nextByteAsInt);
		}
		pngStream.close();
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
					Chunk chunk = createNewChunk();
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
	 * @returns
	 * True if the position has come to the end; false otherwise.
	 */
	private boolean isComplete(ByteHolder holder) {
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
	 * Creates a new Chunk object from the chunk that was read.
	 * After the Chunk is created, the old chunk data is deleted.
	 *
	 * @returns
	 * The created Chunk.
	 */
	private Chunk createNewChunk() {
		Chunk chunk = new Chunk(typeBytes.toArray(), chunkData.toArray(), cyclicRedundancyCheck);
		resetDataFields();
		return chunk;
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
	 * @returns
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
	 * @returns
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