package com.dekarrin.math;

/**
 * Creates a Matrix for math operations.
 */
public class Matrix implements Cloneable {
	
	/**
	 * The array that represents the matrix.
	 */
	private double[][] innerMatrix;
	
	/**
	 * Creates a Matrix from a 2-dimensional array. The
	 * new Matrix will have the same number of columns as
	 * the given array's length, and the same number of
	 * rows as the array's first element's length.
	 * 
	 * @param elements
	 * The array to build the matrix around.
	 */
	public Matrix(double[][] elements) {
		this(elements[0].length, elements.length);
		int columns = elements.length;
		int rows = elements[0].length;
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				set(i, j, elements[j][i]);
			}
		}
	}
	
	/**
	 * Creates a new matrix. All elements are set
	 * to 0.
	 * 
	 * @param rows
	 * The number of rows in the Matrix.
	 * 
	 * @param columns
	 * The number of columns in the Matrix.
	 */
	public Matrix(int rows, int columns) {
		innerMatrix = new double[rows][columns];
	}
	
	/**
	 * Gets the width of this Matrix.
	 * 
	 * @return
	 * How many columns there are.
	 */
	public int columns() {
		return innerMatrix[0].length;
	}
	
	/**
	 * Gets the height of this Matrix.
	 * 
	 * @return
	 * How many rows there are.
	 */
	public int rows() {
		return innerMatrix.length;
	}
	
	/**
	 * Sets a value at a point.
	 * 
	 * @param row
	 * The row of the value to set.
	 * 
	 * @param col
	 * The column of the value to set.
	 * 
	 * @param value
	 * The value to set.
	 * 
	 * @return
	 * This Matrix.
	 */
	public Matrix set(int row, int col, double value) {
		innerMatrix[row][col] = value;
		return this;
	}
	
	/**
	 * Sets the values of a row.
	 * 
	 * @param row
	 * The row to set.
	 * 
	 * @param values
	 * An array containing the values to set in the
	 * row. Only the values corresponding to actual
	 * columns in this Matrix will be used; if this
	 * array is less than the number of columns in
	 * this Matrix, the missing values are assumed
	 * to be the same as those already defined at
	 * those positions.
	 * 
	 * @return
	 * This Matrix.
	 */
	public Matrix setRow(int row, double... values) {
		for(int i = 0; i < values.length && i < columns(); i++) {
			set(row, i, values[i]);
		}
		return this;
	}
	
	/**
	 * Sets the values of a column.
	 * 
	 * @param col
	 * The column to set.
	 * 
	 * @param values
	 * An array containing the values to set in the
	 * column. Only the values corresponding to actual
	 * rows in this Matrix will be used; if this
	 * array is less than the number of rows in
	 * this Matrix, the missing values are assumed
	 * to be the same as those already defined at
	 * those positions.
	 * 
	 * @return
	 * This Matrix.
	 */
	public Matrix setColumn(int col, double... values) {
		for(int i = 0; i < values.length && i < rows(); i++) {
			set(i, col, values[i]);
		}
		return this;
	}
	
	/**
	 * Gets the value at a point.
	 * 
	 * @param row
	 * The row of the value to get.
	 * 
	 * @param col
	 * The column of the value to get.
	 * 
	 * @return
	 * The value at the specified position.
	 */
	public double get(int row, int col) {
		return innerMatrix[row][col];
	}
	
	/**
	 * Gets the values in a row.
	 * 
	 * @param row
	 * The row of the values to get.
	 * 
	 * @return
	 * The values in the specified row.
	 */
	public double[] getRow(int row) {
		return getRow(row, 0, columns());
	}
	
	/**
	 * Gets a segment of a row of values. The length
	 * of the segment will be {@code end}-{@code start}.
	 * The elements in the specified row are copied,
	 * starting with the element in the column with the index
	 * of {@code start} and ending with the element in the
	 * column with the index of {@code end}-1. 0 is used for
	 * any value that falls outside of the range of this
	 * Matrix.
	 * 
	 * @param row
	 * The row of the values to get.
	 * 
	 * @param start
	 * The index of the first element to get.
	 * 
	 * @param end
	 * The index of the end of the segment.
	 * 
	 * @return
	 * The specified segment of the specified row.
	 */
	public double[] getRow(int row, int start, int end) {
		int length = start-end;
		double[] rowValues = new double[length];
		for(int i = start; i < end; i++) {
			if(i < columns()) {
				rowValues[i] = get(row, i);
			} else {
				rowValues[i] = 0;
			}
		}
		return rowValues;
	}
	
	/**
	 * Gets the values in a column.
	 * 
	 * @param col
	 * The column of the values to get.
	 * 
	 * @return
	 * The values in the specified column.
	 */
	public double[] getColumn(int col) {
		return getColumn(col, 0, rows());
	}
	
	/**
	 * Gets a segment of a column of values. The length
	 * of the segment will be {@code end}-{@code start}.
	 * The elements in the specified column are copied,
	 * starting with the element in the row with the index
	 * of {@code start} and ending with the element in the
	 * row with the index of {@code end}-1. 0 is used for
	 * any value that falls outside of the range of this
	 * Matrix.
	 * 
	 * @param col
	 * The column of the values to get.
	 * 
	 * @param start
	 * The index of the first element to get.
	 * 
	 * @param end
	 * The index of the end of the segment.
	 * 
	 * @return
	 * The specified segment of the specified column.
	 */
	public double[] getColumn(int col, int start, int end) {
		int length = start-end;
		double[] colValues = new double[length];
		for(int i = start; i < end; i++) {
			if(i < rows()) {
				colValues[i] = get(i, col);
			} else {
				colValues[i] = 0;
			}
		}
		return colValues;
	}
	
	/**
	 * Adds a Matrix to this one. The two Matrices are
	 * added together, and the sum is set to this
	 * Matrix.
	 * 
	 * @param m2
	 * The Matrix to add to this one. If {@code m2} is
	 * smaller than this Matrix, 0 will be assumed for
	 * the missing values. If it is larger, only the
	 * values that correspond to this Matrix's values
	 * will be used.
	 * 
	 * @return
	 * This Matrix.
	 */
	public Matrix add(Matrix m2) {
		double add, sum;
		for(int i = 0; i < rows(); i++) {
			for(int j = 0; j < columns(); j++) {
				try {
					add = m2.get(i, j);
				} catch(NullPointerException e) {
					add = 0.0;
				}
				sum = get(i, j) + add;
				set(i, j, sum);
			}
		}
		return this;
	}
	
	/**
	 * Adds a scalar to this Matrix.
	 * 
	 * @param scalar
	 * The value to add to each of the elements.
	 * 
	 * @return
	 * This Matrix.
	 */
	public Matrix add(double scalar) {
		double sum;
		for(int i = 0; i < rows(); i++) {
			for(int j = 0; j < columns(); j++) {
				sum = get(i, j) + scalar;
				set(i, j, sum);
			}
		}
		return this;
	}
	
	/**
	 * Multiplies this Matrix by another matrix. The
	 * two Matrices are multiplied together, and the
	 * product is set to this Matrix.
	 * 
	 * The multiplication operation will only take
	 * place if the number of rows in {@code m2} is
	 * the same as the number of columns in this
	 * Matrix. If this is not the case, no
	 * multiplication is performed.
	 * 
	 * @param m2
	 * The Matrix to multiply this one by. {@code m2}
	 * must have exactly as many rows as this Matrix
	 * has columns.
	 * 
	 * @return
	 * This Matrix.
	 */
	public Matrix multiply(Matrix m2) {
		if(m2.rows() == columns()) {
			double[][] product = new double[rows()][m2.columns()];
			int runningTotal;
			for(int m1Row = 0; m1Row < rows(); m1Row++) {
				for(int m2Col = 0; m2Col < m2.columns(); m2Col++) {
					runningTotal = 0;
					for(int i = 0; i < columns(); i++) {
						runningTotal += get(m1Row, i) * m2.get(i, m2Col);
					}
					product[m1Row][m2Col] = runningTotal;
				}
			}
			innerMatrix = product;
		}
		return this;
	}
	
	/**
	 * Multiplies the contents of this Matrix by a
	 * scalar.
	 * 
	 * @param scalar
	 * The value to multiply the contents by.
	 * 
	 * @return
	 * This Matrix.
	 */
	public Matrix multiply(double scalar) {
		double product;
		for(int i = 0; i < rows(); i++) {
			for(int j = 0; j < columns(); j++) {
				product = get(i, j) * scalar;
				set(i, j, product);
			}
		}
		return this;
	}
	
	/**
	 * Transposes this Matrix so that the rows are turned
	 * into columns and vice versa.
	 * 
	 * @return
	 * This Matrix.
	 */
	public Matrix transpose() {
		double[][] transposed = transposeInnerMatrix();
		innerMatrix = transposed;
		return this;
	}
	
	/**
	 * Converts this Matrix into an array.
	 *
	 * @return
	 * An array holding the values of this
	 * Matrix.
	 */
	public double[][] toArray() {
		return transposeInnerMatrix();
	}
	
	/**
	 * Duplicates this Matrix.
	 * 
	 * @return
	 * A clone of this Matrix.
	 */
	public Matrix clone() {
		Matrix m2 = new Matrix(rows(), columns());
		for(int i = 0; i < rows(); i++) {
			for(int j = 0; j < columns(); j++) {
				double value = get(i, j);
				m2.set(i, j, value);
			}
		}
		return m2;
	}
	
	/**
	 * Transposes the inner matrix array. This will
	 * switch the rows with the columns and vice
	 * versa.
	 * 
	 * @return
	 * The transposed array.
	 */
	private double[][] transposeInnerMatrix() {
		double[][] trans = new double[columns()][rows()];
		for(int i = 0; i < rows(); i++) {
			for(int j = 0; j < columns(); j++) {
				trans[j][i] = get(i, j);
			}
		}
		return trans;
	}
}
