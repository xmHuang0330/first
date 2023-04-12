package fayi;

import fayi.entity.Classes;
import fayi.entity.Grade;
import fayi.entity.Root;
import fayi.entity.School;
import fayi.util.XMLUtil;

import java.io.*;

public class Test {
    public static void main(String[] args) {
        String path1 = Test.class.getClassLoader().getResource("./").getPath();
        String path = path1 + "config.xml";
        Root root = (Root) XMLUtil.convertXmlFileToObject(Root.class, path);
        if (root.getSchool() != null && root.getSchool().size() > 0) {
            //表格头
            String[] header = new String[]{"班级","班长","班级信息","学生人数"};
            //csv文件路径
            String filePath = "E:\\TestCsvDirectory";
            for (School r:root.getSchool()
                 ) {
                String fileName = "Csv_" + r.getName() +".csv";//文件名称
                File csvFile = null;
                BufferedWriter csvWriter = null;

                try {
                    csvFile = new File(filePath + File.separator + fileName);
                    File parent = csvFile.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }
                    csvFile.createNewFile();
                    csvWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GB2312"), 1024);

                    csvWriter.write(String.join(",", header));
                    csvWriter.newLine();

                    for (Grade grade: r.getGrade()
                         ) {
                        for (Classes c:grade.getClasses()
                             ) {
                            csvWriter.write(String.join(",",c.getInfo()));

                            csvWriter.newLine();
                        }

                    }
                    csvWriter.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    try {
                        csvWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


        }
    }
}
