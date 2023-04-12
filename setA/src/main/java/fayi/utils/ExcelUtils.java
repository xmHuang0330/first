package fayi.utils;

import fayi.tableObject.FormulaString;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTTableColumns;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/*
    excel工具
 */
public class ExcelUtils {

    //从表中指定的行列读取数据，string类型二维表
    public static ArrayList<ArrayList<String>> readData(XSSFWorkbook wb, String sheetName, int startRow, int endRow, int startCol, int endCol) {
        XSSFSheet sheet;
        if("".equals(sheetName)){
            sheet = wb.getSheetAt(0);
        }else {
            sheet = wb.getSheet(sheetName);
        }

        if(endRow == 0){
            endRow = sheet.getLastRowNum();
        }

        ArrayList<ArrayList<String>> data = new ArrayList<>();
        for (int i = startRow; i <= endRow; i++) {
            ArrayList<String> values = new ArrayList<>();
            XSSFRow row = sheet.getRow(i);
            if(row == null) continue;
            if(endCol == 0){
                endCol = row.getLastCellNum();
            }
            for (int j = startCol; j <= endCol; j++) {
                XSSFCell cell = row.getCell(j);
                values.add( getStringCellValue( cell ) );

            }
            data.add(values);
        }
        return data;
    }
    //列数转变为公式代码，如 第三列3->C 第25列->AA
    public static String index2ColName(int index) {
        if (index < 0) {
            return null;
        }
        int num = 65;
        String colName = "";
        do {
            if (colName.length() > 0) {
                index--;
            }
            int remainder = index % 26;
            colName = ((char) (remainder + num)) + colName;
            index = (index - remainder) / 26;
        } while (index > 0);
        return colName;
    }
    //生成表格区域的公式代码，如 A1:E5
    public static String createAreaRefString(Integer startRow, Integer endRow, Integer startCol, Integer endCol) {
        return index2ColName(startCol) + "" + startRow + ":" + index2ColName(endCol) + "" + endRow;
    }

    // 默认转换数字字符串到string类型
    public static void writeData(XSSFWorkbook wb, String sheetName, String tableName, Integer startRow, Integer startCol, ArrayList<ArrayList<Object>> data) {
        writeData(wb,sheetName,tableName,startRow,startCol,data,true);
    }

