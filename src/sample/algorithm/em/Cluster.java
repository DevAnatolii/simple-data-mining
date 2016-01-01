package sample.algorithm.em;

import sample.algorithm.ICluster;
import sample.model.Matrix;

import java.util.ArrayList;
import java.util.List;

public class Cluster implements ICluster{
    double weight;
    double[] center;
    List<double[]> points;
    Matrix covarianceMatrix;

    public Cluster(double[] center) {
        this.center = center;
        points = new ArrayList<>();
    }

    @Override
    public int size() {
        return points.size();
    }

    public void addPoint(double[] point){
        points.add(point);
    }

    @Override
    public double[] getPointAt(int position) {
        return points.get(position);
    }

    @Override
    public double[] getClusterCenter() {
        return center;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setCovarianceMatrix(Matrix covarianceMatrix) {
        this.covarianceMatrix = covarianceMatrix;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public Matrix getCovariationMatrix() {
        return covarianceMatrix;
    }
}
