package com.goryaninaa.winter.web.http.server.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.goryaninaa.winter.web.http.server.request.handler.HttpResponseCode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

  @Test
  void httpResponseShouldCorrectlyFormWith1Arg() {
    final HttpResponse httpResponse = new HttpResponse(HttpResponseCode.NOTFOUND);
    final String factWithoutDate = getFactWithoutDate(httpResponse);
    final String expected = "HTTP/1.1 404 Not Found\n" +
            "Server: RagingServer\n" +
            "Connection: close\n";
    assertEquals(expected, factWithoutDate);
  }

  @Test
  void httpResponseShouldCorrectlyFormWith2Arg() {
    final String body = "<p>Hello!</p>";
    final HttpResponse httpResponse = new HttpResponse(HttpResponseCode.NOTFOUND, body);
    final String factWithoutDate = getFactWithoutDate(httpResponse);
    final String expected = "HTTP/1.1 404 Not Found\n" + "Server: RagingServer\n"
              + "Connection: close\n" + "Content-Type: text/html; charset=utf-8\n"
              + "Content-Length: 13\n" + "\n" + "<p>Hello!</p>";
    assertEquals(expected, factWithoutDate);

  }

  @Test
  void httpResponseShouldCorrectlyFormWithJson() {
    final PersonStub person = new PersonStub("Alex");
    final HttpResponse httpResponse = new HttpResponse(HttpResponseCode.OK, person);
    final String factWithoutDate = getFactWithoutDate(httpResponse);
    final String expected = "HTTP/1.1 200 OK\n" + "Server: RagingServer\n" + "Connection: close\n"
              + "Content-Type: application/json\n" + "Content-Length: 16\n" + "\n"
              + "{\"name\": \"Alex\"}";
    assertEquals(expected, factWithoutDate);
  }

  private String getFactWithoutDate(HttpResponse httpResponse) {
    final Pattern pattern = Pattern.compile("Date:.*\\n");
    final String factWithDate = httpResponse.getResponseString();
    final Matcher matcher = pattern.matcher(factWithDate);
    String factWithoutDate = "";
    if (matcher.find()) {
      factWithoutDate = factWithDate.substring(0, matcher.start())
              + factWithDate.substring(matcher.end());
    }
    return factWithoutDate;
  }
}
