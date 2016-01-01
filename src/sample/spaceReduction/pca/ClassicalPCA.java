package sample.spaceReduction.pca;

import Jama.Matrix;
import javafx.util.Pair;
import sample.utils.Helper;
import sample.utils.MatrixUtil;
import sample.utils.StatisticAlgebra;

import java.util.ArrayList;
import java.util.List;

public class ClassicalPCA {

    List<double[]> eigenVectors;

    public Pair<List<Double>, double[][]> reduceSpace(double[][] data, double accuracy, int maxIteration){
        int propertySize = data[0].length;

        double[][] cov = StatisticAlgebra.calculateDisperCovariance(data);
        Pair<double[][], double[][]> eigen = QRIteraction.eigenVectorValuesExtractionQRIterative(cov, accuracy, maxIteration);
        eigenVectors = MatrixUtil.decompositMatrixToColumns(eigen.getKey());
        double[][] aIttr = eigen.getValue();

        List<Double> PCAInfo = new ArrayList<>(propertySize);
        double sum = 0;
        for (int i = 0; i < propertySize; i++){
            sum += Math.abs(aIttr[i][i]);
            PCAInfo.add(sum);
        }
        for (int i = 0; i < propertySize; i++) PCAInfo.set(i, PCAInfo.get(i)/sum);

        double[][] result = new double[data.length][propertySize];
        for (int i = 0; i < data.length; i++)
            result[i] = transform(data[i]);

        return new Pair<>(PCAInfo, result);
    }


    private double[] transform(double[] dataItem)
    {
        double[] res = new double[eigenVectors.size()];
        for (int i = 0; i < eigenVectors.size(); i++)
        {
            res[i] = 0;
            for (int j = 0; j < dataItem.length; j++) {
                res[i] += eigenVectors.get(i)[j]*dataItem[j];
            }
        }
        return res;
    }

    public static void main(String[] args){
        double[] _v = new double[] { 1, 0, 3 };

        double[][] _data = {
                    new double[] {1, 2, 23},
                    new double[] {-3, 17, 5},
                    new double[] {13, -6, 7},
                    new double[] {7, 8, -9}
        };

        Matrix matrix = new Matrix(_data);
        ClassicalPCA pca = new ClassicalPCA();
      //  pca.reduceSpace(_data, 2, 0.0001, 1000);
        double[] reduced = pca.transform(_v);
        double[] expReduced = new double[] {-2.75008, 0.19959};
        System.out.println("test status : " + Helper.AreEqualVectors(expReduced, reduced, 0.001));
    }

}
