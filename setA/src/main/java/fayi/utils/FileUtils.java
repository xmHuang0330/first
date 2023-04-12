package fayi.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.zip.GZIPInputStream;

/*
    读写文件工具
 */
@Slf4j
public class FileUtils {

    private FileInputStream stream;
    private InputStreamReader inputStreamReader;
    private BufferedReader bufferedReader;

    private FileOutputStream fileOutputStream;
    private OutputStreamWriter outputStreamWriter;
    private BufferedWriter bufferedWriter;
    private final String File;
    private final String Encode = System.getProperty("file.encoding");

    public FileUtils(String file) {
        File = file;
    }

    public FileUtils(File file) {
        File = file.getAbsolutePath();
    }

    private void setBufferedReader() {
        try {
            Utils.checkReadFile(File);
            stream = new FileInputStream(File);
            inputStreamReader = new InputStreamReader(stream, Encode);
            bufferedReader = new BufferedReader(inputStreamReader);
        } catch (UnsupportedEncodingException | SetAException | FileNotFoundException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private void setBufferedWriter() {
        try {
            fileOutputStream = new FileOutputStream( File );
            outputStreamWriter = new OutputStreamWriter( fileOutputStream,Encode );
            bufferedWriter = new BufferedWriter( outputStreamWriter );
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            log.error("文件输出流创建失败：" + File);
            e.printStackTrace();
        }
    }

    public void deleteOriginalFileIfExists(){
        File file = new File(File);
        if(file.exists()){
            file.delete();
        }
    }

    public String readLine() {
        if(bufferedReader == null) {
            setBufferedReader();
        }
        String s = null;
        try {
            s = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
    public void writeLine(String line){
        if(bufferedWriter == null){
            setBufferedWriter();
        }
        try {
            bufferedWriter.write( line );
            bufferedWriter.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void finishWrite() {
        if(bufferedWriter == null){
            return;
        }
        try {
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void finishRead() {
        if(bufferedReader == null) {
            return;
        }
        try {
            bufferedReader.close();
            inputStreamReader.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getLineNumber() {
        LineNumberReader lineNumberReader = null;
        int lineNumber = 0;
        try {
            if(File.endsWith(".gz")){
                lineNumberReader = new LineNumberReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(new File(File)))));
            }else {
                lineNumberReader = new LineNumberReader(new FileReader(File));
            }
            while(lineNumberReader.readLine()!=null){
            }
            lineNumber = lineNumberReader.getLineNumber();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (lineNumberReader!=null) {
                    lineNumberReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lineNumber;
    }

}
