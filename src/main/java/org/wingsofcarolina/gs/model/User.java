package org.wingsofcarolina.gs.model;


public class User {
	private String name;
	private String email;
	private String userId;
	private String teamId;
	private String access_token;
	private Boolean admin = false;
	
	public User(String name, String email, String userId, String teamId, String access_token) {
		this.name = name;
		this.email = email;
		this.userId = userId;
		this.teamId = teamId;
		this.access_token = access_token;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}
	
	public Boolean getAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}
	
	public String getUserId() {
		return userId;
	}

	public String getTeamId() {
		return teamId;
	}

	public String getAccess_token() {
		return access_token;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", email=" + email + "]";
	}
}
