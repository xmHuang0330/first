package fayi.seqParser;

import fayi.CoreSeqCompress;
import fayi.config.Config;
import fayi.config.Param;
import fayi.flanking.FlankingInfo;
import fayi.tableObject.*;
import fayi.utils.*;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.lang.System.exit;

@Slf4j
@Component
@NoArgsConstructor
public class RazorOutParse {

    HashMap<String, Integer[]> trimConfig = Config.getInstance().getTrimConfig();
    HashMap<String, ArrayList<String>> repeatCore = Param.getInstance().locusSTR;

    HashMap<String, List<String>> tribleCore = new HashMap<>();


    @Autowired
    public void setTribleCOre(){

        repeatCore.forEach((s, strings) -> tribleCore.put(s, strings.stream().map(v -> v + v + v).collect(Collectors.toList())));
    }


    public void readSeqInformation(String sampleFile, SampleInfo sampleInfo) {
        Param param = Config.getPanelParam(sampleInfo.getBasicInfo().panel!=null?sampleInfo.getBasicInfo().panel:Config.getInstance().getPanel().name());
//        FileUtils stutterWriter = new FileUtils(stutterFIle);
        FileUtils sampleReader = new FileUtils(sampleFile);

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
//                        System.out.println(line);
                    }
                }
            } catch (ArrayIndexOutOfBoundsException indexE) {
                System.err.println("Error:" + indexE.getMessage());
                sampleReader.finishRead();
                exit(1);
            }
        }
        sampleReader.finishRead();
//        int count = 0;
//        for(ArrayList<SnpInfo> snpInfos:sampleInfo.getSnpData().values()){
//            for(SnpInfo snpInfo:snpInfos){
//                count += 1;
//            }
//        }
//        sampleInfo.getCalResult().snpAvg = sampleInfo.getCalResult().getSnpSumDepth() / count;
    }


    public void trimSequence(StrInfo strInfo) throws SetAException {
        if(strInfo.getOriginalSeq()  == null){
            throw new SetAException(301, String.format("分型没有原始序列, locus: %s, 分型: %s", strInfo.getLocus(),strInfo.getAlleleName()));
        }

        Integer[] integers = trimConfig.getOrDefault(strInfo.getLocus(), new Integer[]{0, 0});
        try {
            String substring = strInfo.getOriginalSeq().substring( integers[0], strInfo.getOriginalSeq().length() - integers[1] );
            strInfo.setTrimmedSeq( substring );
        } catch (StringIndexOutOfBoundsException e) {
            strInfo.setAboveAT( false );
            strInfo.setAboveIT( false );
            throw new SetAException( 5, String.format( "核心区长度过短，去除侧翼时发生错误。位点:%s 分型:%s", strInfo.getLocus(), strInfo.getAlleleName() ) );
        }
    }

    public void trimSequenceBatch(SampleInfo sampleInfo) throws SetAException {
        for (String locus : Config.getInstance().getParam().StrLocusOrder) {
            boolean reset = false;
            for (StrInfo strInfo : sampleInfo.getStrDataAboveAt( locus )) {
                try {

                    if (Config.getInstance().getPanel().canTrimFlanking)
                        trimSequence(strInfo);
                    else strInfo.setTrimmedSeq(strInfo.getOriginalSeq());
                } catch (SetAException e) {
                    if (e.getCode() == 5) {
                        reset = true;
                        log.warn( e.getMessage() + String.format( " 样本:%s", sampleInfo.getId() ) );
                    } else {
                        throw e;
                    }
                }
            }
            if (reset) {
                sampleInfo.getStrDataAboveAT().remove( locus );
            }
        }
    }

