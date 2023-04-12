package fayi;

import fayi.WriteExcel.CalExcel;
import fayi.WriteExcel.PreCalExcel;
import fayi.WriteExcel.SingleExcel;
import fayi.WriteExcel.FlankingData;
import fayi.config.*;
import fayi.config.Enum.*;
import fayi.flanking.FlankingInfo;
import fayi.flanking.FlankingSequence;
import fayi.seqParser.CorePicker;
import fayi.seqParser.RazorOutParse;
import fayi.seqParser.SE400FqTools;
import fayi.tableObject.*;
import fayi.utils.*;
import fayi.xml.Objects.Data;
import fayi.xml.Objects.Sample;
import fayi.xml.Xml;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fayi.config.Enum.Panel.*;
import static fayi.utils.NgstutterSetting.NGStutterSetter;

@Slf4j
public class Analyse {


    private final Config config ;
    private final Param param ;

    MicroHaplotype microHaplotype;

    public CoreSeqCompress compress;

    private final RazorOutParse razorOutParse;

    SE400FqTools se400FqTools;


    public Analyse(SE400FqTools se400FqTools, RazorOutParse razorOutParse,CoreSeqCompress compress) throws SetAException, IOException {
        this.config = Config.getInstance();
        this.param = Param.getInstance();
        if(config.getPanel().equals( setC ))
            microHaplotype = new MicroHaplotype();

        this.se400FqTools = se400FqTools;
        this.razorOutParse = razorOutParse;
        this.compress = compress;
    }

    final private Cal cal = new Cal();


    public void setSTRAllele(SampleInfo sampleInfo) {
        CorePicker corePicker = new CorePicker();
        for (String locus : param.StrLocusOrder) {
            if (Utils.femaleAndYlocus(sampleInfo.getBasicInfo().gender, locus)) {
                continue;
            }

            List<StrInfo> sameLocusList = sampleInfo.getStrDataAboveAt( locus );
            for (StrInfo strInfo : sameLocusList) {
                try {
                    corePicker.calCoreSTR( strInfo );
                } catch (SetAException e) {
                    if (e.getCode() == 5) {
                        log.error(e.getMessage() + String.format("样本 %s 位点 %s", sampleInfo.getId(), locus));
                    } else {
                        e.printStackTrace();
                    }
                }
            }
            if(!"Y-indel".equals(locus)&& !"Amelogenin".equals( locus ) && !"SRY".equals( locus )) {
                sameLocusList = sameLocusList.stream().filter(strInfo -> Float.parseFloat(strInfo.getAlleleName()) >= 5).collect(Collectors.toList());
            }

            boolean isBiallelicLocus = Utils.isBiallelicLocus(sampleInfo.getBasicInfo().gender, locus);
            if (sameLocusList.size() > 0) {
                sameLocusList.sort(Comparator.comparing(StrInfo::getReads, Comparator.reverseOrder()));
                StrLocusInfo strLocusInfo = sampleInfo.getStrLocusInfo().get(locus);

                StrInfo strInfo = sameLocusList.get(0);
                strInfo.setTyped(true);
                strLocusInfo.getAllele().add(strInfo);

                double strInfo_allele = Double.parseDouble( strInfo.getAlleleName() );
                //如果当前locus是双等位基因，则计算另一个allele
                StrInfo next;
                if (sameLocusList.size() > 1){
                    confirmNGS(strInfo, sameLocusList);
                    for (int i = 1; i < sameLocusList.size(); i++) {
                        //次高reads
                        next = sameLocusList.get(i);
                        if (next.getIsNGStutter()) {
                            continue;
                        }

                        if (config.isMix()) {
                            if (next.getReads() / strInfo.getReads() >= 0.1d) {
                                next.setTyped(true);
                                strLocusInfo.getAllele().add(next);
                                continue;
                            }
                            break;
                        }
                        //次高repeat-最高repeat为-1,reads比小于0.5
                        boolean sameAlleleName = false;
                        for (StrInfo allele : strLocusInfo.getAllele()) {
                            if (allele.getAlleleName().equals( next.getAlleleName() )) {
                                sameAlleleName = true;
                            }
//                            Assert.assertStrSequenceToBeCompressed(next);
                            if (next.getReads() / allele.getReads() < DefaultParam.STUTTER_MAX_PROPORTION) {
                                stutterSetter( allele, next );
                            }
                        }

                        if (next.getIsStutter()) {
                            continue;
                        }
                        confirmNGS( next, sameLocusList );
                        double next_allele = Double.parseDouble( next.getAlleleName() );
                        double IBObserving = strInfo_allele > next_allele ?
                                strInfo.getReadsWithNGS() / next.getReadsWithNGS() :
                                next.getReadsWithNGS() / strInfo.getReadsWithNGS();
                        strLocusInfo.getIBObserving().add( IBObserving );
                        if (isBiallelicLocus) {

                            if (locus.endsWith("a/b") && sameLocusList.size()>=i+2) {
                                StrInfo third = sameLocusList.get(i + 1);
                                if(third.getReads()/next.getReads() > 0.8){
                                    break;
                                }
                            }
                            if (param.getIBLowerLimitByLocus( locus, false )[1] <= IBObserving & IBObserving <= param.getIBUpperLimitByLocus( locus, false )[0]) {
                                // 第三峰是否过高
                                boolean mark = false;
                                for (int k=2;k<sameLocusList.size();k++){
                                    StrInfo third = sameLocusList.get(k);
                                    if (third.getReads() / next.getReads() >0.6) {
                                        mark = true;
                                        break;
                                    }
                                }
                                //符合双峰阈值
                                if(mark){
                                    removeAlleles(strLocusInfo, EmptyReason.THIRD_ALLELE);
                                    break;
                                }
                                next.setTyped( true );
                                strLocusInfo.getAllele().add( next );
                                strLocusInfo.getAllele().sort( Comparator.comparingDouble(o -> Double.parseDouble( o.getAlleleName() ) ) );
                            } else if (IBObserving < param.getIBLowerLimitByLocus( locus, false )[0] || IBObserving > param.getIBUpperLimitByLocus( locus, false )[1]) {
                                //单峰阈值
                                break;
                            } else if (sameAlleleName && ! next.getIsNGStutter()) {
                                strInfo.getNGSStutter().add( next );
                                next.setIsNGStutter( true );
                                //二代stutter
                            } else {
                                //在上下限区间内
                                if (strLocusInfo.getAllele().size() < 2) {
                                    removeAlleles(strLocusInfo, EmptyReason.IB );
                                }
                                break;
                            }

                        }else{
                            // 单等位 位点，次峰/主峰 占比0.9以上则不检出
                            if(next.getReads()/strInfo.getReads() >= 0.9 && !config.isOutHighestOnly()){
                                removeAlleles(strLocusInfo, EmptyReason.SINGLE_IB );
                            }

//                            if (DefaultParam.DEFAULT_SINGLE_LIMIT[0] <= IBObserving && IBObserving <= DefaultParam.DEFAULT_SINGLE_LIMIT[1]) {
//                                removeAlleles(strLocusInfo, EmptyReason.SINGLE_IB );
//                            }
//                            break;
                        }
                        break;
                    }
                }

                //判断深度是否符合单、双峰阈值
                if (strLocusInfo.getAllele().size() > 0) {
                    if (Gender.male.equals(sampleInfo.getBasicInfo().gender) && param.XStrLocusOrder.contains(locus)){
                        if(strLocusInfo.depthCheck(30d)){
                            removeAlleles(strLocusInfo, EmptyReason.SINGLE_DP);
                        }
                        continue;
                    }
                    if (strLocusInfo.depthCheck( null)){
                        removeAlleles(strLocusInfo, EmptyReason.SINGLE_DP);
                        continue;
                    }

                    //杂峰太多，总深度低于150的情况 用于ab位点
                    if ((! config.getNoFilter()) && strLocusInfo.getAllele().size() > 2) {
                        int sumDepth = 0;
                        for (SeqInfo seqInfo : strLocusInfo.getAllele()) {
                            sumDepth += seqInfo.getReads();
                        }
                        if (sumDepth < 150) {
                            removeAlleles(strLocusInfo, EmptyReason.NOISE_ALLELE );
                        }
                    }
                }
            }else{
                if (config.getNoFilter()) {
                    List<StrInfo> strInfos = sampleInfo.getStrData().get(locus);

                    if(strInfos!= null && strInfos.size()>0) {
                        strInfos.sort((o1, o2) -> Float.compare(o2.getReads(), o1.getReads()));
                        StrInfo strInfo = strInfos.get(0);
                        strInfo.setTyped(true);
                        sampleInfo.getStrLocusInfo().get(locus).getAllele().add(strInfo);
                    }
                }
            }
        }
    }

