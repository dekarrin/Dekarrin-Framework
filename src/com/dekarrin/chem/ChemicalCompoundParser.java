package com.dekarrin.chem;

import java.util.*;

/**
 * Parses a string for a chemical compound.
 */
public class ChemicalCompoundParser {

	/**
	 * The string being parsed.
	 */
	@SuppressWarnings("unused")
	private String parsingString;

	/**
	 * Creates a new ChemicalCompoundParser.
	 *
	 * @param compoundString
	 * Contains the string to be parsed.
	 */
	public ChemicalCompoundParser(String compoundString) {
		String parsingString = compoundString;
		parse(parsingString);
	}
	
	/**
	 * Gets the information out of a string and stores the Ionizable objects
	 * 
	 * @param subject
	 * The string to parse.
	 */
	@SuppressWarnings("unused")
	private void parse(String subject) {
		char[] subjectChars = subject.toCharArray();
		StringBuffer ion = new StringBuffer();
		
		for(char sc: subjectChars) {
		}
	}
	
	public Vector<Ionizable> getAll() {
		return null;
	}
}
