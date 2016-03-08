import java.io.File;
import java.io.PrintWriter;

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
//        model.train();
 //       model.test();


        PrintWriter writer = new PrintWriter("C:\\Users\\b0467851\\WORK\\school\\Simplified-TweetLID-corpus\\Simplified-TweetLID-corpus\\test.txt", "UTF-8");


        byte[] bytes = {
                0x48, 0x69, 0x2c,                       // ASCII chars are 1 byte each
                (byte) 0xe6, (byte) 0x82, (byte) 0xa8,  // U+60A8

                (byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x81,

                (byte) 0xe5, (byte) 0xa5, (byte) 0xbd,  // U+597D
                0x21

        };
        String s = new String(bytes, "UTF-8");

        writer.print("\uD83D\uDE01");
        writer.println();
        writer.print(s);
        writer.close();
        System.out.println(s);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i<s.length();) {
            int codePoint = s.codePointAt(i);
            System.out.println(codePoint);
            sb.appendCodePoint(codePoint);
            System.out.println(Character.isValidCodePoint(codePoint));
            i += Character.charCount(codePoint);
        }
        System.out.println(sb);

    }




}
