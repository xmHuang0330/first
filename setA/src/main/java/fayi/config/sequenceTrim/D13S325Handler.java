package fayi.config.sequenceTrim;

import fayi.config.Config;
import fayi.tableObject.StrInfo;
import org.springframework.stereotype.Component;

@locusHandler(locusName = "D13S325")
@Component
public class D13S325Handler implements locusHandlerStriction {

    @Override
    public void sequenceCompress(StrInfo strInfo) {
        Config config = Config.getInstance();
        if (strInfo.getNoneCoreseq().contains("TCA")) {
            int tcaIndex = strInfo.getNoneCoreseq().indexOf("TCA");
            StringBuilder format = new StringBuilder();
            for (int i = 0; i < strInfo.getNoneCoreseq().size(); i++) {
                format.append(" ");
                if (tcaIndex != i) {
                    format.append(String.format("[%s]%s ", strInfo.getNoneCoreseq().get(i), strInfo.getCoreSeqCount().getOrDefault(i, 1)));
                } else {
                    format.append("tca");
                }
            }
            format = new StringBuilder(format.toString().trim());
            strInfo.setRepeatSequence(format.toString());
        } else if (strInfo.getNoneCoreseq().size() >= 3) {
            StringBuilder format = new StringBuilder();
            for (int i = 0; i < strInfo.getNoneCoreseq().size(); i++) {
                format.append(" ");
                if (config.getParam().locusSTR.get(strInfo.getLocus()).contains(strInfo.getNoneCoreseq().get(i))
                        && strInfo.getCoreSeqCount().get(i) >= 1) {
                    format.append("[").append(strInfo.getNoneCoreseq().get(i)).append("]").append(strInfo.getCoreSeqCount().get(i));
                } else {
                    if (strInfo.getNoneCoreseq().get(i).contains("TCA")) {
                        format.append("tca ");
                        String left = strInfo.getNoneCoreseq().get(i).substring(strInfo.getNoneCoreseq().get(i).indexOf("TCA") + "TCA".length());
                        format.append(left);
                    }
                }
            }
            format = new StringBuilder(format.toString().trim());
            strInfo.setRepeatSequence(format.toString());
        }
    }

}
