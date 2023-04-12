package fayi.tableObject;

import fayi.config.DefaultParam;
import fayi.config.SnpMarker;
import fayi.utils.SetAException;
import fayi.xml.Objects.Reads;
import fayi.xml.Objects.Site;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/*
    str序列信息
 */
@Slf4j
public class StrInfo extends SeqInfo implements Serializable {


    //对应的影子序列
    private final ArrayList<StrInfo> stutters = new ArrayList<>();
    //包含的核心序列个数
    private HashMap<Integer, Integer> coreSeqCount;
    //非核心序列
    private ArrayList<String> noneCoreseq = new ArrayList<>();
    private Boolean isStutter = false;
    private final ArrayList<StrInfo> NGSStutter = new ArrayList<>();
    private boolean isNGStutter;
    private String trimmedSeq;
    private StrInfo pair389I;
    private String originalSeq;
    private String Ns;
    private ArrayList<Flanking> flanking = new ArrayList<>();
    private ArrayList<SnpMarker> flankingSnp = new ArrayList<>();
    private String snpMarkerString;

    public StrInfo() {}

    public StrInfo(String Locus, String AlleleName, Boolean Typed, Integer Bases, String RepeatSequence, Integer Forward, Integer Reverse) {
        super(Locus, AlleleName, Typed, Bases, RepeatSequence, Forward, Reverse);
        this.originalSeq = RepeatSequence;
    }



    //xml的site转换为strinfo
    public StrInfo(Site site) {
        super(site.Locus, site.Genotype, "Yes".equals(site.Typed), site.Bases, site.RepeatSequence, (int) site.Reads.forward, (int) site.Reads.reverse);
        Ns = site.getNs();
        originalSeq = site.getOriginalSeq();
        this.snpMarkerString = site.getSnp();
        if (!Typed) {
            if ("stutter".equals(site.Typed)) {
                isStutter = true;
            } else if ("ngstutter".equals(site.Typed)) {
                isNGStutter = true;
            }
        }
    }

//    private void symbolToSnp(String snp) {
//        for(String singleSnp:snp.split(",")){
//            this.flankingSnp.add(new SnpMarker(SnpPosition.getByPosition(snp.substring(0,1)),))
//        }
//    }

    public StrInfo setAboveAT(boolean aboveAT) {
        this.aboveAT = aboveAT;
        return this;
    }
    public String getNs() {
        return Ns;
    }

    public void setNs(String ns) {
        Ns = ns;
    }

    public String getOriginalSeq() {
        return originalSeq;
    }

    public void setTrimmedSeq(String trimmedSeq) {
        this.trimmedSeq = trimmedSeq;
    }

    public String getTrimmedSeq() {
        return trimmedSeq;
    }

    //格式化为report excel的detail information
    public ArrayList<Object> formatAsList() {
        String typed = Typed ? "Yes" : "No";
        if (!Typed) {
            if (isStutter) {
                typed = "stutter";
            } else if (isNGStutter) {
                typed = "ngstutter";
            }
        }
        return new ArrayList<>(Arrays.asList(Locus, aliasSexLocus(), typed, Forward + Reverse, formatSnpAsString(), RepeatSequence));
    }

    //转换为xml的site对象
    public Site formatAsSite() {
        String typed = Typed ? "Yes" : "No";
        if (!Typed) {
            if (isStutter) {
                typed = "stutter";
            } else if (isNGStutter) {
                typed = "ngstutter";
            }
        }
        return new Site(Locus, Bases, AlleleName, typed, new Reads(Forward, Reverse, Forward + Reverse),
                RepeatSequence, getLeftFlanking(), getRightFlanking(), getFlankingSequence(), originalSeq, Ns, formatSnpAsString());
    }

    public String formatSnpAsString()  {
        String newString = flankingSnp.stream().map( SnpMarker::toMarker ).collect( Collectors.joining( ";" ));
        if(null != snpMarkerString){
            if(flankingSnp != null && flankingSnp.size() > 1){
                return newString;
            }
            return snpMarkerString;
        }
        return flankingSnp.size() > 0 ? flankingSnp.stream().map(SnpMarker::toMarker).collect(Collectors.joining(";")) : (flanking.size()<1?"":"REF");
    }

    public ArrayList<StrInfo> getStutters() {
        return stutters;
    }

