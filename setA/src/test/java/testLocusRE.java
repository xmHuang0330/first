import fayi.seqParser.SequenceMapping;
import fayi.tableObject.MappingResult;

public class testLocusRE {

    public static void main(String[] args) {

        String knownPattern = "GATGATAGATATATAGATATATAGATAT";
        String currentSequence = "AGATAGATAGATGATGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGA";

        int start = 0;
        int difference = 0;
        while (start + knownPattern.length() <= currentSequence.length()) {

            for (int i = 0; i < knownPattern.length(); i++) {
                if (knownPattern.charAt(i) != currentSequence.charAt(start + i)) {
                    difference += 1;
                    if (knownPattern.charAt(i) == currentSequence.charAt(start + i + 1)) {

                    } else if (knownPattern.charAt(i + 1) == currentSequence.charAt(start + i)) {

                    }
                }
            }
            System.out.println(start);

            start++;
        }

    }


}
