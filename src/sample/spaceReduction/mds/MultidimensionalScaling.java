package sample.spaceReduction.mds;


import javafx.util.Pair;
import sample.distanceMetric.Metric;
import sample.model.Matrix;
import sample.spaceReduction.pca.ClassicalPCA;


import java.util.ArrayList;
import java.util.List;

public class MultidimensionalScaling {

    private static final int v = -2;
    private static final double E = 0.05;

    List<Integer> skeleton;
    Metric metric;
    int K;
    int newDimensionalSize;

    Matrix data;
    Matrix distances;
    Matrix transformedData;

    public MultidimensionalScaling(Metric metric, int K, int newDimensionalSize) {
        this.metric = metric;
        this.K = K;
        this.newDimensionalSize = newDimensionalSize;
    }

    public Matrix applyAlgrorithm(double[][] data){
        this.data = new Matrix(data);
        transformedData = new Matrix(data.length, newDimensionalSize);
        transformedData.reset();

        calculateDistances();
        initializeSkeleton();
        buildSkeleton();
        stabilizeSkeleton();
        addRestToSkeleton();

        return transformedData;
    }

    //++
    private void calculateDistances(){
        System.out.println("calculateDistances");
        int objectCount = data.getRowsCount();
        distances = new Matrix(objectCount, objectCount);
        for (int objPos1 = 0; objPos1 < objectCount; objPos1++)
            for (int objPos2 = objPos1; objPos2 < objectCount; objPos2++) {
                if (objPos1 == objPos2) { distances.setValueAt(objPos1, objPos2, 0.0); continue;}
                distances.setValueAt(objPos1, objPos2, metric.distance(data.getRow(objPos1), data.getRow(objPos2)));
                distances.setValueAt(objPos2, objPos1, distances.getValueAt(objPos1, objPos2));
            }
    }

    private void initializeSkeleton(){
        int size = newDimensionalSize + 1;
        skeleton = findMostDistancePoint(size);

        double[][] points = new double[size][data.getColumnCount()];
        for (int i = 0; i < size; i++) points[i] = data.getRow(skeleton.get(i));

        ClassicalPCA pca = new ClassicalPCA();
        Pair<List<Double>, double[][]> pair =  pca.reduceSpace(points, E, 500);
        double[][] temp = pair.getValue();

        for (int j = 0; j < size; j++){
            int point = skeleton.get(j);
            for (int i = 0; i < newDimensionalSize; i++){
                transformedData.setValueAt(point, i, temp[j][i]);
            }
        }
    }

    private ArrayList<Integer> findMostDistancePoint(int size){
        ArrayList<Integer> points = new ArrayList<>();
        int objectCount = data.getRowsCount();
        double maxDistance = distances.getValueAt(0, 1);
        int obj1 = 0, obj2 = 1, objN = 2;

        for (int objPos1 = 0; objPos1 < objectCount; objPos1++){
            for (int objPos2 = objPos1 + 1; objPos2 < objectCount; objPos2++) {
                if (distances.getValueAt(objPos1, objPos2) > maxDistance) {
                    maxDistance = distances.getValueAt(objPos1, objPos2);
                    obj1 = objPos1;
                    obj2 = objPos2;
                }
            }
        }

        points.add(obj1);
        points.add(obj2);

        double min;
        while (points.size() < size) {
            maxDistance = 0.0;

            for (int objPos = 0; objPos < objectCount; objPos++) {
                min = distances.getValueAt(obj1, objPos);
                for (Integer point : points) {
                    if (min > distances.getValueAt(point, objPos)) {
                        min = distances.getValueAt(point, objPos);
                    }
                }
                if (min > maxDistance) {
                    maxDistance = min;
                    objN = objPos;
                }
            }

            points.add(objN);
        }
        return points;
    }

    private void buildSkeleton(){
        System.out.println("buildSkeleton");
        while (skeleton.size() < K){
            int objectPos = findMostDistantPoint();
            Matrix transformedObject = applyNewtonRaphson(objectPos, true);
            skeleton.add(objectPos);
            for (int i = 0; i < newDimensionalSize; i++){
                transformedData.setValueAt(objectPos, i, transformedObject.getValueAt(0, i));
            }
        }
    }

