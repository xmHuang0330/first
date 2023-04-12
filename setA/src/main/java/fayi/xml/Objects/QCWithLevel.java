package fayi.xml.Objects;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;

@Data
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "QC")
@AllArgsConstructor
public class QCWithLevel {

    @XmlValue
    String name;

    @XmlAttribute
    String level;

    public QCWithLevel(String name) {
        this.name = name;
    }
}
