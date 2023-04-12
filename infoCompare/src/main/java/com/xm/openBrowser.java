package com.xm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class openBrowser implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        log.info("开始自动加载指定的页面");
        try {
            Runtime.getRuntime().exec("cmd    /c    start   http://localhost:8080");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
