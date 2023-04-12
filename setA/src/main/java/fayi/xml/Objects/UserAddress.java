package fayi.xml.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.xml.bind.annotation.*;
/*
    地址信息
 */
@XmlRootElement(name="address")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
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
