package fayi.entity;

import lombok.Setter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

@Setter
public class School {

    @XmlAttribute
    private String name;
    @XmlAttribute
    private String description;

    private List<Grade> grade;

    @XmlTransient
    public String getName() {
        return name;
    }

    @XmlTransient
    public String getDescription() {
        return description;
    }

    public List<Grade> getGrade() {
        return grade;
    }

    @Override
    public String toString() {
        return "School{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", grade=" + grade +
                '}';
    }
}
