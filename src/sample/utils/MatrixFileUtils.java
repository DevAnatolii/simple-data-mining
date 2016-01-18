package sample.utils;

import sample.model.Matrix;

import java.io.*;
import java.util.stream.Stream;

public class MatrixFileUtils {

    public static void writeToFile(Matrix matrix, File file) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(file);

        int rowCount = matrix.getRowsCount();
        int columnCount = matrix.getColumnCount();
        for (int r = 0; r < rowCount; r++){
            StringBuilder stringBuilder = new StringBuilder();
            for (int c = 0; c < columnCount; c++){
                stringBuilder.append(matrix.getValueAt(r, c));
                if (c != columnCount -1 ) stringBuilder.append(" ");
            }

            if (r != rowCount -1) stringBuilder.append(System.getProperty( "line.separator" ));
            fileWriter.write(stringBuilder.toString());
        }

        fileWriter.close();
    }

    public static Matrix loadFromFile(File file) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(file));
        int dimensionSize = 0;
        Stream<String> lines = reader.lines();
        int objectCount = (int) lines.count();
        reader.close();

        reader = new BufferedReader(new FileReader(file));
        String[] temp = reader.readLine().split(" ");
        double[][] objects = new double[objectCount][temp.length];

        for (int i = 0; i< objectCount; i++){
            if (i != 0) temp = reader.readLine().split(" ");

            for (int j = 0; j < temp.length; j++) {
                objects[i][j]=Double.parseDouble(temp[j]);
            }
        }

        reader.close();
        return new Matrix(objects);
    }

}
