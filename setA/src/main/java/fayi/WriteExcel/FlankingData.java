package fayi.WriteExcel;

import fayi.config.Config;
import fayi.config.Param;
import fayi.config.SnpMarker;
import fayi.seqParser.RazorOutParse;
import fayi.tableObject.SampleInfo;
import fayi.tableObject.StrInfo;
import fayi.utils.ExcelUtils;
import fayi.utils.SetAException;
import fayi.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class FlankingData {

    public void writeFlankingExcel(ArrayList<SampleInfo> sampleInfos) throws IOException, SetAException {
        XSSFWorkbook wb = new XSSFWorkbook();
        ArrayList<ArrayList<Object>> fullSeqData = createFullSeqData(sampleInfos);
        writeFlankingData(wb, sampleInfos);
        ArrayList<ArrayList<Object>> snpData = writeSnpToExcel(sampleInfos);
        ExcelUtils.writeData(wb, "sequence", "", 0, 0, fullSeqData);
        ExcelUtils.writeData(wb, "snp", "", 0, 0, snpData);
        FileOutputStream fileOutputStream = new FileOutputStream(Config.getInstance().getOutputPath() + "/flanking_" + Config.getInstance().getPanel().name() + ".xlsx");
        wb.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        wb.close();
    }

    private ArrayList<ArrayList<Object>> createFullSeqData(ArrayList<SampleInfo> sampleInfos) throws SetAException {
        Param panelParam = Config.getPanelParam("");
        ArrayList<ArrayList<Object>> data = new ArrayList<>();
        ArrayList<Object> header = new ArrayList<>();
        header.add("sample");
        for (String locus : panelParam.StrLocusOrder) {
            header.add(locus);
            header.add(locus);
        }
        data.add(header);

        RazorOutParse razorOutParse = new RazorOutParse();
        for (SampleInfo sampleInfo : sampleInfos) {
            try {
                razorOutParse.trimSequenceBatch( sampleInfo );
            } catch (SetAException e) {
                if (e.getCode() == 5) {
                    log.warn(String.format(e.getMessage() + " 样本：%s ", sampleInfo.getId()));
                } else {
                    throw e;
                }
            }
            for (int i = 0; i < 4; i++) {
                ArrayList<Object> values = new ArrayList<>();
                values.add(sampleInfo.getBasicInfo().id);

                for (String locus : panelParam.StrLocusOrder) {
                    if (i < sampleInfo.getStrLocusInfo().get(locus).getAllele().size()) {
                        StrInfo strInfo = sampleInfo.getStrLocusInfo().get(locus).getAllele().get(i);
                        values.add(strInfo.getTrimmedSeq() == null ? "Error" : strInfo.getTrimmedSeq());
                        values.add(strInfo.getFlankingSequence() == null ? "Error" : strInfo.getFlankingSequence());
                    } else {
                        values.add("");
                        values.add("");
                    }
                }
                data.add(values);
            }
        }
        return data;
    }

    private ArrayList<ArrayList<Object>> writeSnpToExcel(ArrayList<SampleInfo> sampleInfos) {
        Config config = Config.getInstance();
        HashMap<String, ArrayList<SnpMarker>> hgSnp = config.getHgSnp();
        ArrayList<ArrayList<Object>> data = new ArrayList<>();

        ArrayList<Object> header = new ArrayList<>();
        header.add("samples");
        header.addAll(config.getParam().StrLocusOrder);
        data.add(header);

        for (SampleInfo sampleInfo : sampleInfos) {
            ArrayList<Object> values = new ArrayList<>();
            values.add(sampleInfo.getId());
            for (String locus : config.getParam().StrLocusOrder) {
                XSSFRichTextString value = new XSSFRichTextString();
                ArrayList<StrInfo> allele = sampleInfo.getStrLocusInfo().get(locus).getAllele();
                if (allele.size() > 0) {
                    for (StrInfo strInfo : sampleInfo.getStrLocusInfo().get(locus).getAllele()) {
                        if (value.getString() != null && !value.getString().equals("")) value.append(",");
                        if (strInfo.getFlanking().size() > 0) {
                            if (strInfo.getFlankingSnp().size() > 0) {
                                boolean start = true;
                                List<SnpMarker> sorted = strInfo.getFlankingSnp().stream().sorted(Comparator.comparing(SnpMarker::toMarker)).collect(Collectors.toList());
                                for (SnpMarker snpMarker : sorted) {
                                    if (!start) {
                                        value.append(";");
                                    }
                                    if (hgSnp.getOrDefault(locus, new ArrayList<>()).contains(snpMarker)) {
                                        value.append(snpMarker.toMarker(), Utils.createFont("bold"));
                                    } else {
                                        try {
                                            value.append(snpMarker.toMarker());
                                        } catch (NullPointerException e) {
                                            System.err.printf("sample %s output snpMarker on locus %s failed null pointer %s ", sampleInfo.getId(), locus, snpMarker);
                                            value.append("");
                                        }
                                    }
                                    start = false;
                                }
                            } else {
                                value.append("REF");
                            }
                        }
                    }
                }
                values.add(value);
            }
            data.add(values);
        }
        return data;
    }

    private void writeFlankingData(XSSFWorkbook wb, ArrayList<SampleInfo> sampleInfos) {

        HashMap<String, String[]> flankingConfig = Config.getInstance().getFlankingSetting();

        Param panelParam = Config.getPanelParam("");

        List<String> noAb = Config.getInstance().getParam().StrLocusOrder.stream().filter(s -> !s.endsWith("a/b") && !s.equals("DYS572")).collect(Collectors.toList());
        List<String> aB = Config.getInstance().getParam().StrLocusOrder.stream().filter(s -> s.endsWith("a/b") || s.equals("DYS572")).collect(Collectors.toList());
        ArrayList<ArrayList<Object>> data = new ArrayList<>();
        ArrayList<Object> header = new ArrayList<>();
        header.add("sample");
        for (String locus : noAb) {
            header.add(locus);
            header.add(locus);
        }
        data.add(header);

        ArrayList<ArrayList<Object>> abData = new ArrayList<>();
        ArrayList<Object> abHeader = new ArrayList<>();
        abHeader.add("sample");
        for (String locus : aB) {
            abHeader.add(locus);
            abHeader.add(locus);
        }
        abData.add(abHeader);

        ArrayList<ArrayList<Object>> nData = new ArrayList<>();
        ArrayList<Object> nHeader = new ArrayList<>();
        nHeader.add("sample");
        nHeader.addAll(Config.getInstance().getParam().StrLocusOrder);
        nData.add(nHeader);
        for (SampleInfo sampleInfo : sampleInfos) {

            for (int i = 0; i < 2; i++) {
                data.add(addSampleSequence(sampleInfo, flankingConfig, i, noAb));
            }
            for (int i = 0; i < 4; i++) {
                abData.add(addSampleSequence(sampleInfo, flankingConfig, i, aB));
            }

            ArrayList<Object> nValues = new ArrayList<>();
            nValues.add(sampleInfo.getBasicInfo().id);
            for (String locus : panelParam.StrLocusOrder) {
                XSSFRichTextString value = new XSSFRichTextString();
                for (StrInfo strInfo : sampleInfo.getStrLocusInfo().get(locus).getAllele()) {
                    if (value.getString() != null && !value.getString().equals("")) value.append(",");
                    value.append(strInfo.getNs() == null ? "Fail" : strInfo.getNs());
                }
                nValues.add(value);
            }
            nData.add(nValues);
            //双等位输出两行，但等位空缺一行

        }
        ExcelUtils.writeData(wb, "flanking", "", 0, 0, data);
        ExcelUtils.writeData(wb, "flanking-ab", "", 0, 0, abData);
        ExcelUtils.writeData(wb, "N", "", 0, 0, nData);

    }

    private ArrayList<Object> addSampleSequence(SampleInfo sampleInfo, HashMap<String, String[]> flankingConfig, Integer i, List<String> locusOrder) {
        ArrayList<Object> values = new ArrayList<>();
        values.add(sampleInfo.getBasicInfo().id);
        for (String locus : locusOrder) {
            if (i < sampleInfo.getStrLocusInfo().get(locus).getAllele().size()) {
                StrInfo strInfo = sampleInfo.getStrLocusInfo().get(locus).getAllele().get(i);
                String leftFlanking = strInfo.getLeftFlanking().equals(strInfo.getRightFlanking()) ? "FAIL" : strInfo.getLeftFlanking();
                String rightFlanking = strInfo.getLeftFlanking().equals(strInfo.getRightFlanking()) ? "FAIL" : strInfo.getRightFlanking();
                XSSFRichTextString leftRTS = new XSSFRichTextString();
                leftRTS.append(leftFlanking, Utils.createFont("red"));
                values.add(leftFlanking.equals(flankingConfig.get(locus)[0]) ? leftFlanking : leftRTS);

                XSSFRichTextString rightRTS = new XSSFRichTextString();
                rightRTS.append(rightFlanking, Utils.createFont("red"));
                values.add(rightFlanking.equals(flankingConfig.get(locus)[1]) ? rightFlanking : rightRTS);

            } else {
                values.add("");
                values.add("");
            }
        }
        return values;
    }

}
