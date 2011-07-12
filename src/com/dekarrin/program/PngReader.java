package com.dekarrin.program;

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
		String output = getArgument(1);
		
		PortableNetworkGraphic pic = null;
		try {
			pic = new PortableNetworkGraphic(file);
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
		for(int y = 0; y < image.width; y++) {
			for(int x = 0; x < image.height; x++) {
				color = image.colorAt(x, y);
				colorString = String.format("[%d,%d,%d,%d]", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
				p(colorString);
			}
			pl("");
		}
		
		pl("-----------");
		pl("reading test complete.");
		pl("Writing...");
		try {
			pic.save(output);
		} catch(StreamFailureException e) {
			giveFatalError(e.getMessage());
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
		addArgument("output", false);
	}
}