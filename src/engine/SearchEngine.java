package engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import model.Record;
import util.QuickScan;

/**
 *
 * @author hkhoi
 */
public class SearchEngine {

    private static final String DATA_DIR = "C50_train_combine";

    private static final String STOP_WORDS_NAME = "stopwords.txt";

    private static final String REGEX = "[^A-Za-z']";

    private static final int BUFFER = 100;

    private static final List<Record> BLOCK = new ArrayList<>(BUFFER);
    
    private final HashSet<String> stopWordSet;

    private final HashMap<String, Integer> termIdMap;

    private final File[] dataFiles;

    public SearchEngine() throws IOException {
        stopWordSet = new HashSet<>();
        loadStopWords();

        dataFiles = (new File(DATA_DIR)).listFiles();
        termIdMap = new HashMap<>();
    }

    public void BSBConstruction() throws FileNotFoundException, IOException {
        
        for (int docId = 0; docId < dataFiles.length; ++docId) {
            File itFile = dataFiles[docId];
            QuickScan scan = new QuickScan(new FileInputStream(itFile));
            boolean stop = false;
            while (!stop) {
                String curLine = scan.nextLine();
                if (curLine == null) {
                    stop = true;
                } else {
                    for (String token : curLine.split(REGEX)) {
                        String normalized = token.toLowerCase();
                        if (!normalized.isEmpty() && normalized.charAt(0) == '\'') {
                            normalized = normalized.substring(1);
                        }
                        if (!normalized.isEmpty() && normalized.charAt(normalized.length() - 1) == '\'') {
                            normalized = normalized.substring(0, normalized.length() - 1);
                        }

                        if (!normalized.isEmpty() && !stopWordSet.contains(normalized)) {
                            int termId = getTermId(normalized);
                            BLOCK.add(new Record(termId, docId));
                            if (BLOCK.size() >= BUFFER) {
                                dumpBlock();
                            }
                        }
                    }
                }
            }
        }
        dumpBlock();
        mergeBlocks();
        exportTermIdMap();
    }

    private void loadStopWords() throws FileNotFoundException, IOException {
        QuickScan scan = new QuickScan(new FileInputStream(STOP_WORDS_NAME));
        boolean stop = false;
        while (!stop) {
            String curLine = scan.next();
            if (curLine != null) {
                stopWordSet.add(curLine);
            } else {
                stop = true;
            }
        }
    }

    public HashSet<String> getStopWordSet() {
        return stopWordSet;
    }

    public File[] getDataFiles() {
        return dataFiles;
    }

    /* TEST */

    private int getTermId(String normalized) {
        return 0;
    }

    private void dumpBlock() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void mergeBlocks() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void exportTermIdMap() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
