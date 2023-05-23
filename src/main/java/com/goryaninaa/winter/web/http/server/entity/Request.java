package com.goryaninaa.winter.web.http.server.entity;

import com.goryaninaa.winter.web.http.server.exception.ServerException;

@SuppressWarnings("unused")
public class Request {

	private final HttpRequest httpRequest;
	private Authentication auth;

	public Request(final HttpRequest httpRequest) {
		this.httpRequest = httpRequest;
	}
	public Request(final HttpRequest httpRequest, final Authentication auth) {
		this.httpRequest = httpRequest;
		setAuth(auth);
	}

	public HttpRequest getHttpRequest() {
		return httpRequest;
	}

	public Authentication getAuth() {
		return auth;
	}

	public void setAuth(Authentication auth) {
		if (auth.isSuccessful()) {
			this.auth = auth;
		} else {
			throw new ServerException("Failed on authorization while handling request");
		}
	}
}
