package com.goryaninaa.web.http.server.request.handler;

import com.goryaninaa.web.http.server.HttpResponseCode;
import com.goryaninaa.web.http.server.Response;

/**
 * Stub.
 *
 * @author Alex Goryanin
 */
public class OutStub implements ResponsePreparator {

  @Override
  public Response httpResponseFrom(final HttpResponseCode httpResponseCode) {
    return new ResponseStub(httpResponseCode);
  }

  @Override
  public Response httpResponseFrom(final HttpResponseCode httpResponseCode, final String body) {
    return null;
  }

  @Override
  public <T> Response httpResponseFrom(final HttpResponseCode httpResponseCode,
      final T responseObject) {
    return null;
  }

}
