package com.goryaninaa.winter.cache;

import java.util.List;
import java.util.Optional;

/**
 * Implement this interface to match cache contract.
 *
 * @author Alex Goryanin
 * @param <V> - entity object that will be cached
 */
public interface Cache<V> {
  
  Optional<V> getData(CacheKey key);

  void remove(List<CacheKey> cacheKeys);
  
}
