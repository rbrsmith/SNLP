import java.io.*;
import java.nio.charset.Charset;

public class Main {

    public static void main(String[] args) throws Exception {
        // Validate input
        if(args.length < 3) {
            System.err.println("Main.java /path/to/training/tweets /path/to/testing/tweets modelSize [delta]");
            return;
        }
        try {
            Integer.parseInt(args[2]);
        } catch(Exception e) {
            System.err.println("Main.java expects third argument to be an integer");
            return;
        }

        if(args.length == 4) {
            try {
                Double.parseDouble(args[3]);
            } catch (Exception e) {
                System.err.println("Main.java expects the fourth argument to be an double");
            }
        }


        if(!new File(args[0]).exists()|| !new File(args[1]).exists()) {
            System.err.println("Main.java expects 2nd and 3rd parameters to be paths to training and testing tweets");
            return;
        }

        // Collect input
        File training = new File(args[0]);
        File testing = new File(args[1]);
        int modelSize =  Integer.parseInt(args[2]);
        double delta = 0;
        if(args.length == 4) {
            delta = Double.parseDouble(args[3]);
        }

        // Initiate model
        CharacterModel model = new CharacterModel(training, testing, modelSize,  delta);

//        // Train and then test the model on the two tweet files
        model.train();
        model.test();


        PrintWriter writer = new PrintWriter("/home/ross/Dropbox/IdeaProjects/SMLP/test.txt", "UTF-8");
        writer.print("\uD83D\uDE01");
        writer.close();

        InputStream inputStream = new FileInputStream("/home/ross/Dropbox/IdeaProjects/SMLP/test.txt");
        Reader reader      = new InputStreamReader(inputStream,
                Charset.forName("UTF-8"));


    }




}
