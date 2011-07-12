package com.dekarrin.file.csv;

import java.io.*;
import java.util.*;

/**
 * Reads a CSV file.
 */
public class CsvFile {

	/**
	 * Represents a single item from a CsvFile.
	 */
	private class CsvItem {
		private String[] values;

		public CsvItem(String[] values) {
			this.values = values;
		}

		public void addValue(String addedValue) {
			String[] newValues = new String[values.length + 1];
			
			int i = 0;
			for(String v: values) {
				newValues[i++] = v;
			}
			newValues[newValues.length - 1] = addedValue;

			values = newValues;
		}

		public void deleteValue(int index) {
			String[] newValues = new String[values.length - 1];

			int j = 0;
			for(int i = 0; i < values.length; i++) {
				if(i != index) {
					newValues[j++] = values[i];
				}
			}

			values = newValues;
		}

		public void addEmptyValue() {
			addValue("");
		}

		public String[] getValues() {
			String[] data = new String[values.length];
			
			int i = 0;
			for(String v: values) {
				data[i++] = v;
			}

			return data;
		}
		
		public int getValueCount() {
			return values.length;
		}
	}
	
	/**
	 * The number of values in each item.
	 */
	private int valueCount = 0;

	/**
	 * The delimiter between entire items of data.
	 */
	private char itemSeperator = '\n';
	 
	/**
	 * The delimiter between each value in an item of data.
	 */
	private char valueSeperator = ',';

	/**
	 * The items contained in this CSV file.
	 */
	private CsvItem[] items;

	/**
	 * The file to be read.
	 */
	private File file;
	
	/**
	 * The headers, which name the data, at the top of a CSV File.
	 */
	private String[] headers;
	
	/**
	 * Used while reading a csv file. Becomes true once the end of
	 * the stream has been reached.
	 */
	private boolean readComplete;
	
	/**
	 * Creates a new CsvFile associated with the specified
	 * file. If the file does not exist, it will be created.
	 *
	 * @param path
	 * The path to the file being read.
	 */
	public CsvFile(String path) {
		initialize();
		setFile(path);
	}
	
	/**
	 * Adds a single header to the definition of this CSV file.
	 * This method does not actually write the headers to file,
	 * it only defines the headers. If there are already items
	 * added to this file, each will have an additional empty
	 * value added to them to account for the new header.
	 *
	 * @param headerName
	 * The header to add.
	 */
	public void addHeader(String headerName) {
		if(headers == null) {
			headers = new String[1];
			headers[0] = headerName;
		} else {
			String[] newHeaders = new String[headers.length + 1];
			
			int i = 0;
			for(String h: headers) {
				newHeaders[i++] = h;
			}
			
			newHeaders[newHeaders.length - 1] = headerName;
			headers = newHeaders;
		}
		
		equalizeValueCount();
	}
	
	/**
	 * Adds an item to the CSV file. If headers are defined, the item
	 * is checked against it. If the item to be added does not have the
	 * same number of values as previously added items, blank values
	 * are added to either this one or the other ones to make them 
	 *
	 * @param values
	 * The values of the item to add to the file. If the number of
	 * values in this does not match the number of headers, blank
	 * headers are added, and vice versa.
	 */
	public void addItem(String[] values) {
		CsvItem[] newItems = new CsvItem[items.length + 1];

		int i = 0;
		for(CsvItem csv: items) {
			newItems[i++] = csv;
		}

		newItems[newItems.length - 1] = new CsvItem(values);
		items = newItems;

		equalizeValueCount();
	}

	/**
	 * Removes a single item from this CsvFile. If the item being
	 * removed had caused other items to be padded when it was
	 * added, the affected items remain padded even after the other
	 * is removed.
	 *
	 * @param index
	 * The index of the item to be removed.
	 */
	public void removeItem(int index) {
		CsvItem[] newItems = new CsvItem[items.length - 1];

		int j = 0;
		for(int i = 0; i < items.length; i++) {
			if(i != index) {
				newItems[j++] = items[i];
			}
		}
		items = newItems;

		equalizeValueCount();
	}
	
	/**
	 * Changes the character that seperates each item in the file.
	 *
	 * @param newSeperator
	 * The character to use as a seperator.
	 */
	public void setItemSeperator(char newSeperator) {
		itemSeperator = newSeperator;
	}

	/**
	 * Changes the character that seperates each value of an item
	 * in the file.
	 *
	 * @param newSeperator
	 * The character to use as a seperator.
	 */
	public void setValueSeperator(char newSeperator) {
		valueSeperator = newSeperator;
	}

	/**
	 * Gets the headers in this CsvFile.
	 *
	 * @return
	 * The headers.
	 */
	public String[] getHeaders() {
		return headers;
	}

	/**
	 * Gets all the items in this CsvFile. This does not include
	 * the headers.
	 *
	 * @return
	 * An array containing all data in this CsvFile. This is a two
	 * dimensional array. The first dimension is the item, and the
	 * second is the value.
	 */
	public String[][] getData() {
		String[][] data = new String[items.length][valueCount];

		int i = 0;
		for(CsvItem csvItem: items) {
			data[i++] = csvItem.getValues();
		}

		return data;
	}

