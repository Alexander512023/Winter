package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.annotation.HttpMethod;
import com.goryaninaa.winter.web.http.server.annotation.Mapping;
import com.goryaninaa.winter.web.http.server.annotation.RequestMapping;
import com.goryaninaa.winter.web.http.server.entity.HttpRequest;
import com.goryaninaa.winter.web.http.server.entity.HttpResponse;
import com.goryaninaa.winter.web.http.server.request.handler.manager.Controller;

/**
 * Stub.
 *
 * @author Alex Goryanin
 */

//TODO delete...
@RequestMapping("/")
public class ControllerStub implements Controller {
  private final ResponsePreparator out = new OutStub();

  @Mapping(value = "test", httpMethod = HttpMethod.GET)
  public HttpResponse test(@SuppressWarnings("unused") final HttpRequest httpRequest) {
    return out.from(HttpResponseCode.OK);
  }

}
