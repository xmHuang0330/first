package fayi.config.Enum;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public
enum SnpType {
    insert("i"),
    delete("d"),
    snp("s");

    public String symbol;

}
