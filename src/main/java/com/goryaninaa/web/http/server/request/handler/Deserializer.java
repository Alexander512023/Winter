package com.goryaninaa.web.http.server.request.handler;

/**
 * Interface that used to inject dependency on JSON deserializer.
 *
 * @author Alex Goryanin
 */
public interface Deserializer {

  <T> T deserialize(Class<T> clazz, String jsonToParse);

}
