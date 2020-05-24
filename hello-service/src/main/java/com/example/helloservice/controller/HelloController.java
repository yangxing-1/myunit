package com.example.helloservice.controller;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.helloservice.pojo.User;
import com.netflix.discovery.DiscoveryClient;

@RestController
public class HelloController {
	private final Logger logger = Logger.getLogger(getClass());
	
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String hello() {
		return "这是一个服务";
	}
	
	@RequestMapping(value = "/hello1", method = RequestMethod.GET)
	public String hello(@RequestParam String name) {
		return "Hello "+name;
	}
	
	@RequestMapping(value = "/hello2", method = RequestMethod.GET)
	public User hello(@RequestHeader String name,@RequestHeader Integer age) {
		return new User(name,age);
	}
	
	@RequestMapping(value = "/hello3", method = RequestMethod.POST)
	public String hello(@RequestBody User user) {
		return "Hello "+user.getName()+","+user.getAge();
	}

}
