package com.goryaninaa.winter.web.http.server.request.handler;

/**
 * Enumeration of supported HTTP response codes.
 *
 * @author Alex Goryanin
 */
public enum HttpResponseCode {

  OK(200, "HTTP/1.1 200 OK\n"),
  NOTFOUND(404, "HTTP/1.1 404 Not Found\n"),
  UNAUTHORIZED(401, "HTTP/1.1 401 Unauthorized\n"),
  INTERNALSERVERERROR(500, "HTTP/1.1 500 Internal Server Error"),
  BADREQUEST(400, "HTTP/1.1 400 Bad Request\n" );

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
