package fayi.WriteExcel;

import ch.qos.logback.classic.util.LogbackMDCAdapter;
import fayi.Cal;
import fayi.config.DefaultParam;
import fayi.tableObject.StrLocusInfo;
import fayi.config.Config;
import fayi.config.Enum.Gender;
import fayi.config.Param;
import fayi.tableObject.*;
import fayi.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

@Slf4j
public class CalExcel {

    private final HashMap<String, Float> snpMeanDepthOfSamples_Male = new HashMap<>();
    private final HashMap<String, Float> strMeanDepthOfSamples_Male = new HashMap<>();
    private final HashMap<String, Float> strMeanDepthOfSamples = new HashMap<>();
    private final HashMap<String, Float> snpMeanDepthOfSamples_All = new HashMap<>();
    private final HashMap<String, Float> snpMeanDepthOfSamples_Female = new HashMap<>();
    private final HashMap<String, Float> strMeanDepthOfSamples_Female = new HashMap<>();
    private Config config;
    private final String suffix;
    private final String prefix;
    private final ArrayList<SampleInfo> sampleInfos;
    private final Param param = Param.getInstance();
    private HashMap<String, Float> mhMeanDepthOfSamples_Male = new HashMap<>();
    private HashMap<String, Float> mhMeanDepthOfSamples_Female = new HashMap<>();
    private HashMap<String, Float> mhMeanDepthOfSamples_All = new HashMap<>();

    public CalExcel(ArrayList<SampleInfo> sampleInfos, String suffix) {
        this.sampleInfos = sampleInfos;
        this.prefix = sampleInfos.get(0).getBasicInfo().lane;
        sampleInfos.sort(Comparator.comparing(o -> o.getBasicInfo().id));
        this.suffix = suffix;
    }

    public void start() throws IOException {
        config = Config.getInstance();

        int STRDATASTARTCOL = 20;
        switch (config.getPanel()) {
            case setA: {
                STRDATASTARTCOL = 27;
                break;
            }
            case setB: {
                STRDATASTARTCOL = 28;
                break;
            }
            case setC: {
                STRDATASTARTCOL = 31;
                break;
            }
            case yarn: {
                STRDATASTARTCOL = 23;
                break;
            }
            case test: {
                STRDATASTARTCOL = 28;
                break;
            }
        }
        FileInputStream fileInputStream = new FileInputStream(config.getCalXlsx());
        XSSFWorkbook calWorkbook = new XSSFWorkbook(fileInputStream);
        fileInputStream.close();
        //计算所有样本的 位点间有效reads均值
        calLocusDepthAverageOfSamples();
//        //生成数据 输出到excel
//        ArrayList<ArrayList<Object>> dataList = createLaneDataList();
//        ExcelUtils.writeData(calWorkbook, "DepthCov", "AvgDepDET", 2, 1, dataList);
//
//        ArrayList<ArrayList<Object>> stdofLane = createdepthSTDofLane();
//        ExcelUtils.writeData(calWorkbook, "DepthCov", "AvgDepSTD", 0, 5, stdofLane);

        ArrayList<ArrayList<Object>> locusDepthAverageOfGender = createLocusDepthAverageOfGender();
        ExcelUtils.writeData(calWorkbook, "Lane", "", 0, STRDATASTARTCOL, locusDepthAverageOfGender);

        ArrayList<ArrayList<Object>> samplesDataList = createSamplesDataList();
//        ArrayList<String> laneHeader = createLaneHeader(config.getPanel());
//        ExcelUtils.writeHeader(calWorkbook,"Lane",3,1,laneHeader);
        ExcelUtils.writeData(calWorkbook, "Lane", "SampleSum", 4, 1, samplesDataList, false);

        //geno表
        ArrayList<ArrayList<Object>> genoMergedList = createMergedGenoList();
//        ArrayList<String> genoHeader = createGenoHeader();
//        ExcelUtils.writeHeader(calWorkbook,"geno",0,0,genoHeader);
        ExcelUtils.writeData(calWorkbook, "geno", "geno", 1, 0, genoMergedList);
//        ExcelUtils.writeData(calWorkbook, "unMergedGeno", "", 0, 0, genoList);

        ArrayList<ArrayList<Object>> compressedSeqData = createCompressedSeqData();
        ExcelUtils.writeData(calWorkbook, "genoSeq", "geno", 0, 0, compressedSeqData);

//        ArrayList<ArrayList<Object>> genoTypedDepthList = createGenoTypedDepthList();
//        ExcelUtils.writeData(calWorkbook, "genoDepth", "geno", 0, 0, genoTypedDepthList);

        //信噪比
        createNoDpData(calWorkbook);

        //stutter Proption
        ArrayList<ArrayList<Object>> stutterPercData = createStutterPerc();
        ExcelUtils.writeData(calWorkbook, "stutter%", "stutterPercentage", 1, 0, stutterPercData);
        //拆分率
        ArrayList<ArrayList<Object>> ReadsNumAsList = getUnSplitReadsNum();
        ExcelUtils.writeData(calWorkbook, "Lane", "UnsplitReads", 0, 5, ReadsNumAsList);

        XSSFFormulaEvaluator.evaluateAllFormulaCells(calWorkbook);
        FileOutputStream fos = new FileOutputStream(config.getOutputPath() + "/" + prefix + "_" + config.getArtifact() + "_" + suffix + "_" + (config.getNoFilter() ? "10x" : DefaultParam.DEFAULT_DP_LIMIT + "x") + ".xlsx");
        calWorkbook.write(fos);
        fos.flush();
        fos.close();
        calWorkbook.close();
        fileInputStream.close();
    }

