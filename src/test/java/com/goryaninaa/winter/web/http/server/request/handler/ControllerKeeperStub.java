package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.Request;
import java.util.Optional;

public class ControllerKeeperStub implements ControllerKeeper {
	@Override
	public void addController(Controller controller) {

	}

	@Override
	public Optional<Controller> defineController(Request httpRequest) {
		Optional<Controller> controller = Optional.empty();
		if (httpRequest.getMapping().equals("/test")) {
			controller = Optional.of(new ControllerStub());
		}
		return controller;
	}
}
