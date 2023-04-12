package fayi.xml.Objects;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.stream.Collectors;


/*
    位点详细信息，只包括高于AT的
 */
@Data
@XmlRootElement(name = "site")
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor
public class Site implements Comparable<Site> {
    @Override
    public int compareTo(Site o) {

        if (!this.Locus.equals(o.Locus) ||this.Locus.equals("Y-indel") || this.Locus.equals("Amelogenin")) {
            return 0;
        }
        float aFloat = Float.parseFloat(this.Genotype);
        float aFloat1 = Float.parseFloat(o.Genotype);

        return Float.compare(aFloat, aFloat1);
    }

    @XmlAttribute
    public String Locus;

    @XmlAttribute(name = "product_size")
    public Integer Bases;
    //
//    @XmlAttribute
//    public Boolean AboveAT;
    @XmlElement
    public String Genotype;

    @XmlElement
    public String Typed;

    @XmlElement
    public Reads Reads;

    @XmlElement
    public String RepeatSequence;
    @XmlElement
    private String leftFlanking;
    @XmlElement
    private String rightFlanking;
    @XmlElement
    private String flankingSeq;
    @XmlElement
    private String originalSeq;

    @XmlElement
    private String Ns;

    @XmlElement
    private String snp;

    public Site(String locus, Integer bases, String genotype, String typed, fayi.xml.Objects.Reads reads, String repeatSequence) {
        Locus = locus;
        Bases = bases;
        Genotype = genotype;
        Typed = typed;
        Reads = reads;
        RepeatSequence = repeatSequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Site site = (Site) o;
        return Objects.equals(Genotype, site.Genotype);
    }

}
