package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.entity.HttpRequest;
import com.goryaninaa.winter.web.http.server.request.handler.manager.Controller;
import com.goryaninaa.winter.web.http.server.request.handler.manager.ControllerKeeper;
import java.util.Optional;

//TODO delete...
public class ControllerKeeperStub implements ControllerKeeper {
	@Override
	public void addController(Controller controller) {

	}

	@Override
	public Optional<Controller> defineController(HttpRequest httpRequest) {
		Optional<Controller> controller = Optional.empty();
		if (httpRequest.getMapping().equals("/test")) {
			controller = Optional.of(new ControllerStub());
		}
		return controller;
	}

	@Override
	public boolean containsAuthenticationController() {
		return false;
	}

	@Override
	public boolean checkForAuthenticationUrl(String url) {
		return false;
	}
}
