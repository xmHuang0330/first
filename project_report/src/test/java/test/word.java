package test;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.*;

public class word {
    /**
     * 1、先获取各个报告中 常和Y 各自位点对应的分型和序列
     * 2、各报告分别和模板对应比较分型和序列的一致性，取位点中分型相同总数少的报告作为位点排序的标准
     */

    @Test
    public void word() throws Exception {
        String scene = "E:\\huangxiaomiao\\files\\A历史\\其他工作\\模板\\V300113172_L04_986_肇庆竹竿案-2_setB.xlsx";
        String suspect1 = "E:\\huangxiaomiao\\files\\A历史\\其他工作\\模板\\V350095586_L01_631_肇庆市局-黄喜鲜2413_setB.xlsx";
        String suspect2 = "E:\\huangxiaomiao\\files\\A历史\\其他工作\\模板\\V350095586_L01_640_肇庆市局-黄道平2437_setB.xlsx";

        Map<String,HashMap<String, HashMap<String, HashMap<String, String>>>> sceneMap = new HashMap<>();
        String[] files = new String[]{scene, suspect1, suspect2};
        int filesL = files.length;
        for (int k = 0; k < filesL; k++) {
            String file = files[k];
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            //常和Y，位点，对应的分型序列
            sceneMap.put(file, new HashMap<>());
            String[] sheets = new String[]{"Autosomal STRs", "Y STRs"};
            int length = sheets.length;
            int begin = 15;
            int end;
            for (int i = 0; i < length; i++) {
                String sheetName = sheets[i];
                if (sheetName.contains("Autosomal")) {
                    end = 66;
                } else {
                    end = 96;
                }
                sceneMap.get(file).put(sheetName, new HashMap<>());
                XSSFSheet sheet = workbook.getSheet(sheetName);
                for (int j = begin; j <= end; j++) {
                    XSSFRow row = sheet.getRow(j);
                    String local = row.getCell(0).getStringCellValue();
                    sceneMap.get(file).get(sheetName).put(local, new HashMap<>());
                    CellType cellType = row.getCell(1).getCellType();
                    CellType cellType1 = row.getCell(3).getCellType();
                    String reads = null;
                    if (cellType1.equals(CellType._NONE)) {
                        reads = "";
                    } else if (cellType1.equals(CellType.STRING)) {
                        reads = row.getCell(3).getStringCellValue();
                    }
                    if (cellType.equals(CellType.NUMERIC)) {
                        double typeValue = row.getCell(1).getNumericCellValue();
                        BigDecimal value = new BigDecimal(typeValue);
                        BigDecimal noZeros = value.stripTrailingZeros();
                        String type = noZeros.toPlainString();
                        sceneMap.get(file).get(sheetName).get(local).put(type, reads);
                    } else if (cellType.equals(CellType.STRING)) {
                        String type = row.getCell(1).getStringCellValue();
                        sceneMap.get(file).get(sheetName).get(local).put(type, reads);
                    } else if (cellType.equals(CellType._NONE)) {
                        sceneMap.get(file).get(sheetName).get(local).put(local, "");
                    }
                }
            }
        }
        Map<String, HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>>> map = compare(sceneMap, scene);
        Map<String, HashMap<String, HashMap<String, HashMap<String,HashMap<String,String>>>>> map1 = fi(map);
        report(sceneMap,map1);

    }

    public void delete(Map<String,HashMap<String, HashMap<String, HashMap<String, String>>>> sceneMap,Map<String, HashMap<String, HashMap<String, HashMap<String,String>>>> map1) {

    }

    public void report(Map<String,HashMap<String, HashMap<String, HashMap<String, String>>>> sceneMap,Map<String, HashMap<String, HashMap<String, HashMap<String,HashMap<String,String>>>>> map1) {
        String file = "结果.xlsx";
        try {
            FileInputStream fis = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheet("报告");
            int lastRowNum = sheet.getLastRowNum();
            int normalBegin = 10;
            int yBegin = 67;
            Iterator<Map.Entry<String, HashMap<String, HashMap<String, HashMap<String, String>>>>> iterator = sceneMap.entrySet().iterator();
            Iterator<Map.Entry<String, HashMap<String, HashMap<String, HashMap<String,HashMap<String,String>>>>>> iterator1 = map1.entrySet().iterator();


        } catch (Exception e) {
            System.out.println("拿不到文件");
        }
    }

