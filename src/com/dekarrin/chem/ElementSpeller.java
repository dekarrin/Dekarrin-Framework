package com.dekarrin.chem;

import com.dekarrin.util.HelperString;
import java.util.HashMap;
import java.util.Vector;

/**
 * Spells words using chemical symbol.
 */
public class ElementSpeller {
	
	/**
	 * The word that spelling operations are to be performed on.
	 */
	private String word;
	
	/**
	 * The contained word spelled as a series of element symbols.
	 */
	private String spelledWord;
	
	/**
	 * The symbols that make up the spelled word.
	 */
	private String[] symbols;
	
	/**
	 * The elements whose symbols make up the spelled word.
	 */
	private Element[] usedElements;
	
	/**
	 * Creates a new ElementSpeller instance.
	 *
	 * @param elementList
	 * A list of elements, indexed by their symbols.
	 */
	public ElementSpeller() {
		initialize();
	}
	
	/**
	 * Creates a new ElementSpeller instance and sets the contents
	 * to a word.
	 *
	 * @param elementList
	 * A list of elements, indexed by their symbols.
	 *
	 * @param word
	 * The word to set the contents to.
	 */
	public ElementSpeller(String word) {
		initialize();
		this.word = word;
		spell();
	}
	
	/**
	 * Sets the contents of this ElementSpeller to a word.
	 */
	public void setWord(String newWord) {
		initialize();
		word = newWord;
		spell();
	}
	
	/**
	 * Gets the contained word of this ElementSpeller.
	 *
	 * @returns
	 * The contained word.
	 */
	public String getWord() {
		return word;
	}
	
	/**
	 * Gets the word contained in this ElementSpeller spelled as a
	 * series of element symbols.
	 *
	 * @returns
	 * The word spelled with element symbols, if it is possible. If
	 * it is impossible to spell the word with chemical symbols, null
	 * is returned.
	 */
	public String getSpelledWord() {
		return spelledWord;
	}
	
	/**
	 * Gets the Elements that make up the spelled word in this
	 * ElementSpeller.
	 *
	 * @returns
	 * The Elements, if it is possible to spell the word; otherwise, null.
	 */
	public Element[] getElements() {
		return usedElements;
	}
	
	/**
	 * Gets the list of symbols used to spell the word in this
	 * ElementSpeller.
	 *
	 * @returns
	 * The symbols, if it is possible to spell the word; otherwise, null.	 
	 */
	public String[] getSymbols() {
		return symbols;
	}
	
	/**
	 * Checks whether the current contents of this ElementSpeller can be
	 * spelled using chemical symbols.
	 *
	 * @returns
	 * True if it can be spelled, false otherwise.
	 */
	public boolean wordIsPossible() {
		boolean possible;
	
		if(spelledWord != null) {
			possible =  true;
		} else {
			possible = false;
		}
		
		return possible;
	}
	
	/**
	 * Sets fields to their default values.
	 */
	private void initialize() {
		word = null;
		spelledWord = null;
		usedElements = null;
		symbols = null;
	}
	
	/**
	 * Spells the word using element symbols.
	 */
	private void spell() {
		char[] letters = word.toCharArray();
		Vector<String> symbolList = new Vector<String>(letters.length);
		boolean possible = true;
		
		for(int i = 0; i < letters.length; i++) {			
			if(!addThisLetter(symbolList, letters[i])) {
				if(!addPostCombo(symbolList, letters, i)) {
					if(!addPreCombo(symbolList, letters, i)) {
						possible = false;
						break;
					}
				} else {
					// the symbol was two letters, so skip the next one
					i++;
				}
			}
		}
		
		if(possible) {
			String[] spelledWordList = symbolList.toArray(new String[0]);
			symbols = spelledWordList;
			usedElements = new Element[symbols.length];
			
			for(int i = 0; i < symbols.length; i++) {
				usedElements[i] = ElementList.getElement(symbols[i]);
			}
			
			HelperString helper = new HelperString(spelledWordList);
			spelledWord = helper.toString();
		}
	}
	
