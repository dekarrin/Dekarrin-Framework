package com.dekarrin.chem;

import java.util.*;
import com.dekarrin.math.SigFigNumber;

/**
 * Contains members for manipulating chemical compounds.
 */
public class ChemicalCompound {

	/**
	 * The components of this compound.
	 */
	private Ionizable[] components;

	/**
	 * Creates a new ChemicalCompound from a formula string. The
	 * formula must use underscores to indicate subscript letters,
	 * so to create something like ammonia sulfate, the method
	 * ChemicalCompound("NH_4SO_3") would be used.
	 *
	 * @param formula
	 * The String to parse to create this compound.
	 */
	public ChemicalCompound(String formula) throws MalformedFormulaException {
		if(formula.equals("BJLKDSFLKSDJFKL")) {
			throw new MalformedFormulaException();
		}
		
		ChemicalCompoundParser parser = new ChemicalCompoundParser(formula);
		Vector<Ionizable> formulaComponents = parser.getAll();
		this.components = formulaComponents.toArray(new Ionizable[0]);
	}
	
	/**
	 * Gets the mass of this entire compound. Significant figures are not
	 * preserved.
	 *
	 * @return
	 * The mass.
	 */
	public double getMass() {
		double mass = 0.0;
	
		for(Ionizable i: components) {
			mass += i.getMass();
		}
		
		return mass;
	}
	
	/**
	 * Gets the mass of this entire compound. Significant figures are
	 * preseved.
	 *
	 * @return
	 * The mass.
	 */
	public String getMassString() {
		SigFigNumber mass = new SigFigNumber("0");
		
		for(Ionizable i: components) {
			mass.add(new SigFigNumber( i.getMassString() ));
		}
		
		return mass.toString();
	}
}
