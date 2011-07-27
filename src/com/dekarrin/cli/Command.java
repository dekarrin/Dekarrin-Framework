package com.dekarrin.cli;

/**
 * Represents a parsed command from a CLI program.
 */
public class Command {
	
	/**
	 * The name of the actual command that was entered.
	 */
	private String commandName;
	
	/**
	 * The list of arguments that are flags.
	 */
	private FlagArgumentList flags;
	
	/**
	 * The list of arguments that are not flags.
	 */
	private String[] arguments;
	
	/**
	 * The description of this Command.
	 */
	private CommandDescription description;
	
	/**
	 * Creates a new command directly from the input.
	 *
	 * @param input
	 * The input, untouched from the prompt.
	 */
	public Command(String input) {
		String[] words = input.split(" ");
		String[] arguments = java.util.Arrays.copyOfRange(words, 1, words.length);
		commandName = words[0];
		flags = new FlagArgumentList(arguments);
		this.arguments = FlagArgumentList.removeFlags(arguments);
	}
	
	/**
	 * Sets this Command's CommandDescription object so it can
	 * check its own syntax and give indexed arguments. This should
	 * be called as soon as possible after this Command is
	 * constructed.
	 *
	 * @param description
	 * The CommandDescription associated with this command.
	 */
	public void setDescription(CommandDescription description) {
		this.description = description;
	}
	
	/**
	 * Gets the name of this command.
	 *
	 * @return
	 * The name, lower-cased.
	 */
	public String getName() {
		return commandName.toLowerCase();
	}
	
	/**
	 * Checks if this command contains a flag.
	 *
	 * @param name
	 * The name of the flag to check for.
	 *
	 * @return
	 * Whether or not the flag exists.
	 */
	public boolean hasFlag(String name) {
		// both the long and short name need to be checked.
		String[] names = getBothFlagNames(name);
		if(flags.contains(names[0]) || flags.contains(names[1])) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the value of a flag.
	 *
	 * @param name
	 * The name of the flag.
	 *
	 * @return
	 * The value of the flag.
	 */
	public String getFlag(String name) {
		String[] names = getBothFlagNames(name);
		if(flags.contains(names[0])) {
			return flags.getValue(names[0]);
		} else if(flags.contains(names[1])) {
			return flags.getValue(names[1]);
		} else {
			return null;
		}
	}
	
	/**
	 * Checks if this command has a certain argument associated
	 * with it.
	 *
	 * @param name
	 * The name of the argument to check for.
	 *
	 * @return
	 * True if the argument exists, false otherwise.
	 */
	public boolean hasArgument(String name) {
		boolean has = false;
		int argIndex = getArgumentIndex(name);
		if(argIndex < arguments.length) {
			if(arguments[argIndex] != null) {
				has = true;
			}
		}
		return has;
	}
	
	/**
	 * Gets an argument from this Command if it exists.
	 *
	 * @param name
	 * The name of the argument.
	 *
	 * @return
	 * The value of the argument, or null if the argument
	 * doesn't exist.
	 */
	public String getArgument(String name) {
		String argumentValue = null;
		int argIndex = getArgumentIndex(name);
		if(argIndex < arguments.length) {
			argumentValue = arguments[argIndex];
		}
		return argumentValue;
	}
	
	/**
	 * Checks if there are there right number of arguments
	 * in this Command.
	 *
	 * @return
	 * True if this Command has at least as many arguments
	 * as is necessary for it.
	 */
	public boolean syntaxIsGood() {
		boolean isGood = true;
		int min = getMinimumArguments();
		if(arguments.length < min) {
			isGood = false;
		}
		return isGood;
	}
	
	/**
	 * Gets a syntax message for this Command.
	 *
	 * @return
	 * A message showing proper syntax for this Command.
	 */
	public String getSyntaxMessage() {
		return description.getSyntaxMessage();
	}
	
	/**
	 * Gets a full-on help message for this Command.
	 *
	 * @return
	 * A long String containing the help message.
	 */
	public String getHelpMessage() {
		return description.getHelpMessage();
	}
	
	/**
	 * Gets the index of an argument. This is the index that
	 * the argument is at if it exists.
	 *
	 * @param argument
	 * The argument to get the index for.
	 *
	 * @return
	 * The index of the argument, if it is an argument. If
	 * the given argument does not exist, -1 is returned.
	 */
	private int getArgumentIndex(String argument) {
		int index = -1;
		int position = 0;
		ArgumentDescription ad;
		while(index == -1 && position < description.arguments.length) {
			ad = description.arguments[position];
			if(ad.name.equals(argument)) {
				index = position;
			}
			position++;
		}
		return index;
	}
	
	/**
	 * Gets the minimum number of arguments required for this
	 * command.
	 *
	 * @return
	 * The minimum number of arguments.
	 */
	private int getMinimumArguments() {
		int minimum = 0;
		for(ArgumentDescription ad: description.arguments) {
			if(!ad.isOptional) {
				minimum++;
			}
		}
		return minimum;
	}
	
	/**
	 * Gets both the short and long names of a flag.
	 * 
	 * @param name
	 * Either the short or long name; doesn't matter.
	 * 
	 * @return
	 * An array containing the short name at index 0
	 * and the long name at index 1. Both of these will
	 * be null if there is no tag that has the given
	 * name.
	 */
	private String[] getBothFlagNames(String name) {
		String[] names = new String[2];
		for(FlagDescription fd: description.flags) {
			if(fd.name.equalsIgnoreCase(name) || fd.longName.equalsIgnoreCase(name)) {
				names[0] = fd.name;
				names[1] = fd.longName;
			}
		}
		return names;
	}
}