package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.entity.Request;
import java.util.Optional;

public interface Manager {

	<T> Optional<T> performStandardScenario(final Request<?> request);

	<T> Optional<T> performAuthenticationScenario(Request<?> request);
}
