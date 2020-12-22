package com.example.demomir;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
//@EnableFeignClients
//@EnableDiscoveryClient
public class DemomirApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemomirApplication.class, args);
	}

}