    //Next Generation Stutter..
    private void confirmNGS(StrInfo strInfo, List<StrInfo> sameLocusList) {
        if (sameLocusList.size() < 2) return;
        //分型值相同
        if ("Penta-E".equals(strInfo.getLocus())) {
            for (StrInfo sameAlleleStrInfo : sameLocusList) {
                if (strInfo.equals(sameAlleleStrInfo)) {
                    continue;
                }
                if (strInfo.getAlleleName().equals( sameAlleleStrInfo.getAlleleName() )) {
                    int[] ints = StringUtils.oneGenDiff( strInfo.getTrimmedSeq(), sameAlleleStrInfo.getTrimmedSeq() );
                    if (ints[2] == 1) {
                        strInfo.getNGSStutter().add(sameAlleleStrInfo);
                        sameAlleleStrInfo.setIsNGStutter(true);
                    }
                }
            }
            NGStutterSetter(strInfo, sameLocusList, 3);
        } else if ("Penta-D".equals(strInfo.getLocus())) {
            for (StrInfo sameAlleleStrInfo : sameLocusList) {
                if (strInfo.equals(sameAlleleStrInfo)) {
                    continue;
                }
                if (strInfo.getAlleleName().equals(sameAlleleStrInfo.getAlleleName())) {
                    int[] ints = StringUtils.oneGenDiff(strInfo.getOriginalSeq(), sameAlleleStrInfo.getOriginalSeq());
                    if (ints[2] - strInfo.getOriginalSeq().length() == -1) {
                        strInfo.getNGSStutter().add(sameAlleleStrInfo);
                        sameAlleleStrInfo.setIsNGStutter(true);
                    }
                }
            }
            NGStutterSetter(strInfo, sameLocusList, 3);
        } else if ("DYS576".equals(strInfo.getLocus()) || "DYS573".equals(strInfo.getLocus())) {
            NGStutterSetter(strInfo, sameLocusList, 2);
        } else {
            NGStutterSetter(strInfo, sameLocusList, 3);
        }
    }

    private void removeAlleles(StrLocusInfo strLocusInfo, EmptyReason emptyReason) {
        for (SeqInfo seqInfo : strLocusInfo.getAllele()) {
            seqInfo.setTyped( false );
        }
        if (null == strLocusInfo.getEmptyReason())
            strLocusInfo.setEmptyReason( emptyReason );
//        else log.warn(String.format("位点空缺原因已经被设置，位点 %s",locusInfo.getLocusName()));
        strLocusInfo.setAllele( new ArrayList<>() );
    }

