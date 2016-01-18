package sample.clusteringAlgorithm;

import sample.clusteringAlgorithm.em.EmAlgorithm;
import sample.clusteringAlgorithm.kmeans.KmeansAlgorithm;
import sample.clusteringAlgorithm.sem.SemAlgorithm;

public class AlgorithmFabric {
    public static final String K_MEANS_ALGORITHM = "k-means clusteringAlgorithm";
    public static final String EM_ALGORITHM = "EM clusteringAlgorithm";
    public static final String SEM_ALGORITHM = "SEM clusteringAlgorithm";

    public static IAlgorithm create(String type, int clusterCount){
        if (type.equals(K_MEANS_ALGORITHM)) return new KmeansAlgorithm(clusterCount);
        if (type.equals(SEM_ALGORITHM)) return new SemAlgorithm(clusterCount);
        if (type.equals(EM_ALGORITHM)) return new EmAlgorithm(clusterCount);
        return null;
    }
}
