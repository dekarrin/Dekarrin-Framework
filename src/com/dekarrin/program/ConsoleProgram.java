package com.dekarrin.program;

import com.dekarrin.cli.FlagArgumentList;
import com.dekarrin.io.InteractionModule;

import java.util.ArrayList;

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
	 * A list of argument names for syntax help.
	 */
	private ArrayList<String> argumentNames = new ArrayList<String>(2);
	
	/**
	 * A list of whether each argument is optional.
	 */
	private ArrayList<Boolean> optionalArgument = new ArrayList<Boolean>(2);
	
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
	 * Gets a command line argument. If none exists, gives an error message.
	 *
	 * @param index
	 * The index of the argument to get.
	 *
	 * @return
	 * The argument.
	 */
	protected String getArgument(int index) {
		String arg = null;
		if(!hasArgument(index)) {
			if(!argumentIsOptional(index)) {
				giveSyntaxError();
			}
		} else {
			arg = args[index];
		}
		return arg;
	}
	
	/**
	 * Displays a message on proper syntax, then quits. The message is
	 * automatically generated using the internal lists of arguments set
	 * by subclasses.
	 */
	private void giveSyntaxError() {
		String error = "usage: java "+programName+" ";
		for(int i = 0; i < argumentNames.size(); i++) {
			error = error + String.format(argumentIsOptional(i) ? "<%s> " : "[%s] ", argumentNames.get(i));
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
		argumentNames.add(name);
		optionalArgument.add(isOptional);
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
	 * @param index
	 * The index of the argument to check.
	 * 
	 * @return
	 * True if the argument is optional; false otherwise.
	 */
	protected boolean argumentIsOptional(int index) {
		return optionalArgument.get(index);
	}
	
	/**
	 * Checks if this program was given a certain
	 * argument.
	 * 
	 * @param index
	 * The index of the argument to check for.
	 * 
	 * @return
	 * True if the argument exists; false otherwise.
	 */
	protected boolean hasArgument(int index) {
		return (index < args.length);
	}
}