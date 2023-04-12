package fayi.tableObject;
import fayi.config.Config;
import fayi.config.DefaultParam;
import fayi.config.Enum.EmptyReason;
import fayi.config.Enum.Gender;
import fayi.config.Enum.Panel;
import fayi.config.Param;
import fayi.seqParser.CorePicker;
import fayi.utils.SetAException;
import fayi.utils.Utils;
import fayi.xml.Objects.BasicInfo;
import fayi.xml.Objects.CalResult;
import fayi.xml.Objects.Site;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/*
    样本信息
 */
@Data
@Slf4j
@Component
public class SampleInfo implements Comparable<SampleInfo> {
    //基本信息
    private BasicInfo basicInfo;
    // 报告信息
    public String Analysis = new SimpleDateFormat( "yyyyMMdd" ).format( new Date() );
    public String Run = new SimpleDateFormat( "yyyyMMdd" ).format( new Date() );
    private String Created = new SimpleDateFormat( "dd MMM yyyy hh:mma", Locale.ENGLISH ).format( new Date() );
    public String User = "";
    //str.out文件中的所有str信息
    private HashMap<String, List<StrInfo>> strData = new HashMap<>();
    private HashMap<String, List<MHInfo>> mhData = new HashMap<>();
    //str.out文件中的所有str信息
    private HashMap<String, List<StrInfo>> strDataAboveAT = new HashMap<>();
    private HashMap<String, List<StrInfo>> strDataAboveIT = new HashMap<>();
    private HashMap<String, List<SnpInfo>> snpDataAboveAt = new HashMap<>();
    //snp.out文件中的所有snp信息
    private HashMap<String, ArrayList<SnpInfo>> snpData = new HashMap<>();
    //str位点信息
    private HashMap<String, StrLocusInfo> strLocusInfo = new HashMap<>();
    private HashMap<String, MHLocusInfo> MHLocusInfo = new HashMap<>();
    //snp位点信息
    private HashMap<String, SnpLocusInfo> snpLocusInfo = new HashMap<>();
    //位点双等位基因的深度比值
    private HashMap<String, double[]> IntraLocusDepth = new HashMap<>();
    //高于at阈值的allele个数
    private HashMap<String, Integer> chrACCount = new HashMap<>();
    private HashMap<String, Double> locusAT = new HashMap<>();
    private HashMap<String, Double> locusIT = new HashMap<>();
    //统计信息
    private CalResult calResult = new CalResult();
    private Integer lowerThan30;
    private Integer lowerThan100;
    private HgInfo hgInfo;

    //获取str数据（是否包括低于at阈值的）
    public List<StrInfo> getStrDataAboveAt(String locus) {
        if (!strDataAboveAT.containsKey(locus)) {
            strDataAboveAT.put(locus, strData.getOrDefault(locus, new ArrayList<>()).stream().filter(SeqInfo::isAboveAT).collect(Collectors.toList()));
        }
        return strDataAboveAT.get(locus);
    }

    public void resetStrDataAboveAtStore(String locus){
        strDataAboveAT.remove( locus );
    }

    public List<StrInfo> getStrDataAboveIt(String locus) {
        if (!strDataAboveIT.containsKey(locus)) {
            strDataAboveIT.put(locus, strData.getOrDefault(locus, new ArrayList<>()).stream().filter(SeqInfo::isAboveIT).collect(Collectors.toList()));
        }
        return strDataAboveIT.get(locus);
    }

    public List<SnpInfo> getSnpDataAboveAt(String locus) {
        if (!snpDataAboveAt.containsKey(locus)) {
            snpDataAboveAt.put(locus, snpData.getOrDefault(locus, new ArrayList<>()).stream().filter(SeqInfo::isAboveAT).collect(Collectors.toList()));

        }
        return snpDataAboveAt.get(locus);
    }

