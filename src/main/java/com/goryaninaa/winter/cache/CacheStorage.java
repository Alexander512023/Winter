package com.goryaninaa.winter.cache;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * This class is responsible for wrapping {@link Storage}, and taking on itself
 * cache cleaning duties.
 *
 * @author Alex Goryanin
 * @param <V> - entity object, which will be cached
 */
@SuppressWarnings("unused")
public class CacheStorage<V> implements Cache<V> {

  private final Storage<V> storage;

  /**
   * CacheStorage constructor.
   *
   * @param dao - DAO with witch this cache should interact
   * @param properties - app properties
   */
  public CacheStorage(final DataAccessObject<V> dao, final Properties properties) {
    Map<CacheKey, Map<CacheKey, Future<Optional<V>>>> cacheStorageMap =
            new ConcurrentHashMap<>();
    this.storage = new Storage<>(dao, cacheStorageMap);
    final StorageCleaner<V> storageCleaner = new StorageCleaner<>(cacheStorageMap, properties);
    storageCleaner.run();
  }

  @Override
  public Optional<V> getData(final CacheKey key) {
    return storage.getData(key);
  }

  @Override
  public void remove(final List<CacheKey> cacheKeys) {
    storage.remove(cacheKeys);
  }

}
