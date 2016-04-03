package model;

/**
 *
 * @author hkhoi
 */
public class TermIdRecord implements Comparable<TermIdRecord>{
   
    private final int termId;
   
    private final int docId;

    public TermIdRecord(int termId, int docId) {
        this.termId = termId;
        this.docId = docId;
    }

    public int getTermId() {
        return termId;
    }

    public int getDocId() {
        return docId;
    }

    @Override
    public String toString() {
        return termId + " " + docId;
    }

    @Override
    public int compareTo(TermIdRecord o) {
        return termId - o.termId;
    }
}
