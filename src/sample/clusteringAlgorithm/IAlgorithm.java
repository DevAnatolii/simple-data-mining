package sample.clusteringAlgorithm;

import java.util.List;

public interface IAlgorithm {
    List<ICluster> cluster(double[][] data);
}
