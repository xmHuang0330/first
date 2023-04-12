package fayi;

import fayi.util.ExcelUtil;
import fayi.xml.Objects.Data;
import fayi.xml.Objects.Sample;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Testt01 {
    public static void main(String[] args) throws JAXBException, IOException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Data.class);
        Unmarshaller um = jaxbContext.createUnmarshaller();
        String path1 = Testt01.class.getClassLoader().getResource("").getPath();
        System.out.println(path1);
        File file = new File(path1 + "\\2Samples.xml");
        System.out.println(file.exists());
        Data data = (Data)um.unmarshal(file);
        if (data.getSamples().size() > 1) {
            ExcelUtil.allFromTemplate(data.getSamples());
        } else {
            ExcelUtil.singleReportFromTemplate(data.getSamples().get(0));
        }


    }
}
