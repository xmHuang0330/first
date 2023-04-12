package fayi.xml.Objects;

import fayi.config.Enum.Gender;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
    样本基本信息
 */
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "basicInfo")
public class BasicInfo {
    @XmlElement
    public String chip;
    @XmlElement
    public String lane;
    @XmlElement
    public String project;
    @XmlElement
    public String name;
    @XmlElement
    public Integer id;
    @XmlElement
    public Gender gender;
    @XmlElement
    public String type;
    @XmlElement(name = "fq")
    public String fastq;
    @XmlElement(nillable = true)
    public Kit kit = new Kit();
    @XmlElement(nillable = true)
    public Sequencer sequencer = new Sequencer();
    @XmlElement
    public SoftWare softWare;
    @XmlElement
    public String panel;
    @XmlElement
    public String tablet;

    @XmlElement
    public String well;

    @Override
    public String toString() {
        return "BasicInfo{" +
                "chip='" + chip + '\'' +
                ", lane='" + lane + '\'' +
                ", project='" + project + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                ", tablet='" + tablet + '\'' +
                '}';
    }

    //    public BasicInfo(SampleInfo sampleInfo, String rawdata) {
//        this.panel = sampleInfo.getPanel();
//        this.tablet = sampleInfo.getTablet();
//        this.fastq = rawdata;
//        this.type = sampleInfo.getType();
//        this.chip = sampleInfo.getChipName();
//        this.project = sampleInfo.getProjectName();
//        this.id = sampleInfo.getIndex();
//        this.name = sampleInfo.getName();
//        this.lane = sampleInfo.getLane();
//        String gender = sampleInfo.getGender();
//        this.gender ="男".equals(gender) ? Gender.male : "女".equals(gender) ? Gender.female : Gender.uncertain;
//
//    }
}
