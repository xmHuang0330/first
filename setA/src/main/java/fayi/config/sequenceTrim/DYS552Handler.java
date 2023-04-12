package fayi.config.sequenceTrim;

import fayi.seqParser.CorePicker;
import fayi.tableObject.StrInfo;
import fayi.utils.SetAException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@locusHandler(locusName = "DYS552")
@Component
public class DYS552Handler implements locusHandlerStriction {

    String locus = "DYS552";

    @Override
    public void sequenceCompress(StrInfo strInfo) throws SetAException {
        String pattern = "ATCTATAATCTA([ATCG]{8})TCTA([ATCG]{9})TCTATCT";
        Pattern compile = Pattern.compile( pattern );
        Matcher matcher = compile.matcher( strInfo.getTrimmedSeq() );

        if (matcher.find()) {
            String foundN = matcher.group();
            CorePicker corePicker = new CorePicker();
            ArrayList<String> nonCoreSeq = new ArrayList<>();
            HashMap<Integer, Integer> repeatCount = new HashMap<>();

            String[] split = strInfo.getTrimmedSeq().split(foundN);

            ArrayList<String> temp = new ArrayList<>();
            HashMap<Integer, Integer> tempCount = new HashMap<>();
            corePicker.picker(split[0], temp, tempCount, strInfo.getLocus());
            append(nonCoreSeq, repeatCount, temp, tempCount);

            int nIndex = nonCoreSeq.size();
            nonCoreSeq.add(foundN);

            temp = new ArrayList<>();
            tempCount = new HashMap<>();
            corePicker.picker(split[1], temp, tempCount, strInfo.getLocus());
            append(nonCoreSeq, repeatCount, temp, tempCount);

            strInfo.setNoneCoreseq(nonCoreSeq);
            strInfo.setCoreSeqCount(repeatCount);
            StringBuilder format = new StringBuilder();
            for (int i = 0; i < nonCoreSeq.size(); i++) {
                if (i == nIndex) {
                    format.append("N").append(foundN.length()).append(" ");
                } else {
                    format.append("[").append(nonCoreSeq.get(i)).append("]").append(repeatCount.getOrDefault(i, 1)).append(" ");
                }
            }
            strInfo.setNs(foundN);
            strInfo.setRepeatSequence(format.toString().trim());
        }
    }

    private void append(ArrayList<String> nonCoreSeq, HashMap<Integer, Integer> repeatCount, ArrayList<String> temp, HashMap<Integer, Integer> tempCount) {
        int originalLength = nonCoreSeq.size();
        for (int i = 0; i < temp.size(); i++) {
            nonCoreSeq.add(temp.get(i));
            repeatCount.put(i + originalLength, tempCount.getOrDefault(i, 1));
        }
    }
}
