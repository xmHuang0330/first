package fayi.flanking;

import fayi.config.Param;
import fayi.tableObject.StrInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import static fayi.utils.NgstutterSetting.NGStutterSetter;

@Data
@NoArgsConstructor
public class FlankingInfo {

    HashMap<String, ArrayList<StrInfo>> flankingStrInfo = new HashMap<>();

    HashMap<String, Integer> locusDepth = new HashMap<>();
    private HashMap<String, Double> locusAT = new HashMap<>();
    private HashMap<String, Double> locusIT = new HashMap<>();

    private void setLocusAT() {
        for (String locus : flankingStrInfo.keySet()) {
            float totalDepth = locusDepth.get(locus);
            if (totalDepth > 650) {
                locusAT.put(locus, Param.getInstance().AT.getOrDefault(locus, 1.5) * 0.01 * totalDepth);
                locusIT.put(locus, Param.getInstance().IT.getOrDefault(locus, 4.5) * 0.01 * totalDepth);
            } else {
                locusAT.put(locus, Param.getInstance().AT.getOrDefault(locus, 1.5) * 0.01 * 650);
                locusIT.put(locus, Param.getInstance().IT.getOrDefault(locus, 4.5) * 0.01 * 650);
            }
        }

    }

    public void atFilter() {
        setLocusAT();
        for (String locus : flankingStrInfo.keySet()) {
            ArrayList<StrInfo> aboveAt = new ArrayList<>();
            for (StrInfo strInfo : flankingStrInfo.get(locus)) {
                if (strInfo.getReads() > locusAT.get(locus)) {
                    if (strInfo.getReads() >= locusIT.get(locus)) {
                        strInfo.setAboveIT(true);
                    }
                    strInfo.setAboveAT(true);
                    aboveAt.add(strInfo);
                }
            }
            flankingStrInfo.put(locus, aboveAt);
        }
    }

    public void ngsFilter() {
        for (String locus : flankingStrInfo.keySet()) {
            ArrayList<StrInfo> depthSorted = (ArrayList<StrInfo>) flankingStrInfo.get( locus ).stream().sorted( (o1, o2) -> Float.compare( o2.getReads(), o1.getReads() ) ).collect( Collectors.toList() );
            flankingStrInfo.put( locus, depthSorted );
            for (StrInfo trueFlanking : depthSorted) {
                if (trueFlanking.getIsNGStutter())
                    return;
                NGStutterSetter( trueFlanking, depthSorted, 3 );
            }
        }
    }
}
