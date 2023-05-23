package com.goryaninaa.winter.web.http.server.request.handler.security;

import com.goryaninaa.winter.web.http.server.entity.Authentication;

public interface SessionKeeper {

	Authentication getAuthentication(String identifier);

	String addAuthentication(Authentication auth);

}
