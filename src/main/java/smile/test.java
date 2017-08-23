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
import java.util.Random;

public class test {
    public static void main(String[] args) throws Exception{

        // loading data
        ArffParser arffParser = new ArffParser();
        arffParser.setResponseIndex(52);
        AttributeDataset model6 = arffParser.parse(new FileInputStream("data/model7/model6_P31_RI53.arff"));
        double[][] datax = model6.toArray(new double[model6.size()][]);
        int[] datay = model6.toArray(new int[model6.size()]);

        System.out.println("Data X");

        for(int i=0;i<datax.length;i++) {
            //for(int j=0;j<datax[0].length;j++) {
                System.out.print(datax[i][datax[0].length]+"\t");
            //}
            System.out.print("\n");
        }

        System.out.println("Data Y");

        for(int i=0;i<datay.length;i++) {
            System.out.print(datay[i]+"\t");

        }

        // size of examples
        int n = datax.length;

        // size of examples after split in certain percentage
        int m = n * 80 / 100;
        int[] index = Math.permutate(n); // to get the index of the row randomly

        // training data after splitting in certain percentage
        double[][] trainx = new double[m][];
        int[] trainy = new int[m];
        for (int i = 0; i < m; i++) {
            trainx[i] = datax[index[i]];
            trainy[i] = datay[index[i]];
        }

        // testing data after splitting in certain percentage
        double[][] testx = new double[n - m][];
        int[] testy = new int[n - m];
        for (int i = m; i < n; i++) {
            testx[i - m] = datax[index[i]];
            testy[i - m] = datay[index[i]];
        }

        // training with Random Forest classification
        RandomForest forest = new RandomForest(model6.attributes(), trainx, trainy, 100);

        // prediction and calculating the classes classified
        int[] yPredict = new int[testy.length];
        int count_error = 0, count_classified = 0;

        for (int i = 0; i < testx.length; i++) {
            yPredict[i] = forest.predict(testx[i]);
            if (testy[i] != yPredict[i])
                count_error++;
            else
                count_classified++;
        }

        // classified instances
        double total_instances = count_error + count_classified;

        // getting the result of confusion matrix, precision and recall
        FMeasure fmeasure = new FMeasure();
        Precision precision = new Precision();
        Recall recall = new Recall();

        Double result_fmeasure = Double.isNaN(fmeasure.measure(testy, yPredict)) ? 0.0 : fmeasure.measure(testy, yPredict),
                result_precision = Double.isNaN(precision.measure(testy, yPredict)) ? 0.0 : precision.measure(testy, yPredict),
                result_recall = Double.isNaN(recall.measure(testy, yPredict)) ? 0.0 : recall.measure(testy, yPredict);

        System.out.println("Confusion matrix: " + result_fmeasure.toString());
        System.out.println("FMeasure: " + result_fmeasure.toString());
        System.out.println("Precision: " + result_precision.toString());
        System.out.println("Recall: " + result_recall.toString());

    }
}
