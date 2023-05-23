package com.goryaninaa.winter.web.http.server.entity;

import com.goryaninaa.winter.web.http.server.request.handler.RequestPreparator;

/**
 * Implementation of HTTP request wrapper interface.
 *
 * @author Alex Goryanin
 */
public class IncomingRequest implements RequestPreparator {

  @Override
  public Request<?> from(final String requestString) {
    return new Request<>(new HttpRequest(requestString));
  }

}
