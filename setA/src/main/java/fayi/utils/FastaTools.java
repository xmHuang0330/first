package fayi.utils;

import fayi.tableObject.RefSequence;

import java.util.HashMap;

public class FastaTools {

    private static final String HEADER_PATTERN = "^>.*?$";
    private static final String SEQUENCE_PATTERN = "^[ATCGN]+$";



    public static HashMap<String, RefSequence> read(String fa) throws SetAException {

        Utils.checkReadFile( fa );

        FileUtils fileUtils = new FileUtils( fa );

        HashMap<String,RefSequence> result = new HashMap<>();

        String line;
        String head = "";
        StringBuilder faSequence = new StringBuilder();
        while((line = fileUtils.readLine()) != null){
            line = line.trim();
            if(line.matches( HEADER_PATTERN )) {

                String[] value = head.split( ":" );

                if(result.containsKey(  value[0] )){
                    System.err.println("已经包含了header："+head);
                }

                if (!"".equals( faSequence.toString() )){
                    result.put( value[0], new RefSequence( value[0], value[2],
                            Integer.parseInt( value[3].substring( 0, value[3].indexOf( "-" ) ) ),
                            Integer.parseInt( value[3].substring( value[3].indexOf( "-" )+1 )), faSequence.toString() ) );
                }

                head = line.substring( 1 );
                faSequence = new StringBuilder();
            } else if (line.matches( SEQUENCE_PATTERN )) {
                faSequence.append( line );
            }
        }

        String[] value = head.split( ":" );
        result.put(value[0], new RefSequence( value[0], value[2],
                Integer.parseInt( value[3].substring( 0, value[3].indexOf( "-" ) ) ),
                Integer.parseInt( value[3].substring( value[3].indexOf( "-" )+1 )), faSequence.toString() ) );

        return result;
    }


}
