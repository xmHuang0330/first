package fayi;

import fayi.entity.Classes;
import fayi.entity.Grade;
import fayi.entity.Root;
import fayi.entity.School;
import fayi.util.ExcelUtil;
import fayi.util.XMLUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


public class ExcelTest {
    public static void main(String[] args) {

        String path1 = Test.class.getClassLoader().getResource("./").getPath();
        String path = path1 + "config.xml";

        Root root = (Root) XMLUtil.convertXmlFileToObject(Root.class, path);
        String filePath = "E:\\TestXlsxDirectory";

            for (School school:root.getSchool()
            ) {
                //设置文件名
                String fileName = school.getName() + ".xlsx";

                File xlsxFile = null;
                xlsxFile = new File(filePath + File.separator + fileName);

                File parent = xlsxFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }

                try {
                    xlsxFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                for (Grade grade :
                        school.getGrade()) {
                    //表单名
                    String sheetName = grade.getName();
                    //标题栏
                    String[] title = {"班级", "班级信息", "班长", "学生数量"};
                    //声明表单内容
                    //+1是加上表头那一行
                    String[][] content = new String[grade.getClasses().size()][title.length];
                   // content[0] = title;
                   //遍历classes
                    for (int i = 0; i < grade.getClasses().size(); i++) {
                        //得到每一行的classes
                        Classes classes = grade.getClasses().get(i);
                        //拿到classes里面每一个属性
                        List<String> info1 = classes.getInfo();
                        String[] info = new String[info1.size()];
                        //将list转为数组
                        info1.toArray(info);
                        content[i] = info;
                    }
                        //获取文档
                        XSSFWorkbook workbook = ExcelUtil.getWorkbook(sheetName, title, content);
                        //声明输出流
                        FileOutputStream outputStream = null;
                        try {
                            //写到文件里面
                            outputStream = new FileOutputStream(filePath + "\\" + fileName);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        //响应到客户端
                        try {
                            workbook.write(outputStream);
                            outputStream.flush();
                            workbook.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                }
            }
    }
}
