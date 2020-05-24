package com.example.feignconsumer.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.feignconsumer.pojo.User;


@FeignClient("hello-service")//Feign声明的方式用接口去调用hello-service服务（不区分服务名的大小写）
public interface HelloService {
	
	@RequestMapping("/hello")//执行hello-service服务中/hello接口方法
	String hello();
	
	@RequestMapping(value = "/hello1", method = RequestMethod.GET)
	String hello(@RequestParam("name") String name);
	
	@RequestMapping(value = "/hello2", method = RequestMethod.GET)
	User hello(@RequestHeader("name") String name, @RequestHeader("age") Integer age) ;
	
	@RequestMapping(value = "/hello3", method = RequestMethod.POST)
	String hello(@RequestBody User user) ;
}
