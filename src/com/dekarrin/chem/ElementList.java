package com.dekarrin.chem;

import java.util.*;

/**
 * A list of elements. Made into its own class so that any other class
 * can statically refer to it and get information on any element.
 * This class only needs to be instantiated one time; all fields are
 * decalared private static to make them available to any public static
 * method.
 *
 * Note that this class uses a static internal pointer to show where the
 * next data item should be inserted; care should be taken if this field
 * is being accessed simultaneously by two different threads. Also note
 * that this pointer does not reset unless the class is reinstantiated or
 * it is explicitly instructed to do so via the resetPointer() method.
 */
public class ElementList {
	
	/**
	 * The list of Element objects in this ElementList.
	 */
	private static Element[] list;
	
	/**
	 * The list of Element objects organized by name.
	 */
	private static HashMap<String,Element> nomialList;
	
	/**
	 * The list of Element objects organized by symbol.
	 */
	private static HashMap<String,Element> symbolicList;
	
	/**
	 * The pointer for the next item in the internal list.
	 */
	private static int pointer;
	
	/**
	 * Whether or not this ElementList can be written to.
	 */
	private boolean locked;

	/**
	 * Creates a new ElementList of the specified size. This
	 * will reset all internal lists and the lock if it has
	 * been set.
	 *
	 * @param size
	 * The size to make the new ElementList.
	 */
	public ElementList(int size) {
		ElementList.initialize(size);
	}
	
	/**
	 * Sets the value of the Element at a certain position. If the
	 * position set is greater than the internal pointer of this
	 * ElementList, the pointer is changed to be one more than the
	 * set position.
	 *
	 * This method is not made static so that only the creator of
	 * the object can access it. In addition, it cannot be used once
	 * the lock method is called.
	 *
	 * @param position
	 * The index to change.
	 *
	 * @param value
	 * The Element to set that position to.
	 *
	 * @throws ListLockedException
	 * If this ElementList has been locked.
	 */
	public void setElement(int position, Element value) throws ListLockedException {
		if(listIsLocked()) {
			throw new ListLockedException();
		}
		
		ElementList.list[position] = value;
		nomialList.put(value.getName().toLowerCase(), value);
		symbolicList.put(value.getSymbol().toLowerCase(), value);

		if(ElementList.pointer < position) {
			ElementList.resetPointer(position);
		}
	}
	
	/**
	 * Sets the value of a certain position to null. The pointer
	 * is not affected by this method.
	 *
	 * This method will fail if the list has been locked.
	 *
	 * @param position
	 * The index to reset to.
	 *
	 * @throws ListLockedException
	 * If this ElementList has been locked.
	 */
	public void resetValue(int position) {
		if(!listIsLocked()) {
			Element e = ElementList.list[position];
			
			ElementList.nomialList.remove(e.getName());
			ElementList.symbolicList.remove(e.getSymbol());
			ElementList.list[position] = null;
		} else {
			throw new ListLockedException();
		}
	}
	
	/**
	 * Locks this ElementList so that its contents cannot be changed.
	 */
	public void lock() {
		locked = true;
	}
	
	/**
	 * Checks if this ElementList has been locked.
	 *
	 * @return
	 * True if this ElementList is locked, false otherwise.
	 */
	public boolean listIsLocked() {
		boolean isLocked;
		if(locked == true) {
			isLocked = true;
		} else {
			isLocked = false;
		}
		return isLocked;
	}
	
	/**
	 * Sets the internal pointer to a new position. The call for
	 * the next Element will get this index.
	 *
	 * @param position
	 * The position to set it to.
	 *
	 * @throws NullListException
	 * If this ElementList has not yet been created.
	 */
	public static void resetPointer(int position) throws NullListException {
		if(!ElementList.hasBeenCreated()) {
			throw new NullListException();
		}
		
		ElementList.pointer = position;
	}
	
	/**
	 * Sets the internal pointer to 0.
	 *
	 * @throws NullListException
	 * If this ElementList has not yet been created.
	 */
	public static void resetPointer() throws NullListException {
		if(!ElementList.hasBeenCreated()) {
			throw new NullListException();
		}
	
		ElementList.resetPointer(0);
	}
	
