package test;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class ExcelBC {

    static String fileSetB = "C:\\Users\\dr\\Desktop\\一致性\\肇庆\\张展祥\\Y44128420213104_张展祥_FTAcard-1.xlsx";
    static String outputFile = "C:\\Users\\dr\\Desktop\\一致性\\肇庆\\1最终比对结果\\最终比对结果\\FTAcard-1_Y44128420213104_张展祥_一致性2.xlsx";
    static FileInputStream fis;
    static XSSFWorkbook wBook;
    static XSSFSheet sheet;
    static int lastRowNum;
    static int[] arrB;
    static int[] arr;
    static XSSFColor redColor = new XSSFColor(new Color(255, 0, 0), null);
    static XSSFColor blueColor = new XSSFColor(new Color(0, 0, 255), null);

    static {
        try {
            fis = new FileInputStream(fileSetB);
            wBook = new XSSFWorkbook(fis);
        } catch (Exception e) {
            System.out.println("拿不到文件1");
        }
    }

    public static void main(String[] args) throws Exception {

        ExcelBC compare = new ExcelBC();
        compare.normalB();
        compare.close();
    }

    public void normalB() {
        HashMap<String, HashMap<Double,ArrayList<String>>> mapB = new HashMap<>();
        HashMap<String, HashMap<Double,ArrayList<String>>> mapBY = new HashMap<>();
        sheet = wBook.getSheetAt(0);
        lastRowNum = sheet.getLastRowNum();
        for (int i = 70; i <= lastRowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            String locus = row.getCell(0).getStringCellValue();
            if (!mapB.containsKey(locus)) {
                mapB.put(locus, new HashMap<>());
            }
            if (row.getCell(1).getCellType() == CellType.STRING) {
                continue;
            }
            double alleleName = row.getCell(1).getNumericCellValue();
            if (!mapB.get(locus).containsKey(alleleName)) {
                mapB.get(locus).put(alleleName, new ArrayList<>());
            }
            String repS = row.getCell(5).getStringCellValue();
            if (mapB.get(locus).containsKey(alleleName) && !mapB.get(locus).get(alleleName).contains(repS)) {
                mapB.get(locus).get(alleleName).add(repS);
            }
        }
        sheet = wBook.getSheetAt(34);
        lastRowNum = sheet.getLastRowNum();
        for (int i = 100; i <= lastRowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            String locus = row.getCell(0).getStringCellValue();
            if (!mapBY.containsKey(locus)) {
                mapBY.put(locus, new HashMap<>());
            }
            if (row.getCell(1).getCellType() == CellType.STRING) {
                continue;
            }
            double alleleName = row.getCell(1).getNumericCellValue();
            if (!mapBY.get(locus).containsKey(alleleName)) {
                mapBY.get(locus).put(alleleName, new ArrayList<>());
            }
            String repS = row.getCell(5).getStringCellValue();
            if (mapBY.get(locus).containsKey(alleleName) && !mapBY.get(locus).get(alleleName).contains(repS)) {
                mapBY.get(locus).get(alleleName).add(repS);
            }
        }
        handle(mapB, mapBY);
    }

    public void handle(HashMap<String, HashMap<Double,ArrayList<String>>> mapB,HashMap<String, HashMap<Double,ArrayList<String>>> mapBY) {

        XSSFCellStyle red = wBook.createCellStyle();
        XSSFFont fontRed = wBook.createFont();
        fontRed.setColor(redColor);
        red.setFont(fontRed);

        XSSFCellStyle blue = wBook.createCellStyle();
        XSSFFont fontBlue = wBook.createFont();
        fontBlue.setColor(blueColor);
        blue.setFont(fontBlue);

//        int[] normal = new int[]{1};
//        int[] y = new int[]{4};

//        int[] normal = new int[]{1, 2, 3, 4,5,6,7,8};
//        int[] y = new int[]{11,12,13,14,15,16,17,18};
        int[] normal = new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32};
        int[] y = new int[]{35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66};
        for (int k = 0; k < normal.length; k++) {
            sheet = wBook.getSheetAt(normal[k]);
            lastRowNum = sheet.getLastRowNum();
            for (int i = 122; i <= lastRowNum; i++) {
                XSSFRow row = sheet.getRow(i);
                if (row.getCell(0) == null) {
                    continue;
                }
                String locus = null;
                if (row.getCell(0).getCellType() == CellType.NUMERIC) {
                    locus = row.getCell(0).getCellType() + "";
                } else {
                    locus = row.getCell(0).getStringCellValue();
                }
                if (mapB.containsKey(locus)) {
                    double alleleName = row.getCell(1).getNumericCellValue();
                    if (row.getCell(5) == null || row.getCell(5).equals("")) {
                        continue;
                    }
                    String reP = row.getCell(5).getStringCellValue();
                    if (mapB.get(locus).containsKey(alleleName)) {
                        if (mapB.get(locus).containsKey(alleleName) && !mapB.get(locus).get(alleleName).contains(reP)) {
                            row.getCell(0).setCellStyle(blue);
                            row.getCell(1).setCellStyle(blue);
                            row.getCell(5).setCellStyle(red);
                        }
                        if (mapB.get(locus).containsKey(alleleName) && mapB.get(locus).get(alleleName).contains(reP)) {
                            row.getCell(0).setCellStyle(blue);
                            row.getCell(1).setCellStyle(blue);
                            row.getCell(5).setCellStyle(blue);
                        }
                    }
                    if (!mapB.get(locus).containsKey(alleleName)) {
                        row.getCell(0).setCellStyle(blue);
                        row.getCell(1).setCellStyle(red);
                        row.getCell(5).setCellStyle(red);
                    }

                }
            }
        }
        for (int i = 0; i < y.length; i++) {
            sheet = wBook.getSheetAt(y[i]);
            lastRowNum = sheet.getLastRowNum();
            for (int j = 75; j <= lastRowNum; j++) {
                XSSFRow row = sheet.getRow(j);
                if (row == null) {
                    continue;
                }
                String locus;
                if (row.getCell(0).getCellType() == CellType.NUMERIC) {
                    locus = row.getCell(0).getCellType() + "";
                } else {
                    locus = row.getCell(0).getStringCellValue();
                }
                if (mapBY.containsKey(locus)) {
                    if (row.getCell(1).getCellType() != CellType.NUMERIC) {
                        continue;
                    }
                    double alleleName = row.getCell(1).getNumericCellValue();
                    if (row.getCell(5) == null) {
                        continue;
                    }
                    String reP = row.getCell(5).getStringCellValue();
                    if (mapBY.get(locus).containsKey(alleleName)) {
                        if (mapBY.get(locus).containsKey(alleleName) && !mapBY.get(locus).get(alleleName).contains(reP)) {
                            row.getCell(0).setCellStyle(blue);
                            row.getCell(1).setCellStyle(blue);
                            row.getCell(5).setCellStyle(red);
                        }
                        if (mapBY.get(locus).containsKey(alleleName) && mapBY.get(locus).get(alleleName).contains(reP)) {
                            row.getCell(0).setCellStyle(blue);
                            row.getCell(1).setCellStyle(blue);
                            row.getCell(5).setCellStyle(blue);
                        }
                    }
                    if (!mapBY.get(locus).containsKey(alleleName)) {
                        row.getCell(0).setCellStyle(blue);
                        row.getCell(1).setCellStyle(red);
                        row.getCell(5).setCellStyle(red);
                    }
                }
            }
        }

    }

    private void close() throws Exception {
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        wBook.write(outputStream);
        outputStream.close();
        wBook.close();
    }
}
