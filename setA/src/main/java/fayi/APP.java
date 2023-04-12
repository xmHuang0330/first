package fayi;

import fayi.WriteExcel.SingleExcel;
import fayi.config.Config;
import fayi.config.DefaultParam;
import fayi.config.Enum.Gender;
import fayi.config.Param;
import fayi.seqParser.RazorOutParse;
import fayi.seqParser.SE400FqTools;
import fayi.tableObject.SampleInfo;
import fayi.utils.*;
import fayi.xml.Objects.BasicInfo;
import fayi.xml.Objects.Data;
import fayi.xml.Objects.Sample;
import fayi.xml.Objects.SoftWare;
import fayi.xml.Xml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
public class APP implements CommandLineRunner {

    @Autowired
    public CoreSeqCompress compress;
    @Autowired
    private RazorOutParse razorOutParse;
    @Autowired
    SE400FqTools se400FqTools;

    public static void main(String[] args) throws Exception {
        SpringApplication.run(APP.class, args);
    }

    private void outputConfigParams() {
        FileUtils fileUtils = new FileUtils(Config.getInstance().getOutputPath() + "/"  + new SimpleDateFormat("yy-MM-dd-hh-mm").format(new Date()) + ".cmdhistory");

        fileUtils.writeLine( Config.getInstance().toString() );

        fileUtils.finishWrite();
    }

    private void createXml() throws SetAException {
        Config config = Config.getInstance();
        FileUtils fileUtils = new FileUtils(config.getSampleFile());
        HashMap<String, String> nameFile = new HashMap<>();
        String s;
        while ((s = fileUtils.readLine()) != null) {
            s = s.trim();
            try {
                Utils.checkReadFile(s);
            } catch (SetAException e) {
                log.error(e.getMessage());
//                System.exit(1);
            }
            String name = new File(s).getName();
//            System.out.println(name);
            nameFile.put(name, s);
        }
        Data data = new Data();

        String pattern = "(V\\d+[A-Za-z]?_L0\\d{1})";
        Pattern compile = Pattern.compile(pattern);
        Param param = Param.getInstance();
        for (String name : nameFile.keySet()) {

            //获取lane号
            Matcher matcher = compile.matcher(name);
            String lane = "";
            if (matcher.find()) {
                lane = matcher.group(1);
            } else {
                throw new SetAException(1, "未匹配到rawdata的lane号," + name);
            }

            //文件名使用lane号分割，得到短的文件名
            BasicInfo basicInfo = new BasicInfo();
            basicInfo.setLane(lane);
            basicInfo.id = Integer.parseInt(name.split(lane + "_")[1].split("[.]")[0]);
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
            basicInfo.well = param.sampleTW.get(basicInfo.id);
            basicInfo.panel = param.samplePanel.get(basicInfo.id);
            basicInfo.gender = param.genderMap.getOrDefault(basicInfo.id, Gender.uncertain);
            basicInfo.type = param.sampleType.getOrDefault(basicInfo.id, "");
            basicInfo.project = param.sampleProject.getOrDefault(basicInfo.id, "");
            basicInfo.name = param.sampleName.getOrDefault(basicInfo.id, "");
            basicInfo.fastq = nameFile.get(name);
            basicInfo.softWare = new SoftWare("AnalyseAndCalculate", config.getArtifact(), config.getVersion());
            data.samples.add(new Sample(basicInfo));
        }

        // 合并样本
        if(config.mergeSameName) {
            Optional<Integer> maxId = data.samples.stream().map( sample -> sample.getBasicInfo().id ).max( Integer::compareTo );
            if (! maxId.isPresent()) {
                throw new SetAException( 301, "未能获取最大index值" );
            }
            Integer id = maxId.get();
            log.info( String.format( "max id in : %s", id ) );
            id += 5000;
            Map<String, List<Sample>> collect1 = data.samples.stream().collect( Collectors.groupingBy( sample -> sample.getBasicInfo().name, Collectors.toList() ) );
            for (List<Sample> samples : collect1.values()) {
                if (samples.size() > 1 && ! "".equals( samples.get( 0 ).getBasicInfo().name )) {
                    id++;
                    String fastq = samples.stream().map( sample -> sample.getBasicInfo().fastq ).collect( Collectors.joining( " " ) );
                    BasicInfo basicInfo = new BasicInfo();
                    basicInfo.setLane( samples.get( 0 ).basicInfo.lane );
                    basicInfo.setId( id );
                    basicInfo.setTablet( "name-merge" );
                    basicInfo.setGender( samples.get( 0 ).basicInfo.gender );
                    basicInfo.setName( samples.get( 0 ).basicInfo.name );
                    basicInfo.setProject( samples.get( 0 ).basicInfo.project );
                    basicInfo.setFastq( fastq );
                    data.samples.add( new Sample( basicInfo ) );
                }
            }
        }


        data.sampleNum = data.samples.size();
        new Xml().dataToXml(data, config.getOutput());
    }

