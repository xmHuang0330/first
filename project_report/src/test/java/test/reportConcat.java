package test;


import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.LinkedHashMap;

@Slf4j
public class reportConcat {

    @Test
    public void demo1() {

        try {
            String allF = "E:\\huangxiaomiao\\files\\A历史\\代码测试\\侧翼\\报告";
            File fileList = new File(allF);
            File[] files = fileList.listFiles();
            String[] sheetNames = new String[]{"Autosomal STRs", "Y STRs"};
            System.out.println(files.length);
            for (File file :
                    files) {
                if (file.isDirectory()) {
                    continue;
                } else {
                    log.info(file.getAbsolutePath());
                }
                FileInputStream fileInputStream = new FileInputStream(file);
                XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
                System.out.println(file.getName());
                autoNormal(workbook, sheetNames);
            }
        } catch (FileNotFoundException e) {
            log.info("输入文件有误");
        } catch (IOException e) {
            log.info("workbook出错");
        }
    }

    public void autoNormal(XSSFWorkbook workbook,String[] sheetNames) {

        LinkedHashMap<String, String> locusASequence = new LinkedHashMap<>();
        LinkedHashMap<String, LinkedHashMap<String, String>> locusASequenceMap = new LinkedHashMap<>();
        String name = "";
        int start = 0;
        for (String sheetName :
                sheetNames) {
            XSSFSheet sheet = workbook.getSheet(sheetName);
            int lastRowNum = sheet.getLastRowNum();
            if (sheetName.contains("Auto")) {
                start = 70;
                name = sheet.getRow(2).getCell(1).getStringCellValue();
            } else {
                start = 100;
            }
            for (int i = start; i <= lastRowNum; i++) {
                XSSFRow row = sheet.getRow(i);
                String locus = row.getCell(0).getStringCellValue();
                String type = row.getCell(2).getStringCellValue();
                if (!type.equals("Yes")) {
                    continue;
                }
                String snp = "";
                if (row.getCell(4) == null) {
                    snp = "";
                } else {
                    snp = row.getCell(4).getStringCellValue();
                }
                String seq = row.getCell(5).getStringCellValue();
                String concatSeq = seq.concat("_").concat(snp);
                if (locusASequence.containsKey(locus)) {
                    String key = locusASequence.get(locus);
                    String concat = key.concat(",").concat(concatSeq);
                    locusASequence.replace(locus, concat);
                } else {
                    locusASequence.put(locus, concatSeq);
                }
            }
            locusASequenceMap.put(name, locusASequence);
            //System.out.println(locusASequence.size());
            locusASequence.keySet().forEach((String s) ->{
                //log.info(s + "   ---->>>>   " + locusASequence.get(s));
            });
        }
        try {
            workbook.close();
        } catch (IOException e) {
            log.info("输入关闭有误");
        }
        writeOut(locusASequenceMap);
    }

    public void writeOut(LinkedHashMap<String, LinkedHashMap<String, String>> dataMap) {
        try {
            FileInputStream fis = new FileInputStream("E:\\huangxiaomiao\\files\\A历史\\代码测试\\侧翼\\序列_侧翼.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheet("序列_侧翼");
            int lastRowNum = sheet.getLastRowNum() + 1;
            log.info(lastRowNum + "");
            for (String s : dataMap.keySet()) {
                sheet.createRow(lastRowNum);
                log.info(lastRowNum + "");
                XSSFRow row = sheet.getRow(lastRowNum);
                System.out.println(lastRowNum);
                int i = 0;
                row.createCell(i++).setCellValue(s);
                //log.info(s);
                LinkedHashMap<String, String> locusASeq = dataMap.get(s);
                for (String locus :
                        locusASeq.keySet()) {
                    row.createCell(i++);
                    XSSFCell cell = row.getCell(i - 1);
                    cell.setCellValue(locusASeq.get(locus));
                    //log.info(locusASeq.get(locus));
                }
                //log.info(i + "");
                //log.info(lastRowNum + "");
                log.info(sheet.getSheetName());
            }
            FileOutputStream fileOutputStream = new FileOutputStream("E:\\huangxiaomiao\\files\\A历史\\代码测试\\侧翼\\序列_侧翼.xlsx");
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            workbook.close();
            /*dataMap.keySet().forEach((Object s) -> {
                log.info(s + "   ------>   " + dataMap.get(s));
                dataMap.get(s).keySet().forEach((String l) -> {
                    log.info(l + "   ---->   " + dataMap.get(s).get(l));
                });
            });*/
        } catch (FileNotFoundException e) {
            log.info("输出文件有误");
        } catch (IOException e) {
            log.info("获取输出文件有误");
        }

    }
}
