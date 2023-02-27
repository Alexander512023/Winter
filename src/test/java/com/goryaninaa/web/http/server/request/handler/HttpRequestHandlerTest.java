package com.goryaninaa.web.http.server.request.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class HttpRequestHandlerTest {
  private static HttpRequestHandler requestHandler;

  @BeforeAll
  static void init() {
    requestHandler = new HttpRequestHandler(new InStub(), new OutStub(), new ParserStub());
    requestHandler.addController(new ControllerStub());
  }

  @Test
  void handlerShouldHandleCorrectRequest() {
    final int fact = requestHandler.handle("/test").getCode().getCode();
    final int expected = 200;
    assertEquals(expected, fact);
  }

  @Test
  void handlerShouldHandleIncorrectRequest() {
    final int fact = requestHandler.handle("/test1").getCode().getCode();
    final int expected = 404;
    assertEquals(expected, fact);
  }

  @Test
  void handlerShouldHandleBrokeRequest() {
    final int fact = requestHandler.handle("broke").getCode().getCode();
    final int expected = 500;
    assertEquals(expected, fact);
  }
}