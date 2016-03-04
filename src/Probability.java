import java.text.DecimalFormat;

public class Probability {

    private double prob;

    public Probability(double prob) {
        this.prob = prob;
    }

    public String toString() {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return df.format(prob) + "%";
    }

    static Probability multiply(Probability one, Probability two) {
        return new Probability((one.prob)/100 * (two.prob/100) * 100);
    }



}
