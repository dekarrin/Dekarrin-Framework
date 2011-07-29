package com.dekarrin.file.png;

import java.io.*;
import java.util.zip.CRC32;

import com.dekarrin.io.InvalidFormatException;
import com.dekarrin.io.StreamFailureException;

class ChunkInputStream extends DataInputStream {
	
	/**
	 * Whether the PNG has been verified yet.
	 */
	private boolean verified = false;
	
	/**
	 * The values in the header of a valid PNG file.
	 */
	public static final int[] MAGIC_NUMBER = {137, 80, 78, 71, 13, 10, 26, 10};
	
	/**
	 * Creates a new ChunkInputStream for an InputStream.
	 * 
	 * @param iStream
	 * The input stream.
	 */
	public ChunkInputStream(InputStream iStream) {
		super(iStream);
	}
	
	/**
	 * Confirms that the magic number is correct. If
	 * it is not correct, an exception is thrown.
	 * 
	 * @throws InvalidFormatException
	 * If the magic number is incorrect.
	 *
	 * @throws StreamFailureException
	 * If an IOException occurs.
	 */
	public void verifyPng() throws InvalidFormatException, StreamFailureException {
		if(!verified) {
			verified = true;
			for(int i = 0; i < MAGIC_NUMBER.length; i++) {
				try {
					if(readUnsignedByte() != MAGIC_NUMBER[i]) {
						throw new InvalidFormatException("Bad header", "png");
					}
				} catch(IOException e) {
					throw new StreamFailureException(e.getMessage());
				}
			}
		}
	}
	
	/**
	 * Reads the next Chunk.
	 * 
	 * @return
	 * The next Chunk in the stream.
	 */
	public Chunk readChunk() throws InvalidFormatException, StreamFailureException {
		if(!verified) {
			verifyPng();
		}
		int size, type;
		byte[] chunkData;
		long crc;
		try {
			size = readSize();
			type = readType();
			chunkData = readData(size);
			crc = readCrc();
		} catch(EOFException e) {
			throw new InvalidFormatException("Unexpected end of file", "png");
		} catch(IOException e) {
			throw new StreamFailureException(e.getMessage());
		}
		if(!checksumMatches(crc, type, chunkData)) {
			throw new InvalidFormatException("Corrupted chunk encountered", "png");
		}
		return Chunk.getChunkObject(type, chunkData);
	}
	
	/**
	 * Reads the size of the chunk.
	 * 
	 * @return
	 * The size of the chunk.
	 */
	private int readSize() throws EOFException, IOException {
		int size = readInt();
		return size;
	}
	
	/**
	 * Reads the type code of the chunk.
	 * 
	 * @return
	 * The type code.
	 */
	private int readType() throws EOFException, IOException, InvalidFormatException {
		byte[] typeBytes = new byte[4];
		for(int i = 0; i < typeBytes.length; i++) {
			typeBytes[i] = readByte();
			if(!Chunk.isValidTypeByte(typeBytes[i])) {
				throw new InvalidFormatException("Type code out of range", "png");
			}
		}
		int type = Chunk.bytesToType(typeBytes);
		return type;
	}
	
	/**
	 * Reads the chunk data from the body of the chunk.
	 * 
	 * @param dataLength
	 * How long the data field is.
	 * 
	 * @return
	 * The chunk data.
	 */
	private byte[] readData(int dataLength) throws EOFException, IOException {
		byte[] chunkData = new byte[dataLength];
		for(int i = 0; i < chunkData.length; i++) {
			chunkData[i] = readByte();
		}
		return chunkData;
	}
	
	/**
	 * Reads the cyclic redundancy code from the chunk.
	 * 
	 * @return
	 * The CRC.
	 */
	private long readCrc() throws EOFException, IOException {
		long crc = makeUnsigned(readInt());
		return crc;
	}
	
	/**
	 * Checks if the CRC-32 checksum for the read data matches
	 * the one read from the chunk.
	 * 
	 * @param readChecksum
	 * The CRC read from the chunk.
	 * 
	 * @param type
	 * The type code of the chunk being verified.
	 * 
	 * @param data
	 * The data from the chunk being verified.
	 * 
	 * @return
	 * True if the checksum from the type and data matches the
	 * one read from the chunk.
	 */
	private boolean checksumMatches(long readChecksum, int type, byte[] data) {
		CRC32 crcGenerator = new CRC32();
		crcGenerator.update(Chunk.typeToBytes(type));
		crcGenerator.update(data);
		long calculatedChecksum = crcGenerator.getValue();
		return (readChecksum == calculatedChecksum);
	}
	
	/**
	 * Converts a signed integer value into an unsigned long
	 * value. Bitmasking is used to revert the sign bit to a
	 * numeric bit.
	 * 
	 * @param intValue
	 * The value to be converted into an unsigned value.
	 * 
	 * @return
	 * The given value as an unsigned long.
	 */
	private long makeUnsigned(int intValue) {
		long longValue = intValue & 0xffffffffL;
		return longValue;
	}
}
