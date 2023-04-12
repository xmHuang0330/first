package fayi.config.sequenceTrim;

import fayi.seqParser.CorePicker;
import fayi.tableObject.StrInfo;
import fayi.utils.SetAException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@locusHandler(locusName = "D19S433")
@Component
public class D19S433Handler implements locusHandlerStriction {

    String locus = "D19S433";

    @Override
    public void sequenceCompress(StrInfo strInfo) throws SetAException {
        String pattern = "(CCTA|A)(CCTT)(CTTT|TT)(CCTT)+";
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(strInfo.getTrimmedSeq());
        String find;
        if (matcher.find()) {
            CorePicker corePicker = new CorePicker();
            find = matcher.group();

            ArrayList<String> nonCoreSeq = new ArrayList<>();
            HashMap<Integer, Integer> repeatCount = new HashMap<>();
            String[] split = strInfo.getTrimmedSeq().split(find);
            corePicker.picker(split[0], nonCoreSeq, repeatCount, strInfo.getLocus());
            int nStart = nonCoreSeq.size() - 1;
            boolean oneDown = false;
            if ("A".equals(matcher.group(1))) {
                oneDown = true;
                nonCoreSeq.add("A");
            } else {
                nonCoreSeq.add("ccta");
            }
            nonCoreSeq.add(matcher.group(2));
            if ("TT".equals(matcher.group(3))) {
                oneDown = true;
                nonCoreSeq.add("TT");
            } else {
                nonCoreSeq.add("cttt");
            }
            corePicker.picker(matcher.group(4), nonCoreSeq, repeatCount, strInfo.getLocus());

            StringBuilder format = new StringBuilder();
            ArrayList<String> Ns = new ArrayList<>();
            for (int i = 0; i < nonCoreSeq.size(); i++) {
                if (nStart + 1 == i || nStart + 3 == i) {
                    format.append(oneDown ? nonCoreSeq.get(i).toUpperCase(Locale.ROOT) : nonCoreSeq.get(i)).append(" ");
                    Ns.add(nonCoreSeq.get(i));
                } else {
                    format.append("[").append(nonCoreSeq.get(i)).append("]").append(repeatCount.getOrDefault(i, 1)).append(" ");
                }
            }
            strInfo.setRepeatSequence(format.toString().trim());

        }

    }


}
