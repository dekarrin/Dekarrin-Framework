package com.dekarrin.db;

import java.util.HashMap;

/**
 * Represents rows of tabular data. The TableData class can used
 * as an alternative to passing in individual values to any
 * class that implements DatabaseManager.
 * 
 * To use a TableData in an UPDATE, create a TableData with only
 * the columns that you wish to update. In UPDATE statements,
 * only the first row is used; any additional rows will be ignored.
 * 
 * To use a TableData in an INSERT, create a TableData with only
 * the columns you need to insert data into. All rows are read in
 * INSERT queries.
 * 
 * TableData objects may also be obtained from a DatabaseManager
 * as the result of a query. To do so, use the
 * {@link DatabaseManager#getResultTableData() getResultTableData()}
 * method.
 */
public class TableData {
	
	/**
	 * The table this data is associated with.
	 */
	private String table;
	
	/**
	 * The names of the columns.
	 */
	private String[] columns = new String[0];
	
	/**
	 * The rows of data.
	 */
	private RowData[] data = new RowData[0];
	
	/**
	 * The row currently being operated on.
	 */
	private int currentRow = 0;
	
	/**
	 * Creates a new empty TableData for a table.
	 * 
	 * @param table
	 * The table to associate the TableData with.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData(String table) {
		this.table = table;
	}
	
	/**
	 * Checks if this TableData is empty.
	 * 
	 * @return
	 * True if empty.
	 */
	public boolean isEmpty() {
		return (data == null);
	}
	
	/**
	 * Gets the table that this data is associated with.
	 * 
	 * @return
	 * The table.
	 */
	public String getTable() {
		return table;
	}
	
	/**
	 * Gets the number of rows in this TableData.
	 * 
	 * @return
	 * The number of rows.
	 */
	public int rows() {
		return data.length;
	}
	
	/**
	 * Gets the row that the pointer is currently on.
	 * 
	 * @return
	 * The current row.
	 */
	public int currentRow() {
		return currentRow;
	}
	
	/**
	 * The column being operated on. This resets to 0 every
	 * time the current row changes.
	 */
	public int currentColumn() {
		return data[currentRow].currentColumn();
	}
	
	/**
	 * Moves the pointer to a specific row.
	 * 
	 * @param row
	 * The row to jump to.
	 * 
	 * @return
	 * This TableData.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData jump(int row) {
		if(row < 0 || row >= data.length) {
			throw new ArrayIndexOutOfBoundsException();
		}
		data[currentRow].reset();
		currentRow = row;
		return this;
	}
	
	/**
	 * Moves the pointer to a specific column.
	 * 
	 * @param index
	 * The index of the column to move the pointer to.
	 * 
	 * @return
	 * This TableData.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData jumpColumn(int index) {
		data[currentRow].jump(index);
		return this;
	}
	
	/**
	 * Moves the pointer to a specific column.
	 * 
	 * @param name
	 * The name of the column to move the pointer to.
	 * 
	 * @return
	 * This TableData.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData jumpColumn(String name) {
		int index = columnIndex(name);
		data[currentRow].jump(index);
		return this;
	}
	
	/**
	 * Gets the number of columns in this table.
	 * 
	 * @return
	 * The number of columns.
	 */
	public int columns() {
		return columns.length;
	}
	
	/**
	 * Gets the name of a column.
	 * 
	 * @param index
	 * The index of the column to check.
	 * 
	 * @return
	 * The name of the column, or null if the column does not
	 * have a name.
	 */
	public String columnName(int index) {
		String name = null;
		if(columns != null) {
			name = columns[index];
		}
		return name;
	}
	
	/**
	 * Gets the index of a column.
	 * 
	 * @param name
	 * The name of the column.
	 * 
	 * @return
	 * The index of the column, or -1 if there is no column with
	 * that name.
	 */
	public int columnIndex(String name) {
		int index = -1;
		if(columns != null) {
			for(int i = 0; i < columns.length; i++) {
				if(columns[i].equals(name)) {
					index = i;
					break;
				}
			}
		}
		return index;
	}
	
	/**
	 * Goes to the next row.
	 * 
	 * @return
	 * This TableData.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData next() {
		currentRow++;
		if(currentRow >= data.length) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return this;
	}
	
	/**
	 * Checks if there are more rows after the current one.
	 * 
	 * @return
	 * Whether there are more rows.
	 */
	public boolean hasNext() {
		return (currentRow + 1 < data.length); 
	}
	
