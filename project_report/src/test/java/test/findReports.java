package test;


import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class findReports {

    static String inputFile = "E:\\huangxiaomiao\\files\\A历史\\生成codis\\广州\\20230407两板\\广州市局1万份Set-B建库-PSXFU7221001.xlsx";
    static String laneL01 = "";
    static String laneL02 = "";
    static String laneL03 = "";
    static String laneL04 = "E:\\huangxiaomiao\\files\\A历史\\生成codis\\广州\\20230407两板\\L04_reports";
    //static String laneL04 = "";

    static String pathMan = "E:\\huangxiaomiao\\files\\A历史\\生成codis\\广州\\20230407两板\\男\\";
    static String pathFe = "";
    static String sampleFile = "E:\\huangxiaomiao\\files\\A历史\\生成codis\\广州\\20230407两板\\113. V350127878样本信息表2023.03.13(深圳)-20230403修改.xlsx";

    public static void main(String[] args) throws Exception {

        HashMap<String, String> pathMap = new HashMap<>();
        pathMap.put("1", laneL01);
        pathMap.put("2", laneL02);
        pathMap.put("3", laneL03);
        pathMap.put("4", laneL04);

        FileInputStream sampleF = new FileInputStream(sampleFile);
        XSSFWorkbook workbook2 = new XSSFWorkbook(sampleF);

        FileInputStream fileInputStream = new FileInputStream(inputFile);
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        String[] allSheets = new String[]{"合格男性样本总表"/*,"合格女性样本总表"*/};
        for (int i = 0; i < allSheets.length; i++) {

            XSSFSheet sheet = workbook.getSheet(allSheets[i]);
            System.out.println(allSheets[i]);
            int lastRowNum = sheet.getLastRowNum();
            // System.out.println(lastRowNum);
            // ArrayList<String> qualifyNumbers = new ArrayList<>();
            HashMap<String, Integer> hashMap = new HashMap<>();
            ArrayList<String> miss = new ArrayList<>();

            for (int k = 1; k <= lastRowNum; k++) {
                XSSFRow row = sheet.getRow(k);
                String name;
                if (i == 0) {
                   name = row.getCell(2).getStringCellValue();
                    hashMap.put(name, k);
                }
                if (i == 1) {
                    name = row.getCell(2).getStringCellValue();
                    hashMap.put(name, k);
                }
            }
            System.out.println(hashMap.size() + "---------------");
            HashMap<String, HashMap<String, Integer>> lanesNumbers = new HashMap<>();

            String[] sheetNames = new String[]{"lane1", "lane2", "lane3", "lane4"};
            String numberSample = null;
            for (String sheetName :
                    sheetNames) {
                lanesNumbers.put(sheetName, new HashMap<>());
                XSSFSheet sheet2 = workbook2.getSheet(sheetName);
                int lastRowNum2 = sheet2.getLastRowNum();

                //System.out.println(sheetName + "：最后一行：" + lastRowNum2);
                int sum = 0;
                for (int j = 1; j <= lastRowNum2; j++) {
                    XSSFRow row = sheet2.getRow(j);
                    CellType cellType = row.getCell(2).getCellType();
                    if (cellType.equals(CellType.NUMERIC)) {
                        numberSample = row.getCell(2).getNumericCellValue() + "";
                    }
                    if (cellType.equals(CellType._NONE)) {
                        continue;
                    }
                    if (cellType.equals(CellType.STRING)) {
                        numberSample = row.getCell(2).getStringCellValue();
                    }
                    int indexSample = (int) row.getCell(0).getNumericCellValue();

                    for (String n :
                            hashMap.keySet()) {
                        if (numberSample.equals(n)) {
                            lanesNumbers.get(sheetName).put(numberSample, indexSample);
                            sum++;
                        }
                    }

                }
                sum--;
                System.out.println(sheetName + "：" +  sum + "-----------");
            }
            System.out.println(lanesNumbers.size());
            for (String laneNum :
                    pathMap.keySet()) {
                int count = 0;
                for (String laneN :
                        lanesNumbers.keySet()) {
                    if (laneN.contains(laneNum)) {
                        count++;
                        if (pathMap.get(laneNum).equals("") || pathMap.get(laneNum) == null) {
                            continue;
                        }
                        File file = new File(pathMap.get(laneNum));
                        File[] files = file.listFiles();
                        for (File f :
                                files) {
                            for (String sampleN :
                                    lanesNumbers.get(laneN).keySet()) {
                                String name = "_" + lanesNumbers.get(laneN).get(sampleN) + "_" + sampleN + "_";
                                if (f.getName().contains(name)) {
                                    System.out.println(laneN + "：样本编号--->>" + sampleN + "；文件名----->" + f.getName());
                                    miss.add(sampleN);
                                    if (i == 0) {
                                        f.renameTo(new File(pathMan + f.getName()));
                                    }
                                    if (i == 1) {
                                        f.renameTo(new File(pathFe + f.getName()));
                                    }
                                    count++;
                                }
                            }
                        }
                    }
                }
                count--;
                //System.out.println(laneNum + "：的总数 " + count);
            }
            System.out.println(miss.size());
            for (String s :
                    hashMap.keySet()) {
                if (!miss.contains(s)) {
                    for (String pathReports :
                            pathMap.keySet()) {
                        if (pathMap.get(pathReports).equals("") || pathMap.get(pathReports) == null) {
                            continue;
                        }
                        File file = new File(pathMap.get(pathReports));
                        File[] files = file.listFiles();
                        for (File f :
                                files) {
                            if (f.getName().contains(s)) {
                                if (i == 0) {
                                    boolean e = f.renameTo(new File(pathMan + f.getName()));
                                    System.out.println("该样本 " + s + " 存在，是否移动成功：" + e);
                                }
                                if (i == 1) {
                                    boolean e = f.renameTo(new File(pathFe + f.getName()));
                                    System.out.println("该样本 " + s + " 存在，是否移动成功：" + e);
                                }

                            }
                        }
                    }
                }
            }
        }
        fileInputStream.close();
        sampleF.close();
    }
}
