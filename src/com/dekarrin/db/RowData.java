package com.dekarrin.db;

/**
 * Contains data from a single row.
 * 
 * @author Michael Nelson
 */
public class RowData {

	/**
	 * The data in this RowData.
	 */
	private String[] data = null;
	
	/**
	 * The column that is used for all operations.
	 */
	private int currentColumn = 0;
	
	/**
	 * Creates a new RowData.
	 * 
	 * @param columns
	 * The number of columns in the RowData.
	 */
	public RowData(int columns) {
		data = new String[columns];
	}
	
	/**
	 * Gets the current column.
	 * 
	 * @return
	 * The column.
	 */
	public int currentColumn() {
		return currentColumn;
	}
	
	/**
	 * Changes the row.
	 * 
	 * @param index
	 * The row to jump to.
	 */
	public void jump(int index) {
		if(index < 0 || index >= data.length) {
			throw new ArrayIndexOutOfBoundsException();
		}
	}
	
	/**
	 * Goes to the next column.
	 */
	public void next() {
		currentColumn++;
		if(currentColumn >= data.length) {
			throw new ArrayIndexOutOfBoundsException();
		}
	}
	
	/**
	 * Checks if there are more columns after the current one.
	 * 
	 * @return
	 * Whether there are more columns.
	 */
	public boolean hasNext() {
		return (currentColumn + 1 < data.length); 
	}
	
	/**
	 * Resets the pointer to the first column.
	 */
	public void reset() {
		jump(0);
	}
	
	/**
	 * Adds a column.
	 */
	public void addColumn() {
		String[] dataBuffer = new String[data.length + 1];
		for(int i = 0; i < data.length; i++) {
			dataBuffer[i] = data[i];
		}
		data = dataBuffer;
		data[data.length-1] = "";
	}
	
	/**
	 * Gets the value at the current column.
	 * 
	 * @return
	 * The value.
	 */
	public String get() {
		return data[currentColumn];
	}
	
	/**
	 * Sets the value at the current column.
	 * 
	 * @param value
	 * The new value.
	 */
	public void set(String value) {
		data[currentColumn] = value;
	}
	
	/**
	 * Sets the value at a column.
	 * 
	 * @param index
	 * The index of the column to set.
	 * 
	 * @param value
	 * The new value.
	 */
	public void set(int index, String value) {
		data[index] = value;
	}
	
	/**
	 * Gets the value at a column.
	 * 
	 * @param index
	 * The index of the column to set.
	 * 
	 * @return value
	 * The value of the column.
	 */
	public String get(int index) {
		return data[index];
	}
}
