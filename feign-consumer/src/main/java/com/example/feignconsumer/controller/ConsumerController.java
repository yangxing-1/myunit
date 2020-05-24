package com.example.feignconsumer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.feignconsumer.pojo.User;
import com.example.feignconsumer.service.HelloService;

@RestController
public class ConsumerController {
	@Autowired
	private HelloService helloService;
	
	@RequestMapping(value="/feign-consumer" ,method = RequestMethod.GET)
	public String helloConsumer() {
		return helloService.hello();
	}
	
	@RequestMapping(value="/feign-consumer2" ,method = RequestMethod.GET)
	public String helloConsumer2() {
		StringBuilder sb = new StringBuilder();
		sb.append(helloService.hello()).append("\n");
		sb.append(helloService.hello("DIDI")).append("\n");
		sb.append(helloService.hello("CICI",30)).append("\n");
		sb.append(helloService.hello(new User("GIGI",30))).append("\n");
		System.out.println(sb);
		return sb.toString();
	}

}
