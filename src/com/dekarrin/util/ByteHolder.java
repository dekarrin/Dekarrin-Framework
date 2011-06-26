package com.dekarrin.util;

/**
 * Holds bytes and remembers its current location.
 */
public class ByteHolder implements PrimitiveHolder {
	
	/**
	 * The internal array for holding elements.
	 */
	private byte[] holder;
	
	/**
	 * The pointer for remembering the current position.
	 */
	private int pointer = 0;
	
	/**
	 * Creates a new ByteHolder of a specified size.
	 *
	 * @param size
	 * The size of the byte holder.
	 */
	public ByteHolder(int size) {
		holder = new byte[size];
	}
	
	/**
	 * Creates a new ByteHolder from an existing array.
	 *
	 * @param array
	 * The array to create the ByteHolder from.
	 */
	public ByteHolder(byte[] array) {
		holder = array;
	}
	
	/**
	 * Inserts a byte at an index. If the index is greater
	 * than the current position, the position is changed to
	 * the index + 1.
	 *
	 * @param data
	 * The byte to be inserted.
	 *
	 * @param index
	 * The index to insert it at.
	 */
	public void set(byte data, int index) {
		holder[index] = data;
		if(index > pointer) {
			pointer = index + 1;
		}
	}
	
	/**
	 * Inserts a byte at the end of the holder. The byte is
	 * inserted and the position pointer is incremented by 1.
	 *
	 * @param data
	 * The byte to be inserted.
	 */
	public void add(byte data) {
		holder[pointer++] = data;
	}
	
	/**
	 * Gets the byte at the specified index.
	 *
	 * @param index
	 * The index to retrieve the byte from.
	 *
	 * @returns
	 * The specified byte.
	 */
	public byte get(int index) {
		return holder[index];
	}
	
	/**
	 * Gets the entire internal array.
	 *
	 * @returns
	 * The array.
	 */
	public byte[] toArray() {
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
	 * Gets the size of this ByteHolder.
	 *
	 * @returns
	 * The size.
	 */
	public int size() {
		return holder.length;
	}
	
	/**
	 * Gets the next byte.
	 *
	 * @returns
	 * The byte.
	 */
	public byte next() {
		byte nextByte = holder[pointer++];
		return nextByte;
	}
}