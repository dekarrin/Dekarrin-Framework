package com.dekarrin.program;

import com.dekarrin.parse.PngParser;
import com.dekarrin.file.png.*;
import com.dekarrin.graphics.*;
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
		PortableNetworkGraphic pic = null;
		try {
			parser = new PngParser(file);
			pic = parser.parse();
		} catch(InvalidFileFormatException e) {
			giveFatalError(e.getMessage());
		} catch(FileNotFoundException e) {
			giveFatalError(e.getMessage());
		} catch(StreamFailureException e) {
			giveFatalError(e.getMessage());
		}
		
		Image image = pic.getImage();
		Color color;
		String colorString;
		for(int x = 0; x < image.width; x++) {
			for(int y = 0; y < image.height; y++) {
				color = image.colorAt(x, y);
				colorString = String.format("[%i,%i,%i,%i]", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
				p(colorString);
			}
			pl("");
		}
	}
	
	private void pl(String message) {
		System.out.println(message);
	}
	
	private void p(String message) {
		System.out.print(message);
	}
	
	private void addArgs() {
		addArgument("filename", false);
	}
}