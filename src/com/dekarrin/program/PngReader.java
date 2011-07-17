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
		} catch(Exception e) {
			giveFatalError(e.getMessage());
		}
		ColorProfile profile = pic.getProfile();
		Image image = pic.getImage();
		/*Color c;
		for(int y = 0; y < image.height; y++) {
			for(int x = 0; x < image.width; x++) {
				c = image.getColorAt(x, y);
				String r = (new HelperString(Integer.toString(c.getRed(), 16))).padLeft(2, '0').toString();
				String g = (new HelperString(Integer.toString(c.getGreen(), 16))).padLeft(2, '0').toString();
				String b = (new HelperString(Integer.toString(c.getBlue(), 16))).padLeft(2, '0').toString();
		//		String a = (new HelperString(Integer.toString(c.getAlpha(), 16))).padLeft(2, '0').toString();
				String pixel = String.format("#%s%s%s ", r, g, b);
				p(pixel);
			}
			pl("");
		}*/
		PortableNetworkGraphic pic2 = new PortableNetworkGraphic(image, pic.getColorMode());
		pic2.setProfile(profile);
		try {
			pic2.save(output);
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