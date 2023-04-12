package fayi.utils;


import fayi.tableObject.StrInfo;

import java.util.List;

public class NgstutterSetting {


    public static void NGStutterSetter(StrInfo strInfo, List<StrInfo> sameLocusList, int maxNab) {
//        if(true){
//            return;
//        }
        for (StrInfo sameAlleleStrInfo : sameLocusList) {
            if (sameAlleleStrInfo.getIsNGStutter() || strInfo.equals(sameAlleleStrInfo) || sameAlleleStrInfo.getReads() > strInfo.getReads() || sameAlleleStrInfo.getTyped()) {
                continue;
            }
            if (strInfo.getAlleleName().equals(sameAlleleStrInfo.getAlleleName())) {
                if (StringUtils.commonPoly(strInfo.getOriginalSeq(), sameAlleleStrInfo.getOriginalSeq(), maxNab)) {
                    strInfo.getNGSStutter().add(sameAlleleStrInfo);
                    sameAlleleStrInfo.setIsNGStutter(true);
                    NGStutterSetter(sameAlleleStrInfo, sameLocusList, maxNab);
                }
            }
        }
    }

}
