package com.goryaninaa.winter.web.http.server.request.handler;

import com.goryaninaa.winter.web.http.server.entity.Request;

/**
 * This interface is responsible for converting incoming String request to
 * corresponding entities.
 *
 * @author Alex Goryanin
 */
public interface RequestPreparator {

  Request from(String requestString);

}
