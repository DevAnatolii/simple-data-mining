package sample.algorithm.kmeans;

import sample.algorithm.ICluster;
import sample.model.Matrix;

import java.util.ArrayList;
import java.util.List;

public class Cluster implements ICluster {
    private final double E = 0.00000000001;
    List<double[]> points;

    double weight;
    double[] coordCenter;
    double[] coordPrevCenter;

    public Cluster(double[] coordCenter) {
        this.coordCenter = coordCenter;
        points = new ArrayList<>();
    }

    @Override
    public double[] getClusterCenter() {
        return coordCenter;
    }

    @Override
    public int size() {
        return points.size();
    }

    public void clear() {
        points.clear();
    }

    public void addPoint(double[] point) {
        points.add(point);
    }

    @Override
    public double[] getPointAt(int i) {
        return points.get(i);
    }

    public void recalculateCanter() {
        coordPrevCenter = coordCenter;
        int dimensionSize = coordCenter.length;
        for (int i = 0; i < dimensionSize; i++) {
            double sum = 0;
            for (int p = 0; p < points.size(); p++) sum += points.get(p)[i];
            coordCenter[i] = sum / points.size();
        }
    }

    public boolean isCenterMoved() {
        int dimensionSize = coordCenter.length;
        for (int i = 0; i < dimensionSize; i++) {
            if (Math.abs(coordCenter[i] - coordPrevCenter[i]) > E) return true;
        }
        return false;
    }

    public double distanceToPoint(double[] point) {
        double sum = 0;
        for (int i = 0; i < coordCenter.length; i++) {
            sum += Math.pow(point[i] - coordCenter[i], 2);
        }
        return Math.sqrt(sum);
    }

    public void setWeight(double weight){
        this.weight = weight;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public Matrix getCovariationMatrix() {
        return null;
    }
}
