package fayi.config.Enum;

import lombok.AllArgsConstructor;

/*
    样本类型
 */
@AllArgsConstructor
public enum SampleType {
    positive("positive","阳性对照"),
    negative("negative","阴性对照"),
    normal("normal","常规");
    String Type;
    String Description;
}
