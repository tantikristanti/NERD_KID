package smile;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ModelEvaluationTest {

    ModelEvaluation target;

    @Before
    public void setUp() throws Exception {
        target = new ModelEvaluation();
    }


    @Test
    public void testAllPrecisionMicro() throws Exception {
        int[] tp = {3, 3, 2};
        int[] fp = {0, 3, 2};
        final double microPrecision = target.allPrecisionMicro(tp, fp);

        assertThat(microPrecision, is(0.6153846153846154));
    }

    @Test
    public void testAllPrecisioMacro() throws Exception {
        double[] precision = {1.0, 0.5, 0.5};

        final double macroPrecision = target.allPrecisionMacro(precision);
        assertThat(macroPrecision, is(0.6666666865348816));
    }

}