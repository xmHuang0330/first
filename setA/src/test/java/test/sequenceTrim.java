package test;

import fayi.APP;
import fayi.config.Config;
import fayi.config.Enum.Panel;
import fayi.config.sequenceTrim.*;
import fayi.seqParser.CorePicker;
import fayi.seqParser.RazorOutParse;
import fayi.tableObject.StrInfo;
import fayi.utils.SetAException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {APP.class})
public class sequenceTrim {


    @Test
    public static void D3S3045() throws SetAException {
        Config.getInstance();
        D3S3045Handler d3S3045Handler = new D3S3045Handler();
        StrInfo strInfo = new StrInfo(d3S3045Handler.getClass().getAnnotation(locusHandler.class).locusName(), "13", true, 54, "AGATAGATAGATAGATATAGATAGATAGATAGATAGATAGATAAATAGATAGAT", 0, 500);

        RazorOutParse razorOutParse = new RazorOutParse();
        razorOutParse.trimSequence(strInfo);

        CorePicker corePicker = new CorePicker();
        corePicker.calCoreSTR(strInfo);
        d3S3045Handler.sequenceCompress(strInfo);
        System.out.println(strInfo.getRepeatSequence());
    }


    @Test
    public static void DYS449() throws SetAException {
        Config.getInstance();
        DYS449Handler dys449Handler = new DYS449Handler();

        StrInfo strInfo = new StrInfo(dys449Handler.getClass().getAnnotation(locusHandler.class).locusName(), "32", true, 100, "ATTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTCTCTCTCCTCCTCTTTCTTTCCTTCTTTCTTTCTTTTCCTCTTTCCTTCCTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTT", 0, 500);
        StrInfo strInfo1 = new StrInfo(dys449Handler.getClass().getAnnotation(locusHandler.class).locusName(), "28", true, 100, "ATTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTCTCTCTCCTCCTCTTTCTTTCCTTCTTTCTTTCTTTTCCTCTTTCCTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTCTTTTTTTCTTTCTTTC", 0, 500);

        RazorOutParse razorOutParse = new RazorOutParse();
        razorOutParse.trimSequence(strInfo);
        razorOutParse.trimSequence(strInfo1);

        CorePicker corePicker = new CorePicker();
        corePicker.calCoreSTR(strInfo);
        corePicker.calCoreSTR(strInfo1);

        dys449Handler.sequenceCompress(strInfo);
        dys449Handler.sequenceCompress(strInfo1);
        System.out.println(strInfo.getRepeatSequence());
//        System.out.println(strInfo.getNs());
        System.out.println(strInfo1.getRepeatSequence());
//        System.out.println(strInfo.getNs());
    }


    @Test
    public static void DYS448() throws SetAException {
        Config.getInstance();
        DYS448Handler dys448Handler = new DYS448Handler();

        StrInfo strInfo = new StrInfo(dys448Handler.getClass().getAnnotation(locusHandler.class).locusName(), "22", true, 100, "AGAGATAGAGATAGAGATAGAGATAGAGATAGAGATAGAGATAGAGATAGAGATAGAGATAGAGATAGAGATATAGAGATAGAGAGATAGAGATAGAGATAGATAGATAGAGAAAGAGATAGAGATAGAGATAGAGATAGAGATAGAGATAGAGATAGAGATAGAGATAGAGAT", 0, 500);
        StrInfo strInfo1 = new StrInfo(dys448Handler.getClass().getAnnotation(locusHandler.class).locusName(), "18", true, 100, "AGAGATAGAGATAGAGATAGAGATAGAGATAGACATAGAGATAGAGATAGAGATAGAGATATAGAGATAGAGAGATAGAGGTAGAGATAGATAGATAGAGAAAGAGATAGAGATAGAGATAGAGATAGAGATAGAGATAGAGATAGAGAT", 0, 500);

        RazorOutParse razorOutParse = new RazorOutParse();
        razorOutParse.trimSequence(strInfo);
        razorOutParse.trimSequence(strInfo1);

        CorePicker corePicker = new CorePicker();
        corePicker.calCoreSTR(strInfo);
        corePicker.calCoreSTR(strInfo1);

        dys448Handler.sequenceCompress(strInfo);
        dys448Handler.sequenceCompress(strInfo1);
        System.out.println(strInfo.getRepeatSequence());
        System.out.println(strInfo1.getRepeatSequence());
    }

