package com.dekarrin.program;

import com.dekarrin.math.Matrix;
import com.dekarrin.util.HelperString;

public class MatrixTest extends ConsoleProgram {

	public static void main(String[] args) {
		new MatrixTest(args);
	}
	
	public MatrixTest(String[] args) {
		super(args);
		Matrix m1 = new Matrix(2, 3);
		Matrix m2 = new Matrix(3, 2);
		
		m1.setRow(0, 1, 0, 2);
		m1.setRow(1, -1, 3, 1);
		
		m2.setRow(0, 3, 1);
		m2.setRow(1, 2, 1);
		m2.setRow(2, 1, 0);
		
		m1.multiply(3);
		printMatrix(m1);
	}
	
	private void printMatrix(Matrix m) {
		String rowDivider = makeDivider(m.columns());
		StringBuffer output = new StringBuffer(rowDivider + "\n");
		for(int row = 0; row < m.rows(); row++) {
			StringBuffer rowString = new StringBuffer();
			for(int col = 0; col < m.columns(); col++) {
				HelperString s = new HelperString(""+m.get(row, col));
				s.padBoth(6);
				rowString.append("|"+s.toString());
			}
			output.append(rowString.toString() + "|\n");
			output.append(rowDivider +"\n");
		}
		ui.print(output.toString());
	}
	
	private String makeDivider(int cols) {
		StringBuffer l = new StringBuffer();
		for(int i = 0; i < cols; i++) {
			l.append("+------");
		}
		l.append("+");
		return l.toString();
	}
}
