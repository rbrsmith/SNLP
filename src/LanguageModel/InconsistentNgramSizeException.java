package LanguageModel;

public class InconsistentNgramSizeException extends Exception {

    /**
     * Thrown when we are using one size NGram and we see another size NGram
     * @param expected
     * @param received
     */
    public InconsistentNgramSizeException(int expected, int received) {
        super("LanguageModel.NGram sizes do not match. Expected n " + expected + " but found " + received);
    }
}