    @Test
    public static void D8S1132() throws SetAException {
        System.setProperty("config.Artifact", "setB");
        Config.getInstance();
        D8S1132Handler d8S1132Handler = new D8S1132Handler();

        StrInfo strInfo = new StrInfo(d8S1132Handler.getClass().getAnnotation(locusHandler.class).locusName(), "21", true, 100, "TCTATCTATCTATCTATCTATCTATCTATCTATCATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTGTCTA", 0, 500);
        StrInfo strInfo1 = new StrInfo(d8S1132Handler.getClass().getAnnotation(locusHandler.class).locusName(), "21.1", true, 100, "TCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTGTCTA", 0, 500);

        RazorOutParse razorOutParse = new RazorOutParse();
        razorOutParse.trimSequence(strInfo);
        razorOutParse.trimSequence(strInfo1);

        CorePicker corePicker = new CorePicker();
        corePicker.calCoreSTR(strInfo);
        corePicker.calCoreSTR(strInfo1);
        strInfo.formatRepeatSequence(false);
        strInfo1.formatRepeatSequence(false);

        d8S1132Handler.sequenceCompress(strInfo);
        d8S1132Handler.sequenceCompress(strInfo1);
        System.out.println(strInfo.getRepeatSequence());
        System.out.println(strInfo1.getRepeatSequence());
    }

    @Test
    public static void D13S325() throws SetAException {
        StrInfo strInfo = new StrInfo("D13S325", "21", true, 51, "TCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCATCCATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTA", 0, 500);

        RazorOutParse razorOutParse = new RazorOutParse();
        razorOutParse.trimSequence(strInfo);

        CorePicker corePicker = new CorePicker();
        corePicker.calCoreSTR(strInfo);
        D13S325Handler d13S325Handler = new D13S325Handler();
        d13S325Handler.sequenceCompress(strInfo);
        System.out.println(strInfo.getRepeatSequence());
    }

    @Test
    public static void D17S1290() throws SetAException {

        System.setProperty("config.Artifact", Panel.setB.name());
        StrInfo strInfo = new StrInfo("D17S1290", "18", true, 100, "AGATAGATAGATAGATGATGATAGATATATAGATATATAGATATATAGATATATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAG", 0, 500);
        StrInfo strInfo1 = new StrInfo("D17S1290", "18", true, 100, "AGATAAATAGATAGATGATGATATATATATAGATATATAGATATATAGAGATAGATAGATAGATAGATAGATAGATAGATAGATAGAT", 0, 500);

        RazorOutParse razorOutParse = new RazorOutParse();
        razorOutParse.trimSequence(strInfo);
        razorOutParse.trimSequence(strInfo1);

        CorePicker corePicker = new CorePicker();
        corePicker.calCoreSTR(strInfo);
        corePicker.calCoreSTR(strInfo1);
        D17S1290Handler d17S1290Handler = new D17S1290Handler();
        d17S1290Handler.sequenceCompress(strInfo);
        d17S1290Handler.sequenceCompress(strInfo1);
        System.out.println(strInfo.getRepeatSequence());
        System.out.println(strInfo1.getRepeatSequence());
    }


    @Test
    public static void D18S535() throws SetAException {
        Config.getInstance();
        D18S535Handler d18S535Handler = new D18S535Handler();
        StrInfo strInfo = new StrInfo( d18S535Handler.getClass().getAnnotation( locusHandler.class ).locusName(), "15", true, 100, "AGATAGACAGATAGATGGTAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATATAG", 0, 500 );
        StrInfo strInfo1 = new StrInfo( d18S535Handler.getClass().getAnnotation( locusHandler.class ).locusName(), "15", true, 100, "AGATAGACAGGTAGATGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATATAG", 0, 500 );

        strInfo.setTrimmedSeq( strInfo.getOriginalSeq() );
        strInfo1.setTrimmedSeq( strInfo1.getOriginalSeq() );

        CorePicker corePicker = new CorePicker();
        corePicker.calCoreSTR( strInfo );
        corePicker.calCoreSTR(strInfo1);
        d18S535Handler.sequenceCompress(strInfo);
        System.out.println("=======");
        d18S535Handler.sequenceCompress(strInfo1);
        System.out.println(strInfo.getRepeatSequence());
        System.out.println(strInfo1.getRepeatSequence());
    }

