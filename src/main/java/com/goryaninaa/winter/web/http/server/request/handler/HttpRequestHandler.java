package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.logger.mech.Logger;
import com.goryaninaa.winter.logger.mech.LoggingMech;
import com.goryaninaa.winter.logger.mech.StackTraceString;
import com.goryaninaa.winter.web.http.server.Controller;
import com.goryaninaa.winter.web.http.server.HttpResponseCode;
import com.goryaninaa.winter.web.http.server.Request;
import com.goryaninaa.winter.web.http.server.RequestHandler;
import com.goryaninaa.winter.web.http.server.Response;
import com.goryaninaa.winter.web.http.server.annotation.Mapping;
import com.goryaninaa.winter.web.http.server.annotation.RequestMapping;
import com.goryaninaa.winter.web.http.server.exception.ClientException;
import com.goryaninaa.winter.web.http.server.exception.ServerException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is implementation of HTTP request handler. Main goal of this class is to
 * get String representation of HTTP request and provide its handling via client
 * code of concrete app. It uses JAVA Reflection API and according to client
 * code works somehow similar to Spring Framework. User of this class should
 * implement a set of controllers and pass them through the constructor of this
 * class. Then according to mapping that come from HTTP request controllers will
 * be raised to handle corresponding request.
 *
 * @author Alex Goryanin
 */
public class HttpRequestHandler implements RequestHandler {
  private final Map<String, Controller> controllers = new ConcurrentHashMap<>();
  private final RequestPreparator input;
  private final ResponsePreparator out;
  private final Deserializer deserializer;
  private static final Logger LOG =
      LoggingMech.getLogger(HttpRequestHandler.class.getCanonicalName());
  private static final int SINGLE = 1;

  /**
   * Constructor that receives all mandatory dependencies.
   *
   * @param input  - see {@link RequestPreparator}
   * @param out    - see {@link ResponsePreparator}
   * @param parser - see {@link Deserializer}
   */
  public HttpRequestHandler(final RequestPreparator input, final ResponsePreparator out,
      final Deserializer parser) {
    this.input = input;
    this.out = out;
    this.deserializer = parser;
  }

  /**
   * This method receives HTTP request of String type and provide its handling.
   */
  @Override
  public Response handle(final String requestString) {
    Response result = out.httpResponseFrom(HttpResponseCode.NOTFOUND);
    Optional<Response> httpResponse = Optional.empty();
    try {
      final Request httpRequest = input.httpRequestFrom(requestString);
      final Optional<Controller> controller = defineController(httpRequest);
      if (controller.isPresent()) {
        httpResponse = manage(controller.get(), httpRequest);
      }
      if (httpResponse.isPresent()) {
        result = httpResponse.get();
      }
      return result; // NOPMD
    } catch (ClientException e) {
      if (LOG.isErrorEnabled()) {
        LOG.error(StackTraceString.get(e));
      }
      return out.httpResponseFrom(HttpResponseCode.NOTFOUND); // NOPMD
    } catch (RuntimeException e) { // NOPMD
      if (LOG.isErrorEnabled()) {
        LOG.error(StackTraceString.get(e));
      }
      return out.httpResponseFrom(HttpResponseCode.INTERNALSERVERERROR); // NOPMD
    }
  }

  /**
   * Pass all implemented controllers trough this method.
   */
  @Override
  public void addController(final Controller controller) {
    if (controller.getClass().isAnnotationPresent(RequestMapping.class)) {
      final String mapping = controller.getClass().getAnnotation(RequestMapping.class).value();
      controllers.put(mapping, controller);
    } else {
      throw new IllegalArgumentException("Controller should be annotated with the request mapping");
    }
  }

  private Optional<Controller> defineController(final Request httpRequest) {
    Optional<Controller> controller = Optional.empty();
    for (final Entry<String, Controller> controllerDefiner : controllers.entrySet()) {
      final int mappingLength = controllerDefiner.getKey().length();
      final Optional<String> requestMapping = httpRequest.getControllerMapping(mappingLength);
      if (requestMapping.isPresent() && requestMapping.get().equals(controllerDefiner.getKey())) {
        controller = Optional.ofNullable(controllerDefiner.getValue());
        break;
      }
    }
    return controller;
  }

  private Optional<Response> manage(final Controller controller, final Request httpRequest) {
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
