package smile;

import org.junit.Test;

public class smileUsageTest {

    @Test
    public void loadData() throws Exception {
        SmileUsage sm = new SmileUsage();
        sm.loadData(this.getClass().getResourceAsStream("/sample.arff"), 337);

        //TODO: add asserts
    }

}