    @Test
    public static void D19S433() throws SetAException {
        Config.getInstance();
        D19S433Handler d19S433Handler = new D19S433Handler();

        StrInfo strInfo = new StrInfo(d19S433Handler.getClass().getAnnotation(locusHandler.class).locusName(), "13.1", true, 100, "CCTTCCTTCCTTCCTTCCTTCCTTCCTTCCTTCCTTCCTTCCTTCCTTACCTTCTTTCCTT", 0, 500);
        StrInfo strInfo1 = new StrInfo(d19S433Handler.getClass().getAnnotation(locusHandler.class).locusName(), "13.2", true, 100, "CCTTCCTTCCTTCCTTCCTTCCTTCCTTCCTTCCTTCCTTCCTTCCTTCCTACCTTTTCCTT", 0, 500);
        StrInfo strInfo2 = new StrInfo(d19S433Handler.getClass().getAnnotation(locusHandler.class).locusName(), "14", true, 100, "TCTTCCTTCCTTCCTTCCTTCCTTCCTTCCTTCCTTCCTTCCTTCCTTCCTACCTTCTTTCCTT", 0, 500);

        RazorOutParse razorOutParse = new RazorOutParse();
        razorOutParse.trimSequence(strInfo);
        razorOutParse.trimSequence(strInfo1);
        razorOutParse.trimSequence(strInfo2);

        CorePicker corePicker = new CorePicker();
        corePicker.calCoreSTR(strInfo);
        corePicker.calCoreSTR(strInfo1);
        corePicker.calCoreSTR(strInfo2);

        d19S433Handler.sequenceCompress(strInfo);
        d19S433Handler.sequenceCompress(strInfo1);
        d19S433Handler.sequenceCompress(strInfo2);
        System.out.println(strInfo.getRepeatSequence());
        System.out.println(strInfo1.getRepeatSequence());
        System.out.println(strInfo2.getRepeatSequence());
        System.out.println(strInfo2.getNs());
    }

    @Test
    public static void D21S11() throws SetAException {
        Config.getInstance();
        D21S11Handler d21S11Handler = new D21S11Handler();
        String locus = d21S11Handler.getClass().getAnnotation(locusHandler.class).locusName();

        StrInfo strInfo = new StrInfo(locus, "28.2", true, 100, "TCTATCTATCTATCTATCTATCTGTCTGTCTGTCTGTCTGTCCGTCTATCTATCTATATCTATCTATCTATCATCTATCTATCCATATCTATCTATCTATCTATCTATCTATCTATCTATATCTA", 0, 500);
        StrInfo strInfo1 = new StrInfo(locus, "28", true, 100, "TCTATCTATCTATCTATCTATCTGTCTGTCTGTCTGTCTGTCTGTCTATCTATCTATATCTATCTATCTATCATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATATCTA", 0, 500);

        RazorOutParse razorOutParse = new RazorOutParse();
        razorOutParse.trimSequence(strInfo);
        razorOutParse.trimSequence(strInfo1);

        CorePicker corePicker = new CorePicker();
        corePicker.calCoreSTR(strInfo);
        corePicker.calCoreSTR(strInfo1);

        d21S11Handler.sequenceCompress(strInfo);
        d21S11Handler.sequenceCompress(strInfo1);
        System.out.println(strInfo.getRepeatSequence());
        System.out.println(strInfo1.getRepeatSequence());
    }


