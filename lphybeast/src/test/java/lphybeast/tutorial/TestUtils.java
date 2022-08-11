package lphybeast.tutorial;

import beast.evolution.tree.Node;
import beast.evolution.tree.Tree;
import beast.util.NexusParser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Walter Xie
 */
public class TestUtils {

    public static final int BURNIN_PERC = 20;

    public static Tree assertMCCTree(String tree, double meanTreeHeight) {
        File file = new File(tree);
        assertTrue(file.exists(), tree + " file exists");

        NexusParser nexusParser = new NexusParser();
        try {
            nexusParser.parseFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Tree> parsedTrees = nexusParser.trees;
        assertEquals(1, parsedTrees.size(), "MCC tree");

        Tree mcc = parsedTrees.get(0);
//        System.out.println("\nRSV2 MCC tree = " + mcc);

        Node root = mcc.getRoot();
        double meanRootHeight = root.getHeight();

        System.out.println("root height = " + meanRootHeight + ", psi.height mean = " + meanTreeHeight);
        assertEquals(meanTreeHeight, meanRootHeight, 5.0, "root height");
        return mcc;
    }


}
