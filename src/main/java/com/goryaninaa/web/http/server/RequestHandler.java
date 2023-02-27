package com.goryaninaa.web.http.server;

/**
 * Implementation of this interface should provide strategy of handling client's
 * request by preparing {@link Response} interacting with client code via
 * implementation of {@link Controller}.
 *
 * @author Alex Goryanin
 */
public interface RequestHandler {

  Response handle(String request);

  void addController(Controller controller);

}