    private ArrayList<ArrayList<Object>> createStutterPerc() {
        ArrayList<ArrayList<Object>> data = new ArrayList<>();

        for(SampleInfo sampleInfo:sampleInfos){
            ArrayList<Object> line = new ArrayList<>();
            line.add(sampleInfo.getId());
            float total = 0;
            int count = 0;
            for(String locus:param.StrLocusOrder){
                float maxStutter = 0;
                for(StrInfo strInfo: sampleInfo.getStrLocusInfo().getOrDefault(locus,new StrLocusInfo()).getAllele()){
                    if(strInfo.getStutters().size() == 1){
                        float stutter = strInfo.getStutters().get(0).getReads()/strInfo.getReads();
                        if(stutter > maxStutter) maxStutter = stutter;
                    }else if(strInfo.getStutters().size() > 1){
                        for(StrInfo stutterInfo:strInfo.getStutters()){
                            float stutter = stutterInfo.getReads()/strInfo.getReads();
                            if(stutter > maxStutter) maxStutter = stutter;
                        }
                    }
                    count += 1;
                }
                total += maxStutter;
                line.add(maxStutter == 0.0?"":maxStutter);
            }
            line.add(1,total/count);
            data.add(line);
        }
        return data;
    }

    private void createNoDpData(XSSFWorkbook wb) {
        ArrayList<ArrayList<Object>> noData = new ArrayList<>();
        ArrayList<ArrayList<Object>> genoData = new ArrayList<>();

        ArrayList<Object> header = new ArrayList<>();
        header.add("index");
        header.addAll(param.StrLocusOrder);

        noData.add(header);
        genoData.add(header);

        for (SampleInfo sampleInfo : sampleInfos) {
            ArrayList<Object> noValues = new ArrayList<>();
            ArrayList<Object> genoValues = new ArrayList<>();
            noValues.add(sampleInfo.getId());
            genoValues.add(sampleInfo.getId());
            for (String locus : Config.getInstance().getParam().StrLocusOrder) {
                float genodp = 0;
                float nodp = 0;
                int count = 0;
                for (StrInfo strInfo : sampleInfo.getStrDataAboveAt( locus )) {
                    if (strInfo.getTyped() && count < 2) {
                        count++;
                        genodp += strInfo.getReadsWithNGS();
                    } else if (! strInfo.getIsNGStutter()) {
                        nodp += strInfo.getReads();
                    }
                }
                noValues.add(nodp);
                genoValues.add(genodp);
            }
            noData.add(noValues);
            genoData.add(genoValues);
        }

        ExcelUtils.writeData(wb, "genoDp", "geno", 0, 0, genoData);
        ExcelUtils.writeData(wb, "noDP", "geno", 0, 0, noData);

    }

//    private ArrayList<String> createGenoHeader() {
//        ArrayList<String> header = new ArrayList<>(Arrays.asList("sample/Locus", "gender", "type", "project", "name"));
//        header.addAll(config.getParam().StrLocusOrder);
//        header.addAll(config.getParam().SnpLocusOrder);
//        return header;
//    }

//    private ArrayList<String> createLaneHeader(Panel panel) {
//        ArrayList<String> header = new ArrayList<>();
//        switch (panel) {
//            case setA:
//                header = new ArrayList<>(Arrays.asList("index", "tablet", "gender", "type", "project", "name"
//                        , "有效reads", "总reads", "有效reads比", "interlocus_balance", "single_source"
//                        , "Auto_AlleleCount", "X_AlleleCount", "Y_AlleleCount", "auto_loci_typed", "x_loci_typed", "y_loci_typed"
//                        , "STR均值", "STR标准化STD", "<30X NUM", "<100X NUM"));
//                break;
//            case setB:
//                header = new ArrayList<>(Arrays.asList("index", "tablet", "gender", "type", "project", "name"
//                        , "有效reads", "总reads", "有效reads比", "interlocus_balance", "single_source"
//                        , "X_AlleleCount", "Y_AlleleCount", "x_loci_typed", "y_loci_typed"
//                        , "STR均值", "STR标准化STD", "<30X NUM", "<100X NUM"));
//                break;
//            case yarn:
//                header = new ArrayList<>(Arrays.asList("index", "tablet", "gender", "type", "project", "name"
//                        , "有效reads", "总reads", "有效reads比", "interlocus_balance", "single_source"
//                        , "Y_AlleleCount", "y_loci_typed"
//                        , "STR均值", "SNP均值", "STR标准化STD", "SNP标准化STD", "<30X NUM", "<100X NUM"));
//                break;
//        }
//        header.addAll(config.getParam().StrLocusOrder);
//        header.addAll(config.getParam().SnpLocusOrder);
//        return header;
//    }

