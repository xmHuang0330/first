import fayi.APP;
import fayi.Analyse;
import fayi.CoreSeqCompress;
import fayi.WriteExcel.SingleExcel;
import fayi.config.Config;
import fayi.config.Enum.Gender;
import fayi.seqParser.RazorOutParse;
import fayi.seqParser.SE400FqTools;
import fayi.tableObject.*;
import fayi.utils.Utils;
import fayi.xml.Objects.BasicInfo;
import fayi.xml.Objects.Data;
import fayi.xml.Xml;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest(classes = {APP.class})
public class coreSeqCompress {
    @Autowired
    public CoreSeqCompress compress;
    @Autowired
    private RazorOutParse razorOutParse;
    @Autowired
    SE400FqTools se400FqTools;
    Config config;

    @Test
    public void setATest() throws Exception {

        System.setProperty("test.artifactId", "setA");
        config = Config.getInstance();
//        config.setRazorConfig(config.getProjectPath()+ "/resource/razer/str_snp_config");
        config.setOutputPath("/Volumes/DATA/setA_test/");
        config.setTempDir("/Volumes/DATA/setA_test/temp/");
//        config.setRazorConfig(config.getFlankingConfigFile());
        config.setRazorWorker("4");
        config.setNoRazor(false);
//        config.setDoFlanking(true);

        ArrayList<SampleInfo> sampleInfos = new ArrayList<>();

        for (int a : new int[]{1, 2, 4}) {
            BasicInfo basicInfo = new BasicInfo();
            basicInfo.fastq = "/Volumes/DATA/setA_test/rawdata/V300048326_L01_" + a + ".fq.gz";
            basicInfo.id = a;
            basicInfo.lane = "V300048326_L01";
            basicInfo.gender = Gender.male;

            SampleInfo sampleInfo = new SampleInfo();
            sampleInfo.setBasicInfo(basicInfo);

            sampleInfos.add(sampleInfo);
        }


        Xml xml = new Xml();
        Data data = xml.sampleInfoToXmlData(sampleInfos);
        xml.dataToXml(data, "/Volumes/DATA/setA_test/test.xml");
        config.setOutput("/Volumes/DATA/setA_test/test-output.xml");

        for (String locus : config.getParam().StrLocusOrder) {
            if (!config.getParam().locusSTR.containsKey(locus)) {
                System.out.println(locus);
            }
        }
        Analyse analyse = new Analyse( se400FqTools, razorOutParse, compress );

        ArrayList<SampleInfo> start = analyse.start("/Volumes/DATA/setA_test/test.xml");
//        System.out.println(start);
    }
}