//    public void flankingMapping(ArrayList<SampleInfo> sampleInfos) throws SetAException {
//        String tempPath = Config.getInstance().getOutputPath()+"/muscle_temp/";
//        Utils.checkDir(tempPath);
//        HashMap<String, String[]> flankingSetting = Config.getInstance().getFlankingSetting();
//        for(String locus:Config.getInstance().getParam().StrLocusOrder){
//            HashMap<String, String> leftFlankings = new HashMap<>();
//            leftFlankings.put("ref_l",flankingSetting.get(locus)[0]);
//            HashMap<String, String> rightFlankings = new HashMap<>();
//            rightFlankings.put("ref_r",flankingSetting.get(locus)[1]);
//            for(SampleInfo sampleInfo:sampleInfos) {
//                for (SeqInfo seqInfo : sampleInfo.getStrLocusInfo().getOrDefault(locus, new LocusInfo()).getAllele()) {
//                    StrInfo strInfo = (StrInfo) seqInfo;
//                    if(strInfo.getLeftFlanking() != null && strInfo.getRightFlanking() != null) {
//                        leftFlankings.put(sampleInfo.getBasicInfo().id + "-" + seqInfo.hashCode(), strInfo.getLeftFlanking());
//                        rightFlankings.put(sampleInfo.getBasicInfo().id + "-" + seqInfo.hashCode(), strInfo.getRightFlanking());
//                    }
//                }
//            }
//            MuscleMapping.createInFile(leftFlankings,tempPath+locus+"_l.fa");
//            HashMap<String, String> l_results = MuscleMapping.runAndGetResult(tempPath + locus + "_l.fa", tempPath + locus + "_l-result.fa");
//            MuscleMapping.createInFile(rightFlankings,tempPath+locus+"_r.fa");
//            HashMap<String, String> r_results = MuscleMapping.runAndGetResult(tempPath + locus + "_r.fa", tempPath + locus + "_r-result.fa");
//
//        }
//
//    }

    public void specialLocusHandle(SampleInfo sampleInfo) {
        // DYS626   +[AGAA]2 AGAG [GAAG]3 [AAAG]3，分型长度+9
        for (StrInfo strInfo : sampleInfo.getStrDataAboveAt( "DYS626" )) {
            strInfo.setTrimmedSeq( strInfo.getTrimmedSeq() + "AGAAAGAAAGAGGAAGGAAGGAAGAAAGAAAGAAAG" );
            if (strInfo.getAlleleName().contains( "." )) {
                strInfo.setAlleleName( String.format( "%.2f", Float.parseFloat( strInfo.getAlleleName() ) + 9 ) );
            } else {
                strInfo.setAlleleName( (Integer.parseInt( strInfo.getAlleleName() ) + 9) + "" );
            }
        }
        HashMap<String, Integer> changes = new HashMap<>();
        changes.put("DYS626", 2);
        changes.put("DYS630", 5);
        changes.put("DYS510", 6);
        changes.put("D3S3045", -1);
        changes.put("D6S477", -4);
//        changes.put("D15S659", -1);
        changes.put("D18S535", -1);
        changes.put("D7S3048", 1);
        changes.put("D4S2366", -2);
        for (String locus : changes.keySet()) {
            for (StrInfo strInfo : sampleInfo.getStrDataAboveAt(locus)) {
                if (strInfo.getAlleleName().contains(".")) {
                    strInfo.setAlleleName(String.format("%.2f", Float.parseFloat(strInfo.getAlleleName()) + changes.get(locus)));
                } else {
                    strInfo.setAlleleName((Integer.parseInt(strInfo.getAlleleName()) + changes.get(locus)) + "");
                }
            }
        }
        String pattern = "^([ATCG]+AAAGAAGG(AAGG)+)((AAAGAAGG)+)$";
        Pattern compile = Pattern.compile(pattern);
        List<StrInfo> dxs10148 = sampleInfo.getStrData().get("DXS10148");
        if(dxs10148!=null && dxs10148.size()>0){
            for (StrInfo strInfo:dxs10148){
                String substring = strInfo.getOriginalSeq().substring(0, strInfo.getOriginalSeq().length() - 32);
                Matcher matcher = compile.matcher(substring);
                if(matcher.find()){
                    String tail = matcher.group(3);
                    String front = matcher.group(1);
                    strInfo.setTrimmedSeq("GGAA" + front);
                    int change = tail.length()/4;
                    if (strInfo.getAlleleName().contains(".")) {
                        strInfo.setAlleleName(String.format("%.2f", Float.parseFloat(strInfo.getAlleleName()) - change));
                    } else {
                        strInfo.setAlleleName((Integer.parseInt(strInfo.getAlleleName()) - change) + "");
                    }
                }
            }
        }
    }

    public FlankingInfo readFlankingInformation(String razorFile, SampleInfo sampleInfo) {
//        sortBySeqLength(sampleInfo);
        Param param = Config.getPanelParam(sampleInfo.getBasicInfo().panel);

        FileUtils sampleReader = new FileUtils(razorFile);
        String line;

        FlankingInfo flankingInfo = new FlankingInfo();
        HashMap<String, ArrayList<StrInfo>> flankingStrInfo = flankingInfo.getFlankingStrInfo();
        HashMap<String, Integer> locusDepth = flankingInfo.getLocusDepth();

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

                        String locus = columns[0];

                        locusDepth.put(locus, locusDepth.getOrDefault(locus, 0) + forward + reverse);
//                        if((forward + reverse) < 30) continue;

                        if (!flankingStrInfo.containsKey(locus)) {
                            flankingStrInfo.put(locus, new ArrayList<>());
                        }
                        flankingStrInfo.get(locus).add(new StrInfo(locus, columns[1], false, Integer.valueOf(cols[1].split(" ")[0]), cols[2], forward, reverse));
                    }
                }
            } catch (ArrayIndexOutOfBoundsException indexE) {
                log.error(indexE.getMessage());
                sampleReader.finishRead();
                exit(1);
            }
        }
        sampleReader.finishRead();
        return flankingInfo;
    }

    private void sortBySeqLength(SampleInfo sampleInfo) {
        for (String locus : sampleInfo.getStrLocusInfo().keySet()) {
            sampleInfo.getStrLocusInfo().get(locus).getAllele().sort((o1, o2) -> o2.getRepeatSequence().length() - o1.getRepeatSequence().length());
        }
    }

    public boolean testAndFixOriginalReverseComp(SampleInfo sampleInfo){
        int count = 0;
        for (String locus : sampleInfo.getStrLocusInfo().keySet()) {
            for(StrInfo strInfo: sampleInfo.getStrLocusInfo().get(locus).getAllele()) {
                if(strInfo.getOriginalSeq() != null) {
                    if (tribleCore.get(strInfo.getLocus()).stream().noneMatch(s -> strInfo.getOriginalSeq().contains(s))) {
                        String reverseComp = strInfo.reverseComplementOrigSeq();
                        if (tribleCore.get(strInfo.getLocus()).stream().noneMatch(reverseComp::contains)) {
                            log.warn(String.format("反向互补序列依然不包含 重复序列*3， 样本：%s位点：%s分型：%s%n",
                                    sampleInfo.getId(), locus, strInfo.getAlleleName()));
                            continue;
                        }
                        try {
                            trimSequence(strInfo);
                        } catch (SetAException e) {
                            log.error("反向互补序列依然包含重复序列，但是去除侧翼时发生错误");
                        }
                        count ++;
                    }
                }
            }
        }

        return count > 0;

    }
}