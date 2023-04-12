package fayi.tableObject;

import fayi.config.Enum.QC;
import fayi.xml.Objects.LocusData;

import java.util.ArrayList;

public class MHLocusInfo extends LocusInfoImpl<MHInfo> {


    public MHLocusInfo() {
    }

    public MHLocusInfo(String locus) {
        super( locus );
    }

    public MHLocusInfo(LocusData locusData) {
        super( locusData );
    }

    public MHLocusInfo(String locusName, ArrayList<QC> qualityControl, Integer forwardTotal, Integer reverseTotal, ArrayList<MHInfo> allele, ArrayList<Double> IBObserving) {
        super( locusName, qualityControl, forwardTotal, reverseTotal, allele, IBObserving );
    }

    public MHLocusInfo(String locusName, Integer forwardTotal, Integer reverseTotal) {
        super( locusName, forwardTotal, reverseTotal );
    }


}
