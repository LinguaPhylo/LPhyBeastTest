package lphybeast.tutorial;

import beast.util.LogAnalyser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        double mean = logAnalyser.getMean("Theta");
        assertEquals(7.16, mean, 0.5, "Theta");

        mean = logAnalyser.getMean("mu_trait"); //ESS 100
        assertEquals(8.2, mean, 2.0, "psi.mu_trait ");

        mean = logAnalyser.getMean("psi.height");
        assertEquals(10.85, mean, 0.5, "psi.height");

        mean = logAnalyser.getMean("D_trait.treeLikelihood");
        assertEquals(-50, mean, 1.0, "D_trait.treeLikelihood");
    }
}
