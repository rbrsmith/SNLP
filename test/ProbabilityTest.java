import org.junit.Test;

import static org.junit.Assert.*;

public class ProbabilityTest {

    @Test
    public void testMultiply() throws Exception {
        Probability p1 = new Probability(101);
        Probability p2 = new Probability(-1);
        Probability p3 = new Probability(50);
        Probability p4 = new Probability(25);
        assertTrue(p1.equals(new Probability(100)));
        assertTrue(p2.equals(new Probability(0)));
        assertTrue(p3.equals(new Probability(50)));

        assertTrue(Probability.multiply(p1,p3).equals(new Probability(50)));
        assertTrue(Probability.multiply(p3,p4).equals(new Probability(12.5)));
        assertTrue(Probability.multiply(p1,p2).equals(new Probability(0)));
        assertTrue(Probability.multiply(p1, Probability.multiply(p1, p2)).equals(new Probability(0)));
    }
}