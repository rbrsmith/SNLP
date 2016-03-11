package Assignment2;

import LanguageModel.Probability;

/**
 * Class used to hold results saved in results.txt
 */
public class Result {

    String tweetNumber;
    String probLanguage;
    String actualLanguage;
    Probability certain;


    /**
     * Constructor
     * @param probLanguage String language we believe the result to be
     * @param certain Probability of our choice of langage
     */
    public Result(String probLanguage, Probability certain) {
        this.probLanguage = probLanguage;
        this.certain = certain;
    }

    /**
     * Print probability in scientific notation as it is going to be large
     * @return
     */
    public String toString() {
        return this.tweetNumber + "\t" + this.probLanguage + "\t" + this.certain.getRaw() + "\t"+actualLanguage;
    }

    public String getProbLanguage() {
        return probLanguage;
    }

    public void setTweetNumber(String tweetNumber) {
        this.tweetNumber = tweetNumber;
    }

    public void setActualLanguage(String actualLanguage) {
        this.actualLanguage = actualLanguage;
    }
}
