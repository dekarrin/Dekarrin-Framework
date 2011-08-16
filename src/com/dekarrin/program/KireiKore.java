package com.dekarrin.program;

import java.io.FileNotFoundException;

import com.dekarrin.cli.ArgumentDescription;
import com.dekarrin.cli.Command;
import com.dekarrin.cli.CommandDescription;
import com.dekarrin.cli.FlagDescription;
import com.dekarrin.file.png.ColorMode;
import com.dekarrin.file.png.PortableNetworkGraphic;
import com.dekarrin.graphics.Color;
import com.dekarrin.graphics.ColorFormatException;
import com.dekarrin.graphics.Image;
import com.dekarrin.graphics.ImageManipulator;
import com.dekarrin.graphics.ImageResizer;
import com.dekarrin.graphics.NullImageException;
import com.dekarrin.graphics.Pencil;
import com.dekarrin.io.InvalidFormatException;
import com.dekarrin.io.StreamFailureException;
import com.dekarrin.math.Point;
import com.dekarrin.script.ArgumentFormatException;
import com.dekarrin.script.CommandException;
import com.dekarrin.script.ScriptException;
import com.dekarrin.util.HelperString;
import com.dekarrin.script.*;

/**
 * A graphics program for the creation and editing of
 * image files. KireiKore uses a script called 'KKScript', short
 * for KireiKore Script, to process image files. All KKScript files
 * must end with the extension .kks in order to be read by KireiKore.
 */
public class KireiKore extends ConsoleProgram implements ScriptListener {
	
	/**
	 * The extension for KKScript files.
	 */
	public static final String SCRIPT_EXTENSION = "kks";
	
	/**
	 * Reads KireiKore scripts from disk.
	 */
	private ScriptParser kksParser;
	
	/**
	 * The image being operated on.
	 */
	private Image image;
	
	/**
	 * Whether or not to use a color palette in the
	 * saved image.
	 */
	private boolean usePalette = false;
	
	/**
	 * OS execution hook.
	 */
	public static void main(String[] args) {
		new KireiKore(args);
	}
	
	/**
	 * Creates a new KireiKore with its arguments set
	 * up correctly.
	 * 
	 * @param args
	 * The arguments from the command line.
	 */
	public KireiKore(String[] args) {
		super(args);
		addArgument("script", "the script to load; type '?' only for help", false);
		kksParser = setupScriptParser();
		runProgram();
	}
	
	/**
	 * Executes the program.
	 */
	private void runProgram() {
		loadScript();
		runScript();
	}
	
	/**
	 * Loads the script from the command line.
	 */
	private void loadScript() {
		String script = getArgument("script");
		if(script.equalsIgnoreCase("?")) {
			showHelp();
		}
		try {
			kksParser.addScript(script);
		} catch(FileNotFoundException e) {
			giveFatalError(e.getMessage());
		} catch(InvalidFormatException e) {
			giveFatalError(e.getMessage());
		}
	}
	
	/**
	 * Executes the script.
	 */
	private void runScript() {
		kksParser.execute();
	}
	
	/**
	 * Sets up a ScriptParser for reading the KireiKore's
	 * commands.
	 * 
	 * @return
	 * The set up ScriptParser;
	 */
	private ScriptParser setupScriptParser() {
		CommandDescription[] commands = new CommandDescription[12];
		commands[0]		= createNewCommand();
		commands[1]		= createExecCommand();
		commands[2]		= createLoadCommand();
		commands[3]		= createSaveCommand();
		commands[4]		= createResizeCommand();
		commands[5]		= createDesatCommand();
		commands[6]		= createPixelateCommand();
		commands[7]		= createMonoCommand();
		commands[8]		= createReplaceCommand();
		commands[9]		= createSepiaCommand();
		commands[10]	= createLineCommand();
		commands[11]	= createEchoCommand();
		ScriptParser sp = createScriptParser(commands);
		return sp;
	}
	
