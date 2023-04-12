package fayi.tableObject;

import lombok.AllArgsConstructor;
/*
    excel 公式类
 */
@AllArgsConstructor
public class FormulaString {
    public String Name;
    public String Ref;

    @Override
    public String toString() {
        return Ref;
    }
}
