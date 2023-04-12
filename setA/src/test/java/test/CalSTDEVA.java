package test;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CalSTDEVA {

    private static void calSTDEVA(double totalDepth, HashMap<String, Double> locusDepth,int locusCount) {
        double locusCoverage = totalDepth / locusDepth.size();
        ArrayList<Double> doubles = new ArrayList<>();
        for(double i:locusDepth.values()){
            doubles.add(i/locusCoverage);
        }
        double sum = 0d;
        for(double i:doubles){
            sum +=i;
        }
        double fangchaSum = 0d;
        for(double i:doubles){
            fangchaSum += (i-(sum/doubles.size()))*(i-(sum/doubles.size()));
        }
        System.out.println(Math.sqrt(fangchaSum/locusCount));
    }

    public static void main(String[] args) throws IOException {
        XSSFWorkbook sheets = new XSSFWorkbook("/Users/kaidan/Downloads/test.xlsx");
        XSSFSheet sheetAt = sheets.getSheetAt(0);
        HashMap<String, Double> hashMap1 = new HashMap<>();
        HashMap<String, Double> hashMap2 = new HashMap<>();
        HashMap<String, Double> hashMap3 = new HashMap<>();
        double totalDepth1 = 0;
        double totalDepth2 = 0;
        double totalDepth3 = 0;
        for (int i = 2; i < sheetAt.getLastRowNum()+1; i++) {
            XSSFRow row = sheetAt.getRow(i);
            totalDepth1+=row.getCell(1).getNumericCellValue();
            totalDepth2+=row.getCell(2).getNumericCellValue();
            totalDepth3+=row.getCell(3).getNumericCellValue();
            hashMap1.put(row.getCell(0).getStringCellValue(),row.getCell(1).getNumericCellValue());
            hashMap2.put(row.getCell(0).getStringCellValue(),row.getCell(2).getNumericCellValue());
            hashMap3.put(row.getCell(0).getStringCellValue(),row.getCell(3).getNumericCellValue());
        }
        calSTDEVA(totalDepth1,hashMap1,hashMap1.size()-1);
        calSTDEVA(totalDepth2,hashMap2,hashMap2.size()-1);
        calSTDEVA(totalDepth3,hashMap3,hashMap3.size()-1);
    }

}
