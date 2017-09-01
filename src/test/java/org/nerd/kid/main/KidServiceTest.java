package org.nerd.kid.main;

import org.junit.Before;
import org.junit.Test;
import org.nerd.kid.main.KidService;

import static org.hamcrest.core.Is.is;

public class KidServiceTest {
    KidService target;

    @Before
    public void setSm() throws Exception {
        target = new KidService();
    }

    @Test
    public void testLoadData() throws Exception {
        target.loadData(this.getClass().getResourceAsStream("/sample.arff"), 337);
    }
}