package fayi.WriteExcel;

import fayi.config.Config;
import fayi.config.Enum.Gender;
import fayi.config.Param;
import fayi.config.Enum.QC;
import fayi.tableObject.StrLocusInfo;
import fayi.tableObject.SampleInfo;
import fayi.tableObject.SeqInfo;
import fayi.utils.ExcelUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class SumExcel {

    private final Param param = Param.getInstance();

    private final ArrayList<ArrayList<Object>> autoList = new ArrayList<>();
    private final ArrayList<ArrayList<Object>> xList = new ArrayList<>();
    private final ArrayList<ArrayList<Object>> yList = new ArrayList<>();
    private final ArrayList<ArrayList<Object>> snpList = new ArrayList<>();
    private final ArrayList<ArrayList<Object>> autoDepthList = new ArrayList<>();
    private final ArrayList<ArrayList<Object>> xDepthList = new ArrayList<>();
    private final ArrayList<ArrayList<Object>> yDepthList = new ArrayList<>();
    private final ArrayList<ArrayList<Object>> snpDepthList = new ArrayList<>();
    private final ArrayList<ArrayList<Object>> TAIL = new ArrayList<>();

    public SumExcel() {
        for (QC qc : QC.values()) {
            TAIL.add( new ArrayList<>( Arrays.asList( qc.Indictor, qc.Description ) ) );
        }
    }

    public void fillSumXlsx() throws IOException {

        FileInputStream fis = new FileInputStream( Config.getInstance().getReportSumXlsx() );
        XSSFWorkbook sumTable = new XSSFWorkbook(fis);
        ExcelUtils.writeHeader( sumTable,"Autosomal STRs",7,6,param.AutoStrLocusOrder );
        ExcelUtils.writeData(sumTable,"Autosomal STRs","AutosomalSTRs_Table",8,0,autoList);
        ExcelUtils.writeData(sumTable,"Autosomal STRs","tail",8+autoList.size()+1,0,TAIL);
        ExcelUtils.writeHeader( sumTable,"Autosomal STR Coverage",7,2,param.AutoStrLocusOrder );
        ExcelUtils.writeData(sumTable,"Autosomal STR Coverage","AutosomalSTRCoverage_Table",8,0,autoDepthList);

        ExcelUtils.writeHeader( sumTable,"Y STRs",7,6,param.YStrLocusOrder );
        ExcelUtils.writeData(sumTable,"Y STRs","YSTRs_Table",8,0,yList);
        ExcelUtils.writeData(sumTable,"Y STRs","tail",8+yList.size()+1,0,TAIL);
        ExcelUtils.writeHeader( sumTable,"Y STR Coverage",7,2,param.YStrLocusOrder );
        ExcelUtils.writeData(sumTable,"Y STR Coverage","YSTRCoverage_Table",8,0,yDepthList);

        ExcelUtils.writeHeader( sumTable,"X STRs",7,6,param.XStrLocusOrder );
        ExcelUtils.writeData(sumTable,"X STRs","XSTRs_Table",8,0,xList);
        ExcelUtils.writeData(sumTable,"X STRs","tail",8+xList.size()+1,0,TAIL);
        ExcelUtils.writeHeader( sumTable,"X STR Coverage",7,2,param.XStrLocusOrder );
        ExcelUtils.writeData(sumTable,"X STR Coverage","XSTRCoverage_Table",8,0,xDepthList);

        ExcelUtils.writeHeader( sumTable,"iSNPs",7,5,param.SnpLocusOrder );
        ExcelUtils.writeData(sumTable,"iSNPs","iSNPs_Table",8,0,snpList);
        ExcelUtils.writeHeader( sumTable,"iSNP Coverage",7,2,param.SnpLocusOrder );
        ExcelUtils.writeData(sumTable,"iSNP Coverage","iSNPCoverage_Table",8,0,snpDepthList);


        FileOutputStream sumoutput = new FileOutputStream(Config.getInstance().getOutput() + "/" + Config.getInstance().getArtifact() + "_" + "_sumReport.xlsx");
        sumTable.write(sumoutput);
        sumoutput.flush();
        sumoutput.close();
        sumTable.close();
    }

    public void fillSumDepthList(SampleInfo sampleInfo){
        ArrayList<Object> sampleList = new ArrayList<>();
        HashMap<String, StrLocusInfo> locusInfo = sampleInfo.getStrLocusInfo();
        sampleList.add(sampleInfo.getBasicInfo().id);
        sampleList.add("");

        ArrayList<Object> autoDepth = new ArrayList<>(sampleList);
        for (String locus: param.AutoStrLocusOrder) {
            int depth = 0;
            for(SeqInfo seqInfo:locusInfo.getOrDefault(locus,StrLocusInfo.getEmpty()).getAllele()){
                depth += seqInfo.getReads();
            }
            autoDepth.add(depth==0?"":depth);
        }

        ArrayList<Object> yDepth = new ArrayList<>(sampleList);
        if(!sampleInfo.getBasicInfo().gender.equals( Gender.female )) {
            for (String locus : param.YStrLocusOrder) {
                int depth = 0;
                for(SeqInfo seqInfo:locusInfo.getOrDefault(locus,StrLocusInfo.getEmpty()).getAllele()){
                    depth += seqInfo.getReads();
                }
                yDepth.add(depth==0?"":depth);
            }
        }

        ArrayList<Object> xDepth = new ArrayList<>(sampleList);
        for (String locus:param.XStrLocusOrder) {
            int depth = 0;
            for(SeqInfo seqInfo:locusInfo.getOrDefault(locus,StrLocusInfo.getEmpty()).getAllele()){
                depth += seqInfo.getReads();
            }
            xDepth.add(depth==0?"":depth);
        }

        ArrayList<Object> snpDepth = new ArrayList<>(sampleList);
        for(String locus:param.SnpLocusOrder){
            int depth = 0;
            for(SeqInfo seqInfo:locusInfo.getOrDefault(locus,StrLocusInfo.getEmpty()).getAllele()){
                depth += seqInfo.getReads();
            }
            snpDepth.add(depth==0?"":depth);
        }
        synchronized (this) {
            autoDepthList.add(autoDepth);
            yDepthList.add(yDepth);
            xDepthList.add(xDepth);
            snpDepthList.add(snpDepth);
        }
    }

    public void fillSumList(SampleInfo sampleInfo) {
        ArrayList<Object> sampleSum = new ArrayList<>();
        HashMap<String, StrLocusInfo> locusInfos = sampleInfo.getStrLocusInfo();
        sampleSum.add( sampleInfo.getBasicInfo().id );
        sampleSum.add( "" );
        sampleSum.add( "" );
        sampleSum.add( sampleInfo.getCalResult().getSingleSource() );
        sampleSum.add( sampleInfo.getCalResult().getInterlocusBalance() );
        sampleSum.add( sampleInfo.getBasicInfo().gender );

        ArrayList<Object> autoSum = new ArrayList<>(sampleSum);
        for (String locus:param.AutoStrLocusOrder) {
            autoSum.add(locusInfos.get(locus) != null? locusInfos.get(locus).getAlleleNameAsString(false,true)+locusInfos.get(locus).getQCAsIndictor() : sampleInfo.getBasicInfo().type.equals( "" )?QC.Not_detected.Indictor:QC.Not_Analyzed.Indictor);
        }

        ArrayList<Object> xSum = new ArrayList<>(sampleSum);
        for (String locus:param.XStrLocusOrder) {
            xSum.add(locusInfos.get(locus) != null ? locusInfos.get(locus).getAlleleNameAsString(false,true)+locusInfos.get(locus).getQCAsIndictor() : sampleInfo.getBasicInfo().type.equals( "" )?QC.Not_detected.Indictor:QC.Not_Analyzed.Indictor);
        }
        ArrayList<Object> ySum = new ArrayList<>(sampleSum);
        if(!sampleInfo.getBasicInfo().gender.equals( Gender.female )) {
            for (String locus : param.YStrLocusOrder) {
//            System.out.println(locus);
                ySum.add( locusInfos.get( locus ) != null ? locusInfos.get( locus ).getAlleleNameAsString(false,true) + locusInfos.get( locus ).getQCAsIndictor() : sampleInfo.getBasicInfo().type.equals( "" ) ? QC.Not_detected.Indictor : QC.Not_Analyzed.Indictor );
            }
        }
        ///isnp
        ArrayList<Object> snpLocusInfo = new ArrayList<>(sampleSum);
        snpLocusInfo.remove(4);
        HashMap<String, String> snpStrings = sampleInfo.getSnpAlleleAsString();
        for (String snp:param.SnpLocusOrder){
            snpLocusInfo.add(snpStrings.get(snp));
        }
        synchronized (this) {
            autoList.add(autoSum);
            xList.add(xSum);
            yList.add(ySum);
            snpList.add(snpLocusInfo);
        }
    }

}
