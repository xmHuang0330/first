package fayi.xml.Objects;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/*
    试剂盒
 */
@XmlRootElement(name = "kit")
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor
public class Kit{

    @XmlAttribute(name = "type")
    public String Type = "multiplex_pcr";

    @XmlValue()
    public String Name = "FiSeq Targeted DNA Prep kit";

    @XmlAttribute(name = "version")
    public String Version = "1.0.0";
}