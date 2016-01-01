package sample.delegates;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import sample.algorithm.ICluster;
import sample.model.Matrix;

import java.util.Arrays;
import java.util.List;

public class TableHelper {

    public static void fillData(Matrix matrix, TableView tableView){
        tableView.getColumns().clear();
        ObservableList<double[]> data = FXCollections.observableArrayList();
        double[][] modelData = matrix.getData();
        data.addAll(Arrays.asList(modelData));
        for (int i = 0; i < matrix.getColumnCount(); i++) {
            TableColumn tc = new TableColumn("X"+(1+i));
            final int colNo = i;
            tc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
                @Override
                public ObservableValue call(TableColumn.CellDataFeatures param) {
                    return new SimpleStringProperty(String.format("%.4f", ((double[]) param.getValue())[colNo]) );
                }
            });
            tc.setPrefWidth(90);
            tableView.getColumns().add(tc);
        }
        tableView.setItems(data);
    }

    public static void fillData(List<ICluster> clusters, TableView tableView){
        tableView.getColumns().clear();
        ObservableList<double[]> data = FXCollections.observableArrayList();
        double[] tempPoint = clusters.get(0).getPointAt(0);

        for (int i = 0; i <= tempPoint.length; i++) {
            TableColumn tc = new TableColumn( i != tempPoint.length? "X"+(1+i) : "Cluster");
            final int colNo = i;
            tc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
                @Override
                public ObservableValue call(TableColumn.CellDataFeatures param) {
                    return new SimpleStringProperty(String.format("%.4f", ((double[]) param.getValue())[colNo]) );
                }
            });
            tc.setPrefWidth(90);
            tableView.getColumns().add(tc);
        }

        for (int c = 0; c < clusters.size(); c++){
            for (int p = 0; p < clusters.get(c).size(); p++) {
                double[] clusteredPoint = getClusteredPoint(clusters.get(c).getPointAt(p), c+1);
                data.add(clusteredPoint);
            }
        }

        tableView.setItems(data);
    }

    private static double[] getClusteredPoint(double[] point, int clusterNumber){
        double[] clusteredPoint = new double[point.length+1];
        for (int i = 0; i < point.length; i++) clusteredPoint[i] = point[i];
        clusteredPoint[point.length] = clusterNumber;
        return clusteredPoint;
    }
}
