import java.io.*;
import java.util.ArrayList;
import java.util.Queue;

public class CharacterModel {

    int size;
    LanguageModel BasqueLM;
    LanguageModel CatalanLM;
    LanguageModel GalicianLM;
    LanguageModel SpanishLM;
    LanguageModel EnglishLM;
    LanguageModel PortugeseLM;
    File trainingFile;
    File testingFile;

    public CharacterModel(int size, File trainingFile, File testingFile) {
        this.size = size;
        this.trainingFile = trainingFile;
        this.testingFile = testingFile;
        BasqueLM = new LanguageModel(size);
        CatalanLM = new LanguageModel(size);
        GalicianLM = new LanguageModel(size);
        SpanishLM = new LanguageModel(size);
        EnglishLM = new LanguageModel(size);
        PortugeseLM = new LanguageModel(size);

    }

    public void train() throws Exception {
        //TODO strip out /n's


        FileInputStream fstream = new FileInputStream(trainingFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        String strLine;
        while ((strLine = br.readLine()) != null)   {
            // Print the content on the console
            String[] tabs = strLine.split("\t");
            if(tabs.length < 3) {
                continue;
            }
            String text = "";
            for(int i=3;i<tabs.length;i++) {
                text += tabs[i];
            }
            String language = tabs[2];
            if(language.equals("eu")) {
                addToLM(BasqueLM, text);
            } else if(language.equals("ca")){
                addToLM(CatalanLM, text);
            } else if(language.equals("gl")){
                addToLM(GalicianLM, text);
            } else if(language.equals("es")){
                addToLM(SpanishLM, text);
            } else if(language.equals("en")){
                addToLM(EnglishLM, text);
            } else if(language.equals("pt")){
                addToLM(PortugeseLM, text);
            } else {
                continue;
            }
        }
        br.close();
    }


    public void addToLM(LanguageModel lm, String line) throws Exception {
        NGram tmp;
        for(int i=0; i<=line.length() - size; i++) {
            tmp = new NGram();
            for(int j = 0; j<size;j++) {
                tmp.add(line.charAt(i + j) + "");
            }
            lm.add(tmp);
        }
        lm.refreshProbabilities();
    }

    public void test() {
        System.out.println("test");
    }
}



