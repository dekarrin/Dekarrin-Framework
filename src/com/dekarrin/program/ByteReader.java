package com.dekarrin.program;

import java.io.*;
import java.util.Vector;

/**
 * Reads a file and displays the bytes to the console. This has several modes.
 * It can output the data in hex mode, bit mode, or decimal mode.
 */
public class ByteReader {

	/**
	 * Whether or not the bits should be shown.
	 */
	private boolean bitMode = false;

	private boolean hexOutput = false;

	private boolean noFormatting = false;
	
	private File file;

	private File outputFile;

	private boolean[] fileBits;

	private byte[] fileBytes;

	private int bitPointer = 0;

	private static ByteReader program;

	private static final int BINARY_DIGITS_PER_LINE = 8;

	private static final int BITS_PER_GROUP = 8;

	private static final int DECIMAL_DIGITS_PER_LINE = 8;

	private static final int BYTE_WIDTH = 8;

	public static void main(String[] args) {
		Vector<String> flags = getFlags(args);
		args = removeFlags(args);
		String file = getFileName(args);
		String outputFile = getOutputFileName(args);
		boolean bitMode = getBitMode(flags);
		boolean hexOutput = getHexOutput(flags);
		boolean noFormatting = getNoFormatting(flags);
		program = new ByteReader(file, outputFile, bitMode, hexOutput, noFormatting);
	}

	private static String getOutputFileName(String[] args) {
		String fileName = null;
		if(args.length > 1) {
			fileName = args[1];
		}
		return fileName;
	}

	private static String[] removeFlags(String[] args) {
		Vector<String> argsVector = new Vector<String>();
		for(String s: args) {
			if(!s.substring(0,1).equals("-")) {
				argsVector.add(s);
			}
		}
		String[] argArray = argsVector.toArray(new String[0]);
		return argArray;
	}

	private static Vector<String> getFlags(String[] args) {
		Vector<String> flags = new Vector<String>();
		for(String s: args) {
			if(s.substring(0,1).equals("-")) {
				char[] flagChars = s.substring(1).toCharArray();
				for(char c: flagChars) {
					String flagCharString = Character.toString(c);
					if(!flags.contains(flagCharString)) {
						flags.add(flagCharString);
					}
				}
			}
		}
		return flags;
	}

	private static String getFileName(String[] args) {
		requireArgumentCount(args, 1);
		String file = args[0];
		return file;
	}

	private static void requireArgumentCount(String[] args, int required) {
		if(args.length < required) {
			printHelp();
			System.exit(1);
		}
	}
	
	private static void printHelp() {
		System.out.println("syntax: ByteReader [-hfb] file [outputfile]");
		System.out.println("OPTIONS: h - hex output, f - no formatting, b - bit output");
	}

	private static boolean getBitMode(Vector<String> flags) {
		boolean bitMode = false;
		if(flags.contains("b") || flags.contains("B")) {
			bitMode = true;
		}
		return bitMode;
	}

	private static boolean getHexOutput(Vector<String> flags) {
		boolean hexOutput = false;
		if(flags.contains("h") || flags.contains("H")) {
			hexOutput = true;
		}
		return hexOutput;
	}

	private static boolean getNoFormatting(Vector<String> flags) {
		boolean noFormatting = false;
		if(flags.contains("f") || flags.contains("F")) {
			noFormatting = true;
		}
		return noFormatting;
	}

	public ByteReader(String fileName, String outputFileName, boolean bitMode, boolean hexOutput, boolean noFormatting) {
		this.file = new File(fileName);
		if(outputFileName != null) {
			this.outputFile = new File(outputFileName);
		}
		this.bitMode = bitMode;
		this.hexOutput = hexOutput;
		this.noFormatting = noFormatting;
		initArrays();
		readFile();
		
		try {
			outputData();
			System.out.println();
		} catch(IOException e) {
			System.err.println(e.getMessage());	
		}
	}

	private void outputData() throws IOException {
		BufferedWriter out;
		if(outputtingToFile()) {
			out = new BufferedWriter(new FileWriter(outputFile, false));
		} else {
			out = new BufferedWriter(new OutputStreamWriter(System.out));
		}
		writeData(out);
		out.flush();
		if(outputtingToFile()) {
			System.out.println("done");
		}
	}

	private void initArrays() {
		fileBytes = new byte[(int)file.length()];
		if(this.useBitMode()) {
			fileBits = new boolean[(int)(file.length() * BYTE_WIDTH)];
		}
	}

	private void readFile() {
		FileInputStream inStream = null;
		try {
			inStream = new FileInputStream(file);
			inStream.read(fileBytes);
		} catch(FileNotFoundException e) {
			System.err.println("File does not exist.");
		} catch(IOException e) {
			System.err.println(e.getMessage());
		} finally {
			if(inStream != null) {
				try {
					inStream.close();
				} catch(IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

	private void writeData(BufferedWriter out) throws IOException {
		if(this.useBitMode()) {
			convertBytes();
			writeBits(out);
		} else {
			writeBytes(out);
		}
	}

	private boolean useBitMode() {
		return bitMode;
	}

	private void convertBytes() {
		for(byte b: fileBytes) {
			extractBits(b);
		}
	}

	private void extractBits(byte source) {
		for(int i = 128; i > 0; i /= 2) {
			if(((int)source & i) == i) {
				fileBits[bitPointer++] = true;
			} else {
				fileBits[bitPointer++] = false;
			}
		}
	}

	private void writeBytes(BufferedWriter out) throws IOException {
		for(int i = 0; i < fileBytes.length; i++) {
			byte fileByte = fileBytes[i];
			String byteString;
			if(hexOutput) {
				byteString = Integer.toString(convertByteToInt(fileByte), 16);
				if(byteString.length() < 2) {
					byteString = "0"+byteString;	
				}
			} else {
				byteString = String.format("%03d", convertByteToInt(fileByte));
			}
			out.write(byteString);
			if(!noFormatting) {
				out.write(" ");
				if(i % DECIMAL_DIGITS_PER_LINE == DECIMAL_DIGITS_PER_LINE - 1) {
					out.newLine();
				}
			}
		}
	}
	
	private int convertByteToInt(byte source) {
		boolean[] bitmask = new boolean[8];
		int j = 0;
		for(int i = 128; i > 0; i /= 2) {
			if((source & i) == i) {
				bitmask[j++] = true;
			} else {
				bitmask[j++] = false;
			}
		}
		int output = 0;
		j = 0;
		for(int i = 128; i > 0; i /= 2) {
			if(bitmask[j++]) {
				output += i;
			}
		}
		return output;
	}

	private void writeBits(BufferedWriter out) throws IOException {
		for(int i = 0; i < fileBits.length; i++) {
			if(fileBits[i]) {
				out.write("1");
			} else {
				out.write("0");
			}
			if(!noFormatting) {
				if(i % BITS_PER_GROUP == BITS_PER_GROUP - 1) {
					out.write(" ");
				}
				if(i % (BINARY_DIGITS_PER_LINE * BITS_PER_GROUP) == (BINARY_DIGITS_PER_LINE * BITS_PER_GROUP) - 1) {
					out.newLine();
				}
			}
		}
	}

	private boolean outputtingToFile() {
		boolean outputting = false;
		if(outputFile != null) {
			outputting = true;
		}
		return outputting;
	}
}