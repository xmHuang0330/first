package test;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.*;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Slf4j
public class writePro {


    static XSSFColor blueColor = new XSSFColor(new Color(0, 0, 255), null);
    static XSSFColor redColor = new XSSFColor(new Color(255, 0, 0), null);
    static XSSFColor greenColor = new XSSFColor(new Color(0,153,0), null);
    static String file = "E:\\huangxiaomiao\\files\\A历史\\项目填写\\2023年\\4月\\0410_V350106430\\填写\\result\\杭州\\杭州项目进度表.xlsx";
    static String outputFile = "E:\\huangxiaomiao\\files\\A历史\\项目填写\\2023年\\4月\\0410_V350106430\\填写\\result\\杭州\\杭州项目表.xlsx";

    //单表、多表样本号去重及批次数量统计处理
    @Test
    public void rep() throws IOException {

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println("拿不到文件");
        }
        XSSFWorkbook workbook = new XSSFWorkbook(fis);

        XSSFCellStyle blue = workbook.createCellStyle();
        XSSFFont fontBlue = workbook.createFont();
        fontBlue.setColor(blueColor);
        blue.setFont(fontBlue);

        XSSFCellStyle green = workbook.createCellStyle();
        XSSFFont fontGreen = workbook.createFont();
        fontGreen.setColor(greenColor);
        green.setFont(fontGreen);

        XSSFCellStyle red = workbook.createCellStyle();
        XSSFFont fontRed = workbook.createFont();
        fontRed.setColor(redColor);
        red.setFont(fontRed);

        HashMap<String, String> map = new HashMap<>();

