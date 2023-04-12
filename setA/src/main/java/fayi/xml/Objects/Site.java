package fayi.xml.Objects;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/*
    位点详细信息，只包括高于AT的
 */
@XmlRootElement(name = "site")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Site {

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

}
