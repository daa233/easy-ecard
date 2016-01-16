package com.duang.easyecard.Models;

public class LostInfo {
	
	private int id;
	private String name;
	private String stuId;
	private String account;
	private String contact;
	private String publishTime;
	private String state;
	private String foundTime;
	
	public LostInfo() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStuId() {
		return stuId;
	}

	public void setStuId(String stuId) {
		this.stuId = stuId;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(String publishTime) {
		this.publishTime = publishTime;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getFoundTime() {
		return foundTime;
	}

	public void setFoundTime(String foundTime) {
		this.foundTime = foundTime;
	}

}
