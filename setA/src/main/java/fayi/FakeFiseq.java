package fayi;

import fayi.WriteExcel.SingleExcel;
import fayi.config.Config;
import fayi.tableObject.SampleInfo;
import fayi.tableObject.StrInfo;
import fayi.utils.ExcelUtils;
import fayi.utils.FileUtils;
import fayi.utils.SetAException;
import fayi.utils.Utils;
import fayi.xml.Objects.Data;
import fayi.xml.Objects.Sample;
import fayi.xml.Objects.Site;
import fayi.xml.Xml;
import jakarta.xml.bind.JAXBException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FakeFiseq {

    private String fakePath = "/DATA/fake1/";

    public void setFakePath(String fakePath) {
        this.fakePath = fakePath;
    }

    public void filterWithExcel() throws IOException, JAXBException, SetAException, InterruptedException {
        HashMap<String, Integer> changes = new HashMap<>();
        changes.put("D3S3045", -1);
        changes.put("D6S477", -4);
        changes.put("D15S659", -1);
        changes.put("D18S535", -1);
        changes.put("D7S3048", 1);
        changes.put("D4S2366", -2);

        FileUtils fileUtils = new FileUtils("/DATA/fake/batch.txt");
        String line;
        String[] values = null;
        while((line = fileUtils.readLine()) != null){
            if(line.startsWith("#")) continue;
            values = line.split("\t");
            break;
        }
        assert values != null;
        log.info(values[2]);
        String chip = values[0];
        String lane = values[1];
        String xmlFile = "/DATA/fake/" + values[2];
        Utils.checkReadFile(xmlFile);
        String calExcel = "/DATA/fake/" + values[3];
        Utils.checkReadFile(calExcel);


        log.info("Hello");
        Config config = Config.getInstance();

        FileInputStream fis = new FileInputStream(calExcel);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        Iterator<Sheet> iterator = wb.sheetIterator();
        Data result = new Data();
        SingleExcel singleExcel = new SingleExcel();
        Xml xml = new Xml();
        String required_sheet = chip+"_" + lane;

        ArrayList<SampleInfo> sampleInfos = new ArrayList<>();
        while(iterator.hasNext()){
            XSSFSheet sheet = (XSSFSheet)iterator.next();
            String sheetName = sheet.getSheetName();
//            Pattern pattern = Pattern.compile("_("+lane+").*");
//            Matcher matcher = pattern.matcher(sheetName);
            if (required_sheet.equals(sheetName)){
                log.info(sheetName);
                List<Integer> indexes = ExcelUtils.readData(wb, sheetName, 1, 0,0,0).stream().map(strings -> ((Float)Float.parseFloat(strings.get(0))).intValue()).collect(Collectors.toList());

                Data data = new Xml().xmlToData(xmlFile);
                List<Sample> samples = data.samples.stream().filter(sample -> indexes.contains(sample.basicInfo.id)).collect(Collectors.toList());
                log.info(samples.size()+"");
                for(Sample sample:samples){
                    SampleInfo sampleInfo = singleExcel.sampleToSampleInfo(sample);
                    for(String locus:config.getParam().YStrLocusOrder){
                        if("Y-indel".equals(locus)){
                            continue;
                        }
                        for(StrInfo strInfo:sampleInfo.getStrData().getOrDefault(locus, new ArrayList<>())){
                            if(Float.parseFloat(strInfo.getAlleleName()) >50 && strInfo.getReads() < 10){
                                strInfo.setTyped(false);
                                sampleInfo.getStrLocusInfo().get(locus).getAllele().remove(strInfo);
                            }
                        }
                    }
                    for(String locus:new String[]{"DYS447", "DYS448", "DYS449", "DYS518", "DYS527a/b", "DYS627"}){
                        for(StrInfo strInfo:sampleInfo.getStrData().getOrDefault(locus, new ArrayList<>())){
                            if(Float.parseFloat(strInfo.getAlleleName()) < 10 && strInfo.getReads() < 10){
                                strInfo.setTyped(false);
                                sampleInfo.getStrLocusInfo().get(locus).getAllele().remove(strInfo);
                            }
                        }
                    }
                    for(String locus:new String[]{"DYS643","DYS613"}){
                        for(StrInfo strInfo:sampleInfo.getStrData().getOrDefault(locus, new ArrayList<>())){
                            if(Float.parseFloat(strInfo.getAlleleName()) < 4 && strInfo.getReads() < 10){
                                strInfo.setTyped(false);
                                sampleInfo.getStrLocusInfo().get(locus).getAllele().remove(strInfo);
                            }
                        }
                    }
                    for(Site site:sample.sites.strSites){
                        if(!config.getParam().YStrLocusOrder.contains(site.Locus) || "Y-index".equals(site.Locus)){
                            continue;
                        }
                        if(Float.parseFloat(site.Genotype) >= 50){
                            site.Typed = "No";
                            continue;
                        }
                    }


                    log.warn("changing allele value!!!!!!!!!!!");
                    for(String locus:changes.keySet()){
                        for(StrInfo strInfo:sampleInfo.getStrData().getOrDefault(locus, new ArrayList<>())){
                            if (strInfo.getAlleleName().contains(".")) {
                                strInfo.setAlleleName(String.format("%.2f", Float.parseFloat(strInfo.getAlleleName()) + changes.get(locus)));
                            } else {
                                strInfo.setAlleleName((Integer.parseInt(strInfo.getAlleleName()) + changes.get(locus)) + "");
                            }
                        }
                    }
                    sampleInfos.add(sampleInfo);
                }
                result.samples.addAll(xml.sampleInfoToXmlData(sampleInfos).samples);
                break;
            }
        }
        fis.close();
        wb.close();
        result.sampleNum = result.samples.size();
        new Xml().dataToXml(result, config.getOutput());
        config.setOutput(config.getOutputPath());
        new SingleExcel().start(sampleInfos);
    }


    public void copyXml() throws SetAException, IOException {
        FileUtils fileUtils = new FileUtils(fakePath + "batch.txt");
        String line;
        String[] values = null;
        while((line = fileUtils.readLine()) != null){
            if(line.startsWith("#")) continue;
            values = line.split("\t");
            break;
        }
        assert values != null;
        log.info(values[2]);
        String chip = values[0];
        String lane = values[1];
        String xmlFile = fakePath + values[2];
        Utils.checkReadFile(xmlFile);
        String calExcel = fakePath + values[3];
        Utils.checkReadFile(calExcel);


        log.info("Hello");
        Config config = Config.getInstance();

        FileInputStream fis = new FileInputStream(calExcel);
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        Iterator<Sheet> iterator = wb.sheetIterator();
        Data result = new Data();
        String required_sheet = chip+"_" + lane;

        while(iterator.hasNext()){
            XSSFSheet sheet = (XSSFSheet)iterator.next();
            String sheetName = sheet.getSheetName();
            if (required_sheet.equals(sheetName)){
                log.info(sheetName);
                List<Integer> indexes = ExcelUtils.readData(wb, sheetName, 1, 0,0,0).stream().map(strings -> ((Float)Float.parseFloat(strings.get(0))).intValue()).collect(Collectors.toList());

                Data data = new Xml().xmlToData(xmlFile);
                List<Sample> samples = data.samples.stream().filter(sample -> indexes.contains(sample.basicInfo.id)).collect(Collectors.toList());
                log.info(samples.size()+"");

                result.samples.addAll(samples);
                break;
            }
        }
        fis.close();
        wb.close();
        result.sampleNum = result.samples.size();
        new Xml().dataToXml(result, config.getOutput());
    }

}
