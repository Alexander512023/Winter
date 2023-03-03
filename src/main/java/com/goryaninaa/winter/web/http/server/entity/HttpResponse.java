package com.goryaninaa.winter.web.http.server.entity;

import com.goryaninaa.winter.web.http.server.HttpResponseCode;
import com.goryaninaa.winter.web.http.server.Response;
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
public class HttpResponse implements Response {
  private final HttpResponseCode httpResponseCode;
  private Map<String, String> headers;
  private final String response;

  /**
   * Use this constructor if you got nothing to answer to client except HTTP
   * response code.
   *
   * @param httpResponseCode - pass your code
   */
  public HttpResponse(final HttpResponseCode httpResponseCode) {
    this.httpResponseCode = httpResponseCode;
    defineHeaders();
    response = this.httpResponseCode.getStartLine();
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
    defineHeaders(body);
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
    JsonSerializer serializer = new JsonSerializer();
    final String body = serializer.serialize(responseObject);
    defineHeaders(responseObject, body);
    this.response = combine(httpResponseCode, body);
  }

  @Override
  public String getResponseString() {
    return response;
  }

  @Override
  public HttpResponseCode getCode() {
    return httpResponseCode;
  }

  private String combine(final HttpResponseCode httpResponseCode, final String body) {
    //noinspection StringBufferMayBeStringBuilder
    final StringBuffer response = new StringBuffer(httpResponseCode.getStartLine());
    for (final Entry<String, String> header : headers.entrySet()) {
      response.append(header.getKey()).append(": ").append(header.getValue()).append('\n');
    }
    response.append('\n').append(body);
    return response.toString();
  }

  private void defineHeaders(final String body) {
    defineHeaders();
    headers.put("Content-Type", "text/html; charset=utf-8");
    headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
  }

  private <T> void defineHeaders(@SuppressWarnings("unused") final T responseObject, final String body) { // NOPMD
    defineHeaders();
    headers.put("Content-Type", "application/json");
    headers.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
  }

  private void defineHeaders() {
    headers = new LinkedHashMap<>(15, 0.75f, false);
    headers.put("Server", "RagingServer");
    headers.put("Connection", "close");
    headers.put("Date", LocalDateTime.now().toString());
  }
}
