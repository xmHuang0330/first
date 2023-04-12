package fayi.xml.Objects;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
/*
    总位点信息
 */
@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "locusInformation")
@AllArgsConstructor
@NoArgsConstructor
public class LocusInfomations {

    @XmlElementWrapper(name = "Autosomal_STR")
    @XmlElement
    ArrayList<LocusData> autoStr = new ArrayList<>(  );

    @XmlElementWrapper(name = "X_STR")
    @XmlElement
    ArrayList<LocusData> xStr = new ArrayList<>(  );

    @XmlElementWrapper(name = "Y_STR")
    @XmlElement
    ArrayList<LocusData> yStr = new ArrayList<>(  );

    @XmlElementWrapper(name = "iSNP")
    @XmlElement
    ArrayList<LocusData> isnp = new ArrayList<>(  );
}
