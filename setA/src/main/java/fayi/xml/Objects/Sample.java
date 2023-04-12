package fayi.xml.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;
/*
    样本信息
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "sample")
@AllArgsConstructor
@NoArgsConstructor
public class Sample {

    @XmlElement
    public BasicInfo basicInfo;

    @XmlElement
    public CalResult calResult = new CalResult();
    @XmlElement
    public LocusInfomations locusInfomations = new LocusInfomations(  );
    @XmlElement
    public ExperimentProcessing experimentProcessing;
    @XmlElement
    public DataProcessing dataProcessing;
    @XmlElement
    public Sites sites = new Sites();

    public Sample(BasicInfo basicInfo){
        this.basicInfo = basicInfo;
    }


}
