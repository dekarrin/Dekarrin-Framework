package com.dekarrin.file.png;

import com.dekarrin.error.ValueOutOfRangeException;
import com.dekarrin.util.ArrayHelper;
import com.dekarrin.util.ByteParser;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * Represents a chunk from a PNG file.
 */
public class Chunk {
	
	/**
	 * Stores the different types of Chunks and their classes.
	 */
	private static HashMap<Integer,String> chunkTypes = new HashMap<Integer,String>();
	
	public static final int IHDR = 0x49484452;
	public static final int PLTE = 0x504c5445;
	public static final int IDAT = 0x49444154;
	public static final int IEND = 0x49454e44;
	public static final int tRNS = 0x74524e53;
	public static final int gAMA = 0x67414d41;
	public static final int cHRM = 0x6348524d;
	public static final int sRGB = 0x73524742;
	public static final int iCCP = 0x69434350;
	public static final int iTXt = 0x69545874;
	public static final int tEXt = 0x74455874;
	public static final int zTXt = 0x7a545874;
	public static final int bKGD = 0x624b4744;
	public static final int pHYs = 0x70485973;
	public static final int sBIT = 0x73424954;
	public static final int sPLT = 0x73504c54;
	public static final int hIST = 0x68495354;
	public static final int tIME = 0x74494d45;
	
	static {
		registerChunk(Chunk.IHDR, "HeaderChunk");
		registerChunk(Chunk.PLTE, "PaletteChunk");
		registerChunk(Chunk.IDAT, "ImageDataChunk");
		registerChunk(Chunk.IEND, "TrailerChunk");
		registerChunk(Chunk.tRNS, "TransparencyChunk");
		registerChunk(Chunk.gAMA, "GammaChunk");
		registerChunk(Chunk.cHRM, "ChromaticitiesChunk");
		registerChunk(Chunk.sRGB, "StandardRgbColorSpaceChunk");
		registerChunk(Chunk.iCCP, "EmbeddedColorProfileChunk");
		registerChunk(Chunk.iTXt, "InternationalTextDataChunk");
		registerChunk(Chunk.tEXt, "TextDataChunk");
		registerChunk(Chunk.zTXt, "CompressedTextDataChunk");
		registerChunk(Chunk.bKGD, "BackgroundColorChunk");
		registerChunk(Chunk.pHYs, "PhysicalPixelDimensionsChunk");
		registerChunk(Chunk.sBIT, "SignificantBitsChunk");
		registerChunk(Chunk.sPLT, "SuggestedPaletteChunk");
		registerChunk(Chunk.hIST, "PaletteHistogramChunk");
		registerChunk(Chunk.tIME, "ModificationTimeChunk");
	}
	
	/**
	 * Used for parsing properties from chunk data.
	 */
	protected ByteParser parser;
	
	/**
	 * The type of this chunk.
	 */
	private int chunkType;
	
	/**
	 * The data bytes appropriate to the chunk type. This may be null.
	 */
	private byte[] chunkData;
	
	/**
	 * Creates a new Chunk.
	 *
	 * @param type
	 * The type of the chunk. This is made of four letters.
	 *
	 * @param data
	 * The data in the chunk.
	 */
	public Chunk(int type, byte[] data) {
		chunkData = data;
		chunkType = type;
		parser = new ByteParser(chunkData);
	}
	
	/**
	 * Creates a new Chunk with only a type name.
	 *
	 * @param type
	 * The chunk type.
	 */
	public Chunk(int type) {
		chunkType = type;
	}
	
	/**
	 * Gets the type of this chunk as a four-character String.
	 *
	 * @return
	 * The name.
	 */
	public String getTypeName() {
		return Chunk.typeToName(chunkType);
	}
	
	/**
	 * Gets the type of this chunk.
	 *
	 * @return
	 * The type.
	 */
	public int getType() {
		return chunkType;
	}
	
	/**
	 * Gets the length of this chunk.
	 *
	 * @return
	 * The length.
	 */
	public int getLength() {
		return chunkData.length;
	}
	
	/**
	 * Gets the data of this chunk.
	 *
	 * @return
	 * The data.
	 */
	public byte[] getData() {
		return chunkData;
	}
	
	/**
	 * Checks if this chunk is ancillary.
	 *
	 * @return
	 * Whether it is.
	 */
	public boolean isAncillary() {
		return ((chunkType & 0x20000000) != 0);
	}
	
	/**
	 * Checks if this chunk is private.
	 *
	 * @return
	 * Whether it is.
	 */
	public boolean isPrivate() {
		return ((chunkType & 0x200000) != 0);
	}
	
	/**
	 * Checks if this chunk is safe to copy.
	 *
	 * @return
	 * Whether it is.
	 */
	public boolean isSafeToCopy() {
		return ((chunkType & 0x20) != 0);
	}
	
	/**
	 * Sets the chunkData field.
	 *
	 * @param data
	 * What to set the chunk data to.
	 */
	protected void setChunkData(byte[] data) {
		chunkData = data;
		parser = new ByteParser(chunkData);
	}
	
