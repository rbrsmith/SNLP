public class InconsistentNgramSizeException extends Exception {

    public InconsistentNgramSizeException(int expected, int received) {
        super("NGram sizes do not match. Expected n " + expected + " but found " + received);
    }
}
