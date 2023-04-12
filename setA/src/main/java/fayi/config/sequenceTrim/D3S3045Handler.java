package fayi.config.sequenceTrim;

import fayi.tableObject.StrInfo;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@locusHandler(locusName = "D3S3045")
@Component
public class D3S3045Handler implements locusHandlerStriction {

    String locus = "D3S3045";

    @Override
    public void sequenceCompress(StrInfo strInfo) {
        String pattern = "(AGAT)+(AT|[ATCG]{3,4}AT|AT[ATCG]{3,4})(AGAT)+";
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(strInfo.getTrimmedSeq());
        if (matcher.find()) {
            int atIndex = strInfo.getNoneCoreseq().indexOf(matcher.group(2));
            StringBuilder format = new StringBuilder();
            for (int i = 0; i < strInfo.getNoneCoreseq().size(); i++) {
                if (i == atIndex) {
                    format.append(matcher.group(2).toLowerCase()).append(" ");
                } else {
                    format.append("[").append(strInfo.getNoneCoreseq().get(i)).append("]").append(strInfo.getCoreSeqCount().getOrDefault(i, 1)).append(" ");
                }
            }
            strInfo.setRepeatSequence(format.toString().trim());
        }
    }


}