    public Map compare(Map sceneMap,String sceneFile) {
        Iterator<Map.Entry<String, HashMap<String, HashMap<String, HashMap<String, String>>>>> iterator = sceneMap.entrySet().iterator();
        Iterator<Map.Entry<String, HashMap<String, HashMap<String, HashMap<String, String>>>>> iterator1 = sceneMap.entrySet().iterator();
        Iterator<Map.Entry<String, HashMap<String, HashMap<String, String>>>> iterator2;
        Iterator<Map.Entry<String, HashMap<String, String>>> iterator3;
        Iterator<Map.Entry<String, String>> iterator4;
        Iterator<Map.Entry<String, String>> iterator5;
        Map<String, HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>>> map = new HashMap<>();
        while (iterator.hasNext()) {
            Map.Entry<String, HashMap<String, HashMap<String, HashMap<String, String>>>> next = iterator.next();
            String fileName = next.getKey();
            if (!fileName.equals(sceneFile)) {
                continue;
            }
            while (iterator1.hasNext()) {
                Map.Entry<String, HashMap<String, HashMap<String, HashMap<String, String>>>> next1 = iterator1.next();
                String fileName1 = next1.getKey();
                if (fileName1.equals(fileName)) {
                    continue;
                }
                map.put(fileName1, new HashMap<>());
                iterator2 = next1.getValue().entrySet().iterator();
                int normal = 0;
                int y = 0;
                while (iterator2.hasNext()) {
                    Map.Entry<String, HashMap<String, HashMap<String, String>>> next2 = iterator2.next();
                    String sheetName = next2.getKey();
                    if (!map.get(fileName1).containsKey(sheetName)) {
                        map.get(fileName1).put(sheetName,new HashMap<>());
                    }
                    if (!map.get(fileName1).get(sheetName).containsKey("same")) {
                        map.get(fileName1).get(sheetName).put("same", new HashMap<>());
                    }
                    if (!map.get(fileName1).get(sheetName).containsKey("different")) {
                        map.get(fileName1).get(sheetName).put("different", new HashMap<>());
                    }
                    iterator3 = next2.getValue().entrySet().iterator();
                    while (iterator3.hasNext()) {
                        Map.Entry<String, HashMap<String, String>> next3 = iterator3.next();
                        String local = next3.getKey();
                        iterator4 = next3.getValue().entrySet().iterator();
                        while (iterator4.hasNext()) {
                            Map.Entry<String, String> next4 = iterator4.next();
                            String type = next4.getKey();
                            String reads = next4.getValue();
                            //System.out.println(reads + "[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[[");
                            iterator5 = next.getValue().get(sheetName).get(local).entrySet().iterator();
                            while (iterator5.hasNext()) {
                                Map.Entry<String, String> next5 = iterator5.next();
                                String type1 = next5.getKey();
                                String value = next5.getValue();
                                    //完全不一样
                                    //不完全一样
                                if (!type.equals("") && !type1.equals("") && !type.equals(type1)) {
                                    //完全不一样
                                    //如果都没有“，”而且不相等
                                    if (!type.contains(",") && !type1.contains(",")) {//现场只有一个，人员只有一个  //完全不一样
                                        if (sheetName.equals("Autosomal STRs")) {
                                            normal++;
                                        }
                                        if (sheetName.equals("Y STRs")) {
                                            y++;
                                        }
                                        map.get(fileName1).get(sheetName).get("different").put(local,new HashMap<>());
                                        map.get(fileName1).get(sheetName).get("different").get(local).put(type,reads);
                                        System.out.println("完全不一样：" + local + " ：" + type + " ----> " + type1);
                                    } else if (!type.contains(",") && type1.contains(",") && !type1.contains(type)) {//现场不止一个，人员只有一个  //完全不一样
                                        if (sheetName.equals("Autosomal STRs")) {
                                            normal++;
                                        }
                                        if (sheetName.equals("Y STRs")) {
                                            y++;
                                        }
                                        map.get(fileName1).get(sheetName).get("different").put(local,new HashMap<>());
                                        map.get(fileName1).get(sheetName).get("different").get(local).put(type,reads);
                                        System.out.println("完全不一样：" + local + " ：" + type + " ----> " + type1);
                                    } else if (!type1.contains(",") && type.contains(",") && !type.contains(type1)) {//现场只有一个，人员不止一个 //完全不一样
                                        if (sheetName.equals("Autosomal STRs")) {
                                            normal++;
                                        }
                                        if (sheetName.equals("Y STRs")) {
                                            y++;
                                        }
                                        map.get(fileName1).get(sheetName).get("different").put(local,new HashMap<>());
                                        map.get(fileName1).get(sheetName).get("different").get(local).put(type,reads);
                                        System.out.println("完全不一样：" + local + " ：" + type + " ----> " + type1);
                                    } else if (type1.contains(",") && type.contains(",")) {//现场不止一个，人员不止一个 //完全不一样
                                        String[] s = type.split(",");
                                        String[] s1 = type1.split(",");
                                        int length = s.length;
                                        int length1 = s1.length;
                                        boolean have = false;
                                        for (int i = 0; i < length; i++) {
                                            for (int j = 0; j < length1; j++) {
                                                if (s1[j].equals(s[i])) {
                                                    have = true;
                                                }
                                            }
                                        }
                                        if (!have) {
                                            if (sheetName.equals("Autosomal STRs")) {
                                                normal++;
                                            }
                                            if (sheetName.equals("Y STRs")) {
                                                y++;
                                            }
                                            map.get(fileName1).get(sheetName).get("different").put(local,new HashMap<>());
                                            map.get(fileName1).get(sheetName).get("different").get(local).put(type,reads);
                                            System.out.println("完全不一样：" + local + " ：" + type + " ----> " + type1);
                                        }
                                        if (have) {
                                            if (sheetName.equals("Autosomal STRs")) {
                                                normal++;
                                            }
                                            if (sheetName.equals("Y STRs")) {
                                                y++;
                                            }
                                            map.get(fileName1).get(sheetName).get("same").put(local,new HashMap<>());
                                            map.get(fileName1).get(sheetName).get("same").get(local).put(type,reads);
                                            System.out.println("不完全一样：" + local + " ：" + type + " ----> " + type1);
                                        }
                                    } else {
                                        if (sheetName.equals("Autosomal STRs")) {
                                            normal++;
                                        }
                                        if (sheetName.equals("Y STRs")) {
                                            y++;
                                        }
                                        map.get(fileName1).get(sheetName).get("same").put(local,new HashMap<>());
                                        map.get(fileName1).get(sheetName).get("same").get(local).put(type,reads);
                                        System.out.println("不完全一样：" + local + " ：" + type + " ----> " + type1);
                                    }
                                }
                            }
                        }
                    }
                }
                System.out.println("常：" + normal + "，y：" + y + "-----------------------------------------------");
            }
        }
        return map;
    }

