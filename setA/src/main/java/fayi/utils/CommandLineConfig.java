package fayi.utils;

import fayi.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

@Slf4j
public class CommandLineConfig {

    Options options = new Options();

    public CommandLineConfig(){
        options.addOption("i","input",true,"输入文件，例如\"C:\\samples\\a.xml\"。");
        options.addOption("o","output",true,"输出xml文件，请指定有写入权限的文件夹");
        options.addOption("worker",true,"分析样本和输出报告时使用的worker数。默认1");
        options.addOption("razorWorker", true, String.format("razor的-p数。默认%s", Config.getInstance().getWorker()));
        options.addOption("razorConfig", true, "razor的配置文件。");
        options.addOption("sampleInfo", true, "样本信息单，第一列为样本名，第二列为性别：男、女 ，第三列为样本类型");
        options.addOption("sampleExcel", true, "样本信息表，excel文件");
        options.addOption("useGivenGender", false, "使用样本表中指定的性别，不跟参数，默认为false");
        options.addOption("snp", true, "样本snp结果的后缀名，默认为 _SNP.out");
        options.addOption("str", true, "样本str结果的后缀名，默认为 _STR.out");
        options.addOption("locus", true, "zhi fen xi gai wei dian");

        options.addOption("unsplitRawdata", true, "未拆分fq.gz");
        options.addOption("ms", false, "标记snp");
        options.addOption("mix", false, "所有样本按照混合流程");
        options.addOption("noFlanking", false, "处理flanking流程");
        options.addOption("noNoiseFilter", false, "不去除过噪位点，该参数只对常染色体（双等位位点，不包括Yab）进行污染判断。");
        options.addOption("outHighestOnly",false, "！决定单等位位点（Y）是否限制深度占比，（如果次峰与主峰比值高于0.9则该位点不输出。该参数使流程不做该判断）");
        options.addOption("ySTD",true, String.format("自定Y输出标准试剂盒，默认%s", Config.getInstance().getYSTD()));

//        required.addOption("rawdataPath",true,"已拆样本原始数据存放路径，默认是 split/ 。里面的原始数据名的格式应该为: 前缀 样本名 后缀。 例如 V10011_L01_sample1.fq.gz ");
//        required.addOption("rawdataPrefix",true,"已拆样本原始数据前缀，默认会从rawdataPath中.gz/.txt结尾的文件自动获取前缀。 例如命令rawdataPath示例中的 V10011_L01_ ");
//        required.addOption("rawdataSuffix",true,"已拆样本原始数据后缀，默认会从rawdataPath中.gz/.txt结尾的文件自动获取后缀。 例如命令rawdataPath示例中的 .fq.gz");

        options.addOption("project", true, "只分析指定项目");
//        required.addOption("analysis",true,"分析时间");
//        required.addOption("run",true,"运行时间");
//        required.addOption("user",true,"用户名");

        options.addOption("param",true,"阈值参数表，默认为该程序的同等路径下的 params_nofilter.xlsx。用户不要使用");
        options.addOption("template",true,"单个样本的输出模版，默认为该程序的同等路径下的 report_template.xlsx.用户不要使用");
        options.addOption("strConfig",true,"str配置表，默认为该程序的同等路径下的 STR_SNP_config.xlsx。用户不要使用");
        options.addOption("tmp",true,"临时文件夹。默认该程序同路径下的temp");
        options.addOption( "strOrder",true,"str位点顺序" );
        options.addOption( "snpOrder",true,"snp位点顺序" );
        options.addOption( "noRazor",false,"不运行razor,输出目录已经存在out，默认false" );
        options.addOption( "fastq",false,"读取总reads数" );
        options.addOption( "noFilter",false,"使用不过滤参数" );
        options.addOption( "noFilterDepth",true,"不过滤参数最低深度要求，默认为10dp" );
        options.addOption("qualityFilter", false, "过滤输出样本：Auto_AlleleCount <=30，Y_AlleleCount <=50，STR均值>200（且关系）");
        options.addOption("test", false, "测试");
        options.addOption( "noLimit",true,"no limit for some locus，space separated locus" );
        options.addOption( "noMergeSameName", false,"不进行同名样本合并" );

        options.addOption("quiet", false, "");
        options.addOption("h", false, "打印该帮助信息");
    }

