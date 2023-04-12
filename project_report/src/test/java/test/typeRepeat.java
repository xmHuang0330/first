package test;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class typeRepeat {

    //读取信息
    @Test
    public void typeR() throws IOException {
        String file = "E:\\huangxiaomiao\\files\\A历史\\代码测试\\分型一致样本\\测试.xlsx";
        String outFile = "E:\\huangxiaomiao\\files\\A历史\\代码测试\\分型一致样本\\测试.xlsx";
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("拿不到文件");
        }
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet = workbook.getSheet("分型结果");

        int end = 0;
        int lastRowNum = sheet.getLastRowNum();

        ArrayList<String> first = new ArrayList<>();
        Map<String, ArrayList<String>> sNLTWMap = new HashMap<>();
        Map<String, HashMap<String, String>> map = new HashMap<>();

        //首行
        while (end <= 141) {
            if (end == 2 || end == 5 || end == 6 || end == 7) {
                end++;
                continue;
            }
            XSSFRow row = sheet.getRow(0);
            String value = row.getCell(end).getStringCellValue();
            first.add(value);
            end++;
        }
        end = 0;

        for (int i = 1; i <= lastRowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            String sampleName = row.getCell(0).getStringCellValue();
            String lane = row.getCell(1).getStringCellValue();
            String tablet = row.getCell(3).getStringCellValue();
            String well = row.getCell(4).getStringCellValue();
            sNLTWMap.put(sampleName, new ArrayList<>());
            sNLTWMap.get(sampleName).add(0, lane);
            sNLTWMap.get(sampleName).add(1, tablet);
            sNLTWMap.get(sampleName).add(2, well);
            map.put(sampleName, new HashMap<>());

            //位点的分型
            for (int j = 8; j <= 59; j++) {
                XSSFCell cell = row.getCell(j);
                String site = sheet.getRow(0).getCell(j).getStringCellValue();
                if (cell.getCellType() == CellType.NUMERIC) {
                    map.get(sampleName).put(site, cell.getNumericCellValue() + "");
                } else if (cell.getCellType() == CellType.STRING) {
                    map.get(sampleName).put(site, cell.getStringCellValue());
                } else if (cell.getCellType() == CellType._NONE) {
                    map.get(sampleName).put(site, "");
                }
            }
        }

        Map<String, ArrayList<String>> typeMap = compare(map);

        XSSFSheet sheet1 = workbook.createSheet("分型一致样本");
        XSSFRow row = sheet1.createRow(0);
        int size = first.size() - 4;

        //首行
        while (end < size) {
            String s = first.get(end);
            row.createCell(end);
            row.getCell(end).setCellValue(s);
            end++;
        }

        //重复分型的行数
        int sum = 1;
        for (String k :
                typeMap.keySet()) {
            XSSFRow row1 = sheet1.createRow(sum++);
            row1.createCell(0).setCellValue(k);
            ArrayList<String> strings = typeMap.get(k);
            for (String v :
                    strings) {
                XSSFRow row2 = sheet1.createRow(sum++);
                row2.createCell(0).setCellValue(v);
            }
        }


        FileOutputStream fileOutputStream = new FileOutputStream(outFile);
        workbook.write(fileOutputStream);
        workbook.close();
        fis.close();
    }

    public Map<String, ArrayList<String>> compare(Map<String, HashMap<String, String>> map) {

        boolean isValue = false;
        boolean isDifferent = true;
        
        Iterator<Map.Entry<String, String>> iterator1;
        Iterator<Map.Entry<String, ArrayList<String>>> iterator2;
        Map<String, ArrayList<String>> typeRMap = new HashMap<>();
        Iterator<Map.Entry<String, HashMap<String, String>>> iterator3;
        Iterator<Map.Entry<String, HashMap<String, String>>> iterator = map.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<String, HashMap<String, String>> next = iterator.next();
            String sampleName = next.getKey();
            if (!typeRMap.containsKey(sampleName)) {
                typeRMap.put(sampleName, new ArrayList<>());
            }
            iterator2 = typeRMap.entrySet().iterator();
            while (iterator2.hasNext()) {
                Map.Entry<String, ArrayList<String>> next1 = iterator2.next();
                if (next1.getValue().contains(sampleName)) {
                    isValue = true;
                }
            }
            if (isValue) {
                isValue = false;
                continue;
            }
            iterator3 = map.entrySet().iterator();
            while (iterator3.hasNext()) {
                Map.Entry<String, HashMap<String, String>> next1 = iterator3.next();
                String sampleName1 = next1.getKey();
                if (typeRMap.containsKey(sampleName1)) {
                    continue;
                }
                iterator1 = next.getValue().entrySet().iterator();
                while (iterator1.hasNext()) {
                    Map.Entry<String, String> next2 = iterator1.next();
                    String local = next2.getKey();
                    String type = map.get(sampleName).get(local);
                    String type1 = map.get(sampleName1).get(local);
                    if (!type.equals("") && !type1.equals("") && !type.equals(type1)) {
                        isDifferent = false;
                    }
                }
                if (isDifferent) {
                    typeRMap.get(sampleName).add(sampleName1);
                }
                isDifferent = true;
            }
        }
        HashMap<String, ArrayList<String>> hashMap = new HashMap<>();
        typeRMap.forEach((k,v) -> {
            int size = v.size();
            int i = 0;
            if (size > 0) {
                if (!hashMap.containsKey(k)) {
                    hashMap.put(k, new ArrayList<>());
                }
                while (i < size) {
                    hashMap.get(k).add(v.get(i++));
                }
            }
        });
        hashMap.forEach((k,v) -> {
            System.out.println(k + " ----> " + v);
        });
        return hashMap;
    }

}
