package fayi.xml.Objects;


import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
