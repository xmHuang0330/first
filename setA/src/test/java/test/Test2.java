package test;

import fayi.utils.ExcelUtils;
import fayi.utils.Utils;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static fayi.utils.Utils.createFont;

public class Test2 {
    public final String A = "GTTTTTTTTTA";
    public final String B = "GTTTTTTTTAA";


    private void testRichTest() throws IOException {
        XSSFRichTextString xssfRichTextString = new XSSFRichTextString();
        String welcome = "Hello World!";

        for(char i : welcome.toCharArray()){
            if(i >= 110){
                xssfRichTextString.append(i+"",createFont("blue"));
            }else{

                xssfRichTextString.append(i+"",createFont("red"));
            }
        }

        XSSFWorkbook sheets = new XSSFWorkbook();
        XSSFSheet sheet = sheets.createSheet();
        sheet.createRow(0).createCell(0).setCellValue(xssfRichTextString);

        sheets.write(new FileOutputStream("/Users/kaidan/Downloads/test_richtext.xlsx"));
        sheets.close();
    }

    private void readSTRPattern() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("/Users/kaidan/Downloads/strpattern.xlsx");
        XSSFWorkbook sheets = new XSSFWorkbook(fileInputStream);
        ArrayList<ArrayList<String>> sheet1 = ExcelUtils.readData(sheets, "a", 0, 0, 0, 0);
        Iterator<ArrayList<String>> iterator = sheet1.iterator();
        while(iterator.hasNext()){
            ArrayList<String> values = iterator.next();
            String locus = values.get(1);
            String pattern = values.get(4);
            splitPattern(locus,pattern);
        }

    }

    private void splitPattern(String locus, String pattern) {
        boolean coreStart = false;
        StringBuilder core = new StringBuilder();

        HashMap<Integer, Integer> repeatCount = new HashMap<>();
        ArrayList<String> splitCore = new ArrayList<>();

        byte[] bytes = pattern.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            byte value = bytes[i];
            if (value == '['){
                if(!"".equals(core.toString())){
                    splitCore.add(core.toString());
                    core = new StringBuilder();
                }
                coreStart = true;
            }else if (value == ']'){
                splitCore.add(core.toString());
                core = new StringBuilder();
                if(97 <= bytes[i+1] && bytes[i+1] <= 122){
                    repeatCount.put(splitCore.size()-1,0);
                }else if(48 <= bytes[i+1] && bytes[i+1] <= 57){
                    repeatCount.put(splitCore.size()-1,Integer.parseInt((char)bytes[i+1]+""));
                }else{
                    System.err.println(locus + "核心重复次数异常：" + (char)bytes[i + 1]);
                }
                i += 1;
                coreStart = false;
            }else{
                if(coreStart){
                    core.append(value);
                }else{
                    if((char)value == 'N'){
                        StringBuilder nrepeat = new StringBuilder();
                        while(i+1 < bytes.length && 48 <= bytes[i+1] && bytes[i+1] <= 57){
                            nrepeat.append((char)bytes[i+1]);
                            i += 1;
                        }
                        if(!"".equals(nrepeat.toString())) {
                            splitCore.add("N");
                            repeatCount.put(splitCore.size(),Integer.parseInt(nrepeat.toString()));
                        }else{
                            System.err.println(locus + "： N 的重复次数有误");
                        }
                    }else if(97 <= bytes[i] && bytes[i] <= 122) {
                        core.append(value);
                    }
                }
            }
        }
        System.out.println(locus + ":" + repeatCount);
    }


}
