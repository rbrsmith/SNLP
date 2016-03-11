package Assignment2;

import java.io.*;


/**
 * Main drive of the assignment
 */
public class Main {

    public static void main(String[] args) throws Exception {
        // Validate input

        // Make sure we have the correct amount of values
        if(args.length < 3) {
            System.err.println("Assignment2.Main.java /path/to/training/tweets /path/to/testing/tweets modelSize [delta]");
            return;
        }

        // Make sure ints are ints
        try {
            Integer.parseInt(args[2]);
        } catch(Exception e) {
            System.err.println("Assignment2.Main.java expects third argument to be an integer");
            return;
        }

        if(args.length == 4) {
            try {
                Double.parseDouble(args[3]);
            } catch (Exception e) {
                System.err.println("Assignment2.Main.java expects the fourth argument to be an double");
            }
        }


        // Make sure files are files
        if(!new File(args[0]).exists()|| !new File(args[1]).exists()) {
            System.err.println("Assignment2.Main.java expects 2nd and 3rd parameters to be paths to training and testing tweets");
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
        // Train and Test
        model.train();
        model.test();

        System.out.println("O.K.");
    }
}
