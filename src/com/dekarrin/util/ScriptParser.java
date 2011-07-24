package com.dekarrin.util;

import java.lang.reflect.*;
import java.util.*;
import java.io.*;

import com.dekarrin.error.CommandArgumentException;
import com.dekarrin.error.UnknownCommandException;

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
	private ArrayList<Object> targets;
	
	/**
	 * The scripts to read from.
	 */
	private ArrayList<File> scripts;
	
	/**
	 * The command definitions.
	 */
	private HashMap<String,Method> commands;
	
	/**
	 * The reader for the script currently being executed.
	 */
	private BufferedReader scriptReader;
	
	/**
	 * Creates a new ScriptParser.
	 */
	public ScriptParser() {
		targets = new ArrayList<Object>();
		scripts = new ArrayList<File>();
		commands = new HashMap<String,Method>();
	}
	
	/**
	 * Adds a target for the commands to be invoked on.
	 * 
	 * @param target
	 * The target to add.
	 */
	public void addTarget(Object target) {
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
	 */
	public void addScript(String location) throws FileNotFoundException {
		File scriptFile = new File(location);
		if(!scriptFile.exists()) {
			throw new FileNotFoundException("'"+location+"': no such file or directory.");
		}
		scripts.add(scriptFile);
	}
	
	/**
	 * Adds a command definition to this ScriptParser.
	 * 
	 * @param name
	 * The name of the command. This is how the command will
	 * appear in the script.
	 * 
	 * @param method
	 * The method to invoke whenever the command is encountered.
	 */
	public void addCommand(String name, Method method) {
		commands.put(name, method);
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
			}
		}
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
	private void executeCurrentScript() throws IOException {
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
	private void parseLine(String line) {
		String[] words = line.split(" ");
		String command = words[0];
		String[] params = Arrays.copyOfRange(words, 1, words.length);
		try {
			executeCommand(command, params);
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
	 * The name of the command to execute.
	 * 
	 * @param params
	 * The parameters of the command.
	 */
	private void executeCommand(String command, String[] params) throws UnknownCommandException, CommandArgumentException {
		if(!commands.containsKey(command)) {
			throw new UnknownCommandException(command);
		}
		Method method = commands.get(command);
		Class<?>[] paramTypes = method.getParameterTypes();
		if(params.length < paramTypes.length) {
			throw new CommandArgumentException("Not enough parameters given");
		}
		Object[] castParams = castParameters(params, paramTypes);
		invokeCommand(method, castParams);
	}
	
	/**
	 * Casts parameter values to their appropriate values.
	 * The only valid appropriate values are primitive wrapper
	 * classes.
	 * 
	 * @param params
	 * The parameters to cast.
	 * 
	 * @param types
	 * The type that each parameter needs to be cast to.
	 * 
	 * @return
	 * An array containing the params cast to their respective
	 * class.
	 * 
	 * @throws CommandArgumentException
	 * If any one of the types given is not a primitive wrapper
	 * class.
	 */
	private Object[] castParameters(String[] params, Class<?>[] types) throws CommandArgumentException {
		Object[] castParams = new Object[params.length];
		String className;
		for(int i = 0; i < params.length; i++) {
			className = types[i].getSimpleName();
			if(className.equals("Integer")) {
				castParams[i] = new Integer(params[i]);
			} else if(className.equals("Boolean")) {
				castParams[i] = new Boolean(params[i]);
			} else if(className.equals("Short")) {
				castParams[i] = new Short(params[i]);
			} else if(className.equals("Byte")) {
				castParams[i] = new Byte(params[i]);
			} else if(className.equals("Long")) {
				castParams[i] = new Long(params[i]);
			} else if(className.equals("Character")) {
				castParams[i] = new Character(params[i].charAt(0));
			} else if(className.equals("Float")) {
				castParams[i] = new Float(params[i]);
			} else if(className.equals("Double")) {
				castParams[i] = new Double(params[i]);
			} else if(className.equals("String")) {
				castParams[i] = params[i];
			} else {
				throw new CommandArgumentException("Invalid method parameter type '"+className+"'");
			}
		}
		return castParams;
	}
	
	/**
	 * Invokes a command with its parameters on each of the targets.
	 * 
	 * @param command
	 * The method to invoke.
	 * 
	 * @param params
	 * The parameters of the method.
	 */
	private void invokeCommand(Method command, Object[] parameters) {
		for(Object target: targets) {
			try {
				command.invoke(target, parameters);
			} catch(InvocationTargetException e) {
				System.err.println("Error invoking '"+command.getName()+"' on '"+target.toString()+"'");
				System.err.println("An exception was thrown: "+e.getCause().getClass().getSimpleName());
			} catch(IllegalAccessException e) {
				System.err.println("Error invoking '"+command.getName()+"' on '"+target.toString()+"'");
				System.err.println("Access to that method is prohibited");
			}
		}
	}
}