package com.dekarrin.util;

/**
 * Holds ints and remembers its current location.
 */
public class IntHolder implements PrimitiveHolder {
	
	/**
	 * The internal array for holding elements.
	 */
	private int[] holder;
	
	/**
	 * The pointer for remembering the current position.
	 */
	private int pointer = 0;
	
	/**
	 * Creates a new IntHolder of a specified size.
	 *
	 * @param size
	 * The size of the int holder.
	 */
	public class IntHolder(int size) {
		holder = new int[size];
	}
	
	/**
	 * Creates a new IntHolder from an existing array.
	 *
	 * @param array
	 * The array to create the IntHolder from.
	 */
	public class IntHolder(int[] array) {
		holder = array;
	}
	
	/**
	 * Inserts an int at an index. If the index is greater
	 * than the current position, the position is changed to
	 * the index + 1.
	 *
	 * @param data
	 * The int to be inserted.
	 *
	 * @param index
	 * The index to insert it at.
	 */
	public void set(int data, int index) {
		holder[index] = data;
		if(index > pointer) {
			pointer = index + 1;
		}
	}
	
	/**
	 * Inserts an int at the end of the holder. The int is
	 * inserted and the position pointer is incremented by 1.
	 *
	 * @param data
	 * The int to be inserted.
	 */
	public void add(int data) {
		holder[pointer++] = data.
	}
	
	/**
	 * Gets the int at the specified index.
	 *
	 * @param index
	 * The index to retrieve the int from.
	 *
	 * @returns
	 * The specified int.
	 */
	public int get(int index) {
		return holder[index];
	}
	
	/**
	 * Gets the entire internal array.
	 *
	 * @returns
	 * The array.
	 */
	public int[] toArray() {
		return holder;
	}
	
	/**
	 * Gets the position of the internal pointer.
	 *
	 * @returns
	 * The current position.
	 */
	public int position() {
		return pointer;
	}
	
	/**
	 * Sets the internal pointer to a position.
	 *
	 * @param index
	 * The new index to set the pointer to.
	 */
	public void move(int index) {
		pointer = index;
	}
	
	/**
	 * Resets the internal pointer to 0.
	 */
	public void reset() {
		move(0);
	}
	
	/**
	 * Checks if the pointer is at the end of the array.
	 *
	 * @returns
	 * True if the pointer is at the end; false otherwise.
	 */
	public boolean isAtEnd() {
		return (pointer == holder.length);
	}
	
	/**
	 * Gets the size of this IntHolder.
	 *
	 * @returns
	 * The size.
	 */
	public int size() {
		return holder.length;
	}
	
	/**
	 * Gets the next int.
	 *
	 * @returns
	 * The int.
	 */
	public int next() {
		int nextInt = holder[pointer++];
		return nextInt;
	}
}