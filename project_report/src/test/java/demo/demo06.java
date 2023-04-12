package demo;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;

public class demo06 {

    @Test
    void test01() throws Exception {
        String filePath = "E:\\SHARE\\IdeaProjects\\project_report\\result.xlsx";
        FileInputStream fis = new FileInputStream(filePath);
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        XSSFSheet sheet1 = workbook.getSheet("sheet1");
        for (Row row :
                sheet1) {
            for (Cell cell :
                    row) {
                System.out.print(cell + "\t");
            }
            System.out.println();
        }

    }
}
