package com.dekarrin.script;

import java.util.*;
import java.io.*;

import com.dekarrin.cli.Command;
import com.dekarrin.cli.CommandDescription;
import com.dekarrin.error.CommandArgumentException;
import com.dekarrin.error.UnknownCommandException;
import com.dekarrin.io.InvalidFormatException;

/**
 * Reads a program script from disk and executes each item.
 * 
 * The format of the scripts that ScriptParser can execute
 * is a simple one-item-per-line format, with parameters
 * separated by spaces. This may be extended in the future.
 */
public class ScriptParser {
	
	/**
	 * The objects to execute the methods on.
	 */
	private ArrayList<ScriptListener> targets;
	
	/**
	 * The scripts to read from.
	 */
	private ArrayList<File> scripts;
	
	/**
	 * The command definitions.
	 */
	private HashMap<String,CommandDescription> commands;
	
	/**
	 * The reader for the script currently being executed.
	 */
	private BufferedReader scriptReader;
	
	/**
	 * The required extension for files executed by this
	 * ScriptParser.
	 */
	private String scriptExtension;
	
	/**
	 * Creates a new ScriptParser for a script format.
	 * 
	 * @param ext
	 * The extension for files that can be executed.
	 */
	public ScriptParser(String ext) {
		scriptExtension = ext;
		targets = new ArrayList<ScriptListener>();
		scripts = new ArrayList<File>();
		commands = new HashMap<String,CommandDescription>();
	}
	
	/**
	 * Adds a target for the commands to be invoked on.
	 * 
	 * @param target
	 * The target to add.
	 */
	public void addListener(ScriptListener target) {
		targets.add(target);
	}
	
	/**
	 * Adds a script to read commands from.
	 * 
	 * @param location
	 * The location on disk of the script to add.
	 * 
	 * @throws FileNotFoundException
	 * If the specified file location does not exist.
	 * 
	 * @throws InvalidFormatException
	 * If the specified file is not the right format.
	 */
	public void addScript(String location) throws FileNotFoundException, InvalidFormatException {
		String[] parts = location.split(".");
		String ext = parts[parts.length-1];
		if(!scriptExtension.equalsIgnoreCase(ext)) {
			throw new InvalidFormatException("bad file type '"+ext+"'.", scriptExtension);
		}
		File scriptFile = new File(location);
		if(!scriptFile.exists()) {
			throw new FileNotFoundException("'"+location+"': no such file or directory.");
		}
		scripts.add(scriptFile);
	}
	
	/**
	 * Adds a command definition to this ScriptParser.
	 * 
	 * @param command
	 * The description of the command. This includes all of the
	 * necessary data for the command; the name, arguments, and
	 * flags.
	 */
	public void addCommand(CommandDescription command) {
		commands.put(command.name, command);
	}
	
	/**
	 * Reads each specified script and executes the associated
	 * command on each target.
	 */
	public void execute() {
		for(File script: scripts) {
			try {
				openStream(script);
				executeCurrentScript();
			} catch(FileNotFoundException e) {
				System.err.println("Error reading script '"+script.getName()+"'; skipping");
			} catch(IOException e) {
				System.err.println("Error executing script '"+script.getName()+"'; skipping");
			} catch(ScriptException e) {
				System.err.println(e.getMessage());
				System.err.println("Error executing script '"+script.getName()+"'; skipping");
			}
		}
	}
	
	/**
	 * Gets help on the commands this ScriptParser is capable of
	 * processing.
	 * 
	 * @return
	 * The help string.
	 */
	public String getHelp() {
		String help = "";
		for(CommandDescription cd: commands.values()) {
			help += cd.getHelpMessage() + "\n";
		}
		return help;
	}
	
	/**
	 * Opens the reader stream to a script.
	 * 
	 * @param script
	 * The script to open.
	 */
	private void openStream(File script) throws FileNotFoundException {
		FileReader file = null;
		file = new FileReader(script);
		scriptReader = new BufferedReader(file);
	}
	
	/**
	 * Executes the current script on each target.
	 */
	private void executeCurrentScript() throws IOException, ScriptException {
		String line = null;
		while((line = scriptReader.readLine()) != null) {
			parseLine(line);
		}
	}
	
	/**
	 * Parses a line for its command and invokes on each
	 * target.
	 * 
	 * @param line
	 * The line to parse.
	 */
	private void parseLine(String line) throws ScriptException {
		Command command = new Command(line);
		try {
			executeCommand(command);
		} catch(UnknownCommandException e) {
			System.err.println(e.getMessage());
		} catch(CommandArgumentException e) {
			System.err.println(e.getMessage());
		}
	}
	
	/**
	 * Executes a command on each target.
	 * 
	 * @param command
	 * The Command to execute.
	 * 
	 * @throws ScriptException
	 * If an error ocurred when invoking the command.
	 */
	private void executeCommand(Command command) throws UnknownCommandException, CommandArgumentException, ScriptException {
		if(!commands.containsKey(command.getName())) {
			throw new UnknownCommandException(command.getName());
		} else {
			command.setDescription(commands.get(command.getName()));
		}
		if(!command.syntaxIsGood()) {
			throw new CommandArgumentException("Not enough parameters given");
		}
		invokeCommand(command);
	}
	
	/**
	 * Invokes a command with its parameters on each of the targets.
	 * 
	 * @param command
	 * The command to invoke.
	 * 
	 * @throws ScriptException
	 * If a critical error was encountered.
	 */
	private void invokeCommand(Command command) throws ScriptException {
		for(ScriptListener target: targets) {
			try {
				target.executeScriptCommand(command);
			} catch(CommandException e) {
				System.err.println(e.getMessage());
			}
		}
	}
}