package com.goryaninaa.web.http.server;

import com.goryaninaa.web.http.server.annotation.HttpMethod;
import java.util.Optional;

/**
 * Implementation of this interface should be a class, that represents HTTP
 * request.
 *
 * @author Alex Goryanin
 */
public interface Request {

  Optional<String> getControllerMapping(int length);

  String getMapping();

  HttpMethod getMethod();

  Optional<String> getBody();

}
