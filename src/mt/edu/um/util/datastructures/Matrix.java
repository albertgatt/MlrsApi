package mt.edu.um.util.datastructures;

import java.util.ArrayList;
import java.util.List;

public class Matrix {

	private double[] data;
	private int numRows;
	private int numColumns;
	private double[] colTotals;
	private double[] rowTotals;

	public Matrix(int numRows, int numColumns) {
		this.numColumns = numColumns;
		this.numRows = numRows;
		this.data = new double[numRows * numColumns];
		this.colTotals = new double[numColumns];
		this.rowTotals = new double[numRows];
	}

	public Matrix(Matrix toCopy) {
		this(toCopy.numRows(), toCopy.numColumns());
		this.data = toCopy.data;
	}
	
	public void fill(double value) {
		for(int i = 0; i < this.numRows; i++) {
			for(int j = 0; j < this.numColumns; j++) {
				this.set(i, j, value);
			}
		}
	}
	
	public void fillRow(int row, double value) {
		for(int i = 0; i < this.numColumns; i++) {
			this.set(row, i, value);
		}
	}
	
	public void fillColumn(int col, double value) {
		for(int i = 0; i < this.numRows; i++) {
			this.set(i, col, value);
		}
	}

	public int numRows() {
		return this.numRows;
	}

	public int numColumns() {
		return this.numColumns;
	}

	/**
	 * Returns the matrix contents. Ordering depends on the underlying storage
	 * assumptions
	 */
	public double[] getData() {
		return data;
	}

	public void add(int row, int column, double value) {
		data[getIndex(row, column)] += value;
		this.colTotals[column] += value;
	}

	public void set(int row, int column, double value) {
		data[getIndex(row, column)] = value;
		this.colTotals[column] += value;
		this.rowTotals[row] += value;
	}

	public double[] getRowValues(int row) {
		double[] values = new double[this.numColumns];

		for (int col = 0; col < this.numColumns; col++) {
			values[col] = data[getIndex(row, col)];
		}

		return values;
	}
	
	public List<Double> getRowValuesAsList(int row) {
		List<Double> values = new ArrayList<Double>();
		
		for (int col = 0; col < this.numColumns; col++) {
			values.add(col, data[getIndex(row, col)]);
		}

		return values;
	}

	public double[] getColumnValues(int column) {
		double[] values = new double[this.numRows];

		for (int row = 0; row < this.numRows; row++) {
			values[row] = data[getIndex(row, column)];
		}

		return values;
	}
	
	public List<Double> getColumnValuesAsList(int column) {
		List<Double> values = new ArrayList<Double>();
		
		for (int row = 0; row < this.numRows; row++) {
			values.add(row, data[getIndex(row, column)]);
		}

		return values;
	}


	public void multiply(int row, int column, double value) {
		int index = getIndex(row, column);
		double originalColValue = data[index];
		double result = originalColValue * value;
		data[index] = result;
		this.colTotals[column] -= originalColValue;
		this.colTotals[column] += result;
	}

	public void divide(int row, int column, double value) {
		int index = getIndex(row, column);
		double originalColValue = data[index];
		double result = originalColValue / value;
		data[index] = result;
		this.colTotals[column] -= originalColValue;
		this.colTotals[column] += result;
	}

	public double get(int row, int column) {
		return data[getIndex(row, column)];
	}

	public double cosine(int row1, int row2) {
		return dotProduct(row1, row2) / (size(row1) * size(row2));
	}

	public double size(int row) {
		double[] vector = vector(row);
		double size = 0.0D;

		for (double d : vector) {
			size += Math.pow(d, 2);
		}

		return Math.sqrt(size);
	}

	public double dotProduct(int row1, int row2) {
		double[] vector1 = vector(row1);
		double[] vector2 = vector(row2);
		double dotProduct = 0.0D;

		for (int i = 0; i < vector1.length; i++) {
			dotProduct += (vector1[i] * vector2[i]);
		}

		return dotProduct;
	}

	public double dice(int row1, int row2) {
		double[] vector1 = vector(row1);
		double[] vector2 = vector(row2);
		double common = 0.0D;
		double v1Length = 0.0D;
		double v2Length = 0.0D;

		for (int i = 0; i < numColumns; i++) {
			if (vector1[i] != 0 && vector2[i] != 0) {
				common++;
				v1Length++;
				v2Length++;
			} else if (vector1[i] != 0) {
				v1Length++;
			} else if (vector2[i] != 0) {
				v2Length++;
			}
		}

		return (2 * common) / (v1Length + v2Length);
	}

	public double jaccard(int row1, int row2) {
		double[] vector1 = vector(row1);
		double[] vector2 = vector(row2);
		double intersection = 0.0D;
		double union = 0.0D;

		for (int i = 0; i < numColumns; i++) {
			if (vector1[i] != 0 || vector2[i] != 0) {
				union++;

				if (vector1[i] != 0 && vector2[i] != 0) {
					intersection++;
				}
			}
		}

		return intersection / union;

	}

	public double[] vector(int row) {
		double[] vector = new double[this.numColumns];

		for (int i = 0; i < this.numColumns; i++) {
			vector[i] = get(row, i);
		}

		return vector;
	}

	public double[] averageVector(int row1, int row2) {
		double[] vector = new double[this.numColumns];

		for (int i = 0; i < this.numColumns; i++) {
			vector[i] = (get(row1, i) + get(row2, i)) / 2;
		}

		return vector;
	}

	public double columnTotal(int col) {
		return this.colTotals[col];
	}

	public double rowTotal(int row) {
		return this.rowTotals[row];
	}

	/**
	 * Checks the row and column indices, and returns the linear data index
	 */
	private int getIndex(int row, int column) {
		return row + column * numRows;
	}

}
