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
        assertEquals(7, mean, 0.15, "Theta");

        mean = logAnalyser.getMean("mu_trait");
        assertEquals(8.21, mean, 1.7, "psi.mu_trait ");

        mean = logAnalyser.getMean("psi.height");
        assertEquals(10.85, mean, 0.03, "psi.height");

        mean = logAnalyser.getMean("D_trait.treeLikelihood");
        assertEquals(-49.76, mean, 0.5, "D_trait.treeLikelihood");
    }
}
