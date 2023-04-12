package fayi;

import fayi.config.Config;
import fayi.config.Enum.ACLevel;
import fayi.config.Enum.Gender;
import fayi.config.Enum.QC;
import fayi.config.Param;
import fayi.tableObject.*;
import fayi.utils.FileUtils;
import fayi.utils.SetAException;
import fayi.utils.Utils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/*
 * 统计
 * 计算平均深度、标准差、reads数等数据
 */
@Slf4j
public class Cal {

    final private Param param = Param.getInstance();

    public void stutterHighProp(SampleInfo sampleInfo){
        int highStutterCount = 0;
        for(String locus: param.StrLocusOrder){
            StrLocusInfo strLocusInfo = sampleInfo.getStrLocusInfo().getOrDefault(locus, new StrLocusInfo());
            for(StrInfo strInfo: strLocusInfo.getAllele()){
                if(strInfo.getStutters()!=null){
                    for(StrInfo stutter:strInfo.getStutters()){
                        if(stutter.getReads()/strInfo.getReads() > 0.3){
                            highStutterCount += 1;
                        }
                    }
                }
            }
        }
        sampleInfo.getCalResult().setHighStutter(highStutterCount);
    }

    public void CEAutoTyped(SampleInfo sampleInfo, ArrayList<String> locusOrder) throws SetAException {
        int count = 0;
        if(null == locusOrder || locusOrder.size() < 1) throw new SetAException(1,"位点列表不能为空！");
        for(String locus:locusOrder.stream().filter(s -> param.AutoStrLocusOrder.contains(s)).collect(Collectors.toList())){
            if(sampleInfo.getStrLocusInfo().getOrDefault(locus,new StrLocusInfo()).getAllele().size()>0){
                count += 1;
            }
        }
        sampleInfo.getCalResult().setPP21Count(count);
    }

    public void noiseCount(SampleInfo sampleInfo) {
        int totalNo = 0;
        int countNo = 0;
        for(List<StrInfo> locusData:sampleInfo.getStrDataAboveAT().values()){
            totalNo += locusData.stream().mapToInt(strInfo -> strInfo.getTyped()?0:Double.valueOf(strInfo.getReads()).intValue()).sum();
            countNo += locusData.stream().filter(strInfo -> !strInfo.getTyped()).count();
        }
        sampleInfo.getCalResult().setNoCount(countNo);
        sampleInfo.getCalResult().setNoDP(totalNo);
    }

    public int CEYTyped(SampleInfo sampleInfo, ArrayList<String> locusOrder) throws SetAException {
        int count = 0;
        if(null == locusOrder || locusOrder.size() < 1) throw new SetAException(1,"Y位点列表不能为空！");
        List<String> available = locusOrder.stream().filter(s -> param.YStrLocusOrder.contains(s)).collect(Collectors.toList());
        for(String locus: available){
            if(sampleInfo.getStrLocusInfo().getOrDefault(locus,new StrLocusInfo()).getAllele().size()>0){
                count += 1;
                if(locus.endsWith("a/b")){
                    count += 1;
                }
            }
        }
        return count;
    }
    public void mixLevel(SampleInfo sampleInfo) {
        float sum = 0;
        for (String locus : Config.getInstance().getParam().AutoStrLocusOrder) {
            float genodp = 0;
            float nodp = 0;
            int count = 0;
            for (StrInfo strInfo : sampleInfo.getStrDataAboveAt( locus )) {
                if (strInfo.getIsNGStutter() || strInfo.getIsStutter()) {
                    continue;
                }
                if (strInfo.getTyped() && count < 2) {
                    count++;
                    genodp += strInfo.getReadsWithNGS();
                    continue;
                }
                nodp += strInfo.getReads();

            }
            float v = genodp / (genodp + nodp);

            if (param.noiseLimit.containsKey(locus)) {
                sampleInfo.getStrLocusInfo().get( locus ).setAcLevel( v > param.noiseLimit.get( locus )[0] ? ACLevel.Low : (v <= param.noiseLimit.get( locus )[1] ? ACLevel.High : ACLevel.Medium) );
            }
            sum += v;
        }
        sampleInfo.getCalResult().setMixLevel(sum / (float) (Config.getInstance().getParam().AutoStrLocusOrder.size()));
    }

