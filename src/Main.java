import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws Exception {
        // Validate input
        if(args.length < 3) {
            System.err.println("Main.java modelSize /path/to/training/tweets /path/to/testing/tweets");
            return;
        }
        try {
            Integer.parseInt(args[0]);
        } catch(Exception e) {
            System.err.println("Main.java expects first argument to be an integer");
            return;
        }

        if(!new File(args[1]).exists()|| !new File(args[2]).exists()) {
            System.err.println("Main.java expects 2nd and 3rd parameters to be paths to training and testing tweets");
            return;
        }




        CharacterModel unigram = new CharacterModel(Integer.parseInt(args[0]), new File(args[1]), new File(args[2]));
        unigram.train();
        unigram.test();
    }




}
