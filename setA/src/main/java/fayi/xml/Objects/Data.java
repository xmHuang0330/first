package fayi.xml.Objects;

import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
/*
    数据
 */
@XmlRootElement(name = "data")
@XmlAccessorType(XmlAccessType.FIELD)
@NoArgsConstructor
public class Data {
    //样本数
    @XmlAttribute
    public Integer sampleNum = 0;
    //样本
    @XmlElement(name = "sample")
    public ArrayList<Sample> samples = new ArrayList<>();
    public Data(ArrayList<Sample> samples){
        this.samples = samples;
        sampleNum = samples.size();
    }

    public void addSamples(Data xmlToData) {
        samples.addAll(xmlToData.samples);
        sampleNum += xmlToData.sampleNum;
    }
}
