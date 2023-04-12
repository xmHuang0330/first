package fayi.config.sequenceTrim;

import fayi.seqParser.CorePicker;
import fayi.tableObject.StrInfo;
import fayi.utils.SetAException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@locusHandler(locusName = "D18S535")
@Component
public class D18S535Handler implements locusHandlerStriction {

    @Override
    public void sequenceCompress(StrInfo strInfo) throws SetAException {
        CorePicker corePicker = new CorePicker();
        String pattern = "^(AGAT|[ATCG]{4})(AGAC|[ATCG]{4})((AGAT){2}|([ATCG]{4}){2})([ATCG]{3})(AGAT)+";
        Pattern compile = Pattern.compile( pattern );
        Matcher matcher = compile.matcher( strInfo.getTrimmedSeq() );
        if (matcher.find()) {
            String firstN = matcher.group( 1 ) + matcher.group( 2 );
            String nextN = matcher.group( 6 );

            ArrayList<String> nonCoreSeq = new ArrayList<>();
            HashMap<Integer, Integer> repeatCount = new HashMap<>();

            StringBuilder format = new StringBuilder();
            corePicker.picker(strInfo.getTrimmedSeq().substring(8, 16), nonCoreSeq, repeatCount, this.getClass().getAnnotation(locusHandler.class).locusName());
            int nIndex = nonCoreSeq.size();
            nonCoreSeq.add(nextN);
            corePicker.picker(strInfo.getTrimmedSeq().substring(19), nonCoreSeq, repeatCount, this.getClass().getAnnotation(locusHandler.class).locusName());
            format.append("N").append(firstN.length()).append(" ");
            for (int i = 0; i < nonCoreSeq.size(); i++) {
                if (nIndex == i) {
                    format.append("N").append(nextN.length()).append(" ");
                } else {
                    format.append("[").append(nonCoreSeq.get(i)).append("]").append(repeatCount.getOrDefault(i, 1)).append(" ");
                }
            }
            strInfo.setRepeatSequence(format.toString().trim());
        }
    }


}
