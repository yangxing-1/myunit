package com.example.demowps;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DemowpsApplication {
    
    public static void main(String[] args) {
        //SpringApplication.run(DemowpsApplication.class, args);
        System.setProperty("sun.awt.xembedserver", "true");           //Linux下必须加这一句才能调用
        SpringApplicationBuilder builder = new SpringApplicationBuilder(DemowpsApplication.class);
        ApplicationContext applicationContext = builder.headless(false).run(args);//服务器有显示器的话传false,没有显示器传true
    }

}
