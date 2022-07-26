package lphybeast.tutorial;

import beast.util.LogAnalyser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Walter Xie
 */
public class H5N1Test {

    private LogAnalyser logAnalyser;

    @BeforeEach
    void setUp() throws IOException {
        logAnalyser = new LogAnalyser(TestUtils.LOG_PATH + "h5n1.log");
    }

    @Test
    void assertLog() {
        List<String> params = logAnalyser.getLabels();
        System.out.println("\nParameters = " + params);
        assertEquals(66, params.size(), "Number of parameters");

        double mean;
        mean = logAnalyser.getMean("prior");
        assertEquals(-119, mean, 2.0, "prior");

        mean = logAnalyser.getMean("D.treeLikelihood");
        assertEquals(-5785.50, mean, 2.0, "D_trait.treeLikelihood");

        mean = logAnalyser.getMean("Theta");
        assertEquals(7.16, mean, 0.5, "Theta");

        mean = logAnalyser.getMean("mu_trait"); //ESS 100
        assertEquals(8.2, mean, 2.0, "psi.mu_trait ");

        mean = logAnalyser.getMean("psi.height");
        assertEquals(10.85, mean, 0.5, "psi.height");

        mean = logAnalyser.getMean("D_trait.treeLikelihood");
        assertEquals(-50, mean, 1.0, "D_trait.treeLikelihood");
    }

    @Test
    void assertTransitions() {
        File file = new File(TestUtils.LOG_PATH + "h5n1.stc.out");
        assertTrue(file.exists(), "h5n1.stc.out");

//        BufferedReader reader;
//        try {
//            reader = new BufferedReader(new FileReader(file));
//            String line = reader.readLine();
//            while (line != null) {
//                System.out.println(line);
//                // read next line
//                line = reader.readLine();
//            }
//            reader.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }



    }
}
