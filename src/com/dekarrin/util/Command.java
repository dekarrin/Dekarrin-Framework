package com.dekarrin.util;

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
		flags = FlagArgumentList(arguments);
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
	 * @returns
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
	 * @returns
	 * Whether or not the flag exists.
	 */
	public boolean hasFlag(String name) {
		return flags.contains(name);
	}
	
	/**
	 * Gets the value of a flag.
	 *
	 * @param name
	 * The name of the flag.
	 *
	 * @returns
	 * The value of the flag.
	 */
	public String getFlag(String name) {
		return flags.getValue(name);
	}
	
	/**
	 * Checks if this command has a certain argument associated
	 * with it.
	 *
	 * @param name
	 * The name of the argument to check for.
	 *
	 * @returns
	 * True if the argument exists, false otherwise.
	 */
	public boolean hasArgument(String name) {
		boolean has = false;
		int argIndex = getIndexOfArgument(name);
		if(argument.index < arguments.length) {
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
	 * @returns
	 * The value of the argument, or null if the argument
	 * doesn't exist.
	 */
	public String getArgument(String name) {
		String argumentValue = null;
		int argIndex = getIndexOfArgument(name);
		if(argument.index < arguments.length) {
			argumentValue = arguments[argIndex];
		}
		return argumentValue;
	}
	
	/**
	 * Checks if there are there right number of arguments
	 * in this Command.
	 *
	 * @returns
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
	 * @returns
	 * A message showing proper syntax for this Command.
	 */
	public String getSyntaxMessage() {
		String syntax = "syntax: " + commandName;
		syntax += getFlagSyntax();
		syntax += getArgumentSyntax();
		return syntax;
	}
	
	/**
	 * Gets a full-on help message for this Command.
	 *
	 * @returns
	 * A long String containing the help message.
	 */
	public String getHelpMessage() {
		String help = "";
		
		help += "NAME\n";
		help += "\t"+commandName+" -- "+description.description+"\n"
		help += "\n";
		help += "SYNOPSIS\n";
		help += "\t"+getSyntaxMessage()+"\n";
		help += "\n";
		help += getArgumentHelp();
		help += getFlagHelp();
		return help;
	}
	
	/**
	 * Gets the syntax for the flags in this Command.
	 *
	 * @returns
	 * The flag syntax.
	 */
	private String getFlagSyntax() {
		String flagSyntax = "";
		if(description.flags.length > 0) {
			String noValue = "";
			String valued = "";
			for(FlagDescription fd: description.flags) {
				if(fd.hasValue) {
					valued += String.format(" [-%s=%s]", fd.name, fd.longName);
				} else {
					noValue += fd.name;
				}
			}
			if(noValue.length() > 0) {
				noValue = " [" + noValue + "]";
				flagSyntax += noValue;
			}
			if(valued.length() > 0) {
				flagSyntax += valued;
			}
		}
		return flagSyntax;
	}
	
	/**
	 * Gets the syntax for the arguments in this Command.
	 *
	 * @returns
	 * The argument syntax.
	 */
	private String getArgumentSyntax() {
		String argSyntax = "";
		if(description.arguments.length > 0) {
			for(ArgumentDescription ad: description.arguments) {
				String lSep, rSep;
				if(ad.isOptional) {
					lSep = "[";
					rSep = "]"
				} else {
					lSep = "<";
					rSep = ">";
				}
				String argumentLine = String.format(" %s%s%s", lSep, ad.name, rSep);
				argSyntax = argumentLine;
			}
		}	
		return argSyntax;
	}
	
	/**
	 * Gets the help strings for the flags.
	 *
	 * @returns
	 * The flag help string.
	 */
	private String getFlagHelp() {
		String help = "";
		if(description.flags.length > 0) {
			help += "FLAGS";
			for(FlagDescription fd: description.flags) {
				help += "\n";
				help += "\t-"+fd.name;
				if(!fd.name.equals(fd.longName)) {
					help += " | -"+fd.longName;
				}
				if(fd.hasValue) {
					help += " = " + fd.longName;
				}
				help += "\n";
				help += "\t\t"+fd.description+"\n";
			}
			help += "\n";
		}
		return help;
	}
	
	/**
	 * Gets the help strings for the arguments.
	 *
	 * @returns
	 * The argument help string.
	 */
	private String getArgumentHelp() {
		String help = "";
		if(description.arguments.length > 0) {
			help += "ARGUMENTS";
			for(ArgumentDescription ad: description.arguments) {
				help += "\n";
				help += "\t"+ad.name+":\n";
				help += "\t\t"+ad.description+"\n";
			}
			help += "\n";
		}
		return help;
	}
}