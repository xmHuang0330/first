package fayi.config.Enum;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Panel {

    yarn("yarn", true, false, true),
    setB("setB", false, true, true),
    setA("setA", false, false, false),
    setC("setC", false, false, true),
    test("test", false, false, true);

    public String name;
    public boolean canPredictHg;
    public boolean needsFlanking;

    public boolean canTrimFlanking;

}
