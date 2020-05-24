package com.example.configclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope//配置文件自动刷新
@RestController
public class TestController {
	@Value("${from}")
	private String from;
	@Autowired
	private Environment env;
	
	/**
	 * 通过@value获取配置属性
	 * @return
	 */
	@RequestMapping("/from")
	public String from() {
		return from;
	}
	
	/**
	 * 通过Environment对象获取配置属性
	 * @return
	 */
//	@RequestMapping("/from")
//	public String from() {
//		return env.getProperty("from", "undefined");
//	}

}
