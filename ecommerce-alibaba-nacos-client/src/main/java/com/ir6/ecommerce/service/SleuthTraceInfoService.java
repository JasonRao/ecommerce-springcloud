package com.ir6.ecommerce.service;

import brave.Tracer;
import brave.propagation.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SleuthTraceInfoService {
    @Autowired
    private Tracer tracer;

    public void logCurrentTraceInfo() {
        TraceContext traceContext = tracer.currentSpan().context();
        log.info("Sleuth trace id: [{}]", traceContext.traceId()); //十进制
        log.info("Sleuth span id: [{}]", traceContext.spanId());
    }
}