    //深度 低于AT的序列，高于AT的序列，高于IT的序列
    public void strDataFilter() {
        for (String locus : Param.getInstance().StrLocusOrder) {
            if (strData.containsKey( locus )) {
                for (StrInfo strInfo : strData.get( locus )) {
                    if (strInfo.getReads() > locusAT.get( locus )) {
                        if (strInfo.getReads() >= locusIT.get( locus )) {
                            strInfo.setAboveIT( true );
                        }
                        strInfo.setAboveAT(true);
                    }
                }
            }
        }
        for (String locus : Param.getInstance().SnpLocusOrder) {
            if(snpData.containsKey(locus)) {
                for (SnpInfo snpInfo : snpData.get(locus)) {
                    if (snpInfo.getReads() > locusAT.get(locus)) {
                        if (snpInfo.getReads() >= locusIT.get(locus)) {
                            snpInfo.setAboveIT(true);
                        }
                        snpInfo.setAboveAT(true);
                    }
                }
            }
        }
    }

    public int getId() {
        return basicInfo.id;
    }

    //获取snp数据列表，用于生成报告excel
    public ArrayList<ArrayList<Object>> getSnpDataAsList() {
        ArrayList<ArrayList<Object>> arrayList = new ArrayList<>();
        for (String name : Param.getInstance().SnpLocusOrder) {
            for (SnpInfo si : snpData.getOrDefault(name, new ArrayList<>())) {
                arrayList.add(si.FormatAsList(calResult.snpAvg));
            }
        }
        return arrayList;
    }
    //获取str数据列表（是否包括低于at阈值的），用于生成xml
    public ArrayList<Site> getStrDataAsSites() {
        ArrayList<Site> strSites = new ArrayList<>();
        for (String locus : strData.keySet()) {
            int count = 0;
            for (StrInfo si : strData.get(locus)) {
                if (si.aboveAT || si.getTyped()) {
                    strSites.add(si.formatAsSite());
                    count++;
                }
            }
            if (count == 0) {
                if (strData.getOrDefault(locus, new ArrayList<>()).size() > 0) {
                    strSites.add(strData.get(locus).get(0).formatAsSite());
                }
            }
        }
        return strSites;
    }

    //获取snp数据列表，用于生成xml
    public ArrayList<Site> getSnpDataAsSites() {
        ArrayList<Site> snpSites = new ArrayList<>();

        for (String locus : snpDataAboveAt.keySet()) {
            for (SnpInfo snpInfo : getSnpDataAboveAt( locus )) {
                snpSites.add( snpInfo.formatAsSite() );
            }
        }
        return snpSites;
    }

    /*设置性别，若未给定性别，则根据以下规则判断：
        女性：有allele高于AT的X-STR位点（包括Amelogenin的X）多于5个，且 有allele高于AT的Y-STR位点（包括Amelogenin、SRY和Y-indel的Y，此外Amelogenin的Y权重*10）少于等于15个
        男性：有allele高于AT的X-STR位点（包括Amelogenin的X）多于5个，且 有allele高于AT的Y-STR位点（包括Amelogenin、SRY和Y-indel的Y，此外Amelogenin的Y权重*10）多于15个
        不确定：男女都不满足
     */
    public void setGender(Gender givenGender) {
        if (null == givenGender) {

            if (Config.getInstance().getPanel().equals(Panel.setA)) {
                setAGender();
            } else if (Config.getInstance().getPanel().equals(Panel.setB)) {

                if (0.3 <= calResult.getYProportion()) {
                    basicInfo.gender = Gender.male;
                } else if (calResult.getYProportion() <= 0.1) {
                    basicInfo.gender = Gender.female;
                }
            }
        } else {
            basicInfo.gender = givenGender;
        }
    }

    private void setAGender() {
        int autoStrAboveAT = 0;
        int yStrAboveAT = 0;
        for (String locus : Param.getInstance().StrLocusOrder) {
            List<StrInfo> sameLocusList = getStrDataAboveAt( locus );

            if (sameLocusList.size() == 0) {
                continue;
            } else if (Param.getInstance().AutoStrLocusOrder.contains( locus )) {
                autoStrAboveAT += 1;
            } else if (Param.getInstance().YStrLocusOrder.contains( locus )) {
                yStrAboveAT += 1;
            } else if (locus.equals( "Amelogenin" )) {
                for (StrInfo si : sameLocusList) {
                    if (si.getAlleleName().equals( "0" )) {
                        autoStrAboveAT += 1;
                    } else if (si.getAlleleName().equals( "1" )) {
                        yStrAboveAT += 10;
                    }
                }
            } else if (Arrays.asList("SRY", "Y-indel").contains(locus)) {
                yStrAboveAT += 1;
            }
        }
        if (autoStrAboveAT > 5) {
            if (yStrAboveAT > 35) {
                basicInfo.gender = Gender.male;
            } else {
                basicInfo.gender = Gender.female;
            }
        } else {
            basicInfo.gender = Gender.uncertain;
        }
    }

