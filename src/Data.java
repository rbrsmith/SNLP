import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Hashmap that holds the words and their counts and probabilities
 */
public class Data {

    private HashMap data;
    // Size of model 1 = unigram
    private int size;
    // For values that have not been encountered
    private final TE empty = new TE(new Probability(0), 0);

    /**
     * Build the map
     * If we are a unigram then its a simple hashmap
     * Otherwise build the model as a hashmap that represents another model
     * @param size
     */
    public Data(int size) {
        this.size = size;
        if(size == 1) {
            data = new HashMap<String, TE>();
        } else {
            data = new HashMap<String, Data>();
        }
    }

    /**
     *
     * @param words String delimetered by space that we want added to the model
     * @throws InconsistentNgramSizeException
     */
    public void add(String words) throws InconsistentNgramSizeException {
        this.add(new NGram(words), new TE());
    }


    /**
     *
     * @param words NGram of words to be added to the model
     * @param entry TE holding count and probability to be added
     * @throws InconsistentNgramSizeException thrown if current ngram size doesn't match NGram words
     */
    private void add(NGram words, TE entry) throws InconsistentNgramSizeException {
        NGram ws = new NGram(words);

        // The NGram passed is not the same size of the model
        if(words.size() != size) {
            throw new InconsistentNgramSizeException(size, words.size());
        }


        String key = words.get(0);

        if(data.get(key) != null) {
            // Unigram - add to basic hashmap
            if(size == 1) {
                TE cur = (TE) data.get(key);
                // NGram has been seen again, increase count
                cur.addCount();
            } else {
                // Bigram or greater - get model at the key and recursively add to it
                Data d = (Data) data.get(key);
                ws.remove(0);
                d.add(ws, entry);
                data.put(key, d);
            }
        } else {
            // Unigram - put count to 1
            if(size == 1) {
                data.put(key, entry);
            } else {
                // Bigram or greater - create new model at this key
                Data d = new Data(size-1);
                ws.remove(0);
                d.add(ws, entry);
                data.put(key, d);
            }
        }
    }

    /**
     *
     * @param words String space delimited words of TE we want to get
     * @return TE holding count and probability
     * @throws InconsistentNgramSizeException thrown if number of words doesn't match current model size
     */
    public TE get(String words) throws InconsistentNgramSizeException {
        ArrayList<String> ws = new ArrayList<>(Arrays.asList(words.split(" ")));
        return get(ws);
    }

    /**
     *
     * @param words ArrayList of words of TE we want to get
     * @return TE holding count and probability
     * @throws InconsistentNgramSizeException thrown if number of words doesn't match current model size
     */
    private TE get(ArrayList<String> words) throws InconsistentNgramSizeException{
        ArrayList<String> ws = new ArrayList<>(words);

        if(ws.size() != size) {
            throw new InconsistentNgramSizeException(size, ws.size());
        }

        String key = ws.get(0);
        // Unigram
        if(size == 1) {
            if(data.get(key) == null) {
                // Return 0 count, 0 probability
                return empty;
            } else {
                return (TE) data.get(key);
            }
        } else {
            // Bigram or greater
            // Remove the current key
            ws.remove(0);
            // Get the model
            Data d = (Data) data.get(key);
            if(d == null) {
                // Return 0 count, 0 probability
                return empty;
            }

            return d.get(ws);
        }
    }

    /**
     * Re-Calculate all the probabilities in the TE classes at the edge of the table
     * Called when counts have been updated
     */
    public void refreshProbabilities() {
        if(size == 1) {
            // Unigram
            int totalCount = 0;
            // Get total count
            for(Object obj : data.values()){
                TE entry = (TE) obj;
                totalCount += entry.getCount();
            }

            // Calculate probabilities
            for(Object obj : data.values()){
                TE entry = (TE) obj;
                entry.setProb(entry.getCount() / (double) totalCount * 100);
            }
        } else {
            // Recursively update probabilities for each
            // inner model
            for(Object obj : data.values()) {
                Data d = (Data) obj;
                if(d == null) continue;
                d.refreshProbabilities();
            }
        }
    }
}
