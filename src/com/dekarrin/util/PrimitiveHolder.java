package com.dekarrin.util;

/**
 * Holds an array of primitives that knows its position.
 */
public interface PrimitiveHolder {
	public int position();
	public void move(int idex);
	public void reset();
	public boolean isAtEnd();
	public int size();
}