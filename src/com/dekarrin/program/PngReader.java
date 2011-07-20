package com.dekarrin.program;

import com.dekarrin.file.png.*;
import com.dekarrin.graphics.*;
import com.dekarrin.io.*;
import com.dekarrin.util.HelperString;

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
		if(command.equalsIgnoreCase("desaturate")) {
			ui.println("Desaturating...");
			im.desaturate();
		} else if(command.equalsIgnoreCase("pixelate")) {
			ui.println("Pixelating...");
			int scale = getArgumentAsInt(3, 2);
			im.pixelate(scale);
		} else {
			ui.println("Cannot apply unknown command '"+command+"'");
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
		addArgument("param1", true);
	}
}