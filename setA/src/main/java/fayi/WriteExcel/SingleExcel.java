package fayi.WriteExcel;

import fayi.config.Enum.Panel;
import fayi.tableObject.StrLocusInfo;
import fayi.config.Config;
import fayi.config.Enum.Gender;
import fayi.config.Param;
import fayi.tableObject.*;
import fayi.utils.ExcelUtils;
import fayi.utils.SetAException;
import fayi.utils.Utils;
import fayi.xml.Objects.CalResult;
import fayi.xml.Objects.LocusData;
import fayi.xml.Objects.Sample;
import fayi.xml.Objects.Site;
import fayi.xml.Xml;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SingleExcel {

    private final int AUTOCOVERAGESTARTROW = 15 + Param.getInstance().AutoStrLocusOrder.size() + 3;
    private final int YCOVERAGESTARTROW = 15 + Param.getInstance().YStrLocusOrder.size() + 3;
    private final int XCOVERAGESTARTROW = 15 + Param.getInstance().XStrLocusOrder.size() + 3;
    private final int SNPCOVERAGESTARTROW = 15 + Param.getInstance().SnpLocusOrder.size() + 3;

    private final int MHCOVERAGESTARTROW = 15 + Param.getInstance().MHLocusOrder.size() + 3;

    public SingleExcel() {
    }

    public void start(ArrayList<SampleInfo> sampleInfos) throws SetAException, IOException, InterruptedException {

        SumExcel sumExcel = new SumExcel();
        ExecutorService executorService = Executors.newFixedThreadPool(Config.getInstance().getWorker());
        AtomicInteger count = new AtomicInteger();
        for (SampleInfo sampleInfo : sampleInfos) {
            executorService.execute(() -> {
                FileInputStream fileInputStream = null;
                try {
                    fileInputStream = new FileInputStream(Config.getInstance().getReportTemplateXlsx());
                    XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
                    fileInputStream.close();

                    writeSampleXlsx(workbook, sampleInfo);
                    fillBasicInfo(workbook, "Autosomal STRs", sampleInfo.getCalResult().auto_Loci_Typed, sampleInfo);
                    fillBasicInfo(workbook, "Y STRs", sampleInfo.getCalResult().y_Loci_Typed, sampleInfo);
//                    fillBasicInfo(workbook, "mh", sampleInfo.getCalResult().mh_loci_Typed, sampleInfo);
                    fillBasicInfo(workbook, "X STRs", sampleInfo.getCalResult().x_Loci_Typed, sampleInfo);
                    fillBasicInfo(workbook, "iSNPs", sampleInfo.getCalResult().iSNP_Loci_Typed, sampleInfo);
                    writeLocusInfo(workbook, sampleInfo);

                    FileOutputStream fileOutputStream = new FileOutputStream(
                            Config.getInstance().getOutput() + "/" +
                                    sampleInfo.getBasicInfo().lane + "_" +
                                    sampleInfo.getBasicInfo().getId() +
                                    ("".equals(sampleInfo.getBasicInfo().getName()) ? "" : "_" + sampleInfo.getBasicInfo().name) + "_" +
                                    Config.getInstance().getArtifact() + ".xlsx");
                    workbook.write(fileOutputStream);
                    workbook.close();
                    fileOutputStream.close();
                    sumExcel.fillSumDepthList(sampleInfo);
                    sumExcel.fillSumList(sampleInfo);
                } catch (SetAException | IOException e) {
                    if (null != fileInputStream) {
                        try {
                            fileInputStream.close();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }
                    }
                    e.printStackTrace();
                }

            });
        }

        Utils.poolExecuterWaiter(executorService, "single report", count);
        sumExcel.fillSumXlsx();
    }

    public void start() throws SetAException, IOException, InterruptedException {
        Config config = Config.getInstance();
        ArrayList<Sample> samples = new Xml().xmlToData(config.getSampleFile()).samples;
        ArrayList<SampleInfo> sampleInfos = new ArrayList<>();
        for(Sample sample:samples){
            SampleInfo sampleInfo = sampleToSampleInfo(sample);
            sampleInfos.add(sampleInfo);
        }
        SumExcel sumExcel = new SumExcel();

        ExecutorService executorService = Executors.newFixedThreadPool(config.getWorker());
        AtomicInteger count = new AtomicInteger();
        for(SampleInfo sampleInfo:sampleInfos ){

            executorService.execute(() -> {
                sampleInfo.setATDepthValue();
                sampleInfo.strDataFilter();

                try {
                    FileInputStream fileInputStream = new FileInputStream(Config.getInstance().getReportTemplateXlsx());
                    XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
                    fileInputStream.close();

                    writeSampleXlsx( workbook,sampleInfo );
                    fillBasicInfo(workbook, "Autosomal STRs", sampleInfo.getCalResult().auto_Loci_Typed, sampleInfo);
                    fillBasicInfo(workbook, "Y STRs", sampleInfo.getCalResult().y_Loci_Typed, sampleInfo);
                    fillBasicInfo(workbook, "X STRs", sampleInfo.getCalResult().x_Loci_Typed, sampleInfo);
                    fillBasicInfo(workbook, "iSNPs", sampleInfo.getCalResult().iSNP_Loci_Typed, sampleInfo);
                    writeLocusInfo(workbook, sampleInfo);

                    FileOutputStream fileOutputStream = new FileOutputStream(
                            Config.getInstance().getOutput() + "/" + sampleInfo.getBasicInfo().lane + "_" + sampleInfo.getBasicInfo().getId() + ".xlsx");
                    workbook.write(fileOutputStream);
                    workbook.close();
                    fileOutputStream.close();
                } catch (IOException | SetAException e) {
                    log.error("生成个体报告的时候出错：" + e.getMessage());
                }

                sumExcel.fillSumDepthList(sampleInfo);
                sumExcel.fillSumList(sampleInfo);
                count.getAndIncrement();
            });

        }

        Utils.poolExecuterWaiter(executorService, "Export Individual Reports", count);
        sumExcel.fillSumXlsx();
    }

    public SampleInfo sampleToSampleInfo(Sample sample) {
        fayi.tableObject.SampleInfo sampleInfo = new SampleInfo();
        sampleInfo.setBasicInfo(sample.basicInfo);
        sampleInfo.setCalResult( sample.getCalResult()==null?new CalResult():sample.getCalResult());
        //str位点详细
        for(String locus:Param.getInstance().StrLocusOrder) {
            for(Site site:sample.sites.getStrSites()){
                if(locus.equals( site.Locus )) {
                    if (! sampleInfo.getStrData().containsKey( locus )) {
                        sampleInfo.getStrData().put( locus, new ArrayList<>() );
                    }
                    sampleInfo.getStrData().get( locus ).add( new StrInfo( site ).setAboveAT(true) );
                }
            }
        }

        //snp位点详细
        for(String locus:Param.getInstance().SnpLocusOrder) {
            for(Site site:sample.sites.getSnpSites()){
                if(locus.equals( site.Locus )) {
                    if (! sampleInfo.getSnpData().containsKey( locus )) {
                        sampleInfo.getSnpData().put( locus, new ArrayList<>() );
                    }
                    sampleInfo.getSnpData().get( locus ).add( new SnpInfo( site ).setAboveAT(true) );
                }
            }
        }
        //位点信息
        for(String locus:Param.getInstance().AutoStrLocusOrder) {
            for(LocusData locusData:sample.locusInfomations.getAutoStr()){
                if(locus.equals( locusData.getLocusName() )){
                    sampleInfo.getStrLocusInfo().put( locus,new StrLocusInfo( locusData ) );
                }
            }

        }
        for(String locus:Param.getInstance().XStrLocusOrder) {
            for(LocusData locusData:sample.locusInfomations.getXStr()){
                if(locus.equals( locusData.getLocusName() )){
                    sampleInfo.getStrLocusInfo().put( locus,new StrLocusInfo( locusData ) );
                }
            }
        }
        for(String locus:Param.getInstance().YStrLocusOrder) {
            for(LocusData locusData:sample.locusInfomations.getYStr()){
                if(locus.equals( locusData.getLocusName() )){
                    sampleInfo.getStrLocusInfo().put( locus,new StrLocusInfo( locusData ) );
                }
            }
        }

        for(String locus:sampleInfo.getStrLocusInfo().keySet()){
            sampleInfo.getStrLocusInfo().get(locus).setAllele(new ArrayList<>());
            for (StrInfo strInfo:sampleInfo.getStrData().getOrDefault(locus,new ArrayList<>())){
                if(strInfo.getTyped()) {
                    sampleInfo.getStrLocusInfo().get(locus).getAllele().add(strInfo);
                }
            }
        }

        for(String locus:Param.getInstance().SnpLocusOrder) {
            for(LocusData locusData:sample.locusInfomations.getIsnp()){
                if(locus.equals( locusData.getLocusName() )){
                    sampleInfo.getSnpLocusInfo().put( locus,new SnpLocusInfo( locusData ) );
                }
            }
        }

        for(String locus:sampleInfo.getSnpLocusInfo().keySet()){
            sampleInfo.getSnpLocusInfo().get(locus).setAllele(new ArrayList<>());
            for (SnpInfo snpInfo:sampleInfo.getSnpData().getOrDefault(locus,new ArrayList<>())){
                if(snpInfo.getTyped()) {
                    sampleInfo.getSnpLocusInfo().get(locus).getAllele().add(snpInfo);
                }
            }
        }

        return sampleInfo;
    }

    //
    private ArrayList<ArrayList<Object>> getchrList(SampleInfo sampleInfo, ArrayList<String> locusOrder) {
        ArrayList<ArrayList<Object>> data = new ArrayList<>();
        for (String locus : locusOrder) {
            for (StrInfo strInfo : sampleInfo.getStrDataAboveAt(locus)) {
                data.add(strInfo.formatAsList());
            }
        }
        return data;
    }

    private ArrayList<ArrayList<Object>> getSnpChrList(SampleInfo sampleInfo, ArrayList<String> locusOrder) {
        ArrayList<ArrayList<Object>> data = new ArrayList<>();
        for (String locus : locusOrder) {
            for (SnpInfo snpInfo : sampleInfo.getSnpDataAboveAt(locus)) {
                data.add(snpInfo.FormatAsList(sampleInfo.getCalResult().snpAvg));
            }
        }
        return data;
    }
    private ArrayList<ArrayList<Object>> getMHChrList(SampleInfo sampleInfo, ArrayList<String> locusOrder) {
        ArrayList<ArrayList<Object>> data = new ArrayList<>();
        for (String locus : locusOrder) {
            for (MHInfo mhInfo : sampleInfo.getMhData().getOrDefault( locus, new ArrayList<>() )) {
                data.add(mhInfo.FormatAsList(sampleInfo.getCalResult().snpAvg));
            }
        }
        return data;
    }

    public void writeSampleXlsx(XSSFWorkbook workbook, SampleInfo sampleInfo) {
        if (workbook.getSheet("Autosomal STRs") != null) {
            ExcelUtils.writeData(workbook, "Autosomal STRs", "AutosomalSTRs_Coverage_Table", AUTOCOVERAGESTARTROW, 0, getchrList(sampleInfo, Param.getInstance().AutoStrLocusOrder));
        }

        if (workbook.getSheet("X STRs") != null) {
            ExcelUtils.writeData(workbook, "X STRs", "XSTRs_Coverage_Table", XCOVERAGESTARTROW, 0, getchrList(sampleInfo, Param.getInstance().XStrLocusOrder));
        }

        if (!sampleInfo.getBasicInfo().gender.equals(Gender.female) && workbook.getSheet("Y STRs") != null) {
            ExcelUtils.writeData(workbook, "Y STRs", "YSTRs_Coverage_Table", YCOVERAGESTARTROW, 0, getchrList(sampleInfo, Param.getInstance().YStrLocusOrder));
        }

        if (Config.getInstance().getParam().SnpLocusOrder.size() > 0) {
            ExcelUtils.writeData(workbook, "iSNPs", "iSNPs_Coverage_Table", SNPCOVERAGESTARTROW, 0, getSnpChrList(sampleInfo, Param.getInstance().SnpLocusOrder));
        }
        if (Config.getInstance().getParam().MHLocusOrder.size() > 0) {
            ExcelUtils.writeData(workbook, "MH", "MH_Coverage_Table", MHCOVERAGESTARTROW, 0, getMHChrList(sampleInfo, Param.getInstance().MHLocusOrder));
        }
    }


    private void writeLocusInfo(XSSFWorkbook workbook, SampleInfo sampleInfo) throws SetAException {
        HashMap<String, StrLocusInfo> strLocusInfo = sampleInfo.getStrLocusInfo();
        if (workbook.getSheet("Autosomal STRs") != null) {
            ArrayList<ArrayList<Object>> autoAlleleData = createLocusInfoDataList(strLocusInfo, Param.getInstance().AutoStrLocusOrder);
            ExcelUtils.writeData(workbook, "Autosomal STRs", "AutosomalSTRs_Genotype_Table", 15, 0, autoAlleleData);
        }
        if (workbook.getSheet("X STRs") != null) {
            ArrayList<ArrayList<Object>> XAlleleData = createLocusInfoDataList(strLocusInfo, Param.getInstance().XStrLocusOrder);
            ExcelUtils.writeData(workbook, "X STRs", "XSTRs_Genotype_Table", 15, 0, XAlleleData);
        }

        if (!sampleInfo.getBasicInfo().gender.equals(Gender.female) && workbook.getSheet("Y STRs") != null) {
            ArrayList<ArrayList<Object>> YAlleleData = createLocusInfoDataList(strLocusInfo, Param.getInstance().YStrLocusOrder);
            ExcelUtils.writeData(workbook, "Y STRs", "YSTRs_Genotype_Table", 15, 0, YAlleleData);
        }

        if (Config.getInstance().getParam().SnpLocusOrder.size() > 0) {
            HashMap<String, SnpLocusInfo> snpLocusInfo = sampleInfo.getSnpLocusInfo();
            ArrayList<ArrayList<Object>> snpAlleleData = createLocusInfoDataList(snpLocusInfo, Param.getInstance().SnpLocusOrder);
            ExcelUtils.writeData(workbook, "iSNPs", "iSNPs_Genotype_Table", 15, 0, snpAlleleData);
        }


        if (Config.getInstance().getPanel().equals( Panel.setC )) {
            HashMap<String, MHLocusInfo> mhLocusInfo = sampleInfo.getMHLocusInfo();
            ArrayList<ArrayList<Object>> mhAlleleData = createLocusInfoDataList(mhLocusInfo, Param.getInstance().MHLocusOrder);
            ExcelUtils.writeData(workbook, "MH", "MH_Genotype_Table", 15, 0, mhAlleleData);
        }
    }


    private <T extends LocusInfoImpl> ArrayList<ArrayList<Object>> createLocusInfoDataList(HashMap<String, T> locusInfo, ArrayList<String> locusOrder) {
//        sampleInfo.sortStrAlleleName();
        ArrayList<ArrayList<Object>> data = new ArrayList<>();
        for (String locus : locusOrder) {
            if (!locusInfo.containsKey(locus)) {
                data.add(new ArrayList<>(Arrays.asList(locus, "", "")));
            } else {
                data.add(new ArrayList<>(Arrays.asList(locus,
                        locusInfo.get(locus).getAlleleNameAsString(true, true),
                        locusInfo.get(locus).getQCAsString(),
                        locusInfo.get(locus).getSequenceAsString()
                )));
            }
        }
        return data;
    }


    private void fillBasicInfo(XSSFWorkbook workbook, String sheetName, String lociTyped, SampleInfo sampleInfo) {
        XSSFSheet sheetAt = workbook.getSheet(sheetName);
        if (sheetAt == null) {
            return;
        }
        String sample = sampleInfo.getBasicInfo().name != null && !"".equals(sampleInfo.getBasicInfo().name) ? sampleInfo.getBasicInfo().name : sampleInfo.getBasicInfo().id + "";
        sheetAt.getRow(2).createCell(1).setCellValue(sample);
        sheetAt.getRow(3).createCell(1).setCellValue(sampleInfo.getBasicInfo().project);
        sheetAt.getRow(4).createCell(1).setCellValue(sampleInfo.getAnalysis());
        sheetAt.getRow( 5 ).createCell( 1 ).setCellValue( sampleInfo.getRun() );
        sheetAt.getRow( 6 ).createCell( 1 ).setCellValue( sampleInfo.getBasicInfo().gender.Eng );
        sheetAt.getRow( 7 ).createCell( 1 ).setCellValue( sampleInfo.getCreated() );
        sheetAt.getRow( 8 ).createCell( 1 ).setCellValue( sampleInfo.getUser() );
        sheetAt.getRow( 11 ).createCell( 1 ).setCellValue( lociTyped );
        sheetAt.getRow( 12 ).createCell( 1 ).setCellValue( sampleInfo.getCalResult().singleSource );
        sheetAt.getRow( 13 ).createCell( 1 ).setCellValue( sampleInfo.getCalResult().interlocusBalance );
    }
}
