package sample.utils;


public class Helper {
    public static boolean AreEqualMatrices(double[][] expQ, double[][] Q, double e) {
        for (int i =0; i < expQ.length; i++)
            for (int j =0; j <expQ[0].length; j++)
                if (Math.abs(expQ[i][j] - Q[i][j]) > e) return false;

        return true;
    }

    public static boolean AreEqualVectors(double[] expReduced, double[] reduced, double e) {
        for (int j =0; j <expReduced.length; j++)
            if (Math.abs(expReduced[j] - reduced[j]) > e) return false;

        return true;
    }
}
