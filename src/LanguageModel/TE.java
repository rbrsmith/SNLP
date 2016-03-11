package LanguageModel;

/**
 * Table Entry Class
 * This class is at the edge of our langauge model
 * It holds the probability and count metrix used at the model edge
 */
public class TE {

    private Probability prob;
    private double count;

    public TE(Probability prob, double count) {
        this.prob = prob;
        this.count = count;
    }

    /**
     * Default constructor
     * We've seen this NGram for the first time
     */
    public TE() {
        this.prob = new Probability(0);
        this.count = 1;
    }

    public void setProb(Probability prob) {
        this.prob = prob;
    }

    public void setProb(double prob) {
        this.prob = new Probability(prob);
    }

    public void addCount() {
        this.count += 1;
    }

    public double getCount() {
        return this.count;
    }

    public Probability getProb(){
        return this.prob;
    }

    public String toString() {
        return "{"+getCount()+","+getProb()+"}";
    }
}