	/**
	 * Converts a 4 type bytes into a single integer value.
	 * 
	 * @param typeBytes
	 * The bytes to convert.
	 * 
	 * @return
	 * A 32-bit type code for the given typeBytes.
	 */
	public static int bytesToType(byte[] typeBytes) {
		return ArrayHelper.toInt(typeBytes);
	}
	
	/**
	 * Converts a 32-bit type code into 4 type bytes.
	 * 
	 * @param typeCode
	 * The type value to convert.
	 * 
	 * @return
	 * The 4 type bytes that make up {@code typeCode}.
	 */
	public static byte[] typeToBytes(int typeCode) {
		return ArrayHelper.toArray(typeCode);
	}
	
	/**
	 * Gets a specific type of Chunk and initializes it with
	 * data.
	 * 
	 * @param type
	 * The type code of the Chunk to create.
	 * 
	 * @param data
	 * The data to fill the chunk with.
	 */
	public static Chunk getChunkObject(int type, byte[] data) {
		return loadChunkClass(chunkTypes.get(type), data);
	}
	
	/**
	 * Registers a chunk type and its associated class.
	 * 
	 * @param typeCode
	 * The type code of the chunk.
	 * 
	 * @param className
	 * The class that represents that chunk.
	 */
	public static void registerChunk(int typeCode, String className) throws ValueOutOfRangeException {
		chunkTypes.put(typeCode, className);
	}
	
	/**
	 * Converts a type String into an array of bytes.
	 * 
	 * @param typeName
	 * The name of the type.
	 * 
	 * @return
	 * The bytes that make up the type name.
	 */
	public static byte[] nameToBytes(String typeName) {
		char[] typeChars = typeName.toCharArray();
		byte nextTypeByte;
		char nextTypeChar;
		byte[] typeBytes = new byte[4];
		for(int i = 0; i < typeBytes.length; i++) {
			nextTypeChar = typeChars[i];
			nextTypeByte = ArrayHelper.toArray(nextTypeChar)[1];
			if(!isValidTypeByte(nextTypeByte)) {
				throw new ValueOutOfRangeException("Unsupported character in chunk name; value is "+nextTypeByte);
			} else {
				typeBytes[i] = nextTypeByte;
			}
		}
		return typeBytes;
	}
	
	/**
	 * Checks whether a byte, if converted to a character, would
	 * result in an ASCII letter. This can be either a lowercase
	 * or uppercase letter that falls within the range [65, 90]
	 * or [97-122].
	 * 
	 * @param typeByte
	 * The byte to check.
	 * 
	 * @return
	 * Whether the byte can be converted into an ASCII letter.
	 */
	public static boolean isValidTypeByte(byte typeByte) {
		boolean valid = true;
		if((typeByte < 65 || typeByte > 90) && (typeByte < 97 || typeByte > 122)) {
			valid = false;
		}
		return valid;
	}
	
	/**
	 * Converts a type name to a type code.
	 * 
	 * @param typeName
	 * The name to convert into a code.
	 * 
	 * @return
	 * The type code.
	 */
	public static int nameToType(String typeName) {
		byte[] typeBytes = nameToBytes(typeName);
		return bytesToType(typeBytes);
	}
	
	/**
	 * Converts a type code to a type name.
	 * 
	 * @param typeCode
	 * The type to convert into a name.
	 * 
	 * @return
	 * The type code as a name.
	 */
	public static String typeToName(int typeCode) {
		byte[] typeBytes = typeToBytes(typeCode);
		return bytesToName(typeBytes);
	}
	
	/**
	 * Converts type bytes to a type name.
	 * 
	 * @param typeBytes
	 * The bytes to convert.
	 * 
	 * @return
	 * The type name.
	 */
	public static String bytesToName(byte[] typeBytes) {
		return new String(typeBytes, 0, 4);
	}
	
	/**
	 * Loads a chunk class.
	 * 
	 * @param name
	 * The name of the class.
	 * 
	 * @param data
	 * The data to fill the chunk with.
	 * 
	 * @return
	 * The chunk class.
	 */
	private static Chunk loadChunkClass(String name, byte[] data) {
		String chunkClassName = "com.dekarrin.file.png." + name;
		Class<?> chunkClass = null;
		Constructor<?> chunkConstructor = null;
		Chunk chunk = null;
		try {
			chunkClass = Class.forName(chunkClassName);
			chunkConstructor = chunkClass.getConstructor(byte[].class);
			chunk = (Chunk)chunkConstructor.newInstance(data);
		} catch(ClassNotFoundException e) {
			System.err.println("Can't load class:" + e.getMessage());
			System.exit(1);
		} catch(NoSuchMethodException e) {
			System.err.println("Can't load class:" + e.getMessage());
			System.exit(1);
		} catch(InvocationTargetException e) {
			System.err.println("Can't load class:" + e.getMessage());
			System.exit(1);
		} catch(IllegalAccessException e) {
			System.err.println("Can't load class:" + e.getMessage());
			System.exit(1);
		} catch(InstantiationException e) {
			System.err.println("Can't load class:" + e.getMessage());
			System.exit(1);
		}
		return chunk;
	}
}