package fayi.config.sequenceTrim;

import fayi.seqParser.CorePicker;
import fayi.tableObject.StrInfo;
import fayi.utils.SetAException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@locusHandler(locusName = "DYS389II")
public class DYS389IIHandler implements locusHandlerStriction {

    @Override
    public void sequenceCompress(StrInfo strInfo) throws SetAException {
//        if(strInfo.getPair389I() == null){
//            return;
//        }
        String pattern = "((CAGA)+|CAGA[ATCG]{3,4})([ATCG]{47,49}?)([ATCG]{3,4}+)(TAGA)+";
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(strInfo.getTrimmedSeq());
        if (matcher.find()) {
            CorePicker corePicker = new CorePicker();

            ArrayList<String> nonCoreSeq = new ArrayList<>();
            HashMap<Integer, Integer> repeatCount = new HashMap<>();
            String[] split = strInfo.getTrimmedSeq().split(matcher.group(3));

            ArrayList<String> temp = new ArrayList<>();
            HashMap<Integer, Integer> tempCount = new HashMap<>();
            corePicker.picker(split[0], temp, tempCount, strInfo.getLocus());
            append(nonCoreSeq, repeatCount, temp, tempCount);

            int nIndex = nonCoreSeq.size();
            nonCoreSeq.add(matcher.group(3));

            temp = new ArrayList<>();
            tempCount = new HashMap<>();
            corePicker.picker(split[1], temp, tempCount, strInfo.getLocus());
            append(nonCoreSeq, repeatCount, temp, tempCount);

            strInfo.setNoneCoreseq(nonCoreSeq);
            strInfo.setCoreSeqCount(repeatCount);

            StringBuilder format = new StringBuilder();
            for (int i = 0; i < nonCoreSeq.size(); i++) {
                if (i == nIndex) {
                    format.append("N").append(matcher.group(3).length()).append(" ");
                } else {
                    format.append("[").append(nonCoreSeq.get(i)).append("]").append(repeatCount.getOrDefault(i, 1)).append(" ");
                }
            }
            strInfo.setNs(matcher.group(3));
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
