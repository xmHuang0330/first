package fayi.flanking;

import fayi.config.Config;
import fayi.config.Enum.SnpPosition;
import fayi.config.SnpMarker;
import fayi.tableObject.*;
import fayi.utils.MuscleMapping;
import fayi.utils.SetAException;
import fayi.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Slf4j
public class FlankingSequence {

    Config config;
    private MuscleMapping muscleMapping = MuscleMapping.getInstance();

    public FlankingSequence() {
        config = Config.getInstance();
    }

    public void selectCommonFlanking(HashMap<String, ArrayList<StrInfo>> flankingStrInfo, SampleInfo sampleInfo) {

        HashMap<String, String[]> flankingConfig = config.getFlankingSetting();

        for (String locus : Config.getInstance().getParam().StrLocusOrder) {
            String[] strings = flankingConfig.get(locus);
            if (strings == null) {
                continue;
            }
            ArrayList<StrInfo> flankingInfos =
                    (ArrayList<StrInfo>) flankingStrInfo.getOrDefault(locus, new ArrayList<>()).stream().sorted((o1, o2) -> Float.compare(o2.getReadsWithNGS(), o1.getReadsWithNGS())).collect(Collectors.toList());
            flankingStrInfo.put(locus, flankingInfos);
            int i = 0;
            StrInfo first = null;
            while (i < flankingInfos.size()) {
                first = trySeqFlanking(flankingInfos.get(i), sampleInfo, strings);
                if (first != null) break;
                i++;
            }
            if (i >= flankingInfos.size()) {
                continue;
            }
            i += 1;
            if (Utils.isBiallelicLocus(sampleInfo.getBasicInfo().gender, locus)) {
                for (; i < flankingInfos.size(); i++) {
                    StrInfo nextFlanking = flankingInfos.get(i);

                    if (nextFlanking.getTyped() || nextFlanking.getIsNGStutter()) continue;
                    float IBObserving = Double.parseDouble(first.getAlleleName()) > Double.parseDouble(nextFlanking.getAlleleName()) ? first.getReadsWithNGS() / nextFlanking.getReadsWithNGS() : nextFlanking.getReadsWithNGS() / first.getReadsWithNGS();
                    if (config.getParam().getIBLowerLimit().get( locus )[1] <= IBObserving & IBObserving <= config.getParam().getIBUpperLimit().get( locus )[0]) {
                        trySeqFlanking( nextFlanking, sampleInfo, strings );
                    }
                }
            } else {
                for (; i < flankingInfos.size(); i++) {
                    StrInfo nextFlanking = flankingInfos.get(i);
                    trySeqFlanking(nextFlanking, sampleInfo, strings);
                }
            }
        }
    }

    private StrInfo trySeqFlanking(StrInfo flanking, SampleInfo sampleInfo, String[] flankingConfig) {
//        if(flanking.getLocus().equals("TH01")){
//            System.out.println();
//        }
        if (flanking.getIsNGStutter()) return null;
        String repeatSeq = flanking.getRepeatSequence();
        String Lflanking;
        String Rflanking;
        try {
            Lflanking = repeatSeq.substring(0, flankingConfig[0].length());
            Rflanking = repeatSeq.substring(repeatSeq.length() - flankingConfig[1].length());
        } catch (StringIndexOutOfBoundsException e) {
            log.warn(String.format("侧翼序列过短。位点:%s 分型:%s 样本:%s", flanking.getLocus(), flanking.getAlleleName(), sampleInfo.getId()));
            return null;
        }
        if (repeatSeq.length() < flankingConfig[0].length() + flankingConfig[1].length()) {
            log.warn(String.format("侧翼序列%s异常，样本%s，位点%s", repeatSeq, sampleInfo.getId(), flanking.getLocus()));
            return null;
        }
        String substring = repeatSeq.substring(flankingConfig[0].length(), repeatSeq.length() - flankingConfig[1].length());

        for (StrInfo strInfo : sampleInfo.getStrLocusInfo().get(flanking.getLocus()).getAllele()) {

            if (matchLocusAlleleNum(strInfo, sampleInfo)) continue;
            if (substring.equals(strInfo.getTrimmedSeq())) {
                flanking.setTyped(true);
                strInfo.getFlanking().add(new Flanking(
                        Lflanking, Rflanking, flanking.getReads(), flanking.getAlleleName(), flanking.getRepeatSequence()
                ));
                return flanking;
            }
        }
        return null;
    }

