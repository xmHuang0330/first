package fayi.config.Enum;

public enum SnpPosition {

    left("-", "l"), right("+", "r"), N("N", "n");

    public String position;
    public String shortName;

    public static SnpPosition getByPosition(String position) {
        for (SnpPosition snpPosition : SnpPosition.values()) {
            if (position.equals(snpPosition.position) || position.equals(snpPosition.shortName)) {
                return snpPosition;
            }
        }
        return null;
    }

    SnpPosition(String position, String shortName) {
        this.position = position;
        this.shortName = shortName;
    }
}
