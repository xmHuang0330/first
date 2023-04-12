package com.xm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@lombok.Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Data {

    private Integer idxId;
    private String ukSampleName;
    private String chip;
    private String tablet;
    private String well;
    private String mr36aLt;
    private String bglYLt;
}