    private ArrayList<ArrayList<Object>> createCompressedSeqData() {

        ArrayList<ArrayList<Object>> data = new ArrayList<>();

        ArrayList<Object> header = new ArrayList<>();
        header.add("sample");
        header.addAll(config.getParam().StrLocusOrder);
        data.add(header);


        for (SampleInfo sampleInfo : sampleInfos) {
            ArrayList<Object> values = new ArrayList<>();
            values.add(sampleInfo.getBasicInfo().id);
            for (String locus : config.getParam().StrLocusOrder) {
                StringBuilder seq = new StringBuilder();
                for (StrInfo strInfo : sampleInfo.getStrLocusInfo().get(locus).getAllele()) {
                    seq.append(",").append(strInfo.getRepeatSequence());
                }
                values.add(seq.toString().replaceFirst(",", ""));
            }
            data.add(values);
        }
        return data;
    }

    public void ibOutput(ArrayList<SampleInfo> sampleInfos) {
        FileUtils fileUtils = new FileUtils(config.getOutputPath() + "/ab_ibo.txt");
        StringBuilder line = new StringBuilder("index/locus");
        for (String str : config.getParam().StrLocusOrder) {
            if (str.endsWith("a/b")) {
                for (int i = 0; i < 4; i++) {
                    line.append("\t").append(str).append(i + 1);
                }
                for (int i = 0; i < 5; i++) {
                    line.append("\t" + "allele").append(i + 1);
                }
            }
        }
        fileUtils.writeLine(line.toString());
        for(SampleInfo sampleInfo:sampleInfos){
            ArrayList<Double> ibo = new ArrayList<>();
            ArrayList<String> alleles = new ArrayList<>();
            for(String locus:config.getParam().StrLocusOrder){
                if(locus.endsWith("a/b")){
                    List<StrInfo> sameLocusList = sampleInfo.getStrDataAboveAt( locus );
                    sameLocusList.sort(Comparator.comparing(StrInfo::getReads, Comparator.reverseOrder()));
                    if (sameLocusList.size() > 0){
                        StrInfo strInfo = sameLocusList.get(0);
                        strInfo.setTempTyped(true);
                        alleles.add(strInfo.getAlleleName());
                        double IBObserving;
                        //如果当前locus是双等位基因，则计算另一个allele
                        StrInfo next;
                        for (int i = 1; i < 5; i++) {
                            //次高reads
                            if(sameLocusList.size() > i) {
                                next = sameLocusList.get(i);
                                IBObserving = Double.parseDouble(strInfo.getAlleleName()) > Double.parseDouble(next.getAlleleName()) ? strInfo.getReads() / next.getReads() : next.getReads() / strInfo.getReads();
                                ibo.add(IBObserving);
                                alleles.add(next.getAlleleName());
                            }else{
                                ibo.add(0d);
                                alleles.add("");
                            }
                        }
                    }else{
                        alleles.add("");
                        for (int i = 1; i < 5; i++) {
                            //次高reads
                                ibo.add(0d);
                                alleles.add("");
                            }
                        }
                    }
                }
            line = new StringBuilder(sampleInfo.getBasicInfo().id + "");
            for(String str:config.getParam().StrLocusOrder){
                if(str.endsWith("a/b")){
                    for (int i = 0; i < 4; i++) {
                        if(ibo.size() > i){
                            line.append("\t").append(ibo.get(i));
                        }else{
                            line.append("\t");
                        }
                    }
                    for (int i = 0; i < 5; i++) {
                        if(alleles.size() > i){
                            line.append("\t").append(alleles.get(i));
                        }else{
                            line.append("\t");
                        }
                    }
                }
            }
            fileUtils.writeLine(line.toString());
        }
        fileUtils.finishWrite();
    }

