package com.goryaninaa.cache;

import com.goryaninaa.logger.mech.Logger;
import com.goryaninaa.logger.mech.LoggingMech;
import com.goryaninaa.logger.mech.StackTraceString;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import javax.naming.OperationNotSupportedException;

/**
 * Main concurrent cache class. It stands kinda between repository and DAO.
 *
 * @author Alex Goryanin
 * @param <V> - cached entity
 */
public class Storage<V> implements Cache<V> {

  private final Map<CacheKey, Map<CacheKey, Future<Optional<V>>>> cacheStorage
      = new ConcurrentHashMap<>();
  /* package */ final DataAccessObject<V> dao;
  private final int underused;
  private static final Logger LOG = LoggingMech.getLogger(Storage.class.getCanonicalName());

  /**
   * Constructor.
   *
   * @param dao - DAO of cached entity
   * @param properties - properties
   */
  public Storage(final DataAccessObject<V> dao, final Properties properties) {
    this.dao = dao;
    this.underused = Integer.valueOf(properties.getProperty("Cache.underused"));
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
  
  /* package */ int size() {
    return cacheStorage.size();
  }
  
  /* package */ void cleanBelow(final int value) {
    int countBeforeHalf = cacheStorage.size() / 2;
    for (final Entry<CacheKey, Map<CacheKey, Future<Optional<V>>>> cachedElement : cacheStorage
        .entrySet()) {
      if (cachedElement.getKey().getNumberOfUses() < value) {
        cacheStorage.remove(cachedElement.getKey());
        countBeforeHalf--;
      }
      if (countBeforeHalf == 0) {
        break;
      }
    }
  }

  /* package */ int defineMedian() {
    int totalSum = 0;
    for (final Entry<CacheKey, Map<CacheKey, Future<Optional<V>>>> cachedElement : cacheStorage
        .entrySet()) {
      totalSum += cachedElement.getKey().getNumberOfUses();
    }
    return totalSum / cacheStorage.size();
  }

  /* package */ int countUnderused() {
    int underusedNumber = 0;
    for (final Entry<CacheKey, Map<CacheKey, Future<Optional<V>>>> cachedElement : cacheStorage
        .entrySet()) {
      if (cachedElement.getKey().getNumberOfUses() < underused) {
        underusedNumber++;
      }
    }
    return underusedNumber;
  }
  
  private void cleanCacheIfThereIsNoDataInDao(final CacheKey key,
      final Optional<Future<Optional<V>>> data) throws InterruptedException, ExecutionException {
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
    if (cachedData.isPresent()) {
      data = Optional.ofNullable(cachedData.get().get(key));
    } else {
      data = Optional.empty();
    }
    if (data.isEmpty()) {
      data = Optional.ofNullable(getFromDaoTask);
      getFromDaoTask.run();
    }
    return data;
  }

  private Future<Optional<V>> getDataFromCache(final CacheKey key) {
    final Optional<Map<CacheKey, Future<Optional<V>>>> cachedDataWthKey = Optional
        .ofNullable(cacheStorage.get(key));
    return cachedDataWthKey.isPresent() ? cachedDataWthKey.get().get(key) : null;
  }

  private Callable<Optional<V>> getFromDao(final CacheKey key) {
    return new Callable<Optional<V>>() {
      @Override
      public Optional<V> call() throws InterruptedException, OperationNotSupportedException {
        return Storage.this.dao.getData(key.getKey(), key.getOperationType());
      }
    };
  }

  private void useKey(final CacheKey key) {
    final CacheKey cacheKey = cacheStorage.get(key).entrySet().stream().findFirst().get().getKey();
    cacheKey.use();
  }

}
