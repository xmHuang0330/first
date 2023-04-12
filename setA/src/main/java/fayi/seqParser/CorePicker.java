package fayi.seqParser;

import fayi.config.Param;
import fayi.tableObject.StrInfo;
import fayi.utils.SetAException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
public class CorePicker {

    SequenceMapping sequenceMapping;

    public CorePicker() {
        sequenceMapping = new SequenceMapping();
    }


    public void calCoreSTR(StrInfo strInfo) throws SetAException {
        if (strInfo == null) {
            return;
        }
//        if(strInfo.getLocus().equals( "SRY" ))
//            System.out.println();
        ArrayList<String> noneCoreseq = new ArrayList<>();
        HashMap<Integer, Integer> repeatRecord = new HashMap<>();
        picker(strInfo.getTrimmedSeq(), noneCoreseq, repeatRecord, strInfo.getLocus());


        strInfo.setNoneCoreseq(noneCoreseq);
        strInfo.setCoreSeqCount(repeatRecord);
    }

    public void picker(String value, ArrayList<String> temp, HashMap<Integer, Integer> repeatRecord, String locus) throws SetAException {
        Param param = Param.getInstance();
        if (null == value) {
            throw new SetAException( 5, "核心序列拆分失败，trimmed sequence为null" );
        }

        boolean formerFound = false;
        int j = 0;
        while (j < value.length()) {
            int lastPos = 0;
            boolean found = false;
            String aviliable_one_mark = "";
            for (int x = 1; x <= 12; x++) {
                if ((j + x) > value.length()) {
                    break;
                }
                String firstSeq = value.substring(j, j + x);
                ArrayList<String> likeList = new ArrayList<>();
                for (int step = 0; step < (value.length() - j) / x; step++) {
                    try {
                        String step_seq = value.substring(j + x + (x * step), j + x + (x * (step + 1)));
                        if (firstSeq.equals(step_seq)) {
                            likeList.add(step_seq);
                        } else {
                            lastPos = j + x + (x * step);
                            break;
                        }
                    }catch (StringIndexOutOfBoundsException e){
                        lastPos = j + x + (x * step);
                        break;
                    }
                }
                if (likeList.size() >= (x == 1 ? 4 : 1) && param.locusSTR.get(locus).contains(firstSeq)) {
                    temp.add(firstSeq);
                    repeatRecord.put(temp.size() - 1, likeList.size() + 1);
//                    temp.addAll(likeList);
                    j = lastPos;
                    found = true;
                    aviliable_one_mark = "";
                    break;
                } else if (likeList.size() == 0 && firstSeq.length() != 1 && param.locusSTR.get(locus).contains(firstSeq)) {
                    aviliable_one_mark = firstSeq;
                }

            }
            if (!"".equals(aviliable_one_mark)) {
                temp.add(aviliable_one_mark);
                repeatRecord.put(temp.size() - 1, 1);
//                    temp.addAll(likeList);
                j = j + aviliable_one_mark.length();
                found = true;
            }
            if (!found) {
                if (temp.size() > 0 && !formerFound) {
                    temp.set(temp.size() - 1, temp.get(temp.size() - 1) + value.charAt(j));
                } else {
                    temp.add(value.charAt(j) + "");
                }
                j += 1;
            }
            formerFound = found;
        }
    }

