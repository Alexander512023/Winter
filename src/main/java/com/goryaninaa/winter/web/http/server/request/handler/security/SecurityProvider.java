package com.goryaninaa.winter.web.http.server.request.handler.security;

import com.goryaninaa.winter.web.http.server.entity.Authentication;
import com.goryaninaa.winter.web.http.server.entity.Request;
import com.goryaninaa.winter.web.http.server.request.handler.Security;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

public class SecurityProvider implements Security {

	private final SessionKeeper sessionKeeper;
	private final String authCookieKey;

	public SecurityProvider(final SessionKeeper sessionKeeper, final Properties properties) {
		this.authCookieKey = properties.getProperty(
				"Winter.HttpServer.Security.Authentication.Cookie.Key");
		this.sessionKeeper = sessionKeeper;
	}

	@Override
	public Optional<Authentication> getAuthentication(Request request) {
		return request.getHttpRequest().getCookieValue(authCookieKey)
				.map(sessionKeeper::getAuthentication);
	}

	@Override
	public Map<String, String> openSession(Authentication auth) {
		return Map.of(authCookieKey, sessionKeeper.addAuthentication(auth));
	}
}
