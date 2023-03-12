package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.Controller;
import com.goryaninaa.winter.web.http.server.HttpResponseCode;
import com.goryaninaa.winter.web.http.server.Request;
import com.goryaninaa.winter.web.http.server.Response;
import com.goryaninaa.winter.web.http.server.annotation.HttpMethod;
import com.goryaninaa.winter.web.http.server.annotation.Mapping;
import com.goryaninaa.winter.web.http.server.annotation.RequestMapping;

/**
 * Stub.
 *
 * @author Alex Goryanin
 */
@RequestMapping("/")
public class ControllerStub implements Controller {
  private final ResponsePreparator out = new OutStub();

  @Mapping(value = "test", httpMethod = HttpMethod.GET)
  public Response test(@SuppressWarnings("unused") final Request request) {
    return out.httpResponseFrom(HttpResponseCode.OK);
  }

}
