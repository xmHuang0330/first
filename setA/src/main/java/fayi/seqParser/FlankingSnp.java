package fayi.seqParser;

import fayi.config.Config;
import fayi.config.Param;
import fayi.tableObject.MappingResult;
import fayi.tableObject.StrInfo;
import fayi.utils.SetAException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.Ansi.ansi;


public class FlankingSnp {

    SequenceMapping sequenceMapping;
    public FlankingSnp() {
        sequenceMapping = new SequenceMapping();
    }

    public static void main(String[] args) throws SetAException {
        CorePicker corePicker = new CorePicker();
        FlankingSnp flankingSnp = new FlankingSnp();
        Config.getInstance();
        StrInfo refStr = new StrInfo("vWA", "23", false, 93, "AGGAAACAGGTCTAAAGGAACCAAGTTGACTTGGCTGAGATGTGAAAGCCCTAGTGGATGATAAGAATAATCAGTATGTGACTTGGATTGATCTATCTGTCTATCTGTCTGTCTGTCTGTCTATCTATCTATCCATCTATCTATCTATCCATCCA", 622, 0);
        HashMap<String, StrInfo> stringStrInfoHashMap = new ReadReference().readRef(Config.getInstance().getProjectPath() + "/resource/setB.fasta");
        StrInfo strInfo = stringStrInfoHashMap.get("vWA");

        corePicker.calCoreSTR(refStr);
        corePicker.calCoreSTR(strInfo);
        flankingSnp.flankingSnp(strInfo, refStr);
        strInfo.formatRepeatSequence(false);
        System.out.println(refStr.getRepeatSequence());
        System.out.println(strInfo.getRepeatSequence());
    }