	/**
	 * Sets up a ScriptParser with the appropriate commands.
	 * 
	 * @param commands
	 * The commands to set it up with.
	 * 
	 * @return
	 * The set up ScriptReader.
	 */
	private ScriptParser createScriptParser(CommandDescription[] command) {
		ScriptParser sp = new ScriptParser(SCRIPT_EXTENSION);
		sp.addListener(this);
		for(CommandDescription desc: command) {
			sp.addCommand(desc);
		}
		return sp;
	}
	
	/**
	 * Creates the CommandDescription for the 'new' command.
	 * 
	 * @return
	 * The 'new' command's description.
	 */
	private CommandDescription createNewCommand() {
		ArgumentDescription[] args = new ArgumentDescription[3];
		args[0] = new ArgumentDescription("width", "width of the image");
		args[1] = new ArgumentDescription("height", "height of the image");
		args[2] = new ArgumentDescription("color", "the background color");
		FlagDescription[] flags = new FlagDescription[3];
		flags[0] = new FlagDescription("g", "use grayscale instead of color");
		flags[1] = new FlagDescription("a", "do not include an alpha channel");
		flags[2] = new FlagDescription("s", "sampledepth", "the sample depth; default is 8", true);
		CommandDescription com = new CommandDescription("new", "creates a new image file", args, flags);
		return com;
	}
	
	/**
	 * Creates the CommandDescription for the 'exec' command.
	 * 
	 * @return
	 * The 'exec' command's description.
	 */
	private CommandDescription createExecCommand() {
		ArgumentDescription[] args = new ArgumentDescription[1];
		args[0] = new ArgumentDescription("location", "the location of the script");
		CommandDescription com = new CommandDescription("exec", "executes a kkscript file", args);
		return com;
	}
	
	/**
	 * Creates the CommandDescription for the 'load' command.
	 * 
	 * @return
	 * The 'load' command's description.
	 */
	private CommandDescription createLoadCommand() {
		ArgumentDescription[] args = new ArgumentDescription[1];
		args[0] = new ArgumentDescription("location", "the location of the image file");
		CommandDescription com = new CommandDescription("load", "loads an image file to the program", args);
		return com;
	}
	
	/**
	 * Creates the CommandDescription for the 'save' command.
	 * 
	 * @return
	 * The 'save' command's description.
	 */
	private CommandDescription createSaveCommand() {
		ArgumentDescription[] args = new ArgumentDescription[1];
		args[0] = new ArgumentDescription("location", "the location to save the image file to");
		CommandDescription com = new CommandDescription("save", "saves the current image file", args);
		return com;
	}
	
	/**
	 * Creates the CommandDescription for the 'resize' command.
	 * 
	 * @return
	 * The 'resize' command's description.
	 */
	private CommandDescription createResizeCommand() {
		ArgumentDescription[] args = new ArgumentDescription[1];
		args[0] = new ArgumentDescription("factor", "the scale factor for the resize");
		CommandDescription com = new CommandDescription("resize", "resizes the image", args);
		return com;
	}
	
	/**
	 * Creates the CommandDescription for the 'desaturate' command.
	 * 
	 * @return
	 * The 'desaturate' command's description.
	 */
	private CommandDescription createDesatCommand() {
		CommandDescription com = new CommandDescription("desaturate", "desaturates the color");
		return com;
	}
	
	/**
	 * Creates the CommandDescription for the 'pixelate' command.
	 * 
	 * @return
	 * The 'pixelate' command's description.
	 */
	private CommandDescription createPixelateCommand() {
		ArgumentDescription[] args = new ArgumentDescription[1];
		args[0] = new ArgumentDescription("factor", "how big to make the pixels");
		CommandDescription com = new CommandDescription("pixelate", "makes the image pixelated", args);
		return com;
	}
	
	/**
	 * Creates the CommandDescription for the 'monochrome' command.
	 * 
	 * @return
	 * The 'monochrome' command's description.
	 */
	private CommandDescription createMonoCommand() {
		ArgumentDescription[] args = new ArgumentDescription[1];
		args[0] = new ArgumentDescription("color", "a color hex code for what color to monochrome to");
		CommandDescription com = new CommandDescription("monochrome", "makes the image have only one hue", args);
		return com;
	}
	
