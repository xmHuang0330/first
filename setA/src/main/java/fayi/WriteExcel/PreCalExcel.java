package fayi.WriteExcel;

import fayi.tableObject.StrLocusInfo;
import fayi.config.Config;
import fayi.config.Param;
import fayi.tableObject.*;
import fayi.utils.ExcelUtils;
import fayi.utils.FileUtils;
import fayi.utils.SetAException;
import fayi.utils.Utils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static fayi.utils.Utils.generifyArrayList;

public class PreCalExcel {

    public void preCal(ArrayList<SampleInfo> sampleInfos) throws SetAException, IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        for (Double ac : new Double[]{0.015, 0.033, 0.045, 0.1, 0.15}) {
            ExcelUtils.writeData(workbook, "AlleleCount" + ac, "", 0, 0, alleleCount(sampleInfos, ac));
        }
        ExcelUtils.writeData(workbook, "stutter%", "", 0, 0, stutterProportion(sampleInfos));

        ExcelUtils.writeData(workbook, "stutterCount", "", 0, 0, stutterCount(sampleInfos));

        ExcelUtils.writeData(workbook, "IBObserving", "", 0, 0, ibRev(sampleInfos));

        //输出位点所有序列
        Utils.checkDir(Config.getInstance().getOutputPath() + "/sequence_output/");
        outPutLocusReads(sampleInfos);

