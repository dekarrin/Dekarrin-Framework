package com.dekarrin.cli;

import java.util.HashMap;

/**
 * Processes argument lists for flags. This class can be case-sensitive.
 *
 * In order to allow for the possibility of multiple flags in one argument,
 * it is assumed that each character is a different flag, unless the flag
 * begins with a double dash or the flag contains an equals sign and a value.
 * If either of the above cases occur, the argument is assumed to contain
 * only one flag that is several characters long.
 * 
 * For example, '-help' is considered to contain four flags: 'h', 'e', 'l',
 * and 'p'. In order to have this processed as one flag, it must use a double
 * dash: '--help'. '-help=true', on the other hand, is considered to have one
 * flag, 'help', which contains the value 'true'. '--help=true' also works
 * for passing in a single flag.
 */
public class FlagArgumentList {
	
	/**
	 * Removes flag arguments from a list of arguments.
	 *
	 * @param args
	 * The argument list to strip flags from.
	 *
	 * @return
	 * The given list without flags.
	 */
	public static String[] removeFlags(String[] args) {
		int flagCount = countFlags(args);
		int newSize = args.length - flagCount;
		String[] strippedArgs = new String[newSize];
		
		int j = 0;
		for(int i = 0; i < args.length; i++) {
			if(args[i].charAt(0) != '-') {
				strippedArgs[j++] = args[i];
			}
		}
		
		return strippedArgs;
	}
	
	/**
	 * Whether flags are treated as case-sensitive or not.
	 */
	private boolean caseSensitive;
	
	/**
	 * The flags and their values.
	 */
	private HashMap<String,String> flags = new HashMap<String,String>();
	
	/**
	 * Creates a new FlagArgumentList that takes its values from
	 * an argument list. Defaults to case-sensitive mode.
	 *
	 * @param args
	 * The arguments to take flags from.
	 */
	public FlagArgumentList(String[] args) {
		caseSensitive = true;
		getFlags(args);
	}
	
	/**
	 * Creates a new FlagArgumentList that takes its values from
	 * an argument list.
	 *
	 * @param args
	 * The arguments to take flags from.
	 *
	 * @param caseSensitive
	 * Whether flags are treated as case-sensitive or not.
	 */
	public FlagArgumentList(String[] args, boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
		getFlags(args);
	}
	
	/**
	 * Checks if this list contains a flag.
	 *
	 * @param name
	 * The name of the flag to check for.
	 *
	 * @return
	 * Whether or not the flag exists.
	 */
	public boolean contains(String name) {
		return flags.containsKey(name);
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
	public String getValue(String name) {
		return flags.get(name);
	}
	
	/**
	 * Checks how many flags are contained.
	 *
	 * @return
	 * The number of flags contained.
	 */
	public int size() {
		return flags.size();
	}
	
	/**
	 * Gets the flags from a list of arguments. The list is searched
	 * for any arguments that begin with a dash ('-'); this is an
	 * indication of a flag. These arguments are added to the internal
	 * list, and if they have values (-user=jack), they are added as
	 * well.
	 *
	 * @param args
	 * The list to take arguments from.
	 */
	private void getFlags(String[] args) {
		for(int i = 0; i < args.length; i++) {
			if(args[i].charAt(0) == '-') {
				parseFlagArgument(args[i]);
			}
		}	
	}
	
	/**
	 * Adds a flag(s) to the internal list. See the class help for an
	 * explanation on how multiple flags are parsed.
	 *
	 * @param flagArgument
	 * The unformatted flag taken directly from the command line.
	 */
	private void parseFlagArgument(String flagArgument) {
		if(argumentContainsMultipleFlags(flagArgument)) {
			parseMultipleFlagArgument(flagArgument);
		} else {
			parseSingleFlagArgument(flagArgument);
		}
	}
	
	/**
	 * Parses a flag argument containing multiple flags.
	 *
	 * @param flagArgument
	 * The flag argument taken directly from the command line.
	 */
	private void parseMultipleFlagArgument(String flagArgument) {
		// a multi-flag argument will never start with a double dash
		String flagString = flagArgument.substring(1);
		char[] flags = flagString.toCharArray();
		for(char f: flags) {
			String flagName = Character.toString(f);
			addFlag(flagName);
		}	
	}
	
	/**
	 * Parses a flag argument containing a single flag.
	 *
	 * @param flagArgument
	 * The flag argument taken directly from the command line.
	 */
	private void parseSingleFlagArgument(String flagArgument) {
		int startIndex = (flagArgument.charAt(1) == '-') ? 2 : 1;
		String flagString = flagArgument.substring(startIndex);
		
		if(flagContainsValue(flagString)) {
			String flagName = flagString.split("=", 2)[0];
			String flagValue = flagString.split("=", 2)[1];
			addFlag(flagName, flagValue);
		} else {
			addFlag(flagString);
		}	
	}
	
	/**
	 * Adds a flag to the list.
	 *
	 * @param name
	 * The name of the flag.
	 *
	 * @param value
	 * The value of the flag.
	 */
	private void addFlag(String name, String value) {
		flags.put(name, value);
	}
	
	/**
	 * Adds a flag without a value to the list.
	 *
	 * @param name
	 * The name of the flag.
	 */
	private void addFlag(String name) {
		addFlag(name, null);
	}
	
	/**
	 * Checks if an argument contains multiple flags.
	 *
	 * @param flagArgument
	 * The argument being checked.
	 *
	 * @return
	 * Whether or not the argument contains multiple flags.
	 */
	private boolean argumentContainsMultipleFlags(String flagArgument) {
		boolean hasMultiple = true;
		if(flagArgument.charAt(1) == '-' || flagArgument.indexOf("=") != -1) {
			hasMultiple = false;
		}
		return hasMultiple;
	}
	
	/**
	 * Counts the number of arguments that appear to be flags.
	 *
	 * @param arguments
	 * The arguments from the command line.
	 *
	 * @return
	 * The number of arguments which are flags.
	 */
	private static int countFlags(String[] arguments) {
		int count = 0;
		for(String a: arguments) {
			if(a.charAt(0) == '-') {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Checks if there is a value in the flag.
	 *
	 * @param flag
	 * The flag to check.
	 *
	 * @return
	 * Whether there is a value.
	 */
	private boolean flagContainsValue(String flag) {
		return (flag.indexOf("=") != -1);
	}
}