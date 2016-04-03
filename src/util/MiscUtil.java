package util;

import engine.SearchEngine;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hkhoi
 */
public class MiscUtil {

    public static String printBitSet(BitSet b) {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < b.length(); ++i) {
            if (b.get(i)) {
                result.append(i).append(' ');
            }
        }
        return result.toString();
    }

    public static void exportMap(TreeMap<Integer, BitSet> invertedIndice, String fileName) {
        try (PrintWriter out = new PrintWriter(new FileOutputStream(fileName), false)) {
            for (Map.Entry<Integer, BitSet> it : invertedIndice.entrySet()) {
                out.println(it.getKey() + " " + it.getValue());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void exportSPIMIMap(HashMap<String, BitSet> invertedIndice, String fileName) {
        try (PrintWriter out = new PrintWriter(new FileOutputStream(fileName), false)) {
            for (Map.Entry<String, BitSet> it : invertedIndice.entrySet()) {
                out.println(it.getKey() + " " + it.getValue());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
