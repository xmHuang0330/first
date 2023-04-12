package fayi.config;




import fayi.config.Enum.Gender;
import fayi.config.Enum.Panel;
import fayi.config.Enum.SnpPosition;
import fayi.utils.FileUtils;
import fayi.utils.SetAException;
import fayi.utils.Utils;
import fayi.xml.Objects.Sample;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
    配置
 */
@Data
@ToString
@Slf4j
public class Config {


    private String hgSnpFile;
    private String flankingSettingFile;
    private boolean markSnp;
    private boolean mix;
    private String noFilterParamXlsx;
    private Boolean noFilter = false;
    private Double noFilterDepth = 10d;
    private boolean fastq = false;
    public String razorWorker = "1";
    private boolean noRazor = false;
    private String projectPath;
    String razerProgram;
    private HashMap<String, Integer[]> trimConfig;
    private boolean outHighestOnly = false;

    private String sampleFile;
    //输出目录
    private String outputPath;
    private String output;

    private String alignPath;
    public String snpSuffix = "_SNP.out";
    public String strSuffix = "_STR.out";

    private byte worker = 1;
    //配置表
    private String strConfigXlsx;
    //单个样本报告
    private String reportTemplateXlsx;
    //报告总表
    private String reportSumXlsx;
    //统计表
    private String calXlsx;
    private String paramXlsx;
    private String snpLocusOrderFile;
    private String strLocusOrderFile;
    private String trimConfigFile;
    private String flankingConfigFile;
    private String commonSnpFile;
    private String nConfigFile;
    private HashMap<String, String> nRef;
    //拆分的原始数据
    private String UnsplitRawData;
    private String RawDataPath;
    private String RawDataPrefix = "";
    private String RawDataSuffix = "";

    public boolean useGivenGender = true;
    private String tempDir;
    private static Config config;
    private Param param;

    private String version = "1.0.0";
    private String artifact;
    private String SampleInfoFile;
    private boolean qualityFilter = false;

    private String projectOnly;
    private String razorConfig;
    private String noiseFile;
    private Panel panel;
    private HashMap<String, String[]> flankingSetting;
    private HashMap<String, ArrayList<SnpMarker>> commonSnp;
    private HashMap<String, ArrayList<SnpMarker>> hgSnp;
    public HashMap<String, String[]> hgRef = new HashMap<>();
    private String hgRefFile;
    private boolean quiet;
    private boolean doFlanking = true;
    private boolean noNoiseFilter = false;
    private String muscleProgram;
    private String locusOnly;
    private String noLimit = "";
    private String YSTD = "BGI_Y";