	/**
	 * Resets the pointer to the first row.
	 * 
	 * @return
	 * This TableData.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData reset() {
		jump(0);
		return this;
	}
	
	/**
	 * Goes to the next column. This is usually triggered automatically
	 * by data entry and retrieval methods.
	 * 
	 * @return
	 * This TableData.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData nextColumn() {
		data[currentRow].next();
		return this;
	}
	
	/**
	 * Checks if there are more columns after the current one.
	 * 
	 * @return
	 * Whether there are more columns.
	 */
	public boolean hasNextColumn() {
		return data[currentRow].hasNext(); 
	}
	
	/**
	 * Resets the pointer to the first row.
	 * 
	 * @return
	 * This TableData.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData resetColumn() {
		data[currentRow].reset();
		return this;
	}
	
	/**
	 * Adds a new column.
	 * 
	 * @param names
	 * The names of the columns. This can be null.
	 * 
	 * @return
	 * This TableData.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData addColumn(String... names) {
		for(String n: names) {
			addColumnSlot();
			columns[columns.length-1] = n;
			for(int i = 0; i < rows(); i++) {
				data[i].addColumn();
			}
		}
		return this;
	}
	
	/**
	 * Adds a new row to this TableData. The pointer is then set to
	 * the new row.
	 * 
	 * @return
	 * This TableData.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData addRow() {
		addRowSlot();
		data[data.length-1] = new RowData(columns.length);
		jump(data.length-1);
		return this;
	}
	
	/**
	 * Adds a new row filled with data to this TableData. The pointer is
	 * then set to the new row.
	 * 
	 * @param data
	 * The data to fill the row with.
	 * 
	 * @return
	 * This TableData.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData addRow(String... data) {
		addRow();
		for(int i = 0; i < data.length && i < columns.length; i++) {
			set(data[i]);
		}
		return this;
	}
	
	/**
	 * Gets the current row as a Map.
	 * 
	 * @return
	 * The row.
	 */
	public HashMap<String,String> getMap() {
		return getMap(currentRow);
	}
	
	/**
	 * Gets an entire row as a Map.
	 * 
	 * @param index
	 * The index of the row to get.
	 * 
	 * @return
	 * The row.
	 */
	public HashMap<String,String> getMap(int index) {
		HashMap<String,String> map = new HashMap<String,String>();
		jump(index);
		for(int i = 0; i < columns.length; i++) {
			map.put(columns[i], get(i));
		}
		return map;
	}
	
