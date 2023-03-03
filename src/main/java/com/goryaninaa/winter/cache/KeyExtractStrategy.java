package com.goryaninaa.winter.cache;

/**
 * Implement this interface to define key extract strategy.
 *
 * @author Alex Goryanin
 */
public interface KeyExtractStrategy {

  Object extractKey(Object entity);

  @SuppressWarnings("unused")
  String getStrategy();

}
