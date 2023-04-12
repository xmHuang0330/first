package fayi.tableObject;

import fayi.config.Enum.ACLevel;
import fayi.config.Enum.EmptyReason;
import fayi.config.Enum.QC;
import fayi.xml.Objects.GenoType;
import fayi.xml.Objects.LocusData;
import fayi.xml.Objects.QCWithLevel;
import lombok.Data;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class SnpLocusInfo extends LocusInfoImpl<SnpInfo>{

    public SnpLocusInfo(String locusName){
        super(locusName);
    }

    public SnpLocusInfo(LocusData locusData) {
        super( locusData );
    }

    public SnpLocusInfo(String locusName, ArrayList<QC> qualityControl, Integer forwardTotal, Integer reverseTotal, ArrayList<SnpInfo> allele, ArrayList<Double> IBObserving) {
        super( locusName, qualityControl, forwardTotal, reverseTotal, allele, IBObserving );
    }

    public SnpLocusInfo(String locusName, Integer forwardTotal, Integer reverseTotal) {
        super( locusName, forwardTotal, reverseTotal );
    }

    public Integer getTotalDepth() {
        return ForwardTotal + ReverseTotal;
    }

    public SnpLocusInfo() {
    }

    //获取qc
    public ArrayList<QCWithLevel> getQCWithLevel() {
        ArrayList<QCWithLevel> values = new ArrayList<>();
        for (QC qc : QualityControl) {
            if (QC.Allele_count.equals(qc)) {
                values.add(new QCWithLevel(qc.Name, acLevel.getShortName()));
            } else {
                values.add(new QCWithLevel(qc.Name));
            }
        }
        return values;
    }

    public String getQCAsString() {
        StringBuilder qcString = new StringBuilder();
        for (QC qc : QualityControl) {
            qcString.append(",").append(qc.Name);
        }
        return qcString.toString().replaceFirst(",", "");
    }


    //获取等位基因型，得到以逗号分隔的字符串
    public String getSnpAlleleAsString(Boolean mergeSameAlleleName) {
        if (mergeSameAlleleName) {
            return Allele.stream().map(SeqInfo::getAlleleName).collect(Collectors.joining(","));
        } else {
            return Allele.stream().map(SeqInfo::getAlleleName).distinct().collect(Collectors.joining(","));
        }
    }

    public ArrayList<GenoType> getSnpAlleleAsGenoTypes(){
        ArrayList<GenoType> genotypes = new ArrayList<>();
        for(SnpInfo snpInfo:Allele){
            genotypes.add(new GenoType("",snpInfo.AlleleName));
        }
        return genotypes;
    }


}
