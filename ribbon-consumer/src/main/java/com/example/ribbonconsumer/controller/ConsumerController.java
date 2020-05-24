package com.example.ribbonconsumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.ribbonconsumer.service.HelloService;

@RestController
public class ConsumerController {

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private HelloService helloService;
	@Autowired
	private DiscoveryClient discoveryClient;
	
	@RequestMapping(value = "/ribbon-consumer",method = RequestMethod.GET)
	public String helloConsumer()
	{
		//return restTemplate.getForEntity("http://hello-service/hello", String.class).getBody();
		return helloService.helloService();
	}
}
