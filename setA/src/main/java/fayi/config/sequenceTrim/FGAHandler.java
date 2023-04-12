package fayi.config.sequenceTrim;

import fayi.seqParser.CorePicker;
import fayi.tableObject.StrInfo;
import fayi.utils.SetAException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@locusHandler(locusName = "FGA")
public class FGAHandler implements locusHandlerStriction {
    @Override
    public void sequenceCompress(StrInfo strInfo) throws SetAException {
        String pattern = "(AAAG)+(AGAAAAAA)(GAAA)+";
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(strInfo.getTrimmedSeq());
        if (matcher.find()) {
            String[] split = strInfo.getTrimmedSeq().split("AAAGAGAAAAAAGAAA");
            if (split.length == 2) {
                CorePicker corePicker = new CorePicker();
                ArrayList<String> nonCoreSeq = new ArrayList<>();
                HashMap<Integer, Integer> repeatCount = new HashMap<>();
                corePicker.picker(split[0] + "AAAG", nonCoreSeq, repeatCount, strInfo.getLocus());
                int nIndex = nonCoreSeq.size();
                nonCoreSeq.add("AGAAAAAA");
                repeatCount.put(nonCoreSeq.size() + 1, 1);
                corePicker.picker("GAAA" + split[1], nonCoreSeq, repeatCount, strInfo.getLocus());
                strInfo.setNoneCoreseq(nonCoreSeq);
                strInfo.setCoreSeqCount(repeatCount);
                StringBuilder format = new StringBuilder();
                for (int i = 0; i < strInfo.getNoneCoreseq().size(); i++) {
                    if (i == nIndex) {
                        format.append("AGAA AAAA").append(" ");
                    } else {
                        format.append("[").append(strInfo.getNoneCoreseq().get(i)).append("]").append(strInfo.getCoreSeqCount().getOrDefault(i, 1)).append(" ");
                    }
                }
                strInfo.setRepeatSequence(format.toString().trim());
            }
        }
    }

}
