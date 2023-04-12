package fayi.config.sequenceTrim;

import fayi.tableObject.StrInfo;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@locusHandler(locusName = "D4S2366")
@Component
public class D4S2366Handler implements locusHandlerStriction {

    String locus = "D4S2366";

    @Override
    public void sequenceCompress(StrInfo strInfo) {
        String pattern = "(GATA)+(GATT)+(GATA)+(GAC|[ATCG]{3})(GATA){2}";
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(strInfo.getTrimmedSeq());
        String firstN = "GAC";
        if (matcher.find()) {
            firstN = matcher.group(4);
        }
        if (strInfo.getNoneCoreseq().lastIndexOf(firstN) == strInfo.getNoneCoreseq().size() - 2) {
            int gacIndex = strInfo.getNoneCoreseq().lastIndexOf(firstN);
            StringBuilder format = new StringBuilder();
            for (int i = 0; i < strInfo.getNoneCoreseq().size(); i++) {
                if (i == gacIndex) {
                    format.append(firstN.toLowerCase()).append(" ");
                } else {
                    format.append("[").append(strInfo.getNoneCoreseq().get(i)).append("]").append(strInfo.getCoreSeqCount().getOrDefault(i, 1)).append(" ");
                }
            }
            strInfo.setRepeatSequence(format.toString().trim());
        }
    }


}