    //单个样本的位点间平均深度
    public void calAviliableDepthCoverageOfSample(SampleInfo sampleInfo) {
        ArrayList<Float> strDepthValues = new ArrayList<>();
        int strTotalDepth = 0;
        int femaleLocusCount = 0;
        //遍历str位点
        for (String locus : Param.getInstance().StrLocusOrder) {
            //女性跳过Y位点
            if (Utils.femaleAndYlocus(sampleInfo.getBasicInfo().gender, locus)) {
                continue;
            }
            femaleLocusCount++;
            //1. 获取str位点深度
            strDepthValues.add((float) sampleInfo.getStrLocusInfo().get(locus).getTotalDepth());
            //2. str总深度
            strTotalDepth += sampleInfo.getStrLocusInfo().getOrDefault(locus, new StrLocusInfo()).getTotalDepth();
            //3. str统计深度小于30、100的位点个数
            if(sampleInfo.getStrLocusInfo().get( locus ).getTotalDepth() < 30){
                sampleInfo.getCalResult().strDepthBelow30 ++;
            }
            if(sampleInfo.getStrLocusInfo().get( locus ).getTotalDepth() < 100){
                sampleInfo.getCalResult().strDepthBelow100 ++;
            }
            //查错
            if(sampleInfo.getStrLocusInfo().get(locus).getAllele().size()==0){
                for (StrInfo strInfo : sampleInfo.getStrDataAboveAt(locus)) {
                    if (strInfo.getTyped()) {
                        log.warn("样本：" + sampleInfo.getBasicInfo().id + "位点：" + locus + "没有allele，但是有strinfo被标记为typed");
                    }
                }
            }
        }
        //str位点间的标准差
        sampleInfo.getCalResult().strSTD = calSTDEVA(strDepthValues);
        //str位点间的平均值
        sampleInfo.getCalResult().strAvg = strTotalDepth / (float) (Gender.female.equals(sampleInfo.getBasicInfo().gender) ? femaleLocusCount : param.StrLocusOrder.size());

        //遍历snp位点
        int snpAvilDepth = 0;
        ArrayList<Float> snpDepthValues = new ArrayList<>();
        for (String locus : param.SnpLocusOrder) {
            snpAvilDepth += sampleInfo.getSnpLocusInfo().getOrDefault(locus, new SnpLocusInfo(locus)).getTotalDepth();
            snpDepthValues.add((float) sampleInfo.getSnpLocusInfo().getOrDefault(locus, new SnpLocusInfo(locus)).getTotalDepth());
        }

        //snp位点深度均值
        sampleInfo.getCalResult().snpAvg = snpAvilDepth / (float) param.SnpLocusOrder.size();
        //snp位点深度均一性
        sampleInfo.getCalResult().snpSTD = calSTDEVA(snpDepthValues);

        int mhAvilDepth = 0;
        ArrayList<Float> mhDepthValues = new ArrayList<>();
        for (String locus : param.MHLocusOrder) {
            mhAvilDepth += sampleInfo.getMHLocusInfo().getOrDefault(locus, new MHLocusInfo(locus)).getReads();
            mhDepthValues.add((float) sampleInfo.getMHLocusInfo().getOrDefault(locus, new MHLocusInfo(locus)).getReads());
        }
        sampleInfo.getCalResult().mhAvg = mhAvilDepth / (float) param.MHLocusOrder.size();
        sampleInfo.getCalResult().mhSTD = calSTDEVA(mhDepthValues);

        //样本的有效深度
        sampleInfo.getCalResult().setAvailableDepth(strTotalDepth + snpAvilDepth + mhAvilDepth);
    }

    public void calLocusDepthAvg(SampleInfo sampleInfo) {
        //分别计算Auto X Y alleleCount数
        int AutoCount = 0;
        int YCount = 0;
        int XCount = 0;
        for (String locus : param.StrLocusOrder) {
            if (sampleInfo.getStrLocusInfo().get(locus).getQualityControl().contains(QC.Allele_count)) {
                if(param.AutoStrLocusOrder.contains(locus)){
                    AutoCount += 1;
                }else
                if (param.YStrLocusOrder.contains(locus)) {
                    YCount += 1;
                }else
                if (param.XStrLocusOrder.contains(locus)){
                    XCount += 1;
                }
            }
        }
        sampleInfo.getChrACCount().put("Auto", AutoCount);
        sampleInfo.getChrACCount().put("X", XCount);
        sampleInfo.getChrACCount().put("Y", YCount);

        //计算小于30/100层的位点个数
        int lowerThan30 = 0;
        int lowerThan100 = 0;
        for (String locus : param.StrLocusOrder) {
            if(Utils.femaleAndYlocus(sampleInfo.getBasicInfo().gender,locus)){
                continue;
            }
            if (sampleInfo.getStrLocusInfo().get(locus).getTotalDepth() < 30) {
                lowerThan30 += 1;
            }
            if (sampleInfo.getStrLocusInfo().get(locus).getTotalDepth() < 100) {
                lowerThan100 += 1;
            }
        }
        sampleInfo.setLowerThan30(lowerThan30);
        sampleInfo.setLowerThan100(lowerThan100);

    }


    //读取文件reads数
    public void checkRawDataReads(SampleInfo sampleInfo) {
        boolean isLinux = System.getProperty("os.name").toLowerCase().contains("linux");
        int lineNumber = 0;
        try {
            Utils.checkReadFile(sampleInfo.getBasicInfo().fastq);

            if (isLinux) {
                //linux系统使用系统的gzip、wc获取文件行数
                String[] cat = sampleInfo.getBasicInfo().fastq.endsWith(".gz") ? new String[]{"gzip", "-c", "-d",sampleInfo.getBasicInfo().fastq,"|", "wc", "-l"}: new String[]{"cat ", sampleInfo.getBasicInfo().fastq, "|","wc","-l"};
                String[] cmds = {"/bin/bash", "-c"};
                lineNumber = Integer.parseInt(Utils.RunCommand(cmds,cat).trim());
            } else {
                lineNumber = new FileUtils(sampleInfo.getBasicInfo().fastq).getLineNumber();
            }
        } catch (SetAException e) {
            log.error(String.format("文件%s不存在%n", sampleInfo.getBasicInfo().fastq));
        }
        sampleInfo.getCalResult().fqReads = lineNumber / 4d;
    }

    //计算变异系数
    public static double calSTDEVA(ArrayList<Float> data) {
        if(data.size()<=1){
            return 0;
        }
        //总和
        float sum = 0;
        for (float i : data) {
            sum += i;
        }
        //平均值
        float average = sum / data.size();
        //方差
        double Variance = 0;
        for (double i : data) {
            Variance += (i - average) * (i - average);
        }
        Variance = Variance / ( data.size() - 1);
        //变异系数（标准差/均值）
        return Math.sqrt(Variance)/average;
    }


}
