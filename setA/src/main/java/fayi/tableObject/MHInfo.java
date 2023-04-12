package fayi.tableObject;

import java.util.HashMap;

public class MHInfo extends SeqInfo{


    HashMap<Integer,String[]> snp = new HashMap<>();

    public void setSnp(HashMap<Integer, String[]> snp) {
        this.snp = snp;
    }

    public MHInfo(String locus, String alleleName, Boolean typed, Integer bases, String repeatSequence, int forward, int reverse) {
        super( locus, alleleName, typed, bases, repeatSequence, forward, reverse );

    }

}
