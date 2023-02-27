package com.goryaninaa.web.http.server.request.handler;

import com.goryaninaa.web.http.server.HttpResponseCode;
import com.goryaninaa.web.http.server.Response;

/**
 * Stub.
 *
 * @author Alex Goryanin
 */
public class ResponseStub implements Response {

  private final HttpResponseCode httpResponseCode;

  public ResponseStub(final HttpResponseCode httpResponseCode) {
    this.httpResponseCode = httpResponseCode;
  }

  @Override
  public String getResponseString() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public HttpResponseCode getCode() {
    return httpResponseCode;
  }

}
