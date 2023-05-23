package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.annotation.HttpMethod;
import com.goryaninaa.winter.web.http.server.entity.HttpRequest;
import com.goryaninaa.winter.web.http.server.exception.ServerException;
import java.util.Optional;

/**
 * Stub.
 *
 * @author Alex Goryanin
 */
public class HttpRequestStub extends HttpRequest {
  private final String requestString;

  /**
   * Stub constructor.
   *
   * @param requestString - requestString
   */
  public HttpRequestStub(final String requestString) {
    super("POST /cgi-bin/process.cgi?1+1=2&2+3=5 HTTP/1.1\n"
            + "User-Agent: Mozilla/4.0 (compatible; MSIE5.01; Windows NT)\n"
            + "Host: www.tutorialspoint.com\n" + "Content-Type: application/x-www-form-urlencoded\n"
            + "Content-Length: length\n" + "Accept-Language: en-us\n"
            + "Accept-Encoding: gzip, deflate\n" + "Connection: Keep-Alive\n" + "\n"
            + "licenseID=string&content=string&/paramsXML=string");
    this.requestString = requestString;
    if ("broke".equals(requestString)) {
      throw new ServerException(requestString);
    }
  }

  @Override
  public Optional<String> getControllerMapping(final int length) {
    return Optional.of(requestString.substring(0, length));
  }

  @Override
  public String getMapping() {
    return requestString;
  }

  @Override
  public HttpMethod getMethod() {
    return HttpMethod.GET;
  }

  @Override
  public Optional<String> getBody() {
    return Optional.empty();
  }

}
