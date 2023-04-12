package demo;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class demo05 {

    public static void main(String[] args) throws Exception {
        int reads = 200;
        new demo05().Excel(reads);
    }

    static XSSFFont xssfFont = new XSSFFont();
    static {
        xssfFont.setColor(Font.COLOR_RED);
    }

    void Excel(int reads) throws Exception {

        FileInputStream fis = new FileInputStream("C:\\Users\\dr\\Desktop\\V300113002_L01_962_Homgen_Yeasen_Vazyme_Test-2_setB.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        fis.close();

        //获取工作表
        XSSFSheet sheet = workbook.getSheetAt(0);
        int lastRowNum = sheet.getLastRowNum();
        int count = 0;

        //获取读到的信息
        HashMap<String, ArrayList<ArrayList<Object>>> map = new HashMap<>();
        for (int i = 70; i <= lastRowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            //如果为空，说明该行没有数据
            if (row == null) {
                continue;
            }
            //拿到所有的位点
            String locus = row.getCell(0).getStringCellValue();
            if (!map.containsKey(locus)) {
                map.put(locus, new ArrayList<>());
            }
            ArrayList<Object> values = new ArrayList<>();
            for (int j = 0; j < 6; j++) {
                XSSFCell cell = row.getCell(j);
                if (cell.getCellType() == CellType.NUMERIC) {
                    values.add(cell.getNumericCellValue() + "");
                } else {
                    values.add(cell.getStringCellValue());
                }
            }
            map.get(locus).add(values);
        }
        //进行条件过滤
        filter(map, reads);
        for (int i = 15; i < 66; i++) {
            XSSFRow row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            String locus = row.getCell(0).getStringCellValue();
            String genotype = row.getCell(3).getStringCellValue();
            ArrayList<ArrayList<Object>> orDefault = map.getOrDefault(locus, new ArrayList<>());
            if (orDefault.size() < 1) {
                count++;
                if (genotype != null && !genotype.equals("")) {
                    count--;
                }
                continue;
            }
            List<ArrayList<Object>> subList = orDefault.subList(0, Math.min(orDefault.size(), 3));
            String sequence = subList.stream().map(objects -> objects.get(5) + "").collect(Collectors.joining(","));

            StringBuilder marker = new StringBuilder();
            XSSFRichTextString xssfRichTextString = new XSSFRichTextString();
            for (ArrayList<Object> rowValues :
                    subList) {
                if (Double.valueOf((String) rowValues.get(3)) < 300) {
                    xssfRichTextString.append((String) rowValues.get(1), xssfFont);
                    marker.append("===");
                } else {
                    xssfRichTextString.append((String)rowValues.get(1));
                }
            }
            row.getCell(1).setCellValue(xssfRichTextString);
            row.getCell(2).setCellValue(marker.toString());
            row.getCell(3).setCellValue(sequence);
        }
        sheet.getRow(11).getCell(1).setCellValue(52 - count + "/52");
        FileOutputStream out = new FileOutputStream("test01.xlsx");
        workbook.write(out);
        out.close();
        workbook.close();

    }

    private void filter(HashMap<String, ArrayList<ArrayList<Object>>> map, int minDepth) {
        for (String locus :
                map.keySet()) {
            ArrayList<ArrayList<Object>> data = map.get(locus);
            ArrayList<ArrayList<Object>> result = new ArrayList<>();
            for (ArrayList<Object> rows :
                    data) {
                if (Double.valueOf((String) rows.get(3)) >= minDepth) {
                    result.add(rows);
                }
            }
            map.put(locus, result);
        }
    }
}
