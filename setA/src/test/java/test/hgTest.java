package test;

import fayi.Analyse;
import fayi.Cal;
import fayi.HgPredict;
import fayi.WriteExcel.CalExcel;
import fayi.WriteExcel.SingleExcel;
import fayi.config.Config;
import fayi.config.Enum.Gender;
import fayi.config.Enum.Panel;
import fayi.flanking.FlankingInfo;
import fayi.flanking.FlankingSequence;
import fayi.seqParser.RazorOutParse;
import fayi.tableObject.SampleInfo;
import fayi.utils.SetAException;
import fayi.utils.Utils;
import fayi.xml.Objects.Data;
import fayi.xml.Objects.Sample;
import fayi.xml.Xml;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = {fayi.APP.class})
public class hgTest {

    @Autowired
    Analyse analyse;

    @Test
    public void test() throws SetAException, IOException, InterruptedException {
        System.setProperty("config.Artifact", Panel.setC.name());

        Config config = Config.getInstance();
        config.setMix(true);
        config.setOutputPath("/Volumes/DATA/setC_test");

        Xml xml = new Xml();
        Data data = xml.xmlToData("/Volumes/DATA/setC_test/L01_result.xml");
        SingleExcel singleExcel = new SingleExcel();
        ArrayList<SampleInfo> collect = (ArrayList<SampleInfo>) data.samples.stream().map(singleExcel::sampleToSampleInfo).collect(Collectors.toList());


        for (SampleInfo sampleInfo : collect) {

            new RazorOutParse().readSeqInformation(String.format("/Volumes/DATA/setC_test/assemble_fq/%03d_str.out", sampleInfo.getId()), sampleInfo);
            new RazorOutParse().readSeqInformation(String.format("/Volumes/DATA/setC_test/assemble_fq/%03d_snp.out", sampleInfo.getId()), sampleInfo);
            analyse.analyse(sampleInfo);
        }

        HgPredict hgPredict = new HgPredict();
        hgPredict.predictHg(collect);
        CalExcel calExcel = new CalExcel(collect, "cal");
        calExcel.start();
        config.setOutput(config.getOutputPath() + "/reports");
        singleExcel.start(collect);

    }

}
