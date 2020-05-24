package com.example.ribbonconsumer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@Service
public class HelloService {
	@Autowired
	private RestTemplate restTemplate;
	
	@HystrixCommand(fallbackMethod = "helloFallBack")
	public String helloService() {
		return restTemplate.getForEntity("http://hello-service/hello", String.class).getBody();
	}

	public String helloFallBack() {
		return "get from hello-service error";
	}
}
