package lphybeast.tutorial;

import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;
import beast.util.LogAnalyser;
import beast.util.NexusParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(28, params.size(), "Number of parameters");
        assertTrue(params.contains("posterior") && params.contains("likelihood") && params.contains("prior") &&
                params.contains("pi_0.A") && params.contains("pi_1.G") && params.contains("pi_2.T") &&
                params.contains("kappa.3") && params.contains("codon_2.treeLikelihood"), "parameters check");

        double mean;
        mean = logAnalyser.getMean("pi_2.A");
        assertEquals(0.32, mean, 0.5, "pi_2.A");

        mean = logAnalyser.getMean("kappa.3");
        assertEquals(-5785.50, mean, 2.0, "D_trait.treeLikelihood");

        mean = logAnalyser.getMean("r_0");
        assertEquals(0.67, mean, 0.5, "r_0");
        mean = logAnalyser.getMean("r_1");
        assertEquals(0.95, mean, 0.5, "r_1");
        mean = logAnalyser.getMean("r_2");
        assertEquals(1.38, mean, 0.5, "r_2");

        mean = logAnalyser.getMean("mu"); // low ESS
        assertEquals(2.3E-3, mean, 1E-3, "mu");

        mean = logAnalyser.getMean("Theta");
        assertEquals(37, mean, 10.0, "Theta");

        mean = logAnalyser.getMean("psi.height");
        assertEquals(56, mean, 5.0, "psi.height");

        mean = logAnalyser.getMean("codon_0.treeLikelihood");
        assertEquals(-1438, mean, 10.0, "codon_0.treeLikelihood");
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
//        System.out.println("\nRSV2 MCC tree = " + mcc);

        Node root = mcc.getRoot();
        double meanRootHeight = root.getHeight();
        double mean = logAnalyser.getMean("psi.height");

        System.out.println("root height = " + meanRootHeight + ", psi.height mean = " + mean);
        assertEquals(mean, meanRootHeight, 1.0, "root height");

        // USALongs56 is child of root
        Node usa56 = null;
        for (Node ch : root.getChildren()) {
            if (ch.isLeaf())
                usa56 = ch;
        }
        assertNotNull(usa56, "USALongs56 is child node of root");
        assertEquals("USALongs56", usa56.getID(), "USALongs56");
    }


}
