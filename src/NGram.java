import java.util.ArrayList;

/**
 * Simple class that makes use of the ArrayList
 */
public class NGram extends ArrayList<String> {

    public NGram(String words) {
        for(String word: words.split(" ")) {
            add(word);
        }
    }

    public NGram(NGram ngram) {
        super(ngram);
    }

    public NGram() {
        super();
    }


}