	/**
	 * Sets the current column to a value and advances the current
	 * column.
	 * 
	 * @param value
	 * The value to set it to.
	 * 
	 * @return
	 * This TableData.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData set(String value) {
		data[currentRow].set(value);
		if(data[currentRow].hasNext()) {
			data[currentRow].next();
		}
		return this;
	}
	
	/**
	 * Sets a column to a value.
	 * 
	 * @param index
	 * The index of the column to set.
	 * 
	 * @param value
	 * The value to set it to.
	 * 
	 * @return
	 * This TableData.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData set(int index, String value) {
		data[currentRow].set(index, value);
		return this;
	}
	
	/**
	 * Sets a column to a value.
	 * 
	 * @param name
	 * The name of the column to set.
	 * 
	 * @param value
	 * The value to set it to.
	 * 
	 * @return
	 * This TableData.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData set(String name, String value) {
		int index = columnIndex(name);
		set(index, value);
		return this;
	}
	
	/**
	 * Sets the current column to a boolean value and advances the current
	 * column.
	 * 
	 * @param value
	 * The value to set it to.
	 * 
	 * @return
	 * This TableData.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setBoolean(boolean value) {
		String strValue = value ? "1" : "0";
		set(strValue);
		return this;
	}
	
	/**
	 * Sets a column to a boolean value.
	 * 
	 * @param index
	 * The index of the column to set.
	 * 
	 * @param value
	 * The value to set it to.
	 * 
	 * @return
	 * This TableData.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setBoolean(int index, boolean value) {
		String strValue = value ? "1" : "0";
		set(index, strValue);
		return this;
	}
	
	/**
	 * Sets a column to a boolean value.
	 * 
	 * @param name
	 * The name of the column to set.
	 * 
	 * @param value
	 * The value to set it to.
	 * 
	 * @return
	 * This TableData.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setBoolean(String name, boolean value) {
		String strValue = value ? "1" : "0";
		set(name, strValue);
		return this;
	}
	
	/**
	 * Sets the current column to a byte value and advances the current
	 * column.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setByte(byte value) {
		String strValue = Byte.toString(value);
		set(strValue);
		return this;
	}
	
	/**
	 * Sets a column to a byte value.
	 * 
	 * @param index
	 * The index of the column to set.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setByte(int index, byte value) {
		String strValue = Byte.toString(value);
		set(index, strValue);
		return this;
	}
	
	/**
	 * Sets a column to a byte value.
	 * 
	 * @param name
	 * The name of the column to set.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setByte(String name, byte value) {
		String strValue = Byte.toString(value);
		set(name, strValue);
		return this;
	}
	
	/**
	 * Sets the current column to a short value and advances the current
	 * column.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setShort(short value) {
		String strValue = Short.toString(value);
		set(strValue);
		return this;
	}
	
	/**
	 * Sets a column to a short value.
	 * 
	 * @param index
	 * The index of the column to set.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setShort(int index, short value) {
		String strValue = Short.toString(value);
		set(index, strValue);
		return this;
	}
	
	/**
	 * Sets a column to a short value.
	 * 
	 * @param name
	 * The name of the column to set.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setShort(String name, short value) {
		String strValue = Short.toString(value);
		set(name, strValue);
		return this;
	}
	
	/**
	 * Sets the current column to an integer value and advances the current
	 * column.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setInt(int value) {
		String strValue = Integer.toString(value);
		set(strValue);
		return this;
	}
	
	/**
	 * Sets a column to an integer value.
	 * 
	 * @param index
	 * The index of the column to set.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setInt(int index, int value) {
		String strValue = Integer.toString(value);
		set(index, strValue);
		return this;
	}
	
	/**
	 * Sets a column to an integer value.
	 * 
	 * @param name
	 * The name of the column to set.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setInt(String name, int value) {
		String strValue = Integer.toString(value);
		set(name, strValue);
		return this;
	}
	
	/**
	 * Sets the current column to a long value and advances the current
	 * column.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setLong(long value) {
		String strValue = Long.toString(value);
		set(strValue);
		return this;
	}
	
	/**
	 * Sets a column to a long value.
	 * 
	 * @param index
	 * The index of the column to set.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setLong(int index, long value) {
		String strValue = Long.toString(value);
		set(index, strValue);
		return this;
	}
	
	/**
	 * Sets a column to a long value.
	 * 
	 * @param name
	 * The name of the column to set.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setLong(String name, long value) {
		String strValue = Long.toString(value);
		set(name, strValue);
		return this;
	}
	
	/**
	 * Sets the current column to a float value and advances the current
	 * column.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setFloat(float value) {
		String strValue = Float.toString(value);
		set(strValue);
		return this;
	}
	
	/**
	 * Sets a column to a float value.
	 * 
	 * @param index
	 * The index of the column to set.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setFloat(int index, float value) {
		String strValue = Float.toString(value);
		set(index, strValue);
		return this;
	}
	
	/**
	 * Sets a column to a float value.
	 * 
	 * @param name
	 * The name of the column to set.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setFloat(String name, float value) {
		String strValue = Float.toString(value);
		set(name, strValue);
		return this;
	}
	
	/**
	 * Sets the current column to a double value and advances the current
	 * column.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setDouble(double value) {
		String strValue = Double.toString(value);
		set(strValue);
		return this;
	}
	
	/**
	 * Sets a column to a double value.
	 * 
	 * @param index
	 * The index of the column to set.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setDouble(int index, double value) {
		String strValue = Double.toString(value);
		set(index, strValue);
		return this;
	}
	
	/**
	 * Sets a column to a double value.
	 * 
	 * @param name
	 * The name of the column to set.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setDouble(String name, double value) {
		String strValue = Double.toString(value);
		set(name, strValue);
		return this;
	}
	
	/**
	 * Sets the current column to a char value and advances the current
	 * column.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setChar(char value) {
		String strValue = Character.toString(value);
		set(strValue);
		return this;
	}
	
	/**
	 * Sets a column to a char value.
	 * 
	 * @param index
	 * The index of the column to set.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setChar(int index, char value) {
		String strValue = Character.toString(value);
		set(index, strValue);
		return this;
	}
	
	/**
	 * Sets a column to a char value.
	 * 
	 * @param name
	 * The name of the column to set.
	 * 
	 * @param value
	 * The value to set it to.
	 *
	 * @return
	 * This TableData.
	 */
	public TableData setChar(String name, char value) {
		String strValue = Character.toString(value);
		set(name, strValue);
		return this;
	}
	
