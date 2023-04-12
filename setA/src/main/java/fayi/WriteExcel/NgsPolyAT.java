package fayi.WriteExcel;

import fayi.config.Config;
import fayi.tableObject.StrLocusInfo;
import fayi.tableObject.SampleInfo;
import fayi.tableObject.SeqInfo;
import fayi.tableObject.StrInfo;
import fayi.utils.ExcelUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NgsPolyAT {

    public void toExcel(List<SampleInfo> sampleInfos) throws IOException {
        Config config = Config.getInstance();
        ArrayList<ArrayList<Object>> data = new ArrayList<>();

        ArrayList<Object> header = new ArrayList<>();
        header.add("Sample");
        header.addAll(config.getParam().StrLocusOrder);
        data.add(header);

        for (SampleInfo sampleInfo : sampleInfos) {
            ArrayList<Object> values = new ArrayList<>();
            values.add(sampleInfo.getBasicInfo().lane + "_" + sampleInfo.getId());
            for (String locus : config.getParam().StrLocusOrder) {
                StringBuilder value = new StringBuilder();
                for (StrInfo strInfo : sampleInfo.getStrLocusInfo().getOrDefault(locus, new StrLocusInfo()).getAllele()) {
                    if (!"".equals(value.toString())) {
                        value.append(",");
                    }
                    if (strInfo.getNGSStutter().size() > 0) {
                        value.append(strInfo.getRepeatSequence()).append("|").append(strInfo.getReads()).append(";");
                        for (StrInfo NGS : strInfo.getNGSStutter()) {
                            value.append(NGS.getRepeatSequence()).append("|").append(NGS.getReads()).append(";");
                        }
                    } else {
                        value.append("-");
                    }
                }
                if (value.length() > 0) {
                    values.add(value.substring(0, value.length() - 1));
                } else {
                    values.add(value.toString());
                }
            }
            data.add(values);
        }

        XSSFWorkbook sheets = new XSSFWorkbook();
        ExcelUtils.writeData(sheets, "Sheet1", "", 0, 0, data);
        FileOutputStream fileOutputStream = new FileOutputStream(config.getOutputPath() + "/ngs-polyA_" + config.getPanel().name() + ".xlsx");
        sheets.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        sheets.close();

    }

}
