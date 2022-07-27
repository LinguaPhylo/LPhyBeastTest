package lphybeast.tutorial;

import beast.util.LogAnalyser;
import org.junit.jupiter.api.BeforeEach;
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
        logAnalyser = new LogAnalyser("h5n1.log");
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
        assertEquals(-119, mean, 10.0, "prior");

        mean = logAnalyser.getMean("D.treeLikelihood");
        assertEquals(-5785.50, mean, 20.0, "D_trait.treeLikelihood");

        mean = logAnalyser.getMean("Theta");
        assertEquals(7.16, mean, 0.5, "Theta");

//        mean = logAnalyser.getMean("mu_trait"); //low ESS
//        assertEquals(8, mean, 2.0, "psi.mu_trait ");

        mean = logAnalyser.getMean("psi.height");
        assertEquals(10.85, mean, 1.0, "psi.height");

        mean = logAnalyser.getMean("D_trait.treeLikelihood");
        assertEquals(-50, mean, 5.0, "D_trait.treeLikelihood");
    }

    @Test
    void assertTransitions() {
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
