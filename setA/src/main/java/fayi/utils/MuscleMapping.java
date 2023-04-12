package fayi.utils;

import fayi.config.Enum.Exec;

import java.util.HashMap;

public class MuscleMapping {

    private final LocalCommand localCommand;

    private static MuscleMapping muscleMapping = null;
    private MuscleMapping(){
        localCommand = new LocalCommand(Exec.muscle);
    }

    public static MuscleMapping getInstance(){
        if(muscleMapping != null){
            return muscleMapping;
        }
        muscleMapping = new MuscleMapping();
        return muscleMapping;
    }


    public void createInFile(HashMap<String, String> originalFastaData, String inFile) {

        FileUtils fileUtils = new FileUtils(inFile);
        for (String head : originalFastaData.keySet()) {
            fileUtils.writeLine(">" + head);
            fileUtils.writeLine(originalFastaData.get(head));
        }
        fileUtils.finishWrite();
    }

    public HashMap<String, String> runAndGetResult(String ref, String alt) throws SetAException {
        if(ref.length()<1 || alt.length()<1)
            throw new SetAException( 201, "比对序列不可以为空。" );
        String result = "";
        if(System.getProperty("os.name").toLowerCase().contains("linux")) {
            result = Utils.RunCommand(new String[]{"bash", "-c"}, new String[]{"echo", String.format("\">REF\n%s\n>ALT\n%s\"", ref, alt), "|", localCommand.getExecFile(), "-quiet"}).trim();
        }else if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            String[] strings = {localCommand.getExecFile()};
            for (int i = 0; i < strings.length; i++) {
                if (strings[i].contains("\\") || strings[i].contains("/")) {
                    strings[i] = strings[i].replaceAll("\\\\", "\\\\\\\\");
                    strings[i] = strings[i].replaceAll("/", "\\\\\\\\");
                }
                if (!"".equals(strings[i])) {
                    strings[i] = Utils.RunCommand(new String[]{"cmd.exe", "/c"}, new String[]{"wsl", "wslpath", "-a", strings[i]}).trim();
                }
            }
            result = Utils.RunCommand(new String[]{"bash", "-c"}, new String[]{"echo", String.format("'>REF\n%s\n>ALT\n%s'", ref, alt), "|", strings[0],"-quiet"}).trim();
        }else if(System.getProperty("os.name").toLowerCase().contains("mac")){
            result = Utils.RunCommand(new String[]{"bash", "-c"}, new String[]{"echo", String.format("\">REF\n%s\n>ALT\n%s\"", ref, alt), "|", "muscle", "-quiet"}).trim();

        }
        return getResult(result);
    }

    private HashMap<String, String> getResult(String output) {
        String header = "";
        StringBuilder sequence = new StringBuilder();
        HashMap<String, String> fasta = new HashMap<>();
        for (String line : output.split("\n")) {
            if (line.startsWith(">")) {
                fasta.put(header, sequence.toString());
                header = line.substring(1);
                sequence = new StringBuilder();
            } else {
                sequence.append(line.trim());
            }
        }
        fasta.put(header, sequence.toString());
        fasta.remove("");
        return fasta;
    }

}
