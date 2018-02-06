package org.nerd.kid.evaluation;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ModelEvaluationTest {

    ModelEvaluation target;
    int[] testYClass =          {0, 1, 2, 0, 0, 1, 2, 0, 1, 0, 1, 2, 2};
    int[] testYPredictClass =   {0, 1, 2, 0, 1, 1, 2, 0, 1, 2, 2, 1, 1};
    int maxIndexClass;

    @Before
    public void setUp() throws Exception {
        target = new ModelEvaluation();
        maxIndexClass = 2;
    }

    @Test
    public void testConfusionMatrix() throws Exception {
        final int[][] confusMatrix = target.confusionMatrix(testYClass, testYPredictClass, maxIndexClass);

        assertThat(confusMatrix[0][0], is(3));
        assertThat(confusMatrix[0][1], is(1));
        assertThat(confusMatrix[0][2], is(1));
        assertThat(confusMatrix[1][0], is(0));
        assertThat(confusMatrix[1][1], is(3));
        assertThat(confusMatrix[1][2], is(1));
        assertThat(confusMatrix[2][0], is(0));
        assertThat(confusMatrix[2][1], is(2));
        assertThat(confusMatrix[2][2], is(2));
    }

    @Test
    public void testCalculTotalClass() throws Exception {
        int[][] matrix = target.confusionMatrix(testYClass, testYPredictClass, maxIndexClass);
        int Idx = maxIndexClass;

        final int totalClass = target.countingTotalClass(matrix, Idx);

        assertThat(totalClass, is(13));
    }

    @Test
    public void testCalculRowClass() throws Exception {
        int[][] matrix = target.confusionMatrix(testYClass, testYPredictClass, maxIndexClass);
        int Idx = maxIndexClass;

        final int[] totalRow = target.countingRowClass(matrix, Idx);

        assertThat(totalRow[0], is(5));
        assertThat(totalRow[1], is(4));
        assertThat(totalRow[2], is(4));
    }

    @Test
    public void testCalculColumnClass() throws Exception {
        int[][] matrix = target.confusionMatrix(testYClass, testYPredictClass, maxIndexClass);
        int Idx = maxIndexClass;

        final int[] totalColumn = target.countingColumnClass(matrix, Idx);

        assertThat(totalColumn[0], is(3));
        assertThat(totalColumn[1], is(6));
        assertThat(totalColumn[2], is(4));
    }

    @Test
    public void testCalculTruePositive() throws Exception {
        int[][] matrix = target.confusionMatrix(testYClass, testYPredictClass, maxIndexClass);
        int Idx = maxIndexClass;

        final int[] truePositive = target.countingTruePositive(matrix, Idx);

        assertThat(truePositive[0], is(3));
        assertThat(truePositive[1], is(3));
        assertThat(truePositive[2], is(2));
    }

    @Test
    public void testCalculFalseNegative() throws Exception {
        int[][] matrix = target.confusionMatrix(testYClass, testYPredictClass, maxIndexClass);
        int Idx = maxIndexClass;

        final int[] falseNegative = target.countingFalseNegative(matrix, Idx);

        assertThat(falseNegative[0], is(2));
        assertThat(falseNegative[1], is(1));
        assertThat(falseNegative[2], is(2));
    }

    @Test
    public void testCalculFalsePositive() throws Exception {
        int[][] matrix = target.confusionMatrix(testYClass, testYPredictClass, maxIndexClass);
        int Idx = maxIndexClass;

        final int[] falsePositive = target.countingFalsePositive(matrix, Idx);

        assertThat(falsePositive[0], is(0));
        assertThat(falsePositive[1], is(3));
        assertThat(falsePositive[2], is(2));
    }

    @Test
    public void testCalculTrueNegative() throws Exception {
        int[][] matrix = target.confusionMatrix(testYClass, testYPredictClass, maxIndexClass);
        int Idx = maxIndexClass;

        final int[] trueNegative = target.countingTrueNegative(matrix, Idx);

        assertThat(trueNegative[0], is(8));
        assertThat(trueNegative[1], is(6));
        assertThat(trueNegative[2], is(7));
    }

    @Test
    public void testAccuracy() throws Exception {
        int[][] matrix = target.confusionMatrix(testYClass, testYPredictClass, maxIndexClass);
        int Idx = maxIndexClass;
        int[] TP = target.countingTruePositive(matrix, Idx);
        int[] TN = target.countingTrueNegative(matrix, Idx);
        int[] FP = target.countingFalsePositive(matrix, Idx);
        int[] FN = target.countingFalseNegative(matrix, Idx);

        final double[] accuracy = target.accuracy(TP, TN, FP, FN);

        assertThat(accuracy[0], is(0.8461538553237915));
        assertThat(accuracy[1], is(0.692307710647583));
        assertThat(accuracy[2], is(0.692307710647583));
    }

    @Test
    public void testAverageAccuracy() throws Exception {
        double[] accuracy = {0.8461538553237915, 0.692307710647583, 0.692307710647583};

        final double averageAccuracy = target.averageAccuracy(accuracy);
        assertThat(averageAccuracy, is(0.7435896992683411));
    }

    @Test
    public void testPrecision() throws Exception {
        int[][] matrix = target.confusionMatrix(testYClass, testYPredictClass, maxIndexClass);
        int Idx = maxIndexClass;
        int[] TP = target.countingTruePositive(matrix, Idx);
        int[] FP = target.countingFalsePositive(matrix, Idx);

        final double[] precision = target.precision(TP, FP);

        assertThat(precision[0], is(1.0));
        assertThat(precision[1], is(0.5));
        assertThat(precision[2], is(0.5));
    }

    @Test
    public void testRecall() throws Exception {
        int[][] matrix = target.confusionMatrix(testYClass, testYPredictClass, maxIndexClass);
        int Idx = maxIndexClass;
        int[] TP = target.countingTruePositive(matrix, Idx);
        int[] FN = target.countingFalseNegative(matrix, Idx);

        final double[] recall = target.recall(TP, FN);

        assertThat(recall[0], is(0.6000000238418579));
        assertThat(recall[1], is(0.75));
        assertThat(recall[2], is(0.5));
    }

    @Test
    public void testSpecificity() throws Exception {
        int[][] matrix = target.confusionMatrix(testYClass, testYPredictClass, maxIndexClass);
        int Idx = maxIndexClass;
        int[] TN = target.countingTrueNegative(matrix, Idx);
        int[] FP = target.countingFalsePositive(matrix, Idx);

        final double[] specificity = target.specificity(TN, FP);

        assertThat(specificity[0], is(1.0));
        assertThat(specificity[1], is(0.6666666865348816));
        assertThat(specificity[2], is(0.7777777910232544));
    }

    @Test
    public void testFmeasure() throws Exception {
        int[][] matrix = target.confusionMatrix(testYClass, testYPredictClass, maxIndexClass);
        int Idx = maxIndexClass;
        int[] TP = target.countingTruePositive(matrix, Idx);
        int[] FP = target.countingFalsePositive(matrix, Idx);
        int[] FN = target.countingFalseNegative(matrix, Idx);
        double[] precision = target.precision(TP, FP);
        double[] recall = target.recall(TP, FN);

        final double[] fmeasure = target.fmeasure(precision, recall);

        assertThat(fmeasure[0], is(0.7500000186264513));
        assertThat(fmeasure[1], is(0.6));
        assertThat(fmeasure[2], is(0.5));
    }

    @Test
    public void testAverageFmeasure() throws Exception {
        double precision = 0.5;
        double recall = 0.75;

        final double fmeasure = target.averageFmeasure(precision, recall);

        assertThat(fmeasure, is(0.6));
    }

    @Test
    public void testAveragePrecisionMacro() throws Exception {
        double[] precision = {1.0, 0.5, 0.5};

        final double macroPrecision = target.averagePrecisionMacro(precision);
        assertThat(macroPrecision, is(0.6666666865348816));
    }

    @Test
    public void testAveragePrecisionMicro() throws Exception {
        int[] tp = {3, 3, 2};
        int[] fp = {0, 3, 2};

        final double microPrecision = target.averagePrecisionMicro(tp, fp);

        assertThat(microPrecision, is(0.6153846153846154));
    }

    @Test
    public void testAverageRecallMacro() throws Exception {
        double[] recall = {0.6000000238418579, 0.75, 0.5};

        final double macroRecall = target.averageRecallMacro(recall);
        assertThat(macroRecall, is(0.6166666746139526));
    }

    @Test
    public void testAverageRecallMicro() throws Exception {
        int[][] matrix = target.confusionMatrix(testYClass, testYPredictClass, maxIndexClass);
        int Idx = maxIndexClass;
        int[] TP = target.countingTruePositive(matrix, Idx);
        int[] FN = target.countingFalseNegative(matrix, Idx);

        final double microRecall = target.averageRecallMicro(TP, FN);
        assertThat(microRecall, is(0.6153846153846154));
    }

    @Test
    public void testAverageSpecificity() throws Exception {
        double[] specificity = {1.0, 0.6666666865348816, 0.7777777910232544};

        final double averageSpecificity = target.averageSpecificity(specificity);
        assertThat(averageSpecificity, is(0.8148148059844971));
    }

}