        FileOutputStream fileOutputStream = new FileOutputStream(Config.getInstance().getOutputPath() + "/" + Config.getInstance().getArtifact() + "_" + "preCal_" + (Config.getInstance().getNoFilter() ? "10x" : "100x") + ".xlsx");
        workbook.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        workbook.close();

    }

    private void outPutLocusReads(ArrayList<SampleInfo> sampleInfos) {
        for(String locus:Param.getInstance().StrLocusOrder){
            ArrayList<StrInfo> sequences = new ArrayList<>();
            for(SampleInfo sampleInfo:sampleInfos) {
                List<StrInfo> sameLocusList = sampleInfo.getStrData().getOrDefault( locus, new ArrayList<>() ).stream().filter( SeqInfo::isAboveAT ).collect( Collectors.toList() );
                for (StrInfo strInfo : sameLocusList) {
                    boolean flag = true;
                    for (StrInfo strInfo1 : sequences) {
                        if (strInfo1.getRepeatSequence().equals( strInfo.getRepeatSequence() )) {
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        sequences.add( strInfo );
                    }
                }
            }
            sequences.sort(Comparator.comparingInt(o -> o.getRepeatSequence().length()));
            FileUtils fileUtils = new FileUtils(Config.getInstance().getOutputPath() + "/sequence_output/" + locus.split("/")[0] + "_sequence.txt");
            for(StrInfo strInfo:sequences){
                fileUtils.writeLine(strInfo.getAlleleName()+"\t"+strInfo.getRepeatSequence());
            }
            fileUtils.finishWrite();
        }

    }


    private ArrayList<ArrayList<Object>> ibRev(ArrayList<SampleInfo> sampleInfos) {
        ArrayList<ArrayList<Object>> data = generifyArrayList();
        for(SampleInfo sampleInfo:sampleInfos) {
            ArrayList<Object> values = new ArrayList<>();
            values.add( sampleInfo.getBasicInfo().id );
            for(String locus: Param.getInstance().StrLocusOrder) {
                StringBuilder ibo = new StringBuilder();
                for (Double ibObserving : sampleInfo.getStrLocusInfo().getOrDefault( locus, new StrLocusInfo() ).getIBObserving()) {
                    ibo.append(String.format(",%,.2f", ibObserving));
                }
                ibo = new StringBuilder( ibo.toString().replaceFirst( ",", "" ) );
                values.add( ibo.toString().equals( "" ) ? "Na" : ibo.toString() );
            }
            data.add( values );
        }
        return data;
    }


    public ArrayList<ArrayList<Object>> stutterProportion(ArrayList<SampleInfo> sampleInfos){
        ArrayList<ArrayList<Object>> datas = new ArrayList<>();
        datas.add(getHeader(sampleInfos));
        for(String locus: Param.getInstance().StrLocusOrder) {
            ArrayList<Object> values = new ArrayList<>();
            values.add(locus);
            for(SampleInfo sampleInfo:sampleInfos) {
                String alikeOne = "";
                List<StrInfo> sameLocusList = sampleInfo.getStrData().getOrDefault( locus, new ArrayList<>() ).stream().filter( SeqInfo::isAboveAT ).collect( Collectors.toList() );
                for (StrInfo si : sameLocusList) {
                    ArrayList<StrInfo> stutters = si.getStutters();
                    if (si.getTyped()) {
                        if (stutters.size() > 0) {
                            stutters.sort( Comparator.comparingDouble( SeqInfo::getReads ) );
                            alikeOne += String.format( ",%.2f", stutters.get( stutters.size() - 1 ).getReads() / si.getReads() );
                        }
                    }
                }
                values.add( alikeOne.replaceFirst( ",", "" ) );
            }
            datas.add(values);
        }
        return datas;
    }

    private ArrayList<Object> getHeader(ArrayList<SampleInfo> sampleInfos) {
        ArrayList<Object> header = new ArrayList<>();
        header.add( "locus/samples" );
        for (SampleInfo sampleInfo : sampleInfos) {
            header.add( sampleInfo.getBasicInfo().id );
        }
        return header;
    }

    public ArrayList<ArrayList<Object>> ib(ArrayList<SampleInfo> sampleInfos) {
        ArrayList<ArrayList<Object>> datas = new ArrayList<>();

        datas.add( getHeader( sampleInfos ) );
        for (String locus : Param.getInstance().StrLocusOrder) {
            ArrayList<Object> values = new ArrayList<>();
            values.add( locus );
            for (SampleInfo sampleInfo : sampleInfos) {
                List<StrInfo> sameLocusList = sampleInfo.getStrData().getOrDefault( locus, new ArrayList<>() ).stream().filter( SeqInfo::isAboveAT ).collect( Collectors.toList() );

                if (sameLocusList.size() == 0) {
                    values.add( "Na" );
                } else if (sameLocusList.size() == 1) {
                    values.add( 1 );
                } else {
                    values.add( sameLocusList.get( 1 ).getReads() / sameLocusList.get( 0 ).getReads() );
                }
            }
            datas.add(values);
        }
        return datas;
    }

    public ArrayList<ArrayList<Object>> alleleCount(ArrayList<SampleInfo> sampleInfos, Double ac){
        ArrayList<ArrayList<Object>> datas = new ArrayList<>();
        ArrayList<Object> header = new ArrayList<>();
        header.add("locus/samples");
        for(SampleInfo sampleInfo:sampleInfos) {
            header.add(sampleInfo.getBasicInfo().id);
        }
        datas.add(header);
        for(String locus: Param.getInstance().StrLocusOrder){
            ArrayList<Object> values = new ArrayList<>();
            values.add(locus);
            for(SampleInfo sampleInfo:sampleInfos){
                int count = 0;
                for(StrInfo strInfo:sampleInfo.getStrData().getOrDefault(locus,new ArrayList<>())){
                    if(strInfo.getReads() / sampleInfo.getStrLocusInfo().get(locus).getTotalDepth() > ac){
                        count ++;
                    }
                }
                values.add(count);
            }
            datas.add(values);
        }
        return datas;
    }

    public ArrayList<ArrayList<Object>> stutterCount(ArrayList<SampleInfo> sampleInfos){
        ArrayList<ArrayList<Object>> datas = new ArrayList<>();
        datas.add(getHeader(sampleInfos));
        for(String locus: Param.getInstance().StrLocusOrder){
            ArrayList<Object> values = new ArrayList<>();
            values.add(locus);
            for(SampleInfo sampleInfo:sampleInfos) {
                int count = 0;
                for (StrInfo strInfo : sampleInfo.getStrDataAboveAt( locus )) {
                    if (strInfo.getIsStutter()) {
                        count++;
                    }
                }
                values.add( count );
            }
            datas.add(values);
        }
        return datas;
    }
}
