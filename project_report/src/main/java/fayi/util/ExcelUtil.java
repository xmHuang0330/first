package fayi.util;

import fayi.xml.Objects.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.*;

public class ExcelUtil {

    public static XSSFWorkbook getWorkbook (String sheetname, String[] title, String[][] content){

        //新建文档实例
        XSSFWorkbook workbook = new XSSFWorkbook();

        //在文档中新建表单
        XSSFSheet sheet = workbook.createSheet(sheetname);

        //创建单元格格式，并设置居中
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);

        //创建第一行，用于填充标题
        XSSFRow titleRow = sheet.createRow(0);

        //填充标题
        for (int i = 0; i < title.length; i++) {
            XSSFCell cell = titleRow.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }

        //填充内容
        for (int i = 0; i < content.length; i++) {
            XSSFRow row = sheet.createRow(i + 1);
            for (int j = 0; j < content[i].length; j++) {
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(content[i][j]);
                cell.setCellStyle(style);
            }
        }
        //返回文档实例
        return workbook;
    }

    public static void singleReportFromTemplate ( Sample sampleInfo) throws IOException {

        FileInputStream fileInputStream = new FileInputStream(new File("C:\\Users\\dr\\Desktop\\report_template.xlsx"));
        //新建文档实例
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        fileInputStream.close();
        XSSFSheet sheet = workbook.getSheet("Sheet1");


        //创建第一行，用于填充标题
        XSSFCell nameCell = sheet.getRow(0).createCell(1);
        nameCell.setCellValue(sampleInfo.basicInfo.name);
        XSSFCell fq = sheet.getRow(1).createCell(1);
        fq.setCellValue(sampleInfo.basicInfo.fastq);

        //排序
        //重写了compareTo方法
        Collections.sort(sampleInfo.sites.strSites);
        int row = 4;
        for (LocusData locusData :
                sampleInfo.locusInfomations.getAutoStr()) {
            String locusName = locusData.getLocusName();
            String alleleNameAsString = locusData.getAlleleNameAsString();
            Integer totalDepth = locusData.getTotalDepth();

            //动态表头
            ArrayList<Object> header = new ArrayList<>();
            header.add(locusName);

            ArrayList<String> sites = new ArrayList<>();
            int cell = 0;
            for (Site site :
                    sampleInfo.sites.strSites) {
                cell++;
                //三个条件同时满足
                //写了新的包含方法contains
                if (site.getTyped().equalsIgnoreCase("yes") &&
                        locusData.contains(site.getGenotype()) &&
                        locusName.equalsIgnoreCase(site.getLocus())) {
                    //三个条件同时满足就把rsq放到数组里面去
                    //此时已经完成了排序、条件筛选
                        sites.add(site.getRepeatSequence());
                }
            }

            List<Object> obj = Arrays.asList(locusName, totalDepth, alleleNameAsString, String.join(",", sites));

            XSSFRow row2 = sheet.getRow(row);

            ArrayList<Object> lNames = new ArrayList<>();
            lNames.add(locusName);

            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.add(header);
            arrayList.add(sites);

            //如果行为空，就创建
            if (row2 == null) {
                row2 = sheet.createRow(row);
            }

            for (int i = 0; i < obj.size(); i++) {
                //创建列
                XSSFCell cell1 = row2.createCell(i);
                Object o = obj.get(i);
                if (o instanceof Integer){
                    cell1.setCellValue((Integer)o );
                }else{
                    cell1.setCellValue((String)o );
                }
            }
            row++;
        }
        FileOutputStream fos = new FileOutputStream(new File("result.xlsx"));
        workbook.write(fos);
        fos.close();
        workbook.close();
    }


    public static void allFromTemplate ( List<Sample> samples) throws IOException {

        FileInputStream fileInputStream = new FileInputStream(new File("C:\\Users\\dr\\Desktop\\test.xlsx"));
        //新建文档实例
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        fileInputStream.close();
        XSSFSheet sheet = workbook.getSheet("Sheet1");
        int row = 1;

        //把所有的locus放到一个list里面
        ArrayList<String> locus = new ArrayList<>();
        for (LocusData locusData : samples.get(0).locusInfomations.getAutoStr()) {
            locus.add(locusData.getLocusName());
        }

        //得到每一个sample，从每一个sample里面拿序列
        for (Sample sample :
                samples) {
            int cell = 1;
            ArrayList<LocusData> autoStr = sample.locusInfomations.getAutoStr();
            //一个sample为一行
            XSSFRow row1 = sheet.createRow(row);

            //排序
            Collections.sort(sample.sites.strSites);

            for (String locusName :
                    locus) {
                Optional<LocusData> first = autoStr.stream().filter(locusData -> locusName.equals(locusData.getLocusName())).findFirst();
                if (!first.isPresent()){
                    row1.createCell(cell++).setCellValue("");
                    continue;
                }
                LocusData locusData = first.get();

                ArrayList<String> sites = new ArrayList<>();
                for (Site site :
                        sample.sites.strSites) {
                    //三个条件同时满足
                    //写了新的包含方法contains
                    if (site.getTyped().equalsIgnoreCase("yes") &&
                            locusData.contains(site.getGenotype()) &&
                            locusName.equalsIgnoreCase(site.getLocus())) {
                        //三个条件同时满足就把rsq放到数组里面去
                        //此时已经完成了排序、条件筛选
                        sites.add(site.getRepeatSequence());
                    }
                }
                    row1.createCell(cell++).setCellValue(String.join(",", sites));
            }
            row++;
        }
        FileOutputStream fos = new FileOutputStream(new File("result1.xlsx"));
        workbook.write(fos);
        fos.close();
        workbook.close();
    }
}
