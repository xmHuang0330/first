import fayi.APP;
import fayi.CoreSeqCompress;
import fayi.WriteExcel.SingleExcel;
import fayi.WriteExcel.FlankingData;
import fayi.config.Config;
import fayi.config.Enum.SnpPosition;
import fayi.config.SnpMarker;
import fayi.flanking.FlankingSequence;
import fayi.tableObject.SampleInfo;
import fayi.tableObject.SeqInfo;
import fayi.tableObject.StrInfo;
import fayi.xml.Objects.Data;
import fayi.xml.Objects.Sample;
import fayi.xml.Xml;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@SpringBootTest(classes = {APP.class})
public class muscleTest {
    @Autowired
    private CoreSeqCompress compress;
    @Test
    public void test() throws Exception {
        log.warn("start success");
        System.setProperty("test.artifactId", "setB");

        Config config = Config.getInstance();
        config.setWorker((byte) 5);
        config.setOutputPath("/Volumes/DATA/setB_test");
        config.setTempDir(config.getOutputPath() + "/temp/");
        Data data = new Xml().xmlToData("/Volumes/DATA/setB_test/test-output.xml");
        SingleExcel singleExcel = new SingleExcel();

        ArrayList<SampleInfo> sampleInfos = new ArrayList<>();

        for (Sample sample : data.samples) {
            SampleInfo sampleInfo = singleExcel.sampleToSampleInfo(sample);

            sampleInfo.setATDepthValue();
            sampleInfo.strDataFilter();

            compress.processSample(sampleInfo);
            sampleInfos.add(sampleInfo);
        }

        FlankingSequence flankingSequence = new FlankingSequence();
        flankingSequence.batchAlign(sampleInfos);

        FlankingData flankingData = new FlankingData();
        flankingData.writeFlankingExcel(sampleInfos);
        System.out.println(sampleInfos);
    }

}
