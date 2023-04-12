package fayi.tableObject;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
public class Flanking implements Serializable {

    private String leftSequence = "";
    private String rightSequence = "";
    private Float depth;
    private String alleleName;
    private String sequence;

    public Flanking(String leftSequence, String rightSequence, Float depth, String alleleName, String sequence) {
        this.leftSequence = leftSequence;
        this.rightSequence = rightSequence;
        this.depth = depth;
        this.alleleName = alleleName;
        this.sequence = sequence;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flanking flanking = (Flanking) o;
        return sequence.equals(flanking.sequence);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sequence);
    }
}
