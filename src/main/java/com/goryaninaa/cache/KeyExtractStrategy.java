package com.goryaninaa.cache;

/**
 * Implement this interface to define key extract strategy.
 *
 * @author Alex Goryanin
 */
public interface KeyExtractStrategy {

  Object extractKey(Object entity);

  String getStrategy();

}
