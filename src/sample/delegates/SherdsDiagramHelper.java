package sample.delegates;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import sample.distanceMetric.Metric;
import sample.model.Matrix;

import java.util.ArrayList;
import java.util.Random;

public class SherdsDiagramHelper {

    public static void showDiagram(ScatterChart chart, Matrix initialData, Matrix transformedData, Metric metric){
        ObservableList<XYChart.Series> series = FXCollections.observableArrayList();
        XYChart.Series serie = new XYChart.Series();
        serie.setName("");

        int objectCount = initialData.getRowsCount();
        if (objectCount*objectCount / 2 > 1000){
            Random random = new Random(System.currentTimeMillis());
            ArrayList<Integer> x = new ArrayList<>(1000);
            ArrayList<Integer> x1 = new ArrayList<>(1000);
            ArrayList<Integer> x2 = new ArrayList<>(1000);
            while (x.size() < 1000){
                int v1 = random.nextInt(objectCount);
                int v2 = random.nextInt(objectCount);
                int temp1 = v1 * (objectCount + objectCount - v1)/2 + v2;
                int temp2 = v2 * (objectCount + objectCount - v2)/2 + v1;
                if (v1 == v2 || x.contains(temp1) || x.contains(temp2)) continue;
                x.add(temp1);
                x1.add(v1);
                x2.add(v2);
            }

            for (int j = 0; j < x.size(); j++) {
                double R = metric.distance(initialData.getRow(x1.get(j)), initialData.getRow(x2.get(j)));
                double d = metric.distance(transformedData.getRow(x1.get(j)), transformedData.getRow(x2.get(j)));
                serie.getData().add(new XYChart.Data(R, d));
            }

        } else {

            for (int j = 0; j < objectCount; j++) {
                for (int i = j + 1; i < objectCount; i++) {
                    if (i == j) continue;
                    double R = metric.distance(initialData.getRow(i), initialData.getRow(j));
                    double d = metric.distance(transformedData.getRow(i), transformedData.getRow(j));
                    serie.getData().add(new XYChart.Data(R, d));
                }
            }
        }

        series.add(serie);
        chart.setData(series);
    }
}
