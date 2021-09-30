package ChaosAI.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Writes a tree beginning at the given root node
 * to a file in the DOT language.
 * http://www.graphviz.org/Documentation.php
 */
public class TreeWriter {
    private Node root;
    private int branchingFactor;
    private BufferedWriter w;

    public static String[] actions = new String[] {"DOWN", "UP", "LEFT", "RIGHT", "USE", "ESCAPE"};

    public TreeWriter(Node root) {
        this.root = root;
        this.branchingFactor = root.getChildren() != null ? root.getChildren().length : 0;

        try {
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.GERMANY);
            this.w = new BufferedWriter(new FileWriter(new File(sdf.format(d) + "_tree.txt")));
            w.write("strict graph {\n");
            this.recursiveGraph(this.root);
            for(int i=0; i<branchingFactor; i++) {
                w.write("N"+Integer.toHexString(this.root.hashCode()) + " -- N" + Integer.toHexString(this.root.getChildren()[i].hashCode())
                        + " [label="+actions[i]+"]\n");
            }
            w.write("}");
            w.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void recursiveGraph(Node n) throws IOException {
        w.write("N"+Integer.toHexString(n.hashCode()) + " -- {");
        for(int i=0; i<branchingFactor; i++) {
            if(i != 0 && n.getChildren() != null && n.getChildren().length > 0 && n.getChildren()[i] != null)
                w.write(", ");
            if(n.getChildren() != null && n.getChildren().length > 0 && n.getChildren()[i] != null) {
                w.write("N"+Integer.toHexString(n.getChildren()[i].hashCode()));
            }
        }
        w.write("}\n");
        for(int i=0; i<branchingFactor; i++) {
            if(n.getChildren() != null && n.getChildren().length > 0 && n.getChildren()[i] != null) {
                recursiveGraph(n.getChildren()[i]);
            }
        }
    }
}
