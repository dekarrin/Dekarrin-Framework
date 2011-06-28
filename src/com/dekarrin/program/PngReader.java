package com.dekarrin.program;

import com.dekarrin.parse.PngParser;
import com.dekarrin.file.png.Chunk;
import com.dekarrin.io.*;
import java.io.*;

/**
 * Reads information on Png files.
 */
public class PngReader extends ConsoleProgram {
	
	public static void main(String[] args) {
		new PngReader(args);
	}
	
	public PngReader(String[] args) {
		super(args);
		addArgs();
		String file = getArgument(0);
		
		PngParser parser = null;
		try {
			parser = new PngParser(file);
			parser.parse();
		} catch(InvalidFileFormatException e) {
			giveFatalError(e.getMessage());
		} catch(FileNotFoundException e) {
			giveFatalError(e.getMessage());
		} catch(StreamFailureException e) {
			giveFatalError(e.getMessage());
		}
		
		Chunk[] chunks = parser.getChunks();
		for(Chunk pc: chunks) {
			String n = pc.getTypeName();
			byte[] t = pc.getType();
			byte[] d = pc.getData();
			int l = pc.getLength();
			long c = pc.getCrc();
			
			pl(String.format("Type: %s (%d,%d,%d,%d)", n, t[0], t[1], t[2], t[3]));
			pl("----------------------");
			pl("Data:");
			
			boolean flushed = true;
			String line = "";
			for(int i = 0; i < d.length; i++) {
				flushed = false;
				line += " " + String.format("%03d", (int)((int)d[i] & 0xff));
				if(i % 10 == 0 && i > 0) {
					pl(line);
					flushed = true;
					line = "";
				}
			}
			if(!flushed) {
				pl(line);
			}
			
			pl("----------------------");
			pl("("+l+" bytes)");
			pl("");
			pl("CRC: "+c);
			pl("==================================================");
			pl("==================================================");
		}
	}
	
	private void pl(String message) {
		System.out.println(message);
	}
	
	private void addArgs() {
		addArgument("filename", false);
	}
}