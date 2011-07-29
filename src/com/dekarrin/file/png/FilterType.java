package com.dekarrin.file.png;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.dekarrin.util.ArrayHelper;

/**
 * The filter algorithms for the adaptive filter method.
 */
public enum FilterType {
	
	/**
	 * No filtering is applied. The only difference between
	 * data filtered with the NONE algorithm and unfiltered
	 * data is that the filtered data is prepended by a
	 * filter-type byte.
	 */
	NONE(0) {
		public byte[] filter(byte[] raw, byte[] lastLine, int bpp) {
			return raw;
		}
		public byte[] unfilter(byte[] none, byte[] lastLine, int bpp) {
			return none;
		}
	},
	
	/**
	 * Converts each byte to the difference
	 * between the current byte and the previous byte.
	 */
	SUB(1) {
		public byte[] filter(byte[] raw, byte[] lastLine, int bpp) {
			byte[] sub = new byte[raw.length];
			for(int i = 0; i < bpp && i < raw.length; i++) {
				sub[i] = raw[i];
			}
			for(int i = bpp; i < sub.length; i++) {
				sub[i] = (byte)((raw[i] - raw[i-bpp]) % 256);
			}
			return sub;
		}
		public byte[] unfilter(byte[] sub, byte[] lastLine, int bpp) {
			byte[] raw = new byte[sub.length];
			for(int i = 0; i < bpp && i < sub.length; i++) {
				raw[i] = sub[i];
			}
			for(int i = bpp; i < sub.length; i++) {
				raw[i] = (byte)((sub[i] + raw[i-bpp]) % 256);
			}
			return raw;
		}
	},
	
	/**
	 * Converts each byte to the difference between the current
	 * byte and the corresponding byte in the previous scanline.
	 */
	UP(2) {
		public byte[] filter(byte[] raw, byte[] lastLine, int bpp) {
			byte[] prior = (lastLine != null) ? lastLine : new byte[raw.length];
			byte[] up = new byte[raw.length];
			for(int i = 0; i < raw.length; i++) {
				up[i] = (byte)((raw[i] - prior[i]) % 256);
			}
			return up;
		}
		public byte[] unfilter(byte[] up, byte[] lastLine, int bpp) {
			byte[] prior = (lastLine != null) ? lastLine : new byte[up.length];
			byte[] raw = new byte[up.length];
			for(int i = 0; i < up.length; i++) {
				raw[i] = (byte)((up[i] + prior[i]) % 256);
			}
			return raw;
		}
	},
	
	/**
	 * Converts each byte to the average difference between the
	 * current byte, the previous byte, and the corresponding
	 * byte in the previous scanline.
	 */
	AVERAGE(3) {
		public byte[] filter(byte[] raw, byte[] lastLine, int bpp) {
			byte[] prior = (lastLine != null) ? lastLine : new byte[raw.length];
			byte[] average = new byte[raw.length];
			for(int i = 0; i < bpp && i < raw.length; i++) {
				average[i] = (byte)((raw[i] - calculateAverage((byte)0, prior[i])) % 256);
			}
			for(int i = bpp; i < raw.length; i++) {
				average[i] = (byte)((raw[i] - calculateAverage(raw[i-bpp], prior[i])) % 256);
			}
			return average;
		}
		public byte[] unfilter(byte[] average, byte[] lastLine, int bpp) {
			byte[] prior = (lastLine != null) ? lastLine : new byte[average.length];
			byte[] raw = new byte[average.length];
			for(int i = 0; i < bpp && i < average.length; i++) {
				raw[i] = (byte)((average[i] + calculateAverage((byte)0, prior[i])) % 256);
			}
			for(int i = bpp; i < average.length; i++) {
				raw[i] = (byte)((average[i] + calculateAverage(raw[i-bpp], prior[i])) % 256);
			}
			return raw;
		}
		private int calculateAverage(byte left, byte above) {
			int l = (int)left & 0xff;
			int a = (int)above & 0xff;
			int avg = (int)Math.floor((l+a)/2);
			return avg;
		}
	},
	