    public void formatRepeatSequence(Boolean test) throws SetAException {
        StringBuilder format = new StringBuilder();
        StringBuilder reforge = new StringBuilder();
        for (int i = 0; i < noneCoreseq.size(); i++) {
            if ("".equals(noneCoreseq.get(i))) {
                continue;
            }
            format.append("[").append(noneCoreseq.get(i)).append("]").append(coreSeqCount.getOrDefault(i, 1)).append(" ");
            for (int j = 0; j < coreSeqCount.getOrDefault(i, 1); j++) {
                reforge.append(noneCoreseq.get(i));
            }
        }
        String s = reforge.toString().replaceAll("\\[", "").replaceAll("]", "").replaceAll(" ", "").toUpperCase(Locale.ROOT);
        if (trimmedSeq.equals(s)) {
            if (!test) {
                RepeatSequence = format.toString();
            }
        } else {
            throw new SetAException(5, "格式化后的序列与原始序列不一致:\n" + s + "|" + this);
        }
    }

    public ArrayList<String> getNoneCoreseq() {
        return noneCoreseq;
    }

    public void setNoneCoreseq(ArrayList<String> noneCoreseq) {
        this.noneCoreseq = noneCoreseq;
    }

    public void setIsStutter(boolean isStutter) {
        this.isStutter = isStutter;
    }

    public boolean getIsStutter() {
        return isStutter;
    }

    public ArrayList<StrInfo> getNGSStutter() {
        return NGSStutter;
    }

    public void setIsNGStutter(boolean isNGStutter) {
        this.isNGStutter = isNGStutter;
    }

    public boolean getIsNGStutter() {
        return isNGStutter;
    }

    public HashMap<Integer, Integer> getCoreSeqCount() {
        if (coreSeqCount == null) {
            coreSeqCount = new HashMap<>();
        }
        return coreSeqCount;
    }

    public void setCoreSeqCount(HashMap<Integer, Integer> coreSeqCount) {
        this.coreSeqCount = coreSeqCount;
    }

    @Override
    public String toString() {
        return "StrInfo{" +
                "Locus='" + Locus + '\'' +
                ", AlleleName='" + AlleleName + '\'' +
                ", Typed=" + Typed +
                ", originalSeq='" + originalSeq + '\'' +
                ", depth='" + getReads() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        StrInfo strInfo = (StrInfo) o;
        return originalSeq.equals(strInfo.originalSeq) && getFlanking().equals(strInfo.getFlanking());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), originalSeq);
    }

    public StrInfo getPair389I() {
        return pair389I;
    }

    public void setPair389I(StrInfo pair389I) {
        this.pair389I = pair389I;
    }

    public float getReadsWithNGS() {
        float depth = getReads();
        for (StrInfo strInfo : NGSStutter) {
            depth += strInfo.getReads();
        }
        return depth;
    }

    public ArrayList<Flanking> getFlanking() {
        if (flanking == null) {
            flanking = new ArrayList<>();
        }
        return flanking;
    }

    public void setFlanking(ArrayList<Flanking> flanking) {
        this.flanking = flanking;
    }

    public StrInfo(StrInfo strInfo) {

        this.coreSeqCount = strInfo.getCoreSeqCount();
        this.noneCoreseq = strInfo.getNoneCoreseq();
        this.trimmedSeq = strInfo.trimmedSeq;
        this.pair389I = strInfo.pair389I;
        this.originalSeq = strInfo.originalSeq;
        Ns = strInfo.getNs();
    }

    public String getFlankingSequence() {
        return getFlanking().size() > 0 ? flanking.get(0).getSequence() : null;
    }

    public String getLeftFlanking() {
        return getFlanking().size() > 0 ? flanking.get(0).getLeftSequence() : "";
    }

    public String getRightFlanking() {
        return getFlanking().size() > 0 ? flanking.get(0).getRightSequence() : "";
    }

    public Collection<SnpMarker> getFlankingSnp() {
        if (flankingSnp == null) flankingSnp = new ArrayList<>();
        return flankingSnp;
    }

    public String reverseComplementOrigSeq() {
        StringBuilder revComp = new StringBuilder();
        for(int i = originalSeq.length()-1 ; i >= 0 ; i--){

            revComp.append(DefaultParam.revCompGeno.get(String.valueOf(originalSeq.charAt(i))));
        }
        return revComp.toString();
    }
}