    @Test
    public static void DYS19() throws SetAException {
        Config.getInstance();
        DYS19Handler dys19Handler = new DYS19Handler();

        StrInfo strInfo = new StrInfo(dys19Handler.getClass().getAnnotation(locusHandler.class).locusName(), "16", true, 100, "TCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTACCTATCCATCTATCTA", 0, 500);
        StrInfo strInfo1 = new StrInfo(dys19Handler.getClass().getAnnotation(locusHandler.class).locusName(), "16", true, 100, "GCTATCTATCTATCTATCTATCTATCTATCTACCTATCTATCTATCTATCTACCTATCTATCTATCTA", 0, 500);

        RazorOutParse razorOutParse = new RazorOutParse();
        razorOutParse.trimSequence(strInfo);
        razorOutParse.trimSequence(strInfo1);

        CorePicker corePicker = new CorePicker();
        corePicker.calCoreSTR(strInfo);
        corePicker.calCoreSTR(strInfo1);

        dys19Handler.sequenceCompress(strInfo);
        dys19Handler.sequenceCompress(strInfo1);
        System.out.println(strInfo.getRepeatSequence());
        System.out.println(strInfo1.getRepeatSequence());
    }

    @Test
    public static void DYS389I() throws SetAException {
        Config.getInstance();
        DYS389IIHandler dys389IIHandler = new DYS389IIHandler();

        StrInfo dys389i = new StrInfo("DYS389I", "16", true, 100, "TAGATAGATAGATAGATAGATAGATAGATAGATAGATAGACAGACAAACAGA", 0, 500);
        StrInfo strInfo = new StrInfo(dys389IIHandler.getClass().getAnnotation(locusHandler.class).locusName(), "16", true, 100, "TAGATAGATAGATAGATAGATAGATAGATAGATAGACAGACAGACAGATACATAGATAATACAGATGAGAGTTGGATACAGAAGTAGGTATAATGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGACAGACAGACAGACAGA", 0, 500);
        StrInfo strInfo1 = new StrInfo(dys389IIHandler.getClass().getAnnotation(locusHandler.class).locusName(), "16", true, 100, "TAGATAGATAGATAGATAGATAGATAGATAGATAGATAGACAGACAAACAGATACATAGATAATACAGATGAGAGTGGATACAGAAGTAGGTATAATGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGACAGACAGACAGACAGACAGACACACA", 0, 500);

        strInfo.setTrimmedSeq(strInfo.getRepeatSequence());
        strInfo1.setTrimmedSeq(strInfo1.getOriginalSeq());

        CorePicker corePicker = new CorePicker();
        corePicker.calCoreSTR(strInfo);
        corePicker.calCoreSTR(strInfo1);

        dys389IIHandler.sequenceCompress(strInfo);
        dys389IIHandler.sequenceCompress(strInfo1);
        System.out.println(strInfo.getRepeatSequence());
        System.out.println(strInfo.getNs());
        System.out.println(strInfo1.getRepeatSequence());
    }


    @Test
    public static void DYS518() throws SetAException {
        Config.getInstance();
        DYS518handler dys518handler = new DYS518handler();

        StrInfo strInfo = new StrInfo(dys518handler.getClass().getAnnotation(locusHandler.class).locusName(), "40", true, 100, "AAAGAAAGAAAGGAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGGGAGAAAGGAAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAG", 0, 500);
        StrInfo strInfo1 = new StrInfo(dys518handler.getClass().getAnnotation(locusHandler.class).locusName(), "30", true, 100, "AAAGAAAGAAAGGAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGGGAGAAAGGAAGAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAG", 0, 500);

        RazorOutParse razorOutParse = new RazorOutParse();
        razorOutParse.trimSequence(strInfo);
        razorOutParse.trimSequence(strInfo1);

        CorePicker corePicker = new CorePicker();
        corePicker.calCoreSTR(strInfo);
        corePicker.calCoreSTR(strInfo1);

        dys518handler.sequenceCompress(strInfo);
        dys518handler.sequenceCompress(strInfo1);
        System.out.println(strInfo.getRepeatSequence());
        System.out.println(strInfo.getNs());
        System.out.println(strInfo1.getRepeatSequence());
        System.out.println(strInfo1.getNs());
    }

