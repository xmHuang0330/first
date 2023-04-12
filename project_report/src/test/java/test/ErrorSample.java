package test;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
public class ErrorSample {
    static XSSFColor blueColor = new XSSFColor(new Color(0, 0, 255), null);
    static String inputFile = "E:\\huangxiaomiao\\files\\A历史\\项目填写\\2023年\\4月\\lane表\\115. V350127804样本信息表2023.03.30.xlsx";
    static String outputFile = "E:\\huangxiaomiao\\files\\A历史\\项目填写\\2023年\\4月\\lane表\\115. V350127804样本信息表2023.03.30111.xlsx";
    @Test
    public void Test() {
        try {
            FileInputStream fis = new FileInputStream(inputFile);
            String[] strings = new String[]{"lane1", "lane2", "lane3", "lane4"};
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            HashMap<String, ArrayList> map = new HashMap<>();
            for (String sheetName :
                    strings) {
                map.put(sheetName, new ArrayList());
                XSSFSheet sheet = workbook.getSheet(sheetName);
                int lastRowNum = sheet.getLastRowNum();
                System.out.println(lastRowNum);
                for (int i = 1; i <= lastRowNum; i++) {
                    XSSFRow row = sheet.getRow(i);
                    if (row.getCell(2) == null) {
                        continue;
                    }
                    CellType cellType = row.getCell(2).getCellType();
                    if (cellType.equals(CellType._NONE)) {
                        continue;
                    } else if (cellType.equals(CellType.NUMERIC)) {
                        String name = row.getCell(2).getNumericCellValue() + "";
                        if (map.get(sheetName).contains(name)) {
                            log.info(name + "重复---->" + sheetName);
                        } else {
                            map.get(sheetName).add(name);
                        }
                    } else if (cellType.equals(CellType.STRING)) {
                        String name = row.getCell(2).getStringCellValue();
                        if (map.get(sheetName).contains(name)) {
                            log.info(name + "重复---->" + sheetName);
                        } else {
                            map.get(sheetName).add(name);
                        }
                    }
                }
                workbook.close();
            }

        } catch (FileNotFoundException e) {
            log.info("输入文件有误");
        } catch (IOException e) {
            log.info("workbook有误");
        }

    }
}
