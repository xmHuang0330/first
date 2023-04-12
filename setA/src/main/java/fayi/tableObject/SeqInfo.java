package fayi.tableObject;

import fayi.xml.Objects.Reads;
import fayi.xml.Objects.Site;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

/*
    out文件条目信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeqInfo implements Serializable {
    protected String Locus;
    protected String AlleleName;
    protected String ExcelName;
    protected Boolean Typed;
    protected Integer Bases;
    protected String RepeatSequence;
    protected int Forward = 0;
    protected int Reverse = 0;
    boolean aboveAT = false;
    protected boolean aboveIT = false;
    private boolean tempTyped;

    public SeqInfo setAboveAT(boolean aboveAT) {
        this.aboveAT = aboveAT;
        return this;
    }

    public String getExcelName() {
        if (ExcelName == null) {
            return AlleleName;
        }
        return ExcelName;
    }

    public SeqInfo(String locus, String alleleName, Boolean typed, Integer bases, String repeatSequence, int forward, int reverse) {
        Locus = locus;
        AlleleName = alleleName;
        Typed = typed;
        Bases = bases;
        RepeatSequence = repeatSequence;
        Forward = forward;
        Reverse = reverse;
    }

    //深度累加
    public void addForward(Double forward) {
        Forward += forward;
    }

    public void addReverse(Double reverse) {
        Reverse += reverse;
    }
    //总深度
    public float getReads() {
        return Forward + Reverse;
    }

    //格式化为report excel的detail information
    public String aliasSexLocus() {
        String allele = AlleleName;
        if ("Amelogenin".equals(Locus)) {
            switch (AlleleName) {
                case "1":
                    allele = "Y";
                    break;
                case "0":
                    allele = "X";
                    break;
            }
        }else if("SRY".equals( Locus )){
            if("1".equals( AlleleName )){
                allele = "Y";
            }
        } else if ("Y-indel".equals(Locus)) {
            allele = "Y";
        }
        return allele;
    }

    public void setTempTyped(boolean tempTyped) {
        this.tempTyped = tempTyped;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeqInfo)) return false;
        SeqInfo seqInfo = (SeqInfo) o;
        return getLocus().equals(seqInfo.getLocus()) && getAlleleName().equals(seqInfo.getAlleleName()) && getRepeatSequence().equals(seqInfo.getRepeatSequence());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocus(), getAlleleName(), getRepeatSequence());
    }
    //格式化输出为report excel 中的 detail information
    public ArrayList<Object> FormatAsList(float meanDepth) {
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(Locus);
        objects.add(AlleleName);
        objects.add(Typed);
        objects.add((Forward+Reverse) / meanDepth < 0.015?0:Forward+Reverse);
        objects.add( RepeatSequence );
        return objects;
    }
    //转换为xml文件对象
    public Site formatAsSite(){
        return new Site(Locus,Bases,AlleleName, Typed ?"Yes":"No", new Reads(Forward,Reverse,Forward+Reverse),"" );
    }
}
