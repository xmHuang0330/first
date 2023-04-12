package fayi.utils;

import fayi.config.Config;
import fayi.config.Enum.Gender;
import fayi.config.Enum.QC;
import fayi.config.Param;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.awt.*;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Utils {

    public static HashMap<String,String> formatAlleleAsTwoValueQuatedOrINC(HashMap<String,ArrayList<String>> alleleMap){
        HashMap<String,String> alleleStrings = new HashMap<>();
        for(String key:alleleMap.keySet()){
            ArrayList<String> list = alleleMap.get(key);
            switch (list.size()){
                case 0:{
                    alleleStrings.put(key,QC.Not_Analyzed.Indictor);
                    break;
                }
                case 1:{
                    alleleStrings.put(key,list.get(0)+","+list.get(0));
                    break;
                }
                case 2:{
                    Collections.sort(list);
                    alleleStrings.put(key,list.get(0)+","+list.get(1));
                }
            }
        }
        return alleleStrings;
    }

    public static void checkDir(String dir) throws SetAException {

        File tmpdir = new File(dir);
        if(tmpdir.exists()) {
            if( tmpdir.isDirectory()){
                if(!tmpdir.canRead() | !tmpdir.canWrite()) {
                    throw new SetAException(1," folder no permission ");
                }
            }else{
                throw new SetAException(1," there is a file with same name，can't create folder! ");
            }
        }else{
            log.info("create directory "+dir);
            if(!tmpdir.mkdirs()){
                throw new SetAException(1, " folder doesn't exist, and failed creating! "+dir);
            }
        }
    }

    public static void checkReadDir(String dir) throws SetAException {
        File tmpdir = new File(dir);
        if(tmpdir.exists()) {
            if( tmpdir.isDirectory()){
                if(!tmpdir.canRead()) {
                    throw new SetAException(1," folder not readable ");
                }
            }else{
                throw new SetAException(1," there is a file with same name. ");
            }
        }else{
            throw new SetAException(1," folder does not exists: "+dir);
        }
    }

    public static void checkReadFile(String dir) throws SetAException {
        File file = new File(dir);
        if(!file.exists()) {
            throw new SetAException(1,"file doesn't exists: "+dir);
        }else{
            if(!file.canRead()){
                throw new SetAException(1, " file not readable: "+dir);
            }
        }
    }

    public static String getBestMatch(HashMap<String,Integer> shared_Strings){
        String LongestMatchString = "";
        int BiggestMatchTime = 0;
        for(String matchString:shared_Strings.keySet()){
            int matchTime = shared_Strings.get(matchString);
            if(matchTime > BiggestMatchTime){
                LongestMatchString = matchString;
                BiggestMatchTime = matchTime;
            }else if(matchTime == BiggestMatchTime){
                if(matchString.length() > LongestMatchString.length()){
                    LongestMatchString = matchString;
                    BiggestMatchTime = matchTime;
                }
            }
        }
        return LongestMatchString;
    }

    public static ArrayList<ArrayList<Object>> generifyArrayList(){
        ArrayList<ArrayList<Object>> data = new ArrayList<>();
        ArrayList<Object> header = new ArrayList<>();
        header.add( "sample/locus" );
        Param instance = Param.getInstance();
        header.addAll( instance.StrLocusOrder );
        data.add( header );
        return data;
    }

    //获取目录中文件名相同的前、后缀
    public static String[] getSharedPrefixAndSuffixFromFiles(String[] files){

        HashMap<String, Integer> shared_Strings = new HashMap<>();
        String sample = files[0];
        for(String name:files){
            for (int i = 1; i < sample.length(); i++) {
                String subString = sample.substring(0,i);
                if(name.startsWith(subString)){
                    shared_Strings.put(subString,shared_Strings.getOrDefault(subString,0)+1);
                }
            }
        }
        String prefix = getBestMatch(shared_Strings);
        for(String name:files){
            if(!name.startsWith(prefix)){
                prefix = "";
                break;
            }
        }

        shared_Strings = new HashMap<>();
        for(String name:files){
            for (int i = 1; i < sample.length(); i++) {
                String subString = sample.substring(i);
                if(name.endsWith(subString)){
                    shared_Strings.put(subString,shared_Strings.getOrDefault(subString,0)+1);
                }
            }
        }
        String suffix = getBestMatch(shared_Strings);
        for(String name:files){
            if(!name.endsWith(suffix)){
                suffix = "";
                break;
            }
        }
//        System.out.println(prefix+" | "+suffix);
        return new String[]{prefix, suffix};
    }

    public static void poolExecuterWaiter(ExecutorService executorService, String process, AtomicInteger count) throws InterruptedException {
        executorService.shutdown();
//        String[] makk = new String[]{"-","\\","|","/","-","\\","|","/"};
        int time_sum = 0;
        while (!executorService.isTerminated()) {
            time_sum += 1;
            if (! Config.getInstance().isQuiet()) {
                if (time_sum % 10 == 0) {
                    log.info( "Waiting for " + process + " to complete, " + count + " done" );
                }
            }
            Thread.sleep( 500 );
        }
        if (!Config.getInstance().isQuiet()) {
            log.info( "Waiting for " + process + " to complete, " + count + " done" );
        }
    }

    private static XSSFFont bigRed;
    private static XSSFFont bigBlue;
    private static XSSFFont deleted;

    public static XSSFFont createFont(String style) {

        switch (style){
            case "blue":{
                if (bigBlue == null){
                    bigBlue = new XSSFFont();
                    bigBlue.setColor(new XSSFColor(Color.BLUE,null));
                    bigBlue.setFontHeight(13);
                }
                return bigBlue;
            }
            case "red":{
                if(bigRed == null){
                    bigRed = new XSSFFont();
                    bigRed.setColor(new XSSFColor(Color.RED,null));
                    bigRed.setFontHeight(13);
                }
                return bigRed;
            }
            case "deleted": {
                if (deleted == null) {
                    deleted = new XSSFFont();
                    deleted.setStrikeout(true);
                    deleted.setColor(new XSSFColor(Color.GRAY,null));
                }
                return deleted;
            }
            case "bold": {
                if (deleted == null) {
                    deleted = new XSSFFont();
                    deleted.setBold(true);
                    deleted.setColor(new XSSFColor(Color.BLACK, null));
                }
                return deleted;
            }
            case "yellow": {
                if (deleted == null) {
                    deleted = new XSSFFont();
                    deleted.setBold(true);
                    deleted.setColor(new XSSFColor(Color.YELLOW, null));
                }
                return deleted;
            }
            default: {
                return new XSSFFont();
            }
        }

    }

    /*
        运行
     */
    @SneakyThrows
    public static String RunCommand(String[] mainCommand,String[] command) {
        StringBuilder cmd = new StringBuilder();
        for (String s : command) {
            cmd.append(" ").append(s);
        }
//        cmd += "\"";
//        System.out.println(cmd);
        String[] strings = new String[mainCommand.length + 1];
        System.arraycopy(mainCommand, 0, strings, 0, mainCommand.length);
        strings[mainCommand.length] = cmd.toString();
        StringBuilder output = new StringBuilder();
        Process exec = Runtime.getRuntime().exec(strings);
        char[] s = new char[1024];

        while (exec.isAlive()) {
            Thread.sleep(10);
        }
        if (0 == exec.exitValue()) {
            InputStreamReader inputStreamReader = new InputStreamReader(exec.getInputStream(), StandardCharsets.UTF_8);
            while (inputStreamReader.read(s) > -1) {
                output.append(s);
            }
        } else {
            InputStreamReader inputStreamReader = new InputStreamReader(exec.getErrorStream(), "GBK");
            while (inputStreamReader.read(s) > -1) {
                output.append(s);
            }
            throw new SetAException(1, "异常: " + command.getClass().getCanonicalName() + "。程序输出如下：\n" + output);
        }
        exec.destroy();
        return output.toString();
    }

    public static final List<Character> BASE_PAIR = Arrays.asList('A', 'T', 'C', 'G');

    public static String getReverseSeq(String sequence){
        StringBuilder result = new StringBuilder();
        for(int i = sequence.length()-1; i >= 0; i--){
            if(BASE_PAIR.contains(sequence.charAt(i))){
                int i1 = BASE_PAIR.indexOf(sequence.charAt(i));
                if(i1 % 2 == 0){
                    result.append(BASE_PAIR.get(i1+1));
                }else{
                    result.append(BASE_PAIR.get(i1-1));
                }
            }

        }
        return result.toString();
    }

    public static boolean femaleAndYlocus(Gender gender, String locus) {
        return Gender.female.equals(gender) && (Config.getInstance().getParam().YStrLocusOrder.contains(locus) || "Y-indel".equals(locus) || "SRY".equals(locus));
    }

    public static boolean isBiallelicLocus(Gender gender, String locus) {
        boolean marker = false;
        switch (gender) {
            case female: {
                if (Config.getInstance().getParam().BiallelicFemale.contains(locus)) {
                    marker = true;
                }
                break;
            }
            case uncertain: {
                if (Config.getInstance().getParam().BiallelicUn.contains(locus)) {
                    marker = true;
                }
                break;
            }
            case male: {
                if (Config.getInstance().getParam().BiallelicMale.contains(locus)) {
                    marker = true;
                }
                break;
            }
        }
        return marker;
    }
}
