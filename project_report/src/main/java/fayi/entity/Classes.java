package fayi.entity;


import lombok.Setter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Arrays;
import java.util.List;

@Setter
public class Classes {

    @XmlAttribute
    private String name;
    @XmlAttribute
    private String master;
    @XmlAttribute
    private String description;
    @XmlAttribute
    private String studentCount;

    @XmlTransient
    public String getName() {
        return name;
    }

    @XmlTransient
    public String getMaster() {
        return master;
    }

    @XmlTransient
    public String getDescription() {
        return description;
    }

    @XmlTransient
    public String getStudentCount() {
        return studentCount;
    }

    public List<String> getInfo(){

        return Arrays.asList(name,master,description,studentCount);
    }



    @Override
    public String toString() {
        return "Classes{" +
                "name='" + name + '\'' +
                ", master='" + master + '\'' +
                ", description='" + description + '\'' +
                ", studentCount='" + studentCount + '\'' +
                '}';
    }
}