	/**
	 * Creates the CommandDescription for the 'replace' command.
	 * 
	 * @return
	 * The 'replace' command's description.
	 */
	private CommandDescription createReplaceCommand() {
		ArgumentDescription[] args = new ArgumentDescription[2];
		args[0] = new ArgumentDescription("oldColor", "a color hex code for what color to replace");
		args[1] = new ArgumentDescription("newColor", "a color hex code for what color to use as the replacement");
		FlagDescription[] flags = new FlagDescription[1];
		flags[0] = new FlagDescription("t", "tolerance", "how much variance between oldColor and colors actually replaced. Range is [0, 1]", true);
		CommandDescription com = new CommandDescription("replace", "replaces the color", args, flags);
		return com;
	}
	
	/**
	 * Creates the CommandDescription for the 'sepia' command.
	 * 
	 * @return
	 * The 'sepia' command's description.
	 */
	private CommandDescription createSepiaCommand() {
		CommandDescription com = new CommandDescription("sepia", "makes the image sepia toned");
		return com;
	}
	
	/**
	 * Creates the CommandDescription for the 'line' command.
	 * 
	 * @return
	 * The 'line' command's description.
	 */
	private CommandDescription createLineCommand() {
		ArgumentDescription[] args = new ArgumentDescription[4];
		args[0] = new ArgumentDescription("x1", "The x-coord of the first point");
		args[1] = new ArgumentDescription("y1", "The y-coord of the first point");
		args[2] = new ArgumentDescription("x2", "The x-coord of the second point");
		args[3] = new ArgumentDescription("y2", "The y-coord of the second point");
		FlagDescription[] flags = new FlagDescription[2];
		flags[0] = new FlagDescription("c", "color", "the color of the line", true);
		flags[1] = new FlagDescription("a", "antialias", "do not use antialias", true);
		CommandDescription com = new CommandDescription("line", "creates a line between two points", args, flags);
		return com;
	}
	
	/**
	 * Creates the CommandDescription for the 'echo' command.
	 * 
	 * @return
	 * The 'echo' command's description.
	 */
	private CommandDescription createEchoCommand() {
		ArgumentDescription[] args = new ArgumentDescription[1];
		args[0] = new ArgumentDescription("message", "The message to show");
		CommandDescription com = new CommandDescription("echo", "displays a message", args);
		return com;
	}
	
	/**
	 * Executes a command from a script.
	 * 
	 * @param command
	 * The command being executed.
	 * @throws ScriptException 
	 */
	public void executeScriptCommand(Command command) throws CommandException, ScriptException {
		String name = command.getName();
		try {
			if(name.equals("new")) {
				commandCreateNewImage(command);
			} else if(name.equals("exec")) {
				commandExecuteScript(command);
			} else if(name.equals("load")) {
				commandLoadImage(command);
			} else if(name.equals("save")) {
				commandSaveImage(command);
			} else if(name.equals("resize")) {
				commandResizeImage(command);
			} else if(name.equals("desaturate")) {
				commandDesaturateImage(command);
			} else if(name.equals("pixelate")) {
				commandPixelateImage(command);
			} else if(name.equals("monochrome")) {
				commandMakeMonochrome(command);
			} else if(name.equals("replace")) {
				commandReplaceColor(command);
			} else if(name.equals("sepia")) {
				commandMakeSepia(command);
			} else if(name.equals("line")) {
				commandDrawLine(command);
			} else if(name.equals("echo")) {
				commandEchoMessage(command);
			}
		} catch(InvalidFormatException e) {
			ui.printError(e.getMessage());
		} catch(NullImageException e) {
			throw new ScriptException(e.getMessage());
		}
	}
	
