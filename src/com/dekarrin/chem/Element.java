package com.dekarrin.chem;

import com.dekarrin.math.SigFigNumber;

/**
 * Represents a single element.
 */
public class Element {
	
	/**
	 * The name of this element.
	 */
	private String name;
	
	/**
	 * The symbol of this element.
	 */
	private String symbol;
	
	/**
	 * The number of protons in this element. This also serves
	 * as the atomic number.
	 */
	private int protons;
	
	/**
	 * The number of neutrons in the most abundant naturally-occuring
	 * isotope of this element. If there is no naturally-occuring
	 * isotope, then this is the number of neutrons in the most stable
	 * synthetic isotope.
	 */
	private int neutrons;
	
	/**
	 * The mass of this element.
	 */
	private SigFigNumber mass;
	
	/**
	 * The electronegativity of this element.
	 */
	private SigFigNumber electronegativity;
	
	/**
	 * The possible charges of this element, in order of most common to
	 * least common.
	 */
	private int[] charges;
	
	/**
	 * Creates a new Element.
	 */
	public Element(String name, String symbol, int protons,
			int neutrons, SigFigNumber mass,
			SigFigNumber electronegativity, int[] charges) {
			
		this.name = name;
		this.symbol = symbol;
		this.protons = protons;
		this.neutrons = neutrons;
		this.mass = mass;
		this.electronegativity = electronegativity;
		this.charges = charges;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public int getProtons() {
		return protons;
	}
	
	public int getNeutrons() {
		return neutrons;
	}
	
	public double getMass() {
		return mass.getNumericalValue();
	}
	
	public String getMassString() {
		return mass.toString();
	}
	
	public double getElectronegativity() {
		return electronegativity.getNumericalValue();
	}
	
	public String getElectronegativityString() {
		return electronegativity.toString();
	}
	
	public int[] getCharges() {
		return charges;
	}
	
	public boolean hasCharges() {
		if(charges.length > 0) {
			return true;
		} else {
			return false;
		}
	}
}
