package com.goryaninaa.cache;

import java.util.List;
import java.util.Optional;

/**
 * Implement this interface in order to match strategy architecture pattern. It
 * will allow you to extract data from cache.
 *
 * @author Alex Goryanin
 */
public interface DataAccessStrategy {

  <V> Optional<V> getData(Object key, List<V> data);

  String getStrategy();

}