	/**
	 * Parses the command arguments and uses them to create a new
	 * image.
	 * 
	 * @param command
	 * The command that created the image.
	 * 
	 * @throws ArgumentFormatException
	 */
	private void commandCreateNewImage(Command command) throws ArgumentFormatException {
		int width=0,height=0,depth=0;
		Color backgroundColor;
		try {
			width = Integer.parseInt(command.getArgument("width"));
		} catch(NumberFormatException e) {
			throw new IntegerArgumentException("width", command.getArgument("width"));
		}
		try {
			height = Integer.parseInt(command.getArgument("height"));
		} catch(NumberFormatException e) {
			throw new IntegerArgumentException("height", command.getArgument("height"));
		}
		try {
			depth = command.hasFlag("s") ? Integer.parseInt(command.getFlag("s")) : 8;
		} catch(NumberFormatException e) {
			throw new IntegerArgumentException("depth", command.getFlag("s"));
		}
		try {
			backgroundColor = Color.parseColor(command.getArgument("color"), depth);
		} catch(ColorFormatException e) {
			throw new ArgumentFormatException("color", command.getArgument("color"), e.getMessage());
		}
		boolean hasColor = !(command.hasFlag("g"));
		boolean hasAlpha = !(command.hasFlag("a"));
		image = new Image(width, height, depth, hasAlpha, hasColor);
		for(int y = 0; y < image.height; y++) {
			for(int x = 0; x < image.width; x++) {
				image.setColorAt(x, y, backgroundColor);
			}
		}
	}
	
	/**
	 * Parses the command arguments and uses them to execute a script.
	 * 
	 * @param command
	 * The command that executed the script.
	 */
	private void commandExecuteScript(Command command) {
		String location = command.getArgument("location");
		ScriptParser parser = setupScriptParser();
		try {
			parser.addScript(location);
		} catch (Exception e) {
			ui.printError(e.getMessage());
			ui.println("Skipping script '"+location+"'");
		}
		parser.execute();
	}
	
	/**
	 * Parses the command arguments and uses them to load an image.
	 * 
	 * @param command
	 * The command that loaded the image.
	 */
	private void commandLoadImage(Command command) throws InvalidFormatException {
		String location = command.getArgument("location");
		String ext = location.substring(location.lastIndexOf(".")+1, location.length());
		if(ext.equalsIgnoreCase("png")) {
			try {
				PortableNetworkGraphic png = new PortableNetworkGraphic(location);
				image = png.getImage();
			} catch (FileNotFoundException e) {
				ui.printError(e.getMessage());
				ui.println("Image '"+location+"' not loaded");
			} catch (StreamFailureException e) {
				ui.printError(e.getMessage());
				ui.println("Image '"+location+"' not loaded");
			}
		} else {
			throw new InvalidFormatException("Cannot read file format", ext);
		}
	}
	
	/**
	 * Parses the command arguments and uses them to save an image.
	 * 
	 * @param command
	 * The command that saved the image.
	 * 
	 * @throws InvalidFormatException
	 * If an unrecognized format is used to save.
	 * 
	 * @throws NullImageException
	 * If the image has not yet been created.
	 */
	private void commandSaveImage(Command command) throws InvalidFormatException, NullImageException {
		if(image == null) {
			throw new NullImageException();
		}
		String location = command.getArgument("location");
		String ext = location.substring(location.lastIndexOf(".")+1, location.length());
		if(ext.equalsIgnoreCase("png")) {
			try {
				ColorMode mode = selectPngColorMode();
				PortableNetworkGraphic png = new PortableNetworkGraphic(image, mode);
				png.save(location);
			} catch (StreamFailureException e) {
				ui.printError(e.getMessage());
				ui.println("Image not saved to '"+location+"'");
			}
		} else {
			throw new InvalidFormatException("Cannot write file format", ext);
		}
	}
	
	/**
	 * Parses the command arguments and uses them to resize an
	 * image.
	 * 
	 * @param command
	 * The command that resized the image.
	 * 
	 * @throws NullImageException
	 * If the image has not yet been created.
	 * 
	 * @throws ArgumentFormatException
	 * If an argument is the wrong format.
	 */
	private void commandResizeImage(Command command) throws NullImageException, ArgumentFormatException {
		if(image == null) {
			throw new NullImageException();
		}
		double factor = 0.0;
		try {
			factor = Double.parseDouble(command.getArgument("factor"));
		} catch(NumberFormatException e) {
			throw new RealArgumentException("factor", command.getArgument("factor"));
		}
		ImageResizer scaler = new ImageResizer(image);
		scaler.scale(factor);
	}
	
