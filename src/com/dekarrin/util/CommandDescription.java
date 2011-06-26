package com.dekarrin.util;

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
	 * @param arguments
	 * Descriptions of the arguments this command takes.
	 */
	public CommandDescription(String name, String description, ArgumentDescription[] arguments) {
		this.name = name;
		this.description = description;
		this.arguments = arguments;
	}
	
	/**
	 * Creates a new Command that accepts arguments but not
	 * flags.
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
}