    @Test
    public static void DYS630() throws SetAException {
        Config.getInstance();
        DYS630Handler dys630Handler = new DYS630Handler();

        StrInfo strInfo = new StrInfo(dys630Handler.getClass().getAnnotation(locusHandler.class).locusName(), "19", true, 100, "AAAGAAAGAAAGAAAGAGAAAGAGAGAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAG".replaceAll(" ", ""), 0, 500);
        StrInfo strInfo1 = new StrInfo(dys630Handler.getClass().getAnnotation(locusHandler.class).locusName(), "23.1", true, 100, "AAAGAAAGAAAGAAAGAGAGAGAGAGAAAGAGAGAAAGAGAGAAAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAAAAGAAAGAAAG".replaceAll(" ", ""), 0, 500);
        StrInfo strInfo2 = new StrInfo(dys630Handler.getClass().getAnnotation(locusHandler.class).locusName(), "19", true, 100, "AAAGAAAGAAAGAAAGAGAGAGAGAGAGAAAGAGAGAAAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAGAAAG".replaceAll(" ", ""), 0, 500);

        RazorOutParse razorOutParse = new RazorOutParse();
        razorOutParse.trimSequence(strInfo);
        razorOutParse.trimSequence(strInfo1);
        razorOutParse.trimSequence(strInfo2);

        CorePicker corePicker = new CorePicker();
        corePicker.calCoreSTR(strInfo);
        corePicker.calCoreSTR(strInfo1);
        corePicker.calCoreSTR(strInfo2);

        dys630Handler.sequenceCompress(strInfo);
        dys630Handler.sequenceCompress(strInfo1);
        dys630Handler.sequenceCompress(strInfo2);
        System.out.println(strInfo.getRepeatSequence());
        System.out.println(strInfo1.getRepeatSequence());
        System.out.println(strInfo2.getRepeatSequence());
    }

    @Test
    public static void DYS552() throws SetAException {
        Config.getInstance();
        DYS552Handler dys552Handler = new DYS552Handler();

        StrInfo strInfo = new StrInfo(dys552Handler.getClass().getAnnotation(locusHandler.class).locusName(), "24", true, 100, "TCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTA ATCTATAATCTATCCATCCTTCTATCTATTTCATCTATCT TCTATCTATCTATCTATCTATCTATCTATCTATCTA".replaceAll(" ", ""), 0, 500);
        StrInfo strInfo1 = new StrInfo(dys552Handler.getClass().getAnnotation(locusHandler.class).locusName(), "24", true, 100, "TCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTATCTA ATCTATAATCTATCCATCCTTCTA CCTGTTTCA TCTATCT TCTATCTATCTATCTATCTATCTATCTATCTATCTA".replaceAll(" ", ""), 0, 500);

        RazorOutParse razorOutParse = new RazorOutParse();
        razorOutParse.trimSequence(strInfo);
        razorOutParse.trimSequence(strInfo1);

        CorePicker corePicker = new CorePicker();
        corePicker.calCoreSTR(strInfo);
        corePicker.calCoreSTR(strInfo1);

        dys552Handler.sequenceCompress(strInfo);
        dys552Handler.sequenceCompress(strInfo1);
        System.out.println(strInfo.getRepeatSequence());
        System.out.println(strInfo1.getRepeatSequence());
    }

    @Test
    public static void D4S2366() throws SetAException {
        Config.getInstance();
        D4S2366Handler d4S2366Handler = new D4S2366Handler();
        StrInfo strInfo = new StrInfo(d4S2366Handler.getClass().getAnnotation(locusHandler.class).locusName(), "15", true, 59, "GATAGATAGATAGATAGATAGATAGATAGATGGATAGATAGATTGATTGATAGACGATAGATA", 0, 500);
        StrInfo strInfo1 = new StrInfo(d4S2366Handler.getClass().getAnnotation(locusHandler.class).locusName(), "15", true, 59, "GATAGATAGATAGATAGATAGATAGATAGATAGATAGATAGATTGATTGATAGGCGATAGATA", 0, 500);

        RazorOutParse razorOutParse = new RazorOutParse();
        razorOutParse.trimSequence(strInfo);
        razorOutParse.trimSequence(strInfo1);

        CorePicker corePicker = new CorePicker();
        corePicker.calCoreSTR(strInfo);
        corePicker.calCoreSTR(strInfo1);
        d4S2366Handler.sequenceCompress(strInfo);
        d4S2366Handler.sequenceCompress(strInfo1);
        System.out.println(strInfo.getRepeatSequence());
        System.out.println(strInfo.getNs());
        System.out.println(strInfo1.getRepeatSequence());
    }
}
