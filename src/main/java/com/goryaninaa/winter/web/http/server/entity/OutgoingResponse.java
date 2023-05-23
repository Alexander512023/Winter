package com.goryaninaa.winter.web.http.server.entity;

import com.goryaninaa.winter.web.http.server.request.handler.HttpResponseCode;
import com.goryaninaa.winter.web.http.server.request.handler.ResponsePreparator;
import java.util.Map;

/**
 * Implementation of HTTP response wrapper interface.
 *
 * @author Alex Goryanin
 */
public class OutgoingResponse implements ResponsePreparator {

  @Override
  public HttpResponse from(final HttpResponseCode httpResponseCode) {
    return new HttpResponse(httpResponseCode);
  }

  @Override
  public HttpResponse from(final HttpResponseCode httpResponseCode, final String body) {
    return new HttpResponse(httpResponseCode, body);
  }

  @Override
  public <T> HttpResponse from(final HttpResponseCode httpResponseCode,
                               final T responseObject) {
    return new <T>HttpResponse(httpResponseCode, responseObject);
  }

  @Override
  public HttpResponse from(Map<String, String> cookie) {
    return new HttpResponse(cookie);
  }

}
