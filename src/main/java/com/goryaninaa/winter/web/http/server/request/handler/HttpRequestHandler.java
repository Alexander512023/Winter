package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.logger.mech.Logger;
import com.goryaninaa.winter.logger.mech.LoggingMech;
import com.goryaninaa.winter.logger.mech.StackTraceString;
import com.goryaninaa.winter.web.http.server.HttpResponseCode;
import com.goryaninaa.winter.web.http.server.Request;
import com.goryaninaa.winter.web.http.server.RequestHandler;
import com.goryaninaa.winter.web.http.server.Response;
import com.goryaninaa.winter.web.http.server.exception.ClientException;
import java.util.Optional;

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
  private final RequestPreparator input;
  private final ResponsePreparator out;
  private final Manager manager;
  private final ControllerKeeper ctrlKpr;
  private static final Logger LOG =
      LoggingMech.getLogger(HttpRequestHandler.class.getCanonicalName());

  /**
   * Constructor that receives all mandatory dependencies.
   *
   * @param input  - see {@link RequestPreparator}
   * @param out    - see {@link ResponsePreparator}
   * @param manager - see {@link Manager}
   */
  public HttpRequestHandler(final RequestPreparator input, final ResponsePreparator out,
                            final Manager manager, final ControllerKeeper ctrlKpr) {
    this.input = input;
    this.out = out;
    this.manager = manager;
    this.ctrlKpr = ctrlKpr;
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
      final Optional<Controller> controller = ctrlKpr.defineController(httpRequest);
      if (controller.isPresent()) {
        httpResponse = manager.performScenario(controller.get(), httpRequest);
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

}
