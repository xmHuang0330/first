import fayi.config.Config;
import fayi.config.SnpMarker;
import fayi.tableObject.StrLocusInfo;
import fayi.tableObject.SampleInfo;
import fayi.tableObject.SeqInfo;
import fayi.tableObject.StrInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class statsticReport {

    public static void main(String[] args) {
        new statsticReport().flankingSnpTest(null);
    }

    private void flankingSnpTest(SampleInfo sampleInfo) {
        Config config = Config.getInstance();
        HashMap<String, ArrayList<SnpMarker>> commonSnpConfig = config.getCommonSnp();

        for (String locus : commonSnpConfig.keySet()) {

            for (SeqInfo seqInfo : sampleInfo.getStrLocusInfo().getOrDefault(locus, StrLocusInfo.getEmpty()).getAllele()) {
                StrInfo strInfo = (StrInfo) seqInfo;
                for (SnpMarker snpMarkerPattern : commonSnpConfig.get(locus)) {
                    switch (snpMarkerPattern.getPosition()) {
                        case right: {
                            String rightFlanking = strInfo.getRightFlanking();
                            if (null != rightFlanking && rightFlanking.length() > snpMarkerPattern.getEnd()) {
                                System.out.println(rightFlanking.substring(snpMarkerPattern.getStart(), snpMarkerPattern.getEnd()));
                            }
                            break;
                        }
                        case left: {
                            String leftFlanking = strInfo.getLeftFlanking();
                            if (null != leftFlanking && leftFlanking.length() > snpMarkerPattern.getEnd()) {
                                System.out.println(leftFlanking.substring(snpMarkerPattern.getStart(), snpMarkerPattern.getEnd()));
                            }
                            break;
                        }
                        case N: {
//                            String rightFlanking = strInfo.getRightFlanking();
//                            if(null != rightFlanking && rightFlanking.length() > snpPattern.getSpan()){
//                                System.out.println(rightFlanking.substring(snpPattern.getIndex(),snpPattern.getSpan()));
//                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}
