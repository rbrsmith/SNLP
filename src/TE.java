public class TE {

    private Probability prob;
    private int count;

    public TE(Probability prob, int count) {
        this.prob = prob;
        this.count = count;
    }

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

    public int getCount() {
        return this.count;
    }

    public Probability getProb(){
        return this.prob;
    }

    public String toString() {
        return "{"+getCount()+","+getProb()+"}";
    }
}
