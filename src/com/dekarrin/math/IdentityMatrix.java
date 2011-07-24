package com.dekarrin.math;

public final class IdentityMatrix extends Matrix {

	/**
	 * Creates a new IdentityMatrix.
	 * 
	 * @param size
	 * The size of the IdentityMatrix.
	 */
	public IdentityMatrix(int size) {
		super(size, size);
		setContents();
	}
	
	/**
	 * Sets the contents of this IdentityMatrix.
	 */
	private void setContents() {
		for(int i = 0; i < rows(); i++) {
			double[] row = new double[columns()];
			row[i] = 1;
			setRow(i, row);
		}
	}
}
