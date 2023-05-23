package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.entity.HttpResponse;
import com.goryaninaa.winter.web.http.server.entity.HttpRequest;
import com.goryaninaa.winter.web.http.server.entity.Request;
import com.goryaninaa.winter.web.http.server.exception.ClientException;
import java.util.Optional;

public class ManagerStub implements Manager {

	@Override
	public <T> Optional<T> performStandardScenario(Request<?> request) {
		final HttpRequest httpRequest = request.getHttpRequest();
		if (httpRequest.getMapping().equals("/test1")) {
			throw new ClientException("mapping should be /test1 on this branch",
					HttpResponseCode.BADREQUEST);
		} else if (httpRequest.getMapping().equals("/test")) {
			return Optional.of((T) new HttpResponse(HttpResponseCode.OK));
		} else {
			throw new RuntimeException();
		}
	}

	@Override
	public <T> Optional<T> performAuthenticationScenario(Request<?> request) {
		return Optional.empty();
	}
}
