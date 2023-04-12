package fayi;

import fayi.config.Config;
import fayi.config.Param;
import fayi.tableObject.*;
import fayi.utils.FileUtils;
import fayi.utils.SetAException;
import fayi.utils.Utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class HgPredict {

    private final HashMap<String, String[]> hgRelation = new HashMap<>();
    private final Config config;

    public HgPredict() {
        config = Config.getInstance();
        Param.getInstance();
        readHgRelation();
    }

    private void readHgRelation() {
        FileUtils fileUtils = new FileUtils(config.getProjectPath() + "/resource/snp_hg_tree.txt");
        String line;
        while ((line = fileUtils.readLine()) != null) {
            String[] values = line.trim().split("\t");
            hgRelation.put(values[1], new String[]{values[0], values[2]});
        }
    }

    private void extend(String key, ArrayList<String> hgLine) {
        if (hgRelation.containsKey(hgRelation.get(key)[0])) {
            hgLine.add(hgRelation.get(key)[0]);
            extend(hgRelation.get(key)[0], hgLine);
        } else {
            hgLine.add(hgRelation.get(key)[0]);
        }
    }

    public void predictHg(ArrayList<SampleInfo> sampleInfos) throws SetAException {
        FileUtils writer = new FileUtils(config.getOutputPath() + "/" + sampleInfos.iterator().next().getBasicInfo().lane + "_hg_out.txt");
        writer.writeLine("index\thaplogroup\thg_marker\tvalid_marker\ttotal_reads\tqc_score");

        sampleInfos.sort(Comparator.comparing(o -> o.getBasicInfo().id));
        Utils.checkDir(config.getOutputPath() + "/hg_predict/");
        for (SampleInfo sampleInfo : sampleInfos) {
            int availableReads = 0;
            HgInfo hgInfo = sampleInfo.getHgInfo();
            ArrayList<String> longest = new ArrayList<>();
            hgInfo.setPosition(sampleInfo.getSnpLocusInfo());
            for (String key : hgRelation.keySet()) {
                ArrayList<String> keyLine = new ArrayList<>();
                keyLine.add(key);
                extend(key, keyLine);
                ArrayList<String> results = new ArrayList<>();
                for (String locus : keyLine) {
                    if (!"ROOT".equals(locus)) {
                        results.add(hgInfo.getPosition().get(locus));
//                        results.add(hgInfo.getValidMarker().contains(locus)?hgInfo.getPosition().get(locus):"X");
                    }
                }
                if (results.indexOf("1") == 0) {
                    if (results.size() > longest.size()) {
                        longest = new ArrayList<>();
                        longest.addAll(results);
                        hgInfo.setHaplotype(hgRelation.get(key)[1]);
                        hgInfo.setQcScore(keyLine);
                        hgInfo.setHgLine(keyLine);
                    }
                }
                for (SnpInfo snpInfo : sampleInfo.getSnpDataAboveAt(key)) {
                    availableReads += snpInfo.getReads();
                }
            }
            writer.writeLine(sampleInfo.getBasicInfo().id + "\t" + hgInfo.getHaplotype() + "\t" + hgInfo.getHg_marker() + "\t" + hgInfo.getValidMarker().size() + "\t" + availableReads + "\t" + hgInfo.getQcScore());
            outputSingleHgInfo(sampleInfo);
        }
        writer.finishWrite();
    }

    private void outputSingleHgInfo(SampleInfo sampleInfo) {
        HashMap<String, String[]> hgRef = Config.getInstance().hgRef;
        FileUtils fileUtils = new FileUtils(config.getOutputPath() + "/hg_predict/" + sampleInfo.getBasicInfo().id + "_hg.out");
        fileUtils.writeLine("marker_name\thaplogroup\tmutation\tanc\tder\treads\tcalled_perc\tcalled_base\tstate\tDesctiption");
        for (String locus : hgRef.keySet()) {
            float aboveAtDepth = 0;
            for (SnpInfo snpInfo : sampleInfo.getSnpDataAboveAt(locus)) {
                aboveAtDepth += snpInfo.getReads();
            }
            //qcString
            String qcAsString = sampleInfo.getSnpLocusInfo().get(locus).getQCAsString();
            if (sampleInfo.getHgInfo().getDiscordant().contains(locus)) {
                qcAsString += ",Discordant";
            }
            if (qcAsString.startsWith(",")) {
                qcAsString = qcAsString.replaceFirst(",", "");
            }
            //
            StringBuilder called_base = new StringBuilder();
            StringBuilder called_perc = new StringBuilder();
            StringBuilder state = new StringBuilder();
            for (SeqInfo seqInfo : sampleInfo.getSnpLocusInfo().getOrDefault(locus, new SnpLocusInfo(locus)).getAllele()) {
                called_perc.append(";").append(String.format("%.2f", seqInfo.getReads() / aboveAtDepth));
                called_base.append(";").append(seqInfo.getAlleleName());
                state.append(";").append(seqInfo.getAlleleName().equals(hgRef.get(locus)[0]) ? "0" : seqInfo.getAlleleName().equals(hgRef.get(locus)[1]) ? "1" : seqInfo.getAlleleName());
            }
            if ("".equals(called_perc.toString())) {
                called_perc.append("NA");
                called_base.append("NA");
                state.append("NA");
            } else {
                called_base.replace(0, 1, "");
                called_perc.replace(0, 1, "");
                state.replace(0, 1, "");
            }
            fileUtils.writeLine(locus + "\t" + hgRelation.get(locus)[1] + "\t" + hgRef.get(locus)[0] + "->" + hgRef.get(locus)[1] + "\t" + hgRef.get(locus)[0] + "\t" + hgRef.get(locus)[1] + "\t" + aboveAtDepth + "\t" + called_perc + "\t" + called_base + "\t" + state + "\t" + qcAsString);
        }
        fileUtils.finishWrite();
    }
}
