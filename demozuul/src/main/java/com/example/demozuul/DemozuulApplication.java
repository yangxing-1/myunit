package com.example.demozuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class DemozuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemozuulApplication.class, args);
	}

}
