package smile;

import jdk.nashorn.internal.objects.annotations.Function;
import smile.classification.RandomForest;
import smile.data.AttributeDataset;
import smile.data.parser.ArffParser;
import smile.math.Math;
import smile.validation.ConfusionMatrix;
import smile.validation.FMeasure;
import smile.validation.Precision;
import smile.validation.Recall;

import java.io.FileInputStream;
import java.util.*;

public class test {
    public static void main(String[] args) throws Exception{
        int[] testYClass = {0,0,0,1,1,1,2,2};
        int[] testYPredictClass = {0,0,0,1,1,1,0,2};
        int max = 2;
        int[][] matrix = new int[max+1][max+1];

        for (int k=0; k<testYClass.length;k++) {
            int datax = testYClass[k];
            int datay = testYPredictClass[k];
            matrix[datax][datay]++;
        }

        for (int i = 0; i <= max; i++) {
            for (int j = 0; j <= max; j++) {
                System.out.print(matrix[i][j]+"\t");
            }
            System.out.print("\n");
        }

        // getting the result of confusion matrix, precision and recall
        /**FMeasure fmeasure = new FMeasure();
        Precision precision = new Precision();
        Recall recall = new Recall();

        Double result_fmeasure = Double.isNaN(fmeasure.measure(testYClass, testYPredictClass)) ? 0.0 : fmeasure.measure(testYClass, testYPredictClass),
                result_precision = Double.isNaN(precision.measure(testYClass, testYPredictClass)) ? 0.0 : precision.measure(testYClass, testYPredictClass),
                result_recall = Double.isNaN(recall.measure(testYClass, testYPredictClass)) ? 0.0 : recall.measure(testYClass, testYPredictClass);

        System.out.println("FMeasure: " + result_fmeasure.toString());
        System.out.println("Precision: " + result_precision.toString());
        System.out.println("Recall: " + result_recall.toString());**/

    }
}