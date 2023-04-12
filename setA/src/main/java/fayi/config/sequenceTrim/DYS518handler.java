package fayi.config.sequenceTrim;

import fayi.seqParser.CorePicker;
import fayi.tableObject.StrInfo;
import fayi.utils.SetAException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@locusHandler(locusName = "DYS518")
public class DYS518handler implements locusHandlerStriction {
    @Override
    public void sequenceCompress(StrInfo strInfo) throws SetAException {
        String pattern = "(AAAG)+(GA[ATCG]{4})(AAAG)+";
        Pattern compile = java.util.regex.Pattern.compile(pattern);
        Matcher matcher = compile.matcher(strInfo.getTrimmedSeq());

        String patternFound = "";
        String nString = "";
        while (matcher.find()) {
            patternFound = matcher.group();
            nString = matcher.group(2);
        }
        if (!"".equals(patternFound)) {
            CorePicker corePicker = new CorePicker();
            int shortIndex = patternFound.indexOf(nString);
            int longIndex = strInfo.getTrimmedSeq().lastIndexOf(patternFound);
            String left = strInfo.getTrimmedSeq().substring(0, shortIndex + longIndex);
            String right = strInfo.getTrimmedSeq().substring(shortIndex + longIndex + nString.length());

            ArrayList<String> nonCoreSeq = new ArrayList<>();
            HashMap<Integer, Integer> repeatCount = new HashMap<>();
            corePicker.picker(left, nonCoreSeq, repeatCount, strInfo.getLocus());
            int nIndex = nonCoreSeq.size();
            nonCoreSeq.add(nString);
            corePicker.picker(right, nonCoreSeq, repeatCount, strInfo.getLocus());

            StringBuilder format = new StringBuilder();
            for (int i = 0; i < nonCoreSeq.size(); i++) {
                if (nIndex == i) {
                    format.append(nonCoreSeq.get(i).toLowerCase()).append(" ");
                    strInfo.setNs(nString);
                } else {
                    format.append("[").append(nonCoreSeq.get(i)).append("]").append(repeatCount.getOrDefault(i, 1)).append(" ");
                }
            }
            strInfo.setRepeatSequence(format.toString().trim());
        }
    }


}