    //性别位点转换为X\Y
    public void setGenderAllele() {
        for (SeqInfo si : strLocusInfo.getOrDefault("Amelogenin", StrLocusInfo.getEmpty()).getAllele()) {
            if (si.getAlleleName().equals("0")) {
                si.setExcelName("X");
            } else if (si.getAlleleName().equals("1")) {
                si.setExcelName("Y");
            }
        }
        if (strLocusInfo.getOrDefault("Y-indel", StrLocusInfo.getEmpty()).getAllele().size() > 0) {
            for (SeqInfo si : strLocusInfo.get("Y-indel").getAllele()) {
                si.setExcelName("Y");
            }
        }
        if (strLocusInfo.getOrDefault("SRY", StrLocusInfo.getEmpty()).getAllele().size() > 0) {
            if (strLocusInfo.get("SRY").getAllele().get(0).getAlleleName().equals("1")) {
                strLocusInfo.get("SRY").getAllele().get(0).setExcelName("Y");
            }
        }
    }


    //设置参数AT、IT为数值，而不是百分比
    public void setATDepthValue() {
        for (String locus : Config.getInstance().getParam().StrLocusOrder) {
            Integer locusDepth = strLocusInfo.getOrDefault(locus, StrLocusInfo.getEmpty()).getTotalDepth();
            if (locusDepth > 650) {
                locusAT.put(locus, Param.getInstance().AT.getOrDefault(locus, 4.5) * 0.01 * locusDepth);
                locusIT.put(locus, Param.getInstance().IT.getOrDefault(locus, 4.5) * 0.01 * locusDepth);
            } else {
                locusAT.put(locus, Param.getInstance().AT.getOrDefault(locus, 4.5) * 0.01 * 650);
                locusIT.put(locus, Param.getInstance().IT.getOrDefault(locus, 4.5) * 0.01 * 650);
            }
        }
        for (String locus : Param.getInstance().SnpLocusOrder) {
            Integer locusDepth = snpLocusInfo.getOrDefault(locus, new SnpLocusInfo(locus)).getTotalDepth();
            if (locusDepth > 650) {
                locusAT.put(locus, Param.getInstance().AT.getOrDefault(locus, 1.5) * 0.01 * locusDepth);
                locusIT.put(locus, Param.getInstance().IT.getOrDefault(locus, 4.5) * 0.01 * locusDepth);
            } else {
                locusAT.put(locus, Param.getInstance().AT.getOrDefault(locus, 1.5) * 0.01 * 650);
                locusIT.put(locus, Param.getInstance().IT.getOrDefault(locus, 4.5) * 0.01 * 650);
            }
        }
    }

    public Double getLocusAT(String locus) {
        return locusAT.get(locus);
    }
    public Double getLocusIT(String locus) {
        return locusIT.get(locus);
    }
    //获取snp等位基因型
    public HashMap<String, ArrayList<String>> getSnpAlleleAsStringList() {
        HashMap<String, ArrayList<String>> locusInfos = new HashMap<>();
        for (String locus:Param.getInstance().SnpLocusOrder) {
            ArrayList<String> locusInfo = new ArrayList<>();
            for(SnpInfo snpInfo:snpData.getOrDefault(locus,new ArrayList<>())){
                if(snpInfo.getTyped()) {
                    locusInfo.add(snpInfo.getAlleleName());
                }
            }
            locusInfos.put(locus,locusInfo);
        }
        return locusInfos;
    }
    public HashMap<String,String> getSnpAlleleAsString(){
        return Utils.formatAlleleAsTwoValueQuatedOrINC(getSnpAlleleAsStringList());
    }
    //根据基因型排序str数据
    public void sortSeqData() {
        for(List<StrInfo> strInfos : strData.values()){
            strInfos.sort(Comparator.comparing(s -> Float.valueOf(s.getAlleleName())));
        }
    }
    //str位点深度标准差，用于计算Interlocus Balance
    public double getSTD() {
        float locusCount = 0;
        Integer totalDepth = 0;
        for(String locus:Param.getInstance().StrLocusOrder){
            if(Gender.female.equals( getBasicInfo().gender )){
                if(Param.getInstance().YStrLocusOrder.contains( locus ) || Arrays.asList( "Y-indel","SRY" ).contains( locus )){
                    continue;
                }
            }
            locusCount += 1;
            totalDepth += strLocusInfo.getOrDefault( locus,StrLocusInfo.getEmpty() ).getTotalDepth();
        }
        double locusCoverage = totalDepth / locusCount;

        ArrayList<Double> doubles = new ArrayList<>();
        for(String locus: Param.getInstance().StrLocusOrder){
            if(Gender.female.equals( getBasicInfo().gender )){
                if(Param.getInstance().YStrLocusOrder.contains( locus ) || Arrays.asList( "Y-indel","SRY" ).contains( locus )){
                    continue;
                }
            }
            doubles.add(strLocusInfo.get( locus ).getTotalDepth()/locusCoverage);
        }
        double sum = 0d;
        for (double i : doubles) {
            sum += i;
        }
        double fangchaSum = 0d;
        for (double i : doubles) {
            fangchaSum += (i - (sum / doubles.size())) * (i - (sum / doubles.size()));
        }
        return Math.sqrt(fangchaSum / (locusCount - 1));
    }

