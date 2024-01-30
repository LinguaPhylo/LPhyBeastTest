package lphybeast.tutorial;

import beast.base.evolution.tree.Tree;
import beastfx.app.tools.LogAnalyser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Walter Xie
 */
public class HCVTest {

    private LogAnalyser logAnalyser;

    @BeforeEach
    void setUp() throws IOException {
        // working dir is */LPhyBeastTest/lphybeast
        logAnalyser = new LogAnalyser("xmls/hcv_coal.log", TestUtils.BURNIN_PERC);
    }

    @Test
    void testLog() {
        List<String> params = logAnalyser.getLabels();
        System.out.println("\nParameters = " + params);
        assertEquals(25, params.size(), "Number of parameters");
        assertTrue(params.contains("posterior") && params.contains("likelihood") && params.contains("prior") &&
                params.contains("pi.T") && params.contains("rates.GT") && params.contains("gamma") &&
                params.contains("A.1") && params.contains("A.2") && params.contains("A.3") && params.contains("A.4") &&
                params.contains("Theta.1") && params.contains("Theta.2") && params.contains("Theta.3") &&
                params.contains("Theta.4") && params.contains("psi.treeLength") &&
                params.contains("D.treeLikelihood"), "parameters check");

        double mean;
        mean = logAnalyser.getMean("pi.A");
        assertEquals(0.193, mean, 0.5, "pi.A");

        mean = logAnalyser.getMean("rates.CG");
        assertEquals(2.868E-2, mean, 0.01, "rates.CG");

        mean = logAnalyser.getMean("Theta.1");
        assertEquals(8200, mean, 500.0, "Theta.1");
        mean = logAnalyser.getMean("Theta.2");
        assertEquals(1600, mean, 800.0, "Theta.2");
        mean = logAnalyser.getMean("Theta.3");
        assertEquals(400, mean, 200.0, "Theta.3");
        mean = logAnalyser.getMean("Theta.4");
        assertEquals(300, mean, 50.0, "Theta.4");

        mean = logAnalyser.getMean("psi.height");
        assertEquals(412, mean, 50.0, "psi.height");

        mean = logAnalyser.getMean("D.treeLikelihood");
        assertEquals(-6164, mean, 50.0, "D.treeLikelihood");
    }

    @Test
    void testMCCTree() {
        double mean = logAnalyser.getMean("psi.height");
        Tree mccTree = TestUtils.assertMCCTree("xmls/hcv_coal.tree", mean);
    }

    @Test
    void testSkylinePlot() {
        //TODO plot Skyline
    }
}
