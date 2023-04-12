package fayi.utils;


import fayi.config.Config;
import fayi.config.Enum.Exec;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class LocalCommand {

    private final Exec exec;
    private final String projectPath = Config.getInstance().getProjectPath() + "/resource/binary/";
    private String execFile;


    public String getExecFile() {
        return execFile;
    }

    public LocalCommand(Exec exec) {
        this.exec = exec;

        try {
            makeRunnable();
        } catch (Exception e) {
            log.warn("二进制文件设置可运行失败：" + e.getMessage());
        }
    }



    public void makeRunnable() throws SetAException {
        String file = null;
        switch (Utils.getCurrentPlatform()){
            case linux:{
                file = projectPath+exec.getLinuxName();
                break;
            }
            case mac:{
                file = projectPath+exec.getOsxName();
                break;
            }
            case windows:{
                file = projectPath+exec.getWindowsName();
                break;
            }
        }

        if(!new File(file).setExecutable(true)){
            throw new SetAException(2, "binary files can't set executable(x)");
        }

        this.execFile = file;
    }




}
