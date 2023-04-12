package demo;


import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class demo02 {


    @Test
    public void FileWriter() {
        /**
         * 写入文件：
         * 1、找到指定的文件
         * 2、根据文件创建文件的输出流
         * 3、把内容转换成字节数组
         * 4、向文件写入内容
         * 5、关闭输入流
         */
        File file = new File("E:" + File.separator + "hello.txt");
        OutputStream ous = null;
        try {
            //根据文件创建文件的输出流
            ous = new FileOutputStream(file);
            String message = "我是靓仔";
            //把内容转换成字节数组
            byte[] data = message.getBytes();
            //向文件写入内容
            ous.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                ous.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void test() {

        int[] arr = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        //先拿到第一个样本，获取所有的locus,sampleInfo.locusInfomations.getAutoStr().locus

        for (int i = 0; i < 10; i++) {//遍历样本数，List<sample>,get(index)
            //get(index),得到每一个sample，
            //createRow
           // for (int j = 0; j <  sampleInfo.locusInfomations.getAutoStr().length; j++) { //位点
                //sample..RepeatSequence
                //createCell
               // ArrayList<Object> sites = new ArrayList<>();
                //               if (site.getTyped().equalsIgnoreCase("yes") &&
//                        locusData.contains(site.getGenotype()) &&
//                        locusName.equalsIgnoreCase(site.getLocus())) {
//                    //三个条件同时满足就把rsq放到数组里面去
//                    //此时已经完成了排序、条件筛选
//                        sites.add(site.getRepeatSequence());
//                }
//            }
                //System.out.print(arr[j]);
                //createCell(j).setCellValue(sites.get[j])
            }
            System.out.println();
        }
//    }


    @Test
    public void FileReader() {
        /**
         * 读取文件：
         * 1、找到指定的文件
         * 2、根据文件创建文件的输入流
         * 3、创建字节数组
         * 4、把读取的文件放到字节数组里面
         */
        File file = new File("E:" + File.separator + "Hello.txt");
        InputStream ins = null;
        try {
            ins = new FileInputStream(file);
            byte[] data = new byte[1024];
           ins.read(data);
            System.out.println(new String(data));
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void FileCopy() {
        /**
         * 实现思路
         * 1、构建源文件与目标文件
         * 2、源文件创建输入流，目标文件创建输出流
         * 3、创建字节数组
         * 4、使用循环，源文件读取一部分内容，目标文件写入一部分内容，直到写完所有内容
         * 5、关闭源文件输入流、目标文件输出流
         */

        File file = new File("E:" + File.separator + "hello.txt");
        File fileCopy = new File("E:" + File.separator + "helloCopy.txt");

        InputStream ins = null;
        OutputStream ous = null;

        try {
            ins = new FileInputStream(file);
            ous = new FileOutputStream(fileCopy);
            byte[] temp = new byte[1024];
            int length = 0;
            while ((length = ins.read(temp)) != -1) {
                ous.write(temp,0,length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                ins.close();
                ous.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void ScannerWriter() {
        Scanner sc = new Scanner(System.in);
        String message;
        FileOutputStream fos = null;

        while (true) {
            try {
            System.out.println("请输入数据：");
            message = sc.nextLine();
            if (message.equals("exit")) {
                break;
            }

                fos = new FileOutputStream("E:\\scFile.txt", true);
                byte[] data = message.getBytes();
                fos.write(data);
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