	/**
	 * Gets the value of the current column and advances the current
	 * column.
	 * 
	 * @return
	 * The value of the column.
	 */
	public String get() {
		String value = data[currentRow].get();
		if(data[currentRow].hasNext()) {
			data[currentRow].next();
		}
		return value;
	}
	
	/**
	 * Gets the value of a column.
	 * 
	 * @param index
	 * The index of the column to get.
	 * 
	 * @return
	 * The value.
	 */
	public String get(int index) {
		return data[currentRow].get(index);
	}
	
	/**
	 * Gets the value of a column.
	 * 
	 * @param name
	 * The name of the column to get.
	 * 
	 * @return
	 * The value.
	 */
	public String get(String name) {
		int index = columnIndex(name);
		return get(index);
	}
	
	/**
	 * Gets the value of the current column as a boolean and
	 * advances the current column.
	 * 
	 * @return
	 * The value of the column.
	 */
	public boolean getBoolean() {
		String strValue = get();
		boolean value;
		if(strValue.equals("0") || strValue.equalsIgnoreCase("false")) {
			value = false;
		} else {
			value = true;
		}
		return value;
	}
	
	/**
	 * Gets the value of a column as a boolean.
	 * 
	 * @param index
	 * The index of the column to get.
	 * 
	 * @return
	 * The value.
	 */
	public boolean getBoolean(int index) {
		String strValue = get(index);
		boolean value;
		if(strValue.equals("0") || strValue.equals("false")) {
			value = false;
		} else {
			value = true;
		}
		return value;
	}
	
	/**
	 * Gets the value of a column as a boolean.
	 * 
	 * @param name
	 * The name of the column to get.
	 * 
	 * @return
	 * The value.
	 */
	public boolean getBoolean(String name) {
		int index = columnIndex(name);
		return getBoolean(index);
	}
	
	/**
	 * Gets the value of the current column as a byte and
	 * advances the current column.
	 * 
	 * @return
	 * The value of the column.
	 */
	public byte getByte() {
		String strValue = get();
		return Byte.parseByte(strValue);
	}
	
	/**
	 * Gets the value of a column as a byte.
	 * 
	 * @param index
	 * The index of the column to get.
	 * 
	 * @return
	 * The value.
	 */
	public byte getByte(int index) {
		String strValue = get(index);
		return Byte.parseByte(strValue);
	}
	
	/**
	 * Gets the value of a column as a byte.
	 * 
	 * @param name
	 * The name of the column to get.
	 * 
	 * @return
	 * The value.
	 */
	public byte getByte(String name) {
		int index = columnIndex(name);
		return getByte(index);
	}
	
	/**
	 * Gets the value of the current column as a short and
	 * advances the current column.
	 * 
	 * @return
	 * The value of the column.
	 */
	public short getShort() {
		String strValue = get();
		return Short.parseShort(strValue);
	}
	
	/**
	 * Gets the value of a column as a short.
	 * 
	 * @param index
	 * The index of the column to get.
	 * 
	 * @return
	 * The value.
	 */
	public short getShort(int index) {
		String strValue = get(index);
		return Short.parseShort(strValue);
	}
	
	/**
	 * Gets the value of a column as a short.
	 * 
	 * @param name
	 * The name of the column to get.
	 * 
	 * @return
	 * The value.
	 */
	public short getShort(String name) {
		int index = columnIndex(name);
		return getShort(index);
	}
	
	/**
	 * Gets the value of the current column as an integer and
	 * advances the current column.
	 * 
	 * @return
	 * The value of the column.
	 */
	public int getInt() {
		String strValue = get();
		return Integer.parseInt(strValue);
	}
	
	/**
	 * Gets the value of a column as an integer.
	 * 
	 * @param index
	 * The index of the column to get.
	 * 
	 * @return
	 * The value.
	 */
	public int getInt(int index) {
		String strValue = get(index);
		return Integer.parseInt(strValue);
	}
	
