package com.goryaninaa.web.http.server;

/**
 * Implementation of this interface should be a class, that represents HTTP
 * response.
 *
 * @author Alex Goryanin
 */
public interface Response {

  String getResponseString();

  HttpResponseCode getCode();

}
