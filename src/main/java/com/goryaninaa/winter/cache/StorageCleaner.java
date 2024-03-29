package com.goryaninaa.winter.cache;

import com.goryaninaa.winter.logger.mech.Logger;
import com.goryaninaa.winter.logger.mech.LoggingMech;
import com.goryaninaa.winter.logger.mech.StackTraceString;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class is responsible for cache clean up logic.
 *
 * @author Alex Goryanin
 * @param <V> - entity that will be cached
 */
public class StorageCleaner<V> {

  private final Map<CacheKey, Map<CacheKey, Future<Optional<V>>>> cacheStorage;
  private final int sizeParam;
  private final ExecutorService executorService;
  private final AtomicBoolean running;
  private static final Logger LOG = LoggingMech.getLogger(StorageCleaner.class.getCanonicalName());

  /**
   * Constructor.
   *
   * @param cacheStorage - cacheStorage
   * @param properties - properties
   */
  public StorageCleaner(final Map<CacheKey, Map<CacheKey, Future<Optional<V>>>> cacheStorage,
          final Properties properties) {
    this.cacheStorage = cacheStorage;
    this.sizeParam = Integer.parseInt(properties.getProperty("Winter.Cache.size"));
    this.executorService = Executors.newSingleThreadExecutor();
    this.running = new AtomicBoolean(false);
  }

  /**
   * Use to run cache storage cleaning.
   */
  public void run() {
    executorService.submit(this::cleanUp);
    running.set(true);
    if (LOG.isInfoEnabled()) {
      LOG.info("Cache cleaning is running.");
    }
  }

  /**
   * Use to shut down cache storage cleaning.
   */
  public void shutdown() {
    running.set(false);
    executorService.shutdownNow();
    if (LOG.isInfoEnabled()) {
      LOG.info("Cache cleaning is shutdown.");
    }
  }

  private void cleanUp() {
    while (running.get()) {
      cleanCache();
    }
  }

  private void cleanCache() {
    if (size() > sizeParam) {
      cleanBelowUses(defineMedianUsage());
    }
    sleep();
  }

  private int size() {
    return cacheStorage.size();
  }

  private void cleanBelowUses(final int value) {
    int countBeforeLimit = cacheStorage.size() - sizeParam;
    for (final Map.Entry<CacheKey, Map<CacheKey, Future<Optional<V>>>> cachedElement : cacheStorage
            .entrySet()) {
      if (cachedElement.getKey().getNumberOfUses() <= value) {
        cacheStorage.remove(cachedElement.getKey());
        countBeforeLimit--;
      }
      if (countBeforeLimit == 0) {
        break;
      }
    }
  }

  private int defineMedianUsage() {
    int totalSum = 0;
    for (final Map.Entry<CacheKey, Map<CacheKey, Future<Optional<V>>>> cachedElement : cacheStorage
            .entrySet()) {
      totalSum += cachedElement.getKey().getNumberOfUses();
    }
    return totalSum / cacheStorage.size();
  }

  private void sleep() {
    try {
      Thread.sleep(5);
    } catch (InterruptedException e) {
      if (LOG.isErrorEnabled()) {
        LOG.error(StackTraceString.get(e));
      }
      Thread.currentThread().interrupt();
      throw new CacheException("Cache clean up failed due to thread interruption", e);
    }
  }
}