    private ArrayList<ArrayList<Object>> createGenoTypedDepthList() {
        ArrayList<ArrayList<Object>> data = new ArrayList<>();

        ArrayList<Object> header = new ArrayList<>();
        header.add("index");
        header.add("type");
        header.add("project");
        header.add("name");
        header.addAll(param.StrLocusOrder);
        data.add(header);
        for(SampleInfo sampleInfo:sampleInfos){
            ArrayList<Object> values = new ArrayList<>();
            values.add( sampleInfo.getBasicInfo().id );
            values.add(sampleInfo.getBasicInfo().type + "");
            values.add(sampleInfo.getBasicInfo().project + "");
            values.add(sampleInfo.getBasicInfo().name + "");
            HashMap<String, StrLocusInfo> strLocusInfo = sampleInfo.getStrLocusInfo();
            for (String locus : param.StrLocusOrder) {
                StringBuilder temp = new StringBuilder();
                for (SeqInfo strInfo : strLocusInfo.getOrDefault(locus, StrLocusInfo.getEmpty()).getAllele()) {
                    temp.append(",").append(strInfo.getReads());
                }
                values.add(temp.toString().replaceFirst(",", ""));
//                double noDP = 0;
//                for(SeqInfo strInfo:sampleInfo.getStrDataAboveAt(locus)){
//                    if(!strInfo.getTyped() && !((StrInfo)strInfo).getIsStutter()){
//                        noDP += strInfo.getReads();
//                    }
//                }
//                values.add(noDP);
            }
            for (String locus : param.SnpLocusOrder) {
                StringBuilder temp = new StringBuilder();
                for (SeqInfo strInfo : strLocusInfo.getOrDefault(locus, StrLocusInfo.getEmpty()).getAllele()) {
                    temp.append(",").append(strInfo.getReads());
                }
                values.add(temp.toString().replaceFirst(",", ""));
            }
            data.add(values);
        }
        return data;
    }



