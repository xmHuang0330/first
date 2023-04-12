package fayi.config;

import fayi.config.Enum.Gender;
import fayi.config.paramCheck.CheckPanel;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import static fayi.config.Enum.Panel.yarn;

@ToString
@Component
public class Param {

    private static final HashMap<String, Param> panelParams = new HashMap<>();
    public HashMap<String, ArrayList<String>> locusSTR = new HashMap<>();
    public ArrayList<String> StrLocusOrder = new ArrayList<>();
    public ArrayList<String> AutoStrLocusOrder = new ArrayList<>();
    public ArrayList<String> XStrLocusOrder = new ArrayList<>();
    public ArrayList<String> BiallelicMale = new ArrayList<>();
    public ArrayList<String> BiallelicFemale = new ArrayList<>();
    public ArrayList<String> YStrLocusOrder = new ArrayList<>();
    public ArrayList<String> SnpLocusOrder = new ArrayList<>();
    public HashMap<String,ArrayList<String>> locusOrder = new HashMap<>();
    public HashMap<String,ArrayList<String>> SnpAlters = new HashMap<>();
    public float FemaleSTDStandard;
    public float MaleSTDStandard;
    public float stutterMaxProportion;
    public HashMap<Integer,String> sampleType = new HashMap<>();
    public HashMap<Integer, Gender> genderMap = new HashMap<>();
    public HashMap<Integer, String> sampleName = new HashMap<>();

    private static Param Param;
    public HashMap<String, Double> FemaleBiallelicIB = new HashMap<>();
    public HashMap<String, Double> FemaleStutterFilter = new HashMap<>();
    public HashMap<String, Double> MaleBiallelicIB = new HashMap<>();
    public HashMap<String, Double> MaleStutterFilter = new HashMap<>();
    public HashMap<String, Double> UnBiallelicIB = new HashMap<>();
    public HashMap<String, Double> UnStutterFilter = new HashMap<>();
    public HashMap<String, Double> AT = new HashMap<>();
    public HashMap<String, Double> IT = new HashMap<>();
    public ArrayList<String> BiallelicUn = new ArrayList<>();
    private final HashMap<String, double[]> IBUpperLimit = new HashMap<>();
    private final HashMap<String, double[]> IBLowerLimit = new HashMap<>();
    public HashMap<Integer, String> sampleProject = new HashMap<>();
    public HashMap<Integer, String> samplePanel = new HashMap<>();
    public HashMap<Integer, String> sampleTablet = new HashMap<>();

    public HashMap<String, Float[]> noiseLimit = new HashMap<>();
    public HashMap<Integer, String> sampleTW = new HashMap<>();

    @CheckPanel
    public static Param getPanelParams(String panel) {
        return panelParams.getOrDefault( panel, getInstance() );
    }

    public Double getSingleLimit(String locus, Double alleleName, Boolean noFilter, boolean exp) {
        if (noFilter) {
            return Config.getInstance().getNoFilterDepth()*1d;
        }
        if (! exp) {
            switch (locus) {
                case "Penta-E": {
                    if (alleleName <= 5) {
                        return 500d;
                    }
                    switch (alleleName.intValue()) {
                        case 6:
                            return 450d;
                        case 7:
                            return 400d;
                        case 8:
                            return 350d;
                        case 9:
                            return 300d;
                        case 10:
                            return 250d;
                        case 11:
                            return 200d;
                        case 12:
                            return 150d;
                    }
                    return 100d;
                }
                case "D22S1045": {
                    return 150d;
                }
                case "Penta-D": {
                    return 200d;
                }
                default: {
                    if (YStrLocusOrder.contains( locus ) && ! BiallelicMale.contains( locus )) {
                        return 30d;
                    }
                    return DefaultParam.DEFAULT_DP_LIMIT;
                }
            }
        } else {
            switch (locus) {
                case "Penta-E": {
                    return 2500d;
                }
                default: {
                    if (YStrLocusOrder.contains( locus ) && ! BiallelicMale.contains( locus )) {
                        return 30d;
                    }
                    return DefaultParam.DEFAULT_DP_LIMIT;
                }
            }
        }
    }


    private final double[] defaultUpper = new double[]{5, 10};
    private final double[] defaultLower = new double[]{0.1, 0.2};

    public double[] getIBUpperLimitByLocus(String locus, boolean exp) {
        return exp ? defaultUpper : IBUpperLimit.get( locus );
    }

    public double[] getIBLowerLimitByLocus(String locus, boolean exp) {

        return exp ? defaultLower : IBLowerLimit.get( locus );
    }

    public HashMap<String, double[]> getIBUpperLimit() {
        return IBUpperLimit;
    }

    public HashMap<String, double[]> getIBLowerLimit() {
        return IBLowerLimit;
    }

    private Param() {
        try {
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( "resource/property.properties" );
            Properties properties = new Properties();
            properties.load( resourceAsStream );
            FemaleSTDStandard = Float.parseFloat( properties.getProperty( "femaleSTD" ) );
            MaleSTDStandard = Float.parseFloat( properties.getProperty( "maleSTD" ) );
            if (null != resourceAsStream) {
                resourceAsStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Param getInstance() {
        if (null == Param) {
            Param = new Param();
        }
        return Param;
    }

    public String hashmapToString(HashMap<String, double[]> params) {
        StringBuilder out = new StringBuilder("{");
        for (String key : params.keySet()) {
            out.append(String.format("[%s:%s],\n", key, Arrays.toString(params.get(key))));
        }
        return out.toString();
    }

    public int getSingleSourceSTD() {
        if (Config.getInstance().getPanel().equals(yarn)) {
            return 28;
        } else {
            return 0;
        }
    }
}
