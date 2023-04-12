package poi;

import org.apache.poi.hssf.usermodel.DVConstraint;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

public class demo02 {

    /**
     * 基本计算
     * setCellFormula
     * @throws Exception
     */
    @Test
    void calculate() throws Exception {
        String filePath = "C:\\Users\\dr\\Desktop\\testt02.xlsx";
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet1");
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellFormula("2+3+4");//设置公式
        cell = row.createCell(1);
        cell.setCellValue(10);
        cell = row.createCell(2);
        cell.setCellValue(2);
        cell.setCellFormula("A1*B1");

        workbook.write(fileOutputStream);
        fileOutputStream.close();
        workbook.close();
    }

    /**
     * sum函数
     * @throws Exception
     */
    @Test
    void Sum() throws Exception {
        String filePath = "C:\\Users\\dr\\Desktop\\testt02.xlsx";
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("sheet");

        XSSFRow row = sheet.createRow(1);
       row.createCell(1).setCellValue(1);
       row.createCell(2).setCellValue(2);
       row.createCell(3).setCellValue(3);
       row.createCell(4).setCellValue(4);
       row.createCell(5).setCellValue(5);
       row = sheet.createRow(2);
       row.createCell(1).setCellFormula("sum(B2,C2)");//等价于"A1+C1"
       row.createCell(2).setCellFormula("sum(B2:D2)");

       workbook.write(fileOutputStream);
       fileOutputStream.close();
       workbook.close();
    }

    /**
     * 添加图片
     * @throws Exception
     */
    @Test
    void Pic() throws Exception {
        String filePath = "C:\\Users\\dr\\Desktop\\testt02.xlsx";
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        //图片路径
        FileInputStream fileInputStream = new FileInputStream("C:\\Users\\dr\\Desktop\\th.jpg");
        //读取图片到二进制数组
        byte[] bytes = new byte[(int) fileInputStream.getChannel().size()];
        fileInputStream.read(bytes);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet();
        int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);//图片类型
        XSSFDrawing patriarch = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) 0, 0, (short) 4, 22);
        XSSFPicture pict = patriarch.createPicture(anchor, pictureIdx);
        //pict.resize();//自动调节图片大小，图片位置信息可能丢失

        workbook.write(fileOutputStream);
        fileOutputStream.close();
        fileInputStream.close();
        workbook.close();
    }

    /**
     * 从Excel读取图片
     * @throws Exception
     */
    @Test
    void readPic() throws Exception {
        FileInputStream fis = new FileInputStream("C:\\Users\\dr\\Desktop\\testt02.xlsx");
        XSSFWorkbook workbook = new XSSFWorkbook(fis);//读取现有的Excel文件
        List<XSSFPictureData> pictures = workbook.getAllPictures();

        for (int i = 0; i < pictures.size(); i++) {
            XSSFPictureData pic = pictures.get(i);
            String ext = pic.suggestFileExtension();
            //判断文件格式
            if (ext.equals("jpg")) {
                FileOutputStream jpg = new FileOutputStream("C:\\Users\\dr\\Desktop\\th1.png");
                jpg.write(pic.getData());
                jpg.close();
            }
        }
    }

    /**
     * 遍历sheet
     * @throws Exception
     */
    @Test
    void Iterator() throws Exception {
        String filePath = "C:\\Users\\dr\\Desktop\\testt02.xlsx";
        FileInputStream fis = new FileInputStream(filePath);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);//读取现有的Excel
        XSSFSheet sheet = workbook.getSheet("sheet0");
        for (Row row :
                sheet) {
            for (Cell cell :
                    row) {
                System.out.print(cell + " \t");
            }
            System.out.println();
        }
    }
}
