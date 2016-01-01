package sample.model;

final public class Matrix {
    private final int rowCount;
    private final int columnCount;
    private final double[][] data;

    // create rowCount-by-columnCount matrix of 0's
    public Matrix(int rowCount, int columnCount) {
        this.rowCount = rowCount;
        this.columnCount = columnCount;
        data = new double[rowCount][columnCount];
    }

    public Matrix (double[] data, boolean isRow){
        this(isRow? 1 : data.length, isRow ? data.length : 1);

        for (int i = 0; i < data.length; i++)
            if (isRow)
                this.data[0][i] = data[i];
            else
                this.data[i][0] = data[i];
    }

    // create matrix based on 2d array
    public Matrix(double[][] data) {
        rowCount = data.length;
        columnCount = data[0].length;
        this.data = new double[rowCount][columnCount];
        for (int i = 0; i < rowCount; i++)
            for (int j = 0; j < columnCount; j++)
                this.data[i][j] = data[i][j];
    }

    public int getRowsCount() {
        return rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public double[] getRow(int rowPosition){
        return data[rowPosition];
    }

    public void reset (){
        for (int r = 0; r < rowCount; r++)
            for (int c = 0; c < columnCount; c++)
                data[r][c] = 0;
    }

    // copy constructor
    private Matrix(Matrix A) { this(A.data); }

    public void setValueAt(int row, int column, double value){
        data[row][column] = value;
    }

    public void increaseValueAt(int row, int column, double value){
        data[row][column] += value;
    }

    public double getValueAt(int row, int column){
        return data[row][column];
    }

    public double[][] getData() {
        return data;
    }

    // create and return a random rowCount-by-columnCount matrix with values between 0 and 1
    public static Matrix random(int M, int N) {
        Matrix A = new Matrix(M, N);
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                A.data[i][j] = Math.random();
        return A;
    }

    public static Matrix copy(Matrix matrix){
        Matrix res = new Matrix(matrix.getRowsCount(), matrix.getColumnCount());
        for (int r = 0; r < matrix.getRowsCount(); r++)
            for (int c = 0; c < matrix.getColumnCount(); c++)
                res.data[r][c] = matrix.data[r][c];
        return res;
    }

    // create and return the columnCount-by-columnCount identity matrix
    public static Matrix identity(int N) {
        Matrix I = new Matrix(N, N);
        for (int i = 0; i < N; i++)
            I.data[i][i] = 1;
        return I;
    }

    // swap rows i and j
    private void swap(int i, int j) {
        double[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    // create and return the transpose of the invoking matrix
    public Matrix transpose() {
        Matrix A = new Matrix(columnCount, rowCount);
        for (int i = 0; i < rowCount; i++)
            for (int j = 0; j < columnCount; j++)
                A.data[j][i] = this.data[i][j];
        return A;
    }

    // return C = A + B
    public Matrix plus(Matrix B) {
        Matrix A = this;
        if (B.rowCount != A.rowCount || B.columnCount != A.columnCount) throw new RuntimeException("Illegal matrix dimensions.");
        Matrix C = new Matrix(rowCount, columnCount);
        for (int i = 0; i < rowCount; i++)
            for (int j = 0; j < columnCount; j++)
                C.data[i][j] = A.data[i][j] + B.data[i][j];
        return C;
    }


    // return C = A - B
    public Matrix minus(Matrix B) {
        Matrix A = this;
        if (B.rowCount != A.rowCount || B.columnCount != A.columnCount) throw new RuntimeException("Illegal matrix dimensions.");
        Matrix C = new Matrix(rowCount, columnCount);
        for (int i = 0; i < rowCount; i++)
            for (int j = 0; j < columnCount; j++)
                C.data[i][j] = A.data[i][j] - B.data[i][j];
        return C;
    }

    // does A = B exactly?
    public boolean eq(Matrix B) {
        Matrix A = this;
        if (B.rowCount != A.rowCount || B.columnCount != A.columnCount) throw new RuntimeException("Illegal matrix dimensions.");
        for (int i = 0; i < rowCount; i++)
            for (int j = 0; j < columnCount; j++)
                if (A.data[i][j] != B.data[i][j]) return false;
        return true;
    }

    // return C = A * B
    public Matrix times(Matrix B) {
        Matrix A = this;
        if (A.columnCount != B.rowCount) throw new RuntimeException("Illegal matrix dimensions.");
        Matrix C = new Matrix(A.rowCount, B.columnCount);
        for (int i = 0; i < C.rowCount; i++)
            for (int j = 0; j < C.columnCount; j++)
                for (int k = 0; k < A.columnCount; k++)
                    C.data[i][j] += (A.data[i][k] * B.data[k][j]);
        return C;
    }

    public double calculateDeterminant(){
        if (rowCount != columnCount)
            throw new RuntimeException("The matrix isn't square");
        return calculateDeterminant(data);
    }

    private double calculateDeterminant(double[][] data){
        int rowCount = data.length;

        double result=0;
        if (rowCount == 1) return data[0][0];
        if (rowCount == 2) return data[0][0] * data[1][1] - data[0][1] * data[1][0];
        if (rowCount == 3) return data[0][0] * data[1][1] * data[2][2] + data[0][1] * data[1][2] * data[2][0] + data[0][2] * data[1][0] * data[2][1]
                - data[2][0] * data[1][1] * data[0][2] - data[1][0] * data[0][1] * data[2][2] - data[0][0] * data[2][1] * data[1][2];
        double[][] tempData = new double[rowCount -1][rowCount -1];
        for (int i = 0; i < rowCount; i++) {
            for (int j = 1; j < rowCount; j++) {
                for (int k = 0; k < rowCount; k++) {
                    if (k < i) {
                        tempData[j - 1][k] = data[j][k];
                    } else if (k > i) {
                        tempData[j - 1][k - 1] = data[j][k];
                    }
                }
            }
            result += Math.pow(-1, i) * data[0][i] * calculateDeterminant(tempData);
        }
        return result;
    }

    public Matrix inverse() {
        Matrix matrix = cofactor().transpose();
        matrix.multiplyByConstant(1.0 / calculateDeterminant());
        return matrix;
    }

    public void multiplyByConstant(double value){
        for (int r = 0; r < rowCount; r++)
            for (int c = 0; c < columnCount; c++)
                data[r][c] *= value;
    }

    public void divideByConstant(double value){
        multiplyByConstant(1.0 / value);
    }

    private static int sign(int i) {
        if (i % 2 == 0)
            return 1;
        return -1;
    }

    public Matrix createSubMatrix(int excluding_row, int excluding_col) {
        Matrix mat = new Matrix(rowCount - 1, columnCount - 1);
        int r = -1;
        for (int i = 0; i < rowCount; i++) {
            if (i == excluding_row) continue;
            r++;
            int c = -1;
            for (int j = 0; j < columnCount; j++) {
                if (j == excluding_col) continue;
                mat.setValueAt(r, ++c, getValueAt(i, j));
            }
        }
        return mat;
    }

    public Matrix cofactor() {
        Matrix mat = new Matrix(rowCount, columnCount);
        for (int i = 0; i < getRowsCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                mat.setValueAt(i, j, sign(i) * sign(j) * createSubMatrix(i, j).calculateDeterminant());
            }
        }
        return mat;
    }

    // print matrix to standard output
    public void show() {
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++)
                System.out.printf("%9.4f ", data[i][j]);
            System.out.println();
        }
    }

    public static Matrix unitMatrixes(Matrix matrix1, Matrix matrix2){
        double[][] objects1 = matrix1.getData();
        double[][] objects2 = matrix2.getData();

        double[][] objects = new double[objects1.length + objects2.length][];
        for (int i = 0; i < objects.length; i++){
            if (i < objects1.length)
                objects[i] = objects1[i];
            else
                objects[i] = objects2[i-objects1.length];
        }
        return new Matrix(objects);
    }

}
