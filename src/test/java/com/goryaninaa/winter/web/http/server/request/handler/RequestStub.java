package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.Request;
import com.goryaninaa.winter.web.http.server.annotation.HttpMethod;
import com.goryaninaa.winter.web.http.server.exception.ServerException;
import java.util.Optional;

/**
 * Stub.
 *
 * @author Alex Goryanin
 */
public class RequestStub implements Request {
  private final String requestString;

  /**
   * Stub constructor.
   *
   * @param requestString - requestString
   */
  public RequestStub(final String requestString) {
    this.requestString = requestString;
    if ("broke".equals(requestString)) {
      throw new ServerException(requestString);
    }
  }

  @Override
  public Optional<String> getControllerMapping(final int length) {
    return Optional.of(requestString.substring(0, length));
  }

  @Override
  public String getMapping() {
    return requestString;
  }

  @Override
  public HttpMethod getMethod() {
    return HttpMethod.GET;
  }

  @Override
  public Optional<String> getBody() {
    return Optional.empty();
  }

}
