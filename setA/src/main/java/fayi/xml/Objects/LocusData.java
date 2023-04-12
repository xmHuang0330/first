package fayi.xml.Objects;

import fayi.config.Enum.EmptyReason;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
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

    @XmlElement(name = "DropReason")
    private EmptyReason emptyReason;

    public LocusData(String locusName, ArrayList<QCWithLevel> qualityControl, Integer totalDepth, ArrayList<GenoType> genotype, EmptyReason emptyReason) {
        LocusName = locusName;
        QualityControl = qualityControl;
        TotalDepth = totalDepth;
        this.genotype = genotype;
        this.emptyReason = emptyReason;
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
        StringBuilder alleleString = new StringBuilder();
        for(GenoType gt: genotype){
            alleleString.append(",").append(gt.genotype);
        }
        return alleleString.toString().replaceFirst(",","");
    }
}