    public Map<String, HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>>> fi(Map<String, HashMap<String,HashMap<String,HashMap<String,HashMap<String,String>>>>> map) {
        //不完全一样位点中，多的作为位点顺序
        Iterator<Map.Entry<String, HashMap<String, HashMap<String, HashMap<String,HashMap<String,String>>>>>> iterator = map.entrySet().iterator();
        Iterator<Map.Entry<String, HashMap<String, HashMap<String, HashMap<String,HashMap<String,String>>>>>> iterator5 = map.entrySet().iterator();
        Iterator<Map.Entry<String, HashMap<String, HashMap<String, HashMap<String,HashMap<String,String>>>>>> iterator6 = map.entrySet().iterator();
        Iterator<Map.Entry<String, HashMap<String, HashMap<String,HashMap<String,String>>>>> iterator1;
        Iterator<Map.Entry<String, HashMap<String,HashMap<String,String>>>> iterator2;
        Map<String, Integer> map1 = new HashMap<>();
        HashMap<String, HashMap<String, HashMap<String,HashMap<String,HashMap<String,String>>>>> map2 = new HashMap<>();//sheetName，same/different，local
        //获取最大的那个
        while (iterator.hasNext()) {
            Map.Entry<String, HashMap<String, HashMap<String, HashMap<String,HashMap<String,String>>>>> next = iterator.next();
            String fileName = next.getKey();
            iterator1 = next.getValue().entrySet().iterator();
            int sum = 0;
            while (iterator1.hasNext()) {
                Map.Entry<String, HashMap<String, HashMap<String,HashMap<String,String>>>> next1 = iterator1.next();
                iterator2 = next1.getValue().entrySet().iterator();
                while (iterator2.hasNext()) {
                    Map.Entry<String, HashMap<String,HashMap<String,String>>> next2 = iterator2.next();
                    int size = next2.getValue().size();
                    sum += size;
                }
            }
            map1.put(fileName, sum);
        }
        String fileName = null;//最多的
        Integer max = map1.values().stream().max(Integer::compareTo).orElse(-1);
        for (String s :
                map1.keySet()) {
            if (map1.get(s) == max) {
                fileName = s;
            }
        }
        Iterator<Map.Entry<String, HashMap<String, HashMap<String, HashMap<String, String>>>>> iterator3;
        Iterator<Map.Entry<String, HashMap<String, HashMap<String, String>>>> iterator4;
        Iterator<Map.Entry<String, HashMap<String, String>>> iterator7;
        while (iterator5.hasNext()) {
            Map.Entry<String, HashMap<String, HashMap<String, HashMap<String,HashMap<String,String>>>>> next = iterator5.next();
            String fileN = next.getKey();
            if (fileN.equals(fileName)) {
                continue;//留下少的要合并的local
            }
            while (iterator6.hasNext()) {
                Map.Entry<String, HashMap<String, HashMap<String, HashMap<String,HashMap<String,String>>>>> next1 = iterator6.next();
                String fileN1 = next1.getKey();
                if (fileN1.equals(fileN)) {
                    continue;//保证不一样
                }
                map2.put(fileN1, new HashMap<>());
                iterator3 = next1.getValue().entrySet().iterator();
                while (iterator3.hasNext()) {
                    Map.Entry<String, HashMap<String, HashMap<String, HashMap<String, String>>>> next2 = iterator3.next();
                    String sheetName = next2.getKey();
                    iterator4 = next2.getValue().entrySet().iterator();
                    map2.get(fileN1).put(sheetName, new HashMap<>());
                    while (iterator4.hasNext()) {
                        Map.Entry<String, HashMap<String, HashMap<String, String>>> next3 = iterator4.next();
                        String sd = next3.getKey();
                        HashMap<String, HashMap<String, String>> same = map.get(fileN1).get(sheetName).get("same");//多的
                        HashMap<String, HashMap<String, String>> different = map.get(fileN1).get(sheetName).get("different");//多的
                        iterator7 = next3.getValue().entrySet().iterator();
                        map2.get(fileN1).get(sheetName).put(sd, new HashMap<>());
                        while (iterator7.hasNext()) {
                            Map.Entry<String, HashMap<String, String>> next4 = iterator7.next();
                            String local = next4.getKey();
                            map.get(fileN).get(sheetName).get(sd).keySet().forEach((String l) -> {
                                if (!same.containsKey(l) && !different.containsKey(l)) {
                                    System.out.println(l + "------------------------------------------------k");
                                    map2.get(fileN1).get(sheetName).get(sd).put(l, new HashMap<>());
                                    map.get(fileN).get(sheetName).get(sd).get(l).forEach((String t,String r) ->{
                                        map2.get(fileN1).get(sheetName).get(sd).get(l).put(t,r);
                                    });
                                    /*next4.getValue().forEach((String t,String r) -> {
                                        map2.get(fileN1).get(sheetName).get(sd).get(l).put(t,r);
                                    });*/
                                }
                            });
                        }
                    }
                }
            }
        }
        map.remove(fileName);
        Iterator<Map.Entry<String, HashMap<String, HashMap<String,HashMap<String,HashMap<String,String>>>>>> iterator8 = map2.entrySet().iterator();
        Iterator<Map.Entry<String, HashMap<String,HashMap<String,HashMap<String,String>>>>> iterator9;
        Iterator<Map.Entry<String, HashMap<String, HashMap<String,String>>>> iterator10;
        for (String s :
                map.keySet()) {
            while (iterator8.hasNext()) {
                Map.Entry<String, HashMap<String, HashMap<String,HashMap<String,HashMap<String,String>>>>> next = iterator8.next();//sheetName、same/d、local
                String sheetName = next.getKey();
                iterator9 = next.getValue().entrySet().iterator();
                while (iterator9.hasNext()) {
                    Map.Entry<String, HashMap<String,HashMap<String,HashMap<String,String>>>> next1 = iterator9.next();
                    String sd = next1.getKey();
                    iterator10 = next1.getValue().entrySet().iterator();
                    /*next1.getValue().forEach((String l,String r) -> {
                        map.get(s).get(sheetName).get(type).put(l,r);
                    });*/
                }
            }
        }
        return map;
    }
}