	/**
	 * Gets an Element from the list.
	 *
	 * @param position
	 * The position of the Element in the list.
	 *
	 * @return
	 * The Element at the specified index, or null if no Element
	 * at that index exists.
	 *
	 * @throws NullListException
	 * If this ElementList has not yet been created.
	 */
	public static Element getElement(int position) throws NullListException {
		if(!ElementList.hasBeenCreated()) {
			throw new NullListException();
		}
		
		Element elem;
		
		try {
			elem = ElementList.list[position];
		} catch(IndexOutOfBoundsException e) {
			elem = null;
		}
		
		return elem;
	}
	
	/**
	 * Checks if an Element with a symbol or name exists in this ElementList.
	 *
	 * @param id
	 * The symbol or name to check for. This is not case-sensitive.
	 *
	 * @return
	 * True if an Element with the specified symbol or name exists; false
	 * otherwise.
	 *
	 * @throws NullListException
	 * If the ElementList has not yet been created.
	 */
	public static boolean containsElement(String id) throws NullListException {
		if(!ElementList.hasBeenCreated()) {
			throw new NullListException();
		}
		
		boolean exists;
		if(ElementList.isSymbol(id)) {
			exists = ElementList.symbolicList.containsKey(id.toLowerCase());
		} else {
			exists = ElementList.nomialList.containsKey(id.toLowerCase());
		}
		return exists;
	}
	
	/**
	 * Gets an Element from the list.
	 *
	 * @param id
	 * The symbol or name of the Element to retrieve. This is not case-sensitive.
	 *
	 * @return
	 * The Element with the specified symbol, or null if no Element
	 * with that symbol or name exists.
	 *
	 * @throws NullListException
	 * If this ElementList has not yet been created.
	 */
	public static Element getElement(String id) throws NullListException {
		if(!ElementList.hasBeenCreated()) {
			throw new NullListException();
		}
		
		Element e;
		if(ElementList.isSymbol(id)) {
			e = ElementList.symbolicList.get(id.toLowerCase());
		} else {
			e = ElementList.nomialList.get(id.toLowerCase());
		}
		return e;
	}
	
	/**
	 * Checks if the internal pointer can be advanced by one to get
	 * the next Element.
	 *
	 * @return
	 * true if the pointer is not already on the last Element; false
	 * otherwise.
	 *
	 * @throws NullListException
	 * If this ElementList has not yet been created.
	 */
	public static boolean hasNext() throws NullListException {
		if(!ElementList.hasBeenCreated()) {
			throw new NullListException();
		}
	
		boolean hasNext = (ElementList.list.length > ElementList.pointer);
		return hasNext;
	}
	
	/**
	 * Gets the next Element from the list.
	 *
	 * @return
	 * The next Element in the list. If the internal pointer is at the
	 * end of the list, then null is returned instead.
	 *
	 * @throws NoSuchElementException
	 * If the iteration has no more elements.
	 *
	 * @throws NullListException
	 * If this ElementList has not yet been created.
	 */
	public static Element next() throws NoSuchElementException, NullListException {
		if(!ElementList.hasBeenCreated()) {
			throw new NullListException();
		}
	
		Element nextElement = null;	
		if(hasNext()) {
			nextElement = getElement(pointer++);
		} else {
			throw new NoSuchElementException();
		}	
		return nextElement;
	}
	
	public static void remove() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Resets the internal lists of this ElementList to new Element[] objects.
	 *
	 * @param size
	 * The size to set the Element[] objects to.
	 */
	private static void initialize(int size) {
		ElementList.list = new Element[size];
		ElementList.nomialList = new HashMap<String,Element>(size);
		ElementList.symbolicList = new HashMap<String,Element>(size);
		ElementList.pointer = 0;
	}
	
	/**
	 * Checks if this ElementList has been created.
	 *
	 * @return
	 * True if the ElementList has been instantiated; false otherwise.
	 */
	private static boolean hasBeenCreated() {
		boolean created = false;
		if(ElementList.nomialList != null) {
			created = true;
		}
		return created;		
	}
	
	/**
	 * Checks if a String is an element symbol.
	 *
	 * @param element
	 * The input to check.
	 *
	 * @return
	 * True if the element has a length of two or less. False otherwise.
	 */
	private static boolean isSymbol(String element) {
		boolean symbol = (element.length() < 3);
		return symbol;
	}
}