    //++
    private int findMostDistantPoint(){
        double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
        int object = -1;
        int objectCount = data.getRowsCount();
        for (int objPos = 0; objPos < objectCount; objPos++){
            if (skeleton.contains(objPos)) continue;
            for (int j = 0; j < skeleton.size(); j++) {
                int skeletonObjPos = skeleton.get(j);
                if (distances.getValueAt(objPos, skeletonObjPos) != 0 && distances.getValueAt(objPos, skeletonObjPos) < min)
                    min = distances.getValueAt(objPos, skeleton.get(j));
            }
                if (max < min){ max = min; object = objPos;}
        }
        return object;
    }

    private Matrix applyNewtonRaphson(int objectPos, boolean isNeedCalculateFirstState){
        Matrix x = new Matrix(1 ,newDimensionalSize);
        if (isNeedCalculateFirstState) {
            x.reset();
            double c = 0.0;

            for (int i = 0; i < skeleton.size(); i++) {
                int skeletonPos = skeleton.get(i);
                double ci = obtainObjectWeight(objectPos, skeletonPos);
                c += ci;
                for (int j = 0; j < newDimensionalSize; j++) {
                    x.increaseValueAt(0, j, ci*transformedData.getValueAt(skeletonPos, j));
                }
            }

            x.multiplyByConstant(1.0/c);
        } else {
            for (int i = 0; i < newDimensionalSize; i++)
                x.setValueAt(0, i, transformedData.getValueAt(objectPos, i));
        }

        Matrix xPrev, finestX = null;
        double finestDeviation = Double.MAX_VALUE;
        double dev;
        int iterationCount = 0;
        do {
            xPrev = x;
            Matrix gradient = new Matrix(newDimensionalSize, 1);
            for (int i = 0; i < newDimensionalSize; i++){
                gradient.setValueAt(i, 0, calculateFirstDerivativeX(xPrev, objectPos, i));
            }

            Matrix hessian = new Matrix(newDimensionalSize, newDimensionalSize);
            for (int i = 0; i < newDimensionalSize; i++)
                for (int j = 0; j < newDimensionalSize; j++)
                    if (i == j)
                        hessian.setValueAt(i, j, calculateSecondDerivativeXX(xPrev, objectPos, i));
                    else
                        hessian.setValueAt(i, j, calculateSecondDerivativeXY(xPrev, objectPos, i, j));
            Matrix inverseHessian = hessian.inverse();

            x = xPrev.minus(inverseHessian.times(gradient).transpose());//added transpose

            dev = getMaxDeviation(x, xPrev);
            if (dev < finestDeviation){
                finestDeviation = dev;
                finestX = x;
            }
        }while (dev > E && iterationCount++ < 100);

        return finestX;
    }

    private double obtainObjectWeight(int objectPos1, int objectPos2){
        if (objectPos1 == objectPos2) throw new RuntimeException("the same objects");
        return Math.pow(distances.getValueAt(objectPos1, objectPos2), v);
    }

    private double calculateFirstDerivativeX(Matrix x, int objectPosition,  int propertyNumber){
        double sum = 0.0;
        for (int i = 0; i < skeleton.size(); i++){
            int skeletonObjectPos = skeleton.get(i);
            sum += obtainObjectWeight(skeletonObjectPos, objectPosition)
                    * (1 - distances.getValueAt(skeletonObjectPos, objectPosition) / metric.distance(x.getRow(0), transformedData.getRow(skeletonObjectPos)))
                    * (x.getValueAt(0, propertyNumber) - transformedData.getValueAt(skeletonObjectPos, propertyNumber));
        }
        return 2*sum;
    }

