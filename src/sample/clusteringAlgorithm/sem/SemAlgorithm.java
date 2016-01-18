package sample.clusteringAlgorithm.sem;

import sample.clusteringAlgorithm.em.EmAlgorithm;
import sample.model.Matrix;

import java.util.Random;

public class SemAlgorithm extends EmAlgorithm{

    public SemAlgorithm(int numberOfClasses) {
        super(numberOfClasses);
    }

    protected void applyAlgorithm(){
        System.out.println("Start clusteringAlgorithm");
        double deviation;
        randomGenerateG();
        int iterationCount = 0;
        do {
            stepS();
            stepM();
            stepE();
            deviation = getMaxDeviation();
            System.out.println("Deviation: " + deviation);
        } while (deviation > precision && iterationCount++ < maxItarationCount );
    }

    private void randomGenerateG() {
        Random random = new Random(System.currentTimeMillis());

        G = new Matrix(objects.getRowsCount(), numberOfClasses + 1); // +1 for index class
        for (int i = 0; i < objects.getRowsCount(); i++) {
            double sum = 0;
            for (int j = 0; j < numberOfClasses; j++) {
                G.setValueAt(i, j, random.nextDouble());
                sum += G.getValueAt(i, j);
            }
            G.setValueAt(i, numberOfClasses, sum);
        }

        for (int i = 0; i < objects.getRowsCount(); i++)
            for (int j = 0; j < numberOfClasses; j++)
                G.setValueAt(i, j, G.getValueAt(i, j) / G.getValueAt(i, numberOfClasses));
    }

    //-----------------------------Step S-------------------------------------//
    private void stepS(){
        randomGenerateClasses();
    }

    private void randomGenerateClasses() {
        Random random = new Random(System.currentTimeMillis());

        for (int i = 0 ; i < objects.getRowsCount(); i++){
            G.setValueAt(i, numberOfClasses, numberOfClasses -1); // set last custer by default
            double r = random.nextDouble();
            double sum = 0;
            for (int j = 0; j < numberOfClasses; j++)
            {
                sum += G.getValueAt(i,j);
                if (r <= sum){
                    G.setValueAt(i, numberOfClasses, j);
                    break;
                }
            }
        }
    }

    //-----------------------------Step M------------------------------------//

    protected void calculateM() {
        M = new Matrix(numberOfClasses, objects.getColumnCount());
        M.reset();
        for (int indexClass = 0; indexClass < numberOfClasses; indexClass++){
            double n = 0;

            for (int objectIndex = 0; objectIndex <objects.getRowsCount(); objectIndex++)
                if (G.getValueAt(objectIndex, numberOfClasses) == indexClass){
                    n  += 1.0;
                    for (int l = 0; l < objects.getColumnCount(); l++)
                        M.increaseValueAt(indexClass, l, objects.getValueAt(objectIndex, l));
                }

            if (n != 0)
                for (int l = 0; l < objects.getColumnCount(); l++)
                    M.setValueAt(indexClass, l, M.getValueAt(indexClass, l) / n );
        }
    }

    protected void calculateSUM() {
        SUM = new Matrix[numberOfClasses];
        int objectPropertiesCount = objects.getColumnCount();

        for (int indexClass = 0; indexClass < numberOfClasses; indexClass++) {
            double n = 0.0;

            Matrix center = new Matrix(M.getRow(indexClass), true);
            Matrix sum = new Matrix(objectPropertiesCount, objectPropertiesCount);
            sum.reset();

            for (int objectPos = 0; objectPos < objects.getRowsCount(); objectPos++) {
                if (G.getValueAt(objectPos, numberOfClasses) == indexClass) {
                    n += 1.0;
                    Matrix object = new Matrix(objects.getRow(objectPos), true);
                    Matrix difference = object.minus(center);
                    Matrix transposeDifference = difference.transpose();
                    sum = sum.plus(transposeDifference.times(difference));
                }
            }

            if (n >= 0.5) sum.divideByConstant(n);
            SUM[indexClass] = sum;
        }
    }

    protected void calculateC() {
        C = new Matrix(1, numberOfClasses);
        C.reset();
        for (int indexClass = 0; indexClass < numberOfClasses; indexClass++){

            for (int objectIndex = 0; objectIndex<objects.getRowsCount(); objectIndex++)
                if (G.getValueAt(objectIndex, numberOfClasses) == indexClass)
                    C.increaseValueAt(0, indexClass, 1);

            C.setValueAt(0, indexClass, C.getValueAt(0, indexClass) / objects.getRowsCount());
        }
    }
}