    //计算不是stutter的allele数量，QC中的allelecount
    public void alleleCount(SampleInfo sampleInfo) {
        for (String locus : Param.getInstance().StrLocusOrder) {
            int count = 0;
            for (StrInfo strInfo : sampleInfo.getStrDataAboveAt(locus)) {
                if (!strInfo.getIsStutter() && !strInfo.getIsNGStutter()) {
                    if(strInfo.getReads()/sampleInfo.getStrLocusInfo().get( locus ).getTotalDepth() > 0.3) {
                        count++;
                    }
                }
            }
            sampleInfo.getStrLocusInfo().get(locus).setAlleleCount(count);
        }
    }

    //质控。
    public void qualityControl(SampleInfo sampleInfo) {
        HashMap<String, StrLocusInfo> locusInfos = sampleInfo.getStrLocusInfo();

        int Auto_lociTyped = 0;
        int X_lociTyped = 0;
        int Y_lociTyped = 0;
        int snp_lociTyped = 0;
        int alleleCount = 0;
        int y_alleleCount = 0;
        List<String> biallelicLocus = null;
        HashMap<String, Double> biallelicIB = null;
        HashMap<String, Double> stutterFilter = null;
        double stdStandard = 0d;
        switch (sampleInfo.getBasicInfo().gender) {
            case male:
                biallelicLocus = param.BiallelicMale;
                biallelicIB = param.MaleBiallelicIB;
                stutterFilter = param.MaleStutterFilter;
                stdStandard = param.MaleSTDStandard;
                break;
            case female:
                biallelicLocus = param.BiallelicFemale;
                stutterFilter = param.FemaleStutterFilter;
                biallelicIB = param.FemaleBiallelicIB;
                stdStandard = param.FemaleSTDStandard;
                break;
            case uncertain:
                biallelicIB = param.UnBiallelicIB;
                biallelicLocus = param.BiallelicUn;
                stutterFilter = param.UnStutterFilter;
                stdStandard = param.MaleSTDStandard;
        }
        for (String locus : param.StrLocusOrder) {
            if (Utils.femaleAndYlocus(sampleInfo.getBasicInfo().gender,locus)) {
                continue;
            }
            Double locusAT = sampleInfo.getLocusAT(locus);
            Double locusIT = sampleInfo.getLocusIT(locus);
            ArrayList<QC> qcResult = new ArrayList<>();
            StrLocusInfo strLocusInfo;
            if (locusInfos.containsKey(locus)) {
                strLocusInfo = locusInfos.get(locus);
            } else {
                strLocusInfo = new StrLocusInfo(locus);
                locusInfos.put(locus, strLocusInfo);
            }
            //计算
            if (strLocusInfo.getAllele().size() > 0) {
                if (param.AutoStrLocusOrder.contains(locus)) {
                    Auto_lociTyped += 1;
                } else if (param.XStrLocusOrder.contains(locus)) {
                    X_lociTyped += 1;
                } else if (param.YStrLocusOrder.contains(locus)) {
                    Y_lociTyped += 1;
                }
            }
            //无数据
            if (sampleInfo.getStrData().getOrDefault(locus, new ArrayList<>()).size() == 0) {
                qcResult.add(QC.Not_detected);
            } else {
                boolean allAlleleDepthbelowIT = true;
                boolean atLeastOneAlleleAboveAT = false;
                //stutter 如果 该stutter/来源allele 高于 \Stutter Filter阈值\，则QC标记。分(-1)和(-2/+1)两类，(-2/+1)的filter阈值为(-1)的平方。
                for (StrInfo si : sampleInfo.getStrDataAboveAt(locus)) {
                    if (si.getTyped()) {
                        Double aDouble = stutterFilter.getOrDefault(locus, 0.1);
                        for (StrInfo strInfo : si.getStutters()) {
                            if (strInfo.getReads() / si.getReads() > aDouble) {
                                qcResult.add(QC.Stutter);
                            }
                        }

                    }
                    //IT: 所有Allele不能高于IT，且至少有一个Allele是在AT、IT之间
                    if (si.getTyped()) {
                        if (si.getReads() > locusIT) {
                            allAlleleDepthbelowIT = false;
                        }
                        if (si.getReads() > locusAT) {
                            atLeastOneAlleleAboveAT = true;
                        }
                    }
                }
                if (allAlleleDepthbelowIT) {
                    if (atLeastOneAlleleAboveAT) {
                        qcResult.add(QC.Interpretation_threshold);
                    }
                    if (biallelicLocus.contains(locus) ? strLocusInfo.getAllele().size() < 2 : strLocusInfo.getAllele().size() < 1) {
                        qcResult.add(QC.Analytical_threshold);
                    }
                }

                if (biallelicLocus.contains(locus) & sampleInfo.getIntraLocusDepth().containsKey(locus)) {
                    double[] depths = sampleInfo.getIntraLocusDepth().get(locus);
                    double locusIB = 0;
                    if (depths.length < 2) {
                        if (depths[0] > locusIT) {
                            locusIB = 1;
                        }
                    } else {
                        locusIB = depths[1] / depths[0];
                    }
                    if (locusIB < biallelicIB.getOrDefault(locus, 0d)) {
                        qcResult.add(QC.Imbalance);
                    }
                }
                if (sampleInfo.getStrLocusInfo().get(locus).getAlleleCount() - sampleInfo.getStrLocusInfo().get(locus).getAllele().size() > 0) {
                    qcResult.add(QC.Allele_count);
                    if (param.AutoStrLocusOrder.contains(locus)) {
                        alleleCount += 1;
                    } else if (param.YStrLocusOrder.contains(locus)) {
                        y_alleleCount += 1;
                    }
                }
            }
            sampleInfo.getStrLocusInfo().get(locus).setQualityControl(qcResult);
        }
        // setB Y41试剂盒 出峰数计算
//        int y41Count = 0;
//        ArrayList<String> values = new ArrayList<>();
//        for(String locus:param.locusOrder.get("Y41")){
//            if (sampleInfo.getStrLocusInfo().getOrDefault(locus,new StrLocusInfo()).getAllele().size()>0){
//                y41Count += 1;
//                values.add(locus);
//                if(locus.endsWith("a/b")){
//                    y41Count += 1;
//                }
//            }
//        }
//        System.out.println(String.join(",",values) +" | "+y41Count);
//        sampleInfo.getCalResult().setY41Count(y41Count);

        for(String locus:param.SnpLocusOrder){
            SnpLocusInfo snpLocusInfo = sampleInfo.getSnpLocusInfo().get(locus);
            if(snpLocusInfo == null){
                continue;
            }
            ArrayList<QC> qualityControl = snpLocusInfo.getQualityControl();
            if(sampleInfo.getSnpData().getOrDefault(locus,new ArrayList<>()).size() == 0){
                snpLocusInfo.getQualityControl().add(QC.Not_detected);
            }else{
                boolean allAlleleDepthbelowIT = true;
                boolean atLeastOneAlleleAboveAT = false;
                int no_count = 0;
                //stutter 如果 该stutter/来源allele 高于 \Stutter Filter阈值\，则QC标记。分(-1)和(-2/+1)两类，(-2/+1)的filter阈值为(-1)的平方。
                for (SnpInfo si : sampleInfo.getSnpDataAboveAt(locus)) {
                    //IT: 所有Allele不能高于IT，且至少有一个Allele是在AT、IT之间
                    if (si.getTyped()) {
                        if (si.getReads() > sampleInfo.getLocusIT(locus)) {
                            allAlleleDepthbelowIT = false;
                        }
                        if (si.getReads() > sampleInfo.getLocusAT(locus)) {
                            atLeastOneAlleleAboveAT = true;
                        }
                    }else{
                        no_count++;
                    }
                }
                if (allAlleleDepthbelowIT) {
                    if (atLeastOneAlleleAboveAT) {
                        qualityControl.add(QC.Interpretation_threshold);
                    }
                    if (biallelicLocus.contains(locus) ? snpLocusInfo.getAllele().size() < 2 : snpLocusInfo.getAllele().size() < 1) {
                        qualityControl.add(QC.Analytical_threshold);
                    }
                }
                if(no_count>0){
                    qualityControl.add(QC.Allele_count);
                }
            }
        }
        for (ArrayList<SnpInfo> snpInfos : sampleInfo.getSnpData().values()) {
            for (SnpInfo snpInfo : snpInfos) {
                if (snpInfo.getTyped()) {
                    snp_lociTyped += 1;
                    break;
                }
            }
        }

        int mhLociTyped=0;
        for(MHLocusInfo mhLocusInfo: sampleInfo.getMHLocusInfo().values()){
            if(mhLocusInfo.getAllele().size()>0){
                mhLociTyped += 1;
            }
        }

        sampleInfo.getCalResult().setAuto_Loci_Typed(Auto_lociTyped+"/"+param.AutoStrLocusOrder.size());
        sampleInfo.getCalResult().setX_Loci_Typed(X_lociTyped+"/"+param.XStrLocusOrder.size());
        sampleInfo.getCalResult().setY_Loci_Typed(Y_lociTyped+"/"+param.YStrLocusOrder.size());
        sampleInfo.getCalResult().setMH_Loci_Typed(mhLociTyped+"/"+param.MHLocusOrder.size());
        sampleInfo.getCalResult().setISNP_Loci_Typed(snp_lociTyped+"/"+param.SnpLocusOrder.size());
        switch (config.getPanel()) {
            case test: {

            }
            case setA: {

            }
            case setB: {
                if (alleleCount >= 30 || y_alleleCount >= 50) {
                    sampleInfo.getCalResult().setSingleSource("Fail");
                } else {
                    sampleInfo.getCalResult().setSingleSource("Pass");
                }
                break;
            }
            case setC:
            case yarn: {
                if (y_alleleCount >= param.getSingleSourceSTD()) {
                    sampleInfo.getCalResult().setSingleSource("Fail");
                } else {
                    sampleInfo.getCalResult().setSingleSource("Pass");
                }
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + config.getPanel());
        }

        sampleInfo.getCalResult().setInterlocusBalance(sampleInfo.getSTD() < stdStandard ? "Pass" : "Fail");
    }

