package com.goryaninaa.winter.web.http.server.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Controller annotation for mapping definement.
 *
 * @author Alex Goryanin
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

  /**
   * This method serves to designate mapping.
   *
   * @return supported mapping
   */
  String value();

}
