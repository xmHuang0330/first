package fayi.xml.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;
/*
    总深度
 */
@Data
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
