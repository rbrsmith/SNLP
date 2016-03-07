import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Hashmap that holds the words and their counts and probabilities
 */
public class LanguageModel {

    private HashMap data;
    // Size of model 1 = unigram
    private int size;
    // For values that have not been encountered
    private final TE empty = new TE(new Probability(0), 0);
    private String name;
    private String language;


    public LanguageModel(int size, String name, String lang) {
        this.name = name;
        this.language = lang;
        consturct(size);
    }


    public LanguageModel(int size) {
        consturct(size);
    }


    /**
     * Build the map
     * If we are a unigram then its a simple hashmap
     * Otherwise build the model as a hashmap that represents another model
     * @param size
     */
    public void consturct(int size) {
        this.size = size;
        if(size == 1) {
            data = new HashMap<String, TE>();
        } else {
            data = new HashMap<String, LanguageModel>();
        }

    }

    public String getLang() {
        return language;
    }

    public String getName() {
        return name;
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
     * @throws InconsistentNgramSizeException thrown if current ngram size doesn't match NGram words
     */
    public void add(NGram words) throws InconsistentNgramSizeException {
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
                LanguageModel d = (LanguageModel) data.get(key);
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
                LanguageModel d = new LanguageModel(size-1);
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
    public TE get(String words, double delta) throws InconsistentNgramSizeException {
        ArrayList<String> ws = new ArrayList<>(Arrays.asList(words.split(" ")));
        return get(ws, delta);
    }

    public TE get(String words) throws InconsistentNgramSizeException {
        ArrayList<String> ws = new ArrayList<>(Arrays.asList(words.split(" ")));
        return get(ws, 0);
    }

    public TE get(NGram words, double delta) throws InconsistentNgramSizeException {
        ArrayList<String> ws = new ArrayList<>(words);
        return get(ws, delta);

    }

    public TE get(NGram words) throws InconsistentNgramSizeException {
        ArrayList<String> ws = new ArrayList<>(words);
        return get(ws, 0);

    }

    /**
     *
     * @param words ArrayList of words of TE we want to get
     * @return TE holding count and probability
     * @throws InconsistentNgramSizeException thrown if number of words doesn't match current model size
     */
    private TE get(ArrayList<String> words, double delta) throws InconsistentNgramSizeException{
        ArrayList<String> ws = new ArrayList<>(words);

        if(ws.size() != size) {
            throw new InconsistentNgramSizeException(size, ws.size());
        }

        String key = ws.get(0);
        // Unigram
        if(size == 1) {
            if(data.get(key) == null) {
                // Return 0 count, 0 probability
                return new TE(new Probability(delta/getVocabulary()), delta);
            } else {
                return (TE) data.get(key);
            }
        } else {
            // Bigram or greater
            // Remove the current key
            ws.remove(0);
            // Get the model
            LanguageModel d = (LanguageModel) data.get(key);
            if(d == null) {
                // Return 0 count, 0 probability
                return new TE(new Probability(delta/getVocabulary()), delta);
            }

            return d.get(ws, delta);
        }
    }

    /**
     * Re-Calculate all the probabilities in the TE classes at the edge of the table
     * Called when counts have been updated
     */
    public void refreshProbabilities(double delta) {
        if(size == 1) {
            // Unigram
            double totalCount = 0;
            // Get total count
            for(Object obj : data.values()){
                TE entry = (TE) obj;
                totalCount += entry.getCount();
            }
            //TODO cache getVocabulary
            double vocab = getVocabulary();
            totalCount += delta*vocab;


            // Calculate probabilities
            for(Object obj : data.values()){
                TE entry = (TE) obj;
                entry.setProb((entry.getCount() + delta) / totalCount * 100);
            }
        } else {
            // Recursively update probabilities for each
            // inner model
            for(Object obj : data.values()) {
                LanguageModel d = (LanguageModel) obj;
                if(d == null) continue;
                d.refreshProbabilities(delta);
            }
        }
    }

    public void refreshProbabilities() {
        refreshProbabilities(0);
    }

    public double getVocabulary() {
        double count = 0;
        for(Object obj: data.values()) {
            count += 1;
        }
        return count;
    }
}
