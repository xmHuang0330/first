package com.xm.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExcelSetBTagService {

    static XSSFColor redColor = new XSSFColor(new Color(255, 0, 0), null);
    static XSSFColor greenColor = new XSSFColor(new Color(0,168,11), null);
    static XSSFColor orangeColor = new XSSFColor(new Color(255, 165, 0), null);
    static XSSFColor blueColor = new XSSFColor(new Color(0, 0, 255), null);
    static double reads = 400d;

    public void ExcelData(MultipartFile file) throws Exception {
        InputStream fileInputStream = file.getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);

        XSSFCellStyle red = workbook.createCellStyle();
        XSSFFont fontRed = workbook.createFont();
        fontRed.setColor(redColor);
        red.setFont(fontRed);

        XSSFCellStyle green = workbook.createCellStyle();
        XSSFFont fontGreen = workbook.createFont();
        fontGreen.setColor(greenColor);
        green.setFont(fontGreen);

        XSSFCellStyle orange = workbook.createCellStyle();
        XSSFFont fontOrange = workbook.createFont();
        fontOrange.setColor(orangeColor);
        orange.setFont(fontOrange);

        XSSFCellStyle blue = workbook.createCellStyle();
        XSSFFont fontBlue = workbook.createFont();
        fontBlue.setColor(blueColor);
        blue.setFont(fontBlue);



        String fileName = file.getOriginalFilename();
        System.out.println(fileName);
        HashMap<String, ArrayList<ArrayList<Object>>> map = new HashMap<>();
        int rowBegin;
        int[] sheetNum = new int[]{0, 2};
        for (int i = 0; i < sheetNum.length; i++) {
            int num = 2;
            if (sheetNum[i] == 2) {
                num = 1;
            }
            XSSFSheet sheet = workbook.getSheetAt(sheetNum[i]);
            int lastRowNum = sheet.getLastRowNum();
            if (i == 0) {
                rowBegin = 70;
            } else {
                rowBegin = 100;
            }
            for (int j = rowBegin ; j <= lastRowNum; j++) {
                XSSFRow row = sheet.getRow(j);
                if (row == null) {
                    continue;
                }
                String locus = row.getCell(0).getStringCellValue();
                if (!map.containsKey(locus)) {
                    map.put(locus, new ArrayList<ArrayList<Object>>());
                }
                ArrayList<Object> values = new ArrayList<>();
                for (int k = 0; k < 6; k++) {
                    XSSFCell valueType = row.getCell(k);
                    if (valueType == null || valueType.equals("")) {
                        continue;
                    }
                    if (valueType.getCellType() == CellType.NUMERIC) {
                        /*double typeValue = row.getCell(k).getNumericCellValue();
                        BigDecimal value = new BigDecimal(typeValue);
                        BigDecimal noZeros = value.stripTrailingZeros();
                        String type = noZeros.toPlainString();
                        values.add(type);*/
                        values.add(valueType.getNumericCellValue() + "");
                    } else {
                        values.add(valueType.getStringCellValue());
                    }
                }
                map.get(locus).add(values);
            }
            //400
            List<String> failLocus = filter(map, reads);
            int rowEnd;
            if (i == 0) {
                rowEnd = 66;
            } else {
                rowEnd = 96;
            }
            for (int j = 15; j <= rowEnd; j++) {
                //获取单元格
                XSSFRow row = sheet.getRow(j);
                if (row == null) {
                    continue;
                }
                String locus = row.getCell(0).getStringCellValue();
                if (failLocus.contains(locus)) {
                    row.getCell(0).setCellStyle(red);
                } else {
                    //row.getCell(0).setCellStyle(green);
                    continue;
                }
            }
            //YES平均值的十分之一
            HashMap<String, Double> yesTypes = new HashMap<>();
            for (int j = rowBegin; j <= lastRowNum ; j++) {
                //获取单元格
                XSSFRow row = sheet.getRow(j);
                //如果为空，则说明该行没有数据
                if (row == null) {
                    continue;
                }
                if (row.getCell(2).getStringCellValue().equals("Yes")) {
                    if (!yesTypes.containsKey(row.getCell(0).getStringCellValue())) {
                        yesTypes.put(row.getCell(0).getStringCellValue(), row.getCell(3).getNumericCellValue() / 10);
                    } else {
                        double v = (yesTypes.get(row.getCell(0).getStringCellValue()) * 10 + row.getCell(3).getNumericCellValue()) / 20;
                        yesTypes.remove(row.getCell(0).getStringCellValue(), row.getCell(3).getNumericCellValue() / 10);
                        yesTypes.put(row.getCell(0).getStringCellValue(), v);
                    }
                }
                else {
                    if (yesTypes.containsKey(row.getCell(0).getStringCellValue())) {
                        if (row.getCell(3).getNumericCellValue() < yesTypes.get(row.getCell(0).getStringCellValue())) {
                            row.getCell(2).setCellStyle(red);
                        }
                    }
                }
            }
            //相同Allele Name的Yes的reads的六分之一
            Map<String, HashMap<Double, Double>> locANReadsArrayList = new HashMap<>();
            HashMap<String, HashMap<Double,String>> locusAlleleRepeatSMap = new HashMap<>();
            for (int j = rowBegin; j <= lastRowNum; j++) {
                XSSFRow row = sheet.getRow(j);
                if (row == null) {
                    continue;
                }
                if (row.getCell(1).getCellType() != CellType.NUMERIC) {
                    continue;
                }
                String locus = row.getCell(0).getStringCellValue();
                double alleleName = row.getCell(1).getNumericCellValue();
                String typedAllele = row.getCell(2).getStringCellValue();
                double reads = row.getCell(3).getNumericCellValue();

                if (typedAllele.equals("Yes")) {
                    HashMap<Double, Double> alleleNameReadsMap = new HashMap<>();
                    if (!locANReadsArrayList.containsKey(locus)) {
                        alleleNameReadsMap.put(alleleName, reads);
                        locANReadsArrayList.put(locus, alleleNameReadsMap);
                    }
                    if (locANReadsArrayList.get(locus).containsKey(alleleName)) {
                        double maxReads = Math.max(locANReadsArrayList.get(locus).get(alleleName), reads);
                        locANReadsArrayList.get(locus).put(alleleName, maxReads);
                    } else {
                        locANReadsArrayList.get(locus).put(alleleName, reads);
                    }
                }
            }
            for (int j = rowBegin; j <= lastRowNum; j++) {
                XSSFRow row = sheet.getRow(j);
                lastRowNum = sheet.getLastRowNum();
                if (row == null) {
                    continue;
                }
                String locus = row.getCell(0).getStringCellValue();
                if (locANReadsArrayList.containsKey(locus)
                        && locANReadsArrayList
                        .get(locus)
                        .containsKey(row.getCell(1)
                                .getNumericCellValue())
                        && row.getCell(3)
                        .getNumericCellValue()
                        < (locANReadsArrayList
                        .get(locus)
                        .get(row.getCell(1).getNumericCellValue()) / 6)) {
                    row.getCell(2).setCellStyle(orange);

                }
                if (!locANReadsArrayList.containsKey(locus)) {
                    row.getCell(0).setCellStyle(blue);
                    locusAlleleRepeatSMap.put(locus,new HashMap<Double, String>());
                }
            }
            for (int j = rowBegin; j <= lastRowNum; j++) {
                XSSFRow row = sheet.getRow(j);
                String locus = row.getCell(0).getStringCellValue();
                if (row.getCell(1).getCellType() != CellType.NUMERIC) {
                    continue;
                }
                double alleleName = row.getCell(1).getNumericCellValue();
                String repeatS = row.getCell(5).getStringCellValue();
                if (locusAlleleRepeatSMap.containsKey(locus)) {
                    if (locusAlleleRepeatSMap.get(locus).size() >= num) {

//                        for (Double alleleN :
//                                locusAlleleRepeatSMap.get(locus).keySet()) {
//                            minAlleleName = Math.min(alleleName, alleleN);
//                            for (Double alleleN2 :
//                                    locusAlleleRepeatSMap.get(locus).keySet()) {
//                                minAlleleName = Math.min(minAlleleName, alleleN2);
//
//                            }
//                        }
//                        System.out.println(locus + "--------" + minAlleleName);
//                        if (minAlleleName == alleleName) {
//                            continue;
//                        } else {
//                            locusAlleleRepeatSMap.get(locus).remove(minAlleleName);
//                            locusAlleleRepeatSMap.get(locus).put(alleleName, repeatS);
//                        }
                        continue;
                    } else {
                        locusAlleleRepeatSMap.get(locus).put(alleleName, repeatS);
                    }

                }
            }
            int count = 0;
            int count1 = 0;
            for (int j = 15; j <= rowEnd; j++) {
                XSSFRow row = sheet.getRow(j);
                String locus = row.getCell(0).getStringCellValue();
                if (i == 2) {
                    if (row.getCell(1).getStringCellValue().equals("Y")) {
                        continue;
                    }
                }
                XSSFRichTextString xssfRichTextString = new XSSFRichTextString();
                XSSFRichTextString xssfRichTextString2 = new XSSFRichTextString();
                if (locusAlleleRepeatSMap.containsKey(locus)) {
//                    if (row.getCell(1).getStringCellValue().equals("Y")) {
//                        continue;
//                    }
                    count1 = 0;
                    row.getCell(0).setCellStyle(green);
                    for (Double alleleName :
                            locusAlleleRepeatSMap.get(locus).keySet()) {
                        BigDecimal allName = new BigDecimal(alleleName);
                        BigDecimal noZeros = allName.stripTrailingZeros();
                        String aName = noZeros.toPlainString();
                        if (count1 == 0) {
                            xssfRichTextString.append(aName);
                            xssfRichTextString2.append(locusAlleleRepeatSMap.get(locus).get(alleleName));
                            count1++;
                            count++;
                        } else {
                            xssfRichTextString.append("," + aName);
                            xssfRichTextString2.append("," + locusAlleleRepeatSMap.get(locus).get(alleleName));
                            count++;
                        }

                    }
                    row.getCell(1).setCellValue(xssfRichTextString);
                    row.getCell(2).setCellValue("");
                    row.getCell(3).setCellValue(xssfRichTextString2);
                }
                /*if (!row.getCell(1).getCellType().equals(CellType.STRING )|| !row.getCell(1).getCellType().equals(CellType.NUMERIC)) {
                    count++;
                    log.info(count + "");
                }*/
                /*if (row.getCell(1).getCellType() != CellType._NONE || !row.getCell(1).getStringCellValue().equals("")) {
                    count++;
                    log.info(count + "");
                }*/
            }
            XSSFRow row = sheet.getRow(11);

            int sum = 0;
            if (i == 0) {
                sum = 52;
            } else {
                sum = 82;
               // count = count + 1;
            }
            row.getCell(1).setCellValue(count + "/" + sum);

        }
        //close();
        FileOutputStream outputStream = new FileOutputStream("C:\\Users\\dr\\Desktop\\标记结果\\reports" + "\\" + fileName);
        workbook.write(outputStream);
        outputStream.close();
        workbook.close();
    }

    /**
     * R1、R2 与 400
     */
    private List<String> filter(HashMap<String, ArrayList<ArrayList<Object>>> map, double minDepth) {
        List<String> failLocus = new ArrayList<>();
        for (String locus :
                map.keySet()) {
            ArrayList<ArrayList<Object>> data = map.get(locus);
            double sum = 0;
            int count = 0;
            double R1 = 0D;
            double R2 = 0D;
            for (ArrayList<Object> rows :
                    data) {
                if (rows.get(3).equals("") || rows.get(3) == null) {
                    continue;
                }
                if (count < 2) {
                    sum = sum + Double.valueOf((String) rows.get(3));
                }
                if (count == 0) {
                    R1 = sum;
                }
                if (count == 1) {
                    R2 = sum - R1;
                }
                count++;
            }
            if ((R1 + R2) < minDepth) {
                failLocus.add(locus);
            } else if (((R1 / R2) < 0.5D || (R1 / R2) > 2D) && Math.max(R1,R2) < minDepth) {
                failLocus.add(locus);
            }
        }
        return failLocus;
    }
}
