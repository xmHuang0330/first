import fayi.APP;
import fayi.Analyse;
import fayi.Cal;
import fayi.CoreSeqCompress;
import fayi.WriteExcel.NgsPolyAT;
import fayi.WriteExcel.SingleExcel;
import fayi.config.Config;
import fayi.config.Enum.Panel;
import fayi.config.SnpMarker;
import fayi.flanking.FlankingSequence;
import fayi.seqParser.RazorOutParse;
import fayi.seqParser.SE400FqTools;
import fayi.tableObject.*;
import fayi.utils.SetAException;
import fayi.xml.Objects.Data;
import fayi.xml.Objects.Sample;
import fayi.xml.Xml;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = {APP.class})
public class NgsPolyATtest {
    @Autowired
    public CoreSeqCompress compress;
    @Autowired
    private RazorOutParse razorOutParse;
    @Autowired
    SE400FqTools se400FqTools;

    //统计二代stutter
    @Test
    public void test() throws IOException, SetAException {
        System.setProperty("config.Artifact", Panel.setB.name());

        Config config = Config.getInstance();
        config.setOutputPath("/Volumes/DATA/setB_test/");

        Xml xml = new Xml();
        Data data = new Data();
        for (int i = 1; i < 5; i++) {
            data.addSamples(xml.xmlToData("/Volumes/DATA/setB_test/ngstutter/87802_L0" + i + "_result.xml"));
            data.addSamples(xml.xmlToData("/Volumes/DATA/setB_test/ngstutter/87769_L0" + i + "_result.xml"));
        }

        SingleExcel singleExcel = new SingleExcel();
        List<SampleInfo> collect = data.samples.stream().map(singleExcel::sampleToSampleInfo).collect(Collectors.toList());

        for (SampleInfo sampleInfo : collect) {

            RazorOutParse razorOutParse = new RazorOutParse();
            razorOutParse.trimSequenceBatch( sampleInfo );

            sampleInfo.initLocusInfo();
            sampleInfo.setATDepthValue();
            //2. 设置reads的AT、IT信息
            sampleInfo.strDataFilter();

            Analyse analyse = new Analyse( se400FqTools, razorOutParse, compress );
            analyse.setSTRAllele(sampleInfo);
        }
        new NgsPolyAT().toExcel(collect);
    }

    @Test
    public void test1() {
        System.setProperty("test.artifactId", Panel.setB.name());

        Config config = Config.getInstance();
        config.setOutputPath("/Volumes/DATA/setB_test/");

        Xml xml = new Xml();
        Data data = new Data();
        data.addSamples(xml.xmlToData("/Users/kaidan/Documents/运行记录/广州市局/HCX/83505_L01_result.xml"));
        data.addSamples(xml.xmlToData("/Users/kaidan/Documents/运行记录/广州市局/HCX/83505_L02_result.xml"));
        data.addSamples(xml.xmlToData("/Users/kaidan/Documents/运行记录/广州市局/HCX/83505_L04_result.xml"));
        data.addSamples(xml.xmlToData("/Users/kaidan/Documents/运行记录/广州市局/HCX/83805_L03_result.xml"));
        data.addSamples(xml.xmlToData("/Users/kaidan/Documents/运行记录/广州市局/HCX/83805_L04_result.xml"));

        SingleExcel singleExcel = new SingleExcel();
        List<SampleInfo> collect = data.samples.stream().map(singleExcel::sampleToSampleInfo).collect(Collectors.toList());

        Cal cal = new Cal();
//        FileUtils fileUtils = new FileUtils("/Users/kaidan/Downloads/samples.txt");
//        ArrayList<String> samples = new ArrayList<>();
//        String line;
//        while((line = fileUtils.readLine())!=null){
//            samples.add(line.trim());
//        }

//        List<SampleInfo> collect1 = collect.stream().filter(sampleInfo -> samples.contains(sampleInfo.getBasicInfo().name)).collect(Collectors.toList());

//        for(String sample:samples){
//            for(SampleInfo sampleInfo:collect1){
//                if(sampleInfo.getBasicInfo().name.equals(sample)){
//                    sampleInfo.getStrLocusInfo().remove("DYS385a/b");
//                    analyse.analyse(sampleInfo);
//                    System.out.println(sampleInfo.getBasicInfo().name+"\t"+sampleInfo.getStrLocusInfo().get("DYS385a/b").getAlleleNameAsString(false,false));
//                }
//            }
//        }

        for (SampleInfo sampleInfo : collect) {
            cal.calAviliableDepthCoverageOfSample(sampleInfo);
            cal.calLocusDepthAvg(sampleInfo);
            cal.mixLevel(sampleInfo);
            int genoDP = 0;
            int noDP = 0;
            List<StrInfo> orDefault = sampleInfo.getStrData().getOrDefault("DYS385a/b", new ArrayList<>());
            orDefault.sort((o1, o2) -> Float.compare(o1.getReads(), o2.getReads()));
            int count = 0;
            for (StrInfo strInfo : orDefault) {
                if (strInfo.getTyped() && count < 2) {
                    count += 1;
                    genoDP += strInfo.getReads();
                } else if (!strInfo.getIsNGStutter() && !strInfo.getIsStutter()) {
                    noDP += strInfo.getReads();
                }
            }
            System.out.print(sampleInfo.getBasicInfo().lane + "\t");
            System.out.print(sampleInfo.getBasicInfo().id + "\t");
            System.out.print(sampleInfo.getBasicInfo().tablet + "\t");
            System.out.print(sampleInfo.getBasicInfo().gender + "\t");
            System.out.print(sampleInfo.getBasicInfo().type + "\t");
            System.out.print(sampleInfo.getBasicInfo().project + "\t");
            System.out.print(sampleInfo.getBasicInfo().name + "\t");
            System.out.print(sampleInfo.getCalResult().availableDepth + "\t");
            System.out.print(sampleInfo.getCalResult().fqReads + "\t");
            System.out.print(sampleInfo.getCalResult().availableDepth / sampleInfo.getCalResult().fqReads + "\t");
            System.out.print(sampleInfo.getCalResult().getInterlocusBalance() + "\t");
            System.out.print(sampleInfo.getCalResult().getSingleSource() + "\t");
            System.out.print(sampleInfo.getChrACCount().get("Auto") + "\t");
            System.out.print(sampleInfo.getChrACCount().get("Y") + "\t");
            System.out.print(sampleInfo.getCalResult().getAuto_Loci_Typed() + "\t");
            System.out.print(sampleInfo.getCalResult().getY_Loci_Typed() + "\t");
            System.out.print(sampleInfo.getCalResult().getStrAvg() + "\t");
            System.out.print(sampleInfo.getCalResult().getStrSTD() + "\t");
            System.out.print(sampleInfo.getCalResult().strDepthBelow30 + "\t");
            System.out.print(sampleInfo.getCalResult().strDepthBelow100 + "\t");
            System.out.print(genoDP == 0 ? "#" : genoDP / (genoDP + noDP) + "\t");
            System.out.print(genoDP + "\t" + noDP);
            ArrayList<StrInfo> allele = sampleInfo.getStrLocusInfo().get("DYS385a/b").getAllele();
            if (allele.size() < 3) {
                System.out.println("\t" + allele.stream().map(SeqInfo::getAlleleName).collect(Collectors.joining(",")));
            } else {
                System.out.println("\t" + allele.stream().sorted((o1, o2) -> Float.compare(o1.getReads(), o2.getReads())).map(SeqInfo::getAlleleName).collect(Collectors.joining(",")));

            }

        }

    }

