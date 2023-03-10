package com.goryaninaa.winter.web.http.server.entity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.goryaninaa.winter.web.http.server.annotation.HttpMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpRequestTest {
  private String request;

  @BeforeEach
  void init() {
    this.request = "POST /cgi-bin/process.cgi?1+1=2&2+3=5 HTTP/1.1\n"
        + "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\n"
        + "Host: www.tutorialspoint.com\n" + "Content-Type: application/x-www-form-urlencoded\n"
        + "Content-Length: length\n" + "Accept-Language: en-us\n"
        + "Accept-Encoding: gzip, deflate\n" + "Connection: Keep-Alive\n" + "\n"
        + "licenseID=string&content=string&/paramsXML=string";
  }

  @Test
  void httpRequestShouldDefineMethodOnCreation() {
    final HttpRequest httpRequest = new HttpRequest(request);
    final HttpMethod expected = HttpMethod.POST;
    final HttpMethod fact = httpRequest.getMethod();
    assertEquals(expected, fact, "Method is not defined correctly");
  }

  @Test
  void httpRequestShouldDefineMappingOnCreation() {
    final HttpRequest httpRequest = new HttpRequest(request);
    final String expected = "/cgi-bin/process.cgi";
    final String fact = httpRequest.getMapping();
    assertEquals(expected, fact, "Mapping is not defined correctly");
  }

  @Test
  void httpRequestShouldDefineBodyOnCreation() {
    final HttpRequest httpRequest = new HttpRequest(request);
    final String expected = "licenseID=string&content=string&/paramsXML=string";
    final String fact = httpRequest.getBody().get();
    assertEquals(expected, fact, "Body is not defined correctly");
  }

  @Test
  void httpRequestShouldDefineHeadersOnCreation() { // NOPMD
    final HttpRequest httpRequest = new HttpRequest(request);
    assertAll(
        () -> assertEquals("Mozilla/4.0 (compatible; MSIE5.01; Windows NT)",
            httpRequest.getHeaderByName("User-Agent").get()),
        () -> assertEquals("www.tutorialspoint.com", httpRequest.getHeaderByName("Host").get()),
        () -> assertEquals("application/x-www-form-urlencoded",
            httpRequest.getHeaderByName("Content-Type").get()),
        () -> assertEquals("length", httpRequest.getHeaderByName("Content-Length").get()),
        () -> assertEquals("en-us", httpRequest.getHeaderByName("Accept-Language").get()),
        () -> assertEquals("gzip, deflate", httpRequest.getHeaderByName("Accept-Encoding").get()),
        () -> assertEquals("Keep-Alive", httpRequest.getHeaderByName("Connection").get()));
  }

  @Test
  void httpRequestShouldDefineParametersOnCreation() {
    final HttpRequest httpRequest = new HttpRequest(request);
    assertAll(
            () -> assertEquals("2", httpRequest.getParameterByName("1+1").get()),
            () -> assertEquals("5", httpRequest.getParameterByName("2+3").get())
    );
  }

  @Test
  void httpRequestShouldProvideControllerMapping() {
    final HttpRequest httpRequest = new HttpRequest(request);
    assertEquals("/", httpRequest.getControllerMapping(1).get(),
        "Controller mapping doesn't provided correctly");
  }
}
