package fayi.xml.Objects;

import fayi.config.Enum.EmptyReason;
import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.stream.Collectors;

/*
    位点详细信息
    qc，总深度，基因型
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "locusData")
@AllArgsConstructor
@NoArgsConstructor
public class LocusData {

    @XmlAttribute(name = "Locus")
    private String LocusName = "";
    @XmlAttribute(name = "ibo")
    private ArrayList<Double> ibo = new ArrayList<>();

    @XmlElementWrapper(name = "QCs")
    @XmlElement(name = "QC")
    private ArrayList<QCWithLevel> QualityControl = new ArrayList<>();
    @XmlElement(name = "TotalDepth")
    private Integer TotalDepth = 0;

    @XmlElementWrapper(name = "Genotypes")
    private ArrayList<GenoType> genotype = new ArrayList<>();

    @XmlElementWrapper(name = "RepeatSequence")
    private ArrayList<RepeatSequence> repeatSequences = new ArrayList<>();

    public boolean contains(String s) {
        for (GenoType genoType :genotype
        ) {
            if (s.equals(genoType.genotype)) {
                return true;
            }
        }
        return false;
    }


    @XmlElement(name = "DropReason")
    private EmptyReason emptyReason;

    public LocusData(String locusName, ArrayList<QCWithLevel> qualityControl, Integer totalDepth, ArrayList<GenoType> genotype, EmptyReason emptyReason,ArrayList<RepeatSequence> repeatSequences) {
        LocusName = locusName;
        QualityControl = qualityControl;
        TotalDepth = totalDepth;
        this.genotype = genotype;
        this.emptyReason = emptyReason;
        this.repeatSequences = repeatSequences;
    }

    @Override
    public String toString() {
        return "LocusData{" +
                "LocusName='" + LocusName + '\'' +
                ", QualityControl=" + QualityControl +
                ", TotalDepth=" + TotalDepth +
                ", Allele=" + genotype +
                '}';
    }
    public String getAlleleNameAsString() {
        return genotype.stream().map(GenoType::getGenotype).collect(Collectors.joining(","));

    }
    public String getRepeatSequence() {
        return repeatSequences.stream().map(RepeatSequence::getRepeatSequence).collect(Collectors.joining(","));

    }

}
