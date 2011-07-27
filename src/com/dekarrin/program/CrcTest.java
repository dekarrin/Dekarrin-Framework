package com.dekarrin.program;

import java.util.zip.CRC32;

/**
 * Tests the crc-32 capabilities of java.
 */
public class CrcTest extends ConsoleProgram {
	
	public static void main(String[] args) {
		new CrcTest(args);
	}
	
	public CrcTest(String[] args) {
		super(args);
		addArgument("hex data", false);
		String hex = getArgument("hex data");
		if(hex.length() % 2 != 0) {
			giveFatalError("data must be in hex pairs!");
		}
		byte[] input = new byte[hex.length() / 2];
		String h;
		int j = 0;
		for(int i = 0; i < hex.length(); i += 2) {
			h = hex.substring(i, i+2);
			input[j++] = (byte)Integer.parseInt(h, 16);
		}
		
		CRC32 crc = new CRC32();
		crc.update(input);
		ui.println(""+crc.getValue());
	}
}