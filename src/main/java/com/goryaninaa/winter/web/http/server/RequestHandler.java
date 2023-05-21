package com.goryaninaa.winter.web.http.server;

import com.goryaninaa.winter.web.http.server.request.handler.Controller;

/**
 * Implementation of this interface should provide strategy of handling client's
 * request by preparing {@link Response} interacting with client code via
 * implementation of {@link Controller}.
 *
 * @author Alex Goryanin
 */
public interface RequestHandler {

  Response handle(String request);

}