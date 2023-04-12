package fayi.config.Enum;

import lombok.Getter;


@Getter
public enum Exec {
    razor("razer.bin", "str8rzr.exe", "str8rzr_osX.bin"),
    fqAssemble("fq_assemble.bin", "", ""),
    muscle("muscle.bin", "muscle_wsl.bin", "muscle_osx.bin");


    private final String linuxName;
    private final String windowsName;
    private final String osxName;

//    private final String commandlinePattern;



    Exec(String linuxName, String windowsName, String osxName) {
        this.linuxName = linuxName;
        this.windowsName = windowsName;
        this.osxName = osxName;
    }

}
