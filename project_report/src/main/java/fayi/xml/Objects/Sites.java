package fayi.xml.Objects;

import jakarta.xml.bind.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
/*
    样本的所有位点信息
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@NoArgsConstructor
public class Sites{

    @XmlAttribute
    public String reference;

    @XmlElementWrapper(name = "strSites")
    @XmlElement(name = "site")
    public ArrayList<Site> strSites = new ArrayList<>();

    @XmlElementWrapper(name = "snpSites")
    @XmlElement(name = "site")
    public ArrayList<Site> snpSites = new ArrayList<>();

}