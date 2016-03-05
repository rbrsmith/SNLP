import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        LanguageModelTest.class,
        NGramTest.class,
        ProbabilityTest.class,
        TETest.class
        })

public class TestSuite {

}