    private Config() {
        InputStream resourceAsStream;
        try {
            resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("resource/property.properties");
            Properties properties = new Properties();
            properties.load(resourceAsStream);
            version = properties.getProperty("version");

            if (null != System.getProperty("test.artifactId")) {
                artifact = System.getProperty("test.artifactId");
            } else {
                artifact = properties.getProperty("artifactId");
            }
            panel = Panel.valueOf(artifact);
            log.info(panel.name());
            if (null != resourceAsStream) {
                resourceAsStream.close();
            }
            ApplicationHome applicationHome = new ApplicationHome(Config.class);
            File jarPath = applicationHome.getSource();
            if (null == jarPath) {
                jarPath = applicationHome.getDir();
                projectPath = jarPath.getAbsolutePath() + "/src/main/resources/";
            } else {
                projectPath = jarPath.isFile() ? jarPath.getParent() : jarPath.getAbsolutePath();
            }
//            projectPath = jarPath.getAbsolutePath();
            reportTemplateXlsx = projectPath + "/resource/singleTemplate_" + artifact + ".xlsx";
            reportSumXlsx = projectPath + "/resource/Sum_Report.xlsx";
            strConfigXlsx = projectPath + "/resource/strConfig.xlsx";
            paramXlsx = projectPath + "/resource/" + artifact + "_param.xlsx";
            noFilterParamXlsx = projectPath + "/resource/" + artifact + "params_nofilter.xlsx";
            snpLocusOrderFile = projectPath + "/resource/locusOrder/" + artifact + "_Snp";
            strLocusOrderFile = projectPath + "/resource/locusOrder/" + artifact + "_Str";
            tempDir = String.format("%s/temp_%s/", projectPath, UUID.randomUUID());
            calXlsx = projectPath + "/resource/LaneCalTemplate_" + artifact + ".xlsx";

            razorConfig = projectPath + "/resource/razer/" + artifact + "_config";
            flankingConfigFile = projectPath + "/resource/razer/" + artifact + "_flanking_config";
            flankingSettingFile = projectPath + "/resource/strRef/flankingConfig_" + artifact;

            noiseFile = projectPath + "/resource/noise";

            trimConfigFile = projectPath + "/resource/strRef/trimConfig";
            commonSnpFile = projectPath + "/resource/commonSNP_" + artifact;
            hgSnpFile = projectPath + "/resource/strRef/hg_snp";
            hgRefFile = projectPath + "/resource/hgPredict/hg_ref_" + artifact;
            nConfigFile = projectPath + "/resource/strRef/nConfig";
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                razerProgram = projectPath + "/resource/razer/razer.bin";
                muscleProgram = projectPath + "/resource/muscle/muscle.bin";
            } else if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                razerProgram = projectPath + "/resource/razer/razer.bin";
                muscleProgram = projectPath + "/resource/muscle/muscle_wsl.bin";
            } else if (System.getProperty("os.name").toLowerCase().contains("mac os x")) {
                razerProgram = projectPath + "/resource/razer/str8rzr_osX.bin";
                muscleProgram = projectPath + "/resource/muscle/muscle_osx.bin";
            }
            if(!new File(muscleProgram).setExecutable(true) || !new File(razerProgram).setExecutable(true)){
                throw new SetAException(2, "binary files can't set executable(x)");
            }

        } catch (IOException | SetAException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void setAlignPath(String alignPath) throws SetAException {
        Utils.checkDir(alignPath);
        this.alignPath = alignPath;
    }

    public void setTrimConfig(String trimConfigFile) throws SetAException {
        trimConfig = new HashMap<>();
        FileUtils fileUtils = new FileUtils(trimConfigFile);
        String line;
        Pattern compile = Pattern.compile("([+-]{1})([\\d]+)");

        while ((line = fileUtils.readLine()) != null) {
            line = line.trim();
            String[] split = line.split("\t");
            if (!trimConfig.containsKey(split[0])) {
                trimConfig.put(split[0], new Integer[]{0, 0});
            }
            if (split.length < 2) {
                trimConfig.put(split[0], new Integer[]{0, 0});
                continue;
            }
            Matcher matcher = compile.matcher(split[1]);
            if (matcher.find()) {
                if ("+".equals(matcher.group(1))) {
                    trimConfig.get(split[0])[1] = Integer.parseInt(matcher.group(2));
                } else if ("-".equals(matcher.group(1))) {
                    trimConfig.get(split[0])[0] = Integer.parseInt(matcher.group(2));
                } else {
                    throw new SetAException(1, "序列清理规则不对劲！" + line);
                }
            } else {
                throw new SetAException(1, "序列清理规则不对劲！" + split[1]);
            }
        }
    }

    private void setHgSnp() throws SetAException {
        String pattern = "([+-N])([\\d]+)([ATCG-]+)/([ATCG-]+)";
        Pattern compile = Pattern.compile(pattern);
        hgSnp = new HashMap<>();
        FileUtils fileUtils = new FileUtils(hgSnpFile);
        String line;
        while ((line = fileUtils.readLine()) != null) {
            String[] split = line.split("\t");
            hgSnp.put(split[0], new ArrayList<>());
            for (String snpMarker : split[1].split(",")) {
                Matcher matcher = compile.matcher(snpMarker);
                if (matcher.matches()) {
                    hgSnp.get(split[0]).add(new SnpMarker(SnpPosition.getByPosition(matcher.group(1)), Integer.parseInt(matcher.group(2)), matcher.group(3), matcher.group(4), null));
                } else {
                    throw new SetAException(1, "snpMarker 不是规则：" + snpMarker);
                }
            }
        }
    }

    private void readFlankingSetting() {
        flankingSetting = new HashMap<>();
        FileUtils fileUtils = new FileUtils(flankingSettingFile);
        String s;
        while ((s = fileUtils.readLine()) != null) {
            String[] split = s.trim().split("\t");
            flankingSetting.put(split[0], Arrays.copyOfRange(split, 1, 5));
        }
    }

    private void readHGRef() {

        hgRef = new HashMap<>();
        FileUtils fileUtils = new FileUtils(hgRefFile);
        String s;
        while ((s = fileUtils.readLine()) != null) {
            String[] split = s.trim().split("\t");
            hgRef.put(split[0], Arrays.asList(split[1], split[2]).toArray(new String[]{}));
        }
    }

    private void setCommonSnp(String cscfile) throws SetAException {
        FileUtils fileUtils = new FileUtils(cscfile);
        String line;
        String pattern = "([-+N]{1})([\\d]+)([ATCG-]+)/([ATCG-]+)";
        Pattern compile = Pattern.compile(pattern);

        commonSnp = new HashMap<>();

        while ((line = fileUtils.readLine()) != null) {
            String[] values = line.split("\t");
            ArrayList<SnpMarker> patterns = new ArrayList<>();
            for (String snpPattern : values[1].split(",")) {
                Matcher matcher = compile.matcher(snpPattern);
                if (!matcher.matches()) {
                    throw new SetAException(1, String.format("侧翼区常见SNP规则无法识别，位点%s,规则%s", values[0], snpPattern));
                } else {
                    SnpMarker snpMarker = new SnpMarker(SnpPosition.getByPosition(matcher.group(1)), Integer.parseInt(matcher.group(2)), matcher.group(3), matcher.group(4), null);
//                    commonSnp.setSpan(flankingSetting.get(values[0])[0].length());
                    patterns.add(snpMarker);
                }
            }
            commonSnp.put(values[0], patterns);
        }
    }


    public static Param getPanelParam(String panel) {
        return Param.getInstance();
    }

    public void setOutput(String output) throws SetAException {
        String parent = new File(output).getParent();
        if (null == parent) {
            parent = "./";
        }
//        System.out.println(parent);
        Utils.checkDir(parent);
        this.outputPath = parent;

        this.output = output;
    }

    public void setStrLocusOrderFile(String strLocusOrderFile) throws SetAException {
        Utils.checkReadFile(strLocusOrderFile);
        this.strLocusOrderFile = strLocusOrderFile;
        setStrLocusOrder(this.strLocusOrderFile);
    }

    public void setTempDir(String dir) throws SetAException {
        Utils.checkDir(dir);
        tempDir = dir;
        new File(tempDir).deleteOnExit();
        System.setProperty("java.io.tmpdir", dir);
    }

    public void setStrLocus(String file) throws SetAException {
        Utils.checkReadFile(file);
        strConfigXlsx = file;
        XSSFWorkbook sheets = null;

        try {
            FileInputStream fileInputStream = new FileInputStream(strConfigXlsx);
            sheets = new XSSFWorkbook(fileInputStream);
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert sheets != null;
        XSSFSheet sheetAt = sheets.getSheetAt(0);
        Iterator<Row> rowIterator = sheetAt.rowIterator();
        HashMap<String, ArrayList<String>> locusSTR = param.locusSTR;
        while (rowIterator.hasNext()){
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            String locus = cellIterator.next().getStringCellValue();
            locusSTR.put(locus,new ArrayList<>());
            while(cellIterator.hasNext()){
                org.apache.poi.ss.usermodel.Cell next = cellIterator.next();
                String stringCellValue = next.getStringCellValue().trim();
                if (!"".equals(stringCellValue)) {
                    locusSTR.get(locus).add(stringCellValue);
                }
            }
//            System.out.println(locus+" | "+locusSTR.get(locus));
        }
    }

    private void setNConfig() {
        FileUtils fileUtils = new FileUtils(nConfigFile);
        if (null == nRef) {
            nRef = new HashMap<>();
        }
        String line;
        while ((line = fileUtils.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            String[] split = line.split("\t");
            nRef.put(split[0], split[1]);
        }
    }

    private void setLocusOrder() {
        for (String panel : new String[]{"PP21", "Y41", "Y41_YSUP", "HXBJ", "standard_auto", "standard_y","MR36A","BGI_Y"}) {
            ArrayList<String> locus = new ArrayList<>();
            FileUtils fileUtils = new FileUtils(projectPath + "/resource/locusOrder/" + panel);
            String s;
            while ((s = fileUtils.readLine()) != null) {
                locus.add(s.trim());
            }
            param.locusOrder.put(panel, locus);
        }
    }

    public void setSnpLocusOrder(String file) throws SetAException {
        Utils.checkReadFile(file);
        snpLocusOrderFile = file;
        FileUtils fileUtils = new FileUtils(file);
        ArrayList<String> snpOrder = param.SnpLocusOrder;
        HashMap<String, ArrayList<String>> snpAlters = param.SnpAlters;
        String s;
        while ((s = fileUtils.readLine()) != null) {
            String[] values = s.trim().split("\t");
            snpOrder.add(values[0]);
            snpAlters.put(values[0],new ArrayList<>(Arrays.asList(values[1],values[2])));
        }
        fileUtils.finishRead();
    }

    // 从已拆分的单个fastq获取RawdataPath
    public void setRawDataPath(ArrayList<Sample> samples) throws SetAException {
        if(samples.size() == 0){
            throw new SetAException(1,"无样本");
        }

        File file = new File(samples.get(0).basicInfo.fastq);
        String rawDataPath = file.getParent();

        File dir = new File(rawDataPath);
        if(!dir.exists() || !dir.isDirectory() ){
            log.warn("原始数据目录未找到");
//            throw new SetAException(1,"目录不存在："+rawDataPath);
        }else {
            RawDataPath = rawDataPath;
        }
        ArrayList<String> files = new ArrayList<>();
        for(Sample sample:samples){
            files.add(sample.basicInfo.fastq);
        }

        String[] prefixAndSuffix = Utils.getSharedPrefixAndSuffixFromFiles(files.toArray(new String[]{}));
        RawDataPrefix = prefixAndSuffix[0];
        RawDataSuffix = prefixAndSuffix[1];
    }

    public void setSample(String file) throws SetAException {
        Utils.checkReadFile( file );
        sampleFile = file;
    }

    private HashMap<String,String> panelConfig = new HashMap<>();

    public static void destory(){
        config = null;
    }

    public static Config getInstance() {
        if (null == config) {

            config = new Config();
            try {
                config.param = Param.getInstance();
                config.setTrimConfig(config.trimConfigFile);
                config.setStrLocus(config.strConfigXlsx);
                config.setSnpLocusOrder(config.snpLocusOrderFile);
                config.setStrLocusOrder(config.strLocusOrderFile);
                config.setLocusOrder();
                config.setHgSnp();
                if (config.getPanel().canPredictHg) config.readHGRef();
                config.readFlankingSetting();
                config.setCommonSnp(config.commonSnpFile);
                config.setTempDir(config.tempDir);
                config.setNConfig();
                config.readNoiseConfig();
                config.setParamXlsx( config.paramXlsx );
            } catch (SetAException e) {
                e.printStackTrace();
            }
        }
        return config;
    }
    public void setStrLocusOrder(String strLocusOrderFile) throws SetAException {
        Utils.checkReadFile(strLocusOrderFile);
        this.strLocusOrderFile = strLocusOrderFile;

        param.AutoStrLocusOrder = new ArrayList<>();
        param.XStrLocusOrder = new ArrayList<>();
        param.YStrLocusOrder = new ArrayList<>();

        FileUtils fileUtils = new FileUtils(strLocusOrderFile);
        String s;
        ArrayList<String> AllOrder = new ArrayList<>();
        ArrayList<String> order = new ArrayList<>();
        while ((s = fileUtils.readLine()) != null) {
            String value = s.trim();
            if (value.startsWith("#")) {
                switch (value) {
                    case "#A": {
                        order = param.AutoStrLocusOrder;
                        continue;
                    }
                    case "#X":{
                        order = param.XStrLocusOrder;
                        continue;
                    }
                    case "#Y":{
                        order = param.YStrLocusOrder;
                        continue;
                    }
                    default:{
                        continue;
                    }
                }
            }
            AllOrder.add(value);
            order.add(value);
            if (value.contains("a/b")) {
                param.BiallelicMale.add(value);
            }

        }
        param.StrLocusOrder = AllOrder;

        param.BiallelicMale.addAll(param.AutoStrLocusOrder);
        param.BiallelicMale.remove("Y-indel");
        param.BiallelicMale.remove("SRY");
        //DYS572 暂时当作 双等位处理
        param.BiallelicMale.add("DYS572");


        param.BiallelicFemale.addAll(param.AutoStrLocusOrder);
        param.BiallelicFemale.remove("Y-indel");
        param.BiallelicFemale.remove("SRY");
        param.BiallelicFemale.addAll(param.XStrLocusOrder);

        param.BiallelicUn.addAll(param.BiallelicMale);
        param.BiallelicUn.addAll(param.XStrLocusOrder);
        fileUtils.finishRead();
    }

    public void setUnsplitRawData(String unsplitRawData) throws SetAException {
        Utils.checkReadFile(unsplitRawData);
        UnsplitRawData = unsplitRawData;
    }

    public void setSampleInfoFile(String file) throws SetAException {
        Utils.checkReadFile(file);
        SampleInfoFile = file;
        String s;
        FileUtils fileUtils = new FileUtils(file);
        while((s = fileUtils.readLine()) != null){
            s = s.trim();
            String[] split = s.split("\t");
            int index;
            try {
                index = Integer.parseInt(split[0].trim());
            }catch (NumberFormatException e){
                if(!"".equals(split[0].trim())) {
                    System.err.println("first column in sampleInfo file is index，should only be integer,you offered : "+split[0]);
                }
                continue;
            }
            if (split.length >= 2) {
                String gender = split[1].trim();
                param.genderMap.put( index , "男".equals( gender ) ? Gender.male : "女".equals( gender ) ? Gender.female : Gender.uncertain );
            }else{
                param.genderMap.put( index , Gender.uncertain );
            }
            if (split.length >= 3) {
                String type = split[2].trim();
                param.sampleType.put( index, type );
            }
            if (split.length >= 4 && !"".equals(split[3].trim())) {
                param.sampleProject.put(index, split[3].trim());
            }
            if (split.length >= 5 && !"".equals(split[4].trim())) {
                param.sampleName.put(index, split[4].trim());
            }
        }
    }

    public void readNoiseConfig() throws SetAException {
        Utils.checkReadFile(noiseFile);
        FileUtils fileUtils = new FileUtils(noiseFile);
        String line;
        while ((line = fileUtils.readLine()) != null) {
            String[] values = line.trim().split("\t");
            param.noiseLimit.put(values[0], new Float[]{Float.parseFloat(values[1]), Float.parseFloat(values[2])});
        }
        fileUtils.finishRead();
    }


    public boolean isOutHighestOnly() {
        return outHighestOnly;
    }

    public void setOutHighestOnly(boolean outHighestOnly) {
        this.outHighestOnly = outHighestOnly;
    }

    public void setYSTD(String ystd) throws SetAException {
        if(!param.locusOrder.containsKey(ystd)){
            throw new SetAException(1, String.format("该试剂盒未录入，请检查！%s", ystd));
        }
        this.YSTD = ystd;
    }

    public String getYSTD() {
        return YSTD;
    }

    public void setNoFilterDepth(Double noFilterDepth) {
        this.noFilterDepth = noFilterDepth;
    }

    public Double getNoFilterDepth() {
        return noFilterDepth;
    }

}
