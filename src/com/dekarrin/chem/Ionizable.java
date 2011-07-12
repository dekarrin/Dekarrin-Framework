package com.dekarrin.chem;

/**
 * Interface for chemical ions.
 */
public interface Ionizable {
	
	/**
	 * Checks if this Ionizable has more than one possible
	 * charge.
	 *
	 * @return
	 * True if this Ionizable has multiple charges; false otherwise.
	 */
	public boolean hasMultipleCharges();
	
	/**
	 * Gets the most common charge.
	 *
	 * @return
	 * The most common charge.
	 */
	public int getPrimaryCharge();
	
	/**
	 * Gets all charges.
	 *
	 * @return
	 * An array containing all the charges.
	 */
	public int[] getCharges();
	
	/**
	 * Sets this to use a charge. If the charge is not a possible
	 * charge, an exception is thrown.
	 *
	 * @param newCharge
	 * The charge to set this Ionizable to use.
	 *
	 * @throws ChargeOutOfBoundsException
	 * If the charge is not possible with this Ionizable.
	 */	
	public void setUsedCharge(int newCharge) throws ChargeOutOfBoundsException;
	
	/**
	 * Checks which charge is currently being used.
	 *
	 * @return
	 * The used charge.
	 *
	 * @throws ChargeNotSetException
	 * If a charge has not yet been set.
	 */
	public int getUsedCharge()/* throws ChargeNotSetException*/;
	
	/**
	 * Checks if the charge has been set yet.
	 *
	 * @return
	 * true if the used charge has been set, false otherwise.
	 */
	public boolean chargeIsSet();
	
	public double getMass();
	public String getMassString();
}
