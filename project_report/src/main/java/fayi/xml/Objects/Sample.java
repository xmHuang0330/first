package fayi.xml.Objects;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
    样本信息
 */
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
