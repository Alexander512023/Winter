package com.goryaninaa.winter.web.http.server.request.handler.security;

import com.goryaninaa.winter.web.http.server.entity.Authentication;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BasicSessionKeeper implements SessionKeeper {

	private final Map<String, Authentication> authentications = new ConcurrentHashMap<>();
	private final AtomicInteger counter = new AtomicInteger(1);

	public BasicSessionKeeper() {
	// Default constructor
	}

	@Override
	public Authentication getAuthentication(String identifier) {
		return authentications.get(identifier);
	}

	@Override
	public String addAuthentication(Authentication auth) {
		final String ident = generateIdentifier();
		authentications.put(ident, auth);
		return ident;
	}

	private String generateIdentifier() {
		return String.valueOf(counter.getAndIncrement());
	}
}