    //snp等位基因
    public void setSNPAllele(SampleInfo sampleInfo) {
        for (String locus : param.SnpLocusOrder) {
            List<SnpInfo> sameLocusList = sampleInfo.getSnpDataAboveAt( locus );
            sameLocusList.sort(Comparator.comparingDouble(SeqInfo::getReads).reversed());
            SnpLocusInfo snpLocusInfo = sampleInfo.getSnpLocusInfo().get(locus);
            if (sameLocusList.size() > 0) {
                sameLocusList.sort(Comparator.comparing(SnpInfo::getReads, Comparator.reverseOrder()));
                SnpInfo snpInfo = sameLocusList.get(0);
                if (snpInfo.isAboveIT()) {
                    snpInfo.setTyped(true);
                    snpLocusInfo.getAllele().add(snpInfo);
                    if (sameLocusList.size() > 1) {
                        SnpInfo snpInfo1 = sameLocusList.get(1);
                        if (snpInfo1.isAboveIT() & snpInfo1.getReads() / snpInfo.getReads() >= 0.5) {
                            snpInfo1.setTyped(true);
                            snpLocusInfo.getAllele().add(snpInfo1);
                        }
                    }
                }
            }
        }
    }


    private void simulateAmel(SampleInfo sampleInfo) {
        float ySum = 0f;
        for(String locus: param.YStrLocusOrder){
            ySum += sampleInfo.getStrLocusInfo().get(locus).getTotalDepth();
        }
        float sum = 0f;
        for (String locus : param.AutoStrLocusOrder) {
            sum += sampleInfo.getStrLocusInfo().get(locus).getTotalDepth();
        }
        float yPropertion = (ySum / param.YStrLocusOrder.size()) / (sum / param.AutoStrLocusOrder.size());
        if(0.3<= yPropertion) {
            int xDP = ((Float) sampleInfo.getCalResult().strAvg).intValue();
            int yDP = ((Float) (sampleInfo.getCalResult().strAvg * 0.8f)).intValue();
            StrInfo x = new StrInfo("Amelogenin", "X", true, 27, "TGTTGATTCTTTATCCCAGATGTTTCT", 0, xDP);
            StrInfo y = new StrInfo("Amelogenin", "Y", true, 33, "GGTGGATTCTTCATCCCAAATAAAGTGGTTTCT", 0, yDP);
            sampleInfo.getStrLocusInfo().put("Amelogenin",
                    new StrLocusInfo("Amelogenin", new ArrayList<>(), 0, xDP + yDP,
                            new ArrayList<>(Arrays.asList(x, y))
                            , new ArrayList<>(Collections.singletonList(0.8d)))
            );
            sampleInfo.getStrData().put("Amelogenin", new ArrayList<>(Arrays.asList(x, y)));
        }else if(yPropertion <= 0.1) {
            int xDP = ((Float) sampleInfo.getCalResult().strAvg).intValue();
            StrInfo x = new StrInfo("Amelogenin", "X", true, 27, "TGTTGATTCTTTATCCCAGATGTTTCT", 0, xDP);
            sampleInfo.getStrLocusInfo().put("Amelogenin",
                    new StrLocusInfo("Amelogenin", new ArrayList<>(), 0, xDP,
                            new ArrayList<>(Collections.singletonList(x))
                            , new ArrayList<>(Collections.singletonList(0d)))
            );
            sampleInfo.getStrData().put("Amelogenin", new ArrayList<>(Collections.singletonList(x)));
        }else{
            sampleInfo.getStrLocusInfo().put("Amelogenin",new StrLocusInfo());
            sampleInfo.getStrData().put("Amelogenin",new ArrayList<>());
        }
    }

