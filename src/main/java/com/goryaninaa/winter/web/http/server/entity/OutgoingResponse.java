package com.goryaninaa.winter.web.http.server.entity;

import com.goryaninaa.winter.web.http.server.HttpResponseCode;
import com.goryaninaa.winter.web.http.server.Response;
import com.goryaninaa.winter.web.http.server.request.handler.ResponsePreparator;

/**
 * Implementation of HTTP response wrapper interface.
 *
 * @author Alex Goryanin
 */
public class OutgoingResponse implements ResponsePreparator {

  @Override
  public HttpResponse httpResponseFrom(final HttpResponseCode httpResponseCode) {
    return new HttpResponse(httpResponseCode);
  }

  @Override
  public HttpResponse httpResponseFrom(final HttpResponseCode httpResponseCode, final String body) {
    return new HttpResponse(httpResponseCode, body);
  }

  @Override
  public <T> Response httpResponseFrom(final HttpResponseCode httpResponseCode,
      final T responseObject) {
    return new <T>HttpResponse(httpResponseCode, responseObject);
  }

}
