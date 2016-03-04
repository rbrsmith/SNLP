import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class DataTest {

    Data data1;
    Data data2;
    Data data3;

    @Before
    public void setUp() throws Exception {
        data1 = new Data(1);
        data2 = new Data(2);
        data3 = new Data(3);


    }

    @After
    public void tearDown() throws Exception {
        data1=null;data2=null;data3=null;
    }

    @Test
    public void testAdd() throws Exception {

        // Bigram
        String text= "The informative wrecker detaches the enterprise inside an evolutionary novice. A deaf season excludes the doctrine. The seventh accepts in the insulting zone. The plate laughs!";
        for(int i = 0;i<text.length() -1;i++) {
            char c = text.charAt(i);
            char c1 = text.charAt(i+1);
            if(c == ' ' || c1 == ' ') {
                continue;
            }
            String tmp = c + " " + c1;
            data2.add(tmp);
        }
        data2.refreshProbabilities();

        String real = "enterprise";
        Probability total = null;
        for(int i = 0; i<real.length() -1; i++){
            char c = real.charAt(i);
            char c1 = real.charAt(i+1);

            Probability one = data2.get(c + " " + c1).getProb();
            if(total == null) {
                total = one;
            } else {
                total = Probability.multiply(one, total);
            }


        }
        System.out.println(total);

    }
}