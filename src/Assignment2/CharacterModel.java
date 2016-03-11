package Assignment2;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import LanguageModel.*;

/**
 * Class performs the testing and training on the tweet set
 */
public class CharacterModel {

    private int size;
    private HashMap<String, LanguageModel> lms;
    private File trainingFile;
    private File testingFile;
    private String base;
    private String lmBase;
    private double delta;

    /**
     * Constructor - set up language models, their size and delta and input files
     * @param trainingFile
     * @param testingFile
     * @param size
     * @param delta
     * @throws Exception
     */
    public CharacterModel(File trainingFile, File testingFile, int size, Double delta) {
        this.size = size;
        this.trainingFile = trainingFile;
        this.testingFile = testingFile;
        // We get the base directory for future saving
        String p = testingFile.getAbsolutePath();
        this.base = p.substring(0, p.lastIndexOf(File.separator) + 1);
        this.lmBase = this.base + "LMs" + File.separator;
        this.delta = delta;

        // Our language models will be stored in a map of language -> language model
        this.lms = new HashMap<>();
        this.lms.put(Language.Basque.getVal(), new LanguageModel(size, Language.Basque));
        this.lms.put(Language.Catalan.getVal(), new LanguageModel(size, Language.Catalan));
        this.lms.put(Language.Galician.getVal(), new LanguageModel(size, Language.Galician));
        this.lms.put(Language.Spanish.getVal(), new LanguageModel(size, Language.Spanish));
        this.lms.put(Language.English.getVal(), new LanguageModel(size, Language.English));
        this.lms.put(Language.Protugese.getVal(), new LanguageModel(size, Language.Protugese));
    }

    /**
     * Train the data on the training file
     * @throws Exception
     */
    public void train() throws IOException, InconsistentNgramSizeException {

        // Get file
        FileInputStream fstream = new FileInputStream(trainingFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));

        // Read file 1 line at a time
        String strLine;
        while ((strLine = br.readLine()) != null)   {
            // We assume the text is tab delimited
            String[] tabs = strLine.split("\t");
            // We ignore any line that is erroneous
            // This could be the line doesn't format well
            // Or the line has been split by an unrecognized \n
            if(tabs.length < 3) {
                continue;
            }
            // Get the tweet substance
            String text = "";
            for(int i=3;i<tabs.length;i++) {
                text += tabs[i];
            }

            // Get the tweet language

            // We are not using the Language enum because the tweets are not uniformly
            // 2 character languages ie und, es+pt, es/pt..

            String language = tabs[2];
            // Get our model and add this value to the model
            LanguageModel lm = lms.get(language);
            if(lm != null) {
                addToLM(lm, text);
            } else {
                // We ignore - this will mostly be 'und' and tweets
                // in multiple languages
            }
        }
        br.close();

