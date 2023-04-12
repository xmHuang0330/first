package fayi.seqParser;

import fayi.config.Config;
import fayi.tableObject.StrConfig;
import fayi.tableObject.StrInfo;
import fayi.utils.FileUtils;
import fayi.utils.Utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ReadReference {

    public final List<Character> BASE_PAIR = Arrays.asList('A', 'T', 'C', 'G');

    public HashMap<String, StrInfo> readRef(String refFile) {

        HashMap<String, StrConfig> strConfigs = readStrConfig(Config.getInstance().getProjectPath() + "/resource/razer/str_snp_config");

        HashMap<String, StrInfo> refStrInfo = new HashMap<>();
        FileUtils fileUtils = new FileUtils(refFile);
        String s;
        while((s = fileUtils.readLine()) != null){
            String locus = s.split(",")[1];
            s = fileUtils.readLine();
            StrConfig strConfig = strConfigs.get(locus);
            if(s.contains(strConfig.Lflank) && s.contains(strConfig.Rflank) ){
                s = s.substring(s.indexOf(strConfig.Lflank) + strConfig.Lflank.length(),s.indexOf(strConfig.Rflank));
            }else{
                String Lflank = getReverseSeq(strConfig.Lflank);
                String Rflank = getReverseSeq(strConfig.Rflank);
                if(s.contains(Lflank) && s.contains(Rflank)){
                    s = getReverseSeq(s.substring(s.indexOf(Rflank)+Rflank.length(),s.lastIndexOf(Lflank)));
                }else{
                    System.err.println(locus);
                    continue;
                }
            }
            refStrInfo.put(locus, new StrInfo(locus, "", false, 0, s, 0, 0));
//            corePicker.calCoreSTR(refStrInfo.get(locus));
//            System.out.print(locus + "\t");
//            for (int i = 0; i < refStrInfo.get(locus).getNoneCoreseq().size(); i++) {
//                Integer integer = refStrInfo.get(locus).getCoreSeqCount().get(i);
//                System.out.print(refStrInfo.get(locus).getNoneCoreseq().get(i) + (integer == null?" ":"[" + integer + "] "));
//            }
//            System.out.println();

        }
        fileUtils.finishRead();

        return refStrInfo;
    }

    public String getReverseSeq(String sequence){
        StringBuilder result = new StringBuilder();
        for(int i = sequence.length()-1;i >= 0;i--){
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

    public HashMap<String,StrConfig> readStrConfig(String config){
        FileUtils fileUtils = new FileUtils(config);
        String s;
        HashMap<String, StrConfig> configs = new HashMap<>();
        while((s = fileUtils.readLine())!=null){
            String[] values = s.trim().split("\t");
            boolean flag = false;
            StrConfig configTemp = new StrConfig();
            for(StrConfig strConfig:configs.values()){
                if(values[0].split("_")[0].equals(strConfig.Marker)){
                    flag = true;
                    configTemp = strConfig;
                }
            }
            if(!flag){
                configs.put(values[0].split("_")[0],new StrConfig(values[0].split("_")[0],values[2],values[3],values[4],Integer.valueOf(values[5]),Integer.valueOf(values[6])));
            }else {
                HashMap<String, Integer> shared_Strings = new HashMap<>();

                String a = configTemp.Motif;
                String b = values[4];

                for (int i = 1; i < b.length(); i++) {
                    String subString = b.substring(0,i);
                    if(a.startsWith(subString)){
                        shared_Strings.put(subString,shared_Strings.getOrDefault(subString,0)+1);
                    }
                }
                String prefix = Utils.getBestMatch(shared_Strings);
                if(!a.startsWith(prefix) || !b.startsWith(prefix)){
                    prefix = "";
                }
                shared_Strings = new HashMap<>();
                for (int i = 1; i < b.length(); i++) {
                    String subString = b.substring(i);
                    if(a.endsWith(subString)){
                        shared_Strings.put(subString,shared_Strings.getOrDefault(subString,0)+1);
                    }
                }
                String suffix = Utils.getBestMatch(shared_Strings);
                if(!a.endsWith(suffix) || !b.endsWith(prefix)){
                    suffix = "";
                }
                configTemp.Motif = prefix + "1" + suffix;
            }
        }
        fileUtils.finishRead();
        return configs;
    }

    public static void main(String[] args) {
//        new ReadReference().readRef(Config.getInstance().getProjectPath() + "/resource/setB.fasta");
        new ReadReference().readRef("/Users/kaidan/Downloads/setB.fasta");

    }

}
