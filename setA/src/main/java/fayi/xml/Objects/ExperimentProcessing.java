package fayi.xml.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;
/*
    实验流程
 */
@Data
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
