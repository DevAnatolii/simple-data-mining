package sample.clusteringAlgorithm.kmeans;

import sample.distanceMetric.Metric;
import sample.model.Matrix;

import java.util.List;

public class QualityFunctional {

    Metric metric;

    public QualityFunctional(Metric metric) {
        this.metric = metric;
    }

    public double obtainValue(Matrix data, List<Cluster> clusters){
        double q0 = Q0(data, clusters);
        double q1 = Q1(clusters);
        return q0/q1;
    }

    private double Q0(Matrix data, List<Cluster> clusters){
        double res = 0;
        double tempSum = 0;
        for (int classPos = 0; classPos < clusters.size(); classPos++){
            tempSum = 0;
            Cluster cluster = clusters.get(classPos);
            for (int objectPos = 0; objectPos < data.getRowsCount(); objectPos++){
                tempSum += metric.distance(data.getRow(objectPos), cluster.getClusterCenter());
            }
            res += tempSum/cluster.size();
        }
        return res;
    }

    private double Q1(List<Cluster> clusters){
        double res = 0;
        for (int classPos = 0; classPos < clusters.size(); classPos++)
            for (int classPos2 = classPos+1; classPos2 < clusters.size(); classPos2++)
                res += metric.distance(clusters.get(classPos).getClusterCenter(), clusters.get(classPos2).getClusterCenter());
        return res;
    }
}
