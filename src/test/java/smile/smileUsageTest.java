package smile;

import static org.junit.Assert.*;

public class smileUsageTest {
    @org.junit.Test
    public void loadData() throws Exception {
        smileUsage sm = new smileUsage();
        sm.loadData(new java.io.File("data/weather.nominal.arff"),4);
    }

}