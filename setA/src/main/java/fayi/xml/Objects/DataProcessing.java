package fayi.xml.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;
/*
    数据处理
 */
@Data
@XmlRootElement(name = "dataProcessing")
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor
public class DataProcessing {

    @XmlElement
    public SoftWare softWare;

}
