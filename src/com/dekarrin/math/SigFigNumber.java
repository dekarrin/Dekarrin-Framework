package com.dekarrin.math;

import org.ostermiller.util.SignificantFigures;

/**
 * Wrapper class for Ostermiller's SignificantFigures class.
 */
public class SigFigNumber {

	/**
	 * The value of this number as a SigFig.
	 */
	private SignificantFigures value;
	
	/**
	 * Creates a new number for using sigfigs.
	 *
	 * @param number
	 * The number to start this sigfig as.
	 */
	public SigFigNumber(String number) {
		value = new SignificantFigures(number);
	}
	
	/**
	 * Adds a SigFigNumber to this one using sigfig math. This
	 * method will not change the SigFigNumber passed in, only
	 * this SigFigNumber.
	 *
	 * @param number
	 * The number to be added to this one. This must be another
	 * SigFigNumber.
	 */
	public void add(SigFigNumber number) {
		int sigfigPlace = Math.max(getLowestSigfigPlace(), number.getLowestSigfigPlace());
		double newValue = getNumericalValue() + number.getNumericalValue();
		value = new SignificantFigures(newValue);
		value.setLSD(sigfigPlace);
	}
	
	/**
	 * Subtracts a SigFigNumber from this one using sigfig math.
	 * This method will not change the SigFigNumber passed in, only
	 * this SigFigNumber.
	 *
	 * @param number
	 * The number to be subtracted from this one. This must be another
	 * SigFigNumber.
	 */
	public void subtract(SigFigNumber number) {
		int sigfigPlace = Math.max(getLowestSigfigPlace(), number.getLowestSigfigPlace());
		double newValue = getNumericalValue() - number.getNumericalValue();
		value = new SignificantFigures(newValue);
		value.setLSD(sigfigPlace);
	}
	
	/**
	 * Multiplies a sigfig with this one using sigfig math. This
	 * method will not change the SigFigNumber passed in, only this
	 * SigFigNumber.
	 *
	 * @param number
	 * The number to be multiplied with this one. This must be another
	 * SigFigNumber.
	 */
	public void multiply(SigFigNumber number) {
		int sigfigCount = Math.min(getSigfigCount(), number.getSigfigCount());
		double newValue = getNumericalValue() * number.getNumericalValue();
		value = new SignificantFigures(newValue);
		value.setNumberSignificantFigures(sigfigCount);
	}
	
	/**
	 * Divides this SigFigNumber by another using sigfig math. This
	 * method will not change the SigFigNumber passed in, only this
	 * SigFigNumber.
	 *
	 * @param number
	 * The number to divide this one by. This must be another SigFigNumber.
	 */
	public void divide(SigFigNumber number) {
		int sigfigCount = Math.min(getSigfigCount(), number.getSigfigCount());
		double newValue = getNumericalValue() / number.getNumericalValue();
		value = new SignificantFigures(newValue);
		value.setNumberSignificantFigures(sigfigCount);
	}
	
	/**
	 * Gets the place of the lowest sigfig. Gets the place according to the
	 * exponent of 10 at that place. For example, if the lowest sigfig is
	 * in the tens' place, 1 will be returned, because the tens place is
	 * 10^1. For the thousands' place, 3 will be returned, because it is
	 * 10^3.
	 *
	 * @return
	 * The exponent required to raise 10 to to get the place of the lowest
	 * sigfig.
	 */
	public int getLowestSigfigPlace() {
		return value.getLSD();
	}
	
	/**
	 * Gets the number of sigfigs in this SigFigNumber.
	 *
	 * @return
	 * The number of sigfigs.
	 */
	public int getSigfigCount() {
		return value.getNumberSignificantFigures();
	}
	
	/**
	 * Gets the numerical value of this SigFigNumber.
	 *
	 * @return
	 * The value of this SigFigNumber.
	 */
	public double getNumericalValue() {
		return value.doubleValue();
	}
	
	/**
	 * Gets the string value of this SigFigNumber.
	 *
	 * @return
	 * The string value.
	 */
	public String toString() {
		return value.toString();
	}
}
