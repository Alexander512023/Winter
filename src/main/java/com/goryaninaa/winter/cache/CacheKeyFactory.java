package com.goryaninaa.winter.cache;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Factory class that creates cache keys.
 *
 * @author Alex Goryanin
 */
public class CacheKeyFactory {

  private final Map<String, KeyExtractStrategy> cacheKeyCatalog;

  public CacheKeyFactory(final Map<String, KeyExtractStrategy> cacheKeyCatalog) {
    this.cacheKeyCatalog = cacheKeyCatalog;
  }

  public CacheKey generateCacheKey(final Object key, final String accessStrategy) {
    return new CacheKey(key, accessStrategy);
  }

  /**
   * Use this method in case you should delete value from cache from all access strategies.
   *
   * @param entity - value-object, that you want to delete from cache
   * @return - list of all possible cache keys for passed object
   */
  @SuppressWarnings("unused")
  public List<CacheKey> generateAllCacheKeys(final Object entity) {
    final List<CacheKey> allKeysForEntity = new CopyOnWriteArrayList<>();
    for (final Entry<String, KeyExtractStrategy> specification : cacheKeyCatalog.entrySet()) {
      final KeyExtractStrategy extractStrategy = specification.getValue();
      final Object key = extractStrategy.extractKey(entity);
      final String accessStrategy = specification.getKey();
      final CacheKey cacheKey = generateCacheKey(key, accessStrategy);
      allKeysForEntity.add(cacheKey);
    }
    return allKeysForEntity;
  }
}