    private void yIndelPlusOne(SampleInfo sampleInfo){
        for (StrInfo strInfo : sampleInfo.getStrDataAboveAt("Y-indel")) {
            try {
                strInfo.setAlleleName((Integer.parseInt(strInfo.getAlleleName()) + 1) + "");
            } catch (NumberFormatException e) {
                strInfo.setAlleleName((Float.parseFloat(strInfo.getAlleleName()) + 1) + "");
            }
        }
    }

    public void setStutter(SampleInfo sampleInfo) {
        for (String locus : Param.getInstance().StrLocusOrder) {
            List<StrInfo> strDataAboveAt = sampleInfo.getStrDataAboveAt( locus );
            strDataAboveAt.sort( (o1, o2) -> Double.compare( o2.getReads(), o1.getReads() ) );
            for (StrInfo siTrue : strDataAboveAt) {
                if (siTrue.getTyped()) {
                    stutterSetter( siTrue, strDataAboveAt );
                }
            }
        }
    }

    private boolean coreSequenceOneDiff(StrInfo allele, StrInfo next) {
        boolean one_diff_marker = false;
        for (int j = 0; j < next.getNoneCoreseq().size(); j++) {
            int difference = allele.getCoreSeqCount().getOrDefault( j, 0 ) - next.getCoreSeqCount().getOrDefault( j, 0 );
            if (difference != 0) {
                if (difference == 1) {
                    if (one_diff_marker) {
                        one_diff_marker = false;
                        break;
                    }
                    one_diff_marker = true;
                } else {
                    one_diff_marker = false;
                    break;
                }
            }
        }
        return one_diff_marker;
    }


    private void stutterSetter(StrInfo siTrue, StrInfo siFalse) {
        if (siTrue.getStutters().contains( siFalse )) {
            return;
        }
        //非重复序列相同的两个allele
        if (siTrue.getNoneCoreseq().equals( siFalse.getNoneCoreseq() )) {
            boolean one_diff_marker = coreSequenceOneDiff( siTrue, siFalse );

            if (one_diff_marker) {
                siFalse.setIsStutter( true );
                siTrue.getStutters().add( siFalse );
            }
        }
    }

    private void stutterSetter(StrInfo siTrue, List<StrInfo> strDataAboveAt) {
        for (StrInfo siFalse : strDataAboveAt) {
            if (! siFalse.getTyped() ) {
                if(strDataAboveAt.stream().anyMatch(strInfo -> strInfo.getTyped() && siFalse.getReads()/strInfo.getReads()>0.8)){
                    continue;
                }
                stutterSetter( siTrue, siFalse );
            }
        }
    }

