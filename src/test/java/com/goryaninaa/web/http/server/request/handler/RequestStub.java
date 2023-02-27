package com.goryaninaa.web.http.server.request.handler;

import com.goryaninaa.web.http.server.Request;
import com.goryaninaa.web.http.server.annotation.HttpMethod;
import com.goryaninaa.web.http.server.exception.ServerException;
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
    return Optional.ofNullable(requestString.substring(0, length));
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
    // TODO Auto-generated method stub
    return null;
  }

}
