package LanguageModel;

import java.text.DecimalFormat;

/**
 * Simple class that holds probabilities
 */
public class Probability implements Comparable {

    private double prob;
    final private int nbDecimals = 2;

    /**
     * Default constructor
     * Make the probability respect bounds
     * @param prob double value of probability
     */
    public Probability(double prob) {
        if (prob < 0) {
            System.err.println("Setting a negative probability.  Changing to 0%");
            this.prob = 0;
        } else if(prob > 100) {
            System.err.println("Setting a greater than 100%.  Changing to 100%");
            this.prob = 100;
        } else {
            this.prob = prob;
        }
    }

    public String toString() {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(nbDecimals);
        return df.format(prob) + "%";
    }

    /**
     *
     * @return double prob in scientific notation
     */
    public double getRaw() {
        return prob;
    }

    /**
     * Multiply two probabilities
     * @param one LanguageModel.Probability
     * @param two LanguageModel.Probability
     * @return
     */
    static public Probability multiply(Probability one, Probability two) {
        return new Probability((one.prob)/100 * (two.prob/100) * 100);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Probability)) {
            return false;
        }
        if(obj == this) {
            return true;
        }

        Probability p = (Probability) obj;
        if(p.prob == prob) {
            return true;
        } else {
            return false;
        }

    }



    @Override
    public int compareTo(Object o) {
        Probability other = (Probability) o;
        Double mine = new Double(prob);
        Double theirs = new Double(other.prob);
        return mine.compareTo(theirs);
    }
}
