package model;

/**
 *
 * @author hkhoi
 */
public class Record {
   
    private final int termId;
   
    private final int docId;

    public Record(int termId, int docId) {
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
}
