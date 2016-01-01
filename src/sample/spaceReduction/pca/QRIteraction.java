package sample.spaceReduction.pca;

import Jama.Matrix;
import javafx.util.Pair;

public class QRIteraction {

    public static Pair<double[][], double[][]> eigenVectorValuesExtractionQRIterative(double[][]a, double accuracy, int maxIterations){
        double[][] aItr = a;
        double[][] q = null;

        for (int i = 0; i < maxIterations; i++) {
            System.out.println("QR Iteration :" + (i+1) );
            Matrix matrix = new Matrix(aItr);
            Jama.QRDecomposition decomposition = matrix.qr();
            double[][] Q = decomposition.getQ().getArray();
            double[][] R = decomposition.getR().getArray();
            aItr = multipleMatrix(R, Q);
            if (q == null) q = Q;
            else  {
                double[][] qNew = multipleMatrix(q, Q);
                boolean accuracyAcheived = true;
                for (int n = 0; n < q.length; n++) {
                    for (int m = 0; m < q[n].length; m++) {
                        double epsilon = Math.abs(Math.abs(qNew[n][m]) - Math.abs(q[n][m]));
                        if (epsilon > accuracy) {
                            System.out.println("Epsilon = " + epsilon + "cell="+n+"|"+m);
                            accuracyAcheived = false;
                            break;
                        }
                    }
                    if (!accuracyAcheived) break;
                }
                q = qNew;
                if (accuracyAcheived) break;
            }
        }

        return new Pair<>(q, aItr);
    }

    private static double[][] multipleMatrix(double[][] a1, double[][] a2){
        return new sample.model.Matrix(a1).times(new sample.model.Matrix(a2)).getData();
    }

    public static void main(String[] args){
        double[][] a = new double[][]
                {
                        new double[] {1, 2, 4},
                        new double[] {2, 9, 8},
                        new double[] {4, 8, 2}
                };


        Pair<double[][], double[][]> ev = eigenVectorValuesExtractionQRIterative(a, 0.0001, 5000);


        double expEV00 = 15.2964;
        double expEV11 = 4.3487;
        double expEV22 = 1.0523;

        System.out.println("delta = "+ Math.abs(expEV00 - ev.getValue()[0][0]));
        System.out.println("delta = "+ Math.abs(expEV11 - Math.abs(ev.getValue()[1][1])));
        System.out.println("delta = "+ Math.abs(expEV22 - ev.getValue()[2][2]));
    }

}
