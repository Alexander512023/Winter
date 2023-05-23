package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.entity.Request;

/**
 * Stub.
 *
 * @author Alex Goryanin
 */
public class InStub implements RequestPreparator {

  @Override
  public Request<?> from(final String requestString) {
    return new Request<>(new HttpRequestStub(requestString));
  }

}
