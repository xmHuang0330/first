package fayi.tableObject;

import fayi.utils.StringUtils;
import lombok.NoArgsConstructor;

/*
    str8razor 的配置
 */
@NoArgsConstructor
public class StrConfig {
    public String Marker = "";
    public String Type = "";
    public String Lflank = "";
    public String Rflank = "";
    private String rc_Lflank = "";
    private String rc_Rflank = "";
    public String Motif = "";
    public Integer Period = 0;
    public Integer Offset = 0;

    public StrConfig(String marker, String lflank, String rflank, String motif, Integer period, Integer offset) {
        Marker = marker;
        Lflank = lflank;
        Rflank = rflank;
        Motif = motif;
        Period = period;
        Offset = offset;
    }

    public String getRc_Lflank() {
        if (rc_Lflank == null) {
            rc_Lflank = StringUtils.reverseComp(Lflank);
        }
        return rc_Lflank;
    }

    public String getRc_Rflank() {
        if (rc_Rflank == null) {
            rc_Rflank = StringUtils.reverseComp(Rflank);
        }
        return rc_Rflank;
    }
}