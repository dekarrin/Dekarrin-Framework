package com.dekarrin.cli;

import com.dekarrin.util.HelperString;

/**
 * Gives information on a command from a CLI program.
 */
public class CommandDescription {
	
	/**
	 * The name of this command.
	 */
	public String name;
	
	/**
	 * A description of this command to be used with help
	 * screens.
	 */
	public String description;
	
	/**
	 * The list of possible arguments for this command.
	 */
	public ArgumentDescription[] arguments;
	
	/**
	 * The list of possible flags for this command.
	 */
	public FlagDescription[] flags;
	
	/**
	 * Creates a new Command that accepts flags and arguments.
	 *
	 * @param name
	 * The name of the new Command. This is what the user
	 * types to execute the command.
	 *
	 * @param description
	 * The description of the new Command. This should give
	 * a basic overview of what the command does.
	 *
	 * @param arguments
	 * Descriptions of the arguments this command takes.
	 *
	 * @param flags
	 * Descriptions of the flags this command takes.
	 */
	public CommandDescription(String name, String description, ArgumentDescription[] arguments, FlagDescription[] flags) {
		this.name = name;
		this.description = description;
		this.arguments = arguments;
		this.flags = flags;
	}
	
	/**
	 * Creates a new Command that accepts arguments but not
	 * flag.
	 *
	 * @param name
	 * The name of the new Command. This is what the user
	 * types to execute the command.
	 *
	 * @param description
	 * The description of the new Command. This should give
	 * a basic overview of what the command does.
	 *
	 * @param arguments
	 * Descriptions of the arguments this command takes.
	 */
	public CommandDescription(String name, String description, ArgumentDescription[] arguments) {
		this.name = name;
		this.description = description;
		this.arguments = arguments;
	}
	
	/**
	 * Creates a new Command that accepts flags but not
	 * arguments.
	 *
	 * @param name
	 * The name of the new Command. This is what the user
	 * types to execute the command.
	 *
	 * @param description
	 * The description of the new Command. This should give
	 * a basic overview of what the command does.
	 *
	 * @param flags
	 * Descriptions of the flags this command takes.
	 */
	public CommandDescription(String name, String description, FlagDescription[] flags) {
		this.name = name;
		this.description = description;
		this.flags = flags;
	}
	
	/**
	 * Creates a new Command that does not accept arguments
	 * or flags.
	 *
	 * @param name
	 * The name of the new Command. This is what the user
	 * types to execute the command.
	 *
	 * @param description
	 * The description of the new Command. This should give
	 * a basic overview of what the command does.
	 */
	public CommandDescription(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	/**
	 * Gets a syntax message for the command that this
	 * CommandDescription is associated with.
	 *
	 * @return
	 * A message showing proper syntax for this Command.
	 */
	public String getSyntaxMessage() {
		String syntax = "syntax: " + name;
		syntax += getFlagSyntax();
		syntax += getArgumentSyntax();
		return syntax;
	}
	
	/**
	 * Gets a full-on help message for the command that this
	 * CommandDescription is associated with.
	 *
	 * @return
	 * A long String containing the help message.
	 */
	public String getHelpMessage() {
		String help = "";
		
		help += "NAME\n";
		help += "\t"+name+" -- "+description+"\n";
		help += "\n";
		help += "SYNOPSIS\n";
		help += "\t"+getSyntaxMessage()+"\n";
		help += "\n";
		help += getArgumentHelp();
		help += getFlagHelp();
		return help;
	}
	
	/**
	 * Gets the short list-form of the command that this
	 * CommandDescription is associated with.
	 *
	 * @param nameLength
	 * The length that the name should be. The name section
	 * of the list will be padded to this length.
	 *
	 * @return
	 * The short list form of the command.
	 */
	public String getListing(int nameLength) {
		HelperString nameSegment = new HelperString(name);
		nameSegment.padRight(nameLength);
		String listing = nameSegment.toString() + " - " + description;
		return listing;
	}
	
	/**
	 * Gets the syntax for the flags in thie command that
	 * this CommandDescription is associated with.
	 *
	 * @return
	 * The flag syntax.
	 */
	private String getFlagSyntax() {
		String flagSyntax = "";
		if(flags.length > 0) {
			String noValue = "";
			String valued = "";
			for(FlagDescription fd: flags) {
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
	 * Gets the syntax for the arguments in the command that
	 * this CommandDescription is associated with.
	 *
	 * @return
	 * The argument syntax.
	 */
	private String getArgumentSyntax() {
		String argSyntax = "";
		if(arguments.length > 0) {
			for(ArgumentDescription ad: arguments) {
				String lSep, rSep;
				if(ad.isOptional) {
					lSep = "[";
					rSep = "]";
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
	 * @return
	 * The flag help string.
	 */
	private String getFlagHelp() {
		String help = "";
		if(flags.length > 0) {
			help += "FLAGS";
			for(FlagDescription fd: flags) {
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
	 * @return
	 * The argument help string.
	 */
	private String getArgumentHelp() {
		String help = "";
		if(arguments.length > 0) {
			help += "ARGUMENTS";
			for(ArgumentDescription ad: arguments) {
				help += "\n";
				help += "\t"+ad.name+":\n";
				help += "\t\t"+ad.description+"\n";
			}
			help += "\n";
		}
		return help;
	}
}