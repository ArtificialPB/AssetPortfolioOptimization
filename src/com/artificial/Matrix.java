package com.artificial;

import java.util.Arrays;

public class Matrix {
    private final int columns, rows;
    private final double[][] data;

    public Matrix(int rows, int columns) {
        this(new double[rows][columns]);
    }

    public Matrix(final double[] data) {
        this(new double[][]{data});
    }

    public Matrix(final double[][] data) {
        this.data = data;
        this.rows = data.length;
        this.columns = data[0].length;
    }

    /**
     * Returns a matrix with all of the elements equalling to value
     */
    public static Matrix unitMatrix(final int rows, final int columns, final double value) {
        final double[][] data = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                data[i][j] = value;
            }
        }
        return new Matrix(data);
    }

    private static Matrix subMatrix(Matrix curr, int row, int col) {
        final Matrix ret = new Matrix(curr.getRows() - 1, curr.getColumns() - 1);
        int row_index = 0, col_index = 0;
        for (int row_ = 0; row_ < curr.getRows(); row_++) {
            if (row_ == row) continue;
            for (int col_ = 0; col_ < curr.getColumns(); col_++) {
                if (col_ == col) continue;
                ret.put(curr.get(row_, col_), row_index, col_index);
                col_index++;
            }
            row_index++;
            col_index = 0;
        }
        return ret;
    }

    private static double determinant_3x3(final Matrix m) {
        if (m.getRows() != 3 || m.getColumns() != 3) {
            throw new IllegalArgumentException("Required matrix of size 3x3, found " + m.getRows() + "x" + m.getColumns());
        }
        return m.get(0, 0) * m.get(1, 1) * m.get(2, 2)
                + m.get(0, 1) * m.get(1, 2) * m.get(2, 0)
                + m.get(0, 2) * m.get(1, 0) * m.get(2, 1)
                - m.get(0, 0) * m.get(1, 2) * m.get(2, 1)
                - m.get(0, 1) * m.get(1, 0) * m.get(2, 2)
                - m.get(0, 2) * m.get(1, 1) * m.get(2, 0);
    }

    public Matrix inverse() {
        final double d = this.determinant();
        return this.cofactorMatrix().transpose().multiply(1 / d);
    }

    public Matrix cofactorMatrix() {
        final int size;
        if ((size = getRows()) != getColumns()) {
            return null;
        }
        final double[][] ret = new double[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                //2.0 + row + col = (row + 1.0) + (col + 1.0)
                //1x1 matrix
                final Matrix sub = size == 1 ? this : subMatrix(this, row, col);
                ret[row][col] = Math.pow(-1.0, 2.0 + row + col) * recursiveDeterminant(sub);
            }
        }
        return new Matrix(ret);
    }

    public double determinant() {
        final int size;
        if ((size = getRows()) != getColumns()) {
            return Double.NaN;
        }
        if (getRows() == 1) {
            return get(0, 0);
        } else if (size == 2) {
            return get(0, 0) * get(1, 1) - get(0, 1) * get(1, 0);
        } else if (size == 3) {
            return determinant_3x3(this);
        }
        return recursiveDeterminant(this);
    }

    private double recursiveDeterminant(Matrix curr) {
        if (curr.getRows() == 1) {
            return curr.get(0, 0);
        } else if (curr.getRows() == 2) {
            return get(0, 0) * get(1, 1) - get(0, 1) * get(1, 0);
        } else if (curr.getRows() == 3) {
            return determinant_3x3(curr);
        }
        double d = 0;
        for (int col = 0; col < curr.getColumns(); col++) {
            final double sign = Math.pow(-1.0, 2.0 + col);
            d += sign * curr.get(0, col) * recursiveDeterminant(subMatrix(curr, 0, col));
        }
        return d;
    }

    /**
     * Switch rows and columns
     */
    public Matrix transpose() {
        final double[][] ret = new double[columns][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                ret[j][i] = data[i][j];
            }
        }
        return new Matrix(ret);
    }

    public void put(final double obj, int row, int column) {
        if (row > getRows() || column > getColumns()) {
            return;
        }
        data[row][column] = obj;
    }

    public double get(final int row, final int column) {
        if (row > getRows() || column > getColumns()) {
            throw new IllegalArgumentException();
        }
        return data[row][column];
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public Matrix getRow(final int row) {
        if (row < 0 || row >= this.getRows()) {
            throw new IllegalArgumentException();
        }
        return new Matrix(data[row]);
    }

    public Matrix add(final Matrix m) {
        final int rows, cols;
        if ((rows = this.getRows()) != m.getRows() || (cols = this.getColumns()) != m.getColumns()) {
            return null;
        }
        final double[][] ret = new double[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                ret[row][col] = this.get(row, col) + m.get(row, col);
            }
        }
        return new Matrix(ret);
    }

    public Matrix add(final double val) {
        final int rows = this.getRows(), cols = this.getColumns();
        final double[][] ret = new double[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                ret[row][col] = this.get(row, col) + val;
            }
        }
        return new Matrix(ret);
    }

    public Matrix subtract(final Matrix m) {
        final int rows, cols;
        if ((rows = this.getRows()) != m.getRows() || (cols = this.getColumns()) != m.getColumns()) {
            return null;
        }
        final double[][] ret = new double[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                ret[row][col] = this.get(row, col) - m.get(row, col);
            }
        }
        return new Matrix(ret);
    }

    public Matrix subtract(final double val) {
        final int rows = this.getRows(), cols = this.getColumns();
        final double[][] ret = new double[rows][cols];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                ret[row][col] = this.get(row, col) - val;
            }
        }
        return new Matrix(ret);
    }

    public Matrix power(final int power) {
        final Matrix base = this;
        Matrix ret = base;
        for (int i = 0; i < power - 1; i++) {
            ret = ret.multiply(base);
        }
        return ret;
    }

    public Matrix multiply(final Matrix m) {
        if (this.getColumns() != m.getRows()) {
            return null;
        }
        final double[][] ret = new double[this.getRows()][m.getColumns()];
        for (int r1 = 0; r1 < this.getRows(); r1++) {
            for (int c = 0; c < m.getColumns(); c++) {
                double total = 0;
                for (int r2 = 0; r2 < m.getRows(); r2++) {
                    total += m.get(r2, c) * this.get(r1, r2);
                }
                ret[r1][c] = total;
            }
        }
        return new Matrix(ret);
    }

    public Matrix multiply(final double value) {
        final int rows, cols;
        final double[][] ret = new double[rows = this.getRows()][cols = this.getColumns()];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                ret[row][col] = this.get(row, col) * value;
            }
        }
        return new Matrix(ret);
    }

    public Matrix divide(final double value) {
        final int rows, cols;
        final double[][] ret = new double[rows = this.getRows()][cols = this.getColumns()];
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                ret[row][col] = this.get(row, col) / value;
            }
        }
        return new Matrix(ret);
    }

    public Matrix replaceRow(final int row, final double[] values) {
        if (this.getColumns() != values.length) {
            return null;
        }
        final Matrix ret = this.duplicate();
        for (int col = 0; col < values.length; col++) {
            ret.put(values[col], row, col);
        }
        return ret;
    }

    public Matrix replaceColumn(final int col, final double[] values) {
        if (this.getRows() != values.length) {
            return null;
        }
        final Matrix ret = this.duplicate();
        for (int row = 0; row < values.length; row++) {
            ret.put(values[row], row, col);
        }
        return ret;
    }

    public Matrix duplicate() {
        final double[][] ret = new double[this.getRows()][this.getColumns()];
        final double[][] curr = getData();
        for (int row = 0; row < this.getRows(); row++) {
            for (int cols = 0; cols < this.getColumns(); cols++) {
                ret[row][cols] = curr[row][cols];
            }
        }
        return new Matrix(ret);
    }

    public double[][] getData() {
        return data;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (final double[] arr : data) {
            builder.append(Arrays.toString(arr)).append(System.lineSeparator());
        }
        return builder.toString();
    }
}
