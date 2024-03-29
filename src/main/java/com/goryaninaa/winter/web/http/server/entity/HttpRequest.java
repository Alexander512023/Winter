package com.goryaninaa.winter.web.http.server.entity;

import com.goryaninaa.winter.web.http.server.annotation.HttpMethod;
import com.goryaninaa.winter.web.http.server.json.JsonDeserializer;
import com.goryaninaa.winter.web.http.server.request.handler.manager.Deserializer;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTTP request implementation. All parts of request transfers to object fields.
 * Immutable.
 *
 * @author Alex Goryanin
 */
public class HttpRequest {
  private final String request;
  private final Deserializer deserializer = new JsonDeserializer();
  private HttpMethod method;
  private String mapping;
  private final Map<String, String> parameters = new ConcurrentHashMap<>();
  private final Map<String, String> headers = new ConcurrentHashMap<>();
  private final Map<String, String> cookies = new ConcurrentHashMap<>();
  private String body;

  /**
   * Construct object from JSON string.
   *
   * @param request - JSON string
   */
  public HttpRequest(final String request) {
    this.request = request;
    defineMethod();
    defineMapping();
    defineParameters();
    defineHeaders();
    defineBody();
    if (headers.containsKey("Cookie")) {
      defineCookies();
    }
  }

  public String getMapping() {
    return mapping;
  }

  public HttpMethod getMethod() {
    return method;
  }

  /**
   * Returns Optional of String representing mapping which would be compared with
   * controller mapping. If controller mapping length is more than request mapping
   * length - return empty Optional.
   */
  public Optional<String> getControllerMapping(final int length) {
    Optional<String> controllerMapping = Optional.empty();
    if (mapping.length() >= length) {
      controllerMapping = Optional.of(mapping.substring(0, length));
    }
    return controllerMapping;
  }

  public Optional<String> getParameterByName(final String name) {
    return Optional.ofNullable(parameters.get(name));
  }

  public Optional<String> getHeaderByName(final String name) {
    return Optional.ofNullable(headers.get(name));
  }

  public Optional<String> getBody() {
    return Optional.ofNullable(body);
  }

  /**
   * Using {@link Deserializer} returns Optional of Object of type T, that was
   * represented by HTTP request body. If body is empty - empty Optional returned.
   *
   * @param <T>    - Type of returned class
   * @param object - name of parameter
   * @return - Optional of Object of type T or empty Optional
   */
  @SuppressWarnings({"unchecked", "unused"})
  public <T> Optional<T> getJsonObject(final T object) {
    Optional<T> jsonObject = Optional.empty();
    if (headers.containsKey("Content-Type")
        && "application/json".equals(headers.get("Content-Type"))
        || !method.equals(HttpMethod.GET)) {
      jsonObject = (Optional<T>) Optional
          .ofNullable(deserializer.deserialize(object.getClass(), body));
    }
    return jsonObject;
  }

  public Optional<String> getCookieValue(final String key) {
    return Optional.ofNullable(cookies.get(key));
  }

  private void defineCookies() {
    final String[] cookiesPairs = headers.get("Cookie").split(";");
    for (final String cookiesPair : cookiesPairs) {
      final String[] cookiesPairArr = cookiesPair.trim().split("=");
      final String key = cookiesPairArr[0];
      final String value = cookiesPairArr[1];
      cookies.put(key, value);
    }
  }

  private void defineMethod() {
    final Pattern pattern = Pattern.compile("(GET|POST|PUT|PATCH|DELETE)\\s");
    final Matcher matcher = pattern.matcher(request);
    if (matcher.find()) {
      final String methodString = request.substring(0, matcher.end()).trim();
      switch (methodString) {
        case "GET":
          this.method = HttpMethod.GET;
          break;
        case "POST":
          this.method = HttpMethod.POST;
          break;
        case "PUT":
          this.method = HttpMethod.PUT;
          break;
        case "PATCH":
          this.method = HttpMethod.PATCH;
          break;
        case "DELETE":
          this.method = HttpMethod.DELETE;
          break;
        default:
          throw new IllegalStateException("Unexpected value: " + methodString);
      }
    } else {
      throw new IllegalArgumentException("Unsupported http request method");
    }
  }

  private void defineMapping() {
    final Pattern pttrnWithParams = Pattern.compile("/[-a-zA-Z0-9@:%._+~#=/]+?\\?");
    final Matcher mtchrWithParams = pttrnWithParams.matcher(request);
    final Pattern pttrnWithNoParams = Pattern.compile("/[-a-zA-Z0-9@:%._+~#=/]*?\\s");
    final Matcher mtchrWithNoParams = pttrnWithNoParams.matcher(request);
    if (mtchrWithParams.find()) {
      this.mapping = request.substring(mtchrWithParams.start(), mtchrWithParams.end() - 1).trim();
    } else if (mtchrWithNoParams.find()) {
      this.mapping = request.substring(mtchrWithNoParams.start(), mtchrWithNoParams.end()).trim();
    } else {
      throw new IllegalArgumentException("Missing mapping in http request");
    }
  }

  private void defineParameters() {
    final Optional<String> parametersString = cutParametersString();
    if (parametersString.isPresent()) {
      final String[] lines = parametersString.get().split("&");
      for (final String line : lines) {
        parameters.put(line.split("=")[0], line.split("=")[1]);
      }
    }
  }

  private Optional<String> cutParametersString() {
    final Pattern pattern = Pattern.compile("\\?\\S+?\\s");
    final Matcher matcher = pattern.matcher(request);
    Optional<String> cutParamString = Optional.empty();
    if (matcher.find()) {
      cutParamString = Optional.of(request.substring(matcher.start() + 1, matcher.end()).trim());
    }
    return cutParamString;
  }

  private void defineHeaders() {
    final Optional<String> headersString = Optional.ofNullable(cutHeadersString());
    if (headersString.isPresent()) {
      final String[] lines = headersString.get().split("\\n");

      for (final String line : lines) {
        headers.put(line.split(": ")[0].trim(), line.split(": ")[1].trim());
      }
    }
  }

  private String cutHeadersString() {
    final Pattern pattern = Pattern.compile("(?s)\\n.*\\n\\n");
    final Matcher matcher = pattern.matcher(request);
    String cuttedHdrsString = null;
    if (matcher.find()) {
      cuttedHdrsString = request.substring(matcher.start(), matcher.end()).trim();
    }
    return cuttedHdrsString;
  }

  private void defineBody() {
    final Pattern pattern = Pattern.compile("\\n\\n");
    final Matcher matcher = pattern.matcher(request);
    if (matcher.find()) {
      this.body = request.substring(matcher.end()).trim();
    }
  }
}
