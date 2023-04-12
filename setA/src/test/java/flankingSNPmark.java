import fayi.Analyse;
import fayi.WriteExcel.SingleExcel;
import fayi.config.Config;
import fayi.config.Enum.Gender;
import fayi.config.Param;
import fayi.tableObject.*;
import fayi.utils.FileUtils;
import fayi.utils.SetAException;
import fayi.utils.Utils;
import fayi.xml.Objects.BasicInfo;
import fayi.xml.Objects.Data;
import fayi.xml.Objects.Sample;
import fayi.xml.Objects.SoftWare;
import fayi.xml.Xml;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.System.exit;

@Slf4j
public class flankingSNPmark {

    static Param param;

//    public static void main(String[] args) throws Exception {
//        Config config = Config.getInstance();
//        param = config.getParam();
//        config.setSampleInfoExcel("/Users/kaidan/Downloads/out/sample_Info.xlsx");
//        config.setSample("/Users/kaidan/Downloads/out/files.list");
//        config.setOutput("/Users/kaidan/Downloads/out/samples.xml");
//        createXml();
//
//        config.setRazorConfig(config.getProjectPath() + "/resource/razer/y47_config");
//        config.setQualityFilter(true);
//        config.setRazorWorker("5");
//        config.setNoRazor(false);
//        config.setOutput("/Users/kaidan/Downloads/out/result.xml");
//        Analyse analyse = new Analyse();
//        ArrayList<SampleInfo> sampleInfos = analyse.start("/Users/kaidan/Downloads/out/samples.xml");
//        System.out.println(sampleInfos.size());
//
//        Utils.checkDir("/Users/kaidan/Downloads/out/reports/");
//        config.setOutput("/Users/kaidan/Downloads/out/reports/");
//        SingleExcel singleExcel = new SingleExcel();
//        singleExcel.start(sampleInfos);
//
//        for (SampleInfo sampleInfo : sampleInfos) {
//            File str_snp_file = new File(config.getOutputPath() + "/str_snp_out/" + sampleInfo.getBasicInfo().lane + "_" + sampleInfo.getBasicInfo().id + "_STR_SNP.out");
//            readSeqInformation(str_snp_file.getAbsolutePath(), sampleInfo);
//        }
//
//    }

