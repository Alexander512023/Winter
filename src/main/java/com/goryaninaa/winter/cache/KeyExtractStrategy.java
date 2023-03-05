package com.goryaninaa.winter.cache;

/**
 * Implement this interface to define key extract strategy.
 *
 * @author Alex Goryanin
 */
@SuppressWarnings("SameReturnValue")
public interface KeyExtractStrategy {

  Object extractKey(Object entity);

  String getStrategyType();

}
