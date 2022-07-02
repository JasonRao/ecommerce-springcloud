package com.ir6.ecommerce.controller;

import com.ir6.ecommerce.service.NacosClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/nacos-client")
public class NacosClientController {

    @Autowired
    private NacosClientService clientService;

    @GetMapping("/service-instance")
    public List<ServiceInstance> logNacosClientInfo(@RequestParam(defaultValue = "ecommerce-nacos-client") String serviceId) {
        return clientService.getNacosClientInfo(serviceId);
    }

}
