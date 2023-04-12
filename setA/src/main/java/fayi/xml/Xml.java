package fayi.xml;

import fayi.config.Config;
import fayi.config.Param;
import fayi.tableObject.SnpLocusInfo;
import fayi.tableObject.StrLocusInfo;
import fayi.tableObject.SampleInfo;
import fayi.utils.FileUtils;
import fayi.utils.SetAException;
import fayi.xml.Objects.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import lombok.ToString;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;

/*
    读写xml
 */
@ToString
public class Xml {
    Config config;
    public Xml(){
        config = Config.getInstance();
    }

    //样本信息转换到xml的sample对象
    public Data sampleInfoToXmlData(ArrayList<SampleInfo> sampleInfos) {
        Data data = new Data();
        for (SampleInfo sampleInfo : sampleInfos) {
            data.samples.add(sampleInfoToSample(sampleInfo));
        }
        data.sampleNum = sampleInfos.size();
        return data;
    }

    private Sample sampleInfoToSample(SampleInfo sampleInfo) {

        Sample sample = new Sample();
        LocusInfomations locusInfomations = sample.locusInfomations;
        for (String locus : Param.getInstance().AutoStrLocusOrder) {
            StrLocusInfo strLocusInfo = sampleInfo.getStrLocusInfo().getOrDefault(locus, StrLocusInfo.getEmpty());
            locusInfomations.getAutoStr().add(
                    new LocusData( locus, strLocusInfo.getIBObserving(),
                            strLocusInfo.getQCWithLevel(),
                            strLocusInfo.getTotalDepth(),
                            strLocusInfo.getAlleleNameAsGenoType(), strLocusInfo.getEmptyReason() ) );
        }

        for (String locus : Param.getInstance().XStrLocusOrder) {
            StrLocusInfo strLocusInfo = sampleInfo.getStrLocusInfo().getOrDefault(locus, StrLocusInfo.getEmpty());
            locusInfomations.getXStr().add(
                    new LocusData( locus, strLocusInfo.getIBObserving(),
                            strLocusInfo.getQCWithLevel(),
                            strLocusInfo.getTotalDepth(),
                            strLocusInfo.getAlleleNameAsGenoType(), strLocusInfo.getEmptyReason() ) );
        }

        for(String locus: Param.getInstance().YStrLocusOrder) {
            StrLocusInfo strLocusInfo = sampleInfo.getStrLocusInfo().get(locus);
            if (strLocusInfo == null) {
                continue;
            }
            locusInfomations.getYStr().add(new LocusData(locus, strLocusInfo.getIBObserving(),
                    strLocusInfo.getQCWithLevel(),
                    strLocusInfo.getTotalDepth(),
                    strLocusInfo.getAlleleNameAsGenoType(), strLocusInfo.getEmptyReason()));
        }
        for(String locus: Param.getInstance().SnpLocusOrder){
            SnpLocusInfo snpLocusInfo = sampleInfo.getSnpLocusInfo().get(locus);
            if(null == snpLocusInfo){ continue;}

            ArrayList<GenoType> allele = snpLocusInfo.getSnpAlleleAsGenoTypes();
            Integer depth = sampleInfo.getSnpLocusInfo().getOrDefault( locus,new SnpLocusInfo( locus ) ).getTotalDepth();
            locusInfomations.getIsnp().add( new LocusData( locus, sampleInfo.getSnpLocusInfo().getOrDefault( locus, new SnpLocusInfo(locus) ).getQCWithLevel(), depth, allele, null ) );
        }

        //统计信息
        sample.calResult = sampleInfo.getCalResult();
        //基本信息
        sample.basicInfo = sampleInfo.getBasicInfo();
        //软件版本

        sample.basicInfo.softWare = new SoftWare("AnalyseAndCalculate", config.getArtifact(), config.getVersion());
        sample.sites.reference = "hg38";
        sample.sites.strSites = sampleInfo.getStrDataAsSites();
        sample.sites.snpSites = sampleInfo.getSnpDataAsSites();

        return sample;
    }


    public void dataToXml(Data data,String output) throws SetAException {
        try {
            JAXBContext jaxbContext;
            jaxbContext = JAXBContext.newInstance(Data.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,true);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(data,stringWriter);
            FileUtils fileUtils = new FileUtils( output );
            fileUtils.writeLine( stringWriter.toString() );
            fileUtils.finishWrite();
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new SetAException(300, e.getMessage());
        }


    }

    public Data xmlToData(String xmlFile) {
//        Utils.checkReadFile(xmlFile);
//        String s = new String( Files.readAllBytes( new File( xmlFile ).toPath() ) );
        JAXBContext jaxbContext;
        Data data = null;
        try {
            jaxbContext = JAXBContext.newInstance( Data.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            data = (Data)unmarshaller.unmarshal( new File( xmlFile ) );
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return data;
    }


}
