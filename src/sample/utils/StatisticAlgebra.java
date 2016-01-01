package sample.utils;

public class StatisticAlgebra {

    public static double[][] calculateDisperCovariance(double[][] data){
        int propertySize = data[0].length;
        double [] m = calculateMathAvarege(data);
        double[][] result = new double[propertySize][propertySize];

        for (int i=0; i < propertySize; i++)
            for (int j = i; j < propertySize; j++){
                double sum = 0;
                for (int k = 0; k < data.length; k++)
                    sum += (data[k][i] - m[i]) * (data[k][j] - m[j]);
                result[i][j] = sum / data.length;
                result[j][i] = result[i][j];
            }

        return result;
    }

    public static double[] calculateMathAvarege(double[][] data){
        double[] m = new double[data[0].length];
        for (int i =0; i < m.length; i++) m[i] = 0;

        for (int i=0; i < data.length; i++){
            for (int j = 0; j < m.length; j++)
                m[j] += data[i][j];
        }

        for (int i =0; i < m.length; i++) m[i] /= data.length;

        return m;
    }

}
