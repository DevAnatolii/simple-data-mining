package sample.distanceMetric;

public class EuclideMetric implements Metric{

    @Override
    public double distance(double[] object1, double[] object2) {
        if (object1 == null || object2 == null || object1.length != object2.length)
            throw new RuntimeException("Incorrect Parameters");

        double sum = 0.0;
        for (int i = 0; i < object1.length; i++)
            sum += Math.pow(object1[i] - object2[i], 2);
        return Math.sqrt(sum);
    }
}
