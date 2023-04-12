package com.xm.controller;


import com.xm.service.ExcelBCResultCompareService;
import com.xm.service.ExcelSetBTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/excel")
public class ExcelController {

    @Autowired
    ExcelSetBTagService excelSetBTagService;

    @Autowired
    ExcelBCResultCompareService excelBCResultCompareService;

    @ResponseBody
    @RequestMapping("/tagColor")
    public Map<String,Object> uploadExcel(MultipartFile file) throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        excelSetBTagService.ExcelData(file);
        map.put("msg", "上传成功");
        map.put("code", 0);
        log.info("完成============");
        return map;
    }

    @ResponseBody
    @RequestMapping("/BCResultCompare")
    public Map<String, Object> BCResultCompare(MultipartFile file) throws IOException {
        HashMap<String, Object> map = new HashMap<>();
        excelBCResultCompareService.setBData(file);
        map.put("msg", "上传成功");
        map.put("code", 0);
        return map;
    }
}
