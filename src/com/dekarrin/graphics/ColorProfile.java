package com.dekarrin.graphics;

/**
 * Data for an ICC color profile.
 */
public class ColorProfile {
	
	/**
	 * The data of the profile.
	 */
	public byte[] data;
	
	/**
	 * The name of the profile.
	 */
	public String name;
	
	/**
	 * Creates a new ICCP.
	 *
	 * @param data
	 * The data in the ICCP.
	 *
	 * @param name
	 * The name of the ICCP.
	 */
	public ColorProfile(String name, byte[] data) {
		this.name = name;
		this.data = data;
	}
}