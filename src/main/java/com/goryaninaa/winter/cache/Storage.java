package com.goryaninaa.winter.cache;

import com.goryaninaa.winter.logger.mech.Logger;
import com.goryaninaa.winter.logger.mech.LoggingMech;
import com.goryaninaa.winter.logger.mech.StackTraceString;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * Main concurrent cache class. It stands kinda between repository and DAO.
 *
 * @author Alex Goryanin
 * @param <V> - cached entity
 */
public class Storage<V> implements Cache<V> {

  /* package */ final Map<CacheKey, Map<CacheKey, Future<Optional<V>>>> cacheStorage
      = new ConcurrentHashMap<>();
  /* package */ final DataAccessObject<V> dao;
  private static final Logger LOG = LoggingMech.getLogger(Storage.class.getCanonicalName());

  /**
   * Constructor.
   *
   * @param dao - DAO of cached entity
   */
  public Storage(final DataAccessObject<V> dao) {
    this.dao = dao;
  }

  /**
   * Extract data from cache.
   *
   * @param key - key corresponding to desired value
   * @return - desired value
   */
  @Override
  public Optional<V> getData(final CacheKey key) {
    try {
      Optional<Future<Optional<V>>> data = Optional.ofNullable(getDataFromCache(key));
      if (data.isEmpty()) {
        data = getDataFromDaoThroughCache(key);
      } else {
        useKey(key);
      }
      cleanCacheIfThereIsNoDataInDao(key, data);
      //noinspection OptionalGetWithoutIsPresent
      return data.get().get();
    } catch (InterruptedException | ExecutionException e) {
      if (LOG.isErrorEnabled()) {
        LOG.error(StackTraceString.get(e));
      }
      throw new CacheException(e.getMessage(), e);
    }
  }

  /**
   * Remove value specified by list of cache keys from cache.
   *
   * @param cacheKeys - cache keys
   */
  @Override
  public void remove(final List<CacheKey> cacheKeys) {
    for (final CacheKey cacheKey : cacheKeys) {
      cacheStorage.remove(cacheKey);
    }
  }

  private void cleanCacheIfThereIsNoDataInDao(final CacheKey key,
      final Optional<Future<Optional<V>>> data) throws InterruptedException, ExecutionException {
    //noinspection OptionalGetWithoutIsPresent
    if (data.get().get().isEmpty()) {
      cacheStorage.remove(key);
    }
  }

  private Optional<Future<Optional<V>>> getDataFromDaoThroughCache(final CacheKey key) {
    Optional<Future<Optional<V>>> data;
    final Callable<Optional<V>> getFromDao = getFromDao(key);
    final FutureTask<Optional<V>> getFromDaoTask = new FutureTask<>(getFromDao);
    final Optional<Map<CacheKey, Future<Optional<V>>>> cachedData = Optional.ofNullable(
        cacheStorage.putIfAbsent(key, new ConcurrentHashMap<>(Map.of(key, getFromDaoTask))));
    data = cachedData.map(cacheKeyFutureMap -> cacheKeyFutureMap.get(key));
    if (data.isEmpty()) {
      data = Optional.of(getFromDaoTask);
      getFromDaoTask.run();
    }
    return data;
  }

  private Future<Optional<V>> getDataFromCache(final CacheKey key) {
    final Optional<Map<CacheKey, Future<Optional<V>>>> cachedDataWthKey = Optional
        .ofNullable(cacheStorage.get(key));
    return cachedDataWthKey.map(cacheKeyFutureMap -> cacheKeyFutureMap.get(key)).orElse(null);
  }

  private Callable<Optional<V>> getFromDao(final CacheKey key) {
    return () -> Storage.this.dao.getData(key.getKey(), key.getOperationType());
  }

  private void useKey(final CacheKey key) {
    //noinspection OptionalGetWithoutIsPresent
    final CacheKey cacheKey = cacheStorage.get(key).entrySet().stream().findFirst().get().getKey();
    cacheKey.use();
  }

}
