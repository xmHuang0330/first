package test;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.List;

public class Excel {

    static String inputFile = "E:\\huangxiaomiao\\files\\张兴锟\\V300113169_L04_981_0109-2022-0189-R1_setB.xlsx";
    static String outputFile = "C:\\Users\\dr\\Desktop\\标记结果\\reports\\V300113169_L04_981_0109-2022-0189-R1_setB.xlsx";
    static FileInputStream fis;
    static XSSFWorkbook wBook;
    static XSSFColor redColor = new XSSFColor(new Color(255, 0, 0), null);
    static XSSFColor greenColor = new XSSFColor(new Color(0,153,0), null);
    static XSSFColor orangeColor = new XSSFColor(new Color(255, 165, 0), null);
    static XSSFColor blueColor = new XSSFColor(new Color(0, 0, 255), null);
    static XSSFSheet sheet;
    static int lastRowNum;
    static double reads = 400d;

    static {
        try {
            fis = new FileInputStream(inputFile);
            wBook = new XSSFWorkbook(fis);
        } catch (Exception e) {
            System.out.println("拿不到文件");
        }
    }
    public static void main(String[] args) throws Exception {
        Excel excel = new Excel();
        excel.data();
        excel.close();
    }

    /**
     * 所有数据
     * @param
     * @return
     */
   private HashMap<String, ArrayList<ArrayList<Object>>> data() {
        //把下面读取到的信息都存到里面
        // 原来的数组
        HashMap<String, ArrayList<ArrayList<Object>>> map = new HashMap<>();
       int[] arr = new int[]{0, 2};
       for (int k = 0; k < arr.length; k++) {
           int rowBegin = 0;
           sheet = wBook.getSheetAt(arr[k]);
           lastRowNum = sheet.getLastRowNum();
           if (k == 0) {
               rowBegin = 70;
           } else {
               rowBegin = 100;
           }
           for (int i = rowBegin; i <= lastRowNum; i++) {
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
                   if (cell == null || cell.equals("")) {
                       continue;
                   }
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
           yesType();
           alleleNameFilter();
       }
       return map;
    }

    /**
     * R1、R2 与 400
     */
    private void filter(HashMap<String, ArrayList<ArrayList<Object>>> map, double minDepth) {

        XSSFCellStyle red = wBook.createCellStyle();
        XSSFFont fontRed = wBook.createFont();
        fontRed.setColor(redColor);
        red.setFont(fontRed);



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
        int[] arr = new int[]{0, 2};
        for (int j = 0; j < arr.length; j++) {
            int rowEnd = 0;
            sheet = wBook.getSheetAt(arr[j]);
            lastRowNum = sheet.getLastRowNum();
            if (j == 0) {
                rowEnd = 66;
            } else {
                rowEnd = 96;
            }
            for (int i = 15; i <= rowEnd; i++) {
                //获取单元格
                XSSFRow row = sheet.getRow(i);
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
        }
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
        int[] arr = new int[]{0, 2};
        for (int j = 0; j < arr.length; j++) {
            int rowBegin = 0;
            sheet = wBook.getSheetAt(arr[j]);
            lastRowNum = sheet.getLastRowNum();
            if (j == 0) {
                rowBegin = 70;
            } else {
                rowBegin = 100;
            }
            for (int i = rowBegin; i <= lastRowNum ; i++) {
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

    }

    /**
     * 相同Allele Name的Yes的reads的六分之一
     * Locus、Allele Name、Reads
     */
    private void alleleNameFilter() {

        XSSFCellStyle orange = wBook.createCellStyle();
        XSSFFont fontOrange = wBook.createFont();
        fontOrange.setColor(orangeColor);
        orange.setFont(fontOrange);

        XSSFCellStyle blue = wBook.createCellStyle();
        XSSFFont fontBlue = wBook.createFont();
        fontBlue.setColor(blueColor);
        blue.setFont(fontBlue);

        XSSFCellStyle green = wBook.createCellStyle();
        XSSFFont fontGreen = wBook.createFont();
        fontGreen.setColor(greenColor);
        green.setFont(fontGreen);

        Map<String, HashMap<Double, Double>> locANReadsArrayList = new HashMap<>();
        HashMap<String, HashMap<Double,String>> locusAlleleRepeatSMap = new HashMap<>();
        int[] arr = new int[]{0, 2};
        for (int j = 0; j < arr.length; j++) {
            int rowBegin;
            int rowEnd;
            sheet = wBook.getSheetAt(arr[j]);
            lastRowNum = sheet.getLastRowNum();
            if (j == 0) {
                rowBegin = 70;
                rowEnd = 66;
            } else {
                rowBegin = 100;
                rowEnd = 96;
            }
            for (int i = rowBegin; i <= lastRowNum; i++) {
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
            for (int i = rowBegin; i <= lastRowNum; i++) {
                XSSFRow row = sheet.getRow(i);
                lastRowNum = sheet.getLastRowNum();
                if (row == null) {
                    continue;
                }
                String locus = row.getCell(0).getStringCellValue();
                if (row.getCell(1).getCellType() != CellType.NUMERIC) {
                    continue;
                }
                double alleleName = row.getCell(1).getNumericCellValue();
                if (locANReadsArrayList.containsKey(locus)
                        && locANReadsArrayList
                        .get(locus)
                        .containsKey(alleleName)
                        && row.getCell(3)
                        .getNumericCellValue()
                        < (locANReadsArrayList
                        .get(locus)
                        .get(alleleName) / 6)) {
                    row.getCell(2).setCellStyle(orange);
                }
                if (!locANReadsArrayList.containsKey(locus)) {
                    row.getCell(0).setCellStyle(blue);
                    locusAlleleRepeatSMap.put(locus,new HashMap<>());
                }

            }
            for (int i = rowBegin; i <= lastRowNum; i++) {
                XSSFRow row = sheet.getRow(i);
                String locus = row.getCell(0).getStringCellValue();
                if (row.getCell(1).getCellType() != CellType.NUMERIC) {
                    continue;
                }
                double alleleName = row.getCell(1).getNumericCellValue();
                String repeatS = row.getCell(5).getStringCellValue();
                if (locusAlleleRepeatSMap.containsKey(locus)) {
                    if (locusAlleleRepeatSMap.get(locus).size() >= 3) {

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
            for (int i = 15; i <= rowEnd; i++) {
                XSSFRow row = sheet.getRow(i);
                String locus = row.getCell(0).getStringCellValue();
                XSSFRichTextString xssfRichTextString = new XSSFRichTextString();
                XSSFRichTextString xssfRichTextString2 = new XSSFRichTextString();
                if (locusAlleleRepeatSMap.containsKey(locus)) {
                    if (row.getCell(1).getStringCellValue().equals("Y")) {
                        continue;
                    }
                    row.getCell(0).setCellStyle(green);
                    for (Double alleleName :
                            locusAlleleRepeatSMap.get(locus).keySet()) {
                        xssfRichTextString.append(alleleName.toString() + ",");
                        xssfRichTextString2.append(locusAlleleRepeatSMap.get(locus).get(alleleName) + ",");
                    }

                    row.getCell(1).setCellValue(xssfRichTextString);
                    row.getCell(2).setCellValue("");
                    row.getCell(3).setCellValue(xssfRichTextString2);
                }
                if (row.getCell(1) != null || !row.getCell(1).equals("")) {
                    count++;
                }
            }
            XSSFRow row = sheet.getRow(11);
            int sum = 0;
            if (j == 0) {
                sum = 52;
            } else {
                sum = 82;
            }
            row.getCell(1).setCellValue(count + "/" + sum);
        }
    }

    /**
     * 关闭资源
     * @throws Exception
     */
    private void close() throws Exception {
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        wBook.write(outputStream);
        outputStream.close();
        fis.close();
        wBook.close();
    }
}
