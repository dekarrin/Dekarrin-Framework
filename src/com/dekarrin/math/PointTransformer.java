package com.dekarrin.math;

import static java.lang.Math.*;

/**
 * Transforms points using matrix math.
 */
public class PointTransformer {
	
	/**
	 * The number of dimensions that this TransformationMatrix
	 * can transform.
	 */
	private int dimensions;
	
	/**
	 * Creates a new PointTransformer for {@code d}-dimensional
	 * Points.
	 * 
	 * @param d
	 * The dimensions that this PointTransformer will be
	 * operating on.
	 */
	public PointTransformer(int d) {
		dimensions = d;
	}
	
	/**
	 * Creates a new PointTransformer for the default dimension
	 * of 2.
	 */
	public PointTransformer() {
		this(2);
	}
	
	/**
	 * Scales a point.
	 * 
	 * @param p
	 * The point to scale.
	 * 
	 * @param factors
	 * The factors used for each dimension. If there are fewer
	 * factors than dimensions, the last-given factor will be
	 * used for the dimensions that are missing factors.
	 * 
	 * @return
	 * The scaled point.
	 */
	public Point scale(Point p, double... factors) {
		Matrix transformer = createScalingMatrix(factors);
		Matrix point = createPointVector(p);
		transformer.multiply(point);
		setPointFromMatrix(p, transformer);
		return p;
	}
	
	/**
	 * Rotates a point about the origin. This operation
	 * is only applied to the first two dimensions of
	 * the given Point.
	 * 
	 * @param p
	 * The Point to rotate.
	 * 
	 * @param angle
	 * The amount to rotate the point by.
	 * 
	 * @return
	 * The rotated Point.
	 */
	public Point rotate(Point p, double angle) {
		Matrix transformer = createRotationMatrix(angle);
		Matrix point = createPointVector(p);
		transformer.multiply(point);
		setPointFromMatrix(p, transformer);
		return p;
	}
	
	/**
	 * Rotates a point about the origin. This operation
	 * is only applied to the first three dimensions of
	 * the given Point.
	 * 
	 * @param p
	 * The Point to rotate.
	 * 
	 * @param angle
	 * The amount to rotate the point by.
	 * 
	 * @return
	 * The rotated Point.
	 */
	public Point rotate3d(Point p, double angleX, double angleY, double angleZ) {
		Matrix transformer = createRotationMatrix(angleX, angleY, angleZ);
		Matrix point = createPointVector(p);
		transformer.multiply(point);
		setPointFromMatrix(p, transformer);
		return p;
	}
	
	/**
	 * Creates a column vector from a Point.
	 * 
	 * @param p
	 * The point to create the vector from.
	 * 
	 * @return
	 * The column vector as a Matrix.
	 */
	private Matrix createPointVector(Point p) {
		Matrix vector = new Matrix(dimensions, 1);
		for(int i = 0; i < dimensions; i++) {
			vector.set(i, 0, p.get(i));
		}
		return vector;
	}
	
	/**
	 * Creates a Matrix used for scaling.
	 * 
	 * @param factors
	 * The factors for each dimension.
	 * 
	 * @return
	 * A Matrix with the appropriate number of values
	 * in order to scale a Point of the same number
	 * of dimensions as this PointTransformer.
	 */
	private Matrix createScalingMatrix(double[] factors) {
		Matrix scaler = new Matrix(dimensions, dimensions);
		for(int i = 0; i < dimensions; i++) {
			double f = (i < factors.length) ? factors[i] : factors[factors.length-1];
			double[] row = new double[dimensions];
			row[i] = f;
			scaler.setRow(i, row);
		}
		return scaler;
	}
	
	/**
	 * Creates a Matrix used for 2D rotation.
	 * 
	 * @param angle
	 * The angle to rotate by.
	 * 
	 * @return
	 * A Matrix to rotate points with.
	 */
	private Matrix createRotationMatrix(double angle) {
		Matrix rotater = new Matrix(2, 2);
		rotater.setRow(0, cos(angle), -sin(angle));
		rotater.setRow(1, sin(angle), cos(angle));
		return rotater;
	}
	
	/**
	 * Creates a Matrix used for 3D rotation.
	 * 
	 * @param angleX
	 * The angle to rotate about the x-axis.
	 * 
	 * @param angleY
	 * The angle to rotate about the y-axis.
	 * 
	 * @param angleZ
	 * The angle to rotate about the z-axis.
	 * 
	 * @return
	 * A Matrix rotate 3D points with.
	 */
	private Matrix createRotationMatrix(double angleX, double angleY, double angleZ) {
		Matrix rotater = new IdentityMatrix(3);
		Matrix yaw = createYawMatrix(angleZ);
		Matrix pitch = createPitchMatrix(angleX);
		Matrix roll = createRollMatrix(angleY);
		rotater.multiply(yaw).multiply(pitch).multiply(roll);
		return rotater;
	}
	
	/**
	 * Creates a Matrix used for 3D rotation about the
	 * z-axis.
	 * 
	 * @param angle
	 * 
	 * @return
	 * A Matrix rotate 3D points with.
	 */
	private Matrix createYawMatrix(double angle) {
		Matrix yaw = new Matrix(3, 3);
		yaw.setRow(0, cos(angle), -sin(angle), 0);
		yaw.setRow(1, sin(angle), cos(angle), 0);
		yaw.setRow(2, 0, 0, 1);
		return yaw;
	}
	
	/**
	 * Creates a Matrix used for 3D rotation about the
	 * y-axis.
	 * 
	 * @param angle
	 * 
	 * @return
	 * A Matrix rotate 3D points with.
	 */
	private Matrix createPitchMatrix(double angle) {
		Matrix pitch = new Matrix(3, 3);
		pitch.setRow(0, cos(angle), 0, sin(angle));
		pitch.setRow(1, 0, 1, 0);
		pitch.setRow(2, -sin(angle), 0, cos(angle));
		return pitch;
	}
	
	/**
	 * Creates a Matrix used for 3D rotation about the
	 * x-axis.
	 * 
	 * @param angle
	 * 
	 * @return
	 * A Matrix rotate 3D points with.
	 */
	private Matrix createRollMatrix(double angle) {
		Matrix roll = new Matrix(3, 3);
		roll.setRow(0, 1, 0, 0);
		roll.setRow(1, 0, cos(angle), -sin(angle));
		roll.setRow(2, 0, sin(angle), cos(angle));
		return roll;
	}
	
	/**
	 * Sets the coordinates of a Point to the values in
	 * a column vector.
	 * 
	 * @param p
	 * The Point to set.
	 * 
	 * @param m
	 * The Matrix to act as a column vector.
	 * 
	 * @return
	 * The Point with its coordinates set.
	 */
	private Point setPointFromMatrix(Point p, Matrix m) {
		for(int i = 0; i < dimensions; i++) {
			p.set(i, m.get(i, 0));
		}
		return p;
	}
}
