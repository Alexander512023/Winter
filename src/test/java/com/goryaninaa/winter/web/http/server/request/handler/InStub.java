package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.Request;

/**
 * Stub.
 *
 * @author Alex Goryanin
 */
public class InStub implements RequestPreparator {

  @Override
  public Request httpRequestFrom(final String requestString) {
    return new RequestStub(requestString);
  }

}
