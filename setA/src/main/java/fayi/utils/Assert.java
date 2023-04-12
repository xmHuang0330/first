package fayi.utils;

import fayi.tableObject.StrInfo;

public class Assert {

    public static void assertStrSequenceToBeCompressed(StrInfo strInfo) throws SetAException {

        for (Integer count : strInfo.getCoreSeqCount().values()) {
            if (count > 1) {
                return;
            }
        }
        throw new SetAException(1, "sequence not compressed...");

    }

}
