package com.goryaninaa.winter.web.http.server.request.handler.manager;

import com.goryaninaa.winter.logger.mech.Logger;
import com.goryaninaa.winter.logger.mech.LoggingMech;
import com.goryaninaa.winter.logger.mech.StackTraceString;
import com.goryaninaa.winter.web.http.server.annotation.Mapping;
import com.goryaninaa.winter.web.http.server.annotation.RequestMapping;
import com.goryaninaa.winter.web.http.server.entity.HttpRequest;
import com.goryaninaa.winter.web.http.server.entity.Request;
import com.goryaninaa.winter.web.http.server.exception.ClientException;
import com.goryaninaa.winter.web.http.server.exception.ServerException;
import com.goryaninaa.winter.web.http.server.request.handler.HttpResponseCode;
import com.goryaninaa.winter.web.http.server.request.handler.Manager;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class BasicManager implements Manager {

	private final Deserializer deserializer;
	private final ControllerKeeper controllerKeeper;
	private static final int SINGLE = 1;
	private static final Logger LOG = LoggingMech.getLogger(BasicManager.class.getCanonicalName());

	public BasicManager(final Deserializer deserializer, final ControllerKeeper controllerKeeper) {
		this.deserializer = deserializer;
		this.controllerKeeper = controllerKeeper;
	}

	@Override
	public <T> Optional<T> performStandardScenario(final Request<?> request) {
		final HttpRequest httpRequest = request.getHttpRequest();
		final Optional<Controller> controller = controllerKeeper.defineController(httpRequest);
		return perform(httpRequest, controller.orElseThrow(() -> new ClientException(
				"Unsupported URL.", HttpResponseCode.BADREQUEST)));
	}

	@Override
	public <T> Optional<T> performAuthenticationScenario(final Request<?> request) {
		if (!controllerKeeper.checkForAuthenticationUrl(request.getHttpRequest().getMapping())) {
			throw new ClientException(
					"You should pass authentication first.", HttpResponseCode.UNAUTHORIZED);
		}
		return performStandardScenario(request);
	}

	private <T> Optional<T> perform(final HttpRequest httpRequest,
										   final Controller controller) {
		final Optional<Method> handlerMethod =
				getHandlerMethod(controller, httpRequest);
		return invokeMethod(handlerMethod.orElseThrow(() -> new ClientException("Incorrect method.",
						HttpResponseCode.BADREQUEST)), controller, httpRequest);
	}

	private Optional<Method> getHandlerMethod(final Controller controller,
											  final HttpRequest httpRequest) {
		final Method[] methods = controller.getClass().getDeclaredMethods();
		Optional<Method> handlerMethod = Optional.empty();
		final int contrMppngLen = controller.getClass().getAnnotation(RequestMapping.class).value()
				.length();
		for (final Method method : methods) {
			final String methodMapping = defineMethodMappingIfHttpMethodMatch(method, httpRequest);
			final String requestMapping = httpRequest.getMapping().substring(contrMppngLen);
			if (methodMapping.equals(requestMapping)) {
				handlerMethod = Optional.of(method);
			}
		}
		return handlerMethod;
	}

	private <T> Optional<T>  invokeMethod(final Method method, final Controller controller,
												final HttpRequest httpRequest) {
		Optional<T> response;
		final Optional<String> requestBody = httpRequest.getBody();
		try {
			if (method.getParameterCount() > SINGLE && requestBody.isPresent()) {
				final Class<?> clazz = method.getParameterTypes()[1];
				final Object argument = deserializer.deserialize(clazz, requestBody.get());
				response = Optional.ofNullable((T) method.invoke(controller,
						httpRequest, argument));
			} else {
				response = Optional.ofNullable((T) method.invoke(controller,
						httpRequest));
			}
			return response;
		} catch (IllegalAccessException | InvocationTargetException e) {
			if (LOG.isErrorEnabled()) {
				LOG.error(StackTraceString.get(e));
			}
			throw new ServerException("Failed to handle request", e);
		}
	}

	private String defineMethodMappingIfHttpMethodMatch(final Method method, // NOPMD
														final HttpRequest httpRequest) {
		String methodMapping = "";
		for (final Annotation annotation : method.getAnnotations()) {
			if (annotation.annotationType().equals(Mapping.class)
					&& isHttpMethodMatch(httpRequest, annotation)) {
				methodMapping = method.getAnnotation(Mapping.class).value();
				break;
			}
		}
		return methodMapping;
	}

	private boolean isHttpMethodMatch(final HttpRequest httpRequest, final Annotation annotation) {
		return httpRequest.getMethod().equals(((Mapping) annotation).httpMethod());
	}
}
