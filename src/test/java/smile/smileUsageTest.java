package smile;

public class smileUsageTest {
    @org.junit.Test
    public void loadData() throws Exception {
        SmileUsage sm = new SmileUsage();
        sm.loadData(new java.io.File("data/model7/Model7_P17_P21_P31_P106_P159_P361_P373_P569_P625_P646_P910_RI337.arff"));
    }

}