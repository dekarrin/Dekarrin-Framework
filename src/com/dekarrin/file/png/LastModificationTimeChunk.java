package com.dekarrin.file.png;

/**
 * Holds last modification time information.
 */
public class LastModificationTimeChunk extends AncillaryChunk {
	
	/**
	 * The year of the last modification.
	 */
	private int year;
	
	/**
	 * The month of the last modification.
	 */
	private int month;
	
	/**
	 * The day of the last modification.
	 */
	private int day;
	
	/**
	 * The hour of the last modification.
	 */
	private int hour;
	
	/**
	 * The minute of the last modification.
	 */
	private int minute;
	
	/**
	 * The second of the last modification.
	 */
	private int second;
	
	/**
	 * Creates a new LastModificationTimeChunk.
	 *
	 * @param data
	 * The chunk data.
	 *
	 * @param crc
	 * The chunk CRC.
	 */
	public LastModificationTimeChunk(byte[] data, int crc) {
		super(new byte[]{116, 73, 77, 69}, data, crc);
		parseData();
	}
	
	/**
	 * Gets the year of the last modification.
	 *
	 * @returns
	 * The year.
	 */
	public int getYear() {
		return year;	
	}
	
	/**
	 * Gets the month of the last modification.
	 *
	 * @returns
	 * The month.
	 */
	public int getMonth() {
		return month;	
	}
	
	/**
	 * Gets the day of the last modification.
	 *
	 * @returns
	 * The day.
	 */
	public int getDay() {
		return day;	
	}
	
	/**
	 * Gets the hour of the last modification.
	 *
	 * @returns
	 * The hour.
	 */
	public int getHour() {
		return hour;	
	}
	
	/**
	 * Gets the minute of the last modification.
	 *
	 * @returns
	 * The minute.
	 */
	public int getMinute() {
		return minute;
	}
	
	/**
	 * Gets the second of the last modification.
	 *
	 * @returns
	 * The second.
	 */
	public int getSecond() {
		return second;	
	}
	
	/**
	 * Parses the chunk data into properties.
	 */
	private void parseData() {
		year	= parser.parseInt(2);
		month	= parser.parseInt(1);
		day		= parser.parseInt(1);
		hour	= parser.parseInt(1);
		minute	= parser.parseInt(1);
		second	= parser.parseInt(1);
	}
}