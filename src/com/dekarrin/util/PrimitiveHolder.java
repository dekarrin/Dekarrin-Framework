package com.dekarrin.util;

/**
 * An array of primitives that keeps track of its position.
 */
public interface PrimitiveHolder {
	
	/**
	 * Resizes the internal array. If the new size is smaller
	 * than the current size, any element an index greater
	 * than the new size is dropped. After the new size is
	 * set, the pointer is reset to 0 if it exceeds the new
	 * size.
	 *
	 * @param size
	 * The new size.
	 */
	public void resize(int size);
	
	/**
	 * Gets the position of the internal pointer.
	 *
	 * @return
	 * The current position.
	 */
	public int position();
	
	/**
	 * Sets the internal pointer to a position.
	 *
	 * @param index
	 * The new index to set the pointer to.
	 */
	public void move(int index);
	
	/**
	 * Resets the internal pointer to 0.
	 */
	public void reset();
	
	/**
	 * Checks if the internal pointer is at the end of the
	 * internal array.
	 *
	 * @return
	 * True if the pointer is at the end; false otherwise.
	 */
	public boolean isAtEnd();
	
	/**
	 * Gets the size of the internal array.
	 *
	 * @return
	 * The size.
	 */
	public int size();
}