import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TETest {

    TE te1;
    TE te0;

    @Before
    public void setUp() throws Exception {
        te0 = new TE();
        te1 = new TE(new Probability(45) ,45);
    }

    @After
    public void tearDown() throws Exception {
        te1 = null;
    }

    @Test
    public void testSetProb() throws Exception {
        te1.setProb(new Probability(100));
        assertTrue(te1.getProb().equals(new Probability(100)));
        te1.setProb(22);
        assertTrue(te1.getProb().equals(new Probability(22)));
        te1.setProb(-1);
        assertTrue(te1.getProb().equals(new Probability(0)));
        te1.setProb(110);
        assertTrue(te1.getProb().equals(new Probability(100)));
    }

    @Test
    public void testConst() throws Exception {
        assertTrue(te0.getProb().equals(new Probability(0)));
        assertTrue(te0.getCount() == 1);

    }

    @Test
    public void testAddCount() throws Exception {
        te0.addCount();
        assertTrue(te0.getCount() == 2);
        for(int i = 0; i < 100; i++){
            te1.addCount();
        }
        assertTrue(te1.getCount() == 145);

    }
}