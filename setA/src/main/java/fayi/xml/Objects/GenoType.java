package fayi.xml.Objects;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class GenoType{
    @XmlAttribute
    String snp;
    @XmlValue
    String genotype;
}