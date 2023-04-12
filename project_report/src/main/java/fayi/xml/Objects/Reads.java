package fayi.xml.Objects;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
    总深度
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Reads")
@AllArgsConstructor
@NoArgsConstructor
public class Reads {

    @XmlAttribute
    public float forward;

    @XmlAttribute
    public float reverse;

    @XmlValue
    public float total;

}