    //计算所有样本的位点深度均值
    public void calLocusDepthAverageOfSamples() {
        for (String locus : Param.getInstance().StrLocusOrder) {
            float SumDepth_Male = 0;
            float SumDepth_Female = 0;
            float sumDepth = 0;
            float maleCount = 0;
            float femaleCount = 0;
            for (SampleInfo sampleInfo : sampleInfos) {
                StrLocusInfo strLocusInfo = sampleInfo.getStrLocusInfo().getOrDefault(locus,StrLocusInfo.getEmpty());
                sumDepth += strLocusInfo.getTotalDepth();
                if(Gender.male.equals(sampleInfo.getBasicInfo().gender)){
                    maleCount++;
                    SumDepth_Male += strLocusInfo.getTotalDepth();
                }else if(Gender.female.equals(sampleInfo.getBasicInfo().gender)){
                    femaleCount++;
                    SumDepth_Female += strLocusInfo.getTotalDepth();
                }
            }
            strMeanDepthOfSamples_Male.put(locus, SumDepth_Male / maleCount);
            strMeanDepthOfSamples_Female.put(locus, SumDepth_Female / femaleCount);
            strMeanDepthOfSamples.put(locus,sumDepth / sampleInfos.size());
        }
        for(String locus:Param.getInstance().SnpLocusOrder){
            float sumDepth_Male = 0;
            float sumDepth = 0;
            float maleCount = 0;
            float SumDepth_Female = 0;
            float femaleCount = 0;
            for (SampleInfo sampleInfo : sampleInfos) {
                SnpLocusInfo strLocusInfo = sampleInfo.getSnpLocusInfo().getOrDefault( locus, new SnpLocusInfo(locus) );
                sumDepth += strLocusInfo.getTotalDepth();
                if(Gender.male.equals(sampleInfo.getBasicInfo().gender)){
                    maleCount++;
                    sumDepth_Male += strLocusInfo.getTotalDepth();
                }else if(Gender.female.equals(sampleInfo.getBasicInfo().gender)){
                    femaleCount++;
                    SumDepth_Female += strLocusInfo.getTotalDepth();
                }
            }
            snpMeanDepthOfSamples_Male.put( locus,sumDepth_Male/maleCount );
            snpMeanDepthOfSamples_Female.put(locus, SumDepth_Female / femaleCount);
            snpMeanDepthOfSamples_All.put( locus,sumDepth/sampleInfos.size() );
        }
        for(String locus:Param.getInstance().MHLocusOrder){
            float sumDepth_Male = 0;
            float sumDepth = 0;
            float maleCount = 0;
            float SumDepth_Female = 0;
            float femaleCount = 0;
            for (SampleInfo sampleInfo : sampleInfos) {
                MHLocusInfo strLocusInfo = sampleInfo.getMHLocusInfo().getOrDefault( locus, new MHLocusInfo(locus) );
                sumDepth += strLocusInfo.getReads();
                if(Gender.male.equals(sampleInfo.getBasicInfo().gender)){
                    maleCount++;
                    sumDepth_Male += strLocusInfo.getReads();
                }else if(Gender.female.equals(sampleInfo.getBasicInfo().gender)){
                    femaleCount++;
                    SumDepth_Female += strLocusInfo.getReads();
                }
            }
            mhMeanDepthOfSamples_Male.put( locus,sumDepth_Male/maleCount );
            mhMeanDepthOfSamples_Female.put(locus, SumDepth_Female / femaleCount);
            mhMeanDepthOfSamples_All.put( locus,sumDepth/sampleInfos.size() );
        }
    }
    private ArrayList<ArrayList<Object>> createdepthSTDofLane() {
        ArrayList<Float> maleValues = new ArrayList<>();
        ArrayList<Float> femaleValues = new ArrayList<>();
        for (String locus: param.StrLocusOrder) {
            if(param.YStrLocusOrder.contains( locus )){
                maleValues.add( strMeanDepthOfSamples_Male.getOrDefault( locus,0f ) );
            }
        }
        //?位点间深度均一性 是否包含snp位点?//
//        for(String locus: param.SnpLocusOrder){
//            femaleValues.add( strMeanDepthOfSamples_Female.getOrDefault( locus,0d ) );
//            maleValues.add( strMeanDepthOfSamples_Male.getOrDefault( locus,0d ) );
//        }
        ArrayList<ArrayList<Object>> data = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        values.add(Cal.calSTDEVA( maleValues ));
        values.add(Cal.calSTDEVA( femaleValues ));
        data.add( values );
        return data;
    }

