package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.HttpResponseCode;
import com.goryaninaa.winter.web.http.server.Request;
import com.goryaninaa.winter.web.http.server.Response;
import com.goryaninaa.winter.web.http.server.entity.HttpResponse;
import com.goryaninaa.winter.web.http.server.exception.ClientException;
import java.util.Optional;

public class ManagerStub implements Manager {

	@Override
	public Optional<Response> performScenario(Controller controller, Request httpRequest) {
		if (httpRequest.getMapping().equals("/test")) {
			return Optional.of(new HttpResponse(HttpResponseCode.OK));
		} else {
			throw new ClientException("mapping should be /test1 on this branch");
		}
	}
}
