package lphybeast.tutorial;

import beast.evolution.tree.Tree;
import beast.util.LogAnalyser;
import beast.util.NexusParser;
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
public class RSV2Test {

    private LogAnalyser logAnalyser;

    @BeforeEach
    void setUp() throws IOException {
        logAnalyser = new LogAnalyser(TestUtils.LOG_PATH + "RSV2.log");
    }

    @Test
    void assertLog() {
        List<String> params = logAnalyser.getLabels();
        System.out.println("\nParameters = " + params);
        assertEquals(66, params.size(), "Number of parameters");
        assertTrue(params.contains("posterior") && params.contains("likelihood") && params.contains("mu_trait") &&
                params.contains("pi.A") && params.contains("kappa") && params.contains("gamma") &&
                params.contains("pi_trait.6") &&params.contains("I.15") && params.contains("R_trait.15") &&
                params.contains("svs.relGeoRate_Fujian_Guangdong"), "parameters check");

        double mean;
        mean = logAnalyser.getMean("prior");
        assertEquals(-119, mean, 2.0, "prior");

        mean = logAnalyser.getMean("D.treeLikelihood");
        assertEquals(-5785.50, mean, 2.0, "D_trait.treeLikelihood");

        mean = logAnalyser.getMean("Theta");
        assertEquals(7.16, mean, 0.5, "Theta");

//        mean = logAnalyser.getMean("mu_trait"); //low ESS
//        assertEquals(8, mean, 2.0, "psi.mu_trait ");

        mean = logAnalyser.getMean("psi.height");
        assertEquals(10.85, mean, 0.5, "psi.height");

        mean = logAnalyser.getMean("D_trait.treeLikelihood");
        assertEquals(-50, mean, 1.0, "D_trait.treeLikelihood");
    }

    @Test
    void assertTransitions() {
        File file = new File(TestUtils.LOG_PATH + "RSV2.tree");
        assertTrue(file.exists(), "RSV2.tree");

        NexusParser nexusParser = new NexusParser();
        try {
            nexusParser.parseFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Tree> parsedTrees = nexusParser.trees;
        assertEquals(1, parsedTrees.size(), "RSV2 MCC tree");

        Tree mcc = parsedTrees.get(0);
        System.out.println("\nRSV2 MCC tree = " + mcc);


    }


}