    private static void readSeqInformation(String sampleFile, SampleInfo sampleInfo) {
        FileUtils sampleReader = new FileUtils(sampleFile);

        HashMap<String, StrLocusInfo> locusInfos = sampleInfo.getStrLocusInfo();
        String line;
        while ((line = sampleReader.readLine()) != null) {
            try {
                String[] cols = line.split("\t");
                //str8razer 使用-o输出的out文件最后分析结果为5列，与文件上面sequence结果冲突，
                // 使用系统命令 1> 输出到的out文件最后为6列，可以正常跳过
                if (cols.length == 5) {
                    if (cols[2].contains("SumBelowThreshold")) {
                        continue;
                    }
                    String[] columns = cols[0].split(":");
                    try {
                        Float.parseFloat(columns[1]);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    Integer forward = Integer.parseInt(cols[3]);
                    Integer reverse = Integer.parseInt(cols[4]);

                    if (param.StrLocusOrder.contains(columns[0])) {
                        sampleInfo.getCalResult().strSumDepth += forward + reverse;
                        if (!locusInfos.containsKey(columns[0])) {
                            locusInfos.put(columns[0], new StrLocusInfo(columns[0], forward, reverse));
                        } else {
                            locusInfos.get(columns[0]).setForwardTotal(forward);
                            locusInfos.get(columns[0]).setReverseTotal(reverse);
                        }
                        if (!sampleInfo.getStrData().containsKey(columns[0])) {
                            sampleInfo.getStrData().put(columns[0], new ArrayList<>());
                        }
                        sampleInfo.getStrData().get(columns[0]).add(new StrInfo(columns[0], columns[1], false, Integer.valueOf(cols[1].split(" ")[0]), cols[2], forward, reverse));
                    } else if (param.SnpLocusOrder.contains(cols[0].split("_")[0])) {
                        String markerName = cols[0].split("_")[0];
                        String alter = cols[0].split("_")[1].split(":")[0];
                        sampleInfo.getCalResult().snpSumDepth += forward + reverse;
                        if (sampleInfo.getSnpData().containsKey(markerName)) {
                            sampleInfo.getSnpLocusInfo().get(markerName).setForwardTotal(forward);
                            sampleInfo.getSnpLocusInfo().get(markerName).setReverseTotal(reverse);
                            boolean flag = true;
                            for (SnpInfo snpInfo : sampleInfo.getSnpData().get(markerName)) {
                                if (snpInfo.getAlleleName().equals(alter)) {
                                    snpInfo.addForward(forward.doubleValue());
                                    snpInfo.addReverse(reverse.doubleValue());
                                    flag = false;
                                }
                            }
                            if (flag) {
                                sampleInfo.getSnpData().get(markerName).add(new SnpInfo(markerName, alter, false, Integer.valueOf(cols[1].split(" ")[0]), cols[2], forward, reverse));
                            }
                        } else {
                            ArrayList<SnpInfo> snpInfos = new ArrayList<>();
                            snpInfos.add(new SnpInfo(markerName, alter, false, Integer.valueOf(cols[1].split(" ")[0]), cols[2], forward, reverse));
                            sampleInfo.getSnpData().put(markerName, snpInfos);
                            SnpLocusInfo snpLocusInfo = new SnpLocusInfo(markerName);
                            snpLocusInfo.setForwardTotal(forward);
                            snpLocusInfo.setReverseTotal(reverse);
                            sampleInfo.getSnpLocusInfo().put(markerName, snpLocusInfo);
                        }
                    } else {
                    }
                }
            } catch (ArrayIndexOutOfBoundsException indexE) {
                log.error(indexE.getMessage());
                sampleReader.finishRead();
                exit(1);
            }
        }
        sampleReader.finishRead();
        for(String locus:param.StrLocusOrder){
            if (null == sampleInfo.getStrLocusInfo().get(locus)){
                sampleInfo.getStrLocusInfo().put(locus,new StrLocusInfo());
            }
        }
    }

    private static void createXml() throws SetAException {
        Config config = Config.getInstance();
        FileUtils fileUtils = new FileUtils( config.getSampleFile() );
        HashMap<String, String> nameFile = new HashMap<>();
        String s;
        while ((s = fileUtils.readLine()) != null) {
            s = s.trim();
            Utils.checkReadFile(s);
            String name = new File(s).getName();
//            System.out.println(name);
            nameFile.put(name, s);
        }
        Data data = new Data();
        Param param = Param.getInstance();
        for (String name : nameFile.keySet()) {

            //获取lane号
            String pattern = "(V[\\d]+_L0[\\d]{1})";
            Pattern compile = Pattern.compile(pattern);
            Matcher matcher = compile.matcher(name);
            String lane = "";
            if (matcher.find()) {
                lane = matcher.group(1);
            }
            //文件名使用lane号分割，得到短的文件名
            BasicInfo basicInfo = new BasicInfo();
            basicInfo.lane = lane;
            basicInfo.id = Integer.parseInt(name.replace(lane + "_", "").split("[.]")[0]);
            //过滤样本信息单中的样本
            if (!Param.getInstance().genderMap.containsKey(basicInfo.id)) {
                continue;
            }
            if (null != config.getProjectOnly()) {
                if (!config.getProjectOnly().equals(param.sampleProject.get(basicInfo.id))) {
                    continue;
                }
            }

//            if(!param.samplePanel.getOrDefault(basicInfo.id,"").toUpperCase().replaceAll("-","").contains(config.getArtifact().toUpperCase())){
//                continue;
//            }
            basicInfo.lane = lane;
            basicInfo.tablet = param.sampleTablet.get(basicInfo.id);
            basicInfo.panel = param.samplePanel.get(basicInfo.id);
            basicInfo.gender = param.genderMap.getOrDefault(basicInfo.id, Gender.uncertain);
            basicInfo.type = param.sampleType.getOrDefault(basicInfo.id,"");
            basicInfo.project = param.sampleProject.getOrDefault(basicInfo.id,"");
            basicInfo.name = param.sampleName.getOrDefault(basicInfo.id,"");
            basicInfo.fastq = nameFile.get(name);
            basicInfo.softWare = new SoftWare("AnalyseAndCalculate", config.getArtifact(), config.getVersion());
            data.samples.add(new Sample(basicInfo));
        }
        data.sampleNum = data.samples.size();
        new Xml().dataToXml(data,config.getOutput());
    }

}
