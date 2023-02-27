package com.goryaninaa.web.http.server.request.handler;

import com.goryaninaa.web.http.server.HttpResponseCode;
import com.goryaninaa.web.http.server.Response;

/**
 * This interface is used to inject dependency on class that will prepare
 * {@link Response}.
 *
 * @author Alex Goryanin
 */
public interface ResponsePreparator {

  Response httpResponseFrom(HttpResponseCode httpResponseCode);

  Response httpResponseFrom(HttpResponseCode httpResponseCode, String body);

  <T> Response httpResponseFrom(HttpResponseCode httpResponseCode, T responseObject);

}
