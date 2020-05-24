package com.example.feignconsumer.pojo;

public class User {
	private String name;
	private Integer age;

	//默认构造函数是必须的，否则spring cloud feign根据json字符串转化User对象时会报错
	public User() {

	}

	public User(String name, Integer age) {
		this.name = name;
		this.age = age;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name 要设置的 name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return age
	 */
	public Integer getAge() {
		return age;
	}

	/**
	 * @param age 要设置的 age
	 */
	public void setAge(Integer age) {
		this.age = age;
	}

	@Override
	public String toString() {
		return "name = "+name+",age = "+age;
	}
}
