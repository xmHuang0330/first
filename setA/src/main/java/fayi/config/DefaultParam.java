package fayi.config;

import lombok.Setter;

import java.util.HashMap;

@Setter
public class DefaultParam {

    public static final HashMap<String,String> revCompGeno ;

    static {
        revCompGeno = new HashMap(){};
        revCompGeno.put("A", "T");
        revCompGeno.put("T", "A");
        revCompGeno.put("C", "G");
        revCompGeno.put("G", "C");
    }

    public static final double[] DEFAULT_SINGLE_LIMIT = {0.5,2};

    public static final double STUTTER_MAX_PROPORTION = 0.3;

    public static final Double DEFAULT_DP_LIMIT = 100d;
}