	/**
	 * Attempts to add a letter as a chemical symbol. The symbol is
	 * added to a provided Vector.
	 *
	 * @param list
	 * The list that the element symbol should be added to if it exists.
	 *
	 * @param letter
	 * The letter to check for the symbol of.
	 *
	 * @returns
	 * True if the element could be added, false if it couldn't be.
	 */
	private boolean addThisLetter(Vector<String> list, char letter) {
		boolean successState = false;
		
		String symbol = makeChemicalSymbol(letter);
		if(elementWithSymbolExists(symbol)) {
			list.add(symbol);
			successState = true;
		}
		
		return successState;
	}
	
	/**
	 * Attempts to add a letter and the next letter together as a
	 * chemical symbol. The symbol is added to a provided Vector.
	 *
	 * @param list
	 * The list that the element symbol should be added to if it exists.
	 *
	 * @param letter
	 * The letter to be checked.
	 *
	 * @param i
	 * The current iteration that the main spelling loop is on.
	 *
	 * @returns
	 * True if the element could be added, false otherwise.
	 */
	private boolean addPostCombo(Vector<String> list, char[] letters, int i) {
		boolean successState = false;
		
		if(i < letters.length - 1) {
			char[] symChars = {letters[i], letters[i+1]};
			String symbol = makeChemicalSymbol(symChars);

			if(elementWithSymbolExists(symbol)) {
				list.add(symbol);
				successState = true;
			}
		}
		
		return successState;
	}
	
	/**
	 * Attempts to add the letter before and this letter together as a
	 * chemical symbol. The symbol is added to a provided Vector. If the
	 * word would be messed up by taking the letter behind the current
	 * one, this method will be recursively called until either the word
	 * is successfully spelled up to the current iteration of the main
	 * loop, or the beginning of the word is reached without fixing the
	 * spelling, in which case this method returns false.
	 *
	 * @param list
	 * The list that the symbol is to be added.
	 *
	 * @param letters
	 * The letters of the word.
	 *
	 * @param i
	 * The current iteration of the main spelling loop.
	 *
	 * @returns
	 * True if the symbol could be added, false otherwise.
	 */
	private boolean addPreCombo(Vector<String> list, char[] letters, int i) {
		boolean successState = false;
		
		if(i > 0) {
			char[] symChars = {letters[i-1], letters[i]};
			String symbol = makeChemicalSymbol(symChars);
			
			if(elementWithSymbolExists(symbol)) {
				String lastSymbol = list.remove(list.size() - 1);
				if(lastSymbol.length() == 1) {
					list.add(symbol);
					successState = true;
				} else {
					if(addPreCombo(list, letters, i-2)) {
						list.add(symbol);
						successState = true;
					}
				}
			}
		}
		
		return successState;
	}
	
	/**
	 * Makes a chemical symbol from a char. Basically just capitalizes
	 * the letter and converts it into a String.
	 *
	 * @param letter
	 * The letter to convert.
	 *
	 * @returns
	 * Returns the char as a chemical symbol String.
	 */
	private String makeChemicalSymbol(char letter) {
		String symbol = Character.toString(letter).toUpperCase();
		return symbol;
	}
	
	/**
	 * Makes a chemical symbol from a char array.
	 *
	 * @param letters
	 * The letters to convert.
	 *
	 * @returns
	 * The chemical symbol.
	 */
	private String makeChemicalSymbol(char[] letters) {
		StringBuffer symbol = new StringBuffer();
		
		for(int i = 0; i < letters.length; i++) {
			String l = Character.toString(letters[i]);
			
			symbol.append( ((i == 0) ? l.toUpperCase() : l) );
		}
		
		return symbol.toString();
	}
	
	/**
	 * Checks if an element with a given chemical symbol exists.
	 *
	 * @param symbol
	 * The symbol to check for.
	 *
	 * @returns
	 * True if an element with that symbol exists; false otherwise.
	 */
	private boolean elementWithSymbolExists(String symbol) {
		return ElementList.containsElement(symbol);
	}
}
