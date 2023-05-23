package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.entity.Authentication;
import com.goryaninaa.winter.web.http.server.entity.Request;
import java.util.Map;
import java.util.Optional;

public class SecurityStub implements Security{
	@Override
	public Optional<Authentication> getAuthentication(Request request) {
		return Optional.empty();
	}

	@Override
	public Map<String, String> openSession(Authentication auth) {
		return null;
	}
}
