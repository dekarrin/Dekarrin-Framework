package com.dekarrin.util;

import java.util.Arrays;

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
	 * Inserts bytes at the end of the holder. The bytes are
	 * inserted and the position pointer is incremented by
	 * the length of the given array.
	 *
	 * @param data
	 * The bytes to be inserted.
	 */
	public void add(byte[] data) {
		for(int i = 0; i < data.length; i++) {
			add(data[i]);
		}
	}
	
	/**
	 * Gets the byte at the specified index.
	 *
	 * @param index
	 * The index to retrieve the byte from.
	 *
	 * @return
	 * The specified byte.
	 */
	public byte get(int index) {
		return holder[index];
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void resize(int size) {
		holder = Arrays.copyOfRange(holder, 0, size);
		if(pointer > holder.length) {
			reset();
		}
	}
	
	/**
	 * Gets the entire internal array.
	 *
	 * @return
	 * The array.
	 */
	public byte[] toArray() {
		return holder;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int position() {
		return pointer;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void move(int index) {
		pointer = index;
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
		return (pointer == holder.length);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int size() {
		return holder.length;
	}
	
	/**
	 * Gets the next byte.
	 *
	 * @return
	 * The byte.
	 */
	public byte next() {
		byte nextByte = holder[pointer++];
		return nextByte;
	}
}