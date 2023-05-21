package com.goryaninaa.winter.web.http.server.request.handler.controller;

import com.goryaninaa.winter.web.http.server.request.handler.Controller;
import com.goryaninaa.winter.web.http.server.Request;
import com.goryaninaa.winter.web.http.server.annotation.RequestMapping;
import com.goryaninaa.winter.web.http.server.request.handler.ControllerKeeper;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BasicControllerKeeper implements ControllerKeeper {

	private final Map<String, Controller> controllers = new ConcurrentHashMap<>();

	public BasicControllerKeeper() {
		// Default empty constructor of these class
	}

	/**
	 * Pass all implemented controllers trough this method.
	 */
	public void addController(final Controller controller) {
		if (controller.getClass().isAnnotationPresent(RequestMapping.class)) {
			final String mapping = controller.getClass().getAnnotation(RequestMapping.class).value();
			controllers.put(mapping, controller);
		} else {
			throw new IllegalArgumentException("Controller should be annotated with the request mapping");
		}
	}

	@Override
	public Optional<Controller> defineController(final Request httpRequest) {
		Optional<Controller> controller = Optional.empty();
		for (final Map.Entry<String, Controller> controllerDefiner : controllers.entrySet()) {
			final int mappingLength = controllerDefiner.getKey().length();
			final Optional<String> requestMapping = httpRequest.getControllerMapping(mappingLength);
			if (requestMapping.isPresent() && requestMapping.get().equals(controllerDefiner.getKey())) {
				controller = Optional.ofNullable(controllerDefiner.getValue());
				break;
			}
		}
		return controller;
	}
}
