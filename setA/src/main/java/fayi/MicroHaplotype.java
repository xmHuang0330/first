package fayi;

import fayi.config.Config;
import fayi.tableObject.*;
import fayi.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class MicroHaplotype {
    Config config;
    private final HashMap<String, RefSequence> setCFa;
    private static final String CHROM_PATTERN = "^chr[0-9]{1,2}$";
    public MicroHaplotype() throws SetAException, IOException {
        config = Config.getInstance();
        setCFa = FastaTools.read( Config.getInstance().getProjectPath() + "/resource/ref/setC.fa" );

        FileInputStream fis = new FileInputStream( Config.getInstance().getProjectPath() + "/resource/ref/24MH终极版各MH包含的SNP-rename.xlsx" );
        XSSFWorkbook wb = new XSSFWorkbook( fis );
        fis.close();
        ArrayList<ArrayList<String>> vcf = ExcelUtils.readData( wb, "vcf", 1, 401, 0, 4 );
        RefSequence current = null;
        for (ArrayList<String> row:vcf) {
            String value = row.get( 0 );
            if(!value.matches( CHROM_PATTERN )){
                if(setCFa.containsKey( value )){
                    current = setCFa.get( value );
                    continue;
                }
                log.warn( String.format( "value %s is not recognized to be a chromesome or a position in setc.ref. you may want to check! ", value));
            }else{
                if(current == null) {
                    log.error( "we should first find a mh position in mh_snp.xlsx, but didn't." );
                    continue;
                }
                current.addSnp( Integer.valueOf( row.get( 1 ) ), row.get( 3 ), row.get( 4 ) );

            }
        }
    }

    private HashMap<String, ArrayList<MHInfo>> sequenceFromOut(String outPath){
        FileUtils fileUtils = new FileUtils( outPath );
        String line;
        HashMap<String, ArrayList<MHInfo>> mhData = new HashMap<>();
        while((line = fileUtils.readLine()) != null){
            String[] split = line.split( "\t" );
            if(split.length != 5)
                continue;
            String locus = split[0].split( ":" )[0];
            int bases = Integer.parseInt( split[1].split( " " )[0] );
            String sequence = split[2];
            int left = Integer.parseInt( split[3] );
            int right = Integer.parseInt( split[4] );
            MHInfo mhInfo = new MHInfo(locus, null,false,bases,sequence,left,right);
//            mhInfo.setRepeatSequence( sequence );
            if(!mhData.containsKey( locus )){
                mhData.put( locus, new ArrayList<>() );
            }
            mhData.get( locus ).add( mhInfo );
        }
        return mhData;
    }


    public void mhType(SampleInfo sampleInfo) {

        String outPath = config.getOutputPath() + "/str_snp_out/" + sampleInfo.getBasicInfo().lane + "_" + sampleInfo.getBasicInfo().id + "_MH.out";

        List<RefSequence> collect = setCFa.values().stream()
                .filter( refSequence -> refSequence.getSnps().size() > 0 )
                .collect( Collectors.toList() );
        HashMap<String, ArrayList<MHInfo>> stringArrayListHashMap = sequenceFromOut( outPath );

        List<String> locuses = collect.stream().map( RefSequence::getName ).collect( Collectors.toList() );
        ArrayList<MHLocusInfo> mhLocusInfos = new ArrayList<>();


        for(String locus: locuses){
            if(!stringArrayListHashMap.containsKey( locus ))
                continue;
            ArrayList<MHInfo> mhInfos = stringArrayListHashMap.get( locus);
            int left = mhInfos.stream().mapToInt( MHInfo::getForward ).sum();
            int right = mhInfos.stream().mapToInt( MHInfo::getReverse ).sum();
            MHLocusInfo mhLocusInfo = new MHLocusInfo(locus, left, right);
            mhLocusInfos.add( mhLocusInfo );

            // 过滤达到 总深度的0.015
            List<MHInfo> sorted = mhInfos.stream()
                    .filter( mhInfo -> mhInfo.getReads() / mhLocusInfo.getReads() > 0.015 )
                    .sorted( (o1, o2) -> Float.compare( o2.getReads(), o1.getReads() ) )
                    .collect( Collectors.toList() );

            if (sorted.size() > 0) {

                sorted.forEach( mhInfo -> mhInfo.setAboveAT( true ) );

                sampleInfo.getMHLocusInfo().put( mhLocusInfo.getLocusName(), mhLocusInfo );
                sampleInfo.getMhData().put( locus, sorted );

//            System.out.println(sorted);

                // 占最高峰 0.2 算typed
                MHInfo first = sorted.get( 0 );
                first.setTyped( true );
                if (sorted.size() > 1) {
                    for (int i = 1; i < sorted.size(); i++) {
                        MHInfo next = sorted.get( i );
                        if (next.getReads() / first.getReads() > 0.2) {
                            next.setTyped( true );
                        }
                    }
                }

//            String collect1 = sorted.stream().filter( SeqInfo::getTyped ).map( mhInfo -> mhInfo.getReads() + "" ).collect( Collectors.joining( "," ) );
                mhLocusInfo.setAllele( new ArrayList<>( sorted.stream().filter( SeqInfo::getTyped ).collect( Collectors.toList() ) ) );
//            System.out.println(locus + " | " + collect1);
            }
        }

//        System.out.println(stringArrayListHashMap);

    }

    public void start(SampleInfo sampleInfo) throws SetAException, IOException {
        mhType( sampleInfo );
        extractMHSNP(sampleInfo.getMhData());

    }

    private static final String DEL_TAIL =  "-+$";
    private HashMap<Integer, String[]> diff(RefSequence refSequence, MHInfo mhInfo) throws SetAException {

        HashMap<String, String> mappingResult = MuscleMapping.getInstance().runAndGetResult( refSequence.getSequence(), mhInfo.getRepeatSequence() );

        String ref = mappingResult.get( "REF" );
        String alt = mappingResult.get( "ALT" );

        HashMap<Integer, String[]> diffs = new HashMap<>();
        String diff = "";

        for (int i = 0; i < ref.length(); i++) {

            // 新的突变为 indel，加上前面一个碱基
            if((alt.charAt( i ) == '-' || ref.charAt( i ) == '-' ) && "".equals( diff )) {
                if (diffs.size() > 0) {
                    diff = ref.charAt( i - 1 ) +""+ alt.charAt( i );
                }
                continue;
            }
            if(ref.charAt( i ) != alt.charAt( i )){
                diff += alt.charAt( i );
            }else{
                if(!"".equals( diff )){
                    int diff_index = i - diff.length();
                    String subRef = ref.substring( diff_index, diff_index + diff.length() );

                    // 连续的snp分为单个
                    if(!subRef.contains( "-" ) && !diff.contains( "-" ) && diff.length() > 1){
                        for (int j = 0; j < diff.length(); j++) {
                            diffs.put( refSequence.getStart() + diff_index + j + 1 ,new String[]{subRef.charAt( j ) + "", diff.charAt( j ) + ""});
                        }
                        diff = "";
                        continue;
                    }

                    // indel或单个snp
                    if(!diff.matches( "^-+$" )) {
//                        int diff_index = i - diff.length();
//                        String subRef = ref.substring( diff_index, diff_index + diff.length() );
                        diffs.put( refSequence.getStart() + diff_index + 1, new String[]{subRef,diff.replaceFirst( DEL_TAIL, "" )} );
                    }
                    diff = "";
                }
            }
        }
        if(!"".equals( diff )){
            if(!diff.matches( "^-+$" )) {
                diff = diff.replaceFirst( "-+$","" );
                int diff_index = ref.length()-diff.length();
                String subRef = ref.substring( diff_index, diff_index + diff.length() );
                diff = diff.replaceFirst( DEL_TAIL, "" );
                diffs.put( refSequence.getStart() + diff_index + 1, new String[]{subRef, diff.replaceFirst( DEL_TAIL, "" )} );
            }
        }
        return diffs;
    }

    private void extractMHSNP(HashMap<String, List<MHInfo>> mhData) throws SetAException {
        for(String locusName: mhData.keySet() ){
            RefSequence refSequence = this.setCFa.get( locusName );
            for(MHInfo mhInfo: mhData.get( locusName )){
                HashMap<Integer, String[]> diffPosition = diff( refSequence, mhInfo );

                String result = refSequence.getSnps().stream().map( mhSnp ->
                                diffPosition.containsKey( mhSnp.getPosition() ) ? diffPosition.get( mhSnp.getPosition() )[1] : mhSnp.getRef() )
                        .collect( Collectors.joining( "-" ) );

                mhInfo.setAlleleName( result );
                mhInfo.setSnp( diffPosition );
            }
//            System.out.println(locusName +" | "+ locusInfo.getAlleleNameAsStringList().stream().distinct().collect( Collectors.joining( ", " ) ) );
        }
    }

}
