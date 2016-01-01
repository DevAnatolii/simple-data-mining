package sample.visualization.ParallelCoordChart;


import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Serie {

    List <double[]> points;
    String title;
    Color color = null;

    public Serie(String title) {
        this.title = title;
        points = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void addPoint(double[] point){
        points.add(point);
    }

    public int getSize(){
        return points.size();
    }

    public double[] getPoint(int position){
        return points.get(position);
    }

    public Pair<double[], double[]> getMinMaxPoints(){
        if (getSize() == 0) return null;
        int demansionSize = points.get(0).length;
        double[] maxPoint = new double[demansionSize];
        double[] minPoint = new double[demansionSize];

        for (int pointPos = 0; pointPos < points.size(); pointPos++){
            for (int j = 0; j < demansionSize; j++){
                if (pointPos == 0) { maxPoint[j] = points.get(pointPos)[j]; minPoint[j] = maxPoint[j]; continue; }
                double value = points.get(pointPos)[j];
                if (maxPoint[j] < value) maxPoint[j] = value;
                if (minPoint[j] > value) minPoint[j] = value;
            }
        }

        return new Pair<>(minPoint, maxPoint);
    }
}
