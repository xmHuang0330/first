package demo;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;

public class color {
    public static void main(String[] args) throws Exception {
        Workbook workbook = null;
        String excelType = "xls";
        switch (excelType) {
            case "xlsx": {
                XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
                workbook = xssfWorkbook;
                XSSFSheet sheet = xssfWorkbook.createSheet();
                XSSFCellStyle cellStyle = xssfWorkbook.createCellStyle();
                XSSFFont font = xssfWorkbook.createFont();
                font.setColor(new XSSFColor(new Color(255,0,0), null));
                cellStyle.setFont(font);
                cellStyle.setFillForegroundColor(new XSSFColor(new Color(0,255,127), null));
                cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                XSSFRow row = sheet.createRow(0);
                for (int i = 0; i < 4; i++) {
                    XSSFCell cell = row.createCell(i);
                    cell.setCellValue("dct" + i);
                    cell.setCellStyle(cellStyle);
                }
                break;
            }
            case "xls": {
                byte paletteIndex = 0x8;
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
                workbook = hssfWorkbook;
                HSSFSheet sheet = hssfWorkbook.createSheet();
                HSSFCellStyle cellStyle1 = hssfWorkbook.createCellStyle();
                HSSFFont font = hssfWorkbook.createFont();
                HSSFPalette customPalette = hssfWorkbook.getCustomPalette();
                int fontColor = 0xff591f;
                byte[] fontColorBytes = hexColorToBytes(fontColor);
                customPalette.setColorAtIndex(paletteIndex, fontColorBytes[0], fontColorBytes[1], fontColorBytes[2]);
                font.setColor(paletteIndex);
                paletteIndex++;
                cellStyle1.setFont(font);
                int backgroundColor = 0xffcd91;
                byte[] backgroundColorBytes = hexColorToBytes(backgroundColor);
                customPalette.setColorAtIndex(paletteIndex, backgroundColorBytes[0], backgroundColorBytes[1], backgroundColorBytes[2]);
                cellStyle1.setFillForegroundColor(paletteIndex);
                cellStyle1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                HSSFCellStyle cellStyle2 = hssfWorkbook.createCellStyle();
                cellStyle2.setFillForegroundColor(IndexedColors.BLACK.index);
                HSSFRow row = sheet.createRow(0);
                for (int i = 0; i < 8; i++) {
                    if (i < 4) {
                        HSSFCell cell = row.createCell(i);
                        cell.setCellValue("dct" + i);
                        cell.setCellStyle(cellStyle1);
                    } else {
                        HSSFCell cell = row.createCell(i);
                        cell.setCellValue("black" + i);
                        cell.setCellStyle(cellStyle1);
                    }
                }
                break;
            }
        }
        FileOutputStream fileOutputStream = new FileOutputStream(new File("test_" + System.currentTimeMillis() + "." + excelType));
        workbook.write(fileOutputStream);
        workbook.close();
    }

    public static byte[] hexColorToBytes(int hexColor) {
        byte[] rgb = new byte[3];
        int red = (hexColor & 0xff0000) >> 16;
        int green = (hexColor & 0x00ff00) >> 8;
        int blue = hexColor & 0x0000ff;
        rgb[0] = (byte) (red);
        rgb[1] = (byte) (green);
        rgb[2] = (byte) (blue);
        return rgb;
    }
}
