package engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.TermIdRecord;
import util.MiscUtil;
import util.QuickScan;

/**
 *
 * @author hkhoi
 */
public class SearchEngine {

    private static final String DATA_DIR = "C50_train_combine";

    private static final String STOP_WORDS_NAME = "stopwords.txt";

    private static final String REGEX = "[^A-Za-z']";

    private static final String REGEX_POSTING = "[^0-9']";
    
    private static final String REGEX_SPIMI_POSTING = "[^a-z0-9']";

    private static int termIdCount = 0;

    private static int blockCount = 0;

    private static int blockSPIMICount = 0;

    private static int blockSPIMITermCount = 0;

    private static final String BLOCK_PREFIX = "FILE_RAW_";

    private static final String SPIMI_BLOCK_PREFIX = "FILE_SPIMI_RAW_";

    private static final String MERGED_FILE = "MERGED_FILE";
    
    private static final String MERGED_SPIMI_FILE = "MERGEDS_SPIMI_FILE";

    private static final String TERM_ID_MAP_FILE = "TERM_ID_MAP";

    private static final int BUFFER = 20_000;

    private static final List<TermIdRecord> BLOCK = new ArrayList<>();

    private static final HashMap<String, BitSet> BLOCK_SPIMI = new HashMap<>(BUFFER);

    private final HashSet<String> stopWordSet;

    private final HashMap<String, Integer> termIdMap;

    private final File[] dataFiles;

    public SearchEngine() {
        System.out.println("> STATUS: Initializing engine");
        stopWordSet = new HashSet<>();
        loadStopWords();

        dataFiles = (new File(DATA_DIR)).listFiles();
        termIdMap = new HashMap<>();
        System.out.println("> STATUS: Ready to start!");
    }

