package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.entity.HttpResponse;
import java.util.Map;

/**
 * This interface is used to inject dependency on class that will prepare
 * {@link HttpResponse}.
 *
 * @author Alex Goryanin
 */
public interface ResponsePreparator {

  HttpResponse from(HttpResponseCode httpResponseCode);

  HttpResponse from(HttpResponseCode httpResponseCode, String body);

  <T> HttpResponse from(HttpResponseCode httpResponseCode, T responseObject);

  HttpResponse from(Map<String, String> cookie);

}