    public void selectIndelFlanking(HashMap<String, ArrayList<StrInfo>> flankingInformation, SampleInfo sampleInfo) {
        HashMap<String, String[]> flankingSetting = config.getFlankingSetting();
        for (String locus : flankingInformation.keySet()) {
            if(sampleInfo.getStrLocusInfo().getOrDefault(locus,StrLocusInfo.getEmpty()).getAllele().stream().anyMatch(strInfo -> strInfo.getReads()<30)){
                continue;
            }
            if (!"DYS389I".equals(locus) && !"DYS626".equals(locus)) {
                ArrayList<StrInfo> flkInfos = (ArrayList<StrInfo>) flankingInformation.get(locus).stream().sorted((o1, o2) -> Float.compare(o2.getReadsWithNGS(), o1.getReadsWithNGS())).collect(Collectors.toList());
                flankingInformation.put(locus, flkInfos);
                for (StrInfo flking : flkInfos) {
                    if (flking.getTyped() || flking.getIsNGStutter()) continue;
                    for (StrInfo strInfo : sampleInfo.getStrLocusInfo().getOrDefault(locus, StrLocusInfo.getEmpty()).getAllele()) {

                        if (matchLocusAlleleNum(strInfo, sampleInfo)) continue;

                        if (flking.getRepeatSequence().contains(strInfo.getTrimmedSeq())) {

                            HashMap<String, String> result;
                            try {
                                result = muscleMapping.runAndGetResult(flankingSetting.get(locus)[0] + strInfo.getTrimmedSeq() + flankingSetting.get(locus)[1],
                                        flking.getRepeatSequence());
                            } catch (SetAException e) {
                                log.warn( String.format( "比对序列错误, 样本：%s 位点：%s", sampleInfo.getId(), locus) );
                                continue;
                            }
                            String s = result.get("REF");
                            int[] ls = getIndelFlankingLength(flankingSetting.get(locus), s);
                            String flankingSeq = result.get("ALT");
                            String substring = flankingSeq.substring(ls[0], flankingSeq.length() - ls[1]);

                            if (Utils.isBiallelicLocus(sampleInfo.getBasicInfo().getGender(), locus)) {
                                float IBObserving = Double.parseDouble(flkInfos.get(0).getAlleleName()) > Double.parseDouble(flking.getAlleleName()) ? flkInfos.get(0).getReadsWithNGS() / flking.getReadsWithNGS() : flking.getReadsWithNGS() / flkInfos.get(0).getReadsWithNGS();
                                if (config.getParam().getIBLowerLimit().get( locus )[1] <= IBObserving & IBObserving <= config.getParam().getIBUpperLimit().get( locus )[0]) {
                                    if (substring.equals( strInfo.getTrimmedSeq() )) {
                                        // 右侧翼开头 或者 左侧翼最后是核心重复长度的 - 则不是真是的侧翼序列
                                        String l_regex = "^.*[-]{" + config.getParam().locusSTR.get( locus ).get( 0 ).length() + ",}$";
                                        HashMap<String, String> l_result = null;
                                        HashMap<String, String> r_result = null;
                                        try {
                                            r_result = muscleMapping.runAndGetResult( flankingSetting.get( locus )[1], flankingSeq.substring( flankingSeq.length() - ls[1] ).replaceAll( "[-]", "" ) );
                                            l_result = muscleMapping.runAndGetResult( flankingSetting.get( locus )[0], flankingSeq.substring( 0, ls[0] ).replaceAll( "[-]", "" ) );
                                        } catch (SetAException e) {
                                            log.warn( "比对序列错误：r:"+r_result + " | l:" + l_result + String.format( " | 样本：%s 位点：%s", sampleInfo.getId(), locus) );
                                            continue;
                                        }
                                        if (l_result.values().stream().anyMatch( s1 -> s1.matches( l_regex ) )) {
                                            continue;
                                        }
                                        String r_regex = "^[-]{" + config.getParam().locusSTR.get( locus ).get( 0 ).length() + ",}.*$";

                                        if (r_result.values().stream().anyMatch( s1 -> s1.matches( r_regex ) )) {
                                            continue;
                                        }
                                        flking.setTyped(true);
                                        strInfo.getFlanking().add(new Flanking(
                                                flankingSeq.substring(0, ls[0]).replaceAll("-", ""),
                                                flankingSeq.substring(flankingSeq.length() - ls[1]).replaceAll("-", ""),
                                                flking.getReads(),
                                                flking.getAlleleName(),
                                                flankingSeq.replaceAll("-", "")
                                        ));
                                        break;
                                    }
                                }
                            } else {
                                if (substring.equals(strInfo.getTrimmedSeq())) {
                                    flking.setTyped(true);
                                    strInfo.getFlanking().add(new Flanking(
                                            flankingSeq.substring(0, ls[0]).replaceAll("-", ""),
                                            flankingSeq.substring(flankingSeq.length() - ls[1]).replaceAll("-", ""),
                                            flking.getReads(),
                                            flking.getAlleleName(),
                                            flankingSeq.replaceAll("-", "")
                                    ));
                                    break;
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    private boolean matchLocusAlleleNum(StrInfo strInfo, SampleInfo sampleInfo) {
        if (strInfo.getFlanking().size() > 0) {
            int count = 0;
            for (StrInfo temp : sampleInfo.getStrLocusInfo().get(strInfo.getLocus()).getAllele()) {
                if (temp.getOriginalSeq().equals(strInfo.getOriginalSeq())) {
                    count += strInfo.getFlanking().size();
                } else {
                    count += strInfo.getFlanking().size() == 0 ? 1 : strInfo.getFlanking().size();
                }
            }
            return count >= (Utils.isBiallelicLocus(sampleInfo.getBasicInfo().getGender(), strInfo.getLocus()) ? (strInfo.getLocus().endsWith("a/b") || strInfo.getLocus().equals("DYS572") ? 4 : 2) : 1);
        }
        return false;
    }

    private int[] getIndelFlankingLength(String[] o, String s) {
        int[] ints = new int[2];
        if (s.startsWith(o[0])) {
            ints[0] = o[0].length();
        } else {
            int slashLength = 0;
            int oLength = 0;
            char slash = '-';
            while (oLength <= o[0].length()) {
                if (slash == s.charAt(oLength + slashLength)) {
                    slashLength++;
                } else {
                    oLength++;
                }
            }
            ints[0] = oLength - 1 + slashLength;
        }
        if (s.endsWith(o[1])) {
            ints[1] = o[1].length();
        } else {
            int slashLength = 0;
            int oLength = 0;
            char slash = '-';
            while (oLength <= o[1].length()) {
                if (slash == s.charAt(s.length() - oLength - slashLength - 1)) {
                    slashLength++;
                } else {
                    oLength++;
                }
            }
            ints[1] = oLength - 1 + slashLength;
        }
        return ints;
    }


    public void batchAlign(ArrayList<SampleInfo> sampleInfos) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(Config.getInstance().getWorker());

        HashMap<String, String[]> flankingSetting = Config.getInstance().getFlankingSetting();
        HashMap<String, String> nRef = config.getNRef();
//        Map<Integer, SampleInfo> collect = sampleInfos.stream().collect(Collectors.toMap(SampleInfo::getId, sampleInfo -> sampleInfo));
        AtomicInteger count = new AtomicInteger();
        for (SampleInfo sampleInfo : sampleInfos) {
            executorService.execute(() -> {
                try {
                    sampleFlankingAlign(flankingSetting, nRef, sampleInfo);
                } catch (SetAException e) {
                    log.error( String.format( "样本处理snp时发生错误：%s， 样本：%s", e.getMessage(), sampleInfo.getId()) );
                }
                count.getAndIncrement();
            });
        }
        Utils.poolExecuterWaiter(executorService, " flanking alignment ", count);
    }

    private void sampleFlankingAlign(HashMap<String, String[]> flankingSetting, HashMap<String, String> nRef, SampleInfo sampleInfo) throws SetAException {

        for (String locus : flankingSetting.keySet()) {
            if (!"".equals(flankingSetting.get(locus)[0])) {
                for (StrInfo strInfo : sampleInfo.getStrLocusInfo().getOrDefault(locus, StrLocusInfo.getEmpty()).getAllele()) {
                    if (strInfo.getFlanking().size() > 0) {
//                        log.warn("侧翼序列不应该大于1个：" + locus + " 样本：" + sampleInfo.getId());
                        Flanking flanking = strInfo.getFlanking().get(0);
                        if (!flanking.getLeftSequence().equals(flankingSetting.get(locus)[0])) {
                            HashMap<String, String> result = muscleMapping.runAndGetResult(flankingSetting.get(locus)[0], flanking.getLeftSequence());
                            snpRecog("l", strInfo, result);
                        }
                    }
                }
            }
            if (!"".equals(flankingSetting.get(locus)[1])) {
                for (StrInfo strInfo : sampleInfo.getStrLocusInfo().getOrDefault(locus, StrLocusInfo.getEmpty()).getAllele()) {
                    if (strInfo.getFlanking().size() > 0) {
//                        log.warn("侧翼序列不应该大于1个：" + locus + " 样本：" + sampleInfo.getId());
                        Flanking flanking = strInfo.getFlanking().get(0);
                        if (!flanking.getRightSequence().equals(flankingSetting.get(locus)[1])) {
                            HashMap<String, String> result = muscleMapping.runAndGetResult(flankingSetting.get(locus)[1], flanking.getRightSequence());
                            snpRecog("r", strInfo, result);
                        }
                    }
                }
            }
        }
        for (String locus : nRef.keySet()) {
            ArrayList<StrInfo> allele = sampleInfo.getStrLocusInfo().getOrDefault(locus, StrLocusInfo.getEmpty()).getAllele();
            for (StrInfo strInfo : allele) {
                String nString = strInfo.getNs();
                if (null != nString && !"".equals(nString) && !nString.equals(nRef.get(locus))) {
                    HashMap<String, String> result = muscleMapping.runAndGetResult(nRef.get(locus), nString);
                    snpRecog("n", strInfo, result);
                }
            }
        }
    }

    private void snpRecog(String symbol, StrInfo strInfo, HashMap<String, String> mappingResult) throws SetAException {

        String ref = mappingResult.get("REF");
        String alt = mappingResult.get("ALT");
        HashMap<Integer, String> diffs = new HashMap<>();
        findDiff(ref, alt, diffs);
        for (Integer position : diffs.keySet()) {
            try {
                strInfo.getFlankingSnp().add( new SnpMarker( SnpPosition.getByPosition( symbol ), position, null, diffs.get( position ), ref ) );
            }catch (SetAException e){
                throw new SetAException( 1, e.getMessage()+"位点："+strInfo.getLocus() );
            }
        }
    }

    private void findDiff(String ref, String seq, HashMap<Integer, String> diffs) {
        StringBuilder diff = new StringBuilder();
        for (int i = 0; i < seq.length(); i++) {
            if (ref.charAt(i) != seq.charAt(i)) {
                diff.append(seq.charAt(i));
            } else {
                if (!"".equals(diff.toString())) {
                    diffs.put(i, diff.toString());
                }
                diff = new StringBuilder();
            }
        }
        if (!"".equals(diff.toString())) {
            diffs.put(seq.length(), diff.toString());
        }
    }

}
