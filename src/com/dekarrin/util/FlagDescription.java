package com.dekarrin.util;

/**
 * Gives information on a command flag.
 */
public class FlagDescription() {

	/**
	 * The name of this flag.
	 */
	public String name;
	
	/**
	 * The long name of this flag, if it has one.
	 */
	public String longName;
	
	/**
	 * A description of this flag's purpose, and how it affects
	 * command execution.
	 */
	public String description;
	
	/**
	 * Whether or not this flag can hold a value.
	 */
	public boolean hasValue;
	
	/**
	 * Creates a new FlagDescription.
	 *
	 * @param name
	 * The name of the flag being described.
	 *
	 * @param description
	 * Information on this flag.
	 */
	public FlagDescription(String name, String description) {
		this.name = name;
		longName = name;
		this.description = description;
		hasValue = false;
	}
	
	/**
	 * Creates a new FlagDescription with a distinct short
	 * name and long name.
	 *
	 * @param shortName
	 * The name of the flag being described.
	 *
	 * @param longName
	 * The long format name of the flag.
	 *
	 * @param description
	 * Information on this flag.
	 */
	public FlagDescription(String shotName, String longName, String description) {
		name = shortName;
		this.longName = longName;
		this.description = description;
		hasValue = false;
	}
	
	/**
	 * Creates a new FlagDescription.
	 *
	 * @param name
	 * The name of the flag being described.
	 *
	 * @param description
	 * Information on this flag.
	 *
	 * @param hasValue
	 * Whether or not this flag accepts a value.
	 */
	public FlagDescription(String name, String description, boolean hasValue) {
		this.name = name;
		longName = name;
		this.description = description;
		this.hasValue = hasValue;
	}
	
	/**
	 * Creates a new FlagDescription with a distinct short
	 * name and long name.
	 *
	 * @param shortName
	 * The name of the flag being described.
	 *
	 * @param longName
	 * The long format name of the flag.
	 *
	 * @param description
	 * Information on this flag.
	 *
	 * @param hasValue
	 * Whether or not this flag accepts a value.
	 */
	public FlagDescription(String shotName, String longName, String description, boolean hasValue) {
		name = shortName;
		this.longName = longName;
		this.description = description;
		this.hasValue = hasValue;
	}
}