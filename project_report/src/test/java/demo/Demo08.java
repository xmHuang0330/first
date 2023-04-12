package demo;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Demo08 {

    static String inputFile = "E:\\huangxiaomiao\\files\\220829\\20220824_V300113172\\V300113172_L04_976_诺唯赞试剂D4_setB.xlsx";
    static String outputFile = "E:\\huangxiaomiao\\files\\220829\\20220824_V300113172\\最后\\V300113172_L04_976_诺唯赞试剂D4_setB.xlsx";
    static  FileInputStream fis;
    static XSSFWorkbook wBook;
    static XSSFColor redColor = new XSSFColor(new Color(255, 0, 0), null);
    static XSSFColor greenColor = new XSSFColor(new Color(0,255,127), null);
    static XSSFColor orangeColor = new XSSFColor(new Color(255, 165, 0), null);
    static XSSFColor blueColor = new XSSFColor(new Color(0, 0, 255), null);
    static XSSFSheet sheet;
    static int lastRowNum;
    static double reads = 400d;

    static {
        try {
            fis = new FileInputStream(inputFile);
            wBook = new XSSFWorkbook(fis);
            sheet = wBook.getSheetAt(2);
            lastRowNum = sheet.getLastRowNum();
            fis.close();
        } catch (Exception e) {
            System.out.println("拿不到文件");
        }
    }
    public static void main(String[] args) throws Exception {
        new Demo08().Excel();
    }

    /**
     * 所有数据
     * @param
     * @return
     * @throws Exception
     */
    private HashMap<String, ArrayList<ArrayList<Object>>> Excel() throws Exception {
        //把下面读取到的信息都存到里面
        // 原来的数组
        HashMap<String, ArrayList<ArrayList<Object>>> map = new HashMap<>();
        for (int i = 100; i <= lastRowNum; i++) {
            //获取单元格
            XSSFRow row = sheet.getRow(i);
            //如果为空，则说明该行没有数据
            if (row == null) {
                continue;
            }
            String locus = row.getCell(0).getStringCellValue();

            if (!map.containsKey(locus)) {
                map.put(locus,new ArrayList<>());
            }
            ArrayList<Object> values = new ArrayList<>();
            for (int j = 0; j < 6; j++) {
                XSSFCell cell = row.getCell(j);
                //如果是数字
                if (cell.getCellType() == CellType.NUMERIC) {
                    values.add(cell.getNumericCellValue()+"");
                } else {
                    values.add(cell.getStringCellValue());
                }
            }
            map.get(locus).add(values);
        }
        //不符合要求的位点
        //更改颜色
        filter(map, reads);
        return map;
    }

    /**
     * R1、R2 与 400
     * @throws IOException
     */
    private void filter(HashMap<String, ArrayList<ArrayList<Object>>> map, double minDepth) throws Exception {

        XSSFCellStyle red = wBook.createCellStyle();
        XSSFFont fontRed = wBook.createFont();
        fontRed.setColor(redColor);
        red.setFont(fontRed);

        XSSFCellStyle green = wBook.createCellStyle();
        XSSFFont fontGreen = wBook.createFont();
        fontGreen.setColor(greenColor);
        green.setFont(fontGreen);

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
                if (rows.get(3) == null || rows.get(3).equals("")) {
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
        for (int i = 15; i <= 96; i++) {
            //获取单元格
            XSSFRow row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            String locus = row.getCell(0).getStringCellValue();
            if (failLocus.contains(locus)) {
                row.getCell(0).setCellStyle(red);
            } else {
                row.getCell(0).setCellStyle(green);
            }
        }
        yesType();
        AlleleNameFilter();
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        wBook.write(outputStream);
        outputStream.close();
        wBook.close();
    }

    /**
     * YES平均值的十分之一
     */
    private void yesType() {
        HashMap<String, Double> yesType = new HashMap<>();
        XSSFCellStyle red = wBook.createCellStyle();
        XSSFFont fontRed = wBook.createFont();
        fontRed.setColor(redColor);
        red.setFont(fontRed);
        for (int i = 100; i <= lastRowNum ; i++) {
            //获取单元格
            XSSFRow row = sheet.getRow(i);
            //如果为空，则说明该行没有数据
            if (row == null) {
                continue;
            }
            if (row.getCell(2).getStringCellValue().equals("Yes")) {
                if (!yesType.containsKey(row.getCell(0).getStringCellValue())) {
                    yesType.put(row.getCell(0).getStringCellValue(), row.getCell(3).getNumericCellValue() / 10);
                } else {
                    double v = (yesType.get(row.getCell(0).getStringCellValue()) * 10 + row.getCell(3).getNumericCellValue()) / 20;
                    yesType.remove(row.getCell(0).getStringCellValue(), row.getCell(3).getNumericCellValue() / 10);
                    yesType.put(row.getCell(0).getStringCellValue(), v);
                }
            }
            else {
                if (yesType.containsKey(row.getCell(0).getStringCellValue())) {
                    if (row.getCell(3).getNumericCellValue() < yesType.get(row.getCell(0).getStringCellValue())) {
                        row.getCell(2).setCellStyle(red);
                    }
                }
            }
        }
    }

    /**
     * 相同Allele Name的Yes的reads的六分之一
     * Locus、Allele Name、Reads
     */
    private void AlleleNameFilter() {

        XSSFCellStyle orange = wBook.createCellStyle();
        XSSFFont fontOrange = wBook.createFont();
        fontOrange.setColor(orangeColor);
        orange.setFont(fontOrange);

        XSSFCellStyle blue = wBook.createCellStyle();
        XSSFFont fontBlue = wBook.createFont();
        fontBlue.setColor(blueColor);
        blue.setFont(fontBlue);

        Map<String, HashMap<Double, Double>> locANReadsArrayList = new HashMap<>();
        for (int i = 100; i <= lastRowNum; i++) {
            XSSFRow row = sheet.getRow(i);
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
        for (int i = 100; i <= lastRowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            if (locANReadsArrayList.containsKey(row.getCell(0).getStringCellValue())
                    && locANReadsArrayList
                        .get(row.getCell(0).getStringCellValue())
                        .containsKey(row.getCell(1).getNumericCellValue())
                    && row.getCell(3)
                         .getNumericCellValue()
                        < (locANReadsArrayList
                        .get(row.getCell(0).getStringCellValue())
                        .get(row.getCell(1).getNumericCellValue()) / 6)) {
                row.getCell(2).setCellStyle(orange);
            }
            if (!locANReadsArrayList.containsKey(row.getCell(0).getStringCellValue())) {
                row.getCell(0).setCellStyle(blue);
            }
        }
    }
}
