package fayi.tableObject;

import fayi.config.Enum.ACLevel;
import fayi.config.Enum.EmptyReason;
import fayi.config.Enum.QC;
import fayi.xml.Objects.LocusData;
import fayi.xml.Objects.QCWithLevel;
import lombok.Data;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

@Data
public class LocusInfoImpl<SI extends fayi.tableObject.SeqInfo> {

    protected String LocusName = "";
    //位点qc
    protected ArrayList<QC> QualityControl = new ArrayList<>();
    //总深度
    protected Integer ForwardTotal = 0;
    protected Integer ReverseTotal = 0;
    // 等位基因
    protected ArrayList<SI> Allele = new ArrayList<>();
    protected ArrayList<Double> IBObserving = new ArrayList<>();
    protected ACLevel acLevel = ACLevel.Low;
    protected int AlleleCount;
    protected EmptyReason emptyReason;

    public LocusInfoImpl(){}
    public LocusInfoImpl(String locus) {
        this.LocusName = locus;
    }

    public LocusInfoImpl(LocusData locusData){
        LocusName = locusData.getLocusName();
        for (QCWithLevel qcWithLevel : locusData.getQualityControl()) {
            for (QC qc : QC.values()) {
                if (qcWithLevel.getName().equals(qc.Name)) {
                    QualityControl.add(qc);
                }
            }
        }
        IBObserving = locusData.getIbo();
        //
        ForwardTotal = locusData.getTotalDepth();
    }

    public LocusInfoImpl(String locusName, ArrayList<QC> qualityControl, Integer forwardTotal, Integer reverseTotal, ArrayList<SI> allele, ArrayList<Double> IBObserving) {
        LocusName = locusName;
        QualityControl = qualityControl;
        ForwardTotal = forwardTotal;
        ReverseTotal = reverseTotal;
        Allele = allele;
        this.IBObserving = IBObserving;
    }

    public LocusInfoImpl(String locusName, Integer forwardTotal, Integer reverseTotal) {
        LocusName = locusName;
        ForwardTotal = forwardTotal;
        ReverseTotal = reverseTotal;
    }

    public String getAlleleNameAsString(Boolean mergeSameAlleleName,Boolean useAlias) {

//        if(Allele.size() < 1){
//            return emptyReason==null?"":emptyReason.name();
//        }
        try {
            Allele.sort( Comparator.comparing( o ->
                    Float.parseFloat( "X".equals( o.AlleleName ) ? "0" :
                            "Y".equals( o.AlleleName ) ? "1" : o.AlleleName ) ) );
        }catch (NumberFormatException e){
            // setC?
            if(!Allele.get( 0 ).getAlleleName().contains( "-" )){
                throw  e;
            }
        }
        if (mergeSameAlleleName) {
            if (useAlias && (LocusName.equals( "SRY" ) || "Y-indel".equals( LocusName ))) {
                return Allele.size() > 0 ? "Y" : "";
            } else {
                return Allele.stream().map( useAlias ? SeqInfo::getExcelName : SeqInfo::getAlleleName ).distinct().collect( Collectors.joining( "," ) );
            }
        } else {
            if (useAlias && (LocusName.equals( "SRY" ) || "Y-indel".equals( LocusName ))) {
                return Allele.size() > 0 ? "Y" : "";
            } else {
                return Allele.stream().map(useAlias ? SeqInfo::getExcelName : SeqInfo::getAlleleName).collect(Collectors.joining(","));
            }
        }
    }

    public String getQCAsString() {
        StringBuilder qcString = new StringBuilder();
        for (QC qc : QualityControl) {
            qcString.append(",").append(qc.Name);
        }
        return qcString.toString().replaceFirst(",", "");
    }

    public String getSequenceAsString() {
        return Allele.stream().map(SeqInfo::getRepeatSequence).distinct().collect(Collectors.joining(","));
    }

    //获取等位基因型
    public ArrayList<String> getAlleleNameAsStringList() {
        ArrayList<String> alleleString = new ArrayList<>();
        Allele.sort(Comparator.comparing(SeqInfo::getReads).reversed());
        for (SeqInfo si : Allele) {
            alleleString.add(si.getAlleleName());
        }

        return alleleString;
    }
    public int getReads(){
        return ForwardTotal + ReverseTotal;
    }
}
