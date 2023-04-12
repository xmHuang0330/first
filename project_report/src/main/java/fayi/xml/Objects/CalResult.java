package fayi.xml.Objects;

import jakarta.xml.bind.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
    统计信息
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor
public class CalResult {

    @XmlElement
    public int availableDepth;

    @XmlElement(name = "totalDepth")
    public double fqReads;

    @XmlElement
    public String interlocusBalance;

    @XmlElement
    public String singleSource;

    @XmlElement
    public String iSNP_Loci_Typed;

    @XmlElement
    public String auto_Loci_Typed;

    @XmlElement
    public String x_Loci_Typed;

    @XmlElement
    public String y_Loci_Typed;

    @XmlElement
    public float strAvg;

    @XmlElement
    public float snpAvg;

    @XmlElement
    public double strSTD;

    @XmlElement
    public double snpSTD;

    @XmlElement
    public int strDepthBelow30 = 0;

    @XmlElement
    public int strDepthBelow100 = 0;

    @XmlTransient
    public int strSumDepth = 0;

    @XmlTransient
    public int snpSumDepth = 0;

    @XmlElement
    private boolean qualityPass = false;

    @XmlElement(name = "CEYTyped")
    private int y41Count = 0;

    @XmlElement(name = "Y41SUPTyped")
    private int y41SupCount = 0;

    @XmlElement(name = "CEAutoTyped")
    private int PP21Count = 0;

    @XmlElement
    private float mixLevel;

    @XmlElement
    private int highStutter;

    @XmlElement
    private float yProportion;

    public Integer getATyped() {
        if(auto_Loci_Typed == null || "".equals(auto_Loci_Typed)){
            return 0;
        }
        return Integer.parseInt(auto_Loci_Typed.split("/")[0]);
    }
    public Integer getXTyped() {
        if(x_Loci_Typed == null || "".equals(x_Loci_Typed)){
            return 0;
        }
        return Integer.parseInt(x_Loci_Typed.split("/")[0]);
    }
    public Integer getYTyped() {
        if(y_Loci_Typed == null || "".equals(y_Loci_Typed)){
            return 0;
        }
        return Integer.parseInt(y_Loci_Typed.split("/")[0]);
    }
}
