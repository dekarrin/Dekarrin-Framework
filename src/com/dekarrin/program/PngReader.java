package com.dekarrin.program;

import com.dekarrin.file.png.*;
import com.dekarrin.graphics.*;
import com.dekarrin.io.*;
import com.dekarrin.util.HelperString;
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
		} catch(InvalidFormatException e) {
			giveFatalError(e.getMessage());
		} catch(FileNotFoundException e) {
			giveFatalError(e.getMessage());
		} catch(StreamFailureException e) {
			giveFatalError(e.getMessage());
		}
		
		Image image = pic.getImage();
		Color color;
		String colorString;
		@SuppressWarnings("unused")
		String r,b,g,a;
		for(int y = 0; y < image.width; y++) {
			for(int x = 0; x < image.height; x++) {
				color = image.getColorAt(x, y);
				r = (new HelperString(Integer.toString(color.getRed(), 16))).padLeft(2, '0').toString();
				g = (new HelperString(Integer.toString(color.getGreen(), 16))).padLeft(2, '0').toString();
				b = (new HelperString(Integer.toString(color.getBlue(), 16))).padLeft(2, '0').toString();
			//	a = (new HelperString(Integer.toString(color.getAlpha(), 16))).padLeft(2, '0').toString();
				colorString = String.format("(#%s%s%s)", r, g, b);
				p(colorString);
			}
			pl("");
		}
		
		if(hasArgument(1)) {
			pl("-----------");
			pl("reading test complete.");
			pl("Writing...");
			try {
				pic.save(output);
			} catch(StreamFailureException e) {
				giveFatalError(e.getMessage());
			}
		}
		/*Image picture = new Image(100, 100, 8, false);
		java.util.Random rgen = new java.util.Random();
		rgen.setSeed(328924879814357L);
		Color c;
		for(int x = 0; x < picture.width; x++) {
			for(int y = 0; y < picture.height; y++) {
				c = new Color(8);
				c.setSamples(rgen.nextInt(256), rgen.nextInt(256), rgen.nextInt(256));
				picture.setColorAt(x, y, c);
			}
		}*/
		//PortableNetworkGraphic png = new PortableNetworkGraphic(picture, PortableNetworkGraphic.COLOR_TYPE_COLOR);
	//	try {
		//	pic.save(file);
		//} catch (StreamFailureException e) {
			//giveFatalError(e.getMessage());
	//	}
	}
	
	private void pl(String message) {
		System.out.println(message);
	}
	
	private void p(String message) {
		System.out.print(message);
	}
	
	private void addArgs() {
		addArgument("filename", false);
		addArgument("output", true);
	}
}