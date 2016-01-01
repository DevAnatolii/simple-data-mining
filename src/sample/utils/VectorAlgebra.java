package sample.utils;

public class VectorAlgebra {

    public static double[] vectorProjection(double[] a, double[] b)
    {
        double k = scalarVectorProduct(a, b)/scalarVectorProduct(b, b);
        return scalarToVectorProduct(k, b);
    }

    public static double scalarVectorProduct(double[] a, double[] b){
        double res = 0;
        for (int i = 0; i < a.length; i++){
            res += a[i]*b[i];
        }
        return res;
    }

    public static double[] scalarToVectorProduct(double val, double[] a){
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++)
            res[i] = val * a[i];
        return res;
    }

    public static double normOfVector(double[] a){
        double sum = 0;
        for (int i=0; i < a.length; i++)
            sum += a[i]*a[i];
        return Math.sqrt(sum);
    }
}
