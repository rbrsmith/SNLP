import com.sun.xml.internal.ws.server.provider.ProviderArgumentsBuilder;

import java.io.*;
import java.nio.file.ProviderNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
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
        BasqueLM = new LanguageModel(size, "Basque");
        CatalanLM = new LanguageModel(size, "Catalan");
        GalicianLM = new LanguageModel(size, "Galician");
        SpanishLM = new LanguageModel(size, "Spanish");
        EnglishLM = new LanguageModel(size, "English");
        PortugeseLM = new LanguageModel(size, "Protugese");

    }

    public void train() throws Exception {
        //TODO strip out /n's


        FileInputStream fstream = new FileInputStream(trainingFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));

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

    public void test()  throws Exception {
        String test = "romeo did a big fat poo in the kitchen.";
        Probability winner = new Probability(0);
        int winI = -1;

        Probability prob;


        LanguageModel[] lms = new LanguageModel[6];
        lms[0] = BasqueLM;
        lms[1] = CatalanLM;
        lms[2] = GalicianLM;
        lms[3] = SpanishLM;
        lms[4] = EnglishLM;
        lms[5] = PortugeseLM;


        for(int i=0;i<6;i++){
            prob = getProbability(test, lms[i]);
            System.out.println(prob);
            if(prob.compareTo(winner) > 0) {
                winner = prob;
                winI = i;
            }
        }
        System.out.println("Winner is " + lms[winI].getName() + " at " + winner);
    }

    private Probability getProbability(String text, LanguageModel lm) throws Exception {
        NGram tmp;
        Probability total = null;
        for(int i=0; i<=text.length() - size; i++) {
            tmp = new NGram();
            for(int j = 0; j<size;j++) {
                tmp.add(text.charAt(i + j) + "");
            }
            Probability res = lm.get(tmp).getProb();

            if(total == null) {
                total = res;
            } else {
                total = Probability.multiply(total, res);
            }
        }


        return total;
    }
}