	/**
	 * Parses the command arguments and uses them to desaturate an
	 * image.
	 * 
	 * @param command
	 * The command that desaturated the image.
	 * 
	 * @throws NullImageException
	 * If the image has not yet been created.
	 */
	private void commandDesaturateImage(Command command) throws NullImageException {
		if(image == null) {
			throw new NullImageException();
		}
		ImageManipulator im = new ImageManipulator(image);
		im.desaturate();
	}
	
	/**
	 * Parses the command arguments and uses them to pixelate an
	 * image.
	 * 
	 * @param command
	 * The command that pixelated the image.
	 * 
	 * @throws NullImageException
	 * If the image has not yet been created.
	 * 
	 * @throws ArgumentFormatException
	 * If an argument is the wrong format.
	 */
	private void commandPixelateImage(Command command) throws NullImageException, ArgumentFormatException {
		if(image == null) {
			throw new NullImageException();
		}
		int factor = 0;
		try {
			factor = Integer.parseInt(command.getArgument("factor"));
		} catch (NumberFormatException e) {
			throw new IntegerArgumentException("factor", command.getArgument("factor"));
		}
		ImageManipulator im = new ImageManipulator(image);
		im.pixelate(factor);
	}
	
	/**
	 * Parses the command arguments and uses them to make the image
	 * monochrome.
	 * 
	 * @param command
	 * The command that made the image monochrome.
	 * 
	 * @throws NullImageException
	 * If the image has not yet been created.
	 * 
	 * @throws ArgumentFormatException
	 * If an argument is the wrong format.
	 */
	private void commandMakeMonochrome(Command command) throws NullImageException, ArgumentFormatException {
		if(image == null) {
			throw new NullImageException();
		}
		Color color = null;
		try {
			color = Color.parseColor(command.getArgument("color"), image.sampleDepth);
		} catch(ColorFormatException e) {
			throw new ArgumentFormatException("color", command.getArgument("color"), e.getMessage());
		}
		ImageManipulator im = new ImageManipulator(image);
		im.monochrome(color);
	}
	
	/**
	 * Parses the command arguments and uses them to replace a color
	 * in the image.
	 * 
	 * @param command
	 * The command that replaced the color.
	 * 
	 * @throws NullImageException
	 * If the image has not yet been created.
	 * 
	 * @throws ArgumentFormatException
	 * If an argument is the wrong format.
	 */
	private void commandReplaceColor(Command command) throws NullImageException, ArgumentFormatException {
		if(image == null) {
			throw new NullImageException();
		}
		Color oldColor=null,newColor=null;
		double tolerance = 0.0;
		try {
			oldColor = Color.parseColor(command.getArgument("oldColor"), image.sampleDepth);
		} catch(ColorFormatException e) {
			throw new ArgumentFormatException("oldColor", command.getArgument("oldColor"), e.getMessage());
		}
		try {
			newColor = Color.parseColor(command.getArgument("newColor"), image.sampleDepth);
		} catch(ColorFormatException e) {
			throw new ArgumentFormatException("newColor", command.getArgument("newColor"), e.getMessage());
		}
		try {
			tolerance = (command.hasFlag("t")) ? Double.parseDouble(command.getFlag("t")) : 1.0;
		} catch(NumberFormatException e) {
			throw new RealArgumentException("tolerance", command.getFlag("t"));
		}
		ImageManipulator im = new ImageManipulator(image);
		im.replaceColor(oldColor, newColor, tolerance);
	}
	
	/**
	 * Parses the command arguments and uses them to make the image
	 * sepia-toned.
	 * 
	 * @param command
	 * The command that made the image sepia-toned.
	 * 
	 * @throws NullImageException
	 * If the image has not yet been created.
	 */
	private void commandMakeSepia(Command command) throws NullImageException {
		if(image == null) {
			throw new NullImageException();
		}
		ImageManipulator im = new ImageManipulator(image);
		im.sepia();
	}
	
