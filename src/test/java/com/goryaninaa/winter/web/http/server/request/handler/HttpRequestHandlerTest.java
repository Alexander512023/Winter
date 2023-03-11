package com.goryaninaa.winter.web.http.server.request.handler;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpRequestHandlerTest {
  private static HttpRequestHandler requestHandler;

  @BeforeAll
  static void init() {
    requestHandler = new HttpRequestHandler(new InStub(), new OutStub(), new ParserStub());
    requestHandler.addController(new ControllerStub());
  }

  @ParameterizedTest
  @CsvSource({
          "/test, 200, handler should handle correct request",
          "/test1, 404, handler should handle incorrect request",
          "broke, 500, handler should handle broke request"
  })
  void handle(String request, int code, String message) {
    final int fact = requestHandler.handle(request).getCode().getCode();
    assertEquals(code, fact, message);
  }
}