    public void commandLineParse(String[] args) throws SetAException, ParseException {
        if(args.length==0){
            printHelpMsg();
        }
        DefaultParser defaultParser = new DefaultParser();
        try {
            Config config = Config.getInstance();
            CommandLine requireCml = defaultParser.parse(options, args,false);

            for(Option opt:requireCml.getOptions()){
                String name = opt.getOpt();
                switch (name){
                    case "i": {
                        config.setSample(opt.getValue());
                        continue;
                    }
                    case "o": {
                        config.setOutput(opt.getValue());
                        continue;
                    }
                    case "project": {
                        config.setProjectOnly(opt.getValue());
                        continue;
                    }
                    case "quiet": {
                        config.setQuiet(true);
                        continue;
                    }
                    case "worker": {
                        try {
                            byte i = (byte) Integer.parseInt(opt.getValue());
                            config.setWorker(i);
                            continue;
                        } catch (NumberFormatException e) {
                            throw new SetAException(1, "设置的worker数有问题");
                        }
                    }
                    case "useGivenGender":{
                        config.useGivenGender = true;
                        continue;
                    }
                    case "param": {
                        config.setParamXlsx( opt.getValue() );
                        continue;
                    }
                    case "template": {
                        config.setReportTemplateXlsx( opt.getValue() );
                        continue;
                    }
                    case "fastq": {
                        config.setFastq(true);
                        continue;
                    }
                    case "ms": {
                        config.setMarkSnp(true);
                        continue;
                    }
                    case "mix": {
                        config.setMix(true);
                        continue;
                    }
                    case "ySTD": {
                        config.setYSTD(opt.getValue());
                        continue;
                    }
                    case "noFlanking": {
                        log.warn("flanking used ~~~!");
                        config.setNoFlanking(true);
                        continue;
                    }
                    case "qualityFilter": {
                        config.setQualityFilter(true);
                        continue;
                    }
                    case "noFilter": {
                        config.setNoFilter(true);
//                        config.setParamXlsx( config.getNoFilterParamXlsx() );
                        continue;
                    }

                    case "noFilterDepth": {
                        try {
                            config.setNoFilterDepth(Double.parseDouble(opt.getValue()));
                        }catch (NumberFormatException e){
                            throw new SetAException(1, String.format("%s参数应该是%s类型","noFilterDepth","Integer" ));
                        }
//                        config.setParamXlsx( config.getNoFilterParamXlsx() );
                        continue;
                    }
                    case "noNoiseFilter": {
                        log.warn("noNoiseFilter used ~~~!");
                        config.setNoNoiseFilter( true );
//                        config.setParamXlsx( config.getNoFilterParamXlsx() );
                        continue;
                    }
                    case "strConfig": {
                        config.setStrLocus( opt.getValue() );
                        continue;
                    }
                    case "tmp": {
                        config.setTempDir( opt.getValue() );
                        continue;
                    }
                    case "snp": {
                        config.snpSuffix = opt.getValue();
                        continue;
                    }
                    case "str": {
                        config.strSuffix = opt.getValue();
                        continue;
                    }
                    case "unsplitRawdata": {
                        config.setUnsplitRawData( opt.getValue() );
                        continue;
                    }
                    case "rawdataPrefix": {
                        config.setRawDataPrefix( opt.getValue() );
                        continue;
                    }
                    case "rawdataSuffix": {
                        config.setRawDataSuffix( opt.getValue() );
                        continue;
                    }
                    case "razorWorker": {
                        try {
                            Integer.parseInt( opt.getValue() );
                        }catch (NumberFormatException e){
                            throw new SetAException( 1,"razorWorker值不是数字" );
                        }
                        config.razorWorker = opt.getValue();
                        continue;
                    }
                    case "noRazor": {
                        config.setNoRazor( true );
                        continue;
                    }
                    case "strOrder":{
                        config.setStrLocusOrder( opt.getValue() );
                        continue;
                    }
                    case "snpOrder": {
                        config.setSnpLocusOrder(opt.getValue());
                        continue;
                    }
                    case "sampleInfo": {
                        config.setSampleInfoFile(opt.getValue());
                        continue;
                    }
                    case "sampleExcel": {
                        config.setSampleInfoExcel(opt.getValue());
                        continue;
                    }
                    case "razorConfig": {
                        config.setRazorConfig(opt.getValue());
                        continue;
                    }
                    case "locus": {
                        config.setLocusOnly(opt.getValue());
                        continue;
                    }
                    case "noLimit":{
                        config.setNoLimit(opt.getValue());
                        continue;
                    }
                    case "noMergeSameName":{
                        config.setMergeSameName(false);
                        continue;
                    }
                    case "outHighestOnly":{
                        log.warn("！之输出Y最高峰，不检查次峰占比！");
                        config.setOutHighestOnly(true);
                        continue;
                    }
                    default: {
                        System.err.printf("Unknown param: %s !%n", name);
                        printHelpMsg();
                    }
                }
            }
            if(config.getOutput() == null){
                System.out.println("-o, Output 没有设置");
                System.exit(1);
            }
        } catch (UnrecognizedOptionException e){
            printHelpMsg();
            System.exit(1);
        }
    }

    public void printHelpMsg(){
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("Commandline", options);
        System.out.println();
        System.out.println("使用示例：java -jar this.jar fastqToXml -i /DATA/work/samples/497.xml -o /DATA/work/samples/497_result.xml");
        System.exit(0);
    }

}
