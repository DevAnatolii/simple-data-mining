package sample.clusteringAlgorithm.em;

import sample.clusteringAlgorithm.IAlgorithm;
import sample.clusteringAlgorithm.ICluster;
import sample.model.Matrix;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EmAlgorithm implements IAlgorithm{

    protected Matrix objects;
    protected int numberOfClasses;
    protected double precision = 0.01;
    protected int repeatCount = 10;
    protected int maxItarationCount = 40;
    //-------------------------//
    protected Matrix G;
    protected Matrix M;
    protected Matrix[] SUM;
    protected Matrix C;
    protected Matrix previousG;
    //-------------------------//
    protected double finestLValue;
    protected Matrix finestG;
    protected Matrix finestM;
    protected Matrix[] finestSUM;
    protected Matrix finestC;
    protected Matrix finestPreviousG;

    public EmAlgorithm(int clusterCount) {
        numberOfClasses = clusterCount;
    }

    @Override
    public List<ICluster> cluster(double[][] data) {
        objects = new Matrix(data);
        finestLValue = - Double.MAX_VALUE;
        for (byte i = 0; i < repeatCount; i++)
        {
            System.out.println("Apply clusteringAlgorithm: " +(i+1));
            applyAlgorithm();
            saveFinestResult();
        }
        restoreFinestResult();
        return createIClusters();
    }

    protected void applyAlgorithm(){
        int iterationCount = 0;
        G = new Matrix(objects.getRowsCount(), numberOfClasses);
        randomGenerateM();
        randomGenerateSum();
        randomGanarateC();
        recalculateG();
        stepM();
        stepE();
        stepM();
        double deviation = getMaxDeviation();
        System.out.println("Deviation: " + deviation);
        while (deviation>precision && maxItarationCount > iterationCount++){
            stepE();
            stepM();
            deviation = getMaxDeviation();
            System.out.println("Deviation: " + deviation);
        }
    }

    protected void saveFinestResult(){
        double LValue = calculateFunctionL();
        if (LValue > finestLValue){
            finestC         = C;
            finestG         = G;
            finestM         = M;
            finestSUM       = SUM;
            finestPreviousG = previousG;
            finestLValue    = LValue;
        }
    }

    protected void restoreFinestResult(){
        C         = finestC;
        G         = finestG;
        M         = finestM;
        SUM       = finestSUM;
        previousG = finestPreviousG;
    }

    protected List<ICluster> createIClusters(){
        List<ICluster> clusters = new ArrayList<>(numberOfClasses);
        for (int c = 0; c < numberOfClasses; c++) {

            Cluster cluster = new Cluster(M.getRow(c));
            for (int objectPos = 0; objectPos < objects.getRowsCount(); objectPos++)
                if (isKClass(c, objectPos)) cluster.addPoint(objects.getRow(objectPos));
            cluster.setWeight(C.getValueAt(0, c));
            cluster.setCovarianceMatrix(SUM[c]);

            if (cluster.size() > 0) clusters.add(cluster);
        }
        return clusters;
    }

    protected boolean isKClass(int k, int objectIndex){
        double mnemoMax = G.getValueAt(objectIndex, k);
        for (int c = 0; c < numberOfClasses; c++) {
            if (c == k) continue;
            if (mnemoMax < G.getValueAt(objectIndex, c)) return false;
            if (mnemoMax == G.getValueAt(objectIndex, c)) return k < c;
        }
        return true;
    }

    //+++
    protected void randomGenerateM(){
        M = new Matrix(numberOfClasses, objects.getColumnCount());

        Random random = new Random(System.currentTimeMillis());
        ArrayList<Integer> randomPoints = new ArrayList(numberOfClasses);

        while (randomPoints.size() < numberOfClasses){
            int randomValue = random.nextInt(objects.getRowsCount());
            if (randomPoints.contains(randomValue)) continue;
            randomPoints.add(randomValue);
            System.out.println("Random point: "+ randomValue);
        }
//        randomPoints.add(25);
//        randomPoints.add(45);
//        randomPoints.add(85);

        for (int i =0; i < numberOfClasses; i++)
            for (int propIndex = 0; propIndex < objects.getColumnCount(); propIndex++)
                M.setValueAt(i, propIndex, objects.getValueAt(randomPoints.get(i), propIndex) );
    }

    //+++
    protected void randomGenerateSum(){
        int objectPropertiesCount = objects.getColumnCount();
        SUM = new Matrix[numberOfClasses];
        for (int c = 0; c < numberOfClasses; c++) {
            SUM[c] = new Matrix(objectPropertiesCount, objectPropertiesCount);

            for (int r = 0; r < objectPropertiesCount; r++)
                for (int col = 0; col < objectPropertiesCount; col++)
                    SUM[c].setValueAt(r, col, r == col ? 1.0 : 0.0);

        }
    }

    //+++
    protected void randomGanarateC(){
        C = new Matrix(1, numberOfClasses);
        for(int i = 0; i < numberOfClasses; i++)
            C.setValueAt(0, i, 1.0 / numberOfClasses);
    }

    //--------------------------------E Step---------------------------------//

    protected void stepE() {
        savePreviousG();
        recalculateG();
    }

//++
    protected void savePreviousG() {
        previousG = Matrix.copy(G);
    }

    //++
    protected void recalculateG() {
        for (int objectPos = 0 ; objectPos<objects.getRowsCount(); objectPos++) {

            //calculate denominator
            double denominator = 0;
            for (int clazz = 0; clazz<numberOfClasses; clazz++){
                double Fvalue = f(objectPos, clazz);
                if (!Double.isNaN(Fvalue))
                    denominator += C.getValueAt(0, clazz) * Fvalue;
            }

            for (int classPos = 0; classPos < numberOfClasses; classPos++) {
                double Fvalue = f(objectPos, classPos);
                double numerator = 0;
                if (!Double.isNaN(Fvalue))
                    numerator = C.getValueAt(0, classPos) * Fvalue;

                G.setValueAt(objectPos, classPos, denominator != 0? numerator / denominator : 0.0);
            }
        }
    }

    protected double f (int objectIndex, int classIndex){
        Matrix object = new Matrix(objects.getRow(objectIndex), true);
        Matrix center = new Matrix(M.getRow(classIndex), true);
        Matrix Sum    = SUM[classIndex];

        double determinant = Sum.calculateDeterminant();
        double a = 1.0/Math.sqrt( Math.pow(2.0*Math.PI, object.getColumnCount()) * Math.abs(determinant) );
        Matrix inverseSum = Sum.inverse();
        Matrix difference = object.minus(center);
        Matrix transposeDifference = difference.transpose();

        double b = -0.5 * difference.times(inverseSum).times(transposeDifference).getValueAt(0, 0);

        if (Double.isNaN((a * Math.exp(b))))
            System.out.println("Val:"+(a * Math.exp(b)));
        return a*Math.exp(b);
    }

    //------------------------------M Step---------------------------------//

    protected void stepM() {
        calculateM();
        calculateSUM();
        calculateC();
    }

    //+
    protected void calculateM() {
        M = new Matrix(numberOfClasses, objects.getColumnCount());
        M.reset();

        for (int classPos = 0; classPos < numberOfClasses; classPos++){
            double sumG = 0.0;

            for (int objectPos = 0; objectPos < objects.getRowsCount(); objectPos++){
                sumG += G.getValueAt(objectPos, classPos);
                for (int propertyPos = 0; propertyPos < objects.getColumnCount(); propertyPos++)
                    M.increaseValueAt(classPos, propertyPos, G.getValueAt(objectPos, classPos)* objects.getValueAt(objectPos, propertyPos) );
            }

            for (int propertyPos = 0; propertyPos < objects.getColumnCount(); propertyPos++)
                M.setValueAt(classPos, propertyPos, M.getValueAt(classPos, propertyPos) / sumG);
        }
    }
//+
    protected void calculateSUM() {
        SUM = new Matrix[numberOfClasses];

        for (int indexClass = 0; indexClass < numberOfClasses; indexClass++){
            double sumG = 0.0;
            Matrix center = new Matrix(M.getRow(indexClass), true);
            Matrix sum = new Matrix(objects.getColumnCount(), objects.getColumnCount());
            sum.reset();

            for (int objectIndex = 0; objectIndex<objects.getRowsCount(); objectIndex++){
                Matrix object = new Matrix(objects.getRow(objectIndex), true);
                Matrix differece = object.minus(center);
                Matrix transposeDifference = differece.transpose();
                Matrix temp = transposeDifference.times(differece);
                temp.multiplyByConstant(G.getValueAt(objectIndex, indexClass));
                sumG += G.getValueAt(objectIndex, indexClass);
                sum = sum.plus(temp);
            }

            sum.divideByConstant(sumG);
            SUM[indexClass] = sum;

            if (sum.calculateDeterminant() == 0.0 || Double.isNaN(sum.calculateDeterminant()))
                System.out.println("loh");
        }
    }

    //++
    protected void calculateC() {
        C = new Matrix(1, numberOfClasses);
        C.reset();

        for (int indexClass = 0; indexClass < numberOfClasses; indexClass++)
            for (int objectIndex = 0; objectIndex < objects.getRowsCount(); objectIndex++)
                C.increaseValueAt(0, indexClass, G.getValueAt(objectIndex, indexClass));

        C.divideByConstant(objects.getRowsCount());
    }

    //++
    protected double getMaxDeviation(){
        double max = 0.0;
        for (int i = 0; i<objects.getRowsCount(); i++)
            for (int j = 0; j < numberOfClasses; j++){
                double temp = Math.abs(previousG.getValueAt(i, j) - G.getValueAt(i, j) );
                if (temp > max) max = temp;
            }
        return max;
    }

    //++
    protected double calculateFunctionL(){
        double res = 0;
        int objectCount = objects.getRowsCount();
        for (int objectIndex = 0; objectIndex<objectCount; objectIndex++) {
            double sum = 0;
            for (int classIndex = 0; classIndex < numberOfClasses; classIndex++) {
                double Fvalue = f(objectIndex, classIndex);
                if (!Double.isNaN(Fvalue))
                    sum += C.getValueAt(0, classIndex) * Fvalue;
            }
            res += Math.log(sum);
        }
        return res;
    }

}