    private void updateXml() throws SetAException {
        Config config = Config.getInstance();

        Data data = new Xml().xmlToData(config.getSampleFile());

        List<Integer> indexes = data.samples.parallelStream().map(sample -> sample.getBasicInfo().id).collect(Collectors.toList());
        String collect = config.getParam().sampleName.keySet().stream().filter(integer -> !indexes.contains(integer)).map(String::valueOf).collect(Collectors.joining(", "));

        if (!collect.equals("")){
            log.warn("下列样本不存在于xml文件样本中，请检查！");
            log.warn(String.join(", ", collect));
        }
        for(Sample sample: data.samples){
            sample.basicInfo.name = config.getParam().sampleName.get(sample.basicInfo.id);
            sample.basicInfo.tablet = config.getParam().sampleTablet.get(sample.basicInfo.id);
            sample.basicInfo.well = config.getParam().sampleTW.get(sample.basicInfo.id);
            sample.basicInfo.type = config.getParam().sampleType.get(sample.basicInfo.id);
            sample.basicInfo.project = config.getParam().sampleProject.get(sample.basicInfo.id);
        }

        data.sampleNum = data.samples.size();
        new Xml().dataToXml(data, config.getOutput());
    }

    @Override
    public void run(String... args) throws Exception {
        String[] mainCommands = new String[]{"createXml", "fastqToXml", "xmlToXlsx", "ALL","updateInfo"};

        String[] S = {"-i","C:\\Users\\dr\\Desktop\\setA\\L01.list","-o","C:\\Users\\dr\\Desktop\\setA\\L01","-sampleExcel","C:\\Users\\dr\\Desktop\\setA\\V350127877_L01.xlsx","-worker","5","-qualityFilter","-razorWorker","1","ALL","-fastq","-noMergeSameName"};

        System.setProperty("file.encoding", "UTF-8");
        log.info(System.getProperty("os.name"));

        log.info(String.valueOf(DefaultParam.STUTTER_MAX_PROPORTION));

        String usage = "\n请使用" + Arrays.toString(mainCommands) + "中的一个作为程序第一个参数。\n" +
                "createXml 生成xml，需要fastq文件列表，样本信息表，输出文件\n" +
                "fastqToXml 分析fastq数据，将得到的数据输出到xml文件，需要createXml生成的xml\n" +
                "xmlToXlsx 将最终的xml文件生成为excel报告，（多个）样本输出到给定的目录\n" +
                "ALL 上面三个流程，-o输出应该是前缀，比如要输出到L01/L01.xml L01/L01_result.xml L01/L01_reports.xml。" +
                "updateInfo 更新xml的样本信息\n";

        /*if (args.length < 1) {
            System.out.println(usage);
            System.exit(1);
        }*/

        Config config = Config.getInstance();
        CommandLineConfig commandLineConfig = new CommandLineConfig();
        commandLineConfig.commandLineParse(Arrays.copyOfRange(S, 0, S.length));
        //log.info(args[0]);

        Analyse analyse = new Analyse( se400FqTools, razorOutParse, compress );

        String outPrefix = config.getOutput();
        config.setOutput(outPrefix + ".xml");
        createXml();
        log.info( "-- analyzing samples " );
        config.setSampleFile( outPrefix + ".xml" );
        config.setOutput(outPrefix + "_result.xml");
        ArrayList<SampleInfo> data = analyse.start(config.getSampleFile());
        log.info( "-- export sample reports..." );
        config.setSampleFile( outPrefix + "_result.xml" );
        config.setOutput(outPrefix + "_reports");
        Utils.checkDir(config.getOutput());
        new SingleExcel().start(data);

        /*switch (args[0]) {
            case "createXml": {
                createXml();
                break;
            }
            case "fastqToXml": {
                config.setAlignPath(config.getOutputPath() + "/align/");
                config.setQualityFilter(true);
                log.info(Param.getInstance().locusOrder.keySet().toString());
//                    config.setNextAllele(true);
//                    config.setNoNoiseFilter(true);
//                    config.setDoFlanking(true);
//                    config.setWorker((byte) Integer.parseInt("28"));
//                    config.setRazorWorker("2");
                analyse.start(config.getSampleFile());
//                    FakeFiseq fakeFiseq = new FakeFiseq();
//                    fakeFiseq.copyXml();
                break;
            }
            case "xmlToXlsx": {
                Utils.checkDir(config.getOutput());
                new SingleExcel().start();
                break;
            }
            case "ALL": {
                String outPrefix = config.getOutput();
                config.setOutput(outPrefix + ".xml");
                createXml();
                log.info( "-- analyzing samples " );
                config.setSampleFile( outPrefix + ".xml" );
                config.setOutput(outPrefix + "_result.xml");
                ArrayList<SampleInfo> data = analyse.start(config.getSampleFile());
                log.info( "-- export sample reports..." );
                config.setSampleFile( outPrefix + "_result.xml" );
                config.setOutput(outPrefix + "_reports");
                Utils.checkDir(config.getOutput());
                new SingleExcel().start(data);
                break;
            }
            case "updateInfo": {
                updateXml();
                break;
            }
            default: {
                System.err.println(usage);
            }
        }*/

        outputConfigParams();
        log.info("Finished, time used ");

    }
}
