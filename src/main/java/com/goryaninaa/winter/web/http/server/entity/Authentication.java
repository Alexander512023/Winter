package com.goryaninaa.winter.web.http.server.entity;


@SuppressWarnings("unused")
public class Authentication {

	private String login;
	private String password;
	private boolean successful;

	public Authentication(String login) {
		this.login = login;
	}

	public Authentication(String login, String password) {
		this.login = login;
		this.password = password;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public void setSuccessful(boolean successful) {
		this.successful = successful;
	}
}
