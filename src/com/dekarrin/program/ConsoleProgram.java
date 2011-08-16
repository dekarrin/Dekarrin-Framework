package com.dekarrin.program;

import com.dekarrin.cli.ArgumentDescription;
import com.dekarrin.cli.FlagArgumentList;
import com.dekarrin.io.InteractionModule;
import com.dekarrin.util.HelperString;

import java.util.LinkedHashMap;

/**
 * Program to be run from the command line. Processes flags automatically.
 */
public abstract class ConsoleProgram {
	
	/**
	 * The io module for interacting with the user.
	 */
	protected InteractionModule ui;
	
	/**
	 * The arguments passed to this program.
	 */
	protected String[] args;
	
	/**
	 * The flags passed to this program.
	 */
	protected FlagArgumentList flags;
	
	/**
	 * The name of the currently running program. This is
	 * automatically obtained using the exception throw/catch
	 * trick.
	 */
	protected String programName;
	
	/**
	 * The current position of the assignment pointer for
	 * assigning arguments.
	 */
	private int assign = 0;
	
	/**
	 * A list of argument names for syntax help.
	 */
	private LinkedHashMap<String,ArgumentDescription> arguments = new LinkedHashMap<String,ArgumentDescription>(2);
	
	/**
	 * Do not use; immediately throws an exception. The use of this constructor
	 * is invalid because in order to work, ConsoleProgram needs to have the
	 * command line arguments passed to it.
	 */
	public ConsoleProgram() {
		throw new UnsupportedOperationException("You must pass args to ConsoleProgram!");
	}
	
	/**
	 * Creates an instance of the program and checks if it has the minimum
	 * number of arguments.
	 */
	public ConsoleProgram(String[] args) {
		this.flags = new FlagArgumentList(args);
		this.args = FlagArgumentList.removeFlags(args);
		ui = new InteractionModule();
		setProgramName();
	}
	
	/**
	 * Gets a command line argument. If none exists and it was set
	 * as a required argument, an error message is given and the
	 * program terminates.
	 *
	 * @param name
	 * The name of the argument to get.
	 *
	 * @return
	 * The argument.
	 */
	protected String getArgument(String name) {
		String arg = null;
		if(!hasArgument(name)) {
			if(!argumentIsOptional(name)) {
				giveSyntaxError();
			}
		} else {
			arg = args[arguments.get(name).position];
		}
		return arg;
	}
	
	/**
	 * Gets a command line argument as an integer.
	 * 
	 * @param name
	 * The name of the argument to get.
	 * 
	 * @param defaultValue
	 * The value to give if the int is not correctly
	 * formatted.
	 * 
	 * @return
	 * The argument as an int.
	 */
	int getArgumentAsInt(String name, int defaultValue) {
		int iArg = 0;
		try {
			iArg = Integer.parseInt(getArgument(name));
		} catch(NullPointerException e) {
			iArg = defaultValue;
		} catch(NumberFormatException e) {
			giveFatalError(name + " must be an integer.");
		}
		return iArg;
	}
	
	/**
	 * Gets a command line argument as a double.
	 * 
	 * @param name
	 * The name of the argument to get.
	 * 
	 * @param defaultValue
	 * The value to give if the double is not correctly
	 * formatted.
	 * 
	 * @return
	 * The argument as an double.
	 */
	double getArgumentAsDouble(String name, double defaultValue) {
		double dArg = 0;
		try {
			dArg = Double.parseDouble(getArgument(name));
		} catch(NullPointerException e) {
			dArg = defaultValue;
		} catch(NumberFormatException e) {
			giveFatalError(name + " must be a real number.");
		}
		return dArg;
	}
	
	/**
	 * Displays a message on proper syntax, then quits. The message is
	 * automatically generated using the internal lists of arguments set
	 * by subclasses.
	 */
	private void giveSyntaxError() {
		String error = "usage: java "+programName;
		if(arguments.size() > 0) {
			error += " ";
			for(ArgumentDescription a: arguments.values()) {
				error += String.format(a.isOptional ? "<%s> " : "[%s] ", a.name);
			}
		}
		if(arguments.size() > 0) {
			error += "\nARGUMENTS:";
			for(ArgumentDescription a: arguments.values()) {
				error += "\n\t" + (new HelperString(a.name)).padRight(15).toString() + " - " + a.description;
			}
		}
		giveFatalError(error);
	}
	
	/**
	 * Gets the name of the program currently running.
	 */
	private void setProgramName() {
		try{
			throw new Exception();
		} catch(Exception e) {
			StackTraceElement[] l = e.getStackTrace();
			programName = l[l.length-1].getClassName();
		}
	}
	
	/**
	 * Adds an argument to the internal lists. The arguments are used
	 * for syntax help generation. Note that it is not necessary to add
	 * every argument you plan on using; however, any argument not added
	 * will not be displayed in an automatically-generated syntax error.
	 *
	 * @param name
	 * The name of the argument.
	 *
	 * @param isOptional
	 * Whether or not the argument can be omitted.
	 */
	protected void addArgument(String name, boolean isOptional) {
		addArgument(name, "", isOptional);
	}
	
	/**
	 * Adds an argument to the internal lists. The arguments are used
	 * for syntax help generation. Note that it is not necessary to add
	 * every argument you plan on using; however, any argument not added
	 * will not be displayed in an automatically-generated syntax error.
	 *
	 * @param name
	 * The name of the argument.
	 * 
	 * @param description
	 * A description of the argument.
	 *
	 * @param isOptional
	 * Whether or not the argument can be omitted.
	 */
	protected void addArgument(String name, String description, boolean isOptional) {
		ArgumentDescription arg = new ArgumentDescription(name, description, isOptional);
		arg.position = assign++;
		arguments.put(arg.name, arg);
	}
	
	/**
	 * Shows an error message, then terminates the program.
	 *
	 * @param message
	 * The error message.
	 */
	protected void giveFatalError(String message) {
		ui.printError(message);
		ui.println();
		System.exit(1);
	}
	
	/**
	 * Checks if an argument is optional.
	 * 
	 * @param name
	 * The name of the argument to check.
	 * 
	 * @return
	 * True if the argument is optional; false otherwise.
	 */
	protected boolean argumentIsOptional(String name) {
		if(name != null) {
			return arguments.get(name).isOptional;
		} else {
			return true;
		}
	}
	
	/**
	 * Checks if this program was given a certain
	 * argument.
	 * 
	 * @param name
	 * The name of the argument to check for.
	 * 
	 * @return
	 * True if the argument exists; false otherwise.
	 */
	protected boolean hasArgument(String name) {
		ArgumentDescription a = arguments.get(name);
		boolean has = false;
		if(a != null) {
			if(a.position - optionalsBefore(name) < args.length) {
				has = true;
			}
		}
		return has;
	}
	
	/**
	 * Gets the number of optional arguments that occur before a
	 * certain one.
	 * 
	 * @param name
	 * The name of the argument to check.
	 * 
	 * @return
	 * The number of arguments that occur before the given one
	 * and that are optional.
	 */
	private int optionalsBefore(String name) {
		ArgumentDescription a = arguments.get(name);
		int p = a.position;
		ArgumentDescription[] argDefs = arguments.values().toArray(new ArgumentDescription[0]);
		int opt = 0;
		for(int i = 0; i < p; i++) {
			if(argDefs[i].isOptional) {
				opt++;
			}
		}
		return opt;
	}
}