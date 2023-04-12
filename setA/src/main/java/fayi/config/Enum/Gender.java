package fayi.config.Enum;

import lombok.AllArgsConstructor;

/*
    性别
 */
@AllArgsConstructor
public enum Gender {
    male("XY","M","男","Male"),
    female("XX","F","女","Female"),
    uncertain("UN","U","不确定","Uncertain");
    public String Name;
    public String Indictor;
    public String Description;
    public String Eng;

}