    public void initLocusInfo() {

        for (String locus : Config.getPanelParam(basicInfo.panel).StrLocusOrder) {
            strLocusInfo.put(locus, new StrLocusInfo(locus));

            for (StrInfo strInfo : strData.getOrDefault(locus, new ArrayList<>())) {
                calResult.strSumDepth += strInfo.getForward() + strInfo.getReverse();
                strLocusInfo.get(locus).setForwardTotal(strInfo.getForward());
                strLocusInfo.get(locus).setReverseTotal(strInfo.getReverse());
            }
        }

        for (String locus : Config.getPanelParam(basicInfo.panel).SnpLocusOrder) {
            snpLocusInfo.put(locus, new SnpLocusInfo(locus));

            for (SnpInfo snpInfo : snpData.getOrDefault(locus, new ArrayList<>())) {
                calResult.strSumDepth += snpInfo.getForward() + snpInfo.getReverse();
                snpLocusInfo.get(locus).setForwardTotal(snpInfo.getForward());
                snpLocusInfo.get(locus).setReverseTotal(snpInfo.getReverse());
            }
        }
    }

    @Override
    public int compareTo(SampleInfo sampleInfo) {
        return basicInfo.id.compareTo(sampleInfo.basicInfo.id);
    }

    public HgInfo getHgInfo() {
        if (hgInfo == null) {
            hgInfo = new HgInfo();
        }
        return hgInfo;
    }

    public void setHgInfo(HgInfo hgInfo) {
        this.hgInfo = hgInfo;
    }

    public String getName() {
        return this.basicInfo.name;
    }

    public void setYProportion() {
        float ySum = 0f;
        for (String locus : Param.getInstance().YStrLocusOrder) {
            ySum += getStrLocusInfo().get(locus).getTotalDepth();
        }
        float sum = 0f;
        for (String locus : Param.getInstance().AutoStrLocusOrder) {
            sum += getStrLocusInfo().get(locus).getTotalDepth();
        }
        float yProportion = (ySum / Param.getInstance().YStrLocusOrder.size()) / (sum / Param.getInstance().AutoStrLocusOrder.size());
        calResult.setYProportion(yProportion);
    }

    private Param param = Param.getInstance();

    @Autowired
    private CorePicker corePicker;

    private void removeAlleles(StrLocusInfo strLocusInfo, EmptyReason emptyReason) {
        for (SeqInfo seqInfo : strLocusInfo.getAllele()) {
            seqInfo.setTyped( false );
        }
        if (null == strLocusInfo.getEmptyReason())
            strLocusInfo.setEmptyReason( emptyReason );
//        else log.warn(String.format("位点空缺原因已经被设置，位点 %s",locusInfo.getLocusName()));
        strLocusInfo.setAllele( new ArrayList<>() );
    }

    public HashMap<String,MHLocusInfo> getMHLocusInfo() {
        return MHLocusInfo;
    }

    public void setMHLocusInfo(HashMap<String,MHLocusInfo> mhLocusInfo) {
        this.MHLocusInfo = mhLocusInfo;
    }
}
