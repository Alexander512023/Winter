package com.goryaninaa.web.http.server.request.handler;

import com.goryaninaa.web.http.server.Controller;
import com.goryaninaa.web.http.server.HttpResponseCode;
import com.goryaninaa.web.http.server.Request;
import com.goryaninaa.web.http.server.Response;
import com.goryaninaa.web.http.server.annotation.GetMapping;
import com.goryaninaa.web.http.server.annotation.RequestMapping;

/**
 * Stub.
 *
 * @author Alex Goryanin
 */
@RequestMapping("/")
public class ControllerStub implements Controller {
  private final ResponsePreparator out = new OutStub();

  @GetMapping("test")
  public Response test(final Request request) {
    return out.httpResponseFrom(HttpResponseCode.OK);
  }

}