        //把数据写入excel表
    public static void writeData(XSSFWorkbook wb, String sheetName, String tableName, Integer startRow, Integer startCol, ArrayList<ArrayList<Object>> data, boolean convertDigitString) {

        if (null == data || data.size() == 0) {
            return;
        }
        XSSFSheet sheet = wb.getSheet(sheetName);
        if (sheet == null) {
            sheet = wb.createSheet(sheetName);
        }
        int rowLength = getMaxRowLength(data);
        //如果有给定的表格且表格存在，根据data，修改表格大小
        for (XSSFTable table: sheet.getTables()) {
            if (table.getName().equals(tableName)) {
//                cleanTable(table);
                AreaReference areaReference = wb.getCreationHelper().createAreaReference(
                        createAreaRefString( startRow, data.size() + startRow, startCol, startCol + rowLength - 1 ));
                table.setArea( areaReference );
                CTTableColumns tableColumns = table.getCTTable().getTableColumns();
                for (int a = 0; a < tableColumns.sizeOfTableColumnArray(); a++) {
                    tableColumns.getTableColumnArray(a).setId( a+1 );
                }
            }
        }

//        FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
        //写入表格
        XSSFRow row;
        XSSFCell cell;
        List<ArrayList<Object>> collect = data.stream().filter( Objects::nonNull ).collect( Collectors.toList() );
        for (int i = 0; i < collect.size(); i++) {
            row = sheet.getRow(i + startRow);
            if(row == null) {
                row = sheet.createRow(i + startRow);
            }
            ArrayList<Object> sub_data = collect.get(i);
            if(null == sub_data){
                continue;
            }
            for (int j = 0; j < rowLength; j++) {
                cell = row.getCell(j + startCol);
                if(cell == null){
                    cell = row.createCell(j + startCol);
                }
                if(j >= sub_data.size()){
                    continue;
                }
                Object o = sub_data.get(j);
                if (o instanceof Integer) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue((Integer) o);
                }else if(o instanceof Float){
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue((Float) o);
                } else if (o instanceof Boolean){   //是、否
                    cell.setCellValue((Boolean) o ? "Yes" : "No");  //Typed Allele?
                } else if (o instanceof String) {
                    String str = (String) o;
                    if(!convertDigitString || str.matches( "\\d{1,2}[DF]" )){
                        cell.setCellValue(str);
                        continue;
                    }
                    if(NumberUtils.isDigits(str)){
                        try {
                            cell.setCellValue(Integer.parseInt(str));
                        }catch (NumberFormatException e){
                            cell.setCellValue(str);
                        }
                    }else {
                        try {
                            double aDouble = Double.parseDouble(str);
                            cell.setCellValue(aDouble);
                        }catch (NumberFormatException e){
                            cell.setCellValue(str);  //String Value
                        }
                    }
                } else if (o instanceof Double) {
                    cell.setCellType(CellType.NUMERIC);
                    cell.setCellValue((Double) o);
                }else if (o == null){
                    cell.setCellValue("");
                }else if (o instanceof FormulaString){  //公式
//                    cell.setCellType( CellType.FORMULA );
                    cell.setCellFormula(o.toString());
                }else if(o instanceof XSSFRichTextString){
                    cell.setCellValue((XSSFRichTextString)o);
                }else {
                    cell.setCellValue(o.toString());
                }
            }
        }
    }

    //为了防止数据每行长短不一样，先获取最大长度
    private static int getMaxRowLength(ArrayList<ArrayList<Object>> data) {
        int max = 0;
        for(ArrayList<Object> values:data){
            if(null == values) continue;
            if(values.size() > max){
                max = values.size();
            }
        }
        return max;
    }

    //清除表格中的内容
    private static void cleanTable(XSSFTable table) {
        XSSFRow row;
        XSSFCell cell;
        XSSFSheet sheet = table.getXSSFSheet();
        for (int i = table.getStartRowIndex()+1; i < table.getEndRowIndex(); i++) {
            row = sheet.getRow( i );
            if(null != row){
                for (int j = table.getStartRowIndex(); j < table.getEndRowIndex(); j++) {
                    cell = row.getCell( j );
                    if(null != cell){
                        cell.setCellType( CellType.STRING );
                        cell.setCellValue( "" );
                    }
                }
            }
        }
    }
    //将单元格数据转换为字符串
    public static String getStringCellValue(Cell cell){
        if(null == cell){
            return "";
        }
        CellType cellType = cell.getCellType();
        switch (cellType){
            case NUMERIC: return NumberFormat.getInstance().format( cell.getNumericCellValue() ).replaceAll( ",","" );
            case STRING: return cell.getStringCellValue().trim();
            case BOOLEAN: return cell.getBooleanCellValue()?"Yes":"No";
            case FORMULA: return cell.getCellFormula();
            default: return "";
        }
    }


    public static void writeHeader(XSSFWorkbook wb, String sheetName, int startRow, int startCol, ArrayList<String> header) {

        if (null == header || header.size() == 0) {
            return;
        }
        XSSFSheet sheet = wb.getSheet(sheetName);
        if (sheet == null) {
            sheet = wb.createSheet(sheetName);
        }
        //写入表格
        XSSFRow row = sheet.getRow(startRow);
        if(row == null) {
            row = sheet.createRow(startRow);
        }
        XSSFCell cell;
        for (int i = 0; i < header.size(); i++) {
            cell = row.getCell(i + startCol);
            if(cell == null){
                cell = row.createCell(i + startCol);
            }
            String value = header.get( i );
            cell.setCellValue( value );
        }
    }

}
