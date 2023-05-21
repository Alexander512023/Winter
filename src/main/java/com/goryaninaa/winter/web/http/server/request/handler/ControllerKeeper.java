package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.Request;
import java.util.Optional;

public interface ControllerKeeper {

	/**
	 * Pass all implemented controllers trough this method.
	 */
	void addController(Controller controller);

	Optional<Controller> defineController(final Request httpRequest);
}
