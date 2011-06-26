package com.dekarrin.cli;

/**
 * Gives information on a command argument.
 */
public class ArgumentDescription {
	
	/**
	 * The name of this argument.
	 */
	public String name;
	
	/**
	 * The description of this argument.
	 */
	public String description;
	
	/**
	 * Whether or not this argument may be omitted.
	 */
	public boolean isOptional;
	
	/**
	 * Creates a new ArgumentDescription that is not
	 * optional.
	 *
	 * @param name
	 * The name of the argument.
	 *
	 * @param description
	 * Information on the argument.
	 */
	public ArgumentDescription(String name, String description) {
		this.name = name;
		this.description = description;
		this.isOptional = false;
	}
	
	/**
	 * Creates a new ArgumentDescription.
	 *
	 * @param name
	 * The name of the argument.
	 *
	 * @param description
	 * Information on the argument.
	 *
	 * @param isOptional
	 * Whether the argument is optional.
	 */
	public ArgumentDescription(String name, String description, boolean isOptional) {
		this.name = name;
		this.description = description;
		this.isOptional = isOptional;
	}
}