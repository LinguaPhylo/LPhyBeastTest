package lphybeast.tutorial;

import beast.base.evolution.tree.Node;
import beast.base.evolution.tree.Tree;
import beastfx.app.tools.LogAnalyser;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
        assertEquals(50, params.size(), "Number of parameters");
        assertTrue(params.contains("posterior") && params.contains("likelihood") && params.contains("mu_trait") &&
                params.contains("pi.A") && params.contains("kappa") && params.contains("gamma") &&
                // dimension=5 or =10
                params.contains("pi_trait.5") &&params.contains("I.10") && params.contains("R_trait.10") &&
                params.contains("svs.relGeoRate_Fujian_Guangdong"), "parameters check");

        double mean;
        mean = logAnalyser.getMean("prior");
        assertEquals(-121, mean, 5.0, "prior");

        mean = logAnalyser.getMean("Theta");
        assertEquals(6.99, mean, 0.5, "Theta");

        mean = logAnalyser.getMean("mu_trait");
        assertEquals(0.5, mean, 0.2, "psi.mu_trait ");

        mean = logAnalyser.getMean("psi.height");
        assertEquals(10.9, mean, 1.0, "psi.height");

        mean = logAnalyser.getMean("D.treeLikelihood");
        assertEquals(-5785, mean, 10.0, "D_trait.treeLikelihood");

        mean = logAnalyser.getMean("D_trait.treeLikelihood");
        assertEquals(-46.99, mean, 5.0, "D_trait.treeLikelihood");
    }

    @Test
    void testMCCTreeAndNode() {
        double mean = logAnalyser.getMean("psi.height");

        Tree mccTree = TestUtils.assertMCCTree("h5n1_with_trait.tree", mean);
        Node root = mccTree.getRoot();

        // check nodes
        for (Node ch : root.getChildren()) {
            if (ch.getLeafNodeCount() < 12) {
                // HongKong cluster 11 taxa
                List<Node> hk = ch.getAllLeafNodes();
                List<String> ln = hk.stream().map(Node::getID).toList();
                assertFalse(ln.stream().anyMatch(s -> (s == null || s.trim().equals(""))),
                        "check HongKong cluster taxa name " + ln);
                assertTrue(ln.stream().allMatch(s -> s.contains("HongKong")),
                        "check HongKong cluster taxa name " + ln);
            } else {
                // not HongKong cluster
                Node goose96 = null;
                for (Node ch2 : ch.getChildren()) {
                    if (ch2.isLeaf())
                        goose96 = ch2;
                }
                assertNotNull(goose96, "Cannot find A_Goose_Guangdong_1_1996");
                assertEquals("A_Goose_Guangdong_1_1996", goose96.getID(), "A_Goose_Guangdong_1_1996");
            }
        }

        // check root meta data
        String rootMetaData = root.metaDataString;
        assertEquals("HongKong", root.getMetaData("location"), "location=HongKong");
        double prob = Double.parseDouble(root.getMetaData("location.prob").toString());
        assertTrue(prob > 0.4, "location.prob=" + prob);

        assertTrue(rootMetaData.contains("location.set=") && rootMetaData.contains("location.set.prob="),
                "location set");
        String locationSet = StringUtils.substringBetween(rootMetaData, "location.set={", "},");
        assertEquals("Guangdong,HongKong,Hunan,Guangxi,Fujian", locationSet,
                "location.set={" + locationSet + "}");

        String locationSetProb = StringUtils.substringBetween(rootMetaData, "location.set.prob={", "},");
        assertNotNull(locationSetProb, "location.set.prob={");
        double[] probArr = Arrays.stream(locationSetProb.split(","))
                .mapToDouble(Double::parseDouble).toArray();

        System.out.println("location.set = " + locationSet);
        System.out.println("location.set.prob = " + Arrays.toString(probArr));

        // Guangdong,HongKong,Hunan,Guangxi,Fujian
        assertEquals(5, probArr.length, "location.set.prob length");
        double sum = Arrays.stream(probArr).sum();
        assertEquals(1, sum, 1E-10, "sum prob");

        //HongKong prob is the biggest
        assertTrue(probArr[1] > 0.4 && 0.2 > probArr[0] && 0.2 > probArr[2] &&
                        0.2 > probArr[3] && probArr[1] > probArr[4],
                "HongKong prob=" + probArr[1] + ", Fujian prob=" + probArr[4]);
    }

    // TODO java.lang.NoClassDefFoundError: beastfx.app.tools.Application
    @Disabled
    void testTransitions() {
        File file = new File("h5n1.stc.out");
        assertTrue(file.exists(), "h5n1.stc.out");

        boolean startToCount = false;
        // Fujian=>Hunan  Guangdong=>Hunan  Guangxi=>Hunan  HongKong=>Hunan
        String[] direction = new String[4];
        int[] zero = new int[4];
        int[] one = new int[4];
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
                    // only select ?=>Hunan but not Hunan=>Hunan
                    if ( line.trim().toLowerCase().contains("=>hunan") &&
                            !line.trim().toLowerCase().startsWith("hunan=>") ) {
                        String[] elem = line.split("\t");
                        System.out.println(Arrays.toString(elem));
                        // at least 4 columns : Transition	0	1	2
                        assertTrue(elem.length > 2, "Transition array of " + elem[0]);

                        assertTrue(locId < 4, "locId = " + locId);

                        direction[locId] = elem[0];
                        zero[locId] = Integer.parseInt(elem[1]);
                        one[locId] = Integer.parseInt(elem[2]);
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

        // the direction having max transitions
        final String TO = "Guangxi=>Hunan";
        int maxId = getMaxId(zero);
        assertTrue(maxId >= 0 && maxId < 4, "0 transition : maxId = " + maxId);
        // not Guangxi=>Hunan
        assertNotEquals(TO, direction[maxId], "0 transition");

        maxId = getMaxId(one);
        assertTrue(maxId >= 0 && maxId < 4, "1 transition : maxId = " + maxId);
        // yes Guangxi=>Hunan
        assertEquals(TO, direction[maxId], "1 transition"); // uncertain as low ESS

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

//    @Test
//    void testMCCTree() {
//        File treeFile = new File("h5n1_with_trait.tree");
//        assertTrue(treeFile.exists(), "h5n1_with_trait.tree");
//
//        List<String> lines = null;
//        try {
//            lines = Files.readAllLines(treeFile.toPath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        assertNotNull(lines, "tree file lines");
//
//        boolean hasTree = false;
//        for (String line : lines) {
//        // tree TREE1 =
//        if (line.startsWith("tree")) {
//            int lastLeftSquareBracket = line.lastIndexOf("[");
//            int lastRightSquareBracket = line.lastIndexOf("]");
//            assertTrue(lastLeftSquareBracket > 10 && lastRightSquareBracket > lastLeftSquareBracket);
//
//            String rootMetaData = line.substring(lastLeftSquareBracket+1, lastRightSquareBracket);
//            System.out.println(rootMetaData);
//
//            String rootHeight = StringUtils.substringBetween(rootMetaData, "height=", ",");
//            assertNotNull(rootHeight, "height=");
//            double meanRootHeight = Double.parseDouble(rootHeight);
//            System.out.println("root height = " + meanRootHeight + ", psi.height mean = " + mean);
//            assertEquals(mean, meanRootHeight, 5.0, "root height");
//
//            String locationSet = StringUtils.substringBetween(rootMetaData, "location.set={", "},");
//            assertTrue(locationSet.startsWith("Guangdong,HongKong,Hunan,Guangxi,Fujian"),
//                    "location.set={" + locationSet + "}");
//
//            String locationSetProb = StringUtils.substringBetween(rootMetaData, "location.set.prob={", "},");
//            assertNotNull(locationSetProb, "location.set.prob={");
//            double[] probArr = Arrays.stream(locationSetProb.split(","))
//                    .mapToDouble(Double::parseDouble).toArray();
//
//            System.out.println("location.set = " + locationSet);
//            System.out.println("location.set.prob = " + Arrays.toString(probArr));
//
//            // Guangdong,HongKong,Hunan,Guangxi,Fujian
//            assertEquals(5, probArr.length, "location.set.prob length");
//            double sum = Arrays.stream(probArr).sum();
//            assertEquals(1, sum, 1E-10, "sum prob");
//
//            //HongKong prob is the biggest
//            assertTrue(probArr[1] > 0.4 && 0.2 > probArr[0] && 0.2 > probArr[2] &&
//                            0.2 > probArr[3] && probArr[1] > probArr[4],
//                    "HongKong prob=" + probArr[1] + ", Fujian prob=" + probArr[4]);
//
//            hasTree = true;
//        }
//        }
//        assertTrue(hasTree, "tree TREE1 = ");
//    }
}
