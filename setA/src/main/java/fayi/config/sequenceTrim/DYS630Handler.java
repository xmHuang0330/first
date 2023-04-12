package fayi.config.sequenceTrim;

import fayi.seqParser.CorePicker;
import fayi.tableObject.StrInfo;
import fayi.utils.SetAException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@locusHandler(locusName = "DYS630")
public class DYS630Handler implements locusHandlerStriction {

    @Override
    public void sequenceCompress(StrInfo strInfo) throws SetAException {
        String pattern = "(AAAG)+(AG)+(AAAGAGAG[ATCG]{8}A[A|G])";
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(strInfo.getTrimmedSeq());
        String lastMatch;
        if (matcher.find()) {

            lastMatch = matcher.group(3);
            CorePicker corePicker = new CorePicker();

            ArrayList<String> nonCoreSeq = new ArrayList<>();
            HashMap<Integer, Integer> repeatCount = new HashMap<>();
            String[] split = strInfo.getTrimmedSeq().split(lastMatch);
            corePicker.picker(split[0], nonCoreSeq, repeatCount, strInfo.getLocus());

            int nIndex = nonCoreSeq.size();
            nonCoreSeq.add(lastMatch);

            corePicker.picker(split[1], nonCoreSeq, repeatCount, strInfo.getLocus());
            strInfo.setNoneCoreseq(nonCoreSeq);
            strInfo.setCoreSeqCount(repeatCount);

            StringBuilder format = new StringBuilder();
            for (int i = 0; i < strInfo.getNoneCoreseq().size(); i++) {
                if (i == nIndex) {
                    format.append("N").append(lastMatch.length()).append(" ");
                } else {
                    format.append("[").append(nonCoreSeq.get(i)).append("]").append(strInfo.getCoreSeqCount().getOrDefault(i, 1)).append(" ");
                }
            }
            strInfo.setNs(lastMatch);
            strInfo.setRepeatSequence(format.toString().trim());
        }
    }


}
