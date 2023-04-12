package fayi;

import fayi.config.Enum.Gender;
import fayi.config.Param;
import fayi.config.sequenceTrim.locusHandler;
import fayi.config.sequenceTrim.locusHandlerStriction;
import fayi.seqParser.CorePicker;
import fayi.seqParser.RazorOutParse;
import fayi.tableObject.SampleInfo;
import fayi.tableObject.StrInfo;
import fayi.utils.SetAException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CoreSeqCompress {

    private Map<String, locusHandlerStriction> handlerMap;

    @Autowired
    CorePicker corePicker;

    Param param;

    @Autowired
    public void setHandlers(List<locusHandlerStriction> handlers) {
        handlerMap = handlers.stream().collect(
                Collectors.toMap(
                        orderHandler -> AnnotationUtils.findAnnotation(orderHandler.getClass(), locusHandler.class).locusName(),
                        v -> v,
                        (v1, v2) -> v1)
        );
    }

    @Autowired
    RazorOutParse razorOutParse;

    public void processSample(SampleInfo sampleInfo) throws SetAException {
        param = Param.getPanelParams(sampleInfo.getBasicInfo().panel);
        for (String locus : param.StrLocusOrder) {
            if (sampleInfo.getBasicInfo().gender.equals( Gender.female ) && param.YStrLocusOrder.contains( locus )) {
                continue;
            }

            for (StrInfo strInfo : sampleInfo.getStrDataAboveAt( locus )) {
                if (null == strInfo.getTrimmedSeq()) {
                    razorOutParse.trimSequence(strInfo);
                }
                try {
                    processStrInfo(strInfo, true);
                } catch (SetAException e) {
                    if (e.getCode() == 5) {
                        log.error(e.getMessage() + String.format(" 样本:%s, 位点:%s ", sampleInfo.getId(), strInfo.getLocus()));
                    }
                }
            }
        }
    }

    public void processStrInfo(StrInfo strInfo, boolean calCoreStr) throws SetAException {
//        strInfo.formatRepeatSequence(false);
//        if(strInfo.getLocus().equals("DYS389II")){
//            corePicker.calCoreSTR(strInfo.getPair389I());
//        }
        if(calCoreStr) {
            corePicker.calCoreSTR(strInfo);
        }
        strInfo.formatRepeatSequence(false);
        if (handlerMap.containsKey(strInfo.getLocus())) {
            handlerMap.get(strInfo.getLocus()).sequenceCompress(strInfo);
        }
    }
}
