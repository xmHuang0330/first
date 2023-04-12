package fayi.tableObject;

import fayi.xml.Objects.Reads;
import fayi.xml.Objects.Site;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

/*
    snp序列信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SnpInfo extends SeqInfo {

    public SnpInfo setAboveAT(boolean aboveAT) {
        this.aboveAT = aboveAT;
        return this;
    }

    public SnpInfo(String Locus, String AlleleName, Boolean Typed, Integer Bases, String RepeatSequence, int Forward, int Reverse) {
        super(Locus, AlleleName, Typed, Bases, RepeatSequence, Forward, Reverse);
    }

    //xml的site对象转换为snpinfo
    public SnpInfo(Site site) {
        super(site.Locus, site.Genotype, "Yes".equals(site.Typed), site.Bases, site.RepeatSequence, (int) site.Reads.forward, (int) site.Reads.reverse);
    }



}
