package test;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class Data {

    @Test
    public void tablet() {
        try {
            FileInputStream fis = new FileInputStream("E:\\huangxiaomiao\\files\\A历史\\其他\\中山数据\\Zhongshan.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet1 = workbook.getSheet("sheet1");
            //HashMap<String, String> nameATb = new HashMap<>();
            HashMap<String, ArrayList<String>> nameATbL = new HashMap<>();
            int lastRowNum = sheet1.getLastRowNum();

            String allF = "E:\\huangxiaomiao\\files\\A历史\\其他\\中山数据\\表";
            File file = new File(allF);
            File[] files = file.listFiles();
            int count = 1;
            for (File file1 : files) {
                System.out.println(file1.getName() + "---------------");
                FileInputStream fis1 = new FileInputStream(file1);
                XSSFWorkbook workbook1 = new XSSFWorkbook(fis1);
                System.out.println("---------------");
                XSSFSheet lane = workbook1.getSheet("Lane");
                int t = 0;
                int n = 0;
                for (int i = 0; i < 10; i++) {
                    String cellValue = lane.getRow(3).getCell(i).getStringCellValue();
                    if (cellValue.equals("tablet")) {
                        t = i;
                    }
                    if (cellValue.equals("name")) {
                        n = i;
                    }
                }
                int lastRowNum1 = lane.getLastRowNum();
                for (int i = 4; i <= lastRowNum1; i++) {
                    XSSFRow row = lane.getRow(i);
                    String name1 = null;
                    if (row.getCell(n) == null) {
                        continue;
                    }
                    if (row.getCell(n).getCellType() == CellType.NUMERIC) {
                        name1 = row.getCell(n).getNumericCellValue() + "";
                    } else{
                        name1 = row.getCell(n).getStringCellValue();
                    }
                    String tb = null;
                    if (row.getCell(t).getCellType() == CellType._NONE) {
                        System.out.println(name1 + "的板号为null" + "-------" + file1.getName());
                    }
                    if (row.getCell(t).getCellType() == CellType.NUMERIC) {
                        tb = row.getCell(t).getNumericCellValue() + "";
                    }
                    if (row.getCell(t).getCellType() == CellType.STRING) {
                        tb = row.getCell(t).getStringCellValue();
                    }
                    if (nameATbL.containsKey(name1)) {
                        nameATbL.get(name1).add(tb);
                    }
                    if (!nameATbL.containsKey(name1)) {
                        nameATbL.put(name1, new ArrayList<>());
                        nameATbL.get(name1).add(tb);
                    }
                }
                System.out.println(count++);
                workbook1.close();
                fis1.close();
            }
            int c = 0;
            int size = nameATbL.size();
            int a = 0;
            for (int i = 1; i <= lastRowNum; i++) {
                XSSFRow row = sheet1.getRow(i);
                String name = row.getCell(0).getStringCellValue();
                if (!nameATbL.containsKey(name)) {
                    a++;
                    continue;
                }
                ArrayList<String> strings = nameATbL.get(name);
                if (strings.size() == 0) {
                    continue;
                }
                for (String s :
                        strings) {
                    if (c == 0) {
                        row.createCell(2).setCellValue(s);
                        c++;
                    }
                    if (c != 0) {
                        String cellValue = row.getCell(2).getStringCellValue();
                        row.getCell(2).setCellValue(cellValue + "," + s);
                    }
                }
                c = 0;
            }
            System.out.println(a + "个里面没有数据");
            //FileOutputStream fileOutputStream = new FileOutputStream("E:\\huangxiaomiao\\files\\A历史\\其他\\中山数据\\Zhongshan3.xlsx");
           // workbook.write(fileOutputStream);
            workbook.close();
            //fileOutputStream.close();
            fis.close();


        } catch (FileNotFoundException e) {
            System.out.println("找不到文件");
        } catch (IOException e) {
            System.out.println("io出错");
        }
    }

    @Test
    public void fileA() {
        String allF = "E:\\huangxiaomiao\\files\\A历史\\其他工作\\中山数据\\1";
        File fileList = new File(allF);
        File[] files = fileList.listFiles();
        for (File f :
                files) {
            try {
                FileInputStream fis = new FileInputStream(f);
                XSSFWorkbook workbook = new XSSFWorkbook(fis);
                log.info(f.getName());
                data(workbook);
            } catch (FileNotFoundException e) {
                log.info("找不到文件："+ f.getName());
            } catch (IOException e) {
                log.info("IO出错");
            }

        }
    }

    public void data(XSSFWorkbook workbook) {
        Map<String, LinkedHashMap<String, ArrayList<String>>> allData = new LinkedHashMap<>();
        String[] sheetNames = new String[]{"geno", "genoSeq"};
        LinkedHashMap<String, ArrayList<String>> hashMap = new LinkedHashMap<>();
        HashMap<Integer, String> indexASample = new HashMap<>();
        String name = null;
        int start = 5;
        int end = 139;
        for (String sheetName : sheetNames) {
            if (sheetName.equals("genoSeq")) {
                start = 1;
                end = 135;
            }
            if (!hashMap.containsKey(sheetName)) {
                hashMap.put(sheetName, new ArrayList<>());
            }
            XSSFSheet sheet = workbook.getSheet(sheetName);
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                XSSFRow row = sheet.getRow(i);
                int index = (int)row.getCell(0).getNumericCellValue();
                if (sheetName.equals("geno")) {
                    if (row.getCell(4) == null) {
                        name = "";
                    } else if (row.getCell(4).getCellType().equals(CellType.NUMERIC)) {
                        name = row.getCell(4).getNumericCellValue() + "";
                    } else {
                        name = row.getCell(4).getStringCellValue();
                    }
                    if (sheetName.equals("geno")) {
                        indexASample.put(index, name);
                        allData.put(name, new LinkedHashMap<>());
                    }
                    if (!allData.get(name).containsKey(sheetName)) {
                        allData.get(name).put(sheetName, new ArrayList<>());
                    }
                }
                if (sheetName.equals("genoSeq")) {
                    String s = indexASample.get(index);
                    allData.get(s).put(sheetName, new ArrayList<>());
                }
               // LinkedHashMap<String, ArrayList<String>> linkedHashMap = new LinkedHashMap<>();
                /*if (!linkedHashMap.keySet().contains(sheetName)) {
                    linkedHashMap.put(sheetName, new ArrayList<>());
                }*/
                //ArrayList<String> dataList = new ArrayList<>();
                for (int j = start; j < end; j++) {
                    if (sheetName.equals("genoSeq")) {
                        name = indexASample.get(index);
                    }
                    if (row.getCell(j) == null) {
                        allData.get(name).get(sheetName).add("");
                    } else if (row.getCell(j).getCellType().equals(CellType.NUMERIC)) {
                        allData.get(name).get(sheetName).add(row.getCell(j).getNumericCellValue() + "");
                    } else {
                        allData.get(name).get(sheetName).add(row.getCell(j).getStringCellValue());
                    }
                }

            }

        }
        writeOut(allData,indexASample);
    }

    public void writeOut(Map<String, LinkedHashMap<String, ArrayList<String>>> dataMap,HashMap<Integer, String> indexASample) {
        try {
            FileInputStream fis = new FileInputStream("E:\\huangxiaomiao\\files\\A历史\\其他工作\\中山数据\\data.xlsx");
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            String[] sheetNames = new String[]{"geno", "genoSeq"};
            for (String sheetName : sheetNames) {
                XSSFSheet sheet = workbook.getSheet(sheetName);
                int lastRowNum = sheet.getLastRowNum() + 1;
                for (String name :
                        dataMap.keySet()) {
                    sheet.createRow(lastRowNum++);
                    XSSFRow row = sheet.getRow(lastRowNum-1);
                    int i = 0;
                    row.createCell(i++);
                    row.getCell(i - 1).setCellValue(name);
                    //System.out.println(name);
                    for(String sheetName2: dataMap.get(name).keySet()) {
                        if (sheetName.equals(sheetName2)) {
                            for (String s : dataMap.get(name).get(sheetName)) {
                                row.createCell(i++).setCellValue(s);
                            }
                        }
                    }
                }
                //System.out.println(lastRowNum);
            }
            FileOutputStream fileOutputStream = new FileOutputStream("E:\\huangxiaomiao\\files\\A历史\\其他工作\\中山数据\\data.xlsx");
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