	/**
	 * Gets the value of a column as an integer.
	 * 
	 * @param name
	 * The name of the column to get.
	 * 
	 * @return
	 * The value.
	 */
	public int getInt(String name) {
		int index = columnIndex(name);
		return getInt(index);
	}
	
	/**
	 * Gets the value of the current column as a long and
	 * advances the current column.
	 * 
	 * @return
	 * The value of the column.
	 */
	public long getLong() {
		String strValue = get();
		return Long.parseLong(strValue);
	}
	
	/**
	 * Gets the value of a column as a long.
	 * 
	 * @param index
	 * The index of the column to get.
	 * 
	 * @return
	 * The value.
	 */
	public long getLong(int index) {
		String strValue = get(index);
		return Long.parseLong(strValue);
	}
	
	/**
	 * Gets the value of a column as a long.
	 * 
	 * @param name
	 * The name of the column to get.
	 * 
	 * @return
	 * The value.
	 */
	public long getLong(String name) {
		int index = columnIndex(name);
		return getLong(index);
	}
	
	/**
	 * Gets the value of the current column as a float and
	 * advances the current column.
	 * 
	 * @return
	 * The value of the column.
	 */
	public float getFloat() {
		String strValue = get();
		return Float.parseFloat(strValue);
	}
	
	/**
	 * Gets the value of a column as a float.
	 * 
	 * @param index
	 * The index of the column to get.
	 * 
	 * @return
	 * The value.
	 */
	public float getFloat(int index) {
		String strValue = get(index);
		return Float.parseFloat(strValue);
	}
	
	/**
	 * Gets the value of a column as a float.
	 * 
	 * @param name
	 * The name of the column to get.
	 * 
	 * @return
	 * The value.
	 */
	public float getFloat(String name) {
		int index = columnIndex(name);
		return getFloat(index);
	}
	
	/**
	 * Gets the value of the current column as a double and
	 * advances the current column.
	 * 
	 * @return
	 * The value of the column.
	 */
	public double getDouble() {
		String strValue = get();
		return Double.parseDouble(strValue);
	}
	
	/**
	 * Gets the value of a column as a double.
	 * 
	 * @param index
	 * The index of the column to get.
	 * 
	 * @return
	 * The value.
	 */
	public double getDouble(int index) {
		String strValue = get(index);
		return Double.parseDouble(strValue);
	}
	
	/**
	 * Gets the value of a column as a double.
	 * 
	 * @param name
	 * The name of the column to get.
	 * 
	 * @return
	 * The value.
	 */
	public double getDouble(String name) {
		int index = columnIndex(name);
		return getDouble(index);
	}
	
	/**
	 * Gets the value of the current column as a char and
	 * advances the current column.
	 * 
	 * @return
	 * The value of the column.
	 */
	public char getChar() {
		String strValue = get();
		return strValue.charAt(0);
	}
	
	/**
	 * Gets the value of a column as a char.
	 * 
	 * @param index
	 * The index of the column to get.
	 * 
	 * @return
	 * The value.
	 */
	public char getChar(int index) {
		String strValue = get(index);
		return strValue.charAt(0);
	}
	
	/**
	 * Gets the value of a column as a char.
	 * 
	 * @param name
	 * The name of the column to get.
	 * 
	 * @return
	 * The value.
	 */
	public char getChar(String name) {
		int index = columnIndex(name);
		return getChar(index);
	}
	
	/**
	 * Gets a column as an array of Strings.
	 * 
	 * @param index
	 * The index of the column to get.
	 * 
	 * @return
	 * The array.
	 */
	public String[] getArray(int index) {
		String[] values = new String[rows()];
		reset();
		for(int i = 0; i < rows(); jump(i++)) {
			values[i] = get(index);
		}
		return values;
	}
	
	/**
	 * Gets a column as an array of Strings.
	 * 
	 * @param name
	 * The name of the column to get.
	 * 
	 * @return
	 * The array.
	 */
	public String[] getArray(String name) {
		int index = columnIndex(name);
		return getArray(index);
	}
	
	/**
	 * Gets a column as an array of booleans.
	 * 
	 * @param index
	 * The index of the column to get.
	 * 
	 * @return
	 * The array.
	 */
	public boolean[] getBooleanArray(int index) {
		String[] strValues = getArray(index);
		return convertToBoolean(strValues);
	}
	