    //单个样本的流程
    /*
    1。读数据
    2。分性别
    3。设置性别位点的genotype名称（0，1 -> X，Y）
    4。质控
    5。输出str表
    6。输出snp表
    7。输出基本信息
     */
    public void analyse(SampleInfo sampleInfo) throws SetAException {

        sampleInfo.initLocusInfo();
        sampleInfo.sortSeqData();
        sampleInfo.setATDepthValue();
        //2. 设置reads的AT、IT信息
        sampleInfo.strDataFilter();

        yIndelPlusOne( sampleInfo );
        //3. 设置性别
        sampleInfo.setYProportion();
//        if (! config.useGivenGender) {
//            sampleInfo.setGender( null );
//        } else {
//            if (Gender.uncertain.equals( sampleInfo.getBasicInfo().gender ) && ! config.isMix()) {
//                sampleInfo.setGender( null );
//            }
//        }
        if (Gender.uncertain.equals( sampleInfo.getBasicInfo().gender )) {
            sampleInfo.setGender( Gender.male );
        }
        //5. 去除核心区的侧翼
        razorOutParse.trimSequenceBatch(sampleInfo);
        //DYS626 测不通
        razorOutParse.specialLocusHandle(sampleInfo);

        //4. 确定位点的allele
        setSTRAllele(sampleInfo);

        //6. 为DYS389II 分配DYS389I
        setDYS389IIsubIseq(sampleInfo);

        //压缩核心区 识别N区
        compress.processSample(sampleInfo);

        //5. 确定stutter
        setStutter(sampleInfo);

        //7. 输出表中的allele格式化（双等位、性别位点）
        sampleInfo.setGenderAllele();

        setSNPAllele(sampleInfo);

        cal.calAviliableDepthCoverageOfSample(sampleInfo);
        cal.calLocusDepthAvg(sampleInfo);

        // 评定污染等级
        cal.mixLevel(sampleInfo);

        // stutter 高占比位点
        cal.stutterHighProp(sampleInfo);

        //10. 清除过噪位点
        if (!config.isNoNoiseFilter()) {
            filterHighMisLocus(sampleInfo);
        }else{
            if ( config.getPanel().equals(setB) && sampleInfo.getStrLocusInfo().get("Penta-E").getAcLevel() == ACLevel.High) {
                removeAlleles(sampleInfo.getStrLocusInfo().get("Penta-E"), EmptyReason.NOISE_PROP);
            }
        }

        //11. 噪音统计
        cal.noiseCount(sampleInfo);


        //7. 读取侧翼信息
        if ( !config.isNoFlanking() && config.getPanel().needsFlanking ) {
//            log.info("start flanking analyse...");
            String razorFile = config.getOutputPath() +
                    "/str_snp_out/" + sampleInfo.getBasicInfo().lane + "_" + sampleInfo.getBasicInfo().id + "_flanking.out";
            if ( !config.isNoRazor() ) {
                se400FqTools.flankingRazor(razorFile, sampleInfo);
            } else {
                try {
                    Utils.checkReadFile(razorFile);
                } catch (SetAException e) {
                    log.info(String.format("不进行razor抓取流程，但是out文件未找到，将自动运行样本%s的侧翼razor", sampleInfo.getId()));
                    se400FqTools.flankingRazor(razorFile, sampleInfo);
                }
            }

            FlankingInfo flankingInfo = razorOutParse.readFlankingInformation(razorFile, sampleInfo);
            flankingInfo.atFilter();
            flankingInfo.ngsFilter();
            FlankingSequence flankingSequence = new FlankingSequence();

            flankingSequence.selectCommonFlanking(flankingInfo.getFlankingStrInfo(), sampleInfo);
            flankingSequence.selectIndelFlanking(flankingInfo.getFlankingStrInfo(), sampleInfo);

            splitMultipleFlanking(sampleInfo);
        }

        if (config.isFastq()) {
            cal.checkRawDataReads(sampleInfo);
        }


        //6. 非stutter的allele数(QC.allelecount)
        alleleCount(sampleInfo);
        //9. 质控
        qualityControl(sampleInfo);



        cal.CEAutoTyped(sampleInfo, param.locusOrder.get("MR36A"));
        sampleInfo.getCalResult().setY41Count(cal.CEYTyped(sampleInfo, param.locusOrder.get("Y41")));
        sampleInfo.getCalResult().setY41SupCount(cal.CEYTyped(sampleInfo, param.locusOrder.get(config.getYSTD())));
    }

    private void filterHighMisLocus(SampleInfo sampleInfo) {
        for (String locus : param.StrLocusOrder) {
            if (!param.AutoStrLocusOrder.contains(locus) && autolocus_noise_only) {
                continue;
            }
            if (sampleInfo.getStrLocusInfo().get(locus).getAcLevel() == ACLevel.High) {
                removeAlleles(sampleInfo.getStrLocusInfo().get(locus), EmptyReason.NOISE_PROP);
            }
        }
    }

    private void splitMultipleFlanking(SampleInfo sampleInfo) throws SetAException {
        for (String locus : sampleInfo.getStrLocusInfo().keySet()) {
            StrLocusInfo strLocusInfo = sampleInfo.getStrLocusInfo().get(locus);
            ArrayList<StrInfo> newAllele = new ArrayList<>();
            if (strLocusInfo.getAllele().size() >= 2) continue;
            for (StrInfo strInfo : strLocusInfo.getAllele()) {
                if (strInfo.getFlanking().size() > 1) {
                    double sum = strInfo.getFlanking().stream().mapToDouble(Flanking::getDepth).sum();
                    for (Flanking flanking : strInfo.getFlanking()) {
                        StrInfo clone = SerializationUtils.clone(strInfo);
                        clone.setFlanking(new ArrayList<>(Collections.singletonList(flanking)));
                        clone.setForward((int)((flanking.getDepth() / sum) * strInfo.getReads()));
                        clone.setReverse(0);
                        clone.formatRepeatSequence(false);
                        newAllele.add(clone);
                        if (newAllele.size() >= 2) break;
                    }
                } else {
                    newAllele.add(strInfo);
                }
            }
            if (newAllele.size() > strLocusInfo.getAllele().size()) {
                log.info( "样本" + sampleInfo.getId() + " 位点 " + locus + "有多个flanking，拆分为了" + newAllele.size() + "个allele" );
                ArrayList<StrInfo> filtered = new ArrayList<>();
                for (StrInfo strInfo : sampleInfo.getStrData().get(locus)) {
                    if (!strInfo.getTyped()) {
                        filtered.add(strInfo);
                    }
                }
                sampleInfo.resetStrDataAboveAtStore( locus );
                sampleInfo.getStrData().put(locus, filtered);
                sampleInfo.getStrData().get(locus).addAll(newAllele);
                strLocusInfo.setAllele(newAllele);
            }
        }
    }

