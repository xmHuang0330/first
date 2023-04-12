package fayi.config.Enum;

import lombok.AllArgsConstructor;

/*
    质控
 */
@AllArgsConstructor
public enum QC {
//    Low_Coverage("Low coverage","lc","Low coverage.  The amount of signal for a single allele failed to meet the interpretation threshold."),
    Imbalance("Imbalance","(i)","Imbalance.  The balance threshold for the alleles was exceeded.")
    ,Interpretation_threshold("Interpretation","(it)","Interpretation threshold.  A non-stutter allele is present between the analytical and interpretation thresholds.")
    ,Allele_count("Allele Count","(ac)","Allele count. More alleles than expected were detected for the locus.")
    ,Analytical_threshold("Analytical Threshold","(at)","Analytical threshold. The amount of signal for the most intense allele failed to meet the analytical threshold.")
    ,Not_detected("Not Detected","(nd)","Not detected. Signal was not detected for the locus")
//    ,User_actions("User Actions","(ua)","User actions.  A user edited or commented on the locus.")
    ,Stutter("Stutter","(s)","Stutter. The stutter threshold was exceeded.")
    ,Not_Analyzed("Not Analyzed","NA","Not analyzed.  A result is not available for the locus as it was excluded from the analysis.");
//    ,Inconclusive("Inconclusive","INC","Inconclusive.  The genotype for the locus is not reported.");

    public String Name;
    public String Indictor;
    public String Description;


}
