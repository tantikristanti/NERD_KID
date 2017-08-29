package smile;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class smileUsageTest {
    SmileUsage target;

    @Before
    public void setSm() throws Exception {
        target = new SmileUsage();
    }

    @Test
    public void testLoadData() throws Exception {
        target.loadData(this.getClass().getResourceAsStream("/sample.arff"), 337);
    }
}