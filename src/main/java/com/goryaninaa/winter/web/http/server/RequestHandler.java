package com.goryaninaa.winter.web.http.server;

import com.goryaninaa.winter.web.http.server.entity.HttpResponse;
import com.goryaninaa.winter.web.http.server.request.handler.manager.Controller;

/**
 * Implementation of this interface should provide strategy of handling client's
 * request by preparing {@link HttpResponse} interacting with client code via
 * implementation of {@link Controller}.
 *
 * @author Alex Goryanin
 */
public interface RequestHandler {

  HttpResponse handle(String request);

}