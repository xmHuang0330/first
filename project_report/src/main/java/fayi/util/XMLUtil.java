package fayi.util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringReader;

public class XMLUtil {

    public static Object convertXmlStrToObject(Class<?> clazz,String xmlStr){
        Object xmlObject = null;
        try{
            JAXBContext context = JAXBContext.newInstance(clazz);
            //进行将xml转成对象的核心接口
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader sr = new StringReader(xmlStr);
            xmlObject = unmarshaller.unmarshal(sr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlObject;
    }


    public static Object convertXmlFileToObject(Class<?> clazz,String xmlPath){
        Object xmlObject = null;
        try{
            JAXBContext context = JAXBContext.newInstance(clazz);
            //进行将xml转成对象的核心接口
            Unmarshaller unmarshaller = context.createUnmarshaller();
            //InputStreamReader isr = new InputStreamReader(new FileInputStream(xmlPath),"GBK");
            xmlObject = unmarshaller.unmarshal(new File(xmlPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xmlObject;
    }


}