    public void setDYS389IIsubIseq(SampleInfo sampleInfo) {
        for (StrInfo iistrInfo : sampleInfo.getStrDataAboveAt("DYS389II")) {
            for (StrInfo istrInfo : sampleInfo.getStrDataAboveAt("DYS389I")) {
                if (iistrInfo.getTrimmedSeq().startsWith(istrInfo.getTrimmedSeq()) && iistrInfo.getPair389I() == null) {
                    iistrInfo.setPair389I(istrInfo);
                }
            }
        }
    }

    //!!
    static final boolean autolocus_noise_only = true;

    public ArrayList<SampleInfo> start(String xmlFile) throws Exception {
        if (!config.isNoNoiseFilter() && autolocus_noise_only) {
            log.warn("只对常染色体做信噪比筛选");
        }
        Utils.checkDir(config.getOutputPath() + "/str_snp_out/");

        //总表的行信息，每行一个样本
        Xml xml = new Xml();
        Data data = xml.xmlToData(xmlFile);
        Map<String, Long> counts = data.samples.stream().map(sample -> sample.basicInfo.lane).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        for(String key: counts.keySet()) {
            log.info(String.format("lane: %s, total: %s.", key, counts.get(key)));
        }
        if (data.samples.size() < 1) {
            System.exit(1);
        }
        config.setRawDataPath(data.samples);

        final ArrayList<SampleInfo> sampleInfos = new ArrayList<>();
        SingleExcel singleExcel = new SingleExcel();
        ExecutorService executorService = Executors.newFixedThreadPool(config.getWorker());
        AtomicInteger count = new AtomicInteger();

        for (Sample sample : data.samples) {
            executorService.execute(() -> {
                SampleInfo sampleInfo = singleExcel.sampleToSampleInfo(sample);
                try {
                    String razorFile = config.getOutputPath() + "/str_snp_out/" + sampleInfo.getBasicInfo().lane + "_" + sampleInfo.getBasicInfo().id + "_STR_SNP.out";
                    if (!config.isNoRazor()) {
                        se400FqTools.razer(sampleInfo);
                    } else {

                        try {
                            Utils.checkReadFile(razorFile);
                        } catch (SetAException e) {
                            log.info("不进行razor抓取流程，但是out文件未找到，将自动运行该样本的核心razor");
                            se400FqTools.razer(sampleInfo);
                        }
                    }
                    //1. 读取str文件
                    File str_snp_file = new File(razorFile);
                    razorOutParse.readSeqInformation(str_snp_file.getAbsolutePath(), sampleInfo);
                    analyse(sampleInfo);
                } catch (Exception e) {
                    log.error("error on analysing sample " + sampleInfo.getBasicInfo().getId());
                    log.error(e.getMessage());
                    log.error(e.getLocalizedMessage());
                    e.printStackTrace();
                }
                synchronized (this) {
                    sampleInfos.add(sampleInfo);
                }
                count.getAndIncrement();
            });
        }
        Utils.poolExecuterWaiter(executorService, "Analyse Samples", count);
        new PreCalExcel().preCal(sampleInfos);

        if (!config.isNoFlanking() && config.getPanel().needsFlanking) {
            log.info("deal flanking");
            FlankingSequence flankingSequence = new FlankingSequence();
            flankingSequence.batchAlign(sampleInfos);

            // 侧翼区indel 算入分型值
            log.info("fix allele indel...");
            fixAlleleIndel(sampleInfos);
//        razorOutParse.flankingMapping(sampleInfos);

            log.info("output flanking...");
            FlankingData flankingData = new FlankingData();
            flankingData.writeFlankingExcel(sampleInfos);
        }

        if(setC.equals( config.getPanel() )){

            executorService = Executors.newFixedThreadPool(config.getWorker());
            for (SampleInfo sampleInfo : sampleInfos) {
                executorService.execute(() -> {
                    if(!config.isNoRazor())
                        se400FqTools.mhRazor( sampleInfo );
                    try {
                        microHaplotype.start( sampleInfo );
                    }catch (SetAException | IOException e){
                        log.error( String.format( "样本 %s 微单倍型分析错误：%s", sampleInfo.getId(), e.getMessage()) );
                    }
                });
            }
            count.set( 0 );
            Utils.poolExecuterWaiter(executorService, "mh on the way", count);
        }
        CalExcel calExcel = new CalExcel(sampleInfos, "cal");
        calExcel.start();
        calExcel.ibOutput(sampleInfos);
//        if (config.isMarkSnp()) {
//            HashMap<String, StrInfo> refStrs = new ReadReference().readRef(config.getProjectPath() + "/resource/setB.fasta");
//            cal.alleleFlankingSNP(sampleInfos, refStrs);
//        }

        if (yarn.equals(config.getPanel()) || setC.equals(config.getPanel())) {
            log.info("predict haplogroup");
            HgPredict hgPredict = new HgPredict();
            hgPredict.predictHg(sampleInfos);
        }

        markFilter(sampleInfos);

        ArrayList<SampleInfo> result;

        if (!config.isQualityFilter() &&  setB.equals(Config.getInstance().getPanel())) {
            log.info("filtering samples by data quality and allele positive rate");
            result = filter(sampleInfos);
        }else{
            result = sampleInfos;
        }

        if (setB.equals(config.getPanel())) {
            log.info("simulating amelogenin...");
            for (SampleInfo sampleInfo : result) {
                simulateAmel(sampleInfo);
            }
            param.StrLocusOrder.add("Amelogenin");
            param.AutoStrLocusOrder.add("Amelogenin");
        }

        new Xml().dataToXml(xml.sampleInfoToXmlData(result), config.getOutput());

        if (setB.equals(config.getPanel())) {
            param.StrLocusOrder.remove("Amelogenin");
            param.AutoStrLocusOrder.remove("Amelogenin");
        }
        return result;
    }

