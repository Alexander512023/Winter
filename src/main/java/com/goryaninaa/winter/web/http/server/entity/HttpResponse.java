package com.goryaninaa.winter.web.http.server.entity;

import com.goryaninaa.winter.web.http.server.request.handler.HttpResponseCode;
import com.goryaninaa.winter.web.http.server.json.JsonSerializer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * HTTP response implementation. Through different constructors different types
 * of response could be generated.
 *
 * @see HttpResponse#HttpResponse(HttpResponseCode)
 * @see HttpResponse#HttpResponse(HttpResponseCode, String)
 * @see HttpResponse#HttpResponse(HttpResponseCode, Object)
 * @author Alex Goryanin
 */
public class HttpResponse {
  private final HttpResponseCode httpResponseCode;
  private Map<String, String> headers;
  private final String response;

  /**
   * Use this constructor if you send success authentication response.
   *
   * @param cookie - cookie
   */
  public HttpResponse(final Map<String, String> cookie) {
    this.httpResponseCode = HttpResponseCode.OK;
    defineHeaders(cookie);
    response = combine();
  }

  /**
   * Use this constructor if you got nothing to answer to client except HTTP
   * response code.
   *
   * @param httpResponseCode - pass your code
   */
  public HttpResponse(final HttpResponseCode httpResponseCode) {
    this.httpResponseCode = httpResponseCode;
    defineHeaders();
    response = combine();
  }

  /**
   * Use this constructor if you got text that you want to send to your client via
   * HTTP response body.
   *
   * @param httpResponseCode - HTTP response code
   * @param body             - String that will be sent as body of HTTP response
   */
  public HttpResponse(final HttpResponseCode httpResponseCode, final String body) {
    this.httpResponseCode = httpResponseCode;
    defineHeaders("text/html; charset=utf-8", body);
    this.response = combine(httpResponseCode, body);
  }

  /**
   * Use this constructor if you want to send object as answer in JSON format.
   *
   * @param <T>              - type of your response object
   * @param httpResponseCode - HTTP response code
   * @param responseObject   - object that will be converted to JSON and written
   *                         as HTTP response body
   */
  public <T> HttpResponse(final HttpResponseCode httpResponseCode, final T responseObject) {
    this.httpResponseCode = httpResponseCode;
    final JsonSerializer serializer = new JsonSerializer();
    final String body = serializer.serialize(responseObject);
    defineHeaders("application/json", body);
    this.response = combine(httpResponseCode, body);
  }

  public String getResponseString() {
    return response;
  }

  public HttpResponseCode getCode() {
    return httpResponseCode;
  }

  private String combine() {
    final StringBuilder responseString = new StringBuilder(httpResponseCode.getStartLine());
    for (final Entry<String, String> header : headers.entrySet()) {
      responseString.append(header.getKey()).append(": ").append(header.getValue()).append('\n');
    }
    return responseString.toString();
  }

  private String combine(final HttpResponseCode httpResponseCode, final String body) {
    final StringBuilder responseString = new StringBuilder(httpResponseCode.getStartLine());
    for (final Entry<String, String> header : headers.entrySet()) {
      responseString.append(header.getKey()).append(": ").append(header.getValue()).append('\n');
    }
    responseString.append('\n').append(body);
    return responseString.toString();
  }

  private void defineHeaders(Map<String, String> cookie) {
    defineHeaders();
    headers.put("Content-Type", "text/html");
    Entry<String, String> cookiePair = cookie.entrySet().stream().findFirst().orElseThrow();
    headers.put("Set-Cookie", cookiePair.getKey() + "=" + cookiePair.getValue());
  }

  private void defineHeaders(final String value, final String body) {
    defineHeaders();
    headers.put("Content-Type", value);
    headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
  }

  private void defineHeaders() {
    headers = new LinkedHashMap<>(15, 0.75f, false);
    headers.put("Server", "RagingServer");
    headers.put("Connection", "close");
    headers.put("Date", LocalDateTime.now().toString());
  }
}