    private double calculateSecondDerivativeXX(Matrix x, int objectPosition, int propertyNumber) {
        double sum = 0.0;
        for (int i = 0; i < skeleton.size(); i++) {
            int skeletonObjectPos = skeleton.get(i);
            double d = metric.distance(x.getRow(0), transformedData.getRow(skeletonObjectPos));
            double r = distances.getValueAt(skeletonObjectPos, objectPosition) / d;
            double xa = x.getValueAt(0, propertyNumber) - transformedData.getValueAt(skeletonObjectPos, propertyNumber);
            sum += obtainObjectWeight(skeletonObjectPos, objectPosition) * (r * Math.pow(xa / d, 2) - r + 1);
        }
        return 2 * sum;
    }

    private double calculateSecondDerivativeXY(Matrix x, int objectPosition, int propertyNumber1, int propertyNumber2){
        double sum = 0.0;
        for (int i = 0; i < skeleton.size(); i++) {
            int skeletonObjectPos = skeleton.get(i);
            double d = metric.distance(x.getRow(0), transformedData.getRow(skeletonObjectPos));
            double r = distances.getValueAt(skeletonObjectPos, objectPosition) / d;
            double xa = x.getValueAt(0, propertyNumber1) - transformedData.getValueAt(skeletonObjectPos, propertyNumber1);
            double xb = x.getValueAt(0, propertyNumber2) - transformedData.getValueAt(skeletonObjectPos, propertyNumber2);
            sum += obtainObjectWeight(skeletonObjectPos, objectPosition) * r * (xa/d) * (xb/d);
        }
        return 2 * sum;
    }

    private double getMaxDeviation(Matrix x1, Matrix x2){
        double max = Double.MIN_VALUE;
        for (int i = 0; i < x1.getRowsCount(); i++)
            for (int j = 0; j< x1.getColumnCount(); j++){
                double dif = Math.abs(x1.getValueAt(i, j) - x2.getValueAt(i, j));
                if (max < dif) max = dif;
            }
       // System.out.println("Max deviation: " + max);
        return max;
    }

    private double getMaxDeviationInPercent(Matrix x1, Matrix x2){
        double max = Double.MIN_VALUE;
        for (int i = 0; i < x1.getRowsCount(); i++)
            for (int j = 0; j< x1.getColumnCount(); j++){
                double dif = Math.abs(x1.getValueAt(i, j) / x2.getValueAt(i, j));
                dif = dif < 1? dif : dif - 1;
                if (max < dif) max = dif;
            }
        return max;
    }

    private void stabilizeSkeleton(){
        System.out.println("stabilizeSkeleton");
        double max; int objPosition;
        Matrix previousTransformedData;
        do {
            previousTransformedData = Matrix.copy(transformedData);
            max = Double.MIN_VALUE;
            objPosition = 0;
            for (int i = 0; i < skeleton.size(); i++) {
                double s = calculateStress(skeleton.get(i));
                if (s > max) {
                    max = s;
                    objPosition = skeleton.get(i);
                }
            }

            System.out.println("Max:" + max );

            skeleton.remove(Integer.valueOf(objPosition));
            Matrix transformedObject = applyNewtonRaphson(objPosition, true);
            skeleton.add(objPosition);
            for (int i = 0; i < newDimensionalSize; i++) {
                transformedData.setValueAt(objPosition, i, transformedObject.getValueAt(0, i));
            }
        }while (getMaxDeviation(transformedData, previousTransformedData) > E);
    }

    private  double calculateStress(int objectPos){
        double s = 0.0;
        for (int i = 0; i < skeleton.size(); i++){
            if (objectPos == skeleton.get(i)) continue;
            double d = metric.distance(transformedData.getRow(objectPos), transformedData.getRow(skeleton.get(i)));
            double R = distances.getValueAt(objectPos, skeleton.get(i));
            s += obtainObjectWeight(objectPos, skeleton.get(i)) *Math.pow(d - R, 2);
        }
        return s;
    }

    private void addRestToSkeleton(){
        System.out.println("addRestToSkeleton");
        for (int i = 0; i < data.getRowsCount(); i++){
            if (skeleton.contains(i)) continue;

            Matrix transformedObject = applyNewtonRaphson(i, true);
            for (int j = 0; j < newDimensionalSize; j++){
                transformedData.setValueAt(i, j, transformedObject.getValueAt(0, j));
            }
        }
    }
}
