import LanguageModel.NGram;
import org.junit.Test;

import static org.junit.Assert.*;

public class NGramTest {


    @Test
    public void testNgram() throws Exception {
        NGram unigram = new NGram("t");
        NGram bigram = new NGram("t h");
        NGram trigram = new NGram("t h e");
        assertTrue(unigram.size() == 1);
        assertTrue(bigram.size() == 2);
        assertTrue(trigram.size() == 3);

        NGram trigram2 = new NGram(trigram);
        assertTrue(trigram.size() == trigram2.size());
    }

}