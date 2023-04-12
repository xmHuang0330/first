package fayi.config.sequenceTrim;

import fayi.tableObject.StrInfo;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@locusHandler(locusName = "D8S1132")
@Component
public class D8S1132Handler implements locusHandlerStriction {


    @Override
    public void sequenceCompress(StrInfo strInfo) {
        String pattern = "(TCTA)+(TCA)?(TCTA){5,}";
        String firstN = "TCA";
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(strInfo.getTrimmedSeq());
        if (matcher.find()) {
            StringBuilder format = new StringBuilder();
            if (firstN.equals(matcher.group(2))) {
                for (int i = 0; i < strInfo.getNoneCoreseq().size(); i++) {
                    if (firstN.equals(strInfo.getNoneCoreseq().get(i)) && strInfo.getCoreSeqCount().getOrDefault(i + 1, 1) >= 5) {
                        format.append(strInfo.getNoneCoreseq().get(i).toLowerCase()).append(" ");
                    } else {
                        format.append("[").append(strInfo.getNoneCoreseq().get(i)).append("]").append(strInfo.getCoreSeqCount().getOrDefault(i, 1)).append(" ");
                    }
                }
                strInfo.setRepeatSequence(format.toString().trim());
            }
        }
    }

    public String makeSplit(String repeat, int times) {
        String result = "";
        int each = (times - 1) / 2;
        result += "[" + repeat + "]" + each + " ";
        result += repeat.toLowerCase() + " ";
        result += "[" + repeat + "]" + (times % 2 == 1 ? each : each + 1) + " ";
        return result;
    }


}
