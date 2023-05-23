package com.goryaninaa.winter.web.http.server.request.handler.manager;

/**
 * Interface that used to inject dependency on JSON deserializer.
 *
 * @author Alex Goryanin
 */
public interface Deserializer {

  <T> T deserialize(Class<T> clazz, String jsonToParse);

}
