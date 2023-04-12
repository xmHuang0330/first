
import fayi.Analyse;
import fayi.CoreSeqCompress;
import fayi.WriteExcel.SingleExcel;
import fayi.config.Config;
import fayi.config.Enum.Gender;
import fayi.config.Enum.Panel;
import fayi.seqParser.RazorOutParse;
import fayi.seqParser.SE400FqTools;
import fayi.tableObject.SampleInfo;
import fayi.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;


@Slf4j
@SpringBootTest(classes = test.APP.class)
public class AnalyseTest {
    @Autowired
    public CoreSeqCompress compress;
    @Autowired
    private RazorOutParse razorOutParse;
    @Autowired
    SE400FqTools se400FqTools;

    @Value("test.artifactId")
    String artifact;

    @Test
    public void test() throws Exception {

        Config instance = Config.ofPanel( Panel.setB.name );
        instance.setNoRazor(true);
        instance.setNoFlanking( false );
        instance.setWorker( "3".getBytes()[0] );
        instance.setRazorWorker( "2" );
        instance.setNoNoiseFilter(true);
        instance.setQualityFilter( true );
        System.out.println(Utils.isBiallelicLocus( Gender.female, "Amelogenin" ));
        System.out.println(Utils.isBiallelicLocus( Gender.male, "Amelogenin" ));
//        instance.setMix(true);
        instance.setNoFilterDepth(100d);

        instance.setSampleFile("/Users/kaidan/Downloads/L01.xml");


        instance.setOutput( "/Users/kaidan/Downloads/L01_result.xml" );
        Analyse analyse = new Analyse( se400FqTools, razorOutParse, compress );

        ArrayList<SampleInfo> sampleInfos = analyse.start( instance.getSampleFile() );
        Utils.checkDir("/Users/kaidan/Downloads/L01_reports/");
        instance.setOutput("/Users/kaidan/Downloads/L01_reports/");
        new SingleExcel().start(sampleInfos);

    }

    @Test
    public void testb() {
        int a = 3;
        int b = 2;
        float c = 3.0f;
        System.out.println(a/b);
        System.out.println(c/b);
        System.out.println(3/2.0);
    }
}
