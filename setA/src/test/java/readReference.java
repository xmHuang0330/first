import fayi.WriteExcel.SingleExcel;
import fayi.config.Config;
import fayi.seqParser.CorePicker;
import fayi.tableObject.SampleInfo;
import fayi.tableObject.SeqInfo;
import fayi.tableObject.StrConfig;
import fayi.tableObject.StrInfo;
import fayi.utils.FileUtils;
import fayi.utils.SetAException;
import fayi.utils.Utils;
import fayi.xml.Objects.Data;
import fayi.xml.Objects.Sample;
import fayi.xml.Xml;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class readReference {
    public final List<Character> BASE_PAIR = Arrays.asList('A', 'T', 'C', 'G');

    public HashMap<String, StrInfo> readRef(String refFile) {

        HashMap<String, StrInfo> refStrInfo = new HashMap<>();
        FileUtils fileUtils = new FileUtils(refFile);
        String s;
        while ((s = fileUtils.readLine()) != null) {
            String locus = s.split(":")[0].substring(1);

            if (locus.endsWith("a") || locus.endsWith("b")) {
                locus = locus.substring(0, locus.length() - 1) + "a/b";
            }

            s = fileUtils.readLine();
            StrConfig strConfig = strConfigs.get(locus);

            if (s.contains(strConfig.Lflank) && s.contains(strConfig.Rflank)) {
                s = s.substring(s.indexOf(strConfig.Lflank) + strConfig.Lflank.length(), s.indexOf(strConfig.Rflank));
            } else {
                String Lflank = Utils.getReverseSeq(strConfig.Lflank);
                String Rflank = Utils.getReverseSeq(strConfig.Rflank);
                if (s.contains(Lflank) && s.contains(Rflank)) {
                    s = getReverseSeq(s.substring(s.indexOf(Rflank) + Rflank.length(), s.indexOf(Lflank)));
                } else {
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

    public String getReverseSeq(String sequence) {
        StringBuilder result = new StringBuilder();
        for (int i = sequence.length() - 1; i >= 0; i--) {
            if (BASE_PAIR.contains(sequence.charAt(i))) {
                int i1 = BASE_PAIR.indexOf(sequence.charAt(i));
                if (i1 % 2 == 0) {
                    result.append(BASE_PAIR.get(i1 + 1));
                } else {
                    result.append(BASE_PAIR.get(i1 - 1));
                }
            }

        }
        return result.toString();
    }

    public HashMap<String, StrConfig> readStrConfig(String config) throws SetAException {
        FileUtils fileUtils = new FileUtils(config);
        String s;
        HashMap<String, StrConfig> configs = new HashMap<>();

        String pattern = "([ATCG])+";
        Pattern compile = Pattern.compile(pattern);

        while ((s = fileUtils.readLine()) != null) {
            if (s.startsWith("#")) {
                continue;
            }
            String[] values = s.trim().split("\t");
            boolean flag = false;
            StrConfig configTemp = new StrConfig();
            for (StrConfig strConfig : configs.values()) {
                if (values[0].split("_")[0].equals(strConfig.Marker)) {
                    flag = true;
                    configTemp = strConfig;
                }
            }
            if(!flag) {
                String RAnchor = "";
                Matcher matcher = compile.matcher(values[3]);
                if (matcher.find()) {
                    RAnchor = matcher.group();
                } else {
                    throw new SetAException(1, "razor 配置错误，Ranchor未找到规则");
                }
                configs.put(values[0].split("_")[0], new StrConfig(values[0].split("_")[0], values[2], RAnchor, values[4], Integer.valueOf(values[5]), Integer.valueOf(values[6])));
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
                if (!a.endsWith(suffix) || !b.endsWith(prefix)) {
                    suffix = "";
                }
                configTemp.Motif = prefix + "1" + suffix;
            }
        }
        fileUtils.finishRead();
        return configs;
    }

    static HashMap<String, StrConfig> strConfigs;
//
//    public static void main(String[] args) throws SetAException {
//        readReference readReference = new readReference();
////        new ReadReference().readRef(Config.getInstance().getProjectPath() + "/resource/setB.fasta");
//        strConfigs = readReference.readStrConfig(Config.getInstance().getProjectPath() + "/resource/razer/test_config");
////        HashMap<String, StrInfo> stringStrInfoHashMap = readRef("/Users/kaidan/Downloads/setB.fasta");
//
//        Data data = new Xml().xmlToData("/Volumes/DATA/setB_test/test_result.xml");
//        SampleInfo sample2800m = null;
//        for (Sample sample : data.samples) {
//            if (sample.getBasicInfo().type.toLowerCase(Locale.ROOT).contains("阳性")) {
//                sample2800m = new SingleExcel().sampleToSampleInfo(sample);
//                break;
//            }
//        }
//        CorePicker corePicker = new CorePicker();
//
//        HashMap<String, ArrayList<StrInfo>> stringStrInfoHashMap = new HashMap<>();
//        assert sample2800m != null;
//        for (String locus : sample2800m.getStrLocusInfo().keySet()) {
//            stringStrInfoHashMap.put(locus, new ArrayList<>());
//            for (SeqInfo seqInfo : sample2800m.getStrLocusInfo().get(locus).getAllele()) {
//                StrInfo strInfo = (StrInfo) seqInfo;
//                stringStrInfoHashMap.get(locus).add(strInfo);
//            }
//        }
//
//        readReference.trimSequence(stringStrInfoHashMap);
//
//        for (String locus : stringStrInfoHashMap.keySet()) {
//            for (StrInfo strInfo : stringStrInfoHashMap.get(locus)) {
//
//                corePicker.calCoreSTR(strInfo);
//                strInfo.formatRepeatSequence(false);
//                int max = 0;
//                int index = 0;
//                for (int i : strInfo.getCoreSeqCount().keySet()) {
//                    if (strInfo.getCoreSeqCount().get(i) > max) {
//                        max = strInfo.getCoreSeqCount().get(i);
//                        index = i;
//                    }
//                }
//                System.out.println(locus + "\t" + strInfo.getAlleleName() + "\t" + strInfo.getRepeatSequence() + "\t" + strInfo.getNoneCoreseq().get(index));
//            }
//
//        }
//    }

    public void trimSequence(HashMap<String, ArrayList<StrInfo>> stringStrInfoHashMap) throws SetAException {
        String trimConfigFile = Config.getInstance().getProjectPath() + "/resource/strRef/trimConfig";
        FileUtils fileUtils = new FileUtils(trimConfigFile);
        String line;
        HashMap<String, ArrayList<Integer[]>> trimConfig = new HashMap<>();

        Pattern compile = Pattern.compile("([+-]{1})([\\d]+)");

        while ((line = fileUtils.readLine()) != null) {
            line = line.trim();
            String[] split = line.split("\t");
            if (!trimConfig.containsKey(split[0])) {
                trimConfig.put(split[0], new ArrayList<>());
            }
            if (split.length < 2) {
                trimConfig.get(split[0]).add(new Integer[]{0, 0});
                continue;
            }
            Matcher matcher = compile.matcher(split[1]);
            if (matcher.find()) {
                if ("+".equals(matcher.group(1))) {
                    trimConfig.get(split[0]).add(new Integer[]{0, Integer.parseInt(matcher.group(2))});
                } else if ("-".equals(matcher.group(1))) {
                    trimConfig.get(split[0]).add(new Integer[]{Integer.parseInt(matcher.group(2)), 0});
                } else {
                    throw new SetAException(1, "序列清理规则不对劲！" + line);
                }
            } else {
                trimConfig.get(split[0]).add(new Integer[]{0, 0});
                throw new SetAException(1, "序列清理规则不对劲！" + split[1]);
            }
        }

        for (String locus : stringStrInfoHashMap.keySet()) {
            for (StrInfo strInfo : stringStrInfoHashMap.get(locus)) {
                String repeatSequence = strInfo.getRepeatSequence();
                for (Integer[] range : trimConfig.get(locus)) {
                    repeatSequence = repeatSequence.substring(range[0], repeatSequence.length() - range[1]);
                }
                strInfo.setTrimmedSeq(strInfo.getRepeatSequence());
                strInfo.setRepeatSequence(repeatSequence);
            }
        }
    }
}