    public void pickerWithConfig(String value, ArrayList<String> temp, HashMap<Integer, Integer> repeatRecord, String locus, ArrayList<String> locusStr) {
        boolean formerFound = false;
        int j = 0;
        while (j < value.length()) {
            int lastPos = 0;
            boolean found = false;
            String aviliable_one_mark = "";
            for (int x = 1; x <= 12; x++) {
                if ((j + x) > value.length()) {
                    break;
                }
                String firstSeq = value.substring(j, j + x);
                ArrayList<String> likeList = new ArrayList<>();
                for (int step = 0; step < (value.length() - j) / x; step++) {
                    try {
                        String step_seq = value.substring(j + x + (x * step), j + x + (x * (step + 1)));
                        if (firstSeq.equals(step_seq)) {
                            likeList.add(step_seq);
                        } else {
                            lastPos = j + x + (x * step);
                            break;
                        }
                    } catch (StringIndexOutOfBoundsException e) {
                        lastPos = j + x + (x * step);
                        break;
                    }
                }
                if (likeList.size() >= (x == 1 ? 4 : 1) && locusStr.contains(firstSeq)) {
                    temp.add(firstSeq);
                    repeatRecord.put(temp.size() - 1, likeList.size() + 1);
//                    temp.addAll(likeList);
                    j = lastPos;
                    found = true;
                    aviliable_one_mark = "";
                    break;
                } else if (likeList.size() == 0 && firstSeq.length() != 1 && locusStr.contains(firstSeq)) {
                    aviliable_one_mark = firstSeq;
                    continue;
                }

            }
            if (!"".equals(aviliable_one_mark)) {
                temp.add(aviliable_one_mark);
                repeatRecord.put(temp.size() - 1, 1);
//                    temp.addAll(likeList);
                j = j + aviliable_one_mark.length();
                found = true;
            }
            if (!found) {
                if(temp.size()>0 && !formerFound){
                    temp.set(temp.size()-1,temp.get(temp.size()-1)+value.charAt(j));
                }else{
                    temp.add(value.charAt(j)+"");
                }
                j += 1;
            }
            formerFound = found;
        }
    }

    private void lookingAlteredAnchor(String repeatSequence, String dys389IIAnchor) throws SetAException {

        if(repeatSequence.length() <= dys389IIAnchor.length()){
            throw new SetAException(1,"重复序列错误");
        }
        String matchAnchor = "";
        for (int i = 0; i < repeatSequence.length() - dys389IIAnchor.length(); i++) {
            int mismatch = 0;
            for (int j = 0; j < dys389IIAnchor.length(); j++) {
                if(repeatSequence.charAt(i+j) != dys389IIAnchor.charAt(i+j)){
                    mismatch += 1;
                    if(mismatch > 1){
                        break;
                    }
                }
            }
            if (mismatch <= 1) {
                matchAnchor = repeatSequence.substring(i, i + dys389IIAnchor.length());
            }
        }
        System.out.println(matchAnchor);

    }

    public void mergeSingleUnit(StrInfo strInfo) {

        for (int i = 1; i < strInfo.getNoneCoreseq().size() - 1; i++) {
            if (Param.getInstance().locusSTR.get(strInfo.getLocus()).contains(strInfo.getNoneCoreseq().get(i)) && strInfo.getCoreSeqCount().getOrDefault(i, 1) == 1) {
                if (!Param.getInstance().locusSTR.get(strInfo.getLocus()).contains(strInfo.getNoneCoreseq().get(i - 1))
                        && !Param.getInstance().locusSTR.get(strInfo.getLocus()).contains(strInfo.getNoneCoreseq().get(i + 1))) {
                    String merge = strInfo.getNoneCoreseq().get(i - 1) + strInfo.getNoneCoreseq().get(i) + strInfo.getNoneCoreseq().get(i + 1);
                    HashMap<Integer, Integer> replace = new HashMap<>();
                    strInfo.getNoneCoreseq().remove(i);
                    strInfo.getNoneCoreseq().remove(i);
                    strInfo.getNoneCoreseq().set(i - 1, merge);

                    for (int key : strInfo.getCoreSeqCount().keySet()) {
                        if (i < key) {
                            replace.put(key - 2, strInfo.getCoreSeqCount().get(key));
                        } else if (i == key) {
                            replace.put(key - 1, strInfo.getCoreSeqCount().get(key));
                        } else {
                            replace.put(key, strInfo.getCoreSeqCount().get(key));
                        }
                    }
                    strInfo.setCoreSeqCount(replace);
                    mergeSingleUnit(strInfo);
                    break;
                }
            }

        }
    }

}
