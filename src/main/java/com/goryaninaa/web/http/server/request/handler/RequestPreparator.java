package com.goryaninaa.web.http.server.request.handler;

import com.goryaninaa.web.http.server.Request;

/**
 * This interface is responsible for converting incoming String request to
 * corresponding entities.
 *
 * @author Alex Goryanin
 */
public interface RequestPreparator {

  Request httpRequestFrom(String requestString);

}
