package com.dekarrin.program;


import java.util.HashMap;

import com.dekarrin.file.png.*;
import com.dekarrin.graphics.*;
import com.dekarrin.io.*;
import com.dekarrin.math.Point;

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
		
		String file = getArgument("filename");
		String output = getArgument("output");
		String command = getArgument("command");
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
		HashMap<String,Tool> tools = createTools(image);
		if(command.equalsIgnoreCase("desat")) {
			ui.println("Desaturating...");
			im.desaturate();
		} else if(command.equalsIgnoreCase("pixel")) {
			addArgument("scaleFactor", true);
			ui.println("Pixelating...");
			int scale = getArgumentAsInt("scaleFactor", 2);
			im.pixelate(scale);
		} else if(command.equalsIgnoreCase("mono")) {
			addArgument("color", false);
			String colorString = getArgument("color");
			System.out.println(colorString);
			Color hueColor = Color.parseColor(colorString, image.bitDepth);
			ui.println("Mono toning...");
			im.monochrome(hueColor);
		} else if(command.equalsIgnoreCase("replace")) {
			addArgument("oldColor", false);
			addArgument("newColor", false);
			addArgument("tolerance", true);
			String oldColorString = getArgument("oldColor");
			String newColorString = getArgument("newColor");
			double tolerance = getArgumentAsDouble("tolerance", 0.0);
			Color oldColor = Color.parseColor(oldColorString, image.bitDepth);
			Color newColor = Color.parseColor(newColorString, image.bitDepth);
			ui.println("Scaling...");
			im.replaceColor(oldColor, newColor, tolerance);
		} else if(command.equalsIgnoreCase("sepia")) {
			ui.println("Sepia toning...");
			im.sepia();
		} else if(command.equalsIgnoreCase("scale")) {
			ui.println("Scaling...");
			is.scale(2);
		} else if(command.equalsIgnoreCase("line")) {
			addArgument("x1", false);
			addArgument("y1", false);
			addArgument("x2", false);
			addArgument("y2", false);
			addArgument("color", true);
			int x1 = getArgumentAsInt("x1", 1);
			int y1 = getArgumentAsInt("y1", 1);
			int x2 = getArgumentAsInt("x2", 1);
			int y2 = getArgumentAsInt("y2", 1);
			Point p1 = new Point(2, x1, y1);
			Point p2 = new Point(2, x2, y2);
			String colorString = getArgument("color");
			Color lineColor;
			Pencil p = (Pencil)tools.get("pencil");
			if(colorString != null) {
				lineColor = Color.parseColor(colorString, image.bitDepth);
				p.setColor(lineColor);
			}
			ui.println("Creating line...");
			p.moveTo(p1);
			p.down();
			p.moveTo(p2);
		} else if(command.equalsIgnoreCase("copy")) {
			ui.println("copying...");
			
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
	
	private HashMap<String, Tool> createTools(Image image) {
		HashMap<String,Tool> t = new HashMap<String,Tool>();
		t.put("pencil", new Pencil(image));
		return t;
	}
}