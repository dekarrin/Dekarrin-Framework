package com.dekarrin.chem;

import com.dekarrin.fileio.*;
import com.dekarrin.io.*;
import com.dekarrin.math.SigFigNumber;
import com.dekarrin.util.*;
import com.dekarrin.program.CliProgram;
import java.util.*;

/**
 * Provides various options for help with chemistry.
 */
public class ChemistryProgram extends CliProgram {
	
	/**
	 * A list of all the elements.
	 */
	private ElementList elements;
	
	/**
	 * Starts the program.
	 */
	public static void main(String[] args) {
		program = new ChemistryProgram();
		program.showIntro();
		program.startLoop();
	}
	
	/**
	 * Creates a new instance of ChemistryProgram.
	 */
	public ChemistryProgram() {
		addCommands();
		loadElements();
	}
	
	/**
	 * Shows intro message.
	 */
	public void showIntro() {
		ui.println("+--------------------------------------------+");
		ui.println("| BohrMaster v1.0 - Chemistry Helper Program |");
		ui.println("| By TF Nelson                               |");
		ui.println("+--------------------------------------------+");
	}
	
	/**
	 * Adds the new commands.
	 */
	private void addCommands() {
		addCommand("spell");
		addDescription("Spells a word with element symbols");
		addSyntax(" {-l} [word]");
		addOptions("l", "Shows a list of elements that make up the word.");
		
		addCommand("info");
		addDescription("Gets info on an element");
		addSyntax(" [element]");
		addOptions();
	}
	
	/**
	 * Processes a command and gives output.
	 */
	protected String processCommand(String[] words, CliSwitchHolder flags) {
		String output = null;
	
		if(words[0].equals("spell")) {
			if(syntaxIsGood(words, 2)) {
				boolean showList = flags.containsSwitch("l");
				output = spellWord(words[1], showList);
			} else {
				output = getBadSyntaxMessage("spell");
			}
		} else if(words[0].equals("info")) {
			if(syntaxIsGood(words, 2)) {
				output = getElementInfo(words[1]);
			} else {
				output = getBadSyntaxMessage("info");
			}
		}
		
		return output;
	}
	
	/**
	 * Gets information about a single element.
	 * 
	 * @param element
	 * Either a symbol or name of an element.
	 *
	 * @returns
	 * The requested element's datasheet.
	 */
	private String getElementInfo(String element) {
		String output = null;
		Element e = null;
		
		if(ElementList.containsElement(element)) {
			e = ElementList.getElement(element);
		} else {
			output = "'" + element + "' is not an element.";
		}
		
		if(e != null) {
			output = "+--------------------------+\n";
			output += StringHelper.padRight("| "+e.getProtons()+": "+e.getName() + " (" + e.getSymbol() + ")", ' ', 27);
			output += "|\n";
			output += "+--------------------------+\n";
			output += StringHelper.padRight("| Neutrons: "+e.getProtons(), ' ', 27);
			output += "|\n";
			output += StringHelper.padRight("| Mass: "+e.getMassString(), ' ', 27);
			output += "|\n";
			output += StringHelper.padRight("| Electronegativity: "+e.getElectronegativityString(), ' ', 27);
			output += "|\n";
			output += StringHelper.padRight("|", ' ', 27) + "|\n";
		
			if(e.hasCharges()) {
				int[] charges = e.getCharges();
				String chargeLine = "";
			
				for(int c: charges) {
					chargeLine += c + ", ";
				}
			
				chargeLine = chargeLine.substring(0, chargeLine.length()-2);
				output += "|" + StringHelper.padLeft(chargeLine + " |", ' ', 27) + "\n";
			}
		
			output += StringHelper.padRight("+", '-', 27) + "+";
		}
		
		return output;
	}
	
	/**
	 * Spells a word using element symbols.
	 *
	 * @param word
	 * The word to spell.
	 *
	 * @param withList
	 * Whether to list the elements that make up the spelled word.
	 *
	 * @returns
	 * The word spelled as a series of chemical symbols if it is possible,
	 * otherwise a message indicating that it cannot be done.
	 */
	private String spellWord(String word, boolean withList) {
		String output;
		
		ElementSpeller speller = new ElementSpeller(word);
		
		if(speller.wordIsPossible()) {
			output = speller.getSpelledWord();
			
			if(withList) {
				output += " - ";
				
				Element[] usedElements = speller.getElements();
				for(Element e: usedElements) {
					output += e.getName() + ", ";
				}
				output = output.substring(0, output.length() - 2);
			}
		} else {
			output = "Cannot spell '" + word + "' with element symbols.";
		}
		
		return output;
	}
	
	/**
	 * Loads the elements from CSV.
	 */
	private void loadElements() {
		CsvFile file = new CsvFile("elements.csv");
		file.read();
		String[][] csvElements = file.getData();
		
		this.elements = new ElementList(csvElements.length);
		
		int k = 0;
		for(String[] csvE: csvElements) {
			int protons = 0;
			int neutrons = 0;
			
			String name = csvE[0];
			String symbol = csvE[1];
			
			try {
				protons = Integer.parseInt(csvE[2]);
				neutrons = Integer.parseInt(csvE[3]);
			} catch(NumberFormatException e) {
				System.err.println("Bad Format!");
			}
			
			SigFigNumber mass = new SigFigNumber(csvE[4]);
			
			SigFigNumber electronegativity;
			if(!csvE[5].equals("-")) {
				electronegativity = new SigFigNumber(csvE[5]);
			} else {
				electronegativity = new SigFigNumber("0");
			}
			
			int[] charges;
			if(!csvE[6].equals("-")) {
				
				String[] ch = csvE[6].split(":");
				charges = new int[ch.length];
			
				int i = 0;
				for(String c: ch) {
					try {
						charges[i++] = Integer.parseInt(c);
					} catch(NumberFormatException e) {
						System.err.println("Bad number format!");
					}
				}
			} else {
				charges = new int[0];
			}
			Element e = new Element(name, symbol, protons, neutrons,
						mass, electronegativity, charges);
			elements.setElement(k++, e);
		}
		elements.lock();
	}
}
