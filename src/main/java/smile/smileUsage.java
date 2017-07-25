package smile;

import smile.data.AttributeDataset;
import smile.data.parser.ArffParser;
import smile.regression.RandomForest;

import java.io.File;
import java.io.FileInputStream;


/* this class contains the use of Smile Java API for predictive modeling */
public class smileUsage {

    // --------------- attributes ---------------
    private ArffParser arffParser = null;
    private AttributeDataset attributeDataset = null;
    private RandomForest forest = null;

    /* --------------- methods ---------------

     load the training data */
    public void loadData(File file) throws Exception {
        arffParser = new ArffParser().setResponseIndex(4);
        attributeDataset = arffParser.parse(new FileInputStream(file));
        double[][] x = attributeDataset.toArray(new double[attributeDataset.size()][]);
        int[] y = attributeDataset.toArray(new int[attributeDataset.size()]);

    }

    // training the model with Random Forest
    public void trainModel() throws Exception {
        double[][] x = attributeDataset.toArray(new double[attributeDataset.size()][]);
        double[] y = attributeDataset.toArray(new double[attributeDataset.size()]);

        // maximum number of trees: 200; maximum number of leaves:4
        forest = new RandomForest(attributeDataset.attributes(), x, y, 200,4 );
    }

    // validating the model

}