    private void fixAlleleIndel(ArrayList<SampleInfo> sampleInfos) {
        for (SampleInfo sampleInfo : sampleInfos) {
            for (String locus : param.StrLocusOrder) {
//                ArrayList<StrInfo> allele = new ArrayList<>();
                for (StrInfo strInfo : sampleInfo.getStrLocusInfo().get(locus).getAllele()) {
//                    if (strInfo.getFlanking().size() == 1) {
                        changeAlleleByIndel( sampleInfo.getId(), strInfo );
//                    } else if (strInfo.getFlanking().size() > 1) {
//
//                        if (strInfo.getFlankingSnp().stream().anyMatch(snpMarker -> SnpType.insert.equals(snpMarker.getType()) || SnpType.delete.equals(snpMarker.getType()))) {
//                            StrInfo clone = SerializationUtils.clone(strInfo);
//                            changeAlleleByIndel( sampleInfo.getId(), clone );
//                            allele.add( clone );
//                            sampleInfo.getStrData().get(locus).add(clone);
//                        }
//                    }
                }
//                sampleInfo.getStrLocusInfo().get(locus).getAllele().addAll(allele);
            }
        }
    }

    private void changeAlleleByIndel(int index, StrInfo strInfo) {

        for (SnpMarker snpMarker : strInfo.getFlankingSnp()) {
            if(snpMarker.getPosition().equals( SnpPosition.N )){
                continue;
            }
            if(snpMarker.getStart() == 1){
                continue;
            }
            if (SnpType.delete.equals( snpMarker.getType() )) {
                String[] split = strInfo.getAlleleName().split( "[.]" );
                int i = Integer.parseInt( split[0] ) * param.locusSTR.get( strInfo.getLocus() ).get( 0 ).length();
                if (split.length > 1) {
                    i += Integer.parseInt( split[1] );
                }
                i -= Math.abs( snpMarker.getStart() - snpMarker.getEnd() ) + 1;
                int count = i / param.locusSTR.get( strInfo.getLocus() ).get( 0 ).length();
                int left = i % param.locusSTR.get(strInfo.getLocus()).get(0).length();
                strInfo.setAlleleName( left > 0 ? count + "." + left : count + "" );
                log.info( String.format( "样本 %s 位点 %s 改变了分型值 %s", index, strInfo.getLocus(), snpMarker.toMarker() ) );
            } else if (SnpType.insert.equals(snpMarker.getType())) {
                String[] split = strInfo.getAlleleName().split("[.]");
                int i = Integer.parseInt(split[0]) * param.locusSTR.get(strInfo.getLocus()).get(0).length();
                if (split.length > 1) {
                    i += Integer.parseInt(split[1]);
                }
                i += snpMarker.getAlt().length();
                int count = i / param.locusSTR.get(strInfo.getLocus()).get(0).length();
                int left = i % param.locusSTR.get(strInfo.getLocus()).get(0).length();
                strInfo.setAlleleName( left > 0 ? count + "." + left : count + "" );
                log.info( String.format( "样本 %s 位点 %s 改变了分型值 %s", index, strInfo.getLocus(), snpMarker.toMarker() ) );
            }
        }
    }

    public void markFilter(ArrayList<SampleInfo> sampleInfos) {
        for (SampleInfo sampleInfo : sampleInfos) {
            if (sampleInfo.getCalResult().strAvg > 200 && sampleInfo.getChrACCount().get("Auto") <= 30 && sampleInfo.getChrACCount().get("Y") <= 50) {
                if(config.getPanel().equals( yarn )){
                    int count = 0;
                    for (String locus : param.YStrLocusOrder) {
                        if (sampleInfo.getStrLocusInfo().getOrDefault(locus,StrLocusInfo.getEmpty()).getAllele().size() > 0) {
                            count++;
                        }
                    }
                    if(count < 20) {
                        continue;
                    }
                }
                sampleInfo.getCalResult().setQualityPass(true);
            }
        }
    }

    public ArrayList<SampleInfo> filter(ArrayList<SampleInfo> sampleInfos) throws IOException {
        ArrayList<SampleInfo> result = new ArrayList<>();
        ArrayList<SampleInfo> filtered = new ArrayList<>();

        for(SampleInfo sampleInfo:sampleInfos){
            if(sampleInfo.getCalResult().isQualityPass() && sampleInfo.getCalResult().getY41SupCount() >= 35 && sampleInfo.getCalResult().getPP21Count() >= 29) {
                result.add(sampleInfo);
            }else{
                filtered.add(sampleInfo);
            }
        }
//        y41ScoreUp(result,filtered);
        if(filtered.size()>0){
            log.info(filtered.size() + " samples filtered by quality. ");
            new CalExcel(filtered, "filtered").start();
//            new SingleExcel().start(filtered);
        }
        return result;
    }

}
