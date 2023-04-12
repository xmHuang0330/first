package poi;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.junit.jupiter.api.Test;
import java.io.FileOutputStream;
import java.util.Date;

public class demo01 {

    @Test
    public void create() throws Exception {
        String filePath = "C:\\Users\\dr\\Desktop\\testtt.xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet  sheet = workbook.createSheet("sheet1");
        FileOutputStream fos = new FileOutputStream(filePath);

        //创建行
        XSSFRow row = sheet.createRow(0);
        //创建行的单元格
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("张国荣");//设置单元格内容
        row.createCell(1).setCellValue(false);//设置单元格内，重载
        row.createCell(2).setCellValue(new Date());
        row.createCell(3).setCellValue(12.345);
        workbook.write(fos);
        fos.close();
        workbook.close();
    }

    /**
     * 设置格式
     * @throws Exception
     */
    @Test
    void test01() throws Exception {
        String filePath = "C:\\Users\\dr\\Desktop\\testt.xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet1 = workbook.createSheet("sheet1");
        FileOutputStream fos = new FileOutputStream(filePath);
        XSSFRow row = sheet1.createRow(0);

        //设置日期格式--使用Excel内嵌的格式
        XSSFCell cell = row.createCell(0);
        cell.setCellValue(new Date());
        XSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("m/d/yy h:mm"));
        cell.setCellStyle(style);

        //设置2位小数--使用Excel内嵌的格式
        cell = row.createCell(1);
        cell.setCellValue(12.3456789);
        style = workbook.createCellStyle();
        style.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cell.setCellStyle(style);

        //设置货币形式--使用自定义的格式
        cell = row.createCell(2);
        cell.setCellValue(1234.56789);
        style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("￥#，##0"));
        cell.setCellStyle(style);

        //设置百分比格式--使用自定义的格式
        cell = row.createCell(3);
        cell.setCellValue(0.1223455);
        style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        cell.setCellStyle(style);

        //设置中文大写格式--使用自定义的格式
        cell = row.createCell(4);
        cell.setCellValue(1234567898);
        style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("[Db2Num2][$-804]0"));
        cell.setCellStyle(style);

        //设置科学计算法格式--使用自定义的格式
        cell = row.createCell(5);
        cell.setCellValue(12345);
        style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("0.00E+00"));
        cell.setCellStyle(style);

        workbook.write(fos);
        fos.close();
        workbook.close();

    }

    /**
     * 合并单元格
     * new CellRangeAddress
     */

    @Test
    void test02() throws Exception {
        String filePath = "C:\\Users\\dr\\Desktop\\testt02.xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet1");
        FileOutputStream fos = new FileOutputStream(filePath);
        XSSFRow row = sheet.createRow(0);

        //合并列
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("合并列");
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, 5);
        sheet.addMergedRegion(region);

        //合并行
        cell = row.createCell(6);
        cell.setCellValue("合并行");

        region = new CellRangeAddress(0, 5, 6, 6);
        sheet.addMergedRegion(region);

        workbook.write(fos);
        fos.close();
        workbook.close();
    }

    /**
     * 单元格对齐
     *  style.setAlignment
     *  style.setVerticalAlignment
     */
    @Test
    void test03() throws Exception {
        String filePath = "C:\\Users\\dr\\Desktop\\testt02.xlsx";
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet03 = workbook.createSheet("sheet03");
        FileOutputStream fos = new FileOutputStream(filePath);

        HSSFRow row = sheet03.createRow(0);
        HSSFCell cell = row.createCell(3);
        cell.setCellValue("单元格对齐");
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.FILL);//水平居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        style.setWrapText(true); //自动换行
        style.setIndention((short) 5);//缩进
        style.setRotation((short) 60);//文本旋转，这里的取值是从-90到90，而不是0-180度
        cell.setCellStyle(style);

        workbook.write(fos);
        fos.close();
        workbook.close();

    }

    /**
     * 设置字体
     *  font.setFontName
     *   font.setFontHeightInPoints
     *   font.setUnderline
     *    font.setTypeOffset
     *    font.setStrikeout
     * @throws Exception
     */
    @Test
    public void Font() throws Exception {
        String filePath = "C:\\Users\\dr\\Desktop\\testt02.xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet04 = workbook.createSheet("sheet04");
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);

        XSSFRow row = sheet04.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("设置字体");

        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("华文行楷");//设置字体名称
        font.setFontHeightInPoints((short) 28);//设置字号
        font.setColor(Font.COLOR_RED);//设置字体颜色
        font.setUnderline(Font.U_SINGLE);//设置下划线
        font.setTypeOffset(Font.SS_SUPER);//设置上标下线
        font.setStrikeout(true);//设置删除线

        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);//水平居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        style.setFillForegroundColor(IndexedColors.BLUE.index);//图案颜色
        style.setFillBackgroundColor(IndexedColors.BLACK.index);//背景颜色
        cell.setCellStyle(style);

        workbook.write(fileOutputStream);
        fileOutputStream.close();
        workbook.close();

    }

    /**
     * 背景颜色
     * 背景图案
     *  style.setFillForegroundColor
     *   style.setFillBackgroundColor
     *    style.setFillPattern
     */
    @Test
    public void backGround() throws Exception {
        String filePath = "C:\\Users\\dr\\Desktop\\testt02.xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet04 = workbook.createSheet("sheet05");
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);

        XSSFRow row = sheet04.createRow(0);
        XSSFCell cell = row.createCell(0);

        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREEN.index);//图案颜色
        style.setFillBackgroundColor(IndexedColors.RED.index);//背景颜色
        style.setFillPattern(FillPatternType.THIN_FORWARD_DIAG);
        cell.setCellStyle(style);

        workbook.write(fileOutputStream);
        fileOutputStream.close();
        workbook.close();

    }

    /**
     * 设置高度和宽度
     *  sheet.setColumnWidth
     *   row.setHeightInPoints
     */
    @Test
    void Height() throws Exception {
        String filePath = "C:\\Users\\dr\\Desktop\\testt02.xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet01");
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);

        XSSFRow row = sheet.createRow(1);
        XSSFCell cell = row.createCell(1);
        cell.setCellValue("1234567890");
        sheet.setColumnWidth(1,31 * 256);//设置第一列的宽度是31个字符宽度
        row.setHeightInPoints(50);//设置行的高度是50个点

        workbook.write(fileOutputStream);
        fileOutputStream.close();
        workbook.close();
    }

    /**
     * 判断是否是日期类型
     *  DateUtil.isCellDateFormatted
     *  style.setDataFormat(BuiltinFormats.getBuiltinFormat("m/d/yy h:mm"));
     */
    @Test
    void isDate() throws Exception {
        String filePath = "C:\\Users\\dr\\Desktop\\testt02.xlsx";
        XSSFWorkbook workbook = new XSSFWorkbook();
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        XSSFSheet sheet = workbook.createSheet("sheet01");
        XSSFRow row = sheet.createRow(1);
        XSSFCell cell = row.createCell(1);
        cell.setCellValue(new Date());//设置日期数据
        System.out.println(DateUtil.isCellDateFormatted(cell));
        XSSFCellStyle style = workbook.createCellStyle();
        style.setDataFormat(BuiltinFormats.getBuiltinFormat("m/d/yy h:mm"));

        cell.setCellStyle(style);//设置日期样式
        System.out.println(DateUtil.isCellDateFormatted(cell));

        workbook.write(fileOutputStream);
        fileOutputStream.close();
        workbook.close();
    }


}
