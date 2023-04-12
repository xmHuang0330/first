package fayi.config.Enum;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum EmptyReason {

    IB( "双等位IB阈值"),
    SINGLE_DP( "双等位总深度或单峰深度不够"),
    SINGLE_IB( "单等位次峰过高"),
    NOISE_ALLELE( "噪音allele过多，未达有效总深度"),
    NOISE_PROP( "信噪比过低"),
    THIRD_ALLELE("第三峰深度过高");

    String reason;
}
