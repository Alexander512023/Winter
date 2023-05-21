package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.Request;
import com.goryaninaa.winter.web.http.server.Response;
import java.util.Optional;

public interface Manager {

	Optional<Response> performScenario(final Controller controller, final Request httpRequest);
}
