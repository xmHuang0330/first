package fayi.config.sequenceTrim;

import fayi.tableObject.StrInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@locusHandler(locusName = "DYS19")
public class DYS19Handler implements locusHandlerStriction {

    String locus = "DYS19";

    @Override
    public void sequenceCompress(StrInfo strInfo) {
        String pattern = "(TCTA)+([ATCG]*?)(CCTA)([ATCG]*?)(TCTA)+";
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(strInfo.getTrimmedSeq());
        if (matcher.find()) {
            StringBuilder format = new StringBuilder();
            boolean cctaCounted = false;
            for (int i = strInfo.getNoneCoreseq().size() - 1; i >= 0; i--) {
                if (!cctaCounted && strInfo.getNoneCoreseq().get(i).contains("CCTA") && strInfo.getCoreSeqCount().getOrDefault(i, 1) == 1) {
                    int cctaIndex = strInfo.getNoneCoreseq().get(i).indexOf("CCTA");

                    if (strInfo.getNoneCoreseq().get(i).length() > cctaIndex + 4) {
                        format.insert(0, " ").insert(0, strInfo.getNoneCoreseq().get(i).substring(cctaIndex + 4));
                    }
                    format.insert(0, " ").insert(0, strInfo.getNoneCoreseq().get(i).substring(cctaIndex, cctaIndex + 4).toLowerCase());
                    if (cctaIndex > 0) {
                        format.insert(0, " ").insert(0, strInfo.getNoneCoreseq().get(i).substring(0, cctaIndex));
                    }
                    cctaCounted = true;
                } else {
                    format.insert(0, " ").insert(0, strInfo.getCoreSeqCount().getOrDefault(i, 1)).insert(0, "]").insert(0, strInfo.getNoneCoreseq().get(i)).insert(0, "[");
                }
            }
            strInfo.setRepeatSequence(format.toString().trim());
        }
    }

}