    private ArrayList<ArrayList<Object>> getUnSplitReadsNum() {
        ArrayList<ArrayList<Object>> data = new ArrayList<>();
        if(null == config.getUnsplitRawData()){
            String summaryTable = config.getRawDataPath() + "/summaryTable.csv";
            try {
                Utils.checkReadFile(summaryTable);
                FileUtils statFileUtil = new FileUtils(summaryTable);
                String line;
                while ((line=statFileUtil.readLine()) != null) {
                    if(line.toLowerCase().startsWith( "totalreads" )){
                        ArrayList<Object> objects = new ArrayList<>();
                        objects.add( Double.parseDouble(line.trim().split(",")[1]) * 1000000 );
                        data.add( objects );
                    }else if(line.toLowerCase().startsWith( "splitrate" )){
                        ArrayList<Object> objects = new ArrayList<>();
                        objects.add( Double.parseDouble( line.trim().split( "," )[1] ) );
                        data.add( objects );
                    }
                }
                statFileUtil.readLine();
            } catch (SetAException e ) {
                System.out.println("未拆分文件未找到");
            }
        }else{
            System.out.println("未提供未拆分数据，无法获取Reads数");
        }
        return data;
    }

    private ArrayList<ArrayList<Object>> createLocusDepthAverageOfGender(){

        ArrayList<ArrayList<Object>> data = new ArrayList<>();
        ArrayList<Object> femaleLocusAvg = new ArrayList<>();
        ArrayList<Object> maleLocusAvg = new ArrayList<>();
        ArrayList<Object> allLocusAvg = new ArrayList<>();
        for (String locus : param.StrLocusOrder) {
            maleLocusAvg.add( strMeanDepthOfSamples_Male.get(locus));
            femaleLocusAvg.add( strMeanDepthOfSamples_Female.get(locus));
            allLocusAvg.add( strMeanDepthOfSamples.get(locus));
        }
        for (String locus : param.SnpLocusOrder) {
            maleLocusAvg.add( snpMeanDepthOfSamples_Male.get(locus));
            femaleLocusAvg.add( snpMeanDepthOfSamples_Female.get(locus));
            allLocusAvg.add( snpMeanDepthOfSamples_All.get(locus));
        }
        for (String locus : param.MHLocusOrder) {
            maleLocusAvg.add( mhMeanDepthOfSamples_Male.get(locus));
            femaleLocusAvg.add( mhMeanDepthOfSamples_Female.get(locus));
            allLocusAvg.add( mhMeanDepthOfSamples_All.get(locus));
        }

        data.add(femaleLocusAvg);
        data.add(maleLocusAvg);
        data.add(allLocusAvg);
        return data;
    }

    private ArrayList<ArrayList<Object>> createSamplesDataList() {
        ArrayList<ArrayList<Object>> sampleDataList = new ArrayList<>();

        for (SampleInfo sampleInfo : sampleInfos) {
            ArrayList<Object> values = new ArrayList<>();
            values.add(sampleInfo.getBasicInfo().id);
            values.add(sampleInfo.getBasicInfo().tablet);
            values.add(sampleInfo.getBasicInfo().well);
            values.add(sampleInfo.getBasicInfo().gender);
            values.add(sampleInfo.getBasicInfo().type);
            values.add(sampleInfo.getBasicInfo().project);
            values.add(sampleInfo.getBasicInfo().name);
            values.add(sampleInfo.getCalResult().availableDepth);
            values.add(sampleInfo.getCalResult().fqReads);
            values.add(sampleInfo.getCalResult().availableDepth / sampleInfo.getCalResult().fqReads);
            values.add(sampleInfo.getCalResult().getInterlocusBalance());
            values.add(sampleInfo.getCalResult().getSingleSource());
            values.add(sampleInfo.getCalResult().getHighStutter());
            values.add(sampleInfo.getCalResult().getNoCount());
            values.add(sampleInfo.getCalResult().getNoDP());
            values.add("");
            panelInfo(values, sampleInfo);
            values.add(sampleInfo.getLowerThan30());
            values.add(sampleInfo.getLowerThan100());
            for (String locus : config.getParam().StrLocusOrder) {
                values.add(sampleInfo.getStrLocusInfo().getOrDefault(locus, new StrLocusInfo()).getTotalDepth());
            }
            for (String locus : config.getParam().SnpLocusOrder) {
                values.add(sampleInfo.getSnpLocusInfo().getOrDefault(locus, new SnpLocusInfo(locus)).getTotalDepth());
            }
            for (String locus : config.getParam().MHLocusOrder) {
                values.add(sampleInfo.getMHLocusInfo().getOrDefault(locus, new MHLocusInfo(locus)).getReads());
            }
            sampleDataList.add(values);
        }
        return sampleDataList;
    }

