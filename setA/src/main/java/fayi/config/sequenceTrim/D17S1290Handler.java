package fayi.config.sequenceTrim;

import fayi.seqParser.CorePicker;
import fayi.tableObject.StrInfo;
import fayi.utils.SetAException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@locusHandler(locusName = "D17S1290")
@Component
public class D17S1290Handler implements locusHandlerStriction {

    @Override
    public void sequenceCompress(StrInfo strInfo) throws SetAException {

        String pattern = "(GATG(ATA[TG]){6})";

        Pattern compile = Pattern.compile( pattern );
        Matcher matcher = compile.matcher( strInfo.getTrimmedSeq() );
        if (matcher.find()) {

            String N = matcher.group();

            String[] split = strInfo.getTrimmedSeq().split(N);

            CorePicker corePicker = new CorePicker();

            ArrayList<String> nonCoreSeq = new ArrayList<>();
            HashMap<Integer, Integer> repeatCount = new HashMap<>();
            corePicker.pickerWithConfig(split[0], nonCoreSeq, repeatCount, strInfo.getLocus(), new ArrayList<>(Collections.singletonList("AGAT")));

            int Nindex = nonCoreSeq.size();
            nonCoreSeq.add(N);

            ArrayList<String> temp = new ArrayList<>();
            HashMap<Integer, Integer> tempCount = new HashMap<>();
            if (split.length > 1){
                corePicker.picker(split[1], temp, tempCount, strInfo.getLocus());
            }
            append(nonCoreSeq, repeatCount, temp, tempCount);

            StringBuilder format = new StringBuilder();
            for (int j = 0; j < nonCoreSeq.size(); j++) {
                if (j == Nindex) {
                    format.append("N").append(N.length()).append(" ");
                } else {
                    format.append(String.format("[%s]%s ", nonCoreSeq.get(j), repeatCount.getOrDefault(j, 1)));
                }
            }
            strInfo.setRepeatSequence(format.toString().trim());
            strInfo.setNs(N);
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
