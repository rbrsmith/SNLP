import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CharacterModel {

    private int size;
    private LanguageModel BasqueLM;
    private LanguageModel CatalanLM;
    private LanguageModel GalicianLM;
    private LanguageModel SpanishLM;
    private LanguageModel EnglishLM;
    private LanguageModel PortugeseLM;
    private File trainingFile;
    private File testingFile;
    private String base;
    private String lmBase;
    private double delta;

    public CharacterModel(File trainingFile, File testingFile, int size, Double delta) throws Exception {
        this.size = size;
        this.trainingFile = trainingFile;
        this.testingFile = testingFile;
        String p = trainingFile.getAbsolutePath();
        this.base = p.substring(0, p.lastIndexOf(File.separator) + 1);
        this.lmBase = this.base + "LMs" + File.separator;
        this.delta = delta;
        BasqueLM = new LanguageModel(size, "Basque", "eu");
        CatalanLM = new LanguageModel(size, "Catalan", "ca");
        GalicianLM = new LanguageModel(size, "Galician", "gl");
        SpanishLM = new LanguageModel(size, "Spanish", "es");
        EnglishLM = new LanguageModel(size, "English", "en");
        PortugeseLM = new LanguageModel(size, "Protugese", "pt");
    }

    public void train() throws Exception {

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

        BasqueLM.refreshProbabilities(delta);
        CatalanLM.refreshProbabilities(delta);
        GalicianLM.refreshProbabilities(delta);
        SpanishLM.refreshProbabilities(delta);
        EnglishLM.refreshProbabilities(delta);
        PortugeseLM.refreshProbabilities(delta);

        save( BasqueLM);save(CatalanLM);save(GalicianLM);save(SpanishLM);save(EnglishLM);save(PortugeseLM);
    }


//    public void addToLM(LanguageModel lm, String line) throws Exception {
//        NGram tmp;
//        for(int i=0; i<=line.length() - size; i++) {
//            tmp = new NGram();
//            for(int j = 0; j<size;j++) {
//                tmp.add((line.charAt(i + j) + ""));
//            }
//            lm.add(tmp);
//        }
//    }

    public void addToLM(LanguageModel lm, String line) throws Exception {
        NGram tmp;
        for(int i = 0; i<line.length();) {
            tmp = new NGram();
            int k = 0;
            boolean eof = false;
            for(int j=0; j<size;) {
                if((i+k) > line.length() -1) {
                    eof = true;
                    break;
                }
                int codePoint = line.codePointAt(i + k);
                int[] arr = new int[] {codePoint};
                tmp.add(new String(arr, 0, 1));
                k += Character.charCount(codePoint);
                j += 1;
            }
            if(eof) break;
            lm.add(tmp);

            int codePoint = line.codePointAt(i + 0);
            i += Character.charCount(codePoint);
//            if(!Character.isValidCodePoint(codePoint)) {
//                System.out.println(Character.isValidCodePoint(codePoint));
//            }
        }
    }

    public void test()  throws Exception {


        FileInputStream fstream = new FileInputStream(testingFile);
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream, "UTF-8"));

        String strLine;
        int tried   = 0;
        int total   = 0;
        int correct = 0;
        HashMap<String, String> results = new HashMap<>();

        int basqueTotal = 0;
        int catalanTotal = 0;
        int galacianTotal = 0;
        int spanishTotal = 0;
        int englishTotal = 0;
        int portugeseTotal = 0;

        int basqueCorrect = 0;
        int catalanCorrect = 0;
        int galacianCorrect = 0;
        int spanishCorrect = 0;
        int englishCorrect = 0;
        int portugeseCorrect = 0;


        HashMap<String, HashMap<String, Integer>> matrix = new HashMap<>();
        matrix.put("eu", new HashMap<String, Integer>() {{
            put("eu", 0);
            put("ca", 0);
            put("gl", 0);
            put("es", 0);
            put("en", 0);
            put("pt", 0);
        }});
        matrix.put("ca", new HashMap<String, Integer>() {{
            put("eu", 0);
            put("ca", 0);
            put("gl", 0);
            put("es", 0);
            put("en", 0);
            put("pt", 0);
        }});
        matrix.put("gl", new HashMap<String, Integer>() {{
            put("eu", 0);
            put("ca", 0);
            put("gl", 0);
            put("es", 0);
            put("en", 0);
            put("pt", 0);
        }});
        matrix.put("es", new HashMap<String, Integer>() {{
            put("eu", 0);
            put("ca", 0);
            put("gl", 0);
            put("es", 0);
            put("en", 0);
            put("pt", 0);
        }});
        matrix.put("en", new HashMap<String, Integer>() {{
            put("eu", 0);
            put("ca", 0);
            put("gl", 0);
            put("es", 0);
            put("en", 0);
            put("pt", 0);
        }});
        matrix.put("pt", new HashMap<String, Integer>() {{
            put("eu", 0);
            put("ca", 0);
            put("gl", 0);
            put("es", 0);
            put("en", 0);
            put("pt", 0);
        }});



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
            if(text.length() < size) {
                // Too small
                continue;
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


            results.put(tabs[0], probLanguage);
            if(language.equals("eu")) {
                basqueTotal += 1;
                if(probLanguage.equals(language)) {
                    basqueCorrect += 1;
                }
            } else if(language.equals("ca")){
                catalanTotal += 1;
                if(probLanguage.equals(language)) {
                    catalanCorrect += 1;
                }
            } else if(language.equals("gl")){
                galacianTotal += 1;
                if(probLanguage.equals(language)) {
                    galacianCorrect += 1;
                }
            } else if(language.equals("es")){
                spanishTotal += 1;
                if(probLanguage.equals(language)) {
                    spanishCorrect += 1;
                }
            } else if(language.equals("en")){
                englishTotal += 1;
                if(probLanguage.equals(language)) {
                    englishCorrect += 1;
                }
            } else if(language.equals("pt")){
                portugeseTotal += 1;
                if(probLanguage.equals(language)) {
                    portugeseCorrect += 1;
                }
            } else {

            }

            if(probLanguage != null && language.length() == 2) {
                HashMap<String, Integer> tmp = matrix.get(probLanguage);
                tmp.put(language, tmp.get(language) + 1);
            }


        }
        Double triedD = new Double(tried);
        Double totalD = new Double(total);
        Double correctD = new Double(correct);

        System.out.printf("%s%f%s\n", "Recall: ", correctD / totalD * 100, "%");
        System.out.printf("%s%f%s\n", "Precision: ", correctD / triedD * 100, "%");






        String path = this.base + "results-" + size + "gram.txt";
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        for(Map.Entry<String, String> str : results.entrySet()){
            writer.println(str.getKey() + "\t" + str.getValue());
        }
        writer.close();


        path = this.base + "analysis-" + size + "gram.txt";
        writer = new PrintWriter(path, "UTF-8");
        writer.println("Overall Accuracy: " + correctD / totalD * 100 + "%");
        writer.println("Basque Accuracy: " + new Double(basqueCorrect) / new Double(basqueTotal) * new Double(100) + "%");
        writer.println("Catalan Accuracy: " + new Double(catalanCorrect) / new Double(catalanTotal) * new Double(100) + "%");
        writer.println("Glacian Accuracy: " + new Double(galacianCorrect) / new Double(galacianTotal) * new Double(100) + "%");
        writer.println("Spanish Accuracy: " + new Double(spanishCorrect) / new Double(spanishTotal) * new Double(100) + "%");
        writer.println("English Accuracy: " + new Double(englishCorrect) / new Double(englishTotal) * new Double(100) + "%");
        writer.println("Portugese Accuracy: " + new Double(portugeseCorrect) / new Double(portugeseTotal) * new Double(100) + "%");
        writer.println("Confusion Matrix");
        writer.println("\t\tEU\tCA\tGL\tES\tEN\tPT");
        HashMap<String, Integer> mEU =  matrix.get("eu");
        writer.println("EU\t\t"+mEU.get("eu")+"\t"+mEU.get("ca")+"\t"+mEU.get("gl")+"\t"+mEU.get("es") +"\t"+mEU.get("en")+"\t"+mEU.get("pt"));
        mEU =  matrix.get("ca");
        writer.println("CA\t\t"+mEU.get("eu")+"\t"+mEU.get("ca")+"\t"+mEU.get("gl")+"\t"+mEU.get("es") +"\t"+mEU.get("en")+"\t"+mEU.get("pt"));
        mEU =  matrix.get("gl");
        writer.println("GL\t\t"+mEU.get("eu")+"\t"+mEU.get("ca")+"\t"+mEU.get("gl")+"\t"+mEU.get("es") +"\t"+mEU.get("en")+"\t"+mEU.get("pt"));
        mEU =  matrix.get("es");
        writer.println("ES\t\t"+mEU.get("eu")+"\t"+mEU.get("ca")+"\t"+mEU.get("gl")+"\t"+mEU.get("es") +"\t"+mEU.get("en")+"\t"+mEU.get("pt"));
        mEU =  matrix.get("en");
        writer.println("EN\t\t"+mEU.get("eu")+"\t"+mEU.get("ca")+"\t"+mEU.get("gl")+"\t"+mEU.get("es") +"\t"+mEU.get("en")+"\t"+mEU.get("pt"));
        mEU =  matrix.get("pt");
        writer.println("PT\t\t"+mEU.get("eu")+"\t"+mEU.get("ca")+"\t"+mEU.get("gl")+"\t"+mEU.get("es") +"\t"+mEU.get("en")+"\t"+mEU.get("pt"));


        writer.close();





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
        for(int i=0; i<=text.length() - size; i++) {
            tmp = new NGram();
            for(int j = 0; j<size;j++) {
                tmp.add((text.charAt(i + j) + ""));
            }
            Probability res = lm.get(tmp, delta).getProb();

            if(total == null) {
                total = res;
            } else {
                total = Probability.multiply(total, res);
            }
        }


        return total;
    }

    public void save(LanguageModel lm) throws Exception {


        new File(this.lmBase).mkdir();
        String path = this.lmBase + lm.getName() + "-" + size + "gramLM.txt";
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        int i = 0;
        lm.refreshProbabilities(0);
        HashMap<String, String> output = new HashMap<>();
        for(String key: lm.getKeys()) {
            HashMap<String, TE> grams = lm.getAt(key);
            for(Map.Entry<String, TE> gram: grams.entrySet()){
                output.put(gram.getKey(), gram.getValue().getProb().toString());
                i += 1;
            }
        }
        lm.refreshProbabilities(delta);
        for(String key: lm.getKeys()) {
            HashMap<String, TE> grams = lm.getAt(key);
            for(Map.Entry<String, TE> gram: grams.entrySet()) {
                String tmp = output.get(gram.getKey());
                output.put(gram.getKey(), tmp + "\t" + gram.getValue().getProb().toString());
                i += 1;
            }
        }

        int l = 0;
        for(Map.Entry<String, String> str: output.entrySet()) {
            if( l >= 50 ) break;
            l += 1;
            writer.println(str.getKey() + "\t" + str.getValue());
        }


        writer.close();


    }
}



