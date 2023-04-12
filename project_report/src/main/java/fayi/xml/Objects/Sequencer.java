package fayi.xml.Objects;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
    测序信息
 */
@XmlRootElement(name = "sequencer")
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor
public class Sequencer{
    @XmlAttribute(name = "type")
    public String Type = "ngs";
    @XmlValue()
    public String Name = "MGIseq";
    @XmlAttribute(name = "version")
    public String Version = "3.0.0";
}