package lphybeast.tutorial;

import beast.base.evolution.tree.Node;
import beast.base.evolution.tree.Tree;
import beastfx.app.tools.LogAnalyser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        // working dir is */LPhyBeastTest/lphybeast
        logAnalyser = new LogAnalyser("RSV2.log", TestUtils.BURNIN_PERC);
    }

    @Test
    void testLog() {
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
        assertEquals(10.68, mean, 1.0, "kappa.3");

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
    void testMCCTreeAndNode() {
        double mean = logAnalyser.getMean("psi.height");
        Tree mccTree = TestUtils.assertMCCTree("RSV2.tree", mean);

        // USALongs56 is child of root
        Node usa56 = null;
        for (Node ch : mccTree.getRoot().getChildren()) {
            if (ch.isLeaf())
                usa56 = ch;
        }
        assertNotNull(usa56, "USALongs56 is child node of root");

        assertEquals("USALongs56", usa56.getID(), "USALongs56");
    }



}
