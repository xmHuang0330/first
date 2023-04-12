package fayi.config.sequenceTrim;

import fayi.seqParser.CorePicker;
import fayi.tableObject.StrInfo;
import fayi.utils.SetAException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@locusHandler(locusName = "D21S11")
public class D21S11Handler implements locusHandlerStriction {


    @Override
    public void sequenceCompress(StrInfo strInfo) throws SetAException {
        String pattern = "(TA)((TCTA|[ATCG]{4}){2,})(TCA)((TCTA){2,})(TCCATA|[ATCG]{4}TA)";

        Pattern compile = Pattern.compile( pattern );
        Matcher matcher = compile.matcher( strInfo.getTrimmedSeq() );
        if (matcher.find()) {
            CorePicker corePicker = new CorePicker();

            ArrayList<String> nonCoreSeq = new ArrayList<>();
            HashMap<Integer, Integer> repeatCount = new HashMap<>();
            String[] split = strInfo.getTrimmedSeq().split( matcher.group() );
            corePicker.picker(split[0], nonCoreSeq, repeatCount, strInfo.getLocus());

            int firstNIndex = nonCoreSeq.size();
            nonCoreSeq.add(matcher.group(1).toLowerCase());

            ArrayList<String> temp = new ArrayList<>();
            HashMap<Integer, Integer> tempCount = new HashMap<>();
            corePicker.picker(matcher.group(2), temp, tempCount, strInfo.getLocus());
            append(nonCoreSeq, repeatCount, temp, tempCount);

            int secondNIndex = nonCoreSeq.size();
            nonCoreSeq.add(matcher.group(4).toLowerCase());

            temp = new ArrayList<>();
            tempCount = new HashMap<>();
            corePicker.picker(matcher.group(5), temp, tempCount, strInfo.getLocus());
            append(nonCoreSeq, repeatCount, temp, tempCount);

            int thirdNIndex = nonCoreSeq.size();
            nonCoreSeq.add(matcher.group(7).toLowerCase());

            if (split.length > 1) {
                temp = new ArrayList<>();
                tempCount = new HashMap<>();
                corePicker.picker(split[1], temp, tempCount, strInfo.getLocus());
                append(nonCoreSeq, repeatCount, temp, tempCount);
            }

//            System.out.println(nonCoreSeq);
            StringBuilder format = new StringBuilder();
            for (int i = 0; i < nonCoreSeq.size(); i++) {
                if (firstNIndex == i || secondNIndex == i || thirdNIndex == i) {
                    format.append(nonCoreSeq.get(i)).append(" ");
                } else {
                    format.append("[").append(nonCoreSeq.get(i)).append("]").append(repeatCount.getOrDefault(i, 1)).append(" ");
                }
            }
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
