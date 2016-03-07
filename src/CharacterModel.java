import com.sun.xml.internal.ws.server.provider.ProviderArgumentsBuilder;

import java.io.*;
import java.nio.file.ProviderNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

//TODO better delta smoothing handling (especially get)
// TODO why does 4-gram return null with smoothing

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
        BasqueLM = new LanguageModel(size, "Basque", "eu");
        CatalanLM = new LanguageModel(size, "Catalan", "ca");
        GalicianLM = new LanguageModel(size, "Galician", "gl");
        SpanishLM = new LanguageModel(size, "Spanish", "es");
        EnglishLM = new LanguageModel(size, "English", "en");
        PortugeseLM = new LanguageModel(size, "Protugese", "pt");

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

        double delta = 0.5;
        BasqueLM.refreshProbabilities(delta);
        CatalanLM.refreshProbabilities(delta);
        GalicianLM.refreshProbabilities(delta);
        SpanishLM.refreshProbabilities(delta);
        EnglishLM.refreshProbabilities(delta);
        PortugeseLM.refreshProbabilities(delta);
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
    }

    public void test()  throws Exception {


        FileInputStream fstream = new FileInputStream(testingFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));

        String strLine;
        int tried   = 0;
        int total   = 0;
        int correct = 0;
        while ((strLine = br.readLine()) != null) {
            // Print the content on the console
            String[] tabs = strLine.split("\t");
            if (tabs.length < 3) {
                continue;
            }
            String text = "";
            for (int i = 3; i < tabs.length; i++) {
                text += tabs[i];
            }
            String language = tabs[2];
            String probLanguage = getLanguage(text);
            total += 1;
            if(probLanguage != null) {
                tried += 1;
                if(probLanguage.equals(language)) {
                    correct += 1;
                }
            }
        }
        Double triedD = new Double(tried);
        Double totalD = new Double(total);
        Double correctD = new Double(correct);

        System.out.printf("%s%f%s\n", "Recall: ", correctD / totalD * 100, "%");
        System.out.printf("%s%f%s\n", "Precision: ", correctD / triedD * 100, "%");


    }

    private String getLanguage(String text) throws Exception {Probability winner = new Probability(0);
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
            prob = getProbability(text, lms[i]);
        //    System.out.println(prob);
            if(prob.compareTo(winner) > 0) {
                winner = prob;
                winI = i;
            }
        }
        if(winI == -1) {
            return null;
        } else {
            return lms[winI].getLang();
        }
    }

    private Probability getProbability(String text, LanguageModel lm) throws Exception {
        NGram tmp;
        Probability total = null;
        double delta = 0.5;
        for(int i=0; i<=text.length() - size; i++) {
            tmp = new NGram();
            for(int j = 0; j<size;j++) {
                tmp.add(text.charAt(i + j) + "");
            }
            Probability res = lm.get(tmp,delta).getProb();

            if(total == null) {
                total = res;
            } else {
                total = Probability.multiply(total, res);
            }
        }


        return total;
    }
}



