package sample.utils;

import java.util.ArrayList;
import java.util.List;

public class MatrixUtil {

    public static List<double[]> decompositMatrixToColumns(double[][] a){
        int columnCount = a[0].length;
        List<double[]> av = new ArrayList<>(a[0].length);
        for (int i = 0; i < columnCount; i++){
            double[] column = new double[a.length];

            for (int j = 0; j < a.length; j++)
                column[j] = a[j][i];

            av.add(column);
        }

        return av;
    }

}