	/**
	 * Gets a column as an array of booleans.
	 * 
	 * @param name
	 * The name of the column to get.
	 * 
	 * @return
	 * The array.
	 */
	public boolean[] getBooleanArray(String name) {
		String[] strValues = getArray(name);
		return convertToBoolean(strValues);
	}
	
	/**
	 * Gets a column as an array of bytes.
	 * 
	 * @param index
	 * The index of the column to get.
	 * 
	 * @return
	 * The array.
	 */
	public byte[] getByteArray(int index) {
		String[] strValues = getArray(index);
		return convertToByte(strValues);
	}
	
	/**
	 * Gets a column as an array of byte.
	 * 
	 * @param name
	 * The name of the column to get.
	 * 
	 * @return
	 * The array.
	 */
	public byte[] getByteArray(String name) {
		String[] strValues = getArray(name);
		return convertToByte(strValues);
	}
	
	/**
	 * Gets a column as an array of shorts.
	 * 
	 * @param index
	 * The index of the column to get.
	 * 
	 * @return
	 * The array.
	 */
	public short[] getShortArray(int index) {
		String[] strValues = getArray(index);
		return convertToShort(strValues);
	}
	
	/**
	 * Gets a column as an array of shorts.
	 * 
	 * @param name
	 * The name of the column to get.
	 * 
	 * @return
	 * The array.
	 */
	public short[] getShortArray(String name) {
		String[] strValues = getArray(name);
		return convertToShort(strValues);
	}
	
	/**
	 * Gets a column as an array of integers.
	 * 
	 * @param index
	 * The index of the column to get.
	 * 
	 * @return
	 * The array.
	 */
	public int[] getIntArray(int index) {
		String[] strValues = getArray(index);
		return convertToInt(strValues);
	}
	
	/**
	 * Gets a column as an array of integers.
	 * 
	 * @param name
	 * The name of the column to get.
	 * 
	 * @return
	 * The array.
	 */
	public int[] getIntArray(String name) {
		String[] strValues = getArray(name);
		return convertToInt(strValues);
	}
	
	/**
	 * Gets a column as an array of longs.
	 * 
	 * @param index
	 * The index of the column to get.
	 * 
	 * @return
	 * The array.
	 */
	public long[] getLongArray(int index) {
		String[] strValues = getArray(index);
		return convertToLong(strValues);
	}
	
	/**
	 * Gets a column as an array of longs.
	 * 
	 * @param name
	 * The name of the column to get.
	 * 
	 * @return
	 * The array.
	 */
	public long[] getLongArray(String name) {
		String[] strValues = getArray(name);
		return convertToLong(strValues);
	}
	
	/**
	 * Gets a column as an array of floats.
	 * 
	 * @param index
	 * The index of the column to get.
	 * 
	 * @return
	 * The array.
	 */
	public float[] getFloatArray(int index) {
		String[] strValues = getArray(index);
		return convertToFloat(strValues);
	}
	
	/**
	 * Gets a column as an array of floats.
	 * 
	 * @param name
	 * The name of the column to get.
	 * 
	 * @return
	 * The array.
	 */
	public float[] getFloatArray(String name) {
		String[] strValues = getArray(name);
		return convertToFloat(strValues);
	}
	
	/**
	 * Gets a column as an array of doubles.
	 * 
	 * @param index
	 * The index of the column to get.
	 * 
	 * @return
	 * The array.
	 */
	public double[] getDoubleArray(int index) {
		String[] strValues = getArray(index);
		return convertToDouble(strValues);
	}
	
	/**
	 * Gets a column as an array of doubles.
	 * 
	 * @param name
	 * The name of the column to get.
	 * 
	 * @return
	 * The array.
	 */
	public double[] getDoubleArray(String name) {
		String[] strValues = getArray(name);
		return convertToDouble(strValues);
	}
	
	/**
	 * Gets a column as an array of chars.
	 * 
	 * @param index
	 * The index of the column to get.
	 * 
	 * @return
	 * The array.
	 */
	public char[] getCharArray(int index) {
		String[] strValues = getArray(index);
		return convertToChar(strValues);
	}
	
	/**
	 * Gets a column as an array of chars.
	 * 
	 * @param name
	 * The name of the column to get.
	 * 
	 * @return
	 * The array.
	 */
	public char[] getCharArray(String name) {
		String[] strValues = getArray(name);
		return convertToChar(strValues);
	}
	