    private void panelInfo(ArrayList<Object> values, SampleInfo sampleInfo) {
        switch (Config.getInstance().getPanel()) {
            case setA: {
                values.add(sampleInfo.getChrACCount().get("Auto"));
                values.add(sampleInfo.getChrACCount().get("X"));
                values.add(sampleInfo.getChrACCount().get("Y"));
                values.add(sampleInfo.getCalResult().getATyped());
                values.add(sampleInfo.getCalResult().getXTyped());
                values.add(sampleInfo.getCalResult().getYTyped());
                values.add(sampleInfo.getCalResult().getPP21Count());
                values.add(sampleInfo.getCalResult().getY41Count());
                values.add(sampleInfo.getCalResult().getStrAvg());
                values.add(sampleInfo.getCalResult().getStrSTD());
                values.add(sampleInfo.getCalResult().getSnpAvg());
                values.add(sampleInfo.getCalResult().getSnpSTD());
                break;
            }
            case setC: {
                values.add(sampleInfo.getChrACCount().get("Auto"));
                values.add(sampleInfo.getChrACCount().get("X"));
                values.add(sampleInfo.getChrACCount().get("Y"));
                values.add(sampleInfo.getChrACCount().get("MH"));
                values.add(sampleInfo.getCalResult().getATyped());
                values.add(sampleInfo.getCalResult().getXTyped());
                values.add(sampleInfo.getCalResult().getYTyped());
                values.add(sampleInfo.getCalResult().getMH_Loci_Typed());
                values.add(sampleInfo.getCalResult().getPP21Count());
                values.add(sampleInfo.getCalResult().getY41Count());
                values.add(sampleInfo.getCalResult().getStrAvg());
                values.add(sampleInfo.getCalResult().getStrSTD());
                break;
            }
            case yarn: {
                values.add(sampleInfo.getChrACCount().get("Y"));
                values.add(sampleInfo.getCalResult().getY_Loci_Typed());
                values.add(sampleInfo.getCalResult().getY41Count());
                values.add(sampleInfo.getCalResult().getStrAvg());
                values.add(sampleInfo.getCalResult().getSnpAvg());
                values.add(sampleInfo.getCalResult().getStrSTD());
                values.add(sampleInfo.getCalResult().getSnpSTD());
                break;
            }
            case setB: {
                values.add(sampleInfo.getChrACCount().get("Auto"));
                values.add(sampleInfo.getChrACCount().get("Y"));
                values.add(sampleInfo.getCalResult().getATyped());
                values.add(sampleInfo.getCalResult().getYTyped());
                values.add(sampleInfo.getCalResult().getPP21Count());
                values.add(sampleInfo.getCalResult().getY41Count());
                values.add(sampleInfo.getCalResult().getY41SupCount());
                values.add(sampleInfo.getCalResult().getStrAvg());
                values.add(sampleInfo.getCalResult().getStrSTD());
                break;
            }
            case test: {
                values.add(sampleInfo.getChrACCount().get("Auto"));
                values.add(sampleInfo.getChrACCount().get("X"));
                values.add(sampleInfo.getChrACCount().get("Y"));
                values.add(sampleInfo.getCalResult().getATyped());
                values.add(sampleInfo.getCalResult().getXTyped());
                values.add(sampleInfo.getCalResult().getYTyped());
                values.add(sampleInfo.getCalResult().getPP21Count());
                values.add(sampleInfo.getCalResult().getY41Count());
                values.add(sampleInfo.getCalResult().getY41SupCount());
                values.add(sampleInfo.getCalResult().getStrAvg());
                values.add(sampleInfo.getCalResult().getStrSTD());
                break;
            }
        }
    }