	/**
	 * Parses the command arguments and uses them to draw a line on
	 * the image.
	 * 
	 * @param command
	 * The command that drew the line.
	 * 
	 * @throws NullImageException
	 * If the image has not yet been created.
	 * 
	 * @throws ArgumentFormatException 
	 * If an argument is in the wrong format.
	 */
	private void commandDrawLine(Command command) throws NullImageException, ArgumentFormatException {
		if(image == null) {
			throw new NullImageException();
		}
		int x1=0,y1=0,x2=0,y2=0;
		try {
			x1 = Integer.parseInt(command.getArgument("x1"));
		} catch(NumberFormatException e) {
			throw new IntegerArgumentException("x1", command.getArgument("x1"));
		}
		try {
			y1 = Integer.parseInt(command.getArgument("y1"));
		} catch(NumberFormatException e) {
			throw new IntegerArgumentException("y1", command.getArgument("y1"));
		}
		try {
			x2 = Integer.parseInt(command.getArgument("x2"));
		} catch(NumberFormatException e) {
			throw new IntegerArgumentException("x2", command.getArgument("x2"));
		}
		try {
			y2 = Integer.parseInt(command.getArgument("y2"));
		} catch(NumberFormatException e) {
			throw new IntegerArgumentException("y2", command.getArgument("y2"));
		}
		Point p1 = new Point(2, x1, y1);
		Point p2 = new Point(2, x2, y2);
		Pencil pen = new Pencil(image);
		pen.setAntiAlias(!command.hasFlag("a"));
		if(command.hasFlag("c")) {
			try {
				pen.setColor(Color.parseColor(command.getFlag("c"), image.sampleDepth));
			} catch(ColorFormatException e) {
				throw new ArgumentFormatException("color", command.getFlag("c"), e.getMessage());
			}
		}
		pen.moveTo(p1);
		pen.down();
		pen.moveTo(p2);
	}
	
	/**
	 * Parses the command arguments and uses them to echo a message.
	 * 
	 * @param command
	 * The command that echoed the message.
	 */
	private void commandEchoMessage(Command command) {
		ui.println(command.getArgument("message"));
	}
	
	/**
	 * Gives the help for KireiKore. This should be moved to a man
	 * page instead.
	 */
	private void showHelp() {
		String title = new HelperString("KKScript Manual").padBoth(100).toString() + "\n";
		title += new HelperString("---------------").padBoth(100).toString() + "\n";
		String help = "";
		help += "KKScript is a script language used for processing image files. ";
		help += "KKScript files are read by the KireiKore application and commands are ";
		help += "processed in the order that they appear.\n";
		help += "\n";
		help += "KKSCRIPT FILES\n";
		help += "All KKScript files must end with the extension '.kks'. There are no other ";
		help += "format restrictions. Every KKScript file must first either load a file with ";
		help += "the 'load' command, or create a new file with the 'new' command. Every script ";
		help += "should also end with a 'save' command in order to write the edited image to ";
		help += "disk; however, the lack of a 'save' command will not prevent the script from ";
		help += "running. Other KKScript files can be called from a KKScript file by using the ";
		help += "'exec' command. For more information on any of these commands, see the following ";
		help += "sections.\n";
		help += "\n";
		help += "KKSCRIPT COMMAND SYNTAX\n";
		help = new HelperString(help).wrap(100).toString();
		String syntax = kksParser.getHelp();
		ui.println(title);
		ui.println(help);
		ui.print(syntax);
	}
	
	/**
	 * Selects the color type based on the image mode.
	 * The color mode will be usable for PNG files only.
	 * 
	 * @return
	 * The color mode.
	 */
	private ColorMode selectPngColorMode() {
		ColorMode mode = null;
		if(usePalette) {
			mode = ColorMode.INDEXED;
		} else {
			if(image.hasChannel(Image.RED) && image.hasChannel(Image.GREEN) && image.hasChannel(Image.BLUE)) {
				if(image.hasChannel(Image.ALPHA)) {
					mode = ColorMode.TRUECOLOR_ALPHA;
				} else {
					mode = ColorMode.TRUECOLOR;
				}
			} else {
				if(image.hasChannel(Image.GRAY_ALPHA)) {
					mode = ColorMode.GRAYSCALE_ALPHA;
				} else {
					mode = ColorMode.GRAYSCALE;
				}
			}
		}
		return mode;
	}
}
