package fayi.xml.Objects;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
    软件信息
 */
@XmlRootElement(name = "software")
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor
public class SoftWare {
    @XmlAttribute(name = "type")
    public String Type = "DataAnalyse";
    @XmlValue()
    public String Name;
    @XmlAttribute(name = "version")
    public String Version;
//    @XmlElement
//    public String config;
}