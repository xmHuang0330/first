package test;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fayi.Analyse;
import fayi.config.Config;
import fayi.tableObject.StrInfo;
import fayi.tableObject.StrLocusInfo;
import fayi.utils.ExcelUtils;
import fayi.utils.SetAException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFTableStyleInfo;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Demonstrates how to create a simple table using Apache POI.
 */

@SpringBootTest(classes = {fayi.APP.class})
@ActiveProfiles("test")
public class test1 {

    public static void main12(String[] args) {
        String pattern = "V[0-9]{9}(_L){1}[0-9]{2}_";
        Pattern compile = Pattern.compile( pattern );
        Matcher matcher = compile.matcher( "V300036999_L04_585.fq.gz" );
        matcher.find();
        System.out.println(matcher.group(  ));
    }
    public static void main1(String[] args) throws IOException {

        try (Workbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = (XSSFSheet) wb.createSheet();

            // Set which area the table should be placed in
            AreaReference reference = wb.getCreationHelper().createAreaReference(
                    new CellReference(0, 0), new CellReference(2, 2));

            // Create
            XSSFTable table = sheet.createTable(reference); //creates a table having 3 columns as of area reference
            // but all of those have id 1, so we need repairing
            table.getCTTable().getTableColumns().getTableColumnArray(1).setId(2);
            table.getCTTable().getTableColumns().getTableColumnArray(2).setId(3);

            table.setName("Test");
            table.setDisplayName("Test_Table");

            // For now, create the initial style in a low-level way
            table.getCTTable().addNewTableStyleInfo();
            table.getCTTable().getTableStyleInfo().setName("TableStyleMedium2");

            // Style the table
            XSSFTableStyleInfo style = (XSSFTableStyleInfo) table.getStyle();
            style.setName("TableStyleMedium2");
            style.setShowColumnStripes(false);
            style.setShowRowStripes(true);
            style.setFirstColumn(false);
            style.setLastColumn(false);
            style.setShowRowStripes(true);
            style.setShowColumnStripes(true);

            // Set the values for the table
            XSSFRow row;
            XSSFCell cell;
            for (int i = 0; i < 3; i++) {
                // Create row
                row = sheet.createRow(i);
                for (int j = 0; j < 3; j++) {
                    // Create cell
                    cell = row.createCell(j);
                    if (i == 0) {
                        cell.setCellValue("Column" + (j + 1));
                    } else {
                        cell.setCellValue((i + 1.0) * (j + 1.0));
                    }
                }
            }

            // Save
            try (FileOutputStream fileOut = new FileOutputStream("F:/ooxml-table.xlsx")) {
                wb.write(fileOut);
            }
        }
    }

    @Autowired
    Analyse analyse;

    @Test
    public void test() throws Exception {
        Config con = Config.getInstance();

//        String pattern = "^([ATCG]+AAAGAAGG(AAGG)+)((AAAGAAGG)+)$";
//        Pattern compile = Pattern.compile(pattern);
//        String sequence = "GGAAGGAAGGAAGGAAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAAGAAAGAAAGAAAGAAGGAAAGAAGGAAGGAAAGAAGGAAAGAAGGAAAGAAGGAAAGGGAAAGGAAAGG";
//        String substring = sequence.substring(0, sequence.length() - 32);
//
//        Matcher matcher = compile.matcher(substring);
//        if(matcher.find()){
//            System.out.println(matcher.group());
//            System.out.println(matcher.groupCount());
//            String front = matcher.group(3);
//            String tail = matcher.group(1);
//            System.out.println(front);
//            System.out.println(tail);
//        }

        con.setOutput("/Users/kaidan/Downloads/PE300-150/work/L05/L05_result.xml");
        con.setNoRazor(true);
        analyse.start("/Users/kaidan/Downloads/PE300-150/work/L05/L05.xml");
    }

    public static void main(String[] args) throws SetAException, IOException {
        Config instance = Config.getInstance();
        FileInputStream fileInputStream = new FileInputStream(new File(instance.getStrConfigXlsx()));
        XSSFWorkbook sheets = new XSSFWorkbook(fileInputStream);
        fileInputStream.close();


        XSSFSheet sheetAt = sheets.getSheetAt(0);
        Iterator<Row> rowIterator = sheetAt.rowIterator();
        HashMap<String, ArrayList<String>> locusSTR = instance.getParam().locusSTR;
        while (rowIterator.hasNext()){
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            String locus = cellIterator.next().getStringCellValue();
            locusSTR.put(locus,new ArrayList<>());
            while(cellIterator.hasNext()){
                Cell next = cellIterator.next();
                String stringCellValue = next.getStringCellValue().trim();
                if(!"".equals(stringCellValue)) {
                    locusSTR.get(locus).add(stringCellValue);
                }
            }
//            System.out.println(locus+" | "+locusSTR.get(locus));
        }
    }
}