        String[] strings = {"合格男性样本总表", "合格女性样本总表", "建库失败样本总表"};
        for (int i = 0; i < strings.length; i++) {
            Map<String, ArrayList<String>> batchMap = new HashMap<>();
            XSSFSheet sheet = workbook.getSheet(strings[i]);
            int lastRowNum = sheet.getLastRowNum();
            int sampleSelf = 0;
            int manRepeat = 0;
            int feRepeat = 0;
            int batchC = 5;
            if (strings[i].contains("女性")) {
                batchC = 4;
            }
            //System.out.println(lastRowNum);
            for (int j = 1; j <= lastRowNum ; j++) {
                XSSFRow row = sheet.getRow(j);
                String cellValue = row.getCell(2).getStringCellValue();
                String batch = row.getCell(batchC).getStringCellValue();
                if (!batchMap.containsKey(batch)) {
                    batchMap.put(batch, new ArrayList<>());
                }
                    batchMap.get(batch).add(cellValue);
                if (!map.containsKey(cellValue)) {
                    map.put(cellValue, strings[i]);
                    continue;
                }
                if (map.containsKey(cellValue)) {
                    String s = map.get(cellValue);  //已存在样本的表名
                    if (s.equals(strings[i])) {//单表重号，标绿色
                        row.getCell(2).setCellStyle(green);
                        sampleSelf++;
                    } else if (s.contains("男性")){//该样本与男性中的一个样本重号，标蓝色
                        row.getCell(2).setCellStyle(blue);
                        manRepeat++;
                    } else if (s.contains("女性")) {//该样本与女性中的一个样本重号，标红色
                        row.getCell(2).setCellStyle(red);
                        feRepeat++;
                    }
                }
            }
            log.info(strings[i] + " 中  单表  重复数有：" + sampleSelf + " 个，已标绿，（若重复数不为0，则批次号数量无效，下同）");
            if (!strings[i].equals("合格男性样本总表")) {
                log.info(strings[i] + " 中  与 合格男性样本  中有：" + manRepeat + " 个重复，已标蓝");
                if (!strings[i].contains("女性")) {
                    log.info(strings[i] + " 中  与 合格女性样本  中有：" + feRepeat + " 个重复，已标红");
                }
            }
            batchMap.keySet().stream().forEach((String batch) -> {
                log.info("批次号：" + batch + "  有  " + batchMap.get(batch).stream().count() + " 个");
            });
            log.info("");

        }
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();
    }


    @Test
    public void repeat() throws Exception {
        String file = "E:\\huangxiaomiao\\files\\historyMission\\日常检查\\221028\\云南\\云南市局-BSXMWS225002.xlsx";
        String outputFile = "E:\\huangxiaomiao\\files\\historyMission\\日常检查\\221027\\珠海\\珠海项目表.xlsx";
        FileInputStream fis = new FileInputStream(file);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFCellStyle blue = workbook.createCellStyle();
        XSSFFont fontBlue = workbook.createFont();
        fontBlue.setColor(blueColor);
        blue.setFont(fontBlue);
        HashMap<String, String> map = new HashMap<>();
        String[] strings = {"合格男性样本总表", "合格女性样本总表", "建库失败样本总表"};
        int count = 0;
        for (int i = 0; i < strings.length; i++) {
            XSSFSheet sheet = workbook.getSheet(strings[i]);
            int lastRowNum = sheet.getLastRowNum();
            System.out.println(lastRowNum + "============");
            for (int j = 1; j <= lastRowNum; j++) {
                XSSFRow row = sheet.getRow(j);
                String value = null;
                if (strings[i].contains("女性")) {
                    value = row.getCell(4).getStringCellValue();
                } else {
                    value = row.getCell(5).getStringCellValue();
                }
                //if (value.equals("22727E") || value.equals("22727E")) {
                    String cellValue = row.getCell(2).getStringCellValue();
                    if (!map.containsKey(cellValue)) {
                        map.put(cellValue, strings[i]);
                        continue;
                    }
                    if (map.containsKey(cellValue)) {
                        System.out.println(cellValue + "---------------------");
                        count++;

                    }
                //}

            }
        }
        System.out.println(count);
        System.out.println(map.size());

//        for (int i = 61; i <= 119; i++) {
//            XSSFSheet sheet = workbook.getSheet("一代合格");
//            XSSFRow row = sheet.getRow(i);
//            String cellValue = row.getCell(3).getStringCellValue();
//            if (map.containsKey(cellValue)) {
//                System.out.println(cellValue + "一代重复");
//            } else {
//                map.put(cellValue, "一代合格");
//            }
//        }
        System.out.println(map.size() + "全部");
//        XSSFSheet sheet = workbook.getSheet("建库样本总表");
//        int lastRowNum = sheet.getLastRowNum();
//        int count = 0;
//        for (int i = 1; i <= lastRowNum; i++) {
//            XSSFRow row = sheet.getRow(i);
//            String cellValue = row.getCell(3).getStringCellValue();
//            if (cellValue.equals("22727E")) {
//                String cellValue1 = row.getCell(1).getStringCellValue();
//                if (map.containsKey(cellValue1)) {
//                    map.remove(cellValue1);
//                    count++;
//                    continue;
//                }
//            }
//        }
//        System.out.println(map.size());
//        System.out.println(count);
//        for (String s :
//                map.keySet()) {
//            System.out.println(s + "  " + map.get(s));
//        }

    }



    @Test
    public void gender() throws Exception {
        String file = "E:\\huangxiaomiao\\files\\historyMission\\日常检查\\11月\\221102\\杭州\\杭州.xlsx";
        String file2 = "E:\\huangxiaomiao\\files\\historyMission\\日常检查\\11月\\221102\\杭州\\sum汇总.xlsx";
        String file3 = "C:\\Users\\dr\\Desktop\\项目表整理\\云南11.xlsx";
        FileInputStream fis = new FileInputStream(file);
        FileInputStream fis2 = new FileInputStream(file2);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFWorkbook workbook2 = new XSSFWorkbook(fis2);
        XSSFSheet sheet = workbook.getSheet("Y输出");
        ArrayList<String> sampleNames = new ArrayList<>();
        int lastRowNum = sheet.getLastRowNum();
        System.out.println(lastRowNum);
        for (int i = 0; i <= lastRowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            String cellValue = row.getCell(0).getStringCellValue();
            sampleNames.add(cellValue);
        }
        System.out.println(sampleNames.size());

        String[] strings = {"合格男性样本总表", "合格女性样本总表"};
        for (int i = 0; i < strings.length; i++) {
            int cell = 0;
            if (strings[i].contains("男性")) {
                cell = 3;
            } else {
                cell = 1;
            }
            XSSFSheet sheet2 = workbook2.getSheet(strings[i]);
            int lastRowNum2 = sheet2.getLastRowNum();
            int count = 0;
            for (int j = 1; j <= lastRowNum2; j++) {
                XSSFRow row = sheet2.getRow(j);
                String cellValue = row.getCell(cell).getStringCellValue();
                if (sampleNames.contains(cellValue)) {
                    row.getCell(0).setCellValue("hhh");
                    count++;
                }
            }
            System.out.println(count);
        }
        FileOutputStream outputStream = new FileOutputStream(file3);
        workbook2.write(outputStream);
    }
}
