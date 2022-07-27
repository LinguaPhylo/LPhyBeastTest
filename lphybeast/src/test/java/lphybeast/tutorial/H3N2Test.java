package lphybeast.tutorial;

import beast.evolution.tree.Tree;
import beast.util.LogAnalyser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author Walter Xie
 */
public class H3N2Test {

    private LogAnalyser logAnalyser;

    @BeforeEach
    void setUp() throws IOException {
        // working dir is */LPhyBeastTest/lphybeast
        logAnalyser = new LogAnalyser("h3n2.log", TestUtils.BURNIN_PERC);
    }

    @Test
    void testLog() {
        List<String> params = logAnalyser.getLabels();
        System.out.println("\nParameters = " + params);
//        assertEquals(25, params.size(), "Number of parameters");
//        assertTrue(params.contains("posterior") && params.contains("likelihood") && params.contains("prior") &&
//                params.contains("pi.T") && params.contains("rates.GT") && params.contains("gamma") &&
//                params.contains("A.4") && params.contains("Theta.4") && params.contains("psi.treeLength") &&
//                params.contains("D.treeLikelihood"), "parameters check");
//
//        double mean;
//        mean = logAnalyser.getMean("pi.A");
//        assertEquals(0.193, mean, 0.5, "pi.A");
//
//        mean = logAnalyser.getMean("rates.CG");
//        assertEquals(2.868E-2, mean, 0.01, "rates.CG");
//
//        mean = logAnalyser.getMean("Theta.1");
//        assertEquals(8200, mean, 500.0, "Theta.1");
//
//        mean = logAnalyser.getMean("psi.height");
//        assertEquals(412, mean, 50.0, "psi.height");
//
//        mean = logAnalyser.getMean("D_trait.treeLikelihood");
//        assertEquals(-6164, mean, 50.0, "D_trait.treeLikelihood");
    }

    @Test
    void testMCCTree() {
        double mean = logAnalyser.getMean("psi.height");
        Tree mccTree = TestUtils.assertMCCTree("hcv_coal.tree", mean);
    }


}
