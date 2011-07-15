package com.dekarrin.util;

import java.util.Arrays;

/**
 * Holds int primitives and grows if the limit of the array
 * is hit. Even though all PrimitiveHolder-implementing classes
 * already are able to grow via the resize() method, and this
 * class' parent implements PrimitiveHolder, Growable classes
 * are able to automatically grow when necessary. This almost
 * makes the resize() method seem obsolete, but it can (and
 * should) still be used for manual resizing operations.
 */
public class GrowableByteHolder extends ByteHolder implements Growable {
	
	/**
	 * The growth factor of this GrowableByteHolder. This is
	 * how much it will increase the internal array's size
	 * when it runs out of room.
	 */
	private double growthFactor = 0.5;
	
	/**
	 * The number of elements in this GrowableByteHolder that
	 * were set to a value.
	 */
	private int size = 0;
	
	/**
	 * Creates a new GrowableByteHolder.
	 *
	 * @param size
	 * The size of the internal storage array.
	 *
	 * @param growthFactor
	 * How much to grow by whenever the GrowableByteHolder must
	 * be grown.
	 */
	public GrowableByteHolder(int size, double growthFactor) {
		super(size);
		this.growthFactor = growthFactor;
	}
	
	/**
	 * Creates a new GrowableByteHolder with the default
	 * growth behavior of 50% increase.
	 *
	 * @param size
	 * The size of the internal storage array.
	 */
	public GrowableByteHolder(int size) {
		super(size);
	}
	
	/**
	 * Inserts a byte at an index. If the index is greater
	 * than the current position, the position is changed to
	 * the index + 1. If the index is greater than the
	 * current largest index of the internal storage array,
	 * then the internal storage array is resized to make
	 * its largest index greater than the given index by the
	 * growth factor.
	 *
	 * @param data
	 * The byte to be inserted.
	 *
	 * @param index
	 * The index to insert it at.
	 */
	@Override
	public void set(byte data, int index) {
		checkIndexValidity(index);
		super.set(data, index);
		fitSizeToIndex(index);
	}
	
	/**
	 * Inserts an byte at the current position of the holder.
	 * The byte is inserted and the position pointer is
	 * incremented by 1. If the current position is greater
	 * than the current largest index of the internal
	 * storage array, then the internal storage array is
	 * resized.
	 *
	 * @param data
	 * The int to be inserted.
	 */
	@Override
	public void add(byte data) {
		checkIndexValidity(position());
		super.add(data);
		fitSizeToIndex(position()-1);
	}
	
	/**
	 * Inserts byte at the current position of the holder.
	 * The bytes are inserted and the position pointer is
	 * incremented by the length of the given array. If the
	 * current position plus the length of the given data
	 * array is greater than the current largest index of
	 * the internal storage array, then the internal
	 * storage array is resized.
	 *
	 * @param data
	 * The bytes to be inserted.
	 */
	@Override
	public void add(byte[] data) {
		int finalIndex = position() + data.length - 1;
		checkIndexValidity(finalIndex);
		super.add(data);
		fitSizeToIndex(finalIndex);
	}
	
	/**
	 * {@inheritDoc}
	 *
	 * Since this class represents an automatically growing
	 * array, it will never have an actual 'end'; instead,
	 * this method checks whether or not inserting an
	 * element at any index beyond the current position
	 * would result in a growth operation.
	 *
	 * @return
	 * True if inserting another byte to this array at any
	 * point beyond the current index would result in the
	 * internal storage array being grown; false otherwise.
	 */
	@Override
	public boolean isAtEnd() {
		return super.isAtEnd();
	}
	
	/**
	 * Gets the number of elements stored in this
	 * GrowableByteHolder. This will correspond to the
	 * greatest index that had an integer assigned to it,
	 * whether set explicitly using set(byte, int), or
	 * implicitly using add(byte) or add(byte[]). Note that
	 * this is not necessarily the number of elements that
	 * were added to this GrowableIntHolder externally; if
	 * indices are skipped when assigning a value, the
	 * skipped indices are considered to be set to their
	 * default values.
	 *
	 * @return
	 * The number of elements in this GrowableByteHolder.
	 */
	@Override
	public int size() {
		return size;
	}
	
	/**
	 * Gets all the elements of the internal array that have
	 * been set. An element is considered to be set if its
	 * index is less than or equal to the size() of this
	 * GrowableByteHolder.
	 *
	 * @return
	 * The set byte of this GrowableByteHolder.
	 */
	@Override
	public byte[] toArray() {
		byte[] setValues = Arrays.copyOfRange(holder, 0, size);
		return setValues;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resize(int size) {
		super.resize(size);
		if(this.size > holder.length) {
			this.size = holder.length;
		}
	}
	
	/**
	 * Gets the storage size of this GrowableIntHolder. This
	 * represents the maximum number of ints that can be
	 * stored in this GrowableIntHolder without having to
	 * resize the array.
	 *
	 * @return
	 * The number of elements that can be stored in this
	 * GrowableIntHolder.
	 */
	public int capacity() {
		return super.size();
	}
	
	/**
	 * Changes the growth factor. The new growth factor will
	 * be applied the next time a growth operation takes place.
	 *
	 * @param growthFactor
	 * The growth factor to use. This must be greater than 0.
	 * Note that the new array size is not
	 * oldArraySize * growthFactor; it's actually oldArraySize
	 * + (oldArraySize * growthFactor), so the actual
	 * factor for the new size is really 1 + growthFactor.
	 */
	public void setGrowthFactor(double growthFactor) {
		this.growthFactor = growthFactor;
	}
	
	/**
	 * Checks if a given index is within the bounds of the
	 * size of this GrowableIntHolder. If it is not, the size
	 * is adjusted so as to allow the given index to fit within
	 * the bounds.
	 *
	 * @param index
	 * The index to make the size fit to.
	 */
	private void fitSizeToIndex(int index) {
		int requiredSize = index+1;
		if(requiredSize > size) {
			size = requiredSize;
		}
	}
	
	/**
	 * Checks if a given index is within the bounds of the
	 * holder array. If it is not, the holder array is grown.
	 *
	 * @param index
	 * The index to fit the internal array to.
	 */
	private void checkIndexValidity(int index) {
		int requiredSize = index+1;
		while(requiredSize > holder.length) {
			grow();
		}
	}
	
	/**
	 * Grows the internal storage array. The new size of the
	 * array is calculated by taking the current size and
	 * multiplying it by the growthFactor; this is the size
	 * delta of the of array. This is then added to the current
	 * size.
	 */
	private void grow() {
		int lengthDelta = (int)(holder.length * growthFactor);
		int newLength = holder.length + lengthDelta;
		resize(newLength);
	}
}