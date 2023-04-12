package test;

import fayi.APP;
import fayi.Analyse;
import fayi.WriteExcel.SingleExcel;
import fayi.config.Config;
import fayi.config.Param;
import fayi.tableObject.StrLocusInfo;
import fayi.tableObject.SampleInfo;
import fayi.utils.ExcelUtils;
import fayi.utils.FileUtils;
import fayi.utils.SetAException;
import fayi.xml.Objects.*;
import fayi.xml.Xml;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@SpringBootTest(classes = {APP.class})
public class testA {
    static HashMap<String, StrConfig> strConfig = new HashMap<>();
    public static class StrConfig{
        public String Marker = "";
        public String Type = "";
        public String Lflank = "";
        public String Rflank = "";
        public String Motif = "";
        public Integer Period = 0;
        public Integer Offset = 0;

        public StrConfig() {
        }

        public StrConfig(String marker, String lflank, String rflank, String motif, Integer period, Integer offset) {
            Marker = marker;
            Lflank = lflank;
            Rflank = rflank;
            Motif = motif;
            Period = period;
            Offset = offset;
        }
    }

    @Autowired
    private Analyse analyse;


    public static void main4(String[] args) throws SetAException, IOException {
        Config.getInstance();

        String[] Y41 = new String[]{"DYS627","DYS576","DYS439","DYS549","DYS570","DYS385a/b","DYS527a/b","DYS593","DYS596","DYS437","DYS456","rs199815934","DYF387S1a/b","DYS449","DYS444","DYS557","DYS481","DYS390","DYS645","DYS19","DYS448","rs759551978","DYS522","DYS438","DYS391","rs771783753","DYS460","Y-GATA-H4","DYS447","DYS518","DYS393","DYS458","DYS643","DYS533","DYS389I","DYS635","DYS389II","DYS392"};
        File[] files = new File("/Users/kaidan/Documents/运行记录/zq/83505/").listFiles(pathname -> pathname.getName().endsWith("_result.xml"));
        String result = "/Users/kaidan/Documents/运行记录/zq/83505/sum.xlsx";
        ArrayList<SampleInfo> sampleInfos = new ArrayList<>();
        SingleExcel singleExcel = new SingleExcel();
        for(File file:files) {
            for(Sample sample:new Xml().xmlToData(file.getAbsolutePath()).samples ){
                sampleInfos.add(singleExcel.sampleToSampleInfo(sample));
            }
        }
        System.out.println(sampleInfos.size());

        HashMap<String,ArrayList<SampleInfo>> duplicate = new HashMap<>();
        HashMap<String,SampleInfo> distinct = new HashMap<>();

        for(SampleInfo sampleInfo:sampleInfos){
            int typed = 0;
            for(String locus:Y41) {
                if (Param.getInstance().YStrLocusOrder.contains(locus) && sampleInfo.getStrLocusInfo().get(locus).getAllele().size()>0) {
                    if(Param.getInstance().BiallelicMale.contains(locus)){
                        typed += 2;
                    }else {
                        typed += 1;
                    }
                }
            }
//            System.out.println(sampleInfo.getBasicInfo().lane+"\t"+sampleInfo.getBasicInfo().id+"\t"+sampleInfo.getBasicInfo().name+"\t"+sampleInfo.getBasicInfo().type+"\t"+typed);

            //重复样本
            if(distinct.containsKey(sampleInfo.getBasicInfo().type)){
                if(!duplicate.containsKey(sampleInfo.getBasicInfo().type)){
                    duplicate.put(sampleInfo.getBasicInfo().type,new ArrayList<>());
                    duplicate.get(sampleInfo.getBasicInfo().type).add(distinct.get(sampleInfo.getBasicInfo().type));
                }
                duplicate.get(sampleInfo.getBasicInfo().type).add(sampleInfo);
            }else{
                distinct.put(sampleInfo.getBasicInfo().type,sampleInfo);
            }
        }
        System.out.println(distinct.size());
        System.out.println(duplicate.size());

        XSSFWorkbook wb = new XSSFWorkbook();
        ArrayList<ArrayList<Object>> values = new ArrayList<>();
        values.add(createHeader());
        for(String key:duplicate.keySet()){
            values.addAll(createGenoList(duplicate.get(key)));
        }

        ExcelUtils.writeData(wb,"校准样本","",0,0,values);
        wb.write(new FileOutputStream(result));
        wb.close();

        FileUtils fileUtils = new FileUtils("/Users/kaidan/Downloads/zq_history");
        String tmp;
        while((tmp = fileUtils.readLine()) != null){
            if(distinct.containsKey(tmp.split("\t")[0])){
                System.out.println(tmp);
            }
        }

    }

    private static ArrayList<Object> createHeader(){
        ArrayList<Object> header = new ArrayList<>();
        header.add("lane");
        header.add("id");
        header.add("type");
        header.add("str均值");
        header.add("auto_Loci_Typed");
        header.add("auto_ac");
        header.add("x_Loci_Typed");
        header.add("x_ac");
        header.add("y_Loci_Typed");
        header.add("y_ac");
        header.addAll(Param.getInstance().StrLocusOrder);
        return header;
    }

    private static ArrayList<ArrayList<Object>> createGenoList(ArrayList<SampleInfo> sampleInfos) {
        ArrayList<ArrayList<Object>> data = new ArrayList<>();

        for (SampleInfo sample : sampleInfos) {
            ArrayList<Object> alleles = new ArrayList<>();
            alleles.add( sample.getBasicInfo().lane );
            alleles.add( sample.getBasicInfo().id );
            alleles.add( sample.getBasicInfo().type );
            alleles.add( sample.getCalResult().strAvg );
            alleles.add( sample.getCalResult().auto_Loci_Typed );
            alleles.add( sample.getChrACCount().get("Auto") );
            alleles.add( sample.getCalResult().x_Loci_Typed );
            alleles.add( sample.getChrACCount().get("X") );
            alleles.add( sample.getCalResult().y_Loci_Typed );
            alleles.add( sample.getChrACCount().get("Y") );
            for (String locus : Param.getInstance().StrLocusOrder) {
                alleles.add( sample.getStrLocusInfo().getOrDefault( locus, new StrLocusInfo() ).getAlleleNameAsString( !Config.getInstance().isMix(),true ) );
            }
            data.add( alleles );
        }
        return data;
    }

}