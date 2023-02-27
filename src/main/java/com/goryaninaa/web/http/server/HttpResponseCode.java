package com.goryaninaa.web.http.server;

/**
 * Enumeration of supported HTTP response codes.
 *
 * @author Alex Goryanin
 */
public enum HttpResponseCode {

  OK(200, "HTTP/1.1 200 OK\n"), NOTFOUND(404, "HTTP/1.1 404 Not Found\n"),
  INTERNALSERVERERROR(500, "HTTP/1.1 500 Internal Server Error");

  private final int code;
  private final String startLine;

  HttpResponseCode(final int code, final String startLine) {
    this.code = code;
    this.startLine = startLine;
  }

  public int getCode() {
    return code;
  }

  public String getStartLine() {
    return startLine;
  }
}
