package com.dekarrin.util;

/**
 * Holds int primitives and grows if the limit of the array
 * is hit. Even though all PrimitiveHolder-implementing classes
 * already are able to grow via the resize() method, and this
 * class' parent implements PrimitiveHolder, Growable classes
 * are able to automatically grow when necessary. This almost
 * makes the resize() method seem obsolete, but it can (and
 * should) still be used for manual resizing operations.
 */
public class GrowableIntHolder extends IntHolder implements Growable {
	
	/**
	 * The growth factor of this GrowableIntHolder. This is
	 * how much it will increase the internal array's size
	 * when it runs out of room.
	 */
	private double growthFactor = 0.5;
	
	/**
	 * The number of elements in this GrowableIntHolder that
	 * were set to a value.
	 */
	private int size = 0;
	
	/**
	 * Creates a new GrowableIntHolder.
	 *
	 * @param size
	 * The size of the internal storage array.
	 *
	 * @param growthFactor
	 * How much to grow by whenever the GrowableIntHolder must
	 * be grown.
	 */
	public GrowableIntHolder(int size, double growthFactor) {
		super(size);
		this.growthFactor = growthFactor;
	}
	
	/**
	 * Creates a new GrowableIntHolder with the default
	 * growth behavoir of 50% increase.
	 *
	 * @param size
	 * The size of the internal storage array.
	 */
	public GrowableIntHolder(int size) {
		super(size);
	}
	
	/**
	 * Inserts an int at an index. If the index is greater
	 * than the current position, the position is changed to
	 * the index + 1. If the index is greater than the
	 * current largest index of the internal storage array,
	 * then the internal storage array is resized to make
	 * its largest index greater than the given index by a
	 * factor equival
	 *
	 * @param data
	 * The int to be inserted.
	 *
	 * @param index
	 * The index to insert it at.
	 */
	@Override
	public void set(int data, int index) {
		checkIndexValidity(index);
		super.set(data, index);
		fitSizeToIndex(index);
	}
	
	/**
	 * Inserts an int at the current position of the holder.
	 * The int is inserted and the position pointer is
	 * incremented by 1. If the current position is greater
	 * than the current largest index of the internal
	 * storage array, then the internal storage array is
	 * resized.
	 *
	 * @param data
	 * The int to be inserted.
	 */
	@Override
	public void add(int data) {
		checkIndexValidity(position() + 1);
		super.add(data);
		fitSizeToIndex(position());
	}
	
	/**
	 * Inserts ints at the current position of the holder.
	 * The ints are inserted and the position pointer is
	 * incremented by the length of the given array. If the
	 * current position plus the length of the given data
	 * array is greater than the current largest index of
	 * the internal storage array, then the internal
	 * storage array is resized.
	 *
	 * @param data
	 * The ints to be inserted.
	 */
	@Override
	public void add(int[] data) {
		checkIndexValidity(position() + data.length);
		super.add(data);
		fitSizeToIndex(position());
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
	 * True if inserting another int to this array at any
	 * point beyond the current index would result in the
	 * internal storage array being grown; false otherwise.
	 */
	@Override
	public boolean isAtEnd() {
		return super.isAtEnd();
	}
	
	/**
	 * Gets the number of elements stored in this
	 * GrowableIntHolder. This will correspond to the
	 * greatest index that had an integer assigned to it,
	 * whether set explicitly using set(int, int), or
	 * implicitly using add(int) or add(int[]). Note that
	 * this is not necessarily the number of elements that
	 * were added to this GrowableIntHolder externally; if
	 * indices are skipped when assigning a value, the
	 * skipped indices are considered to be set to their
	 * default values.
	 *
	 * @return
	 * The number of elements in this GrowableIntHolder.
	 */
	@Override
	public int size() {
		return size;
	}
	
	/**
	 * Gets all the elements of the internal array that have
	 * been set. An element is considered to be set if its
	 * index is less than or equal to the size() of this
	 * GrowableIntHolder.
	 *
	 * @return
	 * The set ints of this GrowableIntHolder.
	 */
	@Override
	public int[] toArray() {
		int[] setValues = Arrays.copyOfRange(holder, 0, size);
		return setValues;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resize(int size) {
		super.resize(size);
		if(size > holder.length) {
			size = holder.length;
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