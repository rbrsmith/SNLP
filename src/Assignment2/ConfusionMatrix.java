package Assignment2;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import LanguageModel.*;

/**
 * This class largely wraps a 2D HashMap
 * It is placed in its own class to keep Character Model cleaner
 */
public class ConfusionMatrix {


    private HashMap<String, HashMap<String, Integer>> matrix = new HashMap<>();
    private Probability overalAccuracy;

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
        overalAccuracy = null;
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
    public void save(String dir, int size, HashMap<String, LanguageModel> lms) throws FileNotFoundException, UnsupportedEncodingException {

        // Set up the file
        String path = dir + "analysis-" + size + "gram.txt";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        // Save the accuracies first
        HashMap<String, Integer> total = saveAccuracies(lms, writer);

        // Now save the confusion matrix

        writer.println();
        writer.println("Confusion Matrix Counts (P - Predicted, A - Actual)");
        // Save header first
        writer.printf("%-15s", " ");
        for(HashMap<String, Integer> map: matrix.values()) {
            for(String predictLangauge: map.keySet()) {
                writer.printf("%-15s", "P: " + predictLangauge);
            }
            break;
        }

        writer.println();

        // Now each entry
        for(Map.Entry<String, HashMap<String, Integer>> map : matrix.entrySet() ) {
            String actualLanguage = map.getKey();
            HashMap<String, Integer> subMatrix = map.getValue();
            writer.printf("%-15s", "A: " +actualLanguage);
            for(Map.Entry<String, Integer> subMap: subMatrix.entrySet()) {
                Integer amountGuessed = subMap.getValue();
              //  writer.print(amountGuessed + "\t\t\t");
                writer.printf("%-15s", amountGuessed.toString());
            }
            writer.println();
        }



        writer.println();
        writer.println("Confusion Matrix Percents (P - Predicted, A - Actual)");
        // Save header first
        writer.printf("%-15s", " ");
        for(HashMap<String, Integer> map: matrix.values()) {
            for(String predictLangauge: map.keySet()) {
                writer.printf("%-15s", "P: " + predictLangauge);
            }
            break;
        }

        writer.println();

        // Now each entry
        for(Map.Entry<String, HashMap<String, Integer>> map : matrix.entrySet() ) {
            String actualLanguage = map.getKey();
            HashMap<String, Integer> subMatrix = map.getValue();
            writer.printf("%-15s", "A: " +actualLanguage);
            for(Map.Entry<String, Integer> subMap: subMatrix.entrySet()) {
                Integer amountGuessed = subMap.getValue();
                Probability percentGuessed = new Probability(
                        (new Double(amountGuessed) / new Double(total.get(actualLanguage))*100));
                writer.printf("%-15s", percentGuessed.toString());
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
     * @return Map Language -> total tweets
     */
    private HashMap<String, Integer>  saveAccuracies(HashMap<String, LanguageModel> lms, PrintWriter writer ) {
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

        writer.println("Per Language Accuracy:");
        for(String language: lms.keySet()) {
            writer.println(language + " Accuracy: " +
                    new Probability(new Double(correctCount.get(language))/new Double(totalCount.get(language)) *100));
        }
        writer.println();

        // Now we do it for total accuracy
        Integer totalCorrect = 0;
        Integer totalTotal = 0;
        for(Integer i: correctCount.values()) {
            totalCorrect += i;
        }
        for(Integer i: totalCount.values()) {
            totalTotal += i;
        }
        int preOtherTotal = totalTotal;
        int preOtherCorrect = totalCorrect;
        // Add in the ones we didn't find
        for(HashMap<String, Integer> map: matrix.values()) {
            totalTotal += map.get("ot");
        }
        totalCorrect += matrix.get("ot").get("ot");

        Probability totalOtherAccuracy = new Probability(new Double(totalCorrect)/new Double(totalTotal)*100);
        Probability totalAccuracy = new Probability(new Double(preOtherCorrect)/new Double(preOtherTotal)*100);
        this.overalAccuracy = totalAccuracy;

        writer.println("Overall Accuracy (with 'other'): " + totalOtherAccuracy);
        writer.println("Overall Accuracy (without 'other'): " + totalAccuracy);

        // Add other to total count
        totalCount.put("ot", totalTotal - preOtherTotal);
        return totalCount;
    }

    public Probability getOveralAccuracy() {
        return overalAccuracy;
    }
}
