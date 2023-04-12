package fayi.xml.Objects;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
    实验流程
 */
@XmlRootElement(name = "experimentProcessing")
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor
public class ExperimentProcessing {

    @XmlElement(name = "kit")
    public Kit Kit;

    @XmlElement(name = "sequencer")
    public Sequencer Sequencer;

}
