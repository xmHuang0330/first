import fayi.utils.ExcelUtils;
import fayi.utils.SetAException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class reagentDate {

    public static void main(String[] args) throws Exception {
        String[] chips = {"V300056231","V300048333","V300082265","V300082269","V300082997","V300083804","V300083805","V300087769"};
        String[] header = {"Index","板号","原始编号","项目号","性别","样本信息","建库试剂"};
        File excelPath = new File("/Users/kaidan/Desktop/forensic分析数据/");
        File[] xlsxes = excelPath.listFiles(pathname -> pathname.getName().endsWith("xlsx"));
        if(xlsxes == null){
            throw new Exception("文件夹下没有excel文件");
        }
        for(String chip:chips){
            File hasFile = null;
            for(File file:xlsxes){
                if(file.getName().contains(chip)){
                    hasFile = file;
                }
            }
            if(hasFile == null){
                System.out.println("file " + chip + " not Found..");
                System.exit(1);
            }
            FileInputStream fileInputStream = new FileInputStream(hasFile);
            XSSFWorkbook sheets = new XSSFWorkbook(fileInputStream);
            fileInputStream.close();
            for (int i = 1; i < 5; i++) {
                HashMap<String, ArrayList<String>> columnFromHeader
                        = getColumnFromHeader(sheets, "lane" + i, header);
                ArrayList<ArrayList<Object>> values = new ArrayList<>();


                ArrayList<Object> headerRow = new ArrayList<>();
                headerRow.addAll(Arrays.asList(header));
                values.add(headerRow);
                int max = 0;
                for(String head : header){
                    if (columnFromHeader.get(head).size() > max){
                        max = columnFromHeader.get(head).size();
                    }
                }
                for (int j = 0; j < max; j++) {
                    ArrayList<Object> lineRow = new ArrayList<>();
                    for(String head : header){
                        if(j >= columnFromHeader.get(head).size()) {
                            lineRow.add("");
                        }else{
                            lineRow.add(columnFromHeader.get(head).get(j));
                        }
                    }
                    values.add(lineRow);
                }
                FileOutputStream fileOutputStream = new FileOutputStream("/Users/kaidan/Downloads/" + chip + "_L0" + i + ".xlsx");
                XSSFWorkbook wb = new XSSFWorkbook();
                ExcelUtils.writeData(wb,"sheet1","",0,0,values);
                wb.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                wb.close();
            }
            sheets.close();

        }
    }


    private static HashMap<String,ArrayList<String>> getColumnFromHeader(XSSFWorkbook wb, String sheetName,String[] header) throws SetAException {

        XSSFSheet sheet = wb.getSheet(sheetName);
        if(sheet==null){
            throw new SetAException(1,"工作簿不存在sheet："+sheetName);
        }
        XSSFRow headerRow = sheet.getRow(0);
        HashMap<Integer,String> headerIndex = new HashMap<>();
        for(String str:header) {
            for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                if (ExcelUtils.getStringCellValue(headerRow.getCell(j)).equals(str)) {
                    headerIndex.put(j,str);
                }
            }
        }

        HashMap<String,ArrayList<String>> result = new HashMap<>();
        for(String head:header){
            result.put(head,new ArrayList<>());
        }

        for (int i = 1; i < sheet.getLastRowNum();i++) {
            XSSFRow row = sheet.getRow(i);
            if(row != null){
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    if(headerIndex.containsKey(j)){
                        result.get(headerIndex.get(j)).add(ExcelUtils.getStringCellValue(row.getCell(j)));
                    }
                }
            }
        }

        return result;
    }

}
