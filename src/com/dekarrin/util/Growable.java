package com.dekarrin.util;

/**
 * Implemented by classes containing data structures that are
 * allowed to grow.
 */
interface Growable {
	
	/**
	 * Gets the current size of this Growable. This is the size
	 * that an array containing the same number of elements as
	 * this Growable would be.
	 *
	 * @return
	 * The number of elements in this Growable.
	 */
	public int size();
	
	/**
	 * Gets the number of elements that can be stored in this
	 * Growable. If this number is exceeded, than this Growable
	 * will expand in order to be able to add more elements.
	 *
	 * @return
	 * The number of items that can be stored in this Growable
	 * withit having to resize.
	 */
	public int capacity();
	
	/**
	 * Manually changes the size of the data structure. The data
	 * structure is re-allocated to the specified number of
	 * elements, but existing elements remain intact.
	 *
	 * @param size
	 * The new size to set the structure to. If this is smaller
	 * than the current size of the structure, any elements stored
	 * at indices greater than or equal to the new size are dropped.
	 */
	public void resize(int size);
	
	/**
	 * Sets the growth factor of this Growable. The growth factor
	 * is controls how much the data structure will expand by when
	 * it becomes necessary to automatically increase its size.
	 * Its new size after regrowing will be its current size, plus
	 * its current size times the growth factor.
	 *
	 * @param factor
	 * What to set the growth factor to. This can be any value
	 * greater than 0, but for all practical purposes is usually
	 * less than 1.
	 */
	public void setGrowthFactor(double factor);
}