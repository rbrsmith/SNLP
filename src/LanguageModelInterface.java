import java.io.File;

public interface LanguageModelInterface {

    public void trainModel(int ngramSize, File trainingData);

    public void getProbability(File heldout);

}
