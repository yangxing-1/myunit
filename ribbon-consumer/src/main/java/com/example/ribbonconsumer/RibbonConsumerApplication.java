package com.example.ribbonconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

//@EnableCircuitBreaker //开启断路器功能
//@EnableDiscoveryClient //服务发现
//@SpringBootApplication
@SpringCloudApplication //springcloud标准应用，包含上述springbootApplication、服务发现和开启断路器三个注解
public class RibbonConsumerApplication {

	@Bean
	@LoadBalanced//开启客户端负载均衡
	RestTemplate rstTemplate()
	{
		return new RestTemplate();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(RibbonConsumerApplication.class, args);
	}

}
