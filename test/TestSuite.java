import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        DataTest.class,
        NGramTest.class,
        ProbabilityTest.class,
        TETest.class
        })

public class TestSuite {

}