    public void flankingSnp(StrInfo refStr, StrInfo alleleStr) throws SetAException {
//        singleRepeatMerge(refStr);
//        singleRepeatMerge(alleleStr);

//        refStr.formatRepeatSequence(true);
//        alleleStr.formatRepeatSequence(true);
        sequenceMapping.arrayMapper(refStr, alleleStr);

        if (!refStr.getNoneCoreseq().equals(alleleStr.getNoneCoreseq())) {
//            System.out.println(refStr.getLocus());
            for (int i = 0; i < refStr.getNoneCoreseq().size(); i++) {

                if("-".equals(refStr.getNoneCoreseq().get(i))){
                    HashMap<Integer, Integer> ref_Count = new HashMap<>();
                    ArrayList<Integer> integers = new ArrayList<>(refStr.getCoreSeqCount().keySet());
                    Collections.reverse(integers);
                    for(Integer key :integers){
                        if(key >= i){
                            ref_Count.put(key + 1,refStr.getCoreSeqCount().get(key));
                        }else{
                            ref_Count.put(key ,refStr.getCoreSeqCount().get(key));
                        }
                    }
                    refStr.setCoreSeqCount(ref_Count);
                }else if("-".equals(alleleStr.getNoneCoreseq().get(i))) {
                    HashMap<Integer, Integer> allele_Count = new HashMap<>();
                    ArrayList<Integer> integers = new ArrayList<>(alleleStr.getCoreSeqCount().keySet());
                    Collections.reverse(integers);
                    for(Integer key :integers){
                        if(key >= i){
                            allele_Count.put(key + 1,alleleStr.getCoreSeqCount().get(key));
                        }else{
                            allele_Count.put(key, alleleStr.getCoreSeqCount().get(key));
                        }
                    }
                    alleleStr.setCoreSeqCount(allele_Count);
                }

            }
            diff(refStr,alleleStr,refStr.getLocus());
            System.out.print("   {");
            for (int i = 0; i < refStr.getNoneCoreseq().size(); i++) {
                if("-".equals(refStr.getNoneCoreseq().get(i))){
                    System.out.print(refStr.getNoneCoreseq().get(i) + ansi().fg(WHITE).a("[0]").reset() + " ");
                }else {
                    System.out.print(refStr.getNoneCoreseq().get(i) + ansi().fg(Param.getInstance().locusSTR.get(refStr.getLocus()).contains(alleleStr.getNoneCoreseq().get(i))?GREEN:DEFAULT).a("[" + refStr.getCoreSeqCount().getOrDefault(i, 1) + "]").reset() + " ");
                }
            }
            System.out.print("}, \n"+alleleStr.getAlleleName()+"{");
            for (int i = 0; i < alleleStr.getNoneCoreseq().size(); i++) {
                if("-".equals(alleleStr.getNoneCoreseq().get(i))){
                    System.out.print(alleleStr.getNoneCoreseq().get(i) + ansi().fg(WHITE).a("[0]").reset() + " ");
                }else {
                    System.out.print(alleleStr.getNoneCoreseq().get(i) + ansi().fg( Param.getInstance().locusSTR.get(refStr.getLocus()).contains(alleleStr.getNoneCoreseq().get(i))?GREEN:DEFAULT).a("[" + alleleStr.getCoreSeqCount().getOrDefault(i , 1) + "]").reset() + " ");
                }
            }
            System.out.println("}");
        }
        refStr.formatRepeatSequence(true);
        alleleStr.formatRepeatSequence(false);


    }
    private void diff(StrInfo refStr, StrInfo alleleStr,String locus){
        for (int i = 0; i < refStr.getNoneCoreseq().size(); i++) {
            if(!refStr.getNoneCoreseq().get(i).equals(alleleStr.getNoneCoreseq().get(i))){
                if(refStr.getNoneCoreseq().get(i).equals("-")){
                    if(i+1 < refStr.getNoneCoreseq().size() && refStr.getNoneCoreseq().get(i+1).equals("-")){
                        if(i+2 < refStr.getNoneCoreseq().size() && refStr.getNoneCoreseq().get(i+2).equals(alleleStr.getNoneCoreseq().get(i+2))
                                && Param.getInstance().locusSTR.get(locus).contains(refStr.getNoneCoreseq().get(i+2)) ){
                            String a = refStr.getNoneCoreseq().get(i + 2);
                            String b = alleleStr.getNoneCoreseq().get(i + 1);
                            alleleStr.getNoneCoreseq().set(i+1,mark(a, b)[1]);
//                                refStr.getNoneCoreseq().set(i+1,mark(b, a)[1]);
                            i += 2;

                        }else{
//                            System.err.println(String.format("sequence not in common pattern,locus = %s position = %s",locus,i));
//                            mappingResult mapper = SequenceMapping.mapper((refStr.getNoneCoreseq().get(i) + refStr.getNoneCoreseq().get(i + 1) + refStr.getNoneCoreseq().get(i + 2)).replaceAll("-", "")
//                                    , (alleleStr.getNoneCoreseq().get(i) + alleleStr.getNoneCoreseq().get(i + 1) + alleleStr.getNoneCoreseq().get(i + 2)).replaceAll("-", ""));
//                                refStr.getNoneCoreseq().set(i, mark(mapper.sequence_A,mapper.sequence_B)[0]);
//                                alleleStr.getNoneCoreseq().set(i, mark(mapper.sequence_A,mapper.sequence_B)[1]);
//                                refStr.getNoneCoreseq().remove(i+1);
//                                refStr.getNoneCoreseq().remove(i+1);
//                                alleleStr.getNoneCoreseq().remove(i+1);
//                                alleleStr.getNoneCoreseq().remove(i+1);
                        }
                    }else{

                    }
                }else if(alleleStr.getNoneCoreseq().get(i).equals("-")){
                    if(i+1 < refStr.getNoneCoreseq().size() && alleleStr.getNoneCoreseq().get(i+1).equals("-")){
                        if(i+2 < refStr.getNoneCoreseq().size() ){
                            if( alleleStr.getNoneCoreseq().get(i+2).equals(refStr.getNoneCoreseq().get(i+2))
                                    && Param.getInstance().locusSTR.get(locus).contains(alleleStr.getNoneCoreseq().get(i+2))){
//                                String a = refStr.getNoneCoreseq().get(i + 1);
//                                String b = alleleStr.getNoneCoreseq().get(i + 2);
//                                refStr.getNoneCoreseq().set(i+1,mark(a,b)[0]);
//                                alleleStr.getNoneCoreseq().set(i,refStr.getNoneCoreseq().get(i));
//                                alleleStr.getCoreSeqCount().put(i,0);
//                                alleleStr.getNoneCoreseq().set(i+1,mark(a,b)[0]);
//                                alleleStr.getCoreSeqCount().put(i+1,0);
                                i += 2;
                            }else{
//                                System.out.println(String.format("sequence not in common pattern,locus = %s position = %s",locus,i));
//                                mappingResult mapper = SequenceMapping.mapper((refStr.getNoneCoreseq().get(i) + refStr.getNoneCoreseq().get(i + 1) + refStr.getNoneCoreseq().get(i + 2)).replaceAll("-", "")
//                                        , (alleleStr.getNoneCoreseq().get(i) + alleleStr.getNoneCoreseq().get(i + 1) + alleleStr.getNoneCoreseq().get(i + 2)).replaceAll("-", ""));
//                                refStr.getNoneCoreseq().set(i, mark(mapper.sequence_A,mapper.sequence_B)[0]);
//                                alleleStr.getNoneCoreseq().set(i, mark(mapper.sequence_A,mapper.sequence_B)[1]);
//                                refStr.getNoneCoreseq().remove(i+1);
//                                refStr.getNoneCoreseq().remove(i+1);
//                                alleleStr.getNoneCoreseq().remove(i+1);
//                                alleleStr.getNoneCoreseq().remove(i+1);
                                break;
                            }
                        }
                    }else {
                        alleleStr.getNoneCoreseq().set(i,alleleStr.getNoneCoreseq().get(i));
                        alleleStr.getCoreSeqCount().put(i,-1 * refStr.getCoreSeqCount().getOrDefault(i,1));

                    }
                }else{
                    if(refStr.getNoneCoreseq().get(i).indexOf("-") == 0){
                        
                    }else if(refStr.getNoneCoreseq().get(i).lastIndexOf("-") == refStr.getNoneCoreseq().get(i).length()-1){
                        
                    }else {
                        String[] mark = mark(refStr.getNoneCoreseq().get(i), alleleStr.getNoneCoreseq().get(i));
//                        refStr.getNoneCoreseq().set(i, mark[0]);
                        alleleStr.getNoneCoreseq().set(i, mark[1]);
                    }
                }
            }
        }
    }

    private String[] mark(String a,String b){
        StringBuilder result_a = new StringBuilder();
        StringBuilder result_b = new StringBuilder();
        MappingResult mapper = sequenceMapping.gatkMapper(a, b);
        a=mapper.sequence_A;
        b=mapper.sequence_B;
        for (int i = 0; i < a.length(); i++) {
            if(a.charAt(i) != b.charAt(i) && !("-".equals(b) || "-".equals(a))){
                result_a.append(">").append(a.charAt(i)).append("<");
                result_b.append(">").append(b.charAt(i)).append("<");
            }else{
                result_a.append(a.charAt(i));
                result_b.append(b.charAt(i));
            }
        }
        return new String[]{result_a.toString(), result_b.toString()};
    }
}

