package com.goryaninaa.winter.web.http.server.request.handler.manager;

import com.goryaninaa.winter.logger.mech.Logger;
import com.goryaninaa.winter.logger.mech.LoggingMech;
import com.goryaninaa.winter.logger.mech.StackTraceString;
import com.goryaninaa.winter.web.http.server.Request;
import com.goryaninaa.winter.web.http.server.Response;
import com.goryaninaa.winter.web.http.server.annotation.Mapping;
import com.goryaninaa.winter.web.http.server.annotation.RequestMapping;
import com.goryaninaa.winter.web.http.server.exception.ServerException;
import com.goryaninaa.winter.web.http.server.request.handler.Controller;
import com.goryaninaa.winter.web.http.server.request.handler.Deserializer;
import com.goryaninaa.winter.web.http.server.request.handler.Manager;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class BasicManager implements Manager {

	private final Deserializer deserializer;
	private static final int SINGLE = 1;
	private static final Logger LOG = LoggingMech.getLogger(BasicManager.class.getCanonicalName());

	public BasicManager(Deserializer deserializer) {
		this.deserializer = deserializer;
	}

	@Override
	public Optional<Response> performScenario(final Controller controller,
											final Request httpRequest) {
		final Optional<Method> handlerMethod = getHandlerMethod(controller, httpRequest);
		Optional<Response> httpResponse = Optional.empty();
		if (handlerMethod.isPresent()) {
			httpResponse = invokeMethod(handlerMethod.get(), controller, httpRequest);
		}
		return httpResponse;
	}

	private Optional<Method> getHandlerMethod(final Controller controller,
											  final Request httpRequest) {
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

	private Optional<Response> invokeMethod(final Method method, final Controller controller,
											final Request httpRequest) {
		Optional<Response> response;
		final Optional<String> requestBody = httpRequest.getBody();
		try {
			if (method.getParameterCount() > SINGLE && requestBody.isPresent()) {
				final Class<?> clazz = method.getParameterTypes()[1];
				final Object argument = deserializer.deserialize(clazz, requestBody.get());
				response = Optional.ofNullable((Response) method.invoke(controller, httpRequest, argument));
			} else {
				response = Optional.ofNullable((Response) method.invoke(controller, httpRequest));
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
														final Request httpRequest) {
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

	private boolean isHttpMethodMatch(final Request httpRequest, final Annotation annotation) {
		return httpRequest.getMethod().equals(((Mapping) annotation).httpMethod());
	}
}
