package com.xm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DemoTh {
    @RequestMapping("/")
    public String getDemo() {
        return "index";
    }


}
