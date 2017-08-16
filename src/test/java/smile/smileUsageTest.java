package smile;

public class smileUsageTest {
    @org.junit.Test
    public void loadData() throws Exception {
        smileUsage sm = new smileUsage();
        sm.loadData(new java.io.File("data/model7/Model7_P21_P31_P106_P361_P373_P569_P625_P646_P910_RI334.arff"),4);
    }

}