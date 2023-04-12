package fayi.entity;

import lombok.Setter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

@Setter
public class Grade {

    @XmlAttribute
    private String name;
    @XmlAttribute
    private String description;

    private List<Classes> classes;

    @XmlTransient
    public String getName() {
        return name;
    }

    @XmlTransient
    public String getDescription() {
        return description;
    }

    public List<Classes> getClasses() {
        return classes;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", classes=" + classes +
                '}';
    }
}
