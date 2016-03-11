package Assignment2;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import LanguageModel.*;

/**
 * This class largely wraps a 2D HashMap
 * It is placed in its own class to keep Character Model cleaner
 */
public class ConfusionMatrix {


    private HashMap<String, HashMap<String, Integer>> matrix = new HashMap<>();

    /**
     * Constructor - Initiate matrix rows and columns
     * One row / column for each lanaguage and one for 'other'
     * @param lms
     */
    public ConfusionMatrix(HashMap<String, LanguageModel> lms) {
        for(String language : lms.keySet()) {
            HashMap<String, Integer> languages = new HashMap<>();
            for(String l : lms.keySet()) {
                languages.put(l, 0);
            }
            languages.put("ot", 0);
            matrix.put(language, languages);
        }
        HashMap<String, Integer> languages = new HashMap<>();
        for(String l : lms.keySet()) {
            languages.put(l, 0);
        }
        languages.put("ot", 0);
        matrix.put("ot", languages);
    }

    /**
     *
     * Add an entry to the matrix
     *
     * @param probLanguage String language our model beleive it is
     * @param language String language we know it is
     */
    public void add(String probLanguage, String language) {
        // Matrix Results
        if (probLanguage != null) {
            HashMap<String, Integer> tmp = matrix.get(probLanguage);
            if (language.length() == 2) {
                tmp.put(language, tmp.get(language) + 1);
            } else {
                // Language is not size 2 - so it could be multiple languages
                // If our guessed langauged is included in the original langauge
                // We add it to the correct column - other wise add it to other
                if(language.contains(probLanguage)) {
                    tmp.put(probLanguage, tmp.get(probLanguage) + 1);
                } else {
                    tmp.put("ot", tmp.get("ot") + 1);
                }
            }
        } else {
            HashMap<String, Integer> tmp = matrix.get("ot");
            if (language.length() == 2) {
                // This never happens - ie we always have a percent value for a known single language
                tmp.put(language, tmp.get(language) + 1);
            } else {
                tmp.put("ot", tmp.get("ot") + 1);
            }
        }
    }

    /**
     *
     * Save the analysis file
     * Includes language accuracies
     * Includes overall accuracy
     * Includes confusion matrix
     *
     * @param dir String of the directory to save the file to
     * @param size int size of the model to be included in file name
     * @param lms Map containing language -> language model
     * @throws Exception
     */
    public void save(String dir, int size, HashMap<String, LanguageModel> lms) throws Exception {

        // Set up the file
        String path = dir + "analysis-" + size + "gram.txt";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        // Save the accuracies first
        saveAccuracies(lms, writer);

        // Now save the confusion matrix

        // Save header first
        writer.print("\t\t");
        for(HashMap<String, Integer> map: matrix.values()) {
            for(String predictLangauge: map.keySet()) {
                writer.print(predictLangauge + "\t\t");
            }
            break;
        }

        writer.println();

        // Now each entry
        for(Map.Entry<String, HashMap<String, Integer>> map : matrix.entrySet() ) {
            String actualLanguage = map.getKey();
            HashMap<String, Integer> subMatrix = map.getValue();
            writer.print(actualLanguage + "\t\t");
            for(Map.Entry<String, Integer> subMap: subMatrix.entrySet()) {
                Integer amountGuessed = subMap.getValue();
                writer.print(amountGuessed + "\t\t");
            }
            writer.println();
        }
        writer.close();
    }

    /**
     * Save the langauge and overal accuracies to a file
     *
     * @param lms Map of language -> Langauge Model
     * @param writer Printwriter object used to print the results
     */
    private void saveAccuracies(HashMap<String, LanguageModel> lms, PrintWriter writer ) {
        // We need to count total and correct
        HashMap<String, Integer> totalCount = new HashMap<>();
        HashMap<String, Integer> correctCount = new HashMap<>();
        for(String language: lms.keySet()) {
            totalCount.put(language, 0);
            correctCount.put(language, 0);
        }

        // We can use the matrix to get these values for each language
        for(String language: lms.keySet()) {
            HashMap<String, Integer> submatrix = matrix.get(language);
            for(String l : lms.keySet()) {
                correctCount.put(language, submatrix.get(language));
                totalCount.put(l, totalCount.get(l) + submatrix.get(l));
            }
        }

        for(String language: lms.keySet()) {
            writer.println(language + " Accuracy: " +
                    new Double(correctCount.get(language)) / new Double(totalCount.get(language)) *100 + "%" );
        }

        // Now we do it for total accuracy
        Integer totalCorrect = 0;
        Integer totalTotal = 0;
        for(Integer i: correctCount.values()) {
            totalCorrect += i;
        }
        for(Integer i: totalCount.values()) {
            totalTotal += i;
        }
        // Add in the ones we didn't find
        for(HashMap<String, Integer> map: matrix.values()) {
            totalTotal += map.get("ot");
        }
        totalCorrect += matrix.get("ot").get("ot");


        writer.println("Overall Accuracy: " + new Double(totalCorrect) / new Double(totalTotal) * 100 + "%");
    }

}
