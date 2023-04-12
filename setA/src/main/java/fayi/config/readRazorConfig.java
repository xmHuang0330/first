package fayi.config;

import fayi.tableObject.StrConfig;
import fayi.utils.FileUtils;
import fayi.utils.SetAException;
import fayi.utils.Utils;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class readRazorConfig {


    public static HashMap<String, StrConfig> readStrConfig(String config) throws SetAException {
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
            if (!flag) {
                String RAnchor = "";
                Matcher matcher = compile.matcher(values[3]);
                if (matcher.find()) {
                    RAnchor = matcher.group();
                } else {
                    throw new SetAException(1, "razor 配置错误，Ranchor未找到规则");
                }
                configs.put(values[0].split("_")[0], new StrConfig(values[0].split("_")[0], values[2], RAnchor, values[4], Integer.valueOf(values[5]), Integer.valueOf(values[6])));
            } else {
                HashMap<String, Integer> shared_Strings = new HashMap<>();

                String a = configTemp.Motif;
                String b = values[4];

                for (int i = 1; i < b.length(); i++) {
                    String subString = b.substring(0, i);
                    if (a.startsWith(subString)) {
                        shared_Strings.put(subString, shared_Strings.getOrDefault(subString, 0) + 1);
                    }
                }
                String prefix = Utils.getBestMatch(shared_Strings);
                if (!a.startsWith(prefix) || !b.startsWith(prefix)) {
                    prefix = "";
                }
                shared_Strings = new HashMap<>();
                for (int i = 1; i < b.length(); i++) {
                    String subString = b.substring(i);
                    if (a.endsWith(subString)) {
                        shared_Strings.put(subString, shared_Strings.getOrDefault(subString, 0) + 1);
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

}
