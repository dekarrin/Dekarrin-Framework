package com.dekarrin.zip;

import java.io.*;

/**
 * Provides methods for progressively reading compressed
 * data.
 */
public class PersistantZlibCompresser {
	
	public static int COMPRESSION_METHOD_DEFLATE = 8;
	public static int COMPRESSION_ALGORITHM_FASTEST = 0;
	public static int COMPRESSION_ALGORITHM_FAST = 1;
	public static int COMPRESSION_ALGORITHM_DEFAULT = 2;
	public static int COMPRESSION_ALGORITHM_SLOWEST = 3;
	
	/**
	 * The compression method used by the data.
	 */
	private int compressionMethod;
	
	/**
	 * Information on the compressed data.
	 */
	private int compressionInfo;
	
	/**
	 * Initial bytes fed to compresser.
	 */
	private int dictionary;
	
	/**
	 * The compression algorithm to use with this compression.
	 */
	private int compressionLevel;
	
	/**
	 * Adler-32 struct, used for calculating adler-32 values
	 * as data is compressed.
	 */
	private /* struct */ class Adler {
		public int s1 = 1;
		public int s2 = 0;
		public void addBytes(byte[] bytes) {
			for(byte b: bytes) {
				int a = byteToInt(b);
				s1 = (s1 + a) % 65521;
				s2 = (s2 + s1) % 65521;
			}
		}
		public int getAdler32() {
			return (s1 << 16) | s2;
		}
	} private Adler adlr;
	
	/**
	 * The Adler-32 checksum calculated on the compressed data.
	 */
	private int adlerChecksum;
	
	/**
	 * Whether or not this is the first time that data has
	 * been compressed.
	 */
	private boolean firstCompression = true;
	
	/**
	 * The output stream to write the compressed data to.
	 */
	private OutputStream out;
	
	/**
	 * Creates a new compresser that gives its output to a
	 * stream.
	 * 
	 * @param stream
	 * The output stream to write the compressed data to.
	 */
	public PersistantZlibCompresser(OutputStream stream) {
		out = stream;
	}
	
	/**
	 * Creates a new compresser that writes the compressed data to
	 * a file.
	 * 
	 * @param location
	 * The location of the file to write to. If it does not exist,
	 * it will be created.
	 */
	public PersistantZlibCompresser(String location) {
		File file = new File(location);
		FileOutputStream fileStream = null;
		while(fileStream == null) {
			try {
				fileStream = new FileOutputStream(file);
			} catch(FileNotFoundException e) {
				try {
					file.createNewFile();
				} catch (IOException ioe) {
					ioe.printStackTrace();
					System.exit(1);
				}
			}
		}
	}
	
	/**
	 * Compresses data and adds it to the stream.
	 * 
	 * @param data
	 * The data to compress.
	 */
	public void compress(byte[] data) {
		adlr.addBytes(data);
		byte[] compressedData = compressData(data);
		if(firstCompression) {
			writeHeader();
			firstCompression = false;
		}
		writeData(compressedData);
	}
	
	/**
	 * Writes the header and closes the stream.
	 */
	public void finish() {
		writeFooter();
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Writes the header information to the stream.
	 */
	private void writeHeader() {
		writeData(createCmf());
		writeData(createFlg());
		if(hasDictionary()) {
			writeData(dictionaryId());
		}
	}
	
	/**
	 * Writes the footer information to the stream.
	 */
	private void writeFooter() {
		calculateAdlerChecksum();
		writeData(intToBytes(adlerChecksum, 4));
	}
	
	/**
	 * Writes a sequence of bytes to the output stream.
	 * 
	 * @param dataBytes
	 * The bytes to write.
	 */
	private void writeData(byte[] dataBytes) {
		try {
			out.write(dataBytes);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Writes a single byte to the output stream.
	 */
	private void writeData(byte dataByte) {
		writeData(new byte[]{dataByte});
	}
	
	/**
	 * Creates the CMF byte for the current data.
	 * 
	 * @return
	 * The CMF byte.
	 */
	private byte createCmf() {
		byte cmf = (byte)(compressionMethod & 0xf);
		cmf |= ((byte)(compressionInfo & 0xf) << 4);
		return cmf;
	}
	
	/**
	 * Creates the FLG byte for the current data.
	 * 
	 * @return
	 * The FLG byte.
	 */
	private byte createFlg() {
		byte flg = 0;
		if(hasDictionary()) {
			flg |= 0x10;
		}
		flg |= ((byte)(compressionLevel & 0x3) << 6);
		flg |= (calculateFcheck(flg) & 0x1f);
		return flg;
	}
	
	/**
	 * Calculates the FCHECK value based on the current
	 * value and the value of the CMF byte.
	 * 
	 * @param currentFlg
	 * The FLG byte as it currently is.
	 */
	private byte calculateFcheck(byte currentFlg) {
		int combinedInt = createFcheckInt(currentFlg, (byte)0);
		byte fcheck = (byte)(31 - (combinedInt % 31));
		return fcheck;
	}
	
	/**
	 * Creates the FCHECK int for correctness calculation.
	 * 
	 * @param currentFlg
	 * The flg as it currently is.
	 * 
	 * @param fcheck
	 * The value to use for FCHECK.
	 * 
	 * @return
	 * The int created by putting the FLG and CMF bytes together.
	 */
	private int createFcheckInt(byte currentFlg, byte fcheck) {
		int combined = (createCmf() << 8) | (currentFlg | (fcheck & 0x1f));
		return combined;
	}
	
	/**
	 * Converts an int into bytes.
	 *
	 * @param value
	 * The int to convert.
	 *
	 * @param width
	 * The number of bytes to convert to. Depending on
	 * how big the int is, this may alter the value.
	 * 
	 * @return
	 * The bytes that make up the int.
	 */
	private byte[] intToBytes(int value, int width) {
		byte[] bytes = new byte[width];
		for(int i = 0; i < width; i++) {
			bytes[i] = (byte)(value >>> 8*(width-1-i));
		}
		return bytes;
	}
	
	/**
	 * Converts a byte into an int.
	 * 
	 * @param byteValue
	 * The byte to convert.
	 * 
	 * @return
	 * The value of the byte as an int.
	 */
	private int byteToInt(byte byteValue) {
		return (int)byteValue & 0xff;
	}
	
	/**
	 * Gets the adler checksum and assigns it to the
	 * appropriate property.
	 */
	private void calculateAdlerChecksum() {
		adlerChecksum = adlr.getAdler32();
	}
}