	/**
	 * Converts each byte to the most efficient difference by
	 * using the Paeth algorithm.
	 */
	PAETH(4) {
		public byte[] filter(byte[] raw, byte[] lastLine, int bpp) {
			byte[] prior = (lastLine != null) ? lastLine : new byte[raw.length];
			byte[] paeth = new byte[raw.length];
			for(int i = 0; i < bpp && i < raw.length; i++) {
				paeth[i] = (byte)((raw[i] - paethPredictor((byte)0, prior[i], (byte)0)) % 256);
			}
			for(int i = bpp; i < raw.length; i++) {
				paeth[i] = (byte)((raw[i] - paethPredictor(raw[i-bpp], prior[i], prior[i-bpp])) % 256);
			}
			return paeth;
		}
		public byte[] unfilter(byte[] paeth, byte[] lastLine, int bpp) {
			byte[] prior = (lastLine != null) ? lastLine : new byte[paeth.length];
			byte[] raw = new byte[paeth.length];
			for(int i = 0; i < bpp && i < paeth.length; i++) {
				raw[i] = (byte)((paeth[i] + paethPredictor((byte)0, prior[i], (byte)0)) % 256);
			}
			for(int i = bpp; i < paeth.length; i++) {
				raw[i] = (byte)((paeth[i] + paethPredictor(raw[i-bpp], prior[i], prior[i-bpp])) % 256);
			}
			return raw;
		}
		private int paethPredictor(byte left, byte above, byte upperLeft) {
			int predictor;
			int a,b,c; // results are off if not properly cast to ints
			a = (int)left & 0xff;
			b = (int)above & 0xff;
			c = (int)upperLeft & 0xff;
			int paeth = a + b - c;
			int pa = Math.abs(paeth - a);
			int pb = Math.abs(paeth - b);
			int pc = Math.abs(paeth - c);
			if(pa <= pb && pa <= pc) {
				predictor = a;
			} else if(pb <= pc) {
				predictor = b;
			} else {
				predictor = c;
			}
			return predictor;
		}
	};
	
	/**
	 * Maps data values to FilterTypes.
	 */
	private static final Map<Integer,FilterType> dataTable;
	
	static {
		dataTable = new HashMap<Integer,FilterType>();
		for(FilterType cm: EnumSet.allOf(FilterType.class)) {
			dataTable.put(cm.dataValue(), cm);
		}
	}
	
	/**
	 * The value that is written to disk for this FilterType.
	 */
	private int dataValue;
	
	/**
	 * Creates a new FilterType.
	 * 
	 * @param value
	 * The value of this FilterType as it is written to disk.
	 */
	private FilterType(int value) {
		dataValue = value;
	}
	
	/**
	 * Gets a FilterType from a data value read from disk.
	 * 
	 * @param dataValue
	 * The data value of the desired FilterType.
	 * 
	 * @return
	 * The FilterType that has the given data value.
	 */
	public static FilterType fromData(int dataValue) {
		return dataTable.get(dataValue);
	}
	
	/**
	 * Gets the value of the filter method to write to a PNG
	 * file.
	 * 
	 * @return
	 * What value to write for this FilterType.
	 */
	public int dataValue() {
		return dataValue;
	}
	
	/**
	 * Chooses a filter method based on which one produces the smallest
	 * running total. This heuristic is taken from the PNG specification.
	 *
	 * @param unfiltered
	 * The data that is to be filtered.
	 * 
	 * @param lastLine
	 * The unfiltered previous bytes.
	 * 
	 * @param bpp
	 * The number of bytes per pixel.
	 *
	 * @return
	 * The number representing the filter algorithm to use.
	 */
	public static FilterType choose(byte[] unfiltered, byte[] lastLine, int bpp) {
		int total = 0;
		int nextTotal = 0;
		boolean set = false;
		FilterType type = null;
		for(FilterType ft: EnumSet.allOf(FilterType.class)) {
			nextTotal = (int)ArrayHelper.sum(ft.filter(unfiltered, lastLine, bpp));
			if(nextTotal < total || !set) {
				type = ft;
				total = nextTotal;
				set = true;
			}
		}
		return type;
	}
	
	/**
	 * Filters data according to this FilterType.
	 * 
	 * @param raw
	 * The unfiltered data.
	 * 
	 * @param lastLine
	 * The unfiltered previous bytes.
	 * 
	 * @param bpp
	 * The number of bytes per pixel.
	 * 
	 * @return
	 * The filtered data.
	 */
	public abstract byte[] filter(byte[] raw, byte[] lastLine, int bpp);
	
	/**
	 * Unfilters data according to this FilterType.
	 * 
	 * @param unraw
	 * The filtered data.
	 * 
	 * @param lastLine
	 * The unfiltered previous bytes.
	 * 
	 * @param bpp
	 * The number of bytes per pixel.
	 * 
	 * @return
	 * The unfiltered data.
	 */
	public abstract byte[] unfilter(byte[] unraw, byte[] lastLine, int bpp);
}