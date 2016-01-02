package sample;

import com.sun.deploy.uitoolkit.impl.fx.ui.FXMessageDialog;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import sample.algorithm.AlgorithmFabric;
import sample.algorithm.IAlgorithm;
import sample.algorithm.ICluster;
import sample.delegates.ChartHelper;
import sample.delegates.SherdsDiagramHelper;
import sample.delegates.TableHelper;
import sample.dialogs.MultiDimensionalScalingDialog;
import sample.distanceMetric.EuclideMetric;
import sample.model.Matrix;
import sample.spaceReduction.mds.MultidimensionalScaling;
import sample.spaceReduction.pca.ClassicalPCA;
import sample.utils.MatrixFileUtils;
import sample.visualization.ParallelCoordChart.ParallelCoordsChart;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import static sample.algorithm.AlgorithmFabric.*;

public class Controller {
    private static final String VISUALIZATION_METHOD_PARALLEL_COORD = "Parallel Coordinates Chart";
    private static final String VISUALIZATION_METHOD_2D_CHART = "Simple 2D Chart";

    Stage stage;

    // Load Data tab
    public TableView tableInputData;
    public ScatterChart chartInputData;
    public Button btnLoadData;
    public ChoiceBox axisXInputData;
    public ChoiceBox axisYInputData;
    public Pane parallelCoordInputDataContainer;
    public ChoiceBox visualizationMethodInputData;
    private ParallelCoordsChart parallelCoordsInputDataChart;

    // SEM, EM, k-means tab
    public TableView tableClusteredData;
    public ScatterChart chartClusteredData;
    public ChoiceBox axisXClusteredData;
    public ChoiceBox axisYClusteredData;
    public ChoiceBox clusteringAlgorithm;
    public VBox clustersParams;
    public Button btnClusterData;
    public TextField clusterCount;
    public ChoiceBox visualizationMethodClusteredData;
    public Pane parallelCoordClusteredDataContainer;
    private ParallelCoordsChart parallelCoordsClusteredDataChart;

    //Multidimensional scaling
    public TableView initialData;
    public TableView transformedData;
    public ScatterChart shepardaDiagram;

    //ClassicalPCA method
    public TableView initialDataPCA;
    public TableView transformedDataPCA;
    public LineChart PCADiagromInfo;
    public TextField PCANewSpaceSize;

