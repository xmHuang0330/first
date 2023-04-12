package demo;


import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class demo04 {

    //颜色
        static XSSFFont xssfFont = new XSSFFont();
    static {
        xssfFont.setColor(Font.COLOR_RED);
    }

     void Excel(int reads) throws IOException {
        //拿到文件
        FileInputStream fis = new FileInputStream("C:\\Users\\dr\\Desktop\\V350040329_L01_871_setB.xlsx");
        //不可以直接打开
        XSSFWorkbook wBook = new XSSFWorkbook(fis);
        fis.close();
        //获取工作表
        XSSFSheet sheet = wBook.getSheetAt(0);

        int lastRowNum = sheet.getLastRowNum();
        //把读取到的信息都存到里面
         // 原来的数组
        HashMap<String, ArrayList<ArrayList<Object>>> map = new HashMap<>();
        int count = 0;

        for (int i = 70; i <= lastRowNum; i++) {
            //获取单元格
            XSSFRow row = sheet.getRow(i);
            //如果为空，则说明该行没有数据
            if (row == null) {
                continue;
            }
            String locus = row.getCell(0).getStringCellValue();

            if (!map.containsKey(locus)) {
                map.put(locus,new ArrayList<>());
            }

            ArrayList<Object> values = new ArrayList<>();
            for (int j = 0; j < 6; j++) {
                XSSFCell cell = row.getCell(j);
                //如果是数字
                if (cell.getCellType() == CellType.NUMERIC) {
                    values.add(cell.getNumericCellValue()+"");
                } else {
                    values.add(cell.getStringCellValue());
                }
            }
            map.get(locus).add(values);

        }
        //原来的数组
         filter(map, reads);
        for (int i = 15; i <= 66; i++) {
            //获取单元格
            XSSFRow row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            //count最终得到的是全部不符合条件的总数量
            String locus = row.getCell(0).getStringCellValue();
            String genotype = row.getCell(3).getStringCellValue();
            ArrayList<ArrayList<Object>> orDefault = map.getOrDefault(locus, new ArrayList<>());
            if(orDefault.size() <1){
                count++;
                if (genotype != null && !genotype.equals("")) {
                    count--;

                }
                continue;
            }
            List<ArrayList<Object>> subList = orDefault.subList(0, Math.min(orDefault.size(), 3));

            String sequence = subList.stream().map(objects -> objects.get(5) + "").collect(Collectors.joining(","));

            String genotype1 = subList.stream().map(objects -> objects.get(1) + "").collect(Collectors.joining(","));

            /**
             * 判断哪些数据需要加上标记
             */
            StringBuilder marker = new StringBuilder();
            XSSFRichTextString xssfRichTextString = new XSSFRichTextString();
            for(ArrayList<Object> rowValues: subList){
                if(Double.valueOf((String)rowValues.get(3))<200){
                    //给需要标记的Genotype加个颜色
                    xssfRichTextString.append((String) rowValues.get(1),xssfFont);
                    //marker.append(rowValues.get(1)).append("<200;");
                    marker.append("===");
                }else{
                    xssfRichTextString.append((String) rowValues.get(1));
                }
            }
            row.getCell(1).setCellValue(xssfRichTextString);
            row.getCell(2).setCellValue(marker.toString());
            row.getCell(3).setCellValue(sequence);
        }
        //指定单元格设置值
         sheet.getRow(11).getCell(1).setCellValue(52 - count + "/52");
         FileOutputStream outputStream = new FileOutputStream("test.xlsx");
        wBook.write(outputStream);
        outputStream.close();
        wBook.close();
    }

    /**
     * 用来过滤满足条件的reads
     * 把符合条件的数据放到一个新的list里面
     * 把新的数组替换掉原来的数组
     * @param map
     * @param minDepth
     */
    private void filter(HashMap<String, ArrayList<ArrayList<Object>>> map , int minDepth){
         for (String locus : map.keySet()){
             ArrayList<ArrayList<Object>> data = map.get(locus);
             ArrayList<ArrayList<Object>> result = new ArrayList<>();
             for(ArrayList<Object> rows: data){
                 if(Double.valueOf((String)rows.get(3)) >= minDepth){
                     result.add(rows);
                 }
             }
             map.put(locus,result);
         }
    }

    public static void main(String[] args) throws IOException {
        int reads = 100;
       new demo04().Excel(reads);
    }

}
