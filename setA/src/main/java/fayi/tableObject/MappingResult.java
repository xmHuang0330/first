package fayi.tableObject;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class MappingResult {

    public String sequence_A;
    public String sequence_B;
    public float score;


    @Override
    public String toString() {
        return "MappingResult{" +
                "sequence_A='" + sequence_A + '\'' +
                ", sequence_B='" + sequence_B + '\'' +
                ", score=" + score +
                '}';
    }
}
