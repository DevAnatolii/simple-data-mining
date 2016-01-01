package sample.visualization.ParallelCoordChart;


import com.sun.javafx.tk.Toolkit;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ParallelCoordsChart extends Pane {
    List<Serie> series;
    Canvas canvas;
    String title;
    Colors colors = new Colors();
    private double[] maxPoint;
    private double[] minPoint;

    private double axisHeaderHeight;
    private double labelsSerieHeight;
    private double chartAxixesStart;
    private double chartAxixesEnd;
    private double serieHeightLabel;
    private final double AXIS_WIDTH = 2;
    private final double POINT_SIZE = 5;
    private final double DEFAULT_LINE_WIDTH = 1.4;
    private final double SERIE_COLOR_LABEL_SIZE = 15;
    private final double SPACE_BETWEEN_LABLE_AND_COLOR_LABEL = 7;
    private final double SERIE_MARGIN_HORIZONTAL_BETWEEN_SERIA_LABELS = 30;
    private final double SERIE_MARGIN_VERTICAL_BETWEEN_SERIA_LABELS = 10;
    private final double MARGIN_BOUNDS = 20;
    private final double SPACE_BETWEEN_HEADERS_AND_CHART = 5;
    private final double SPACE_BETWEEN_LABLES_AND_CHART = 20;
    Color defaultBackgroundColor = Color.color(0.97, 0.97, 0.97);
    Color defaultColor = Color.color(0.1, 0.1, 0.1);
    Color defaultStrokeColor = Color.BLACK;

    public ParallelCoordsChart(String title, int width, int height) {
        this.title = title;
        series = new ArrayList<>();
        this.canvas = new Canvas(width, height);
        this.getChildren().add(canvas);

        drawBackGround();
        canvas.widthProperty().addListener(observable -> redrawChart());
        canvas.heightProperty().addListener(observable -> redrawChart());
    }

    public List<Serie> getSeries() {
        return series;
    }

    public void addSeria(Serie serie){
        series.add(serie);
        redrawChart();
    }

    public void addAllSerias(Collection<Serie> series){
        this.series.addAll(series);
        redrawChart();
    }

    public void setBackgroundColor(Color color){
        defaultBackgroundColor = color;
        redrawChart();
    }

    private void redrawChart(){
        drawBackGround();
        if (series.size() == 0) return;
        colors.reset();
        for (Serie serie: series)
            if (serie.getColor() == null) serie.setColor(colors.nextColor());
        obtainMinMaxPoints();
        mesureChartComponents();
        drawAxis();
        drawSeries();
        drawLables();
    }

    private void drawBackGround(){
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(defaultBackgroundColor);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void obtainMinMaxPoints(){
        minPoint = null; maxPoint = null;
        for (int i = 0; i < series.size(); i++){
            Pair<double[], double[]> serieMinMaxPoints = series.get(i).getMinMaxPoints();
            double[] serieMinPoint = serieMinMaxPoints.getKey();
            double[] serieMaxPoint = serieMinMaxPoints.getValue();
            if (serieMinMaxPoints == null) return;
            if (minPoint == null || maxPoint == null) {minPoint = serieMinPoint; maxPoint = serieMaxPoint; continue;}

            for (int j = 0; j < minPoint.length; j++) {
                if (maxPoint[j] < serieMaxPoint[j]) maxPoint[j] = serieMaxPoint[j];
                if (minPoint[j] > serieMinPoint[j]) minPoint[j] = serieMinPoint[j];
            }
        }
    }

    private void mesureChartComponents(){
        GraphicsContext context = canvas.getGraphicsContext2D();
        double contentWidth = canvas.getWidth() - 2*MARGIN_BOUNDS;
        int headerCount = series.get(0).getPoint(0).length;
        chartAxixesStart = Toolkit.getToolkit().getFontLoader().computeStringWidth("X1", context.getFont()) / 2;
        chartAxixesEnd = contentWidth - Toolkit.getToolkit().getFontLoader().computeStringWidth("X"+headerCount, context.getFont()) / 2;
        axisHeaderHeight = Toolkit.getToolkit().getFontLoader().getFontMetrics(context.getFont()).getLineHeight();

        int lineCount = series.size() > 0? 1 : 0;
        double tempWidth = 0.0;
        for (int i = 0; i < series.size(); i++){
            Serie serie = series.get(i);
            double seriaLabelWidth = SERIE_COLOR_LABEL_SIZE + SPACE_BETWEEN_LABLE_AND_COLOR_LABEL
                    + Toolkit.getToolkit().getFontLoader().computeStringWidth(serie.getTitle() != null? serie.getTitle() : "Serie #" + (i+1), context.getFont());
            if (tempWidth != 0.0) seriaLabelWidth += SERIE_MARGIN_HORIZONTAL_BETWEEN_SERIA_LABELS;
            if (tempWidth + seriaLabelWidth > contentWidth){
                lineCount++;
                tempWidth = 0.0;
                seriaLabelWidth -= SERIE_MARGIN_HORIZONTAL_BETWEEN_SERIA_LABELS;
            }

            tempWidth += seriaLabelWidth;
        }

        serieHeightLabel = Math.max(SERIE_COLOR_LABEL_SIZE, axisHeaderHeight);
        if (lineCount == 1) labelsSerieHeight = serieHeightLabel;
        else labelsSerieHeight = serieHeightLabel*lineCount + (lineCount-1)*SERIE_MARGIN_VERTICAL_BETWEEN_SERIA_LABELS;
    }

    private void drawAxis(){
        int axisCount = series.get(0).getPoint(0).length;
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.setFill(defaultColor);
        context.setStroke(defaultStrokeColor);
        double step = (chartAxixesEnd - (axisCount - 1) * AXIS_WIDTH - chartAxixesStart)/(axisCount-1);
        double startAxisHeight = MARGIN_BOUNDS + axisHeaderHeight + SPACE_BETWEEN_HEADERS_AND_CHART;
        double axisHeight = canvas.getHeight() - 2*MARGIN_BOUNDS - startAxisHeight - labelsSerieHeight - SPACE_BETWEEN_LABLES_AND_CHART;

        for(int axisPos = 0; axisPos < axisCount; axisPos++){
            String headerLabel = "X"+(axisPos+1);
            double headerHalfWidth = Toolkit.getToolkit().getFontLoader().computeStringWidth(headerLabel, context.getFont()) / 2;
            double centerAxis = MARGIN_BOUNDS + chartAxixesStart + axisPos*step;
            context.fillRect(centerAxis - AXIS_WIDTH / 2, startAxisHeight, AXIS_WIDTH, axisHeight);
            context.strokeText(headerLabel, centerAxis - headerHalfWidth, MARGIN_BOUNDS+axisHeaderHeight);
        }
    }

    private void drawSeries(){
        for (Serie serie : series)
            drawSimpleSerie(serie);
    }

    private void drawSimpleSerie(Serie serie){
        int axisCount = series.get(0).getPoint(0).length;
        GraphicsContext context = canvas.getGraphicsContext2D();
        context.setFill(serie.getColor());
        context.setLineWidth(DEFAULT_LINE_WIDTH);
        context.setStroke(serie.getColor());
        double step = (chartAxixesEnd - (axisCount - 1) * AXIS_WIDTH - chartAxixesStart)/(axisCount-1);
        double startAxisHeight = MARGIN_BOUNDS + axisHeaderHeight + SPACE_BETWEEN_HEADERS_AND_CHART;
        double axisHeight = canvas.getHeight() - 2*MARGIN_BOUNDS - startAxisHeight - labelsSerieHeight - SPACE_BETWEEN_LABLES_AND_CHART;

        int pointCount = serie.getSize();
        for (int pointPos = 0; pointPos < pointCount; pointPos++) {
            double[] scaledPoint = getScalePoint(serie.getPoint(pointPos));

            for (int axisPos = 0; axisPos < axisCount; axisPos++) {
                double pointX = chartAxixesStart + MARGIN_BOUNDS + axisPos * step;
                double pointY = startAxisHeight + axisHeight * (1.0-scaledPoint[axisPos]);
                context.fillOval(pointX - POINT_SIZE/2, pointY - POINT_SIZE /2, POINT_SIZE, POINT_SIZE);

                if (axisPos < axisCount - 1)
                    context.strokeLine(pointX, pointY, pointX + step, startAxisHeight + axisHeight * (1.0 - scaledPoint[axisPos+1]));
            }

        }
    }

    private double[] getScalePoint(double[] point){
        double[] sclaledPoint = new double[point.length];
        for (int i = 0; i < point.length; i++) sclaledPoint[i] = (point[i] - minPoint[i])/(maxPoint[i] - minPoint[i]);
        return sclaledPoint;
    }

    private void drawLables(){
        GraphicsContext context = canvas.getGraphicsContext2D();
        double contentWidth = canvas.getWidth() - 2*MARGIN_BOUNDS;
        double heightStart = canvas.getHeight() - MARGIN_BOUNDS - labelsSerieHeight;
        double startWidth = MARGIN_BOUNDS;

        for (int i = 0; i < series.size(); i++){
            Serie serie = series.get(i);
            String serieTitle = serie.getTitle() != null? serie.getTitle() : "Serie #" + (i+1);
            double seriaLabelWidth = SERIE_COLOR_LABEL_SIZE + SPACE_BETWEEN_LABLE_AND_COLOR_LABEL
                    + Toolkit.getToolkit().getFontLoader().computeStringWidth(serieTitle, context.getFont());
            if (startWidth != MARGIN_BOUNDS) seriaLabelWidth += SERIE_MARGIN_HORIZONTAL_BETWEEN_SERIA_LABELS;
            if (startWidth + seriaLabelWidth > contentWidth){
                heightStart += serieHeightLabel + SERIE_MARGIN_VERTICAL_BETWEEN_SERIA_LABELS;
                seriaLabelWidth -= SERIE_MARGIN_HORIZONTAL_BETWEEN_SERIA_LABELS;
                startWidth = MARGIN_BOUNDS;
            }
            drawSerieLabel(context, serieTitle, serie.getColor(), startWidth != MARGIN_BOUNDS? startWidth + SERIE_MARGIN_HORIZONTAL_BETWEEN_SERIA_LABELS : startWidth, heightStart);
            startWidth += seriaLabelWidth;
        }
    }

    private void drawSerieLabel(GraphicsContext context, String serieTitle, Color serieColor, double x, double y){
        double margin = 0.0;
        context.setFill(serieColor);
        context.setStroke(defaultStrokeColor);
        if (SERIE_COLOR_LABEL_SIZE < serieHeightLabel) margin = (serieHeightLabel - SERIE_COLOR_LABEL_SIZE) /2;
        context.fillRect(x, y + margin, SERIE_COLOR_LABEL_SIZE, SERIE_COLOR_LABEL_SIZE);

        context.setFill(defaultColor);
        if (axisHeaderHeight < serieHeightLabel) margin = (serieHeightLabel - axisHeaderHeight) / 2;
        else margin = 0.0;
        context.strokeText(serieTitle, x + SPACE_BETWEEN_LABLE_AND_COLOR_LABEL + SERIE_COLOR_LABEL_SIZE, y - margin + context.getFont().getSize());
    }

}
