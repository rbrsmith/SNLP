import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataTest {

    private Data unigram;
    private Data bigram;
    private Data trigram;

    @Before
    public void setUp() throws Exception {
        unigram = new Data(1);
        bigram = new Data(2);
        trigram = new Data(3);

    }

    @After
    public void tearDown() throws Exception {
        unigram=null;bigram=null;trigram=null;
    }

    @Test
    public void unigramTest() throws Exception {
        unigram.add("t");
        assertTrue(unigram.get("t").getCount() == 1);
        unigram.add("t");
        assertTrue(unigram.get("t").getCount() == 2);
        assertTrue(unigram.get("f").getCount() == 0);

        try {
            unigram.add("F T");
            fail();
        } catch(InconsistentNgramSizeException e) {}

        unigram.refreshProbabilities();
        assertTrue(unigram.get("t").getProb().equals(new Probability(100)));
        unigram.add("f");
        unigram.refreshProbabilities();
        Probability result = new Probability(new Double(2)/new Double(3) *100);
        assertTrue(unigram.get("t").getProb().equals(result));
        result = new Probability(new Double(1)/new Double(3)*100);
        assertTrue(unigram.get("f").getProb().equals(result));
    }

    @Test
    public void bigramTest() throws Exception {
        bigram.add("t h");
        assertTrue(bigram.get("t h").getCount() == 1);
        bigram.add("t h");
        assertTrue(bigram.get("t h").getCount() == 2);
        assertTrue(bigram.get("t f").getCount() == 0);
        assertTrue(bigram.get("f f").getCount() == 0);

        try {
            bigram.add("t");
            fail();
        } catch(InconsistentNgramSizeException e) {}
        try {
            bigram.add("t h e");
            fail();
        } catch (InconsistentNgramSizeException e) {}

        bigram.refreshProbabilities();
        assertTrue(bigram.get("t h").getProb().equals(new Probability(100)));
        bigram.add("t f");
        bigram.refreshProbabilities();
        Probability result = new Probability(new Double(2)/new Double(3) *100);
        assertTrue(bigram.get("t h").getProb().equals(result));
        result = new Probability(new Double(1)/new Double(3)*100);
        assertTrue(bigram.get("t f").getProb().equals(result));

    }

    @Test
    public void trigramTest() throws Exception {
        trigram.add("t h e");
        assertTrue(trigram.get("t h e").getCount() == 1);
        trigram.add("t h e");
        assertTrue(trigram.get("t h e").getCount() == 2);
        assertTrue(trigram.get("t f e").getCount() == 0);
        assertTrue(trigram.get("t h r").getCount() == 0);
        assertTrue(trigram.get("f h e").getCount() == 0);

        try {
            trigram.add("t");
            fail();
        } catch(InconsistentNgramSizeException e) {}
        try {
            trigram.add("t h ");
            fail();
        } catch (InconsistentNgramSizeException e) {}
        try {
            trigram.add("t h e y");
            fail();
        } catch (InconsistentNgramSizeException e) {}

        trigram.refreshProbabilities();
        assertTrue(trigram.get("t h e").getProb().equals(new Probability(100)));
        trigram.add("t h r");
        trigram.refreshProbabilities();
        Probability result = new Probability(new Double(2)/new Double(3) *100);
        assertTrue(trigram.get("t h e").getProb().equals(result));
        result = new Probability(new Double(1)/new Double(3)*100);
        assertTrue(trigram.get("t h r").getProb().equals(result));

    }

    @Test
    public void testRefreshProbabilities() throws Exception {

    }
}