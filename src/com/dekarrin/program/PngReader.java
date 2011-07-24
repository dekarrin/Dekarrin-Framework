package com.dekarrin.program;


import com.dekarrin.file.png.*;
import com.dekarrin.graphics.*;
import com.dekarrin.io.*;

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
		String command = getArgument(2);
		PortableNetworkGraphic pic = null;
		ui.println("Loading image...");
		try {
			pic = new PortableNetworkGraphic(file);
		} catch(Exception e) {
			giveFatalError(e.getMessage());
		}
		Image image = pic.getImage();
		ImageManipulator im = new ImageManipulator(image);
		ImageResizer is = new ImageResizer(image);
		if(command.equalsIgnoreCase("desat")) {
			ui.println("Desaturating...");
			im.desaturate();
		} else if(command.equalsIgnoreCase("pixel")) {
			addArgument("scaleFactor", true);
			ui.println("Pixelating...");
			int scale = getArgumentAsInt(3, 2);
			im.pixelate(scale);
		} else if(command.equalsIgnoreCase("mono")) {
			addArgument("color", false);
			String colorString = getArgument(3);
			System.out.println(colorString);
			Color hueColor = Color.parseColor(colorString, image.bitDepth);
			im.monochrome(hueColor);
		} else if(command.equalsIgnoreCase("replace")) {
			addArgument("oldColor", false);
			addArgument("newColor", false);
			addArgument("tolerance", true);
			String oldColorString = getArgument(3);
			String newColorString = getArgument(4);
			double tolerance = getArgumentAsDouble(5, 0.0);
			Color oldColor = Color.parseColor(oldColorString, image.bitDepth);
			Color newColor = Color.parseColor(newColorString, image.bitDepth);
			im.replaceColor(oldColor, newColor, tolerance);
		} else if(command.equalsIgnoreCase("sepia")) {
			im.sepia();
		} else if(command.equalsIgnoreCase("scale")) {
			is.scale(2);
		} else {
			giveFatalError("Cannot apply unknown command '"+command+"'");
		}
		ui.println("Saving image...");
		try {
			pic.save(output);
		} catch(StreamFailureException e) {
			giveFatalError(e.getMessage());
		}
	}
	
	private void addArgs() {
		addArgument("filename", false);
		addArgument("output", false);
		addArgument("command", false);
	}
}