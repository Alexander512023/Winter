package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.entity.HttpResponse;

/**
 * Stub.
 *
 * @author Alex Goryanin
 */
public class HttpResponseStub extends HttpResponse {

  private final HttpResponseCode httpResponseCode;

  public HttpResponseStub(final HttpResponseCode httpResponseCode) {
    super(httpResponseCode);
    this.httpResponseCode = httpResponseCode;
  }

  @Override
  public String getResponseString() {
    return null;
  }

  @Override
  public HttpResponseCode getCode() {
    return httpResponseCode;
  }

}
