package fayi.tableObject;

import fayi.config.Config;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Data
public class HgInfo {

    private String haplotype;
    private float qcScore;
    private String hg_marker;
    // AKA ValidMarker
    private HashMap<String, String> position = new HashMap<>();
    private HashMap<String, String> positionQc = new HashMap<>();
    private ArrayList<String> validMarker = new ArrayList<>();
    private ArrayList<String> hgLine;
    private ArrayList<String> discordant = new ArrayList<>();

    public void setQcScore(ArrayList<String> hgLine) {
        float conflict = 0f;
        discordant = new ArrayList<>();
        String[] samePosition = {"M407", "F3737",
                "Z34163", "F1313",
                "M346", "L56",
                "F2584", "CTS439",
                "FGC10851", "FGC10863",
                "F446", "F560",
                "F1232", "F2356",
                "B433", "Y26395",
                "F1276", "F708",
                "F16381", "F22912",
                "F100", "F85",
                "F856", "F1095",
                "C6651181T", "F24357",
                "F24357", "F26868",
                "F1022", "F1273",
                "F3163", "F1007",
                "Z35160", "F4065",
                "CTS623", "M73",
                "SK1922", "PH5117",
                "F26868", "C6651181T",
                "AM01856/F871", "SK1772"};
        ArrayList<String> strings = new ArrayList<>(Arrays.asList(samePosition));
        for (String locus : validMarker) {
            if (!hgLine.contains(locus) && "1".equals(position.get(locus))) {
                if (strings.contains(locus)) {
                    int siblingIndex = 0;
                    if ((strings.indexOf(locus) % 2) == 0) {
                        siblingIndex = strings.indexOf(locus) + 1;
                    } else {
                        siblingIndex = strings.indexOf(locus) - 1;
                    }
                    if (hgLine.contains(strings.get(siblingIndex))) {
                        continue;
                    }
                }
                discordant.add(locus);
                conflict += 1;
            }
        }
        qcScore = (validMarker.size() - conflict) / validMarker.size();
    }

    public void setPosition(HashMap<String, SnpLocusInfo> locusInfos) {
        for (String locusName : locusInfos.keySet()) {
            ArrayList<SnpInfo> allele = locusInfos.get(locusName).getAllele();
            if (allele.size() > 1) {
                position.put(locusName, "Het");
            } else if (allele.size() == 1) {
                String alleleName = allele.get(0).getAlleleName();
                if (locusInfos.get(locusName).getQualityControl().size() == 0) {
                    validMarker.add(locusName);
                }
                if (alleleName.equals(Config.getInstance().hgRef.get(locusName)[0])) {
                    position.put(locusName, "0");
                } else if (alleleName.equals(Config.getInstance().hgRef.get(locusName)[1])) {
                    position.put(locusName, "1");
                } else {
                    position.put(locusName, alleleName);
                }
            } else {
                position.put(locusName, "");
            }
        }
    }

    public void setHgLine(ArrayList<String> hgLine) {
        this.hgLine = hgLine;
        hg_marker = hgLine.get(0);
    }

    public ArrayList<String> getHgLine() {
        return hgLine;
    }
}
