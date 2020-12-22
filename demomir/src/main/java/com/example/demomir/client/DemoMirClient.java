package com.example.demomir.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "demoMirClient")
public interface DemoMirClient {
    
    @RequestMapping(value = "/demomir/getIp", method = RequestMethod.POST)
    String getIpPort();


}