    Matrix inputData;
    Matrix currentData;
    Matrix pcaData;
    List<ICluster> clusteredData;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void onLoadData(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            Platform.runLater(() -> {
                try {
                    inputData = MatrixFileUtils.loadFromFile(file);
                    currentData = inputData;
                    showDataOnTable();
                    fillInputDataVisualizationMethods();
                    visualizationMethodInputData.getSelectionModel().select(0);
                    fillChartSelectionAxis();
                    showInputDataParallelCoordChart();
                    showInputData2DChart();
                    loadMathAparatus();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void showDataOnTable(){
        TableHelper.fillData(inputData, tableInputData);
        TableHelper.fillData(inputData, initialData);
        TableHelper.fillData(inputData, initialDataPCA);
    }

    private void fillInputDataVisualizationMethods(){
        ObservableList<String> data = FXCollections.observableArrayList(VISUALIZATION_METHOD_2D_CHART, VISUALIZATION_METHOD_PARALLEL_COORD);
        visualizationMethodInputData.setItems(data);
        visualizationMethodInputData.getSelectionModel().selectedItemProperty().addListener(this::changeInputDataVisualizationMethod);
    }

    private void changeInputDataVisualizationMethod(ObservableValue<Object> observable, Object oldValue, Object newValue){
        if (oldValue == newValue) return;
        String method = (String) newValue;
        parallelCoordInputDataContainer.setVisible(method.equals(VISUALIZATION_METHOD_PARALLEL_COORD));
        chartInputData.setVisible(method.equals(VISUALIZATION_METHOD_2D_CHART));
    }

    private void fillChartSelectionAxis(){
        ObservableList<String> data = FXCollections.observableArrayList();
        for (int i = 0; i < inputData.getColumnCount();)
            data.add("X"+ ++i);
        axisXInputData.setItems(data);
        axisYInputData.setItems(data);
        axisXInputData.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showInputData2DChart());
        axisYInputData.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showInputData2DChart());
    }

    private void showInputData2DChart(){
        if (axisXInputData.getValue() == null || axisYInputData.getValue() == null ) return;
        int x = Integer.parseInt(((String) axisXInputData.getValue()).substring(1)) - 1;
        int y = Integer.parseInt(((String) axisYInputData.getValue()).substring(1)) - 1;
        ChartHelper.showMatrix(inputData, chartInputData, x, y);
    }

    private void showInputDataParallelCoordChart(){
        if (parallelCoordsInputDataChart == null){
            parallelCoordsInputDataChart = new ParallelCoordsChart("", 500, 450);
            parallelCoordInputDataContainer.getChildren().addAll(parallelCoordsInputDataChart);
        }
        Platform.runLater(() -> ChartHelper.showMatrix(inputData, parallelCoordsInputDataChart));
    }

    private void loadMathAparatus(){
        ObservableList<String> data = FXCollections.observableArrayList();
        data.addAll(K_MEANS_ALGORITHM, EM_ALGORITHM, SEM_ALGORITHM);
        clusteringAlgorithm.setItems(data);
    }

    //--------------------------------------------------------------------------------//
    public void onClusterData(ActionEvent event){
        String algorithType = (String) clusteringAlgorithm.getValue();
        int count = Integer.parseInt(clusterCount.getText());
        Platform.runLater(() -> {
            IAlgorithm algorithm = AlgorithmFabric.create(algorithType, count);
            if (algorithm == null) return;

            clusteredData = algorithm.cluster(currentData.getData());
            TableHelper.fillData(clusteredData, tableClusteredData);
            fillClusteredDataChartSelectionAxis();
            fillInputClusteredVisualizationMethods();
            visualizationMethodClusteredData.getSelectionModel().select(1);
            showClusteredDataParallelCoordChart();
            showClustersParameters();
        });
    }

    private void fillClusteredDataChartSelectionAxis(){
        ObservableList<String> data = FXCollections.observableArrayList();
        for (int i = 0; i < clusteredData.get(0).getPointAt(0).length;)
            data.add("X"+ ++i);
        axisXClusteredData.setItems(data);
        axisYClusteredData.setItems(data);
        axisXClusteredData.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showClusteredData2DChart());
        axisYClusteredData.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> showClusteredData2DChart());
    }

    private void fillInputClusteredVisualizationMethods(){
        ObservableList<String> data = FXCollections.observableArrayList(VISUALIZATION_METHOD_2D_CHART, VISUALIZATION_METHOD_PARALLEL_COORD);
        visualizationMethodClusteredData.setItems(data);
        visualizationMethodClusteredData.getSelectionModel().selectedItemProperty().addListener(this::changeClusteredDataVisualizationMethod);
    }

    private void showClusteredData2DChart(){
        if (axisXClusteredData.getValue() == null || axisYClusteredData.getValue() == null ) return;
        int x = Integer.parseInt(((String) axisXClusteredData.getValue()).substring(1)) - 1;
        int y = Integer.parseInt(((String) axisYClusteredData.getValue()).substring(1)) - 1;
        ChartHelper.showClusters(clusteredData, chartClusteredData, x, y);
    }

    private void changeClusteredDataVisualizationMethod(ObservableValue<Object> observable, Object oldValue, Object newValue){
        if (oldValue == newValue || oldValue == null || newValue == null) return;
        String method = (String) newValue;
        parallelCoordClusteredDataContainer.setVisible(method.equals(VISUALIZATION_METHOD_PARALLEL_COORD));
        chartClusteredData.setVisible(method.equals(VISUALIZATION_METHOD_2D_CHART));
    }