	/**
	 * Writes out this CsvFile to the file it's associated with.
	 * This does not append to the file; the file is overwritten.
	 */
	public void write() {
		eraseFile();
		BufferedWriter out = getWriter();
		
		try {
			writeHeaders(out);
			writeData(out);
			out.flush();
			out.close();
		} catch(IOException e) {
			System.err.println("Error while writing: " + e.getMessage());
		}
	}

	/**
	 * Reads in the associated file to this CsvFile. This CsvFile
	 * is overwritten if it contains data.
	 */
	public void read() {
		initialize();
		readComplete = false;
		
		BufferedReader in = getReader();

		Vector<CsvItem> items = new Vector<CsvItem>(10);
		String itemString = null;
		boolean readHeaders = true;
		while(!readComplete) {
			try {
				itemString = readItem(in);
			} catch(IOException e) {
				System.err.println("Error reading item: " + e.getMessage());
			}
			
			if(itemString != null) {
				if(readHeaders) {
					readHeaders = false;
					headers = itemString.split((new Character(valueSeperator)).toString());
				} else {
					String sep = (new Character(valueSeperator)).toString();
					String[] itemValues = itemString.split(sep);
					items.add(new CsvItem(itemValues));
				}
			}
		}
		
		this.items = items.toArray(new CsvItem[1]);
		equalizeValueCount();
	}
	
	/**
	 * Sets the file that this CsvFile is associated with.
	 *
	 * @param path
	 * The path to the file.
	 */
	public void setFile(String path) {
		file = new File(path);
		try {
			file.createNewFile();
		} catch(IOException e) {
			System.err.println("Error: Cannot create file!");
		}
	}
	
	/**
	 * Makes all items/headers have the same number of items 
	 */
	private void equalizeValueCount() {
		valueCount = (headers != null) ? headers.length : 0;
		
		for(CsvItem c: items) {
			valueCount = Math.max(c.getValueCount(), valueCount);
		}
		
		while(headers.length < valueCount) {
			addHeader("");
		}
		
		for(CsvItem c: items) {
			while(c.getValueCount() < valueCount) {
				c.addEmptyValue();
			}
		}
	}
	
	/**
	 * Initializes all references to their default values.
	 */
	private void initialize() {
		headers = null;
		items = null;
	}
	
	/**
	 * Truncates the file to 0 bytes.
	 */
	private void eraseFile() {
		file.delete();
		
		try {
			file.createNewFile();
		} catch(IOException e) {
			System.err.println("Error truncating file: " + e.getMessage());
		}
	}
	
	/**
	 * Sets up a BufferedWriter for this file.
	 */
	private BufferedWriter getWriter() {
		BufferedWriter w = null;
	
		try {
			w = new BufferedWriter(new FileWriter(file.toString()));
		} catch(FileNotFoundException e) {
			System.err.println("Could not find file!");
		} catch(IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
		
		return w;
	}
	
	/**

	 * Sets up a BufferedReader for this file.
	 */
	private BufferedReader getReader() {

		BufferedReader r = null;
		
		try {
			r = new BufferedReader(new FileReader(file.toString()));
		} catch(FileNotFoundException e) {
			System.err.println("Could not find file!");
		} catch(IOException e) {
			System.err.println("Error: " + e.getMessage());
		}
		
		return r;
	}
	
	/**
	 * Writes the headers to a Writer.
	 */
	private void writeHeaders(Writer out) throws IOException {
		String output = makeItemString(headers);
		out.write(output);
		
		if(items != null) {
			out.write((int)itemSeperator);
		}
	}
	
	/**
	 * Outputs the data to a Writer.
	 */
	private void writeData(Writer out) throws IOException {
		for(int i = 0; i < items.length-1; i++) {
			String[] dataValues = items[i].getValues();
			String output = makeItemString(dataValues);
			
			out.write(output);
			out.write((int)itemSeperator);
		}
		
		String[] dataValues = items[items.length-1].getValues();
		String output = makeItemString(dataValues);
		out.write(output);
	}
	
	/**
	 * Creates a string of item data values, seperated by the seperator.
	 *
	 * @param dataValues
	 * The values to use to create the String.
	 *
	 * @return
	 * The items in the dataValues array in a single string, seperated
	 * by the valueSeperator char.
	 */
	private String makeItemString(String[] dataValues) {
		StringBuffer itemString = new StringBuffer();
		
		for(String d: dataValues) {
			itemString.append(d);
			itemString.append(valueSeperator);
		}
		
		String output = itemString.toString();
		output = output.substring(0, output.length() - 1);
		
		return output;
	}
	
	/**
	 * Reads a single item from a Reader.
	 *
	 * @param in
	 * The Reader to use as input.
	 *
	 * @return
	 * A String containing the values of an entire item.
	 */
	private String readItem(Reader in) throws IOException {
		StringBuffer readString = new StringBuffer();
		int c;
		
		while((c = in.read()) != -1) {
			if((char)c != itemSeperator) {
				readString.append((char)c);
			} else {
				break;
			}
		}
		
		String output = readString.toString();
		
		if(c == -1) {
			readComplete = true;
			String sep = (new Character(valueSeperator)).toString();
			if(output.indexOf(sep) == -1) {
				return null;
			}
		}
		
		return output;
	}
}