    private ArrayList<ArrayList<Object>> createLaneDataList() {
        ArrayList<ArrayList<Object>> data = new ArrayList<>();
        for (String locus : param.StrLocusOrder) {
            ArrayList<Object> values = new ArrayList<>();
            if ("Y-indel".equals(locus)) {
                values.add("SEX");
            } else if (param.YStrLocusOrder.contains(locus)) {
                values.add("Y");
            }else if (param.AutoStrLocusOrder.contains(locus)) {
                values.add("A");
            } else if (param.XStrLocusOrder.contains(locus)) {
                values.add("X");
            }
            values.add(locus);
            values.add( strMeanDepthOfSamples_Male.get(locus));
            values.add( strMeanDepthOfSamples_Female.get(locus));
            data.add(values);
        }
        for (String locus : param.SnpLocusOrder) {
            ArrayList<Object> values = new ArrayList<>();
            values.add("SNP");
            values.add(locus);
            values.add(snpMeanDepthOfSamples_Male.get(locus));
            data.add(values);
        }
        return data;
    }

    private ArrayList<ArrayList<Object>> createMergedGenoList() {
        ArrayList<ArrayList<Object>> data = new ArrayList<>();
        for (SampleInfo sample : sampleInfos) {
            ArrayList<Object> values = new ArrayList<>();
            values.add(sample.getBasicInfo().id);
            values.add(sample.getBasicInfo().gender.Description);
            values.add(sample.getBasicInfo().tablet);
            values.add(sample.getBasicInfo().well);
            values.add(sample.getBasicInfo().name);
            for (String locus : param.StrLocusOrder) {
                values.add(sample.getStrLocusInfo().getOrDefault(locus, new StrLocusInfo()).getAlleleNameAsString(!config.isMix(), !"Y-indel".equals(locus)));
            }
            for (String locus : param.SnpLocusOrder) {
                values.add(sample.getSnpLocusInfo().getOrDefault(locus, new SnpLocusInfo(locus)).getSnpAlleleAsString(!config.isMix()));
            }
            for (String locus : param.MHLocusOrder) {
                values.add(sample.getMHLocusInfo().getOrDefault(locus, new MHLocusInfo(locus)).getAlleleNameAsString(true,false));
            }
            data.add(values);
        }
        return data;
    }

    private ArrayList<ArrayList<Object>> createGenoList() {
        ArrayList<ArrayList<Object>> data = new ArrayList<>();
        ArrayList<Object> header = new ArrayList<>();
        header.add("id");
        header.add("gender");
        header.add("type");
        header.add("project");
        header.add("name");
        header.addAll(param.StrLocusOrder);
        data.add(header);
        for (SampleInfo sample : sampleInfos) {
            ArrayList<Object> values = new ArrayList<>();
            values.add(sample.getBasicInfo().id + "");
            values.add(sample.getBasicInfo().gender.Description + "");
            values.add(sample.getBasicInfo().type + "");
            values.add(sample.getBasicInfo().project + "");
            values.add(sample.getBasicInfo().name + "");
            for (String locus : param.StrLocusOrder) {
                values.add(sample.getStrLocusInfo().getOrDefault(locus, new StrLocusInfo()).getAlleleNameAsString(false, true));
            }
            for (String locus : param.SnpLocusOrder) {
                values.add(sample.getSnpLocusInfo().getOrDefault(locus, new SnpLocusInfo(locus)).getSnpAlleleAsString(false));
            }
            data.add(values);
        }
        return data;
    }
}
