package fayi.config.Enum;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Panel {

    yarn("yarn", true, false),
    setB("setB", false, true),
    setA("setA", false, false),
    setC("setC", true, false),
    test("test", false, false);

    String name;
    public boolean canPredictHg;
    public boolean needsFlanking;


}
