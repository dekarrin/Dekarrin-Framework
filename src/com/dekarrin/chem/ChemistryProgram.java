package com.dekarrin.chem;

import com.dekarrin.file.csv.CsvFile;
import com.dekarrin.io.*;
import com.dekarrin.math.SigFigNumber;
import com.dekarrin.util.*;
import com.dekarrin.cli.*;
import com.dekarrin.program.*;
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
		ChemistryProgram program = new ChemistryProgram(args);
		program.showIntro();
		program.startLoop();
	}
	
	/**
	 * Creates a new instance of ChemistryProgram.
	 */
	public ChemistryProgram(String[] args) {
		super(args);
		addCommands();
		setShutdownMessage("Goodbye");
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
		ArgumentDescription[] args;
		FlagDescription[] flags;
		
		args	= new ArgumentDescription[]{new ArgumentDescription("word", "The word to be spelled")};
		flags	= new FlagDescription[]{new FlagDescription("l", "Shows the list of elements that make up the word")};
		addCommandDefinition("spell", "Spells a word with element symbols", args, flags);
		
		args	= new ArgumentDescription[]{new ArgumentDescription("element", "The element to get info on")};
		addCommandDefinition("info", "Gets info on an element", args);
	}
	
	/**
	 * Processes a command and gives output.
	 *
	 * @param command
	 * The Command created by the user-entered command.
	 *
	 * @return
	 * The output for whatever command was entered.
	 */
	protected String processCommand(Command command) {
		String output = null;
		String name = command.getName();
		if(name.equals("spell")) {
				output = spellWord(command.getArgument("word"), command.hasFlag("l"));
		} else if(name.equals("info")) {
			output = getElementInfo(command.getArgument("element"));
		} else {
			throw new UnrecognizedCommandException(name, "Bad command '"+name+"'");
		}
		
		return output;
	}
	
	/**
	 * Gets information about a single element.
	 * 
	 * @param element
	 * Either a symbol or name of an element.
	 *
	 * @return
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
			HelperString[] unpadded = new HelperString[6];
			unpadded[0] = new HelperString("| "+e.getProtons()+": "+e.getName() + " (" + e.getSymbol() + ")");
			unpadded[1] = new HelperString("| Neutrons: "+e.getProtons());
			unpadded[2] = new HelperString("| Mass: "+e.getMassString());
			unpadded[3] = new HelperString("| Electronegativity: "+e.getElectronegativityString());
			unpadded[4] = new HelperString("|");
			
			for(int i = 0; i < 5; i++) {
				unpadded[i].padRight(27);	
			}
			
			output = "+--------------------------+\n";
			output += unpadded[0].toString() + "|\n";
			output += "+--------------------------+\n";
			output += unpadded[1].toString() + "|\n";
			output += unpadded[2].toString() + "|\n";
			output += unpadded[3].toString() + "|\n";
			output += unpadded[4].toString() + "|\n";
		
			if(e.hasCharges()) {
				int[] charges = e.getCharges();
				String chargeLine = "";
				for(int c: charges) {
					chargeLine += c + ", ";
				}
				chargeLine = chargeLine.substring(0, chargeLine.length()-2);
				HelperString chargeLineDisp = new HelperString(chargeLine + " |");
				chargeLineDisp.padLeft(27);
				output += "|" + chargeLineDisp.toString() + "\n";
			}
		
			output += "+--------------------------+";
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
	 * @return
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
