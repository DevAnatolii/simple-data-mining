package sample.clusteringAlgorithm;

import sample.model.Matrix;

public interface ICluster {
    int size();
    double[] getPointAt(int position);
    double [] getClusterCenter();

    double getWeight();
    Matrix getCovariationMatrix();
}
