package com.ir6.ecommerce.controller;

import com.ir6.ecommerce.service.SleuthTraceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sleuth")
public class SleuthTraceInfoController {
    @Autowired
    private SleuthTraceInfoService traceInfoService;

    @GetMapping("/trace-info")
    public void logCurrentTraceInfo() {
        traceInfoService.logCurrentTraceInfo();
    }
}
