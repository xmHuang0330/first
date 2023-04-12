package fayi.seqParser;

import fayi.config.Config;
import fayi.config.Enum.Exec;
import fayi.config.Enum.Platform;
import fayi.tableObject.SampleInfo;
import fayi.utils.LocalCommand;
import fayi.utils.Utils;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import static fayi.config.Enum.Panel.setC;

@Component
public class SE400FqTools {

    private final LocalCommand razorBin = new LocalCommand(Exec.razor);
    private final Config config = Config.getInstance();



    @SneakyThrows
    public void razer(SampleInfo sampleInfo) {
        String strOutput = Config.getInstance().getOutputPath() + "/str_snp_out/" +sampleInfo.getBasicInfo().lane+ "_" + sampleInfo.getBasicInfo().id + "_STR_SNP.out";
        String cat = "cat";
        if (sampleInfo.getBasicInfo().fastq.endsWith(".gz")) {
            cat = "gzip -d -c";
        }
        String[] strRazer = new String[]{cat, sampleInfo.getBasicInfo().fastq, "|", razorBin.getExecFile(), config.getPanel().equals(setC) ? "-a 0" : "", "-p", config.getRazorWorker() + "", "-v", "-c", config.getRazorConfig(), "1>", strOutput};
        if (Utils.getCurrentPlatform() == Platform.linux) {
            Utils.RunCommand(new String[]{"/bin/bash", "-c"}, strRazer);
        } else if (Utils.getCurrentPlatform() == Platform.windows) {
            String[] strings = {sampleInfo.getBasicInfo().fastq, razorBin.getExecFile(), config.getRazorConfig(), strOutput};
            for (int i = 0; i < strings.length; i++) {
                if (strings[i].contains("\\") || strings[i].contains("/")) {
                    strings[i] = strings[i].replaceAll("\\\\", "\\\\\\\\");
                    strings[i] = strings[i].replaceAll("/", "\\\\\\\\");
                }
//                if (!"".equals(strings[i])) {
//                    strings[i] = Utils.RunCommand(new String[]{"cmd.exe", "/c"}, new String[]{"wsl", "wslpath", "-a", strings[i]}).trim();
//                }
            }
   //         Utils.RunCommand(new String[]{"cmd", "/c", "bash", "-c"}, new String[]{cat, strings[0], "|", strings[1], "-p", config.getRazorWorker() + "", "-v", "-c", strings[2], "1>", strings[3]});
            cat = "7z x";
            String so = "-so";
            Utils.RunCommand(new String[]{"cmd", "/c",}, new String[]{cat, strings[0],so, "|", strings[1], "-p", config.getRazorWorker() + "", "-v", "-c", strings[2], "1>", strings[3]});
        } else if (Utils.getCurrentPlatform() == Platform.mac) {
            strRazer = new String[]{cat, sampleInfo.getBasicInfo().fastq, "|", razorBin.getExecFile(), "-p", config.getRazorWorker() + "", "-v", "-c", config.getRazorConfig(), "1>", strOutput};
            Utils.RunCommand(new String[]{"/bin/bash", "-c"}, strRazer);
        }
    }



    @SneakyThrows
    public void flankingRazor(String flankingOut, SampleInfo sampleInfo) {
        Utils.checkDir(config.getOutputPath() + "/str_snp_out/");
        String cat = "cat";
        if (sampleInfo.getBasicInfo().fastq.endsWith(".gz")) {
            cat = "gzip -d -c";
        }
        String[] flankingRazer = new String[]{cat, sampleInfo.getBasicInfo().fastq, "|", razorBin.getExecFile(), "-p", config.getRazorWorker() + "", "-v", "-c", config.getFlankingConfigFile(), "1>", flankingOut};
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            Utils.RunCommand(new String[]{"/bin/bash", "-c"}, flankingRazer);
        } else if (System.getProperty("os.name").toLowerCase().contains("mac os x")) {
            Utils.RunCommand(new String[]{"/bin/bash", "-c"}, flankingRazer);
        } else if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            String[] strings = {sampleInfo.getBasicInfo().fastq, razorBin.getExecFile(),config.getFlankingConfigFile(),flankingOut};
            for (int i = 0; i < strings.length; i++) {
                if (strings[i].contains("\\") || strings[i].contains("/")) {
                    strings[i] = strings[i].replaceAll("\\\\", "\\\\\\\\");
                    strings[i] = strings[i].replaceAll("/", "\\\\\\\\");
                }
//                if (!"".equals(strings[i])) {
//                    strings[i] = Utils.RunCommand(new String[]{"cmd.exe", "/c"}, new String[]{"wsl", "wslpath", "-a", strings[i]}).trim();
//                }
            }
            //Utils.RunCommand(new String[]{"cmd", "/c", "bash", "-c"}, new String[]{cat, strings[0], "|", strings[1], "-p", config.getRazorWorker() + "", "-v", "-c", strings[2], "1>", strings[3]});

            cat = "7z x";
            String so = "-so";
            Utils.RunCommand(new String[]{"cmd", "/c",}, new String[]{cat, strings[0],so, "|", strings[1], "-p", config.getRazorWorker() + "", "-v", "-c", strings[2], "1>", strings[3]});
        }

//        Utils.RunCommand( snpRazer );
    }

    @SneakyThrows
    public void mhRazor(SampleInfo sampleInfo) {
        Utils.checkDir(config.getOutputPath() + "/str_snp_out/");
        String mhOut = config.getOutputPath() + "/str_snp_out/" + sampleInfo.getBasicInfo().lane + "_" + sampleInfo.getBasicInfo().id + "_MH.out";
        String cat = "cat";
        if (sampleInfo.getBasicInfo().fastq.endsWith(".gz")) {
            cat = "gzip -d -c";
        }
        String[] flankingRazer = new String[]{cat, sampleInfo.getBasicInfo().fastq, "|", razorBin.getExecFile(), "-p", config.getRazorWorker() + "", "-v", "-c", config.getMHRazorConfig(), "1>", mhOut};
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            Utils.RunCommand(new String[]{"/bin/bash", "-c"}, flankingRazer);
        } else if (System.getProperty("os.name").toLowerCase().contains("mac os x")) {
            Utils.RunCommand(new String[]{"/bin/bash", "-c"}, flankingRazer);
        } else if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            String[] strings = {sampleInfo.getBasicInfo().fastq, razorBin.getExecFile(),config.getFlankingConfigFile(),mhOut};
            for (int i = 0; i < strings.length; i++) {
                if (strings[i].contains("\\") || strings[i].contains("/")) {
                    strings[i] = strings[i].replaceAll("\\\\", "\\\\\\\\");
                    strings[i] = strings[i].replaceAll("/", "\\\\\\\\");
                }
                if (!"".equals(strings[i])) {
                    strings[i] = Utils.RunCommand(new String[]{"cmd.exe", "/c"}, new String[]{"wsl", "wslpath", "-a", strings[i]}).trim();
                }
            }
            Utils.RunCommand(new String[]{"cmd", "/c", "bash", "-c"}, new String[]{cat, strings[0], "|", strings[1], "-p", config.getRazorWorker() + "", "-v", "-c", strings[2], "1>", strings[3]});

        }

    }

}
