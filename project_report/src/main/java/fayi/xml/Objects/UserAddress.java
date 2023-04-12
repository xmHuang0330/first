package fayi.xml.Objects;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
    地址信息
 */
@XmlRootElement(name="address")
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor
public class UserAddress {
    @XmlElement
    public String province;
    @XmlElement
    public String city;
    @XmlElement
    public String country;
    @XmlElement
    public String location;
}
