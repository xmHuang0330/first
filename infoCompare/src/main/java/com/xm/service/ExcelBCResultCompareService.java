package com.xm.service;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class ExcelBCResultCompareService {

    public void setBData(MultipartFile file) throws IOException {

        XSSFColor redColor = new XSSFColor(new Color(255, 0, 0), null);
        XSSFColor blueColor = new XSSFColor(new Color(0, 0, 255), null);

        InputStream inputStream = file.getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        String fileName = file.getOriginalFilename();

        XSSFCellStyle red = workbook.createCellStyle();
        XSSFFont fontRed = workbook.createFont();
        fontRed.setColor(redColor);
        red.setFont(fontRed);

        XSSFCellStyle blue = workbook.createCellStyle();
        XSSFFont fontBlue = workbook.createFont();
        fontBlue.setColor(blueColor);
        blue.setFont(fontBlue);

        HashMap<String, HashMap<Double, ArrayList<String>>> setBNormalInfo = new HashMap<>();
        HashMap<String, HashMap<Double, ArrayList<String>>> setBYInfo = new HashMap<>();

        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            //System.out.println(i);

            String sheetName = workbook.getSheetName(i);
            //System.out.println(sheetName);

            if (sheetName.equals("Autosomal STR Figure") || sheetName.equals("Y STR Figure") || sheetName.equals("Settings")) {
                continue;
            }

            if (sheetName.equals("Autosomal STRs")) {
                XSSFSheet sheet = workbook.getSheet(sheetName);
                int lastRowNum = sheet.getLastRowNum();

                for (int j = 70; j <= lastRowNum; j++) {
                    XSSFRow row = sheet.getRow(j);
                    if (row == null) {
                        continue;
                    }
                    String locus = row.getCell(0).getStringCellValue();
                    if (row.getCell(1).getCellType() != CellType.NUMERIC) {
                        continue;
                    }
                    double alleleName = row.getCell(1).getNumericCellValue();
                    if (!setBNormalInfo.containsKey(locus)) {
                        setBNormalInfo.put(locus, new HashMap<Double, ArrayList<String>>());
                    }
                    if (setBNormalInfo.containsKey(locus)) {
                        if (!setBNormalInfo.get(locus).containsKey(alleleName)) {
                            setBNormalInfo.get(locus).put(alleleName,new ArrayList<String>());
                        }
                        String repS = row.getCell(5).getStringCellValue();
                        if (setBNormalInfo.get(locus).containsKey(alleleName) && !setBNormalInfo.get(locus).get(alleleName).contains(repS)) {
                            setBNormalInfo.get(locus).get(alleleName).add(repS);
                        }
                    }
                }
            }
            if (sheetName.contains("Autosomal STRs")) {
                if (sheetName.equals("Autosomal STRs")) {
                    continue;
                }
                XSSFSheet sheet = workbook.getSheet(sheetName);
                int lastRowNum = sheet.getLastRowNum();

                for (int j = 122; j <= lastRowNum; j++) {
                    XSSFRow row = sheet.getRow(j);
                    if (row == null) {
                        continue;
                    }
                    if (row.getCell(0) == null) {
                        continue;
                    }
                    String locus = row.getCell(0).getStringCellValue();
                    if (setBNormalInfo.containsKey(locus)) {
                        double alleleName = row.getCell(1).getNumericCellValue();
                        if (row.getCell(5) == null || row.getCell(5).equals("")) {
                            continue;
                        }
                        String reP = row.getCell(5).getStringCellValue();
                        if (setBNormalInfo.get(locus).containsKey(alleleName)) {
                            if (setBNormalInfo.get(locus).containsKey(alleleName) && !setBNormalInfo.get(locus).get(alleleName).contains(reP)) {
                                row.getCell(0).setCellStyle(blue);
                                row.getCell(1).setCellStyle(blue);
                                row.getCell(5).setCellStyle(red);
                            }
                            if (setBNormalInfo.get(locus).containsKey(alleleName) && setBNormalInfo.get(locus).get(alleleName).contains(reP)) {
                                row.getCell(0).setCellStyle(blue);
                                row.getCell(1).setCellStyle(blue);
                                row.getCell(5).setCellStyle(blue);
                            }
                        }
                        if (!setBNormalInfo.get(locus).containsKey(alleleName)) {
                            row.getCell(0).setCellStyle(blue);
                            row.getCell(1).setCellStyle(red);
                            row.getCell(5).setCellStyle(red);
                        }
                    }
                }
            }

            if (sheetName.equals("Y STRs")) {
                XSSFSheet sheet = workbook.getSheet(sheetName);
                int lastRowNum = sheet.getLastRowNum();

                for (int j = 100; j <= lastRowNum; j++) {
                    XSSFRow row = sheet.getRow(j);
                    if (row == null) {
                        continue;
                    }
                    String locus = row.getCell(0).getStringCellValue();
                    if (row.getCell(1).getCellType() != CellType.NUMERIC) {
                        continue;
                    }
                    double alleleName = row.getCell(1).getNumericCellValue();
                    if (!setBYInfo.containsKey(locus)) {
                        setBYInfo.put(locus, new HashMap<Double, ArrayList<String>>());
                    }
                    if (setBYInfo.containsKey(locus)) {
                        if (!setBYInfo.get(locus).containsKey(alleleName)) {
                            setBYInfo.get(locus).put(alleleName,new ArrayList<String>());
                        }
                        String repS = row.getCell(5).getStringCellValue();
                        if (setBYInfo.get(locus).containsKey(alleleName) && !setBYInfo.get(locus).get(alleleName).contains(repS)) {
                            setBYInfo.get(locus).get(alleleName).add(repS);
                        }
                    }
                }
            }

            if (sheetName.contains("Y STRs")) {
                if (sheetName.equals("Y STRs")) {
                    continue;
                }
                XSSFSheet sheet = workbook.getSheet(sheetName);
                int lastRowNum = sheet.getLastRowNum();

                for (int j = 75; j <= lastRowNum; j++) {
                    XSSFRow row = sheet.getRow(j);
                    if (row == null) {
                        continue;
                    }
                    String locus = row.getCell(0).getStringCellValue();
                    if (setBYInfo.containsKey(locus)) {
                        if (row.getCell(1).getCellType() != CellType.NUMERIC) {
                            continue;
                        }
                        double alleleName = row.getCell(1).getNumericCellValue();
                        if (row.getCell(5) == null || row.getCell(5).equals("")) {
                            continue;
                        }
                        String reP = row.getCell(5).getStringCellValue();
                        if (setBYInfo.get(locus).containsKey(alleleName)) {
                            if (setBYInfo.get(locus).containsKey(alleleName) && !setBYInfo.get(locus).get(alleleName).contains(reP)) {
                                row.getCell(0).setCellStyle(blue);
                                row.getCell(1).setCellStyle(blue);
                                row.getCell(5).setCellStyle(red);
                            }
                            if (setBYInfo.get(locus).containsKey(alleleName) && setBYInfo.get(locus).get(alleleName).contains(reP)) {
                                row.getCell(0).setCellStyle(blue);
                                row.getCell(1).setCellStyle(blue);
                                row.getCell(5).setCellStyle(blue);
                            }
                        }
                        if (!setBYInfo.get(locus).containsKey(alleleName)) {
                            row.getCell(0).setCellStyle(blue);
                            row.getCell(1).setCellStyle(red);
                            row.getCell(5).setCellStyle(red);
                        }
                    }
                }
            }

        }
        FileOutputStream outputStream = new FileOutputStream("C:\\Users\\dr\\Desktop\\比对结果\\reports" + "\\" + fileName);
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();
    }
}
