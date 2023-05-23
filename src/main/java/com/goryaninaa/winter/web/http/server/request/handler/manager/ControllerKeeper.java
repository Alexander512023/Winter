package com.goryaninaa.winter.web.http.server.request.handler.manager;

import com.goryaninaa.winter.web.http.server.entity.HttpRequest;
import java.util.Optional;

public interface ControllerKeeper {

	/**
	 * Pass all implemented controllers trough this method.
	 */
	void addController(Controller controller);

	Optional<Controller> defineController(final HttpRequest httpRequest);

	boolean containsAuthenticationController();

	boolean checkForAuthenticationUrl(final String url);

}
