package org.wingsofcarolina.gs.model;

import org.wingsofcarolina.gs.domain.Person;

public class User {
	private String name;
	private String email;
	private String uuid;
	private Boolean admin = false;
	private Boolean student = false;
	
	public User(Person person) {
		this.name = person.getName();
		this.email = person.getEmail();
		this.uuid = person.getUUID();
		this.admin = person.isAdmin();
		this.student = person.isStudent();
	}
	
	public User(String name, String email, String uuid) {
		this.name = name;
		this.email = email;
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}
	
	public Boolean isAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}
	
	public Boolean isStudent() {
		return student;
	}

	public void setStudent(Boolean student) {
		this.student = student;
	}

	public String getUUID() {
		return uuid;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", email=" + email + "]";
	}
}