        // Calculate the probabilities with the dela
        // Save the models to a file
        for(LanguageModel lm : lms.values()) {
            lm.refreshProbabilities(delta);
            lm.save(this.lmBase, delta);
        }
    }


    /**
     * Add a tweet to a model
     *
     * @param lm Language Model to add the tweet to
     * @param line String containing the tweet text
     * @throws Exception
     */
    private void addToLM(LanguageModel lm, String line) throws InconsistentNgramSizeException {
        NGram tmp;
        // 'i' will hold the character code point - NOT the character
        for(int i = 0; i<line.length();) {
            // Build a new NGram
            tmp = new NGram();
            int k = 0;
            boolean eof = false;
            // For the size of the model, keep getting 'characters'
            for(int j=0; j<size;) {
                // Determine if we have reached too far and are out of the line length
                if((i+k) > line.length() -1) {
                    eof = true;
                    break;
                }
                // Get the 'character'
                int codePoint = line.codePointAt(i + k);
                int[] arr = new int[] {codePoint};
                // Add it to the NGram and move on
                tmp.add(new String(arr, 0, 1));
                k += Character.charCount(codePoint);
                j += 1;
            }
            // If we are at end of file we are done
            if(eof) break;
            // Add it to the language model
            lm.add(tmp);

            // Get the next 'character'
            int codePoint = line.codePointAt(i);
            i += Character.charCount(codePoint);
        }
    }


    /**
     * Test the data using the testing tweets
     * @return Probability of the overal performance of the assignment
     * @throws Exception
     */
    public Probability test() throws IOException, InconsistentNgramSizeException {

        // Set up the file
        FileInputStream fstream = new FileInputStream(testingFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));


        // We have two results
        // A confusion matrix - saved in analysis.txt
        // A results data - saved in reulsts.txt
        ArrayList<Result> results = new ArrayList<>();
        ConfusionMatrix matrix = new ConfusionMatrix(lms);


        // For each line in the tweet training file
        String strLine;
        while ((strLine = br.readLine()) != null) {
            // Assume files are tab delimited
            String[] tabs = strLine.split("\t");
            // Skip any erroneous line
            // This can be from a line not containing the right number of fields
            // Or a badly parsed new line \n
            if (tabs.length < 3) {
                continue;
            }
            // Get the tweet substance
            String text = "";
            for (int i = 3; i < tabs.length; i++) {
                text += tabs[i];
            }
            // If tweet is smaller than our NGram model - we can not really do anything
            if(text.length() < size) {
                continue;
            }

            // We are not using the Language enum because the tweets are not uniformly
            // 2 character languages ie und, es+pt, es/pt..

            // Get the langauge and probable language as defined by our language models
            String language = tabs[2];
            Result result = getLanguage(text);
            result.setTweetNumber(tabs[0]);
            result.setActualLanguage(language);
            String probLanguage = result.getProbLanguage();


            // Add to results.txt
            results.add(result);
            // Add to analysis.txt
            matrix.add(probLanguage, language);
        }

        // Save results.txt file
        saveResults(results);

        // Save analysis.txt file
        matrix.save(this.base, size, lms);
        Probability overalAccuracy = matrix.getOveralAccuracy();
        br.close();
        fstream.close();

        return overalAccuracy;
    }

    /**
     * Save results.txt
     * @param results Map of tweet number -> most probable language
     * @throws Exception
     */
    private void saveResults(ArrayList<Result> results) throws FileNotFoundException, UnsupportedEncodingException {
        // Results txt
        String path = this.base + "results-" + size + "gram.txt";
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        writer.println("Tweet Number\tProb Language\tCertainty\tActual Language");
        for(Result res: results) {
            writer.println(res);
        }

        writer.close();
    }

    /**
     * Get the most probable language based on our language models
     * @param text String text to be analysed
     * @param certain Proabibility to be filled in with how certain we are of the chosen language
     * @return String reprenting the language
     * @throws Exception
     */
    private Result getLanguage(String text) throws InconsistentNgramSizeException {
        // Set up default winner with 0 probability and null
        Probability winner = new Probability(0);
        LanguageModel winLM = null;
        Probability prob;
        // Get the probability from each language model
        // If greater than current winner, set current winner to that model
        for(LanguageModel lm: lms.values()) {
            prob = getProbability(text, lm);
            if(prob == null) continue;
            if(prob.compareTo(winner) > 0) {
                winner = prob;
                winLM = lm;
            }
        }
        if(winLM != null) {
            return new Result(winLM.getLang(), winner);
        } else {
            return new Result(null, winner);
        }
    }




    /**
     *
     * Get the Probability of a language model for predicting a text
     * @param text String to retreive probability of
     * @param lm Language Model which will be used to get the probability
     * @return Probability of the given text from this language model
     * @throws Exception
     */
    private Probability getProbability(String text, LanguageModel lm) throws InconsistentNgramSizeException {
        NGram tmp;
        Probability total = null;
        for(int i = 0; i<text.length();) {
            // Build a new NGram
            tmp = new NGram();
            int k = 0;
            boolean eof = false;
            // For the size of the model, keep getting 'characters'
            for(int j=0; j<size;) {
                // Determine if we have reached too far and are out of the line length
                if((i+k) > text.length() -1) {
                    eof = true;
                    break;
                }
                // Get the 'character'
                int codePoint = text.codePointAt(i + k);
                int[] arr = new int[] {codePoint};
                // Add it to the NGram and move on
                tmp.add(new String(arr, 0, 1));
                k += Character.charCount(codePoint);
                j += 1;
            }
            // If we are at end of file we are done
            if(eof) break;

            // Get the probability of this NGram
            Probability res = lm.get(tmp, delta).getProb();

            // Add the probability to the running total by taking the product
            if(total == null) {
                total = res;
            } else {
                total = Probability.multiply(total, res);
            }

            // Get the next 'character'
            int codePoint = text.codePointAt(i);
            i += Character.charCount(codePoint);
        }

        return total;
    }

}



