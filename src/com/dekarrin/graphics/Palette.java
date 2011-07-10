package com.dekarrin.graphics;

import java.util.Arrays;

/**
 * Holds palettes of colors. Their associated alpha values
 * as well as the frequency of each is contained within.
 */
public class Palette {
	
	/**
	 * The name of this Palette.
	 */
	private String name;
	
	/**
	 * The colors in this palette.
	 */
	private Color[] colors = new Color[0];
	
	/**
	 * The frequencies of this Palette.
	 */
	private int[] frequencies = new int[0];
	
	/**
	 * Whether frequencies hold any meaning.
	 */
	private boolean frequenciesAreSet = false;
	
	/**
	 * The sample depth of this palette.
	 */
	private int sampleDepth;
	
	/**
	 * Creates a new Palette with only a name.
	 *
	 * @param name
	 * The name of the palette.
	 *
	 * @param sampleDepth
	 * The sample depth of the palette.
	 */
	public Palette(String name, int sampleDepth) {
		this.name = name;
		this.sampleDepth = sampleDepth;
	}
	
	/**
	 * Creates a new Palette with a name and a set of Colors.
	 * The frequencies are assumed to be 0.
	 *
	 * @param name
	 * The name of the palette.
	 *
	 * @param sampleDepth
	 * The sample depth fo the palette.
	 *
	 * @param colors
	 * The colors in the palette.
	 */
	public Palette(String name, int sampleDepth, Color[] colors) {
		this.name = name;
		this.sampleDepth = sampleDepth;
		for(Color c: colors) {
			addColor(c);
		}
	}
	
	/**
	 * Creates a new Palette with a name, a set of colors, and
	 * frequencies. If there are fewer frequencies than colors,
	 * the remaining colors are assumed to have a frequency of
	 * 0.
	 *
	 * @param name
	 * The name of the palette.
	 *
	 * @param sampleDepth
	 * The sample depth
	 *
	 * @param colors
	 * The colors in the palette.
	 *
	 * @param frequencies
	 * The frequencies of each of the colors in the set.
	 */
	public Palette(String name, int sampleDepth, Color[] colors, int[] frequencies) {
		this.name = name;
		this.sampleDepth = sampleDepth;
		int i = 0;
		for(; i < frequencies.length && i < colors.length; i++) {
			addColor(colors[i], frequencies[i]);
		}
		for(; i < colors.length; i++) {
			addColor(colors[i]);
		}
	}
	
	/**
	 * Gets the name of this palette.
	 *
	 * @return
	 * The name of this Palette.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the sample depth of this palette.
	 *
	 * @return
	 * The sample depth.
	 */
	public int getSampleDepth() {
		return sampleDepth;
	}
	
	/**
	 * Gets the number of colors in this palette.
	 *
	 * @return
	 * The number of colors.
	 */
	public int size() {
		return colors.length;
	}
	
	/**
	 * Gets a color from this Palette.
	 *
	 * @param index
	 * The index of the color to get.
	 *
	 * @return
	 * The color at the index.
	 */
	public Color getColor(int index) {
		return colors[index];
	}
	
	/**
	 * Gets a frequency from this Palette.
	 *
	 * @param index
	 * The index of the frequency to get.
	 *
	 * @return
	 * The frequency at the index.
	 */
	public int getFrequency(int index) {
		return frequencies[index];
	}
	
	/**
	 * Checks if this Palette has meaningful frequencies.
	 *
	 * @return
	 * Whether the frequencies are meaningful.
	 */
	public boolean hasFrequencies() {
		return frequenciesAreSet;
	}
	
	/**
	 * Adds a color to this Palette. The frequency is assumed
	 * to be 0.
	 *
	 * @param color
	 * The color to add.
	 */
	public void addColor(Color color) {
		increaseLength(1);
		insertColor(color, 0);
	}
	
	/**
	 * Adds a color to this Palette with a specified frequency.
	 *
	 * @param color
	 * The color to add.
	 *
	 * @param frequency
	 * The frequency to add.
	 */
	public void addColor(Color color, int frequency) {
		increaseLength(1);
		frequenciesAreSet = true;
		insertColor(color, frequency);
	}
	
	/**
	 * Gets all the colors in this Palette.
	 *
	 * @return
	 * All the colors.
	 */
	public Color[] getAllColors() {
		return colors;
	}
	
	/**
	 * Gets all the frequencies in this Palette.
	 *
	 * @return
	 * All the frequencies.
	 */
	public int[] getAllFrequencies() {
		return frequencies;
	}
	
	/**
	 * Increases the length of the color and frequency arrays.
	 *
	 * @param growthSize
	 * The amount that each should grow by.
	 */
	private void increaseLength(int growthSize) {
		colors = Arrays.copyOfRange(colors, 0, colors.length + growthSize);
		frequencies = Arrays.copyOfRange(frequencies, 0, frequencies.length + growthSize);
	}
	
	/**
	 * Inserts a color at the end of the internal arrays.
	 *
	 * @param color
	 * The color to add.
	 *
	 * @param frequency
	 * The frequency of the color.
	 */
	private void insertColor(Color color, int frequency) {
		colors[colors.length-1] = color;
		frequencies[frequencies.length-1] = frequency;
	}
}