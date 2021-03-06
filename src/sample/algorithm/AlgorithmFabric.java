package sample.algorithm;

import sample.algorithm.em.EmAlgorithm;
import sample.algorithm.kmeans.KmeansAlgorithm;
import sample.algorithm.sem.SemAlgorithm;

public class AlgorithmFabric {
    public static final String K_MEANS_ALGORITHM = "k-means algorithm";
    public static final String EM_ALGORITHM = "EM algorithm";
    public static final String SEM_ALGORITHM = "SEM algorithm";

    public static IAlgorithm create(String type, int clusterCount){
        if (type.equals(K_MEANS_ALGORITHM)) return new KmeansAlgorithm(clusterCount);
        if (type.equals(SEM_ALGORITHM)) return new SemAlgorithm(clusterCount);
        if (type.equals(EM_ALGORITHM)) return new EmAlgorithm(clusterCount);
        return null;
    }
}