    private void showClusteredDataParallelCoordChart(){
        if (parallelCoordsClusteredDataChart == null){
            parallelCoordsClusteredDataChart = new ParallelCoordsChart("", 815, 450);
            parallelCoordClusteredDataContainer.getChildren().addAll(parallelCoordsClusteredDataChart);
        }
        Platform.runLater(() -> ChartHelper.showClusters(clusteredData, parallelCoordsClusteredDataChart));
    }

    private void showClustersParameters(){
        clustersParams.getChildren().clear();
        for (int i = 0; i < clusteredData.size(); i++){
            ICluster cluster = clusteredData.get(i);
            double[][] center = new double[][]{cluster.getClusterCenter()};

            Label clusterLabel = new Label(String.format("Custer #%d with weight %.4f", i+1, cluster.getWeight()));
            Label centerLabel = new Label("Center");
            TableView centerTable = new TableView();
            centerTable.setPrefHeight(50);
            TableHelper.fillData(new Matrix(center), centerTable);
            Label divider = new Label("    ");

            if (cluster.getCovariationMatrix() != null){
                Label covarianceLabel = new Label("Covariance Matrix");
                TableView covarianceTable = new TableView();
                TableHelper.fillData(cluster.getCovariationMatrix(), covarianceTable);
                covarianceTable.setMinHeight(25 * (cluster.getCovariationMatrix().getRowsCount()+1));
                covarianceTable.setMaxHeight(25 * (cluster.getCovariationMatrix().getRowsCount()+1));
                clustersParams.getChildren().addAll(clusterLabel, centerLabel, centerTable, covarianceLabel, covarianceTable, divider);
            } else {
                clustersParams.getChildren().addAll(clusterLabel, centerLabel, centerTable, divider);
            }
        }
    }

    //---------------------------------------Reduce property space-----------------------------------------//

    public void reduceDataPropertySpace(ActionEvent event){
        new MultiDimensionalScalingDialog(new MultiDimensionalScalingDialog.Listener() {
            @Override
            public void onCancel() {

            }

            @Override
            public void onOk(int skeletonSize, int newDimensionalSize) {
                Platform.runLater(() ->{
                    MultidimensionalScaling multidimensionalScaling = new MultidimensionalScaling(new EuclideMetric(), skeletonSize, newDimensionalSize);
                    currentData = multidimensionalScaling.applyAlgrorithm(inputData.getData());
                    TableHelper.fillData(currentData, transformedData);
                   SherdsDiagramHelper.showDiagram(shepardaDiagram, inputData, currentData, new EuclideMetric());
                });
            }
        });
    }

    //----------------------------------------ClassicalPCA----------------------------------------------//

    public void reduceDataPropertySpacePCA(ActionEvent event){
        Platform.runLater(() -> {
            ClassicalPCA pca = new ClassicalPCA();
            Pair<List<Double>, double[][]> pcaRes = pca.reduceSpace(inputData.getData(), 0.0001, 3000);
            pcaData = new Matrix(pcaRes.getValue());
            List<Double> infoPCA = pcaRes.getKey();
            ChartHelper.showPCAInfoDiagram(infoPCA, PCADiagromInfo);
            TableHelper.fillData(pcaData, transformedDataPCA);
        });
    }

    public void reducePCAData(ActionEvent event){
        int newSpaceSize = Integer.parseInt(PCANewSpaceSize.getText());
        Matrix temp = new Matrix(inputData.getRowsCount(), newSpaceSize);
        for (int i = 0; i < inputData.getRowsCount(); i++)
            for (int j = 0; j < newSpaceSize; j++)
                temp.setValueAt(i, j, pcaData.getValueAt(i, j));
        currentData = temp;
        TableHelper.fillData(currentData, transformedDataPCA);
    }

}