    //统计序列频率、snp频率
    @Test
    public void test2() throws InterruptedException {
        System.setProperty("config.Artifact", Panel.setB.name());

        Config config = Config.getInstance();
        config.setOutputPath("/Volumes/DATA/setB_test/");

        Xml xml = new Xml();
        Data data = new Data();
        SingleExcel singleExcel = new SingleExcel();
        FlankingSequence flankingSequence = new FlankingSequence();
        ArrayList<SampleInfo> sampleInfos = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            Data data1 = xml.xmlToData("/Users/kaidan/Documents/运行记录/99652/L0" + i + "_result.xml");
            ArrayList<SampleInfo> collect = (ArrayList<SampleInfo>) data1.samples.stream().map(singleExcel::sampleToSampleInfo).collect(Collectors.toList());
            flankingSequence.batchAlign(collect);
            sampleInfos.addAll(collect);


            data1 = xml.xmlToData("/Users/kaidan/Documents/运行记录/99637/L0" + i + "_result.xml");
            collect = (ArrayList<SampleInfo>) data1.samples.stream().map(singleExcel::sampleToSampleInfo).collect(Collectors.toList());
            flankingSequence.batchAlign(collect);
            sampleInfos.addAll(collect);
        }

        HashMap<String, HashMap<String, Integer>> locusSeq = new HashMap<>();
        HashMap<String, HashMap<String, Integer>> locusSnp = new HashMap<>();
        for (String locus : config.getParam().StrLocusOrder) {
            HashMap<String, Integer> values = new HashMap<>();
            HashMap<String, Integer> snp_count = new HashMap<>();
            for (SampleInfo sampleInfo : sampleInfos) {
                for (StrInfo strInfo : sampleInfo.getStrLocusInfo().get(locus).getAllele()) {
                    values.put(strInfo.getRepeatSequence() + "\t" + strInfo.getAlleleName(), values.getOrDefault(strInfo.getRepeatSequence() + "\t" + strInfo.getAlleleName(), 0) + 1);

                    for (SnpMarker snpMarker : strInfo.getFlankingSnp()) {
                        snp_count.put(snpMarker.toMarker(), snp_count.getOrDefault(snpMarker.toMarker(), 0) + 1);
                    }
                }
            }
            locusSnp.put(locus, snp_count);
            locusSeq.put(locus, values);
        }
//        for(String locus:locusSeq.keySet()){
//            for(String seq:locusSeq.get(locus).keySet()) {
//                System.out.println(locus + "\t" + seq + "\t" + locusSeq.get(locus).get(seq));
//            }
//        }


        for (String locus : locusSnp.keySet()) {
            for (String seq : locusSnp.get(locus).keySet()) {
                System.out.println(locus + "\t" + seq + "\t" + locusSnp.get(locus).get(seq));
            }
        }

    }

    @Test
    public void test3() {
        System.setProperty("test.artifactId", "setB");
        Config.getInstance();
        Data data = new Xml().xmlToData("/Users/kaidan/Documents/运行记录/87769/L03_result.xml");
        SingleExcel singleExcel = new SingleExcel();
        for (Sample sample : data.samples) {
            SampleInfo sampleInfo = singleExcel.sampleToSampleInfo(sample);
            StrLocusInfo strLocusInfo = sampleInfo.getStrLocusInfo().get("Penta-E");
            System.out.println(sampleInfo.getBasicInfo().id + "\t" +
                    sampleInfo.getBasicInfo().name + "\t" +
                    (strLocusInfo.getIBObserving().size() > 0 ? strLocusInfo.getIBObserving().get(0) : "") + "\t" +
                    strLocusInfo.getAlleleNameAsString(false, false));
        }

    }

}
