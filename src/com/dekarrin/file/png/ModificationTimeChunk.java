package com.dekarrin.file.png;

import java.util.Date;

/**
 * Holds last modification time information.
 */
public class ModificationTimeChunk extends AncillaryChunk {

	/**
	 * The type code for this chunk.
	 */
	public static final byte[] TYPE_CODE = {116, 73, 77, 69}; // tIME
	
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
	 * Creates a new ModificationTimeChunk.
	 *
	 * @param data
	 * The chunk data.
	 *
	 * @param crc
	 * The chunk CRC.
	 */
	public ModificationTimeChunk(byte[] data, long crc) {
		super(TYPE_CODE, data, crc);
		parseData();
	}
	
	/**
	 * Creates a new ModificationTimeChunk from a date.
	 *
	 * @param modificationDate
	 * The date to set this ModificationTimeChunk to.
	 */
	public ModificationTimeChunk(Date modificationDate) {
		super(TYPE_CODE);
		Calendar c = Calendar.getInstance();
		c.setTime(modificationDate);
		setProperties(	c.get(Calendar.YEAR), c.get(Calendar.MONTH),
						c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY),
						c.get(Calendar.MINUTE), c.get(Calendar.SECOND)
					);
		setChunkData(createDataBytes());
	}
	
	/**
	 * Gets the year of the last modification.
	 *
	 * @return
	 * The year.
	 */
	public int getYear() {
		return year;	
	}
	
	/**
	 * Gets the month of the last modification.
	 *
	 * @return
	 * The month.
	 */
	public int getMonth() {
		return month;	
	}
	
	/**
	 * Gets the day of the last modification.
	 *
	 * @return
	 * The day.
	 */
	public int getDay() {
		return day;	
	}
	
	/**
	 * Gets the hour of the last modification.
	 *
	 * @return
	 * The hour.
	 */
	public int getHour() {
		return hour;	
	}
	
	/**
	 * Gets the minute of the last modification.
	 *
	 * @return
	 * The minute.
	 */
	public int getMinute() {
		return minute;
	}
	
	/**
	 * Gets the second of the last modification.
	 *
	 * @return
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
	
	/**
	 * Sets the internal properties of this chunk.
	 *
	 * @param year
	 * The year of the last modification.
	 *
	 * @param month
	 * The month of the last modification.
	 *
	 * @param day
	 * The day of the month of the last modification.
	 *
	 * @param hour
	 * The hour of the last modification.
	 *
	 * @param min
	 * The minute of the last modification.
	 *
	 * @param sec
	 * The second of the last modification.
	 */
	private void setProperties(int year, int month, int day, int hour, int min, int sec) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.hour = hour;
		this.minute = min;
		this.second = sec;
	}
	
	/**
	 * Creates the data byte array for this chunk.
	 *
	 * @return
	 * The data byte array.
	 */
	private byte[] createDataBytes() {
		ByteComposer composer = new ByteComposer(7);
		composer.composeInt(year, 2);
		composer.composeInt(month, 1);
		composer.composeInt(day, 1);
		composer.composeInt(hour, 1);
		composer.composeInt(minute, 1);
		composer.composeInt(second, 1);
		return composer.toArray();
	}
}