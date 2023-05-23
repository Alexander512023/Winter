package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.entity.HttpResponse;
import java.util.Map;

/**
 * Stub.
 *
 * @author Alex Goryanin
 */
public class OutStub implements ResponsePreparator {

  @Override
  public HttpResponse from(final HttpResponseCode httpResponseCode) {
    return new HttpResponseStub(httpResponseCode);
  }

  @Override
  public HttpResponse from(final HttpResponseCode httpResponseCode, final String body) {
    return null;
  }

  @Override
  public <T> HttpResponse from(final HttpResponseCode httpResponseCode,
                               final T responseObject) {
    return new HttpResponseStub(httpResponseCode);
  }

  @Override
  public HttpResponse from(Map<String, String> cookie) {
    return null;
  }

}
