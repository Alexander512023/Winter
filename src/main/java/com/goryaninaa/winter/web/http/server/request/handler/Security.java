package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.entity.Authentication;
import com.goryaninaa.winter.web.http.server.entity.Request;
import java.util.Map;
import java.util.Optional;

public interface Security {
	Optional<Authentication> getAuthentication(Request request);

	Map<String, String> openSession(Authentication auth);
}
