package sample.algorithm.kmeans;

import sample.algorithm.IAlgorithm;
import sample.algorithm.ICluster;
import sample.distanceMetric.EuclideMetric;
import sample.model.Matrix;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KmeansAlgorithm implements IAlgorithm{

    int clusterCount;
    List<Cluster> clusters;
    double[][] objects;

    int repeatCount = 10;
    List<Cluster> finestClusters;

    public KmeansAlgorithm(int clusterCount) {
        this.clusterCount = clusterCount;
    }

    @Override
    public List<ICluster> cluster(double[][] data){
        objects = data;

        double finestQualityFunctional = Double.MAX_VALUE;
        QualityFunctional qualityFunctional = new QualityFunctional(new EuclideMetric());

        for (int i = 0; i < repeatCount; i++){
            applyAlgorithm();

            double quality = qualityFunctional.obtainValue(new Matrix(data), clusters);
            if (finestQualityFunctional > quality){
                finestClusters = clusters;
                finestQualityFunctional = quality;
            }
        }

        for (Cluster cluster: finestClusters) cluster.setWeight(((double)cluster.size()/data.length));
        return new ArrayList<ICluster>(finestClusters);
    }

    private void applyAlgorithm(){
        initClusters();
        movePointsToClaster();
        recalculateCenters();
        while (isClustersMoved()) {
            recalculateCenters();
            movePointsToClaster();
        }
    }

    private void initClusters(){
        Random random = new Random(System.currentTimeMillis());
        int size = objects.length;
        ArrayList<Integer> randomPoints = new ArrayList(clusterCount);
        clusters = new ArrayList<>(clusterCount);

        while (randomPoints.size() < clusterCount){
            int randomValue = random.nextInt(size);
            if (randomPoints.contains(randomValue)) continue;
            randomPoints.add(randomValue);
        }

        for (Integer randomPoint : randomPoints)
            clusters.add(new Cluster(objects[randomPoint]));
    }

    private void movePointsToClaster() {
        for (Cluster cluster : clusters) cluster.clear();

        int nearestCluster;
        double minDistance;
        for (int pointPos = 0; pointPos < objects.length; pointPos++) {
            nearestCluster = 0;
            minDistance = Double.MAX_VALUE;

            for (int clusterPos = 0; clusterPos < clusters.size(); clusterPos++) {
                double distance = clusters.get(clusterPos).distanceToPoint(objects[pointPos]);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestCluster = clusterPos;
                }
            }

            clusters.get(nearestCluster).addPoint(objects[pointPos]);
        }
    }

    private boolean isClustersMoved(){
        for (Cluster cluster: clusters)
            if (cluster.isCenterMoved()) return true;

        return false;
    }

    private void recalculateCenters(){
        for (Cluster cluster: clusters) cluster.recalculateCanter();
    }
}
