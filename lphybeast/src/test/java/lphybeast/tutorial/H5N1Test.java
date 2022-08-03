package lphybeast.tutorial;

import beast.util.LogAnalyser;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Walter Xie
 */
public class H5N1Test {

    private LogAnalyser logAnalyser;

    @BeforeEach
    void setUp() throws IOException {
        // working dir is */LPhyBeastTest/lphybeast
        logAnalyser = new LogAnalyser("h5n1.log", TestUtils.BURNIN_PERC);
    }

    @Test
    void testLog() {
        List<String> params = logAnalyser.getLabels();
        System.out.println("\nParameters = " + params);
        assertEquals(66, params.size(), "Number of parameters");
        assertTrue(params.contains("posterior") && params.contains("likelihood") && params.contains("mu_trait") &&
                params.contains("pi.A") && params.contains("kappa") && params.contains("gamma") &&
                params.contains("pi_trait.6") &&params.contains("I.15") && params.contains("R_trait.15") &&
                params.contains("svs.relGeoRate_Fujian_Guangdong"), "parameters check");

        double mean;
        mean = logAnalyser.getMean("prior");
        assertEquals(-119, mean, 10.0, "prior");

        mean = logAnalyser.getMean("Theta");
        assertEquals(7.16, mean, 0.5, "Theta");

//        mean = logAnalyser.getMean("mu_trait"); //low ESS
//        assertEquals(8, mean, 2.0, "psi.mu_trait ");

        mean = logAnalyser.getMean("psi.height");
        assertEquals(10.85, mean, 1.0, "psi.height");

        mean = logAnalyser.getMean("D.treeLikelihood");
        assertEquals(-5785.50, mean, 20.0, "D_trait.treeLikelihood");

        mean = logAnalyser.getMean("D_trait.treeLikelihood");
        assertEquals(-50, mean, 5.0, "D_trait.treeLikelihood");
    }

    @Test
    void testMCCTreeAndNode() {
        double mean = logAnalyser.getMean("psi.height");
        //TODO beast.util.TreeParser$TreeParsingException: token recognition error at: '?'
//        Tree mccTree = TestUtils.assertMCCTree("h5n1_with_trait.tree", mean);

        File treeFile = new File("h5n1_with_trait.tree");
        assertTrue(treeFile.exists(), "h5n1_with_trait.tree");

        List<String> lines = null;
        try {
            lines = Files.readAllLines(treeFile.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertNotNull(lines, "tree file lines");

        //TODO no "?" in MCC tree anymore
        // location.set = {Guangdong,HongKong,Hunan,Guangxi,Fujian,?}
        // location.set.prob = {0.18878400888395336,0.5857856746252083,...}
        boolean hasTree = false;
        for (String line : lines) {
            // tree TREE1 =
            if (line.startsWith("tree")) {
                int lastLeftSquareBracket = line.lastIndexOf("[");
                int lastRightSquareBracket = line.lastIndexOf("]");
                assertTrue(lastLeftSquareBracket > 10 && lastRightSquareBracket > lastLeftSquareBracket);

                String rootMetaData = line.substring(lastLeftSquareBracket+1, lastRightSquareBracket);
                System.out.println(rootMetaData);

                String rootHeight = StringUtils.substringBetween(rootMetaData, "height=", ",");
                assertNotNull(rootHeight, "height=");
                double meanRootHeight = Double.parseDouble(rootHeight);
                System.out.println("root height = " + meanRootHeight + ", psi.height mean = " + mean);
                assertEquals(mean, meanRootHeight, 5.0, "root height");

                String locationSet = StringUtils.substringBetween(rootMetaData, "location.set={", "},");
                assertTrue(locationSet.startsWith("Guangdong,HongKong,Hunan,Guangxi,Fujian"),
                        "location.set={" + locationSet + "}");

                String locationSetProb = StringUtils.substringBetween(rootMetaData, "location.set.prob={", "},");
                assertNotNull(locationSetProb, "location.set.prob={");
                double[] probArr = Arrays.stream(locationSetProb.split(","))
                        .mapToDouble(Double::parseDouble).toArray();

                System.out.println("location.set = " + locationSet);
                System.out.println("location.set.prob = " + Arrays.toString(probArr));

                // HongKong prob is the biggest
                assertTrue(probArr.length == 5 || probArr.length == 6, "location.set.prob length");
                double sum = Arrays.stream(probArr).sum();
                assertEquals(1, sum, 1E-10, "sum prob");

                //TODO sometime Pr HongKong < Fujian even chain len = 20M, which is wrong
                assertTrue(probArr[1] > 0.4 && 0.2 > probArr[0] && 0.2 > probArr[2] &&
                        0.2 > probArr[3] && probArr[1] > probArr[4],
                        "HongKong prob=" + probArr[1] + ", Fujian prob=" + probArr[4]);

                hasTree = true;
            }
        }
        assertTrue(hasTree, "tree TREE1 = ");
    }

    @Test
    void testTransitions() {
        File file = new File("h5n1.stc.out");
        assertTrue(file.exists(), "h5n1.stc.out");

        boolean startToCount = false;
        // Fujian=>Hunan  Guangdong=>Hunan  Guangxi=>Hunan  HongKong=>Hunan
        String[] direction = new String[4];
        int[] zero = new int[4];
//        int[] one = new int[4];
        int[] two = new int[4];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            int locId = 0;
            while (line != null) {
                if (!startToCount)
                    startToCount = line.trim().startsWith("Transition\t0\t1\t2");
                if (startToCount) {
                    // skip header
                    line = reader.readLine();
                    // then, line looks like : Fujian=>Fujian	39	15	450 ...
                    if ( line.trim().contains("=>Hunan") && !line.trim().equals("Hunan=>Hunan") ) {
                        String[] elem = line.split("\t");
                        System.out.println(Arrays.toString(elem));
                        // at least 4 columns : Transition	0	1	2
                        assertTrue(elem.length > 2, "Transition array of " + elem[0]);

                        assertTrue(locId < 4, "locId = " + locId);

                        direction[locId] = elem[0];
                        zero[locId] = Integer.parseInt(elem[1]);
//                        one[locId] = Integer.parseInt(elem[2]);
                        if (elem.length > 3)
                            two[locId] = Integer.parseInt(elem[3]);
                        else two[locId] = 0;

                        locId += 1;
                    }
                    // until empty line
                    if (line.trim().isEmpty()) break;
                }
                // read next line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String TO = "Guangxi=>Hunan";
        int maxId = getMaxId(zero);
        assertTrue(maxId >= 0 && maxId < 4, "0 transition : maxId = " + maxId);
        // not Guangxi=>Hunan
        assertNotEquals(TO, direction[maxId], "0 transition");

//        maxId = getMaxId(one);
//        assertTrue(maxId >= 0 && maxId < 4, "1 transition : maxId = " + maxId);
//        // yes Guangxi=>Hunan
//        assertEquals(TO, direction[maxId], "1 transition"); // uncertain as low ESS

        maxId = getMaxId(two);
        assertTrue(maxId >= 0 && maxId < 4, "2 transition : maxId = " + maxId);
        // yes Guangxi=>Hunan
        assertEquals(TO, direction[maxId], "2 transition");
    }

    private int getMaxId(int[] ints) {
        int max = 0;
        int maxId = -1;
        for (int i = 0; i < ints.length; i++) {
            if (ints[i] > max) {
                maxId = i;
                max = ints[i];
            }
        }
        return maxId;
    }
}
