package lphybeast.tutorial;

import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;
import beast.util.LogAnalyser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

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
        assertEquals(23, params.size(), "Number of parameters");
        assertTrue(params.contains("posterior") && params.contains("likelihood") && params.contains("prior") &&
                params.contains("pi.T") && params.contains("kappa") && params.contains("gamma") && params.contains("mu") &&
                params.contains("b_m.Hong_Kong_New_York") && params.contains("Theta.New_Zealand") && params.contains("Mascot") &&
                params.contains("psi.height") && params.contains("D.treeLikelihood"), "parameters check");

        double mean;
        mean = logAnalyser.getMean("kappa");
        assertEquals(5.85, mean, 0.5, "kappa");

        mean = logAnalyser.getMean("mu");
        assertEquals(4.037E-3, mean, 1E-3, "clockRate");

        mean = logAnalyser.getMean("b_m.Hong_Kong_New_York");
        assertEquals(1.2, mean, 0.5, "b_m.Hong_Kong_New_York");
        mean = logAnalyser.getMean("b_m.New_York_Hong_Kong");
        assertEquals(1.5, mean, 0.5, "b_m.New_York_Hong_Kong");
        double mean1 = logAnalyser.getMean("b_m.Hong_Kong_New_Zealand");
        double mean2 = logAnalyser.getMean("b_m.New_Zealand_Hong_Kong");
        double mean3 = logAnalyser.getMean("b_m.New_Zealand_New_York");
        assertTrue(mean1 < 1 && mean2 < 1 && mean3 < 1, "rest of b_m");

        mean = logAnalyser.getMean("Theta.Hong_Kong");
        assertEquals(1.43, mean, 0.5, "Theta.Hong_Kong");
        mean = logAnalyser.getMean("Theta.New_York");
        assertEquals(2.15, mean, 0.5, "Theta.New_York");
        mean = logAnalyser.getMean("Theta.New_Zealand");
        assertEquals(0.85, mean, 0.5, "Theta.New_Zealand");

        mean = logAnalyser.getMean("psi.height");
        assertEquals(3.62, mean, 0.5, "psi.height");

        mean = logAnalyser.getMean("Mascot");
        assertEquals(-38.62, mean, 5.0, "Mascot");

        mean = logAnalyser.getMean("D_trait.treeLikelihood");
        assertEquals(-1907.5, mean, 10.0, "D_trait.treeLikelihood");
    }

    @Test
    void testMCCTree() {
        double mean = logAnalyser.getMean("psi.height");
        Tree mccTree = TestUtils.assertMCCTree("h3n2.mascot.tree", mean);

        // check root meta data
        Node root = mccTree.getRoot();
        assertTrue(root.metaDataString.contains("max=") && root.metaDataString.contains("max.prob="), "root metaDataString");
        assertEquals("Hong_Kong", root.getMetaData("max"), "max=Hong_Kong");
        double prob = Double.parseDouble(root.getMetaData("max.prob").toString());
        assertTrue(prob > 0.5, "max.prob= " + prob);

        // check leaf nodes meta data
        for (Node leafN : mccTree.getExternalNodes()) {
            Set<String> meta = leafN.getMetaDataNames();
            assertTrue(meta.contains("max"), "max=");
            Object loc = leafN.getMetaData("max");

            String taxonName = leafN.getID();
            assertNotNull(taxonName, "taxonName " + leafN);
            // A/Auckland/588/2000|CY022501|2000.732240|New_Zealand
            String loc2 = taxonName.substring(taxonName.lastIndexOf("|")+1);
            assertEquals(loc2, loc, "Check location meta data of " + leafN);
        }
    }


}
