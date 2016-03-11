package LanguageModel;

import Assignment2.Language;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Hashmap that holds the words and their counts and probabilities
 */
public class LanguageModel {

    private HashMap data;
    private int size;
    private Language name;


    /**
     *
     * @param size int NGram model size
     * @param name String name given to the model
     */
    public LanguageModel(int size, Language name) {
        this.name = name;
        consturct(size);
    }


    /**
     * More general constructor
     * @param size int size of model
     */
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
        return name.getVal();
    }

    public Language getName() {
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
     * @param words LanguageModel.NGram of words to be added to the model
     * @throws InconsistentNgramSizeException thrown if current ngram size doesn't match LanguageModel.NGram words
     */
    public void add(NGram words) throws InconsistentNgramSizeException {
        this.add(new NGram(words), new TE());
    }

    /**
     *
     * @param words LanguageModel.NGram of words to be added to the model
     * @param entry LanguageModel.TE holding count and probability to be added
     * @throws InconsistentNgramSizeException thrown if current ngram size doesn't match LanguageModel.NGram words
     */
    private void add(NGram words, TE entry) throws InconsistentNgramSizeException {
        NGram ws = new NGram(words);

        // The LanguageModel.NGram passed is not the same size of the model
        if(words.size() != size) {
            throw new InconsistentNgramSizeException(size, words.size());
        }


        String key = words.get(0);

        if(data.get(key) != null) {
            // Unigram - add to basic hashmap
            if(size == 1) {
                TE cur = (TE) data.get(key);
                // LanguageModel.NGram has been seen again, increase count
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
     * @param words String space delimited words of LanguageModel.TE we want to get
     * @param delta double to be applied in getting values
     * @return TE holding count and probability
     * @throws InconsistentNgramSizeException thrown if number of words doesn't match current model size
     */
    public TE get(String words, double delta) throws InconsistentNgramSizeException {
        ArrayList<String> ws = new ArrayList<>(Arrays.asList(words.split(" ")));
        return get(ws, delta);
    }

    /**
     *
     * @param words String space delimited words of LanguageModel.TE we want to get
     * @return TE holding count and probability
     * @throws InconsistentNgramSizeException thrown if number of words doesn't match current model size
     */
    public TE get(String words) throws InconsistentNgramSizeException {
        ArrayList<String> ws = new ArrayList<>(Arrays.asList(words.split(" ")));
        return get(ws, 0);
    }

    /**
     *
     * @param words NGram of words of TE we want to get
     * @param delta double to be applied in getting values
     * @return TE holding count and probability
     * @throws InconsistentNgramSizeException thrown if number of words doesn't match current model size
     */
    public TE get(NGram words, double delta) throws InconsistentNgramSizeException {
        ArrayList<String> ws = new ArrayList<>(words);
        return get(ws, delta);

    }

    /**
     *
     * @param words NGram of words of TE we want to get
     * @return TE holding count and probability
     * @throws InconsistentNgramSizeException thrown if number of words doesn't match current model size
     */
    public TE get(NGram words) throws InconsistentNgramSizeException {
        ArrayList<String> ws = new ArrayList<>(words);
        return get(ws, 0);
    }

    /**
     *
     * @param words ArrayList of words of LanguageModel.TE we want to get
     * @return LanguageModel.TE holding count and probability
     * @throws InconsistentNgramSizeException thrown if number of words doesn't match current model size
     */
    private TE get(ArrayList<String> words, double delta) throws InconsistentNgramSizeException {
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
     * Re-Calculate all the probabilities in the LanguageModel.TE classes at the edge of the table
     * Called when counts have been updated
     * @param delta double to be applied to all probabilities
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

    /**
     * Re-Calculate all the probabilities in the LanguageModel.TE classes at the edge of the table
     * Called when counts have been updated
     */
    public void refreshProbabilities() {
        refreshProbabilities(0);
    }

    /**
     *
     * @return size of the model which represents total vocabulary |V|
     */
    public double getVocabulary() {
        double count = 0;
        for(Object obj: data.values()) {
            count += 1;
        }
        return count;
    }


    /**
     *
     * @param key String we want to get all the following NGrams and TE (count/prob) of
     * @return Map of all following String which key is the first part of an NGram and their TE values
     */
    private HashMap<String, TE> getAt(String key) {
        if(size == 1) {
            // We're at the smallest model
            // Add the single TE value to the hashmap and return
            return new HashMap<String, TE>() {{ put(key, (TE) data.get(key)); }};
        } else {
            // Build results map to be returned
            HashMap<String, TE> res = new HashMap<>();
            // Get the language model at this key
            LanguageModel d = (LanguageModel) data.get(key);
            // For each element in this model - get a hasmap of all their
            // values and add it to our running hashmap
            for(Object obj : d.data.keySet()) {
                String k = (String) obj;
                HashMap<String, TE> inner = d.getAt(k);
                for(Map.Entry<String, TE> entry : inner.entrySet()) {
                    String entryK = entry.getKey();
                    TE val = entry.getValue();
                    res.put(key + " " + entryK, val);
                }
            }
            return res;
        }
    }

    /**
     *
     * @return ArrayList of all the keys in this model
     */
    public ArrayList<String> getKeys() {
        ArrayList<String> tmp = new ArrayList<>(data.keySet());
        return tmp;
    }

    /**
     * Saves the model in a directory
     * Saves the 50 first NGrams and their delta and non-delta values
     *
     * @param dir String path to save this model in
     * @param delta Delta to be applied
     * @throws Exception
     */
    public void save(String dir, double delta) throws FileNotFoundException, UnsupportedEncodingException {

        // Set up file
        new File(dir).mkdir();
        String path = dir + getName() + "-" + size + "gramLM.txt";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        // Set no delta
        refreshProbabilities(0);
        // Get the NGrams recursively
        HashMap<String, String> output = new HashMap<>();
        for(String key: getKeys()) {
            HashMap<String, TE> grams = getAt(key);
            for(Map.Entry<String, TE> gram: grams.entrySet()){
                output.put(gram.getKey(), gram.getValue().getProb().toString());
            }
        }
        // Repeat, this time with delta applied
        refreshProbabilities(delta);
        for(String key: getKeys()) {
            HashMap<String, TE> grams = getAt(key);
            for(Map.Entry<String, TE> gram: grams.entrySet()) {
                String tmp = output.get(gram.getKey());
                output.put(gram.getKey(), tmp + "\t" + gram.getValue().getProb().toString());
            }
        }

        // Now do the output
        int l = 0;
        writer.println(size + " Gram\tUnSmooted\tSmoothed");
        for(Map.Entry<String, String> str: output.entrySet()) {
            if( l >= 50 ) break;
            l += 1;
            writer.println(str.getKey() + "\t" + str.getValue());
        }
        writer.close();
    }
}