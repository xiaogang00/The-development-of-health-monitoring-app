package com.Register;

import java.io.*;

public class User implements Serializable {
	/*
	 * 用户类，用户信息的封装 
	 * Author:樊俊彬 Time:2013-10-01
	 */
	private int id;
	private String username;
	private String password;
	private String name;
	private String sex;
	private int age;
	private String phone;

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}

	public User(String username, String password, String name, String sex, int age,
			String phone) {
		super();
		this.username = username;
		this.password = password;
		this.sex = sex;
		this.name = name;
		this.age = age;
		this.phone = phone;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password="
				+ password + ", name=" + name + ", sex=" + sex + ", age=" + age
				+ ", phone=" + phone + "]";
	}

}