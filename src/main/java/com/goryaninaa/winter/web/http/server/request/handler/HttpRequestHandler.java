package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.logger.mech.Logger;
import com.goryaninaa.winter.logger.mech.LoggingMech;
import com.goryaninaa.winter.logger.mech.StackTraceString;
import com.goryaninaa.winter.web.http.server.RequestHandler;
import com.goryaninaa.winter.web.http.server.dto.ErrorDto;
import com.goryaninaa.winter.web.http.server.entity.Authentication;
import com.goryaninaa.winter.web.http.server.entity.HttpResponse;
import com.goryaninaa.winter.web.http.server.entity.Request;
import com.goryaninaa.winter.web.http.server.exception.ClientException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

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
  private final RequestPreparator complexRequest;
  private final ResponsePreparator response;
  private final Manager manager;
  private final AtomicBoolean securityEnabled;
  private Security security;
  private static final Logger LOG =
      LoggingMech.getLogger(HttpRequestHandler.class.getCanonicalName());

  /**
   * Constructor that receives all mandatory dependencies.
   *
   * @param complexRequest  - see {@link RequestPreparator}
   * @param response    - see {@link ResponsePreparator}
   * @param configurator - see {@link HandlerCofigurator}
   */
  public HttpRequestHandler(final RequestPreparator complexRequest,
                            final ResponsePreparator response,
                            final HandlerCofigurator configurator) {
    this.complexRequest = complexRequest;
    this.response = response;
    this.manager = configurator.getManager();
    this.securityEnabled = configurator.isSecurityEnabled();
    if (securityEnabled.get()) {
      this.security = configurator.getSecurity();
    }
  }

  /**
   * This method receives HTTP request of String type and provide its handling.
   */
  @Override
  public HttpResponse handle(final String requestString) {
    try {
      final Request request = complexRequest.from(requestString);
      return securityEnabled.get() ? secureHandle(request) : handle(request);
    } catch (ClientException e) {
      if (LOG.isErrorEnabled()) {
        LOG.error(StackTraceString.get(e));
      }
      return response.from(e.getResponseCode(), // NOPMD
              new ErrorDto(e.getResponseCode().getCode(), e.getMessage()));
    } catch (RuntimeException e) { // NOPMD
      if (LOG.isErrorEnabled()) {
        LOG.error(StackTraceString.get(e));
      }
      return response.from(HttpResponseCode.INTERNALSERVERERROR); // NOPMD
    }
  }

  private HttpResponse handle(final Request request) {
    return manager.<HttpResponse>performStandardScenario(request)
            .orElseGet(() -> response.from(HttpResponseCode.NOTFOUND));
  }

  private HttpResponse secureHandle(final Request request) {
    final Optional<Authentication> auth = security.getAuthentication(request);
    return auth.isEmpty()
            ? handleAuthentication(request)
            : handle(new Request(request.getHttpRequest(), auth.get()));
  }

  private HttpResponse handleAuthentication(final Request request) {
    Optional<Authentication> auth =
            manager.performAuthenticationScenario(request);
    final Map<String, String> cookie = security.openSession(auth.orElseThrow(() ->
            new ClientException("Authentication failed, try again.", HttpResponseCode.BADREQUEST)));
    return response.from(cookie);
  }
}