	/**
	 * Duplicates the column structure of this TableData without
	 * keeping any rows.
	 * 
	 * @return
	 * The new TableData object containing identical columns and
	 * table properties.
	 */
	public TableData cloneStructure() {
		TableData t2 = new TableData(table);
		for(String colName: columns) {
			t2.addColumn(colName);
		}
		return t2;
	}
	
	/**
	 * Converts String array into a boolean array.
	 * 
	 * @param strArray
	 * The array to convert.
	 * 
	 * @return
	 * The converted array.
	 */
	private boolean[] convertToBoolean(String[] strArray) {
		boolean[] arr = new boolean[strArray.length];
		for(int i = 0; i < strArray.length; i++) {
			if(strArray[i].equals("0") || strArray[i].equalsIgnoreCase("false")) {
				arr[i] = false;
			} else {
				arr[i] = true;
			}
		}
		return arr;
	}
	
	/**
	 * Converts String array into a byte array.
	 * 
	 * @param strArray
	 * The array to convert.
	 * 
	 * @return
	 * The converted array.
	 */
	private byte[] convertToByte(String[] strArray) {
		byte[] arr = new byte[strArray.length];
		for(int i = 0; i < strArray.length; i++) {
			arr[i] = Byte.parseByte(strArray[i]);
		}
		return arr;
	}
	
	/**
	 * Converts String array into a short array.
	 * 
	 * @param strArray
	 * The array to convert.
	 * 
	 * @return
	 * The converted array.
	 */
	private short[] convertToShort(String[] strArray) {
		short[] arr = new short[strArray.length];
		for(int i = 0; i < strArray.length; i++) {
			arr[i] = Short.parseShort(strArray[i]);
		}
		return arr;
	}
	
	/**
	 * Converts String array into an integer array.
	 * 
	 * @param strArray
	 * The array to convert.
	 * 
	 * @return
	 * The converted array.
	 */
	private int[] convertToInt(String[] strArray) {
		int[] arr = new int[strArray.length];
		for(int i = 0; i < strArray.length; i++) {
			arr[i] = Integer.parseInt(strArray[i]);
		}
		return arr;
	}
	
	/**
	 * Converts String array into a long array.
	 * 
	 * @param strArray
	 * The array to convert.
	 * 
	 * @return
	 * The converted array.
	 */
	private long[] convertToLong(String[] strArray) {
		long[] arr = new long[strArray.length];
		for(int i = 0; i < strArray.length; i++) {
			arr[i] = Long.parseLong(strArray[i]);
		}
		return arr;
	}
	
	/**
	 * Converts String array into a float array.
	 * 
	 * @param strArray
	 * The array to convert.
	 * 
	 * @return
	 * The converted array.
	 */
	private float[] convertToFloat(String[] strArray) {
		float[] arr = new float[strArray.length];
		for(int i = 0; i < strArray.length; i++) {
			arr[i] = Float.parseFloat(strArray[i]);
		}
		return arr;
	}
	
	/**
	 * Converts String array into a double array.
	 * 
	 * @param strArray
	 * The array to convert.
	 * 
	 * @return
	 * The converted array.
	 */
	private double[] convertToDouble(String[] strArray) {
		double[] arr = new double[strArray.length];
		for(int i = 0; i < strArray.length; i++) {
			arr[i] = Double.parseDouble(strArray[i]);
		}
		return arr;
	}
	
	/**
	 * Converts String array into a char array.
	 * 
	 * @param strArray
	 * The array to convert.
	 * 
	 * @return
	 * The converted array.
	 */
	private char[] convertToChar(String[] strArray) {
		char[] arr = new char[strArray.length];
		for(int i = 0; i < strArray.length; i++) {
			arr[i] = strArray[i].charAt(0);
		}
		return arr;
	}
	
	/**
	 * Adds space for an extra row to the data array.
	 */
	private void addRowSlot() {
		RowData[] dataBuffer = new RowData[data.length + 1];
		for(int i = 0; i < data.length; i++) {
			dataBuffer[i] = data[i];
		}
		data = dataBuffer;
	}
	
	/**
	 * Adds space for an extra row to the data array.
	 */
	private void addColumnSlot() {
		String[] dataBuffer = new String[columns.length + 1];
		for(int i = 0; i < columns.length; i++) {
			dataBuffer[i] = columns[i];
		}
		columns = dataBuffer;
	}
}