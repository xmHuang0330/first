package fayi.tableObject;

import fayi.config.Config;
import fayi.config.Enum.EmptyReason;
import fayi.config.Enum.QC;
import fayi.config.Enum.ACLevel;
import fayi.config.Param;
import fayi.xml.Objects.GenoType;
import fayi.xml.Objects.LocusData;
import fayi.xml.Objects.QCWithLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.xmlbeans.impl.xb.xsdschema.All;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/*
 * 位点信息
 */

@AllArgsConstructor
public class StrLocusInfo extends LocusInfoImpl<StrInfo> {


    public StrLocusInfo(String locusName) {
        super(locusName);
    }

    public StrLocusInfo(LocusData locusData){
        super(locusData);
    }

    public StrLocusInfo(String locusName, ArrayList<QC> qualityControl, Integer forwardTotal, Integer reverseTotal, ArrayList<StrInfo> allele, ArrayList<Double> IBObserving) {
        super(locusName, qualityControl, forwardTotal,reverseTotal,allele,IBObserving);
    }

    public StrLocusInfo(String locusName, Integer forwardTotal, Integer reverseTotal) {
        super(locusName,forwardTotal,reverseTotal);
    }

    private static final StrLocusInfo empty = new StrLocusInfo();
    public static StrLocusInfo getEmpty() {
        return empty;
    }

    //深度累加
    public void setForwardTotal(Integer depth) {
        ForwardTotal += depth;
    }
    public void setReverseTotal(Integer depth){
        ReverseTotal += depth;
    }

    public Integer getTotalDepth() {
        return ForwardTotal + ReverseTotal;
    }

    //获取等位基因型，得到以逗号分隔的字符串


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

    //获取qc简称，以字符串形式，无间隔
    public String getQCAsIndictor() {
        StringBuilder IndictorString = new StringBuilder();
        for (QC qc: QualityControl) {
            IndictorString.append(qc.Indictor);
        }
        return IndictorString.toString();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (SeqInfo strInfo : Allele) {
            result.append(String.format("%s\t%s\t%s\t%s\n", strInfo.Locus, strInfo.AlleleName, strInfo.Reverse + strInfo.getForward(), strInfo.RepeatSequence));
        }
        return result.toString();
    }

    public double getTypedDepth() {
        double typedDepth = 0d;
        for (StrInfo strInfo : Allele) {
            for (StrInfo ngsStutter : strInfo.getNGSStutter()) {
                typedDepth += ngsStutter.getReads();
            }
            typedDepth += strInfo.getReads();
        }
        return typedDepth;
    }

    public ArrayList<GenoType> getAlleleNameAsGenoType() {
        ArrayList<GenoType> genotypes = new ArrayList<>();
        for(StrInfo strInfo:Allele){
            genotypes.add(new GenoType(strInfo.formatSnpAsString(),strInfo.AlleleName));
        }
        return genotypes;
    }

    public boolean depthCheck(Double dp){

        if (dp == null){
            dp = Param.getInstance().getSingleLimit( LocusName, Double.parseDouble(Allele.get(0).getAlleleName() ), Config.getInstance().getNoFilter(), false );
        }

        double typedDepth = 0d;
        for (StrInfo strInfo : Allele) {
            for (StrInfo ngsStutter : strInfo.getNGSStutter()) {
                typedDepth += ngsStutter.getReads();
            }
            typedDepth += strInfo.getReads();
        }
        Allele.sort(Comparator.comparingDouble(o -> Double.parseDouble(o.getAlleleName())));

        return typedDepth < dp;
    }
}
