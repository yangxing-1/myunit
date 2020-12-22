package com.example.demomir.controller;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demomir")
public class DemoController {
    
    @Value("${server.port}")
    private String port;
    
    @RequestMapping("/getIp")
    public String getIpPort() throws UnknownHostException {
        InetAddress inetAddress = Inet4Address.getLocalHost();
        String ip = inetAddress.getHostAddress();
        System.out.println(ip+":"+port);
        return ip+":"+port;
    }

}
