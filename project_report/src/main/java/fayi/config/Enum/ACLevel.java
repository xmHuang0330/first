package fayi.config.Enum;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public enum ACLevel {

    High("h"), Medium("m"), Low("l");

    String shortName;

    public String getShortName() {
        return this.shortName;
    }

    ACLevel(String shortName) {
        this.shortName = shortName;
    }


}