    public void BSBConstruction() {

        System.out.println("> STATUS: Buidling BSB, please wait...");

        for (int docId = 0; docId < dataFiles.length; ++docId) {
            try {
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
                            if (!normalized.isEmpty()
                                    && normalized.charAt(0) == '\'') {
                                normalized = normalized.substring(1);
                            }
                            if (!normalized.isEmpty()
                                    && normalized.charAt(normalized.length() - 1) == '\'') {
                                normalized
                                        = normalized.substring(0, normalized.length() - 1);
                            }

                            if (!normalized.isEmpty()
                                    && !stopWordSet.contains(normalized)) {
                                int termId = getTermId(normalized);
                                BLOCK.add(new TermIdRecord(termId, docId));
                                if (BLOCK.size() >= BUFFER) {
                                    dumpBlock();
                                }
                            }
                        }
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        dumpBlock();
        mergeBlocks();
        exportTermIdMap();

        System.out.println("> STATUS: Done, number of blocks: " + blockCount + ", buffer = " + BUFFER);
    }

    public void SPIMIConstruction() {
        System.out.println("> STATUS: Buidling SPIMI, please wait...");

        for (int docId = 0; docId < dataFiles.length; ++docId) {
            try {
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
                            if (!normalized.isEmpty()
                                    && normalized.charAt(0) == '\'') {
                                normalized = normalized.substring(1);
                            }
                            if (!normalized.isEmpty()
                                    && normalized.charAt(normalized.length() - 1) == '\'') {
                                normalized
                                        = normalized.substring(0, normalized.length() - 1);
                            }

                            if (!normalized.isEmpty()
                                    && !stopWordSet.contains(normalized)) {
                                if (!BLOCK_SPIMI.containsKey(normalized)) {
                                    BLOCK_SPIMI.put(normalized, new BitSet());
                                }
                                BLOCK_SPIMI.get(normalized).set(docId);
                                ++blockSPIMITermCount;

                                if (blockSPIMITermCount >= BUFFER) {
                                    dumpSPIMIBlock();
                                }
                            }
                        }
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        dumpSPIMIBlock();
        mergeSPIMIBlocks();
        System.out.println("> STATUS: Done, number of blocks: " + blockSPIMICount + ", buffer = " + BUFFER);
    }

    private void loadStopWords() {
        System.out.println("> STATUS: Loading stopwords");
        try {
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
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("> STATUS: Done, number of stopwords: " + stopWordSet.size());
    }

    public HashSet<String> getStopWordSet() {
        return stopWordSet;
    }

    public File[] getDataFiles() {
        return dataFiles;
    }

    /* TEST */
    private int getTermId(String normalized) {
        if (termIdMap.containsKey(normalized)) {
            return termIdMap.get(normalized);
        } else {
            termIdMap.put(normalized, termIdCount);
            return termIdCount++;
        }
    }

    private void dumpBlock() {
        System.out.println("> STATUS: buffer is full, dumping to disk...");
        String fileName = BLOCK_PREFIX + blockCount++;
        TreeMap<Integer, BitSet> invertedIndice = new TreeMap<>();

        for (TermIdRecord it : BLOCK) {
            if (!invertedIndice.containsKey(it.getTermId())) {
                invertedIndice.put(it.getTermId(), new BitSet());
            }
            invertedIndice.get(it.getTermId()).set(it.getDocId());
        }

        BLOCK.clear();

        MiscUtil.exportMap(invertedIndice, fileName);

        System.out.println("> STATUS: buffer dumped to file: " + fileName);
    }

    private void mergeBlocks() {
        System.out.println("> STATUS: Merging blocks...");
        TreeMap<Integer, BitSet> invertedIndice = new TreeMap<>();
        for (int i = 0; i < blockCount; ++i) {
            try {
                String fileName = BLOCK_PREFIX + i;
                QuickScan scan = new QuickScan(new FileInputStream(fileName));
                boolean end = false;
                while (!end) {
                    String curLine = scan.nextLine();
                    if (curLine == null) {
                        end = true;
                    } else {
                        String[] tokens = curLine.split(REGEX_POSTING);

                        int termId = Integer.parseInt(tokens[0]);
                        if (!invertedIndice.containsKey(termId)) {
                            invertedIndice.put(termId, new BitSet());
                        }
                        for (int j = 1; j < tokens.length; ++j) {
                            try {
                                int docId = Integer.parseInt(tokens[j]);
                                invertedIndice.get(termId).set(docId);
                            } catch (NumberFormatException e) {
                                // Simply ignore that shit
                            }
                        }
                    }
                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MiscUtil.exportMap(invertedIndice, MERGED_FILE);
        System.out.println("> STATUS: Merged!, Dumped to file: " + MERGED_FILE);
    }

    private void exportTermIdMap() {
        try (PrintWriter out = new PrintWriter(new FileOutputStream(TERM_ID_MAP_FILE, false))) {
            for (Map.Entry<String, Integer> it : termIdMap.entrySet()) {
                out.println(it.getKey() + " " + it.getValue());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void dumpSPIMIBlock() {
        String fileName = SPIMI_BLOCK_PREFIX + blockSPIMICount++;
        System.out.println("> STATUS: Buffer is full, dumping to disk");

        try (PrintWriter out = new PrintWriter(new FileOutputStream(fileName, false))) {
            for (Map.Entry<String, BitSet> it : BLOCK_SPIMI.entrySet()) {
                out.println(it.getKey() + " " + it.getValue());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        }

        blockSPIMITermCount = 0;
        BLOCK_SPIMI.clear();
        System.out.println("> STATUS: buffer dumped to file: " + fileName);
    }

    private void mergeSPIMIBlocks() {
        System.out.println("> STATUS: Merging SPIMI blocks...");
        HashMap<String, BitSet> invertedIndice = new HashMap<>();
        for (int i = 0; i < blockSPIMICount; ++i) {
            try {
                String fileName = SPIMI_BLOCK_PREFIX + i;
                QuickScan scan = new QuickScan(new FileInputStream(fileName));
                boolean end = false;
                while (!end) {
                    String curLine = scan.nextLine();
                    if (curLine == null) {
                        end = true;
                    } else {
                        String[] tokens = curLine.split(REGEX_SPIMI_POSTING);

                        String term = tokens[0];
                        if (!invertedIndice.containsKey(term)) {
                            invertedIndice.put(term, new BitSet());
                        }
                        for (int j = 1; j < tokens.length; ++j) {
                            try {
                                int docId = Integer.parseInt(tokens[j]);
                                invertedIndice.get(term).set(docId);
                            } catch (NumberFormatException e) {
                                // Simply ignore that shit
                            }
                        }
                    }
                }

            } catch (FileNotFoundException ex) {
                Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(SearchEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        MiscUtil.exportSPIMIMap(invertedIndice, MERGED_SPIMI_FILE);
        System.out.println("> STATUS: Merged!, Dumped to file: " + MERGED_SPIMI_FILE);
    }
}
