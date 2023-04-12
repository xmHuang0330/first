package test;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
@SpringBootTest
public class type {

    @Test
    public void same() {

        try {
            FileInputStream fis = new FileInputStream("");
            XSSFWorkbook workbook = new XSSFWorkbook(fis);

        } catch (FileNotFoundException e) {
            log.info("输入文件有误");
        } catch (IOException e) {
            log.info("IO有问题");
        }

    }
}
