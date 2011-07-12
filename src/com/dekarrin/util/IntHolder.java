package com.dekarrin.util;

import java.util.Arrays;

/**
 * Holds ints and remembers its current location.
 */
public class IntHolder implements PrimitiveHolder {
	
	/**
	 * The internal array for holding elements.
	 */
	protected int[] holder;
	
	/**
	 * The pointer for remembering the current position.
	 */
	protected int position = 0;
	
	/**
	 * Creates a new IntHolder of a specified size.
	 *
	 * @param size
	 * The size of the int holder.
	 */
	public IntHolder(int size) {
		holder = new int[size];
	}
	
	/**
	 * Creates a new IntHolder from an existing array.
	 *
	 * @param array
	 * The array to create the IntHolder from.
	 */
	public IntHolder(int[] array) {
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
		if(index > position) {
			position = index + 1;
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
		holder[position++] = data;
	}
	
	/**
	 * Inserts ints at the end of the holder. The ints are
	 * inserted and the position pointer is incremented by
	 * the length of the given array.
	 *
	 * @param data
	 * The ints to be inserted.
	 */
	public void add(int[] data) {
		for(int i = 0; i < data.length; i++) {
			add(data[i]);
		}
	}
	
	/**
	 * Gets the int at the specified index.
	 *
	 * @param index
	 * The index to retrieve the int from.
	 *
	 * @return
	 * The specified int.
	 */
	public int get(int index) {
		return holder[index];
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void resize(int size) {
		holder = Arrays.copyOfRange(holder, 0, size);
		if(position > holder.length) {
			reset();
		}
	}
	
	/**
	 * Gets the entire internal array.
	 *
	 * @return
	 * The array.
	 */
	public int[] toArray() {
		return holder;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int position() {
		return position;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void move(int index) {
		position = index;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void reset() {
		move(0);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isAtEnd() {
		return (position == holder.length);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return holder.length;
	}
	
	/**
	 * Gets the next int.
	 *
	 * @return
	 * The int.
	 */
	public int next() {
		int nextInt = holder[position++];
		return nextInt;
	}
}