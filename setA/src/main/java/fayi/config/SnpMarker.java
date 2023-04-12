package fayi.config;

import fayi.config.Enum.SnpPosition;
import fayi.config.Enum.SnpType;
import fayi.utils.SetAException;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Getter
public class SnpMarker implements Serializable {

    private final SnpPosition position;
    private final int index;
    private String ref = "";
    private final String alt;
    private final int start;
    private final int end;
    private final SnpType type;
    private String marker;

    public SnpMarker(SnpPosition position, Integer index, String ref, String alt, String oRef) throws SetAException {
        //比对返回的是
        index = index - 1;
        this.position = position;
        this.index = index;
        this.alt = alt;
        if (null != ref) {
            this.ref = ref;
            this.start = index;
            this.end = ref.equals("-") ? start : start + alt.length();
            type = ref.equals("-") ? SnpType.insert : alt.equals("-") ? SnpType.delete : SnpType.snp;
            return;
        }
        if (this.position.equals(SnpPosition.left)) {
            if (oRef.contains("-")) {
                if (index > oRef.lastIndexOf("-")) {
                    char[] chars = oRef.toCharArray();
                    int count = 0;
                    for (int i = 0; i <= index; i++) {
                        if (chars[i] == '-') {
                            count++;
                        }
                    }
                    index = index - count + 1;
                    String clean = oRef.replaceAll("-", "");
                    this.ref = clean.substring(index - alt.length(), index);
                    end = clean.length() - index + alt.length();
                    start = end - alt.length() + 1;
                } else if (oRef.charAt(index) == '-') {
                    this.ref = oRef.substring(index - alt.length() + 1, index + 1);
                    start = oRef.replaceAll("-", "").length() - index + alt.length();
                    end = start + alt.length() - 1;
                } else {
                    this.ref = oRef.substring(index - alt.length() + 1, index + 1);
                    start = oRef.replaceAll("-", "").length() - index;
                    end = start + alt.length() - 1;
                }
            } else {
                this.ref = oRef.substring(index - alt.length() + 1, index + 1);
                start = oRef.replaceAll("-", "").length() - index;
                end = start + alt.length() - 1;
            }
        } else {
            if (oRef.contains("-")) {
                if (index < oRef.lastIndexOf("-")) {
                    this.ref = oRef.substring(index - (alt.length() - 1), index + 1);
                    start = index - (alt.length() - 1) + 1;
                    end = start + alt.length() - 1;
                } else if (index == oRef.lastIndexOf("-")) {
                    this.ref = "";
                    this.start = index + 1;
                    this.end = this.start;
                } else {
                    char[] chars = oRef.toCharArray();
                    int count = 0;
                    for (int i = 0; i <= index; i++) {
                        if (chars[i] == '-') {
                            count++;
                        }
                    }
                    index = index - count;
                    try {
                        this.ref = oRef.replaceAll( "-", "" ).substring( index - alt.length() + 1, index + 1 );
                    }catch (StringIndexOutOfBoundsException e){
                        throw new SetAException( 1, String.format( "snp生成错误，参考序列 %s， index：%s，alter：%s", oRef,index,alt));
                    }
                    start = index - (alt.length() - 1) + 1;
                    end = start + alt.length() - 1;
                }
            } else {
                this.ref = oRef.substring(index - (alt.length() - 1), index + 1);
                start = index - (alt.length() - 1) + 1;
                end = start + alt.length() - 1;
            }
        }
        if (this.ref.matches("^[-]+$") || "".equals(this.ref)) this.type = SnpType.insert;
        else if (this.alt.matches("^[-]+$")) this.type = SnpType.delete;
        else this.type = SnpType.snp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SnpMarker snpMarker = (SnpMarker) o;
        return index == snpMarker.index && position == snpMarker.position && ref.equals(snpMarker.ref) && alt.equals(snpMarker.alt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, index, ref, alt);
    }

    @Override
    public String toString() {
        return position.position + start + (type.equals(SnpType.insert) ? "-" : ref) + '/' + (type.equals(SnpType.delete) ? "-" : alt);
    }

    public String toMarker() {
        return position.position + (type.equals(SnpType.insert) ? start : (start != end ? start + "-" + end : start)) + (type.equals(SnpType.insert) ? "i" + alt : type.equals(SnpType.delete) ? "d" : "s" + alt);
    }

}
