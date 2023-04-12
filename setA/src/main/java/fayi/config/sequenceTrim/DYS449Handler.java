package fayi.config.sequenceTrim;

import fayi.tableObject.StrInfo;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@locusHandler(locusName = "DYS449")
@Component
public class DYS449Handler implements locusHandlerStriction {

    @Override
    public void sequenceCompress(StrInfo strInfo) {
        String pattern = "(TCTCTCTCCTCCTC)(TTTC){2}CTTC(TTTC){2}T(TTTC)CTC(TTTCCTTC)";
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(strInfo.getTrimmedSeq());
        if (matcher.find()) {
            boolean Nstart = false;
            String N = "";
            StringBuilder format = new StringBuilder();
            for (int i = 0; i < strInfo.getNoneCoreseq().size(); i++) {
                if (strInfo.getNoneCoreseq().get(i).startsWith("TCTCTCTCCTCCTC")) {
                    Nstart = true;
                }
                if (Nstart) {
                    for (int j = 0; j < strInfo.getCoreSeqCount().getOrDefault(i, 1); j++) {
                        N += strInfo.getNoneCoreseq().get(i);
                        if (N.length() >= 50) {
                            int tttccttc = N.lastIndexOf("TTTCCTTC") + "TTTCCTTC".length();
                            String left = N.substring(tttccttc) + (" ");
                            format.append("N").append(N.substring(0, tttccttc).length()).append(" ");
                            if (!" ".equals(left)) {
                                format.append(left);
                            }
                            if (j + 1 < strInfo.getCoreSeqCount().getOrDefault(i, 1)) {
                                format.append(String.format("[%s]%s ", strInfo.getNoneCoreseq().get(i), strInfo.getCoreSeqCount().getOrDefault(i, 1) - j - 1));
                                break;
                            }
                            N = N.substring(0, tttccttc);
                            Nstart = false;
                        }
                    }
                } else {
                    format.append(String.format("[%s]%s ", strInfo.getNoneCoreseq().get(i), strInfo.getCoreSeqCount().getOrDefault(i, 1)));
                }
            }
            strInfo.setNs(N);
            strInfo.setRepeatSequence(format.toString());
        }
    }

}
