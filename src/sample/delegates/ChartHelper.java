package sample.delegates;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import sample.algorithm.ICluster;
import sample.model.Matrix;
import sample.visualization.ParallelCoordChart.ParallelCoordsChart;
import sample.visualization.ParallelCoordChart.Serie;

import java.util.ArrayList;
import java.util.List;

public class ChartHelper {

    public static void showMatrix(Matrix matrix, ScatterChart chart, int axisX, int axisY){

        ObservableList<XYChart.Series> series = FXCollections.observableArrayList();
        XYChart.Series serie = new XYChart.Series();
        serie.setName("Data");

        int objectCount = matrix.getRowsCount();
        for (int i = 0; i < objectCount; i++) {
            serie.getData().add(new XYChart.Data(matrix.getValueAt(i, axisX), matrix.getValueAt(i, axisY)));
        }

        series.add(serie);
        chart.setData(series);
    }

    public static void showMatrix(Matrix matrix, ParallelCoordsChart chart){
        chart.getSeries().clear();
        Serie serie = new Serie("Data");
        int objectCount = matrix.getRowsCount();
        for (int i = 0; i < objectCount; i++) {
            serie.addPoint(matrix.getData()[i]);
        }
        chart.addSeria(serie);
    }


    public static void showClusters(List<ICluster> clusters, ParallelCoordsChart chart){
        chart.getSeries().clear();
        List<Serie> series = new ArrayList<>(clusters.size() +1);
        Serie serieCenters = new Serie("Cluster's centers");

        for (int c = 0; c < clusters.size(); c++){
            serieCenters.addPoint(clusters.get(c).getClusterCenter());
            Serie serie = new Serie("Cluster #" + (c+1));

            for (int p = 0; p < clusters.get(c).size(); p++)
                serie.addPoint(clusters.get(c).getPointAt(p));

            series.add(serie);
        }

        series.add(serieCenters);
        chart.addAllSerias(series);
    }

    public static void showClusters(List<ICluster> clusters, ScatterChart chart, int axisX, int axisY){
        ObservableList<XYChart.Series> series = FXCollections.observableArrayList();
        XYChart.Series serieCenters = new XYChart.Series();
        serieCenters.setName("Cluster's centers");

        for (int c = 0; c < clusters.size(); c++){
            XYChart.Series serie = new XYChart.Series();
            serie.setName("Cluster #" + (c+1));
            double[] center = clusters.get(c).getClusterCenter();

            for (int p = 0; p < clusters.get(c).size(); p++){
                double[] point = clusters.get(c).getPointAt(p);
                serie.getData().add(new XYChart.Data(point[axisX], point[axisY]));
            }

            serieCenters.getData().add(new XYChart.Data(center[axisX], center[axisY]));
            series.add(serie);
        }

        series.add(serieCenters);
        chart.setData(series);
    }

    public static void showPCAInfoDiagram(List<Double> info, LineChart chart){
        ObservableList<XYChart.Series> series = FXCollections.observableArrayList();
        XYChart.Series serie = new XYChart.Series();
        serie.setName("ClassicalPCA info");

        int objectCount = info.size();
        serie.getData().add(new XYChart.Data<>(0, 0));
        for (int i = 0; i < objectCount; i++) {
            serie.getData().add(new XYChart.Data((i+1), info.get(i)));
        }

        series.add(serie);
        chart.setData(series);
    }
}
