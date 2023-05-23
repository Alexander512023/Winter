package com.goryaninaa.winter.web.http.server.request.handler.manager;

import com.goryaninaa.winter.web.http.server.annotation.RequestMapping;
import com.goryaninaa.winter.web.http.server.entity.HttpRequest;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class BasicControllerKeeper implements ControllerKeeper {

	private final Map<String, Controller> controllers = new ConcurrentHashMap<>();
	private final AtomicReference<String> authUrl = new AtomicReference<>();

	public BasicControllerKeeper(final Properties properties) {
		if (properties.getProperty("Winter.HttpServer.Security.Enabled").equals("true")) {
			authUrl.set(properties.getProperty("Winter.HttpServer.Security.Authentication.URL"));
		}
	}

	/**
	 * Pass all implemented controllers trough this method.
	 */
	public void addController(final Controller controller) {
		if (controller.getClass().isAnnotationPresent(RequestMapping.class)) {
			final String mapping = controller.getClass().getAnnotation(RequestMapping.class).value();
			controllers.put(mapping, controller);
		} else {
			throw new IllegalArgumentException("Controller should be annotated");
		}
	}

	@Override
	public Optional<Controller> defineController(final HttpRequest httpRequest) {
		Optional<Controller> controller = Optional.empty();
		for (final Map.Entry<String, Controller> controllerDefiner : controllers.entrySet()) {
			final int mappingLength = controllerDefiner.getKey().length();
			final Optional<String> requestMapping = httpRequest.getControllerMapping(mappingLength);
			if (requestMapping.isPresent()
					&& requestMapping.get().equals(controllerDefiner.getKey())) {
				controller = Optional.ofNullable(controllerDefiner.getValue());
				break;
			}
		}
		return controller;
	}

	@Override
	public boolean containsAuthenticationController() {
		return controllers.containsKey(authUrl.get());
	}

	@Override
	public boolean checkForAuthenticationUrl(final String url) {
		return url.equals("/" + authUrl.get());